package com.example.ali.gameengine4.Carscroller;

import com.example.ali.gameengine4.Breakout.*;
import com.example.ali.gameengine4.GameEngine;
import com.example.ali.gameengine4.Screen;

/**
 * Created by Ali on 31-10-2017.
 */

public class Carscroller extends GameEngine
{
    @Override
    public Screen createStartScreen()
    {
        music = loadMusic("breakoutassets/music.ogg");
        return new MainMenuScreen(this);
    }

    public void onPaused()
    {
        super.onPause();
        music.pause();
    }

    public void onResume()
    {
        super.onResume();
        music.play();
    }
}