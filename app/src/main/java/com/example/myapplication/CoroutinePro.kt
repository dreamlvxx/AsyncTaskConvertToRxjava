package com.example.myapplication

import android.os.Handler
import android.os.Looper
import android.os.Process
import kotlinx.coroutines.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于替换asynctask
 * 可publish progress
 */

val TAG = "CoroutinePro"

@InternalCoroutinesApi
fun CoroutineScope.launchX(
        func: () -> Unit,
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = StandaloneCoroutineX(onCancelFunc = func, parentContext = newContext, active = true)
    coroutine.start(CoroutineStart.DEFAULT, coroutine, block)
    return coroutine
}

@InternalCoroutinesApi
private open class StandaloneCoroutineX(
        val onCancelFunc: () -> Unit,
        parentContext: CoroutineContext,
        active: Boolean,
) : AbstractCoroutine<Unit>(parentContext, active) {
    override fun handleJobException(exception: Throwable): Boolean {
        handleCoroutineException(context, exception)
        return true
    }

    override fun onCancelled(cause: Throwable, handled: Boolean) {
        super.onCancelled(cause, handled)
        onCancelFunc.invoke()
    }

}

abstract class CoroutinePro<Params, Progress, Result> {

    private var job: Job? = null
    private var coroutineScope: CoroutineScope? = null

    class CustomThreadPoolFactory(private val namePrefix: String) : ThreadFactory {
        private val threadNumber = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            return BackgroundThread(r, namePrefix + " #" + threadNumber.getAndIncrement())
        }

        private class BackgroundThread(target: Runnable?, name: String) : Thread(target, name) {
            override fun run() {
                // By default, the system sets a thread’s priority to the same priority and group memberships as the spawning thread
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
                super.run()
            }
        }
    }

    companion object {
        private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
        private val POOL_SIZE = CPU_COUNT + 1
        private val MAX_POOL_SIZE = CPU_COUNT * 2 + 1
        const val KEEP_ALIVE_TIME = 5L
        private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS

        val DEFAULT_THREAD_POOL_SIZE = POOL_SIZE
        val PUBLISH_PROGRESS: Int = 999

        //单线程执行
        @JvmField
        val SERIAL_EXECUTOR: ExecutorService = Executors.newSingleThreadExecutor()

        //多任务执行
        @JvmField
        val THREAD_POOL_EXECUTOR: ExecutorService = ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT, LinkedBlockingQueue(128), CustomThreadPoolFactory("SyncThreadPool"), ThreadPoolExecutor.DiscardPolicy())

        //默认单线程执行
        private var sDefaultExecutor = THREAD_POOL_EXECUTOR

        @JvmField
        var defaultIO = Dispatchers.IO

        @JvmStatic
        fun setDefaultExecutor(exec: ExecutorService) {
            sDefaultExecutor = exec
        }

        private val handler: Handler by lazy {
            Handler(Looper.getMainLooper()) {
                when (it.what) {
                    PUBLISH_PROGRESS -> {
                        val res = it.obj as CoroutinePro<Any, Any, Any>.ProgressResult<Any>
                        res.coroutinePro.onProgressUpdate(*res.data)
                    }
                    else -> {

                    }
                }
                false
            }
        }
    }

    @InternalCoroutinesApi
    fun execute(vararg args: Params): CoroutinePro<Params, Progress, Result> {
        this.executeOnExecutor(sDefaultExecutor, *args)
        return this
    }

    @InternalCoroutinesApi
    fun executeOnExecutor(executors: ExecutorService, vararg args: Params): CoroutinePro<Params, Progress, Result> {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        this.job = coroutineScope?.launchX(func = ::onCancelled) {
            onPreExecute()
            withContext(executors.asCoroutineDispatcher()) {
                var res: Result? = null
                try {
                    ensureActive()
                    res = doInBackground(*args)
                } catch (e: Exception) {
                    onError(e)
                }
                res
            }.let {
                onPostExecute(it)
            }
        }
        return this
    }

    @InternalCoroutinesApi
    fun executeOnIO(vararg args: Params): CoroutinePro<Params, Progress, Result> {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        this.job = coroutineScope?.launchX(func = ::onCancelled) {
            onPreExecute()
            withContext(Dispatchers.IO) {
                var res: Result? = null
                try {
                    ensureActive()
                    res = doInBackground(*args)
                } catch (e: Exception) {
                    onError(e)
                }
                res
            }.let {
                onPostExecute(it)
            }
        }
        return this
    }


    protected abstract fun doInBackground(vararg args: Params): Result?

    protected open fun onPostExecute(res: Result?) {

    }

    protected open fun onPreExecute() {

    }

    protected open fun onCancelled(res: Result) {
        onCancelled()
    }

    protected open fun onCancelled() {

    }

    protected open fun onError(e: Exception) {

    }

    fun cancel() {
        job?.cancel()
    }

    fun isCancelled(): Boolean {
        if (job == null) {
            return true
        } else {
            return job!!.isCancelled
        }
    }

    private inner class ProgressResult<Data>(
            val coroutinePro: CoroutinePro<Params, Progress, Result>,
            val data: Array<Data>)

    protected open fun publishProgress(vararg values: Progress) {
        if (!isCancelled()) {
            handler
                    .obtainMessage(PUBLISH_PROGRESS, ProgressResult(this, values))
                    .sendToTarget()
        }
    }

    protected open fun onProgressUpdate(vararg values: Progress) {

    }

}