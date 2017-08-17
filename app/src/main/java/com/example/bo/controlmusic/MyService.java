package com.example.bo.controlmusic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    private String TAG = "MyService";

    public MyService () {
    }

    @Override public IBinder onBind (Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException ("Not yet implemented");
    }

    private AudioManager mAm;
    private static boolean vIsActive = false;
    private MyOnAudioFocusChangeListener mListener;

    public class MyOnAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override public void onAudioFocusChange (int focusChange) {
            // TODO Auto-generated method stub
        }
    }

    public int onStartCommand (Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        Log.d (TAG, "onStartCommand");
        Toast.makeText (getApplicationContext (), "服务已启动", Toast.LENGTH_SHORT).show ();

        mAm = (AudioManager) getApplicationContext ().getSystemService (Context.AUDIO_SERVICE);
        vIsActive = mAm.isMusicActive ();
        mListener = new MyOnAudioFocusChangeListener ();
        if (vIsActive) {
            int result =
                mAm.requestAudioFocus (mListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.d (TAG, "requestAudioFocus successfully.");
            } else {
                Log.d (TAG, "requestAudioFocus failed.");
            }
        }
        return super.onStartCommand (intent, flags, startId);
    }

    @Override public void onDestroy () {
        // TODO Auto-generated method stub
        super.onDestroy ();
        if (vIsActive) {
            mAm.abandonAudioFocus (mListener);
        }
        Log.d (TAG, "onDestroy");
    }
}
