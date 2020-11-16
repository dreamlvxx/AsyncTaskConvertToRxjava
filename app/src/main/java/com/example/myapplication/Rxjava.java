package com.example.myapplication;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;

public final class Rxjava {
    public static <T> Observable<T> defaultO(ObservableOnSubscribe<T> observable){
        return Observable
                .create(observable)
                .observeOn(AndroidSchedulers.mainThread());
    }

}
