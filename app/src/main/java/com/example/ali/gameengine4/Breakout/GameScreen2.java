package com.example.ali.gameengine4.Breakout;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import com.example.ali.gameengine4.GameEngine;
import com.example.ali.gameengine4.Screen;
import com.example.ali.gameengine4.Sound;
import com.example.ali.gameengine4.TouchEvent;

import java.util.List;

/**
 * Created by Ali on 24-10-2017.
 */

public class GameScreen2 extends GameScreen

{
    enum State
    {
        Paused,
        Running,
        GameOver
    }

    WorldLevel2 world;
    WorldRenderer2 renderer;
    GameScreen.State state = GameScreen.State.Running;

    Bitmap background = null;
    Bitmap resume = null;
    Bitmap gameOver = null;
    Typeface font = null;
    Sound bounceSound = null;
    Sound blockSound = null;
    Sound gameOverSound = null;


    public GameScreen2(GameEngine gameEngine)
    {
        super(gameEngine);
        world = new WorldLevel2(gameEngine, new CollisionListener()
        {
            @Override
            public void collisionWall()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionPaddle()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionBlock()
            {
                blockSound.play(1);
            }
            @Override
            public void gameover()
            {
                gameOverSound.play(1);
            }
        });
        renderer = new WorldRenderer2(gameEngine, world);
        background = gameEngine.loadBitmap("breakoutassets/background.png");
        resume = gameEngine.loadBitmap("breakoutassets/resume.png");
        gameOver = gameEngine.loadBitmap("breakoutassets/gameover.png");
        font = gameEngine.loadfont("breakoutassets/font.ttf");
        bounceSound = gameEngine.loadSound("breakoutassets/bounce.wav");
        blockSound = gameEngine.loadSound("breakoutassets/blocksplosion.wav");
        gameOverSound = gameEngine.loadSound("breakoutassets/gameover.wav");
    }


    @Override
    public void update(float deltaTime)
    {
        if (world.gameOver)
        {
            state = GameScreen.State.GameOver;
        }

        // level done


        if (state == GameScreen.State.Paused && gameEngine.getTouchEvents().size() > 0)
        {
            state = GameScreen.State.Running;
            resume();
        }
        if (state == GameScreen.State.GameOver)
        {
            List<TouchEvent> events = gameEngine.getTouchEvents();
            for (int i = 0; i < events.size(); i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    gameEngine.setScreen(new MainMenuScreen(gameEngine));
                    return;
                }
            }
        }

        if (state == GameScreen.State.Running && gameEngine.getTouchY(0) < 30 && gameEngine.getTouchX(0) > 320 - 38)
        {
            state = GameScreen.State.Paused;
            pause();
            return;
        }

        gameEngine.drawBitmap(background, 0, 0);
        if (state == GameScreen.State.Running)
        {
            world.update(deltaTime, gameEngine.getAccelerometer()[0]);
        }


        renderer.render();

        gameEngine.drawText(font, ("point: " + world.point + " lives " + world.lives), 24, 24, Color.GREEN, 15);

        if (state == GameScreen.State.Paused)
        {
            gameEngine.drawBitmap(resume, 160 - resume.getWidth() / 2, 240 - resume.getHeight() / 2);
        }
        if (state == GameScreen.State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 160 - gameOver.getWidth() / 2, 240 - gameOver.getHeight() / 2);
        }
    }

    @Override
    public void pause()
    {
        if (state == GameScreen.State.Running) state = GameScreen.State.Paused;
        gameEngine.music.pause();
    }

    @Override
    public void resume()
    {
        gameEngine.music.play();
    }

    @Override
    public void dispose()
    {

    }
}
