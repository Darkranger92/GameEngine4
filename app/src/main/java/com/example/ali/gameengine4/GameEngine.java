package com.example.ali.gameengine4;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class GameEngine extends Activity implements Runnable, SensorEventListener
{
    private int framesPerSecound = 0;

    private Screen screen;
    private Canvas canvas;
    private Bitmap virtualScreen;
    Rect src = new Rect();
    Rect dst = new Rect();

    Paint paint = new Paint();
    public Music music = null;

    private SoundPool soundPool;

    private TouchHandler touchHandler;
    private TouchEventPool touchEventPool = new TouchEventPool();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private List<TouchEvent> touchEventCopied = new ArrayList<>();

    private float[] accelerometer = new float[3];

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> stateChanges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();


        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        SoundPool.Builder sBoulder = new SoundPool.Builder();
        sBoulder.setMaxStreams(20);
        AudioAttributes.Builder audioAttrBuilder = new AudioAttributes.Builder();
        audioAttrBuilder.setUsage(AudioAttributes.USAGE_GAME);
        AudioAttributes audioAttr = audioAttrBuilder.build();
        sBoulder.setAudioAttributes(audioAttr);
        this.soundPool = sBoulder.build();

        //this.soundpool = new Soundpool(20, AudioManager, Stream_Music, 0); // the old way of getting the soundPool

        screen = createStartScreen();

        if (surfaceView.getWidth() > surfaceView.getHeight())
        {
            setVirtualScreen(480, 320);
        }
        else
        {
            setVirtualScreen(320, 480);
        }

        //manual set to landscape
      setVirtualScreen(480, 320); // ændring til breackout
        // ******* important ******** net flexible right now!!!!!
        // above test screen orientation is premature in the app lifecyrcle
        // so we force the virtual screen to be landscape

//        AudioAttributes audioAttr = new AudioAttributes.Builder().build();
//        AudioAttributes.
//        this.soundPool = new SoundPool();

        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
    }// end of onCreate (= method

    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
    }

    public void onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3);
        accelerometer[0] = -1.0f * accelerometer[0];
    }

    public float[] getAccelerometer()
    {
        return accelerometer;
    }

    private void fillEvents()
    {
        synchronized (touchEventBuffer)
        {
            int stop = touchEventBuffer.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventCopied.add(touchEventBuffer.get(i)); // copy all objects from list to the other
            }
            touchEventBuffer.clear(); //empty the buffer
        }
    }

    private void freeEvents()
    {
        synchronized (touchEventCopied)
        {
            int stop = touchEventCopied.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventPool.free(touchEventCopied.get(i)); //return all used objects to the free pool
            }
            touchEventCopied.clear(); // empty the list with used objects
        }
    }

    public List<TouchEvent> getTouchEvents()
    {
        return touchEventCopied;
    }

    public void setVirtualScreen(int width, int height)  //laver skærm størrelse
    {
        if (virtualScreen != null) virtualScreen.recycle();
        virtualScreen = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(virtualScreen);
    }

    public int getFramBufferWidth()
    {
        return virtualScreen.getWidth();
    }

    public int getFramBufferHeight()
    {
        return virtualScreen.getHeight();
    }

    public abstract Screen createStartScreen();

    public void setScreen(Screen screen)
    { // this methed allow us to switch from a screen to another
        if (this.screen != null) this.screen.dispose();
        this.screen = screen;
    }

    public Bitmap loadBitmap(String filename)
    {
        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = getAssets().open(filename);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
            {
                throw new RuntimeException("*** Could not find graphics in this file " + filename);
            }
            return bitmap;
        } catch (Exception e)
        {
            throw new RuntimeException("*** Could not load that stupid graphics file " + filename);
        } finally
        {
            if (in != null)
            {
                try
                {
                    in.close();

                } catch (IOException e)
                {
                }
            }
        }
    }//

    public Music loadMusic(String filename)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(filename);
            return new Music(assetFileDescriptor);
        } catch (IOException e)
        {
            throw new RuntimeException("Could not load music file: " + filename + "!!!!!!!!!!!!");
        }
    }

    public Sound loadSound(String filename)
    {
        try
        {
            AssetFileDescriptor assDescriptor = getAssets().openFd(filename);
            int soundId = soundPool.load(assDescriptor, 0);
            Sound sound = new Sound(soundPool, soundId);
            return sound;
        } catch (IOException e)

        {
            throw new RuntimeException("Could not load sound from file: " + filename);
        }
    }

    public void clearFrameBuffer(int color)
    {
        canvas.drawColor(color);
    }

    public int getFrameBufferwidth()
    {
        return virtualScreen.getWidth();
    }

    public int getFrameBufferHeight()
    {
        return virtualScreen.getHeight();
    }

    public void drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null) canvas.drawBitmap(bitmap, x, y, null);
    }

    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        Rect src = new Rect();
        Rect dst = new Rect();
        if (canvas == null) return;
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src.bottom = srcY + srcHeight;
        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;
        canvas.drawBitmap(bitmap, src, dst, null);
    }

    public boolean isTouchDown(int pointer)
    {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer)
    {
        int virtualX = 0;
        virtualX = (int) ((float) touchHandler.getTouchX(pointer) / (float) surfaceView.getWidth() * virtualScreen.getWidth());
        return virtualX;
    }

    public int getTouchY(int pointer)
    {
        int virtualY = 0;
        virtualY = (int) ((float) touchHandler.getTouchY(pointer) / (float) surfaceView.getHeight() * virtualScreen.getHeight());
        return virtualY;
    }

    public Typeface loadfont(String filename)
    {
        Typeface font = Typeface.createFromAsset(getAssets(),filename);
        if (font == null)
        {
            throw new RuntimeException("could load font from assets: " + filename);
        }
        return font;
    }

    public void drawText(Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.setColor(color);
        canvas.drawText(text, x, y, paint);
    }



    public void onPause()
    {
        super.onPause();
        synchronized (stateChanges)
        {
            if (isFinishing())
            {
                stateChanges.add(state.Disposed);
            } else
            {
                stateChanges.add(State.Paused);
            }
        }
        try
        {
            mainLoopThread.join();
        } catch (Exception e)
        {
            Log.d("GameEngine", "something went shit when waiting for MainLoop thread to die");
        }
        if (isFinishing())
        {
            ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
        }
    }

    public void onResume()
    {
        super.onResume();
        synchronized (stateChanges)
        {
            stateChanges.add(state.Resumed);
        }
        Log.d("GameEngine", "*** About to start the main");
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
    }

    @Override
    public void run()
    { // det er en thread som køre en del af af programmet
        //int frames = 0;
        long StartTime = System.nanoTime();
        long currentTime = StartTime;
        float deltaTime = 0;



        while (true)
        {

            synchronized (stateChanges)
            { // (eksempel cola, so you don´t put it on the other one)

                int stopvalue = stateChanges.size();
                for (int i = 0; i < stopvalue; i++)
                {
                    state = stateChanges.get(i);
                    if (state == State.Disposed)
                    {

                        if (screen != null) screen.dispose();
                        Log.d("GameEngine", "Main Loop thread is disposed");
                        stateChanges.clear();
                        return;
                    }
                    if (state == State.Paused)
                    {
                        if (screen != null) screen.pause();
                        Log.d("GameEngine", "Main Loop thread is paused");
                        stateChanges.clear();
                        return;
                    }
                    if (state == State.Resumed)
                    {
                        if (screen != null) screen.resume();
                        Log.d("GameEngine", "Main Loop thread is Resumed");
                        state = State.Running;
                    }
                }
                stateChanges.clear();
            }
            //after the synchronized state check we can do the actualt work of the thread
            if (state == State.Running)
            {
                //it takes some time for android to connect to HW surface and..
                //getting the surfaceHolder, so we wait using it until we have one!
                if (!surfaceHolder.getSurface().isValid())
                {
                    continue;
                }
                Canvas canvas = surfaceHolder.lockCanvas();
                //ok now we can do all the drawing stuff
                //canvas.drawColor(Color.RED);
                fillEvents();
                currentTime = System.nanoTime();
                deltaTime = (currentTime - StartTime)/1000000000.0f;
                if (screen != null) screen.update(deltaTime);
                StartTime = currentTime;
                freeEvents();
                //after the screen has made all game object to the virtualScreen
                // we need to copy and resize the virtualScreen to the actual physical surfaceView
                src.left = 0;
                src.top = 0;
                src.right = virtualScreen.getWidth() - 1;
                src.bottom = virtualScreen.getHeight() - 1;
                dst.left = 0;
                dst.top = 0;
                dst.right = surfaceView.getWidth();
                dst.bottom = surfaceView.getHeight();
                canvas.drawBitmap(virtualScreen, src, dst, null);
                surfaceHolder.unlockCanvasAndPost(canvas);
            } // state running

//            frames++;
//            if ((System.nanoTime() - startTime) > 1000000000)
//            {
//                framesPerSecound = frames;
//                frames = 0;
//                startTime = System.nanoTime();
//                Log.d("MainLoop", "FramesPerSecond = " + framesPerSecound + " ************");
//            }
        } // end of the while loop
    } // end of run() method

    public int getFramerate()
    {
        return framesPerSecound;
    }

}
