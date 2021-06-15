package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionImpl;
import kotlinx.coroutines.GlobalScope;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doManyTasks();
            }
        });
    }

    private void doManyTasks(){
        CoroutineUtils.submit(new CorouRunnable<String>() {
            @Override
            public String call() throws Exception {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    Log.e(TAG, "call: " + i);
                }
                return null;
            }
        });

        CoroutineUtils.submit(new CorouRunnable<String>() {
            @Override
            public String call() throws Exception {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(1000);
                    Log.e(TAG, "call: +++" + i);
                }
                return null;
            }
        });
    }

    private void docancel(){
        AtomicInteger integer = new AtomicInteger();
        CoroutinePro<String, String, String> co = new CoroutinePro<String, String, String>() {

            @Override
            protected String doInBackground(String... args) {
                while (integer.get() < 10000 && !isCancelled()) {
                    if (integer.get() == 1000) {
                        cancel();
                        Log.e(TAG, "doInBackground: 1000le");
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
    }
}