package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RxjavaTask<String,Integer,String>(){

            @Override
            protected String doInBackground(String... strings) {
                Log.e("xxx", "doInBackground: params  = " + strings[0] + Thread.currentThread().getName());
//                String asd = null;
//                asd.length();
                return "this is result + x";
            }
        }.execute("PParams");
    }
}