package com.example.myapplication;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public final class Rxjava {
    public static <T> Observable<T> defaultO(ObservableOnSubscribe<T> observable){
        Log.e("xxx", "defaultO: Rxjava");
        return Observable
                .create(observable).observeOn(AndroidSchedulers.mainThread());
    }

}
