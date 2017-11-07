package com.example.ali.gameengine4.Breakout;

import android.graphics.Bitmap;

import com.example.ali.gameengine4.GameEngine;
import com.example.ali.gameengine4.Screen;

/**
 * Created by Ali on 03-10-2017.
 */

public class MainMenuScreen extends Screen
{
    Bitmap mainmenu = null;
    Bitmap insertCoin = null;
    float passedTime = 0;
    long startTime = System.nanoTime();

    public MainMenuScreen(GameEngine gameEngine)
    {
        super(gameEngine);
        mainmenu = gameEngine.loadBitmap("breakoutassets/mainmenu.png");
        insertCoin = gameEngine.loadBitmap("breakoutassets/insertcoin.png");
    }

    @Override
    public void update(float deltaTime)
    {
        if (gameEngine.isTouchDown(0))
        {
            gameEngine.setScreen(new GameScreen(gameEngine));
            return;
        }

        gameEngine.drawBitmap(mainmenu, 0, 0);
        passedTime = passedTime + deltaTime;
        if ((passedTime - (int)passedTime) > 0.5f)
        {
            gameEngine.drawBitmap(insertCoin, 160 - (insertCoin.getWidth()/2), 350);
        }
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {

    }
}
