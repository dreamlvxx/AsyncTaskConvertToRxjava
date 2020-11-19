package com.example.myapplication;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class RxjavaTask<Params, Progress, Result>{

    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private static final String TAG = "xxx";

    private Observable<Result> innerObservable;
    private Params[] mParams;
    private final AtomicBoolean mCancelled = new AtomicBoolean(false);
    private static InternalHandler sHandler;
    private Disposable mDisposable;

    public RxjavaTask() {
        sHandler = new InternalHandler(Looper.getMainLooper());
        init();
    }

    private void init(){
        innerObservable = Rxjava.defaultO(emitter -> {
            Result res = doInBackground(mParams);
            emitter.onNext(res);
        });
    }

    protected void onPreExecute(){

    }

    protected abstract Result doInBackground(Params...params);

    protected void onPostExecute(Result result){

    }

    public final RxjavaTask<Params, Progress, Result> executeOnExecutor(Executor exec,
                                        Params... params){
        if (null != innerObservable){
            this.mParams = params;
            this.innerObservable = this.innerObservable.subscribeOn(Schedulers.from(exec));
            subscribeInner();
        }
        return this;
    }

    public final RxjavaTask<Params, Progress, Result> execute(Params... params){
        if (null != innerObservable){
            this.mParams = params;
            this.innerObservable = this.innerObservable.subscribeOn(Schedulers.io());
            subscribeInner();
        }
        return this;
    }

    private void subscribeInner(){
        this.innerObservable.subscribe(new Observer<Result>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposable = d;
                onPreExecute();
            }

            @Override
            public void onNext(@NonNull Result result) {
                finish(result);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                onInnerError(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void finish(Result result){
        if (!isCancelled()){
            onPostExecute(result);
        }else{
            onCancelled(result);
        }
    }

    protected void onInnerError(@NonNull Throwable e){

    }

    protected void onCancelled(Result result){
        mCancelled.set(true);
        mDisposable.dispose();
        onCancelled();
    }

    protected void onCancelled(){

    }

    public final boolean cancel() {
        mCancelled.set(true);
        mDisposable.dispose();
        onCancelled(null);
        return true;
    }

    public final boolean isCancelled() {
        return mCancelled.get();
    }

    private Handler getHandler() {
        return sHandler;
    }

    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            getHandler().obtainMessage(MESSAGE_POST_PROGRESS,
                    new RxjavaTaskResult<>(this, values)).sendToTarget();
        }
    }

    protected void onProgressUpdate(Progress... values) {

    }

    private static class InternalHandler extends Handler {
        public InternalHandler(Looper looper) {
            super(looper);
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            RxjavaTaskResult<?> result = (RxjavaTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static class RxjavaTaskResult<Data> {
        final RxjavaTask mTask;
        final Data[] mData;

        RxjavaTaskResult(RxjavaTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }

}
