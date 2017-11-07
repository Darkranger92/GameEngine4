package com.example.ali.gameengine4.Breakout;

/**
 * Created by Ali on 17-10-2017.
 */

public class Block
{
    public static final float WIDTH = 40;
    public static final float HEIGHT = 18;
    public float x;
    public float y;
    public int type;

    public Block(float x, float y, int type)
    {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
