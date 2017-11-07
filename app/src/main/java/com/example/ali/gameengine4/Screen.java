package com.example.ali.gameengine4;

/**
 * Created by Ali on 05-09-2017.
 */

public abstract class Screen
{
    protected final GameEngine gameEngine;

    public Screen(GameEngine gameEngine)
    {
        this.gameEngine = gameEngine;
    }

    public abstract void update(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}
