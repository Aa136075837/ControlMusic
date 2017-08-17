package com.example.bo.controlmusic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AudioManager mAudioManager;
    private Button mPause;
    private Button mLarge;
    private Button mLow;
    private Button mNextMusic;
    private Button mPreMusic;
    private RemoteControlClient mRemoteControlClient;
    private MusicState mMusicState;
    private RemoteControlClient mMyRemoteControlClient;
    private Handler mHandler;
    private Button mCamera;

    @TargetApi (Build.VERSION_CODES.M) @RequiresApi (api = Build.VERSION_CODES.JELLY_BEAN_MR2) @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        if (Build.VERSION.SDK_INT > 23){
            requestPermissions (new String[] {
                    Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET,
                    Manifest.permission.CONTROL_LOCATION_UPDATES, Manifest.permission.MEDIA_CONTENT_CONTROL
            }, 111);
        }
        initViewAndListener ();
        mAudioManager = (AudioManager) getApplicationContext ().getSystemService (Context.AUDIO_SERVICE);
        mHandler = new Handler ();
        nextMusic ();
        initCamera();
        //registerMusicState();
    }

    private void initCamera () {
    }

    private void initViewAndListener () {
        mPause = (Button) findViewById (R.id.pause);
        mLarge = (Button) findViewById (R.id.voice_large);
        mLow = (Button) findViewById (R.id.voice_low);
        mNextMusic = (Button) findViewById (R.id.next_music);
        mPreMusic = (Button) findViewById (R.id.pre_music);
        mCamera = (Button) findViewById (R.id.camera);
        mNextMusic.setOnClickListener (this);
        mPreMusic.setOnClickListener (this);
        mLarge.setOnClickListener (this);
        mLow.setOnClickListener (this);
        mPause.setOnClickListener (this);
        mCamera.setOnClickListener (this);
    }

    private void registerMusicState () {
        //mMusicState = new MusicState (this);
        //mMusicState.registerRemoteController (mAudioManager);

        ComponentName myEventReceiver =
            new ComponentName (getPackageName (), MyRemoteControlEventReceiver.class.getName ());
        AudioManager myAudioManager = (AudioManager) getSystemService (Context.AUDIO_SERVICE);
        myAudioManager.registerMediaButtonEventReceiver (myEventReceiver);
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent (Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent (myEventReceiver);
        PendingIntent mediaPendingIntent =
            PendingIntent.getBroadcast (getApplicationContext (), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        mMyRemoteControlClient = new RemoteControlClient (mediaPendingIntent);
        myAudioManager.registerRemoteControlClient (mMyRemoteControlClient);

        mMyRemoteControlClient.setPlaybackState (RemoteControlClient.PLAYSTATE_PLAYING);

        myAudioManager.requestAudioFocus (new AudioManager.OnAudioFocusChangeListener () {

            @Override public void onAudioFocusChange (int focusChange) {
                System.out.println ("focusChange = " + focusChange);
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS
            | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
            | RemoteControlClient.FLAG_KEY_MEDIA_PLAY
            | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
            | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE
            | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
        mMyRemoteControlClient.setTransportControlFlags (flags);
    }

    @RequiresApi (api = Build.VERSION_CODES.JELLY_BEAN_MR2) private void nextMusic () {
        Intent intent = new Intent (Intent.ACTION_MEDIA_BUTTON);
        PendingIntent pendingIntent = PendingIntent.getActivity (this, 110, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteControlClient = new RemoteControlClient (pendingIntent);
    }

    @Override public void onClick (View view) {
        switch (view.getId ()) {
            case R.id.pause://暂停
                if (mAudioManager.isMusicActive ()) {
                    startService (new Intent (this, MyService.class));
                    mPause.setText ("继续==");
                } else {
                    stopService (new Intent (this, MyService.class));
                    mPause.setText ("暂停--");
                }
                break;
            case R.id.voice_large://增大
                //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                //    mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
                mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
                break;
            case R.id.voice_low://减小
                mAudioManager.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                    AudioManager.FX_FOCUS_NAVIGATION_UP);
                break;
            case R.id.next_music:
                //mMusicState.sendMusicKeyEvent (KEYCODE_MEDIA_NEXT);
                Intent intent = new Intent (Intent.ACTION_MEDIA_BUTTON);
                intent.putExtra (Intent.EXTRA_KEY_EVENT,
                    new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                sendBroadcast (intent);
                //Toast.makeText (MainActivity.this, "发送模仿耳机广播", Toast.LENGTH_SHORT).show ();
                //mRemoteControlClient.setTransportControlFlags (RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
                break;
            case R.id.pre_music:
                //mMusicState.sendMusicKeyEvent (KEYCODE_MEDIA_PREVIOUS);
                sendBroadcast (new Intent (Intent.ACTION_MEDIA_BUTTON));
                //Toast.makeText (this, "上一曲", Toast.LENGTH_SHORT).show ();
                //mRemoteControlClient.setTransportControlFlags (RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS);
                break;
            case R.id.camera:
                //sendMediaKeyCode ();

                break;
        }
    }

    private void sendMediaKeyCode () {
        /**
         * KEYCODE_MEDIA_NEXT  下键
         * KEYCODE_MEDIA_PLAY_PAUSE  中间键
         * KEYCODE_HEADSETHOOK  中间键
         * KEYCODE_MEDIA_PREVIOUS  上键
         */
        Intent cameraIntent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri.fromFile(new File ("1111"));
        startActivityForResult(cameraIntent,4);

        mHandler.postDelayed (new Runnable () {
            @Override public void run () {
                Intent intent = new Intent (Intent.ACTION_MEDIA_BUTTON);
                intent.putExtra (Intent.EXTRA_KEY_EVENT,
                    new KeyEvent (KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
                sendBroadcast (intent);
                Toast.makeText (MainActivity.this, "模拟按键  KEYCODE_MEDIA_PLAY_PAUSE ", Toast.LENGTH_SHORT).show ();
                //sendBroadcast (new Intent (Intent.ACTION_MEDIA_BUTTON));
            }
        }, 3000);
    }

    private class MyRemoteControlEventReceiver {

    }
}
