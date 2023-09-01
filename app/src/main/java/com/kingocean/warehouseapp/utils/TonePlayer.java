package com.kingocean.warehouseapp.utils;

import android.media.AudioManager;
import android.media.ToneGenerator;

public class TonePlayer {
    private ToneGenerator toneGenerator;

    public TonePlayer() {
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }

    public void playSuccessSound(int duration) {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER, duration);
    }

    public void playGoToApp(int duration){
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE);
    }

    public void playErrorSound(int duration) {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_NACK, duration);
    }

    public void playRepackSound(int duration) {
        toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, duration);
    }

    public void playDeRepackSound(int duration) {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_CALLDROP_LITE, duration);
    }

    public void release() {
        toneGenerator.release();
    }
}
