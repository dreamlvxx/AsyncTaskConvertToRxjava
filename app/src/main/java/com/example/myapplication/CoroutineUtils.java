package com.example.myapplication;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import static com.example.myapplication.CoroutinePro.SERIAL_EXECUTOR;
import static com.example.myapplication.CoroutinePro.THREAD_POOL_EXECUTOR;

public class CoroutineUtils {

    public static CoroutinePro submit(CorouRunnable runnable, Callback callback) {
        CoroutinePro innerCorou = new CoroutinePro<String, String, String>() {

            @Override
            protected String doInBackground(String... args) {
                Log.e("xxx", String.format("start doInBackground: on Thread [%s]", Thread.currentThread().getName()));
                runnable.run();
                return String.format("runnable finish task tag = [%s]on thread [%s]", args[0], Thread.currentThread().getName());
            }

            @Override
            protected void onProgressUpdate(@NotNull String[] data) {
                super.onProgressUpdate(data);
            }

            @Override
            protected void onPostExecute(String res) {
                super.onPostExecute(res);
                callback.onFinish();
                Log.e("xxx", String.format("receive res on thread [%s],res = %s", Thread.currentThread().getName(), res));
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

    public interface Callback {
        void onFinish();

        void onError();

        void onCancel();
    }

}
