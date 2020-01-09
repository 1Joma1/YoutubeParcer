package com.joma.youtubeparcer.utils;

public class CallBacks {
    void callbackObserver(Object object) {

    }

    public interface playerCallBack {
        void onItemClickOnItem(int albumId);

        void onPlayingEnd();
    }
}
