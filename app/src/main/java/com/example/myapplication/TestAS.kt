package com.example.myapplication

import android.util.Log
import java.util.concurrent.Callable
import kotlin.reflect.KProperty

fun test(){
    getmess("asd",{ Log.e("xxx", "test: func", ) }, "asdasd")
}

fun main() {
//    val ImpdBa = BainImpl()
//    val BaHasDelgate = BaHasDelgate(ImpdBa)
//    BaHasDelgate.extraMathod()
//    BaHasDelgate.printX()
//    CoroutineUtils.excuOnIO(object : Callable<String> {
//        override fun call(): String {
//
//            return ""
//        }
//    })
}

class TestAS {
    companion object{

        @JvmStatic
        fun doMes(){
            CoroutineUtils.waitAndExcuAsyncDefault({
                bacFunc()
            },{
                maiFunc(it)
            })
        }

        fun bacFunc() :String{
            return "bacFunc res"
        }

        fun maiFunc(s :String?){
            Log.e(TAG, "maiFunc: " + s)
        }
    }
}



interface BaIn{
    fun printX()
}

class BainImpl : BaIn{
    override fun printX() {
        println("baimpl")
    }
}

class BaHasDelgate(delgatre : BaIn) : BaIn by delgatre{
    fun extraMathod(){
        println("extra method")
    }
}

fun getmess(mess :String,ssss :() -> Unit,sss :String){
    Log.e("xxx", "getmess: ")
    ssss.invoke()
    Log.e("sadads","sad")
    Log.e("asd","sad")
    Log.e("asd","after")
    Log.e("asd","333")

}