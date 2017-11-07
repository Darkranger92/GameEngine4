package com.example.ali.gameengine4.Carscroller;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import com.example.ali.gameengine4.GameEngine;
import com.example.ali.gameengine4.Screen;
import com.example.ali.gameengine4.Sound;
import com.example.ali.gameengine4.TouchEvent;

import java.util.List;

/**
 * Created by Ali on 31-10-2017.
 */

public class GameScreen extends Screen
{
    enum State
    {
        Paused,
        Running,
        GameOver
    }

    World world;
    WorldRenderer renderer;
    State state = State.Running;

    Bitmap background = null;
    float backgroundX = 0;

    Bitmap resume = null;
    Bitmap gameOver = null;
    //Typeface font = null;
    Sound bounceSound = null;
    Sound blockSound = null;
    Sound gameOverSound = null;


    public GameScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        world = new World(gameEngine, new CollisionListener()
        {
            @Override
            public void collisionWall()
            {
                bounceSound.play(1);
            }

            @Override
            public void collisionMonster()
            {

            }

            @Override
            public void gameover()
            {

            }
        });


        renderer = new WorldRenderer(gameEngine, world);

        background = gameEngine.loadBitmap("carscrollerassets/xcarbackground.png");
        resume = gameEngine.loadBitmap("carscrollerassets/resume.png");
        gameOver = gameEngine.loadBitmap("carscrollerassets/gameover.png");
        //font = gameEngine.loadfont("carscrollerassets/font.ttf");
        bounceSound = gameEngine.loadSound("carscrollerassets/bounce.wav");
        blockSound = gameEngine.loadSound("carscrollerassets/blocksplosion.wav");
        gameOverSound = gameEngine.loadSound("carscrollerassets/gameover.wav");
    }


    @Override
    public void update(float deltaTime)
    {
        if (world.gameOver)
        {
            state = State.GameOver;
        }


        if (state == State.Paused && gameEngine.getTouchEvents().size() > 0)
        {
            state = State.Running;
            resume();
        }
        if (state == State.GameOver)
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

        if (state == State.Running && gameEngine.getTouchY(0) < 30 && gameEngine.getTouchX(0) > 320 - 38)
        {
            state = State.Paused;
            pause();
            return;
        }



        if (state == State.Running)
        {
            //scroll the background image
            backgroundX = backgroundX + 100 * deltaTime;
            if (backgroundX > 2700 - 480)
            {
                backgroundX = 0;
            }
            //update all the game world objects
            world.update(deltaTime, gameEngine.getAccelerometer()[1]);
        }
        gameEngine.drawBitmap(background, 0, 0, (int)backgroundX, 0, 480, 320);
        //draw all the game objects
        renderer.render();

        //gameEngine.drawText(font, ("point: " + world.point + " lives " + world.lives), 24, 24, Color.GREEN, 15);

        if (state == State.Paused)
        {
            gameEngine.drawBitmap(resume, 240 - resume.getWidth() / 2, 160 - resume.getHeight() / 2);
        }
        if (state == State.GameOver)
        {
            gameEngine.drawBitmap(gameOver, 240 - gameOver.getWidth() / 2, 160 - gameOver.getHeight() / 2);
        }
    }

    @Override
    public void pause()
    {
        if (state == State.Running) state = State.Paused;
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
