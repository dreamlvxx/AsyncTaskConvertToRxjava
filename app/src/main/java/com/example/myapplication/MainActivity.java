package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    private final static String TAG =  "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AtomicInteger integer = new AtomicInteger();
        CoroutinePro<String,String,String> co = new CoroutinePro<String,String,String>(){

            @Override
            protected String doInBackground(String... args) {
                while (integer.get() < 10000){
                    if (integer.get() == 1000){
//                        cancel();
                    }
                    integer.incrementAndGet();
                    Log.e(TAG, "doInBackground: i = " + integer.get() + "iscancel? = " + isCancelled());
                }
                Log.e(TAG, "doInBackground: final");
                return "res is " + args[0] + Thread.currentThread().getName();
            }

            @Override
            protected void onPostExecute(String res) {
                Log.e(TAG, "onPostExecute: " + res + Thread.currentThread().getName());
            }

            @Override
            protected void onCancelled(String res) {
                super.onCancelled(res);
                Log.e(TAG, "onCancelled: 1" + res);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                Log.e(TAG, "onCancelled: 0");
            }
        }.execute("saaaa");
        co.cancel();
    }
}