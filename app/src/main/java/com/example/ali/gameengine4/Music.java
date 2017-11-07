package com.example.ali.gameengine4;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by Ali on 03-10-2017.
 */

public class Music implements MediaPlayer.OnCompletionListener
{
    private MediaPlayer mediaPlayer;
    private boolean isPrepraed = false;

    public Music(AssetFileDescriptor assetFileDescriptor)
    {
        mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength());
            mediaPlayer.prepare();
            isPrepraed = true;
            mediaPlayer.setOnCompletionListener(this);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not load the music *********");
        }
    }

    public boolean isLooping()
    {
        return mediaPlayer.isLooping();
    }

    public boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean isStopped()
    {
        return !isPrepraed;
    }

    public void dispose()
    {
        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public void pause()
    {
        mediaPlayer.pause();
    }

    public void play()
    {
        if (mediaPlayer.isPlaying()) return;
        try
        {
            synchronized (this)
            {
                if (!isPrepraed) mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public  void setLooping(boolean isLooping)
    {
        mediaPlayer.setLooping(isLooping);
    }

    public void setVolume(float volume)
    {
        mediaPlayer.setVolume(volume, volume);
    }

    public void stop()
    {
        synchronized (this)
        {
            if(!isPrepraed)return;
            mediaPlayer.stop();
            isPrepraed = false;
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        synchronized (this)
        {
            isPrepraed = false;
        }
    }
}
