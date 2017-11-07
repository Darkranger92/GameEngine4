package com.example.ali.gameengine4.Breakout;

import com.example.ali.gameengine4.GameEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali on 10-10-2017.
 */

public class World
{
    public static float MIN_X = 0;
    public static float MAX_X = 319;
    public static float MIN_Y = 36;
    public static float MAX_Y = 479;
    Ball ball = new Ball();
    Paddle paddle = new Paddle();
    List<Block> blocks = new ArrayList<>();
    GameEngine gameEngine;
    CollisionListener listener;
    boolean gameOver = false;
    int point = 0;
    int lives = 3;
    int paddleHits = 0;
    int advance = 0;
    boolean levelDone = false;

    public World(GameEngine ge, CollisionListener listener)
    {
        this.gameEngine = ge;
        this.listener = listener;
        generateBlocks();
    }

    private void generateBlocks() //sætter op blockne
    {
        blocks.clear();
        for (int y = 50, type = 0; y < 50 + 1 * Block.HEIGHT; y = y + (int) (Block.HEIGHT), type++) //for each row
        {
            for (int x = 20; x < MAX_X - Block.WIDTH / 2; x = x + (int) (Block.WIDTH)) // for each column
            {
                blocks.add(new Block(x, y, type));
            }
        }

    }

    public void update(float deltatime, float accelX)
    {
        ball.x = (int) (ball.x + ball.vx * deltatime);
        ball.y = (int) (ball.y + ball.vy * deltatime);
        if (ball.x < MIN_X) // tjek om den rammer venstre væg)
        {
            ball.vx = -ball.vx;
            ball.x = (int) MIN_X;
            listener.collisionWall();
        }
        if (ball.x > MAX_X - ball.WIDTH) // tjek om den rammer højer væg
        {
            ball.vx = -ball.vx;
            ball.x = (int) (MAX_X - ball.WIDTH);
            listener.collisionWall();
        }
        if (ball.y < MIN_Y) //up
        {
            ball.vy = -ball.vy;
            ball.y = (int) MIN_Y;
            listener.collisionWall();
        }
//        if (ball.y > MAX_Y - ball.HEIGHT) //ned
//        {
//            ball.vy = -ball.vy;
//            ball.y = (int) (MAX_Y - ball.HEIGHT);
//        }
        if (ball.y + Ball.HEIGHT > MAX_Y)
        {
            lives = lives - 1;
            if (lives == 0)
            {
                gameOver = true;
                listener.gameover();
                return;
            }
            else
            {
                ball.y = (int) MAX_Y / 2;
                if (ball.vy > 0) ball.vy = -ball.vy;
                listener.collisionBlock();
            }

        }

        paddle.x = paddle.x + accelX * deltatime * 50; // movement of the paddle
        if (paddle.x < MIN_X) paddle.x = MIN_X; //if it hits the wall
        if (paddle.x + Paddle.WIDTH > MAX_X) paddle.x = MAX_X - Paddle.WIDTH;
        //paddle move with touch only for testing purposes, Remove for final game
        if (gameEngine.isTouchDown(0))
        {
            if (gameEngine.getTouchY(0) > 450)
            {
                paddle.x = gameEngine.getTouchX(0);
            }
        }
        collideBallPaddle();
        collideBallBlocks(deltatime);

        //if all blocks are removed, regenerate or better start a new lvl
        if (blocks.size() == 0)
        {
            generateBlocks();
        }
    }


    private void collideBallBlocks(float deltatime)
    {
        Block block = null;
        for (int i = 0; i < blocks.size(); i++)
        {
            block = blocks.get(i);
            if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT,
                    block.x, block.y, Block.WIDTH, Block.HEIGHT))
            {
                blocks.remove(i);
                listener.collisionBlock();
                i = i - 1;
                float oldvx = ball.vx;
                float oldvy = ball.vy;
                reflectBall(ball, block);
                ball.x = (int) (ball.x - oldvx * deltatime * 1.01f);
                ball.y = (int) (ball.y - oldvy * deltatime * 1.01f);
                point = point + (10 - block.type);
            }
        }
    }

    private void reflectBall(Ball ball, Block block)
    {
        // check the top left corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1, 1))
        {
            if (ball.vx > 0) ball.vx = -ball.vx;
            if (ball.vy > 0) ball.vy = -ball.vy;
            return;
        }
        //check top right corner
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y, 1, 1))
        {
            if (ball.vx < 0) ball.vx = -ball.vx;
            if (ball.vy > 0) ball.vy = -ball.vy;
            return;
        }
        //check bottom left corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y + Block.HEIGHT, 1, 1))
        {
            if (ball.vx > 0) ball.vx = -ball.vx;
            if (ball.vy < 0) ball.vy = -ball.vy;
            return;
        }
        //check bottom right corner of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y + Block.HEIGHT, 1, 1))
        {
            if (ball.vx < 0) ball.vx = -ball.vx;
            if (ball.vy < 0) ball.vy = -ball.vy;
            return;
        }
        //Check top edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, Block.WIDTH, 1))
        {
            //if (ball.vx > 0) ball.vx = -ball.vx; //the other case should be impossible
            ball.vy = -ball.vy;
            return;
        }
        //check the bottom edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y + Block.HEIGHT, Block.WIDTH, 1))
        {
            ball.vy = -ball.vy;
            return;
        }
        //check the left edge of the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x, block.y, 1, Block.HEIGHT))
        {
            ball.vx = -ball.vx;
            return;
        }
        //check the right edge og the block
        if (collideRects(ball.x, ball.y, Ball.WIDTH, Ball.HEIGHT, block.x + Block.WIDTH, block.y, 1, Block.HEIGHT))
        {
            ball.vx = -ball.vx;
            return;
        }
    }

    private boolean collideRects(float x1, float y1, float width1, float height1,
                                 float x2, float y2, float width2, float height2)
    {
        if ((x1 < x2 + width2) && (x1 + width1 > x2) && (y1 + height1 > y2) && (y1 < y2 + height2))
        {
            return true;
        }
        return false;
    }

    private void collideBallPaddle() // rammer paddlen
    {
        if (ball.y + Ball.HEIGHT >= paddle.y &&
                ball.x < paddle.x + Paddle.WIDTH &&
                ball.x + Ball.WIDTH > paddle.x)
        {
            ball.y = (int) (paddle.y - Ball.HEIGHT - 2);
            ball.vy = -ball.vy;
            listener.collisionPaddle();
            paddleHits++;
            if (paddleHits == 3) // to be adjusted for normal play
            {
                paddleHits = 0;
                advance = 10;
                //advance = advance + 10; // acceerate the advance for a tougher level
                //advanceBlocks();
            }
        }
    }

    private void advanceBlocks()
    {
        Block block;
        int size = blocks.size();
        for (int i = 0; i <size; i++)
        {
            block = blocks.get(i);
            block.y = block.y + advance;
        }
    }
}
