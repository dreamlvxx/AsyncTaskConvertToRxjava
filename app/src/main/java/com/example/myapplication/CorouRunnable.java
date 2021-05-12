package com.example.myapplication;

import java.util.concurrent.Callable;

public abstract class CorouRunnable<T> implements Callable<T> {
    private static final String DEFAULT_TAG = "CorouRunnable";
   public String mTag;

    public CorouRunnable() {
        mTag = DEFAULT_TAG;
    }

    public CorouRunnable(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }
}
