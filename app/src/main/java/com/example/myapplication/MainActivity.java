package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final static String TAG =  "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CoroutinePro co = new CoroutinePro<String,String,String>(){

            @Override
            protected String doInBackground(String... args) {
                Log.e(TAG, "doInBackground: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "res is " + args[0] + Thread.currentThread().getName();
            }

            @Override
            protected void onPostExecute(String res) {
                Log.e(TAG, "onPostExecute: " + res + Thread.currentThread().getName());
            }
        }.execute("sad");
    }
}