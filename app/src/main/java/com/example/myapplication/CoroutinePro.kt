package com.example.myapplication

import kotlinx.coroutines.*

abstract class CoroutinePro<Params,Progress,Result> {


    val fixed = newFixedThreadPoolContext(10,"CoroutinePro")

    fun execute(vararg args : Params) : CoroutinePro<Params,Progress,Result>{
        val coroutineScope = CoroutineScope(Dispatchers.Unconfined)
        coroutineScope.launch(Dispatchers.Main) {
            val res = withContext(fixed) {
                doInBackground(*args)
            }
            onPostExecute(res)
        }
        return this
    }

    protected abstract fun doInBackground(vararg args : Params) : Result

    protected open fun onPostExecute(res : Result) {

    }

}