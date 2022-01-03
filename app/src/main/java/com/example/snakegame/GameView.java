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
    public static int sizeElementMap = 75*Constants.SCREEN_WIDTH/1080;
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
        SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
        if(sp!=null){
            bestScore = sp.getInt("bestscore",0);
        }
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
        for(int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                if((j+i)%2==0){
                    arrLake.add(new Lake(bmGrass1, j*bmGrass1.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }else{
                    arrLake.add(new Lake(bmGrass2, j*bmGrass2.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                }
            }
        }
        duck = new Duck(bmDuck1, bmDuck2,arrLake.get(126).getX(),arrLake.get(126).getY(), 4);
        food = new Food(bmFood, arrLake.get(randomApple()[0]).getX(), arrLake.get(randomApple()[1]).getY());
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        if(Build.VERSION.SDK_INT>=21){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        }else{
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loadedsound = true;
            }
        });
        soundEat = this.soundPool.load(context, R.raw.eat, 1);
        soundDie = this.soundPool.load(context, R.raw.die, 1);
    }

    private int[] randomApple(){
        int []xy = new int[2];
        Random r = new Random();
        xy[0] = r.nextInt(arrLake.size()-1);
        xy[1] = r.nextInt(arrLake.size()-1);
        Rect rect = new Rect(arrLake.get(xy[0]).getX(), arrLake.get(xy[1]).getY(), arrLake.get(xy[0]).getX()+sizeElementMap, arrLake.get(xy[1]).getY()+sizeElementMap);
        boolean check = true;
        while (check){
            check = false;
            for (int i = 0; i < duck.getArrPartDuck().size(); i++){
                if(rect.intersect(duck.getArrPartDuck().get(i).getrBody())){
                    check = true;
                    xy[0] = r.nextInt(arrLake.size()-1);
                    xy[1] = r.nextInt(arrLake.size()-1);
                    rect = new Rect(arrLake.get(xy[0]).getX(), arrLake.get(xy[1]).getY(), arrLake.get(xy[0]).getX()+sizeElementMap, arrLake.get(xy[1]).getY()+sizeElementMap);
                }
            }
        }
        return xy;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int a = event.getActionMasked();
        switch (a){
            case  MotionEvent.ACTION_MOVE:{
                if(move==false){
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                }else{
                    if(mx - event.getX() > 100 && !duck.isMove_right()){
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_left(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(event.getX() - mx > 100 &&!duck.isMove_left()){
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_right(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(event.getY() - my > 100 && !duck.isMove_up()){
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_down(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }else if(my - event.getY() > 100 && !duck.isMove_down()){
                        mx = event.getX();
                        my = event.getY();
                        this.duck.setMove_up(true);
                        isPlaying = true;
                        MainActivity.img_swipe.setVisibility(INVISIBLE);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                mx = 0;
                my = 0;
                move = false;
                break;
            }
        }
        return true;
    }

    public void draw(Canvas canvas){
        super.draw(canvas);
        canvas.drawColor(0xFF03A9F4);
        for(int i = 0; i < arrLake.size(); i++){
            canvas.drawBitmap(arrLake.get(i).getBm(), arrLake.get(i).getX(), arrLake.get(i).getY(), null);
        }
        if(isPlaying){
            duck.update();
            if(duck.getArrPartDuck().get(0).getX() < this.arrLake.get(0).getX()
                    ||duck.getArrPartDuck().get(0).getY() < this.arrLake.get(0).getY()
                    ||duck.getArrPartDuck().get(0).getY()+sizeElementMap>this.arrLake.get(this.arrLake.size()-1).getY() + sizeElementMap
                    ||duck.getArrPartDuck().get(0).getX()+sizeElementMap>this.arrLake.get(this.arrLake.size()-1).getX() + sizeElementMap){
                gameOver();
            }
            for (int i = 1; i < duck.getArrPartDuck().size(); i++){
                if (duck.getArrPartDuck().get(0).getrBody().intersect(duck.getArrPartDuck().get(i).getrBody())){
                    gameOver();
                }
            }
        }
        duck.drawSnake(canvas);
        food.draw(canvas);
        if(duck.getArrPartDuck().get(0).getrBody().intersect(food.getR())){
            if(loadedsound){
                int streamId = this.soundPool.play(this.soundEat, (float)0.5, (float)0.5, 1, 0, 1f);
            }
            food.reset(arrLake.get(randomApple()[0]).getX(), arrLake.get(randomApple()[1]).getY());
            duck.addPart();
            score++;
            MainActivity.txt_score.setText(score+"");
            if(score > bestScore){
                bestScore = score;
                SharedPreferences sp = context.getSharedPreferences("gamesetting", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("bestscore", bestScore);
                editor.apply();
                MainActivity.txt_best_score.setText(bestScore+"");
            }
        }
        handler.postDelayed(r, 100);
    }

    private void gameOver() {
        isPlaying = false;
        MainActivity.dialogScore.show();
        MainActivity.txt_dialog_best_score.setText(bestScore+"");
        MainActivity.txt_dialog_score.setText(score+"");
        if(loadedsound){
            int streamId = this.soundPool.play(this.soundDie, (float)0.5, (float)0.5, 1, 0, 1f);
        }
    }

    public void reset(){
        for(int i = 0; i < h; i++){
            for (int j = 0; j < w; j++){
                if((j+i)%2==0){
                    arrLake.add(new Lake(bmGrass1, j*bmGrass1.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass1.getWidth(), i*bmGrass1.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass1.getWidth(), bmGrass1.getHeight()));
                }else{
                    arrLake.add(new Lake(bmGrass2, j*bmGrass2.getWidth() + Constants.SCREEN_WIDTH/2 - (w/2)*bmGrass2.getWidth(), i*bmGrass2.getHeight()+50*Constants.SCREEN_HEIGHT/1920, bmGrass2.getWidth(), bmGrass2.getHeight()));
                }
            }
        }
        duck = new Duck(bmDuck1, bmDuck2,arrLake.get(126).getX(),arrLake.get(126).getY(), 4);
        food = new Food(bmFood, arrLake.get(randomApple()[0]).getX(), arrLake.get(randomApple()[1]).getY());
        score = 0;
    }
}
