package com.example.myapplication

import android.util.Log
import kotlinx.coroutines.*
import java.lang.Runnable
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

object CoroutineUtils {
    //用于承接上下逻辑，把一些耗时操作放在background
    @JvmStatic
    fun waitAndExcuAsyncDefault(prefunc : () -> Unit,nextFunc : () -> Unit){
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO){
                prefunc()
            }
            nextFunc()
        }
    }

    @JvmStatic
    fun <R,T> waitAndExcuAsync(runnable: Callable<R>,after : (R) -> T){
        runBlocking {
            val str = async(Dispatchers.IO) {
                val futureTask = FutureTask(runnable)
                futureTask.run()
                return@async futureTask.get() as R
            }
            after(str.await())
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
                callback.onError()
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
        fun onFinish(res: T)
        fun onError()
        fun onCancel()
    }
}