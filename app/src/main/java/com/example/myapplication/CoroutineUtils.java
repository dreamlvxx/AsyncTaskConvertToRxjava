package com.example.myapplication;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

public class CoroutineUtils {
    public static <T> CoroutinePro submit(CorouRunnable<T> runnable, Callback<T> callback) {
        CoroutinePro innerCorou = new CoroutinePro<String, String, T>() {

            @Override
            protected T doInBackground(String... args) {
                Log.e("xxx", String.format("start doInBackground: on Thread [%s]", Thread.currentThread().getName()));
//                runnable.run();
                FutureTask<T> futureTask = new FutureTask<>(runnable);
                futureTask.run();
                try {
                    return futureTask.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(@NotNull String[] data) {
                super.onProgressUpdate(data);
            }

            @Override
            protected void onPostExecute(T res) {
                super.onPostExecute(res);
                Log.e("xxx", String.format("receive res on thread [%s],res = %s", Thread.currentThread().getName(), res));
                callback.onFinish(res);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                callback.onCancel();
            }

            @Override
            protected void onError(@NotNull Exception e) {
                super.onError(e);
                callback.onError();
            }
        }.executeOnIO(runnable.getTag());
        return innerCorou;
    }

    public interface Callback<T> {
        void onFinish(T res);

        void onError();

        void onCancel();
    }

}
