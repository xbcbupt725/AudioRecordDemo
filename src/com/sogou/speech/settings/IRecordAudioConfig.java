package com.sogou.speech.settings;

import android.media.AudioFormat;

public interface IRecordAudioConfig {
	
	// audio source configuration
	// public static final 
	
	// audio channel configuration
	public static final int MONO = AudioFormat.CHANNEL_IN_MONO;
    public static final int STEREO = AudioFormat.CHANNEL_IN_STEREO;
    
    // audio sample rate configuration
    public static final int DEFAULT_LOW_AUDIO_SAMPLE_RATE = 8000;
    public static final int DEFAULT_HIGH_AUDIO_SAMPLE_RATE = 16000;
    
    // audio encoding configuration
    public static final int PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int PCM_8BIT = AudioFormat.ENCODING_PCM_8BIT;
    
    // max audio length(short), 20 seconds audio
    public static final int MAX_AUDIO_TIME = 20;
    
    // minimum buffer for AudioRecord, measured by Bytes
    public static final int MIN_BUFFER = 4096;
}
