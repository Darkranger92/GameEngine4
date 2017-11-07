package com.example.ali.gameengine4.Breakout;

/**
 * Created by Ali on 24-10-2017.
 */

public interface CollisionListener
{
    public void collisionWall();
    public void collisionPaddle();
    public void collisionBlock();
    public void gameover();
}
