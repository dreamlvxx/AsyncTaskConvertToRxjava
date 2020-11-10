package com.example.myapplication;



import android.util.Log;

import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public abstract class RxjavaTask<Params, Progress, Result>{
    private final static String TAG = "xxx";

    private Observable<Result> innerObservable;
    private Params[] mParams;

    public RxjavaTask() {
        init();
    }

    private void init(){
        Log.e(TAG, "init: ");
        innerObservable = Rxjava.defaultO(emitter -> {
            Result res = doInBackground(mParams);
            emitter.onNext(res);
        });
    }

    protected void onPreExecute(){
        Log.e(TAG, "onPreExecute: ");
    }

    protected abstract Result doInBackground(Params...params);

    protected void onPostExecute(Result result){
        Log.e(TAG, "onPostExecute: " + result + Thread.currentThread().getName());
    }

    public final void executeOnExecutor(Executor exec,
                                        Params... params){
        execute(params);
    }

    public final void execute(Params... params){
        mParams = params;
        subscribeInner();
    }

    private void subscribeInner(){
        innerObservable.subscribe(new Observer<Result>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                onPreExecute();
            }

            @Override
            public void onNext(@NonNull Result result) {
                onPostExecute(result);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                onCancelled();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    protected void onCancelled(Result result){
        onCancelled();
    }

    protected void onCancelled(){
        Log.e(TAG, "onCancelled: ");
    }

}
