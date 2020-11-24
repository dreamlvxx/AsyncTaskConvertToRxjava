package com.example.myapplication

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


val TAG = "CoroutinePro"
@InternalCoroutinesApi
fun CoroutineScope.launchX(
        func : ()->Unit,
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit,
): Job {
    val newContext = newCoroutineContext(context)
    val coroutine = StandaloneCoroutineX(onCancelFunc = func,parentContext = newContext, active = true)
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

    private val myDispatcher = Executors.newFixedThreadPool(8).asCoroutineDispatcher()

    val fixed = newFixedThreadPoolContext(10, "CoroutinePro")
    val serial = newSingleThreadContext("single")
    var job: Job? = null
    var coroutineScope: CoroutineScope? = null

    @InternalCoroutinesApi
    fun execute(vararg args: Params): CoroutinePro<Params, Progress, Result> {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        job = coroutineScope?.launchX(func = ::onCancelled) {
            onPreExecute()
            withContext(myDispatcher) {
                doInBackground(*args)
            }.let {
                onPostExecute(it)
            }
        }
        return this
    }

    fun executeOnExecutor(executors: ExecutorService, vararg args: Params): CoroutinePro<Params, Progress, Result> {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.job = coroutineScope.launch {
            onPreExecute()
            withContext(executors.asCoroutineDispatcher()) {
                doInBackground(*args)
            }.let {
                onPostExecute(it)
            }
        }
        return this
    }

    protected abstract fun doInBackground(vararg args: Params): Result

    protected open fun onPostExecute(res: Result) {

    }

    protected open fun onPreExecute() {

    }

    protected open fun onCancelled(res: Result?) {
        onCancelled()
    }

    protected open fun onCancelled() {

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

    companion object{
        val PUBLISH_PROGRESS : Int = 999
    }


    val handler : Handler by lazy {
        Handler(Looper.getMainLooper()){
            when(it.what){
                PUBLISH_PROGRESS ->{
                    val res = it.obj as CoroutinePro<Params, Progress, Result>.ProgressResult<Progress>
                    res.croinstance.onProgressUpdate(*res.data)
                }
                else -> {

                }
            }
            false
        }
    }

    private inner class ProgressResult<Data>(
            var croinstance: CoroutinePro<Params,Progress,Result>,
            val data :Array<Data>)

    protected open fun publishProgress(vararg values : Progress){
        if(!isCancelled()){
            handler
                    .obtainMessage(PUBLISH_PROGRESS,ProgressResult(this, values))
                    .sendToTarget()
        }
    }

    protected open fun onProgressUpdate(vararg values : Progress){

    }

}