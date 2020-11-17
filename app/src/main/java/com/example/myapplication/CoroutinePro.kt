package com.example.myapplication

import kotlinx.coroutines.*

abstract class CoroutinePro<Params, Progress, Result> {


    val fixed = newFixedThreadPoolContext(10, "CoroutinePro")
    var job: Job? = null

    fun execute(vararg args: Params): CoroutinePro<Params, Progress, Result> {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        job = coroutineScope.launch {
            onPreExecute()
            val res = withContext(Dispatchers.IO) {
                doInBackground(*args)
            }
            onPostExecute(res)
        }
        return this
    }

    protected abstract fun doInBackground(vararg args: Params): Result

    protected open fun onPostExecute(res: Result) {

    }

    protected open fun onPreExecute(){

    }

}