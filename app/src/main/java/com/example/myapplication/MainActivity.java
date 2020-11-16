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
            protected String doInBackground(String... arge) {
                return "res is " +arge[0];
            }

            @Override
            protected void onPostExecute(String res) {
                Log.e(TAG, "onPostExecute: " + res);
            }
        }.execute("sad");
    }
}