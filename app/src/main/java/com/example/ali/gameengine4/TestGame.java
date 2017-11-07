package com.example.ali.gameengine4;

/**
 * Created by Ali on 05-09-2017.
 */

public class TestGame extends GameEngine
{

    @Override
    public Screen createStartScreen()
    {
        return new TestScreen(this);
    }
}
