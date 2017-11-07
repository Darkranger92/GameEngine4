package com.example.ali.gameengine4;

import android.media.SoundPool;

/**
 * Created by Ali on 26-09-2017.
 */

public class Sound
{
    int soundId;
    SoundPool soundPool;

    public Sound(SoundPool soundPool, int soundId)
    {
        this.soundId = soundId;
        this.soundPool = soundPool;
    }

    public void play(float volume)
    {
        soundPool.play(soundId, volume, volume, 0, 0, 1);
    }

    public void dispose()
    {
        soundPool.unload(soundId);
    }
}
