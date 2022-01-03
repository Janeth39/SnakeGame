package com.example.snakegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.snakegame.Food;
import com.example.snakegame.Constants;
import com.example.snakegame.Lake;
import com.example.snakegame.Duck;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {
    private Bitmap bmGrass1, bmGrass2, bmDuck1, bmDuck2,bmFood;
    private ArrayList<Lake> arrLake = new ArrayList<>();
    private int w = 12, h=21;
    public static int sizeElementMap = 75*Constants.screenWidth/1080;
    private Duck duck;
    private Food food;
    private Handler handler;
    private Runnable r;
    private boolean move = false;
    private float mx, my;
    public static boolean isPlaying = false;
    public static int score = 0, bestScore = 0;
    private Context context;
    private int soundEat, soundDie;
    private float volume;
    private boolean loadedsound;
    private SoundPool soundPool;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //savves the best score
        SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
        if(sp!=null)
        {
            bestScore = sp.getInt("bestscore",0);
        }//end of if statement
        //assigns size and image to Bitmaps
        bmGrass1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass);
        bmGrass1 = Bitmap.createScaledBitmap(bmGrass1, sizeElementMap, sizeElementMap, true);
        bmGrass2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.grass03);
        bmGrass2 = Bitmap.createScaledBitmap(bmGrass2, sizeElementMap, sizeElementMap, true);
        bmDuck1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.duck_l);
        bmDuck1 = Bitmap.createScaledBitmap(bmDuck1, 14*sizeElementMap, 7*sizeElementMap, true);
        bmDuck2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.duck_f);
        bmDuck2 = Bitmap.createScaledBitmap(bmDuck2, 14*sizeElementMap, 7*sizeElementMap, true);
        bmFood = BitmapFactory.decodeResource(this.getResources(), R.drawable.food);
        bmFood = Bitmap.createScaledBitmap(bmFood, sizeElementMap, sizeElementMap, true);
        //creates the grid like pattern in the app
        for(int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                if((j+i)%2==0)
                {
                    arrLake.add(new Lake(bmGrass1, j*bmGrass1.getWidth() + Constants.screenWidth/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.screenHeight/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }//end of if statement
                else
                    {
                        arrLake.add(new Lake(bmGrass2, j*bmGrass2.getWidth() + Constants.screenWidth/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.screenHeight/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                    }//end of else statement
            }//end of inner for loop
        }//end of outer for loop
        duck = new Duck(bmDuck1, bmDuck2,arrLake.get(126).getX(),arrLake.get(126).getY(), 4);
        food = new Food(bmFood, arrLake.get(randomDuck()[0]).getX(), arrLake.get(randomDuck()[1]).getY());
        handler = new Handler();
        r = new Runnable()
        {
            @Override
            public void run()
            {
                invalidate();
            }//end of run
        };
        //in charge of the sounds
            if(Build.VERSION.SDK_INT>=21)
            {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
                SoundPool.Builder builder = new SoundPool.Builder();
                builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
                this.soundPool = builder.build();
            }//end of if statement
            else
            {
                soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            }//end of else
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status)
            {
                loadedsound = true;
            }//end of onLoadComplete
        });
        soundEat = this.soundPool.load(context, R.raw.eat, 1);
        soundDie = this.soundPool.load(context, R.raw.die, 1);
    }//end of GameView

    //randomizes the place where the food is located
    private int[] randomDuck(){
        int []xy = new int[2];
        Random r = new Random();
        xy[0] = r.nextInt(arrLake.size()-1);
        xy[1] = r.nextInt(arrLake.size()-1);
        Rect rect = new Rect(arrLake.get(xy[0]).getX(), arrLake.get(xy[1]).getY(), arrLake.get(xy[0]).getX()+sizeElementMap, arrLake.get(xy[1]).getY()+sizeElementMap);
        boolean check = true;
        while (check)
        {
            check = false;
            for (int i = 0; i < duck.getArrPartDuck().size(); i++)
            {
                if(rect.intersect(duck.getArrPartDuck().get(i).getrBody()))
                {
                    check = true;
                    xy[0] = r.nextInt(arrLake.size()-1);
                    xy[1] = r.nextInt(arrLake.size()-1);
                    rect = new Rect(arrLake.get(xy[0]).getX(), arrLake.get(xy[1]).getY(), arrLake.get(xy[0]).getX()+sizeElementMap, arrLake.get(xy[1]).getY()+sizeElementMap);
                }//end of if statement
            }//end of for loop
        }//end of while
        return xy;
    }//end of randomDuck

    //allows user to move the ducks
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getActionMasked();
        switch (a)
        {
            case  MotionEvent.ACTION_MOVE:
                {
                    if(!move)
                    {
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                    }//end of if statement
                    else if(mx - event.getX() > 100 && !duck.isMove_right())
                    {
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_left(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    } // end of first else if statement
                    else if(event.getX() - mx > 100 &&!duck.isMove_left())
                    {
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_right(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }//end of second else if statement
                    else if(event.getY() - my > 100 && !duck.isMove_up())
                    {
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_down(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }//end of third else if statement
                    else if(my - event.getY() > 100 && !duck.isMove_down())
                    {
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_up(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }//end of fourth else if statement
                break;
            }//end of case
            case MotionEvent.ACTION_UP:
                {
                    mx = 0;
                    my = 0;
                    move = false;
                    break;
                }//end of case
        }//end of switch
        return true;
    }//end of onTouchEvent

    //draws the game on the screen
    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(0xFF03A9F4);
        for(int i = 0; i < arrLake.size(); i++)
        {
            canvas.drawBitmap(arrLake.get(i).getBm(), arrLake.get(i).getX(), arrLake.get(i).getY(), null);
        }//end of for loop
        if(isPlaying)
        {
            duck.update();
            if(duck.getArrPartDuck().get(0).getX() < this.arrLake.get(0).getX()
                    ||duck.getArrPartDuck().get(0).getY() < this.arrLake.get(0).getY()
                    ||duck.getArrPartDuck().get(0).getY()+sizeElementMap>this.arrLake.get(this.arrLake.size()-1).getY() + sizeElementMap
                    ||duck.getArrPartDuck().get(0).getX()+sizeElementMap>this.arrLake.get(this.arrLake.size()-1).getX() + sizeElementMap)
            {
                gameOver();
            }//end of inner if statement
            for (int i = 1; i < duck.getArrPartDuck().size(); i++)
            {
                if (duck.getArrPartDuck().get(0).getrBody().intersect(duck.getArrPartDuck().get(i).getrBody()))
                {
                    gameOver();
                }//end of inner if statement
            }//end of for loop
        }// end of outer if statement
        duck.drawSnake(canvas);
        food.draw(canvas);
        if(duck.getArrPartDuck().get(0).getrBody().intersect(food.getR()))
        {
            if(loadedsound)
            {
                int streamId = this.soundPool.play(this.soundEat, (float)0.5, (float)0.5, 1, 0, 1f);
            }//end of first inner if statement
            food.reset(arrLake.get(randomDuck()[0]).getX(), arrLake.get(randomDuck()[1]).getY());
            duck.addPart();
            score++;
            MainActivity.txt_score.setText(score+"");
            if(score > bestScore)
            {
                bestScore = score;
                SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("bestscore", bestScore);
                editor.apply();
                MainActivity.txt_best_score.setText(bestScore+"");
            }//end of second inner statement
        }//end of outer if statement
        handler.postDelayed(r, 100);
    }//end of draw

    //Ends the game
    private void gameOver() {
        isPlaying = false;
        MainActivity.dialogScore.show();
        MainActivity.txt_dialog_best_score.setText(bestScore+"");
        MainActivity.txt_dialog_score.setText(score+"");
        if(loadedsound)
        {
            int streamId = this.soundPool.play(this.soundDie, (float)0.5, (float)0.5, 1, 0, 1f);
        }//end of if statement
    }//end of gameOver

    //resets the set up of the game in case the user would like to play again
    public void reset() {
        for(int i = 0; i < h; i++)
        {
            for (int j = 0; j < w; j++)
            {
                if((j+i)%2==0)
                {
                    arrLake.add(new Lake(bmGrass1, j*bmGrass1.getWidth() + Constants.screenWidth/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.screenHeight/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }
                else
                    {
                    arrLake.add(new Lake(bmGrass2, j*bmGrass2.getWidth() + Constants.screenWidth/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.screenHeight/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                }//end of else statement
            }//end of inner for loop
        }//en of outer for loop
        duck = new Duck(bmDuck1, bmDuck2,arrLake.get(126).getX(),arrLake.get(126).getY(), 4);
        food = new Food(bmFood, arrLake.get(randomDuck()[0]).getX(), arrLake.get(randomDuck()[1]).getY());
        score = 0;
    }//end of reset
}//end of GameView
