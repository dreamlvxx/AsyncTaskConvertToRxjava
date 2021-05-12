package com.example.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.myapplication.CoroutinePro.SERIAL_EXECUTOR;
import static com.example.myapplication.CoroutinePro.THREAD_POOL_EXECUTOR;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AtomicInteger integer = new AtomicInteger();
//        CoroutinePro<String,String,String> co = new CoroutinePro<String,String,String>(){
//
//            @Override
//            protected String doInBackground(String... args) {
//                while (integer.get() < 10000){
//                    if (integer.get() == 1000){
////                        cancel();
//                    }
//                    integer.incrementAndGet();
//                    Log.e(TAG, "doInBackground: i = " + integer.get() + "iscancel? = " + isCancelled());
//                }
//                Log.e(TAG, "doInBackground: final");
//                return "res is " + args[0] + Thread.currentThread().getName();
//            }
//
//            @Override
//            protected void onPostExecute(String res) {
//                Log.e(TAG, "onPostExecute: " + res + Thread.currentThread().getName());
//            }
//
//            @Override
//            protected void onCancelled(String res) {
//                super.onCancelled(res);
//                Log.e(TAG, "onCancelled: 1" + res);
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                Log.e(TAG, "onCancelled: 0");
//            }
//        }.execute("saaaa");
//        co.cancel();


//       new CoroutinePro<String,String,String>(){
//
//           @Override
//           protected String doInBackground(String... args) {
//               Log.e(TAG, "doInBackground: thread = " + Thread.currentThread().getName());
//               publishProgress("progress");
//               try {
//                   Thread.sleep(300);
//               } catch (InterruptedException e) {
//                   e.printStackTrace();
//               }
//               return "doInBackground" + args[0];
//           }
//
//           @Override
//           protected void onProgressUpdate(@NotNull String[] data) {
//               super.onProgressUpdate(data);
//               Log.e(TAG, "onProgressUpdate: " + data[0] + "thread = " + Thread.currentThread().getName());
//           }
//
//           @Override
//           protected void onPostExecute(String res) {
//               super.onPostExecute(res);
//               Log.e(TAG, "onPostExecute: " + res + "thread = " + Thread.currentThread().getName());
//           }
//       }.executeOnExecutor(SERIAL_EXECUTOR,"[parma1]");

//        for (int i = 0; i < 100; i++) {
//            final int val = i;
//            CoroutineUtils.submit(new CorouRunnable(String.valueOf(i)) {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new CoroutineUtils.Callback() {
//                @Override
//                public void onFinish() {
//
//                }
//
//                @Override
//                public void onError() {
//
//                }
//
//                @Override
//                public void onCancel() {
//
//                }
//            });
//        }

//        Log.e(TAG, "onCreate: start get res");
//        List<String> res= getres();
//        Log.e(TAG, "onCreate: finish getres" + res.toString());
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.e(TAG, "onClick: ");
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}