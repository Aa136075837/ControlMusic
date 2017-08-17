package com.example.bo.controlmusic;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.RemoteController;
import android.os.Build;
import android.os.SystemClock;
import android.service.notification.NotificationListenerService;
import android.view.KeyEvent;

/**
 * @author bo.
 * @Date 2017/5/11.
 * @desc
 */

@TargetApi (Build.VERSION_CODES.KITKAT) public class MusicState extends NotificationListenerService
    implements RemoteController.OnClientUpdateListener {

    public MusicState () {
    }
    private RemoteController remoteController;
    private boolean clientIdLost;
    private Context mContext;

    public MusicState (Context context) {
        mContext = context;
    }

    @Override public void onClientChange (boolean b) {

    }

    @Override public void onClientPlaybackStateUpdate (int i) {

    }

    @Override public void onClientPlaybackStateUpdate (int i, long l, long l1, float v) {

    }

    @Override public void onClientTransportControlUpdate (int i) {

    }

    @Override public void onClientMetadataUpdate (RemoteController.MetadataEditor metadataEditor) {

    }

    public void registerRemoteController(AudioManager manager) {
        remoteController
            = new RemoteController(mContext, this);
        boolean registered;
        try {
            registered = manager.registerRemoteController(remoteController);
        } catch (NullPointerException e) {
            registered = false;
        }
        if (registered) {
            try {
                remoteController.setArtworkConfiguration(
                    getResources().getDimensionPixelSize(R.dimen.remote_artwork_bitmap_width),
                    getResources().getDimensionPixelSize(R.dimen.remote_artwork_bitmap_height));
                remoteController.setSynchronizationMode(RemoteController.POSITION_SYNCHRONIZATION_CHECK);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean sendMusicKeyEvent(int keyCode) {
        if (!clientIdLost && remoteController != null) {
            // send "down" and "up" key events.
            KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            boolean down = remoteController.sendMediaKeyEvent(keyEvent);
            keyEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            boolean up = remoteController.sendMediaKeyEvent(keyEvent);
            return down && up;
        } else {
            long eventTime = SystemClock.uptimeMillis();
            KeyEvent key = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0);
            dispatchMediaKeyToAudioService(key);
            dispatchMediaKeyToAudioService(KeyEvent.changeAction(key, KeyEvent.ACTION_UP));
        }
        return false;
    }

    private void dispatchMediaKeyToAudioService(KeyEvent event) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager != null) {
            try {
                audioManager.dispatchMediaKeyEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
