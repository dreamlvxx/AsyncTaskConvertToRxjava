package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private final static String TAG =  "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        CoroutinePro co = new CoroutinePro<String,String,String>(){
//
//            @Override
//            protected String doInBackground(String... args) {
//                Log.e(TAG, "doInBackground: " + Thread.currentThread().getName());
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return "res is " + args[0] + Thread.currentThread().getName();
//            }
//
//            @Override
//            protected void onPostExecute(String res) {
//                Log.e(TAG, "onPostExecute: " + res + Thread.currentThread().getName());
//            }
//        }.execute("sad");

        RxjavaTask<String,String,String> task = new RxjavaTask<String,String,String>(){

            @Override
            protected String doInBackground(String... strings) {
                try {
                    Thread.sleep(3000);
                    cancel();
                    publishProgress("BBBBB");
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "this is res : " + strings[0];
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e(TAG, "onPostExecute: res is ====" + s);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.e(TAG, "onPreExecute: ");
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                Log.e(TAG, "onProgressUpdate: value is === " + values[0] + Thread.currentThread().getName());
            }

            @Override
            protected void onCancelled(String s) {
                super.onCancelled(s);
                Log.e(TAG, "onCancelled: " + s);
            }
        };
        task.execute("[this is parmas]");
    }
}