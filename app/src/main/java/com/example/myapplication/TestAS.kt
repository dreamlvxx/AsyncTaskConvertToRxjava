package com.example.myapplication

import android.util.Log

fun test(){
    getmess("asd",{ Log.e("xxx", "test: func", ) }, "asdasd")
}

fun getmess(mess :String,ssss :() -> Unit,sss :String){
    Log.e("xxx", "getmess: ")
    ssss.invoke()
    Log.e("sadads","sad")

}