// IMusicController.aidl
package com.pp.neteasemusic;

// Declare any non-default types here with import statements

interface IMusicController {
    boolean play(String url);
    boolean pause();
    void next(String url);
    void previous(String url);
    void progress(int duration);
    int getCurrentTime();
    boolean isPlaying();
}
