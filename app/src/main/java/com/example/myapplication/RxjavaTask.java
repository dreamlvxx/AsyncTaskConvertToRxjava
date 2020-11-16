package com.example.myapplication;



import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class RxjavaTask<Params, Progress, Result>{

    private Observable<Result> innerObservable;
    private Params[] mParams;

    public RxjavaTask() {
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

    public final void executeOnExecutor(Executor exec,
                                        Params... params){
        if (null != innerObservable){
            this.mParams = params;
            this.innerObservable.subscribeOn(Schedulers.from(exec));
            subscribeInner();
        }
    }

    public final void execute(Params... params){
        if (null != innerObservable){
            this.mParams = params;
            this.innerObservable.subscribeOn(Schedulers.io());
            subscribeInner();
        }
    }

    private void subscribeInner(){
        this.innerObservable.subscribe(new Observer<Result>() {
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

    }

}
