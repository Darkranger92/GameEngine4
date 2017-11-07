package com.example.ali.gameengine4;

/**
 * Created by Ali on 12-09-2017.
 */

public class TouchEventPool extends Pool<TouchEvent>
{
    @Override
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    }
}
