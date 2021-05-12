package com.example.myapplication

import android.util.Log
import kotlin.reflect.KProperty

fun test(){
    getmess("asd",{ Log.e("xxx", "test: func", ) }, "asdasd")
}

fun main() {
    val ImpdBa = BainImpl()
    val BaHasDelgate = BaHasDelgate(ImpdBa)
    BaHasDelgate.extraMathod()
    BaHasDelgate.printX()
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