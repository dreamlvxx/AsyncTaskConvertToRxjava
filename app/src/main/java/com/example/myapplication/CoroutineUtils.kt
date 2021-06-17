package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

object CoroutineUtils {
    /**
     *  用于承接上下逻辑，把一些耗时操作放在background,
     *  替换回调的写法
     */
    @JvmStatic
    fun <R> waitAndExcuAsyncDefault(prefunc: () -> R, nextFunc: (R?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            var result: R? = null
            withContext(Dispatchers.IO) {
                try {
                    result = prefunc()
                } catch (e: Exception) {

                }
                result
            }
            nextFunc(result)
        }
    }

    @JvmStatic
    fun <T> excuOnIO(callable: Callable<T>) {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            callable.call()
        }
    }

    @JvmStatic
    fun <T> excuOnIOWithCallback(callable: Callable<T>,callback : Callback<T>) {
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            var result : T? = null
            withContext(Dispatchers.IO){
                try {
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
                Log.e("xxx", String.format("start doInBackground: on Thread [%s]", Thread.currentThread().name))
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
                Log.e("xxx", String.format("receive res on thread [%s],res = %s", Thread.currentThread().name, res))
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
                Log.e("xxx", String.format("start doInBackground: on Thread [%s]", Thread.currentThread().name))
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
                Log.e("xxx", String.format("receive res on thread [%s],res = %s", Thread.currentThread().name, res))
            }

        }.executeOnIO(runnable.tag)
    }

    interface Callback<T> {
        fun onFinish(res: T?)
        fun onError(mes : String?)
        fun onCancel()
    }
}