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

    private var job: Job? = null
    private var coroutineScope: CoroutineScope? = null

    companion object{
        val PUBLISH_PROGRESS : Int = 999
        private val serial = Executors.newSingleThreadExecutor()
    }

    @InternalCoroutinesApi
    fun execute(vararg args: Params) : CoroutinePro<Params, Progress, Result>{
        this.executeOnExecutor(serial,*args)
        return this
    }

    @InternalCoroutinesApi
    fun executeOnExecutor(executors: ExecutorService, vararg args: Params): CoroutinePro<Params, Progress, Result> {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        this.job = coroutineScope?.launchX(func = ::onCancelled) {
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

    private fun isCancelled(): Boolean {
        if (job == null) {
            return true
        } else {
            return job!!.isCancelled
        }
    }



    private val handler : Handler by lazy {
        Handler(Looper.getMainLooper()){
            when(it.what){
                PUBLISH_PROGRESS ->{
                    val res = it.obj as CoroutinePro<Params, Progress, Result>.ProgressResult<Progress>
                    res.coroutinePro.onProgressUpdate(*res.data)
                }
                else -> {

                }
            }
            false
        }
    }

    private inner class ProgressResult<Data>(
            val coroutinePro: CoroutinePro<Params,Progress,Result>,
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