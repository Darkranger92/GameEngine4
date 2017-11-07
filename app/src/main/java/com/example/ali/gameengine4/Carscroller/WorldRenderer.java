package com.example.ali.gameengine4.Carscroller;

import android.graphics.Bitmap;

import com.example.ali.gameengine4.GameEngine;

/**
 * Created by Ali on 31-10-2017.
 */

public class WorldRenderer
{
        GameEngine gameEngine;
        World world;
        Bitmap carImage;
        Bitmap monsterImage
    ;

    public WorldRenderer(GameEngine ge, World w)
        {
            gameEngine = ge;
            world = w;
            carImage = gameEngine.loadBitmap("carscrollerassets/xbluecar2.png");
            monsterImage = gameEngine.loadBitmap("carscrollerassets/xyellowmonster2.png");

        }

    public void render()
    {
        gameEngine.drawBitmap(carImage, world.car.x, world.car.y);
        for (int i = 0; i < world.maxMonsters; i++) {
            gameEngine.drawBitmap(monsterImage, world.monsterList.get(i).x,
                    world.monsterList.get(i).y);
        }


}

}
