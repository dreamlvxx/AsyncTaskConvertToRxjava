package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

object CoroutineUtils {

    val tag = "CoroutineUtils"
    /**
     *  替换回调的写法,异步执行，回调处理
     */
    @JvmStatic
    fun <R> waitAndExcuAsyncDefault(backgroundFunc: () -> R, mainFunc: (R?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            var result: R? = null
            withContext(Dispatchers.IO) {
                try {
                    log(backgroundFunc.toString())
                    result = backgroundFunc()
                } catch (e: Exception) {

                }
                result
            }
            log(mainFunc.toString())
            mainFunc(result)
        }
    }

    @JvmStatic
    fun <T> excuOnIO(callable: CorouRunnable<T>) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            log("excuOnIO: task = " + callable.mTag)
            callable.call()
        }
    }

    @JvmStatic
    fun <T> excuOnIOWithCallback(callable: CorouRunnable<T>,callback : Callback<T>) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            var result : T? = null
            withContext(Dispatchers.IO){
                try {
                    log("excuOnIOWithCallback: task = " + callable.mTag)
                    result = callable.call()
                }catch (e : Exception){
                    callback.onError(e.message)
                }
                result
            }
            callback.onFinish(result)
        }
    }

    @InternalCoroutinesApi
    @JvmStatic
    fun <T> submitWithCallback(runnable: CorouRunnable<T>, callback: Callback<T?>): CoroutinePro<*, *, *> {
        return object : CoroutinePro<String?, String?, T>() {
            override fun doInBackground(vararg args: String?): T? {
                val futureTask = FutureTask(runnable)
                futureTask.run()
                try {
                    return futureTask.get()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(res: T?) {
                super.onPostExecute(res)
                callback.onFinish(res)
            }

            override fun onCancelled() {
                super.onCancelled()
                callback.onCancel()
            }

            override fun onError(e: Exception) {
                super.onError(e)
                callback.onError(e.message)
            }
        }.executeOnIO(runnable.tag)
    }

    @InternalCoroutinesApi
    @JvmStatic
    fun <T> submit(runnable: CorouRunnable<T>): CoroutinePro<*, *, *> {
        return object : CoroutinePro<String?, String?, T>() {
            override fun doInBackground(vararg args: String?): T? {
                val futureTask = FutureTask(runnable)
                futureTask.run()
                try {
                    return futureTask.get()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return null
            }

            override fun onPostExecute(res: T?) {
                super.onPostExecute(res)
            }

        }.executeOnIO(runnable.tag)
    }

    interface Callback<T> {
        fun onFinish(res: T?)
        fun onError(mes : String?)
        fun onCancel()
    }

    fun log(mes : String?){
        val shouldLog= true
        if (shouldLog){
            Log.e(tag, String.format("message = %s ,t = %s", mes,Thread.currentThread().name))
        }
    }
}