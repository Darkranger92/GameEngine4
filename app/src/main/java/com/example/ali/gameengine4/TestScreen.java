package com.example.ali.gameengine4;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.List;


/**
 * Created by Ali on 05-09-2017.
 */

public class TestScreen extends Screen
{
    Bitmap bob = null;
    float bobX = 0;
    int bobY = 50;
    TouchEvent event = null;
    Sound sound = null;
    Music music = null;
    boolean isPlaying = false;

    public TestScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        bob = gameEngine.loadBitmap("bob.png");
        sound = gameEngine.loadSound("blocksplosion.wav");
        music = gameEngine.loadMusic("music.ogg");
        music.setLooping(true);
        music.play();
        isPlaying = true;
    }

    @Override
    public void update(float deltaTime)
    {
        gameEngine.clearFrameBuffer(Color.BLUE);

        bobX = bobX + 100 * deltaTime;
        if (bobX > gameEngine.getFramBufferWidth()) bobX = 0 - bob.getWidth();

        gameEngine.drawBitmap(bob, (int)bobX, bobY);



//        gameEngine.drawBitmap(bob, 10, 10);
//        gameEngine.drawBitmap(bob, 100, 200, 64, 64, 128, 128);


//        for (int pointer = 0; pointer < 5; pointer++)
//        {
//            if (gameEngine.isTouchDown(pointer))
//            {
//                gameEngine.drawBitmap(bob, gameEngine.getTouchX(pointer), gameEngine.getTouchY(pointer));
//            }
//        }

        List<TouchEvent> touchEvents = gameEngine.getTouchEvents();
        int stop = touchEvents.size();
        
        if (stop == 0 && event != null)
        {
            Log.d("testScreen", "Touch event type: " + event.type + ", x: " + event.x + ", y:" + event.y);
            gameEngine.drawBitmap(bob, gameEngine.getTouchX(event.pointer), gameEngine.getTouchY(event.pointer));
        }
        for (int i = 0; i < stop; i++)
        {
            event = touchEvents.get(i);
            Log.d("testScreen", "Touch event type: " + event.type + ", x: " + event.x + ", y:" + event.y);
            gameEngine.drawBitmap(bob, gameEngine.getTouchX(event.pointer), gameEngine.getTouchY(event.pointer));
            if (event.type == TouchEvent.TouchEventType.Down)
            {
                sound.play(1);
            }
        }

        if (gameEngine.isTouchDown(0))
        {
            if (music.isPlaying())
            {
                music.pause();
                isPlaying = false;
            }
            else
            {
                music.play();
                isPlaying = true;
            }
        }



/*
        float accX = gameEngine.getAccelerometer()[0];
        float accY = gameEngine.getAccelerometer()[1];
        //accX = 0; accY = 0;
        float x = gameEngine.getFramBufferWidth() / 2 + (accX/10) * gameEngine.getFramBufferWidth()/2;
        float y = gameEngine.getFramBufferHeight() / 2 + (accY/10) * gameEngine.getFramBufferHeight()/2;
        gameEngine.drawBitmap(bob, (int) (x - (bob.getWidth()/2)), (int) (y - (bob.getHeight()/2)) );
*/
    }

    @Override
    public void pause()
    {
        Log.d("TestScreen", "The screen is pause!!!!!!!!");
        music.pause();
    }

    @Override
    public void resume()
    {
        Log.d("TestScreen", "The screen is resumed");
        if (!isPlaying)
        {
            music.play();
            isPlaying = true;
        }
    }

    @Override
    public void dispose()
    {
        Log.d("TestScreen", "The screen is dispose");
        music.dispose();
    }
}
