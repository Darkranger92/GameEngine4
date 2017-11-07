package com.example.ali.gameengine4.Breakout;

import android.graphics.Bitmap;

import com.example.ali.gameengine4.GameEngine;

/**
 * Created by Ali on 10-10-2017.
 */

public class WorldRenderer
{
    GameEngine gameEngine;
    World world;
    Bitmap ballImage;
    Bitmap paddleImage;
    Bitmap blocksImage;

    public WorldRenderer(GameEngine ge, World w)
    {
        gameEngine = ge;
        world = w;
        ballImage = gameEngine.loadBitmap("breakoutassets/ball.png");
        paddleImage = gameEngine.loadBitmap("breakoutassets/paddle.png");
        blocksImage = gameEngine.loadBitmap("breakoutassets/blocks.png");
    }

    public void render()
    {
        gameEngine.drawBitmap(ballImage, world.ball.x, world.ball.y);
        gameEngine.drawBitmap(paddleImage, (int) world.paddle.x, (int) world.paddle.y);
        int listSize = world.blocks.size();
        Block block = null;
        for (int i = 0; i < listSize; i++) //draw the blocks in rows and columns
        {
            block = world.blocks.get(i);
            gameEngine.drawBitmap(blocksImage, (int) block.x, (int) block.y,
                    0, (int) (block.type * Block.HEIGHT),
                    (int) Block.WIDTH, (int) Block.HEIGHT); // first time first block next time second etc.
        }

    }
}
