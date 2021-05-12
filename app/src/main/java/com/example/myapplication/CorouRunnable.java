package com.example.myapplication;

public abstract class CorouRunnable implements Runnable {
   public String mTag;

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
