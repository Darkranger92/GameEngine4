package com.example.ali.gameengine4;

/**
 * Created by Ali on 12-09-2017.
 */

public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        Up,
        Dragged
    }

    public TouchEventType type; // the type of the event
    public int x;               // the x-coordinate of the event
    public int y;               // the y-coordinate of the event
    public int pointer;         // the pointer id (from the android system)
}
