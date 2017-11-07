package com.example.ali.gameengine4.Carscroller;


import com.example.ali.gameengine4.GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ali on 10-10-2017.
 */

public class World {
    public static float MIN_X = 0;
    public static float MAX_X = 479;
    public static float MIN_Y = 28;
    public static float MAX_Y = 319 - 28;
    Car car = new Car();
    List<Monster> monsterList = new ArrayList<>();
    int maxMonsters = 3;

    GameEngine gameEngine;
    CollisionListener listener;
    boolean gameOver = false;
    int point = 0;
    int lives = 3;


    public World(GameEngine ge, CollisionListener listener) {
        this.gameEngine = ge;
        this.listener = listener;
        initialzeMonsters();

    }

    public void update(float deltatime, float accelY) {
        //move the car based on the phone accelerometer in y-axxis
        car.y = (int) (car.y - accelY * deltatime * 50);

        //car moves with the touch ONLY for testin purposes, REMOVE for final game
        if (gameEngine.isTouchDown(0)) {
            if (gameEngine.getTouchX(0) < 100) {
                car.y = gameEngine.getTouchY(0) - Car.HEIGHT;
            }
        }
        //check if car touches top side of the road
        if (car.y < MIN_Y) car.y = (int) MIN_Y;
        //check if car touches bottom side of the road
        if (car.y + car.HEIGHT > MAX_Y) car.y = (int) (MAX_Y - car.HEIGHT);


        //move the monsters
        Monster monster = null;
        for (int i = 0; i < maxMonsters; i++) {
            monster = monsterList.get(i);
            monsterList.get(i).x = (int) (monsterList.get(i).x - 100 * deltatime);
            if (monster.x < 0 - Monster.WIDTH) // monster left the screen to the left
            {
                Random random = new Random();
                monster.x = 500 + random.nextInt(200);
                monster.y = 28 + random.nextInt(235);
            }
        }


        collideCarMonster();
        //end of update method
    }


    private void collideCarMonster() {
        Monster monster = null;
        for (int i = 0; i < maxMonsters; i++) {
            monster = monsterList.get(i);
            if (collideRects(car.x, car.y, car.WIDTH, car.HEIGHT,
                    monster.x, monster.y, Monster.WIDTH, Monster.HEIGHT)) {
                gameOver = true;
            }
        }
    }


    private boolean collideRects(float x1, float y1, float width1, float height1,
                                 float x2, float y2, float width2, float height2) {
        if ((x1 < x2 + width2) && (x1 + width1 > x2) && (y1 + height1 > y2) && (y1 < y2 + height2)) {
            return true;
        }
        return false;
    }

    private void initialzeMonsters()
    {
        for (int i = 0; i < maxMonsters; i++)
        {
            Random rand = new Random();
            int randX = rand.nextInt(50);
            int randY = rand.nextInt(235);
            Monster monster = new Monster((500 + randX) + i * 100, 28 + randY);
            monsterList.add(monster);
        }
    }

}
