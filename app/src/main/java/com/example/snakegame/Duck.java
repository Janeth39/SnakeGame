package com.example.snakegame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;

public class Duck {
    private Bitmap bm, bm2, bm_duckL_left, bm_duckL_up, bm_duckL_right, bm_duckL_down, bm_duckF_left, bm_duckF_up, bm_duckF_right, bm_duckF_down;
    private ArrayList <PartDuck> arrPartDuck = new ArrayList<>();
    private int length;
    private boolean move_left, move_right, move_up, move_down;

    public Duck(Bitmap bm, Bitmap bm2, int x, int y, int length) {
        this.bm = bm;
        this.bm2 = bm2;
        this.length = length;
        //assigns the part of the image facing in different direction to a bitmap variable
        bm_duckL_left = Bitmap.createBitmap(bm, GameView.sizeElementMap-30,20, GameView.sizeElementMap+20,GameView.sizeElementMap+30);
        bm_duckL_up = Bitmap.createBitmap(bm,2*GameView.sizeElementMap+40,0, GameView.sizeElementMap,GameView.sizeElementMap+30);
        bm_duckL_right = Bitmap.createBitmap(bm,4*GameView.sizeElementMap+50,20, GameView.sizeElementMap,GameView.sizeElementMap+30);
        bm_duckL_down = Bitmap.createBitmap(bm,5*GameView.sizeElementMap+70,20, GameView.sizeElementMap,GameView.sizeElementMap+30);
        bm_duckF_left = Bitmap.createBitmap(bm2, GameView.sizeElementMap-30,10, GameView.sizeElementMap,GameView.sizeElementMap);
        bm_duckF_up = Bitmap.createBitmap(bm2,2*GameView.sizeElementMap+10,0, GameView.sizeElementMap,GameView.sizeElementMap);
        bm_duckF_right = Bitmap.createBitmap(bm2,3*GameView.sizeElementMap+60,10, GameView.sizeElementMap,GameView.sizeElementMap);
        bm_duckF_down = Bitmap.createBitmap(bm2,4*GameView.sizeElementMap,0, GameView.sizeElementMap,GameView.sizeElementMap);
        //sets up the duck chain at the beginning of the game
        setMove_right(true);
        arrPartDuck.add(new PartDuck(bm_duckL_right, x, y));
        for (int i = 1; i < length-1; i++)
        {
            this.arrPartDuck.add(new PartDuck(bm_duckF_right, this.arrPartDuck.get(i-1).getX()-GameView.sizeElementMap, y));
        }//end of for loop
        arrPartDuck.add(new PartDuck(bm_duckL_right, arrPartDuck.get(length-2).getX()-GameView.sizeElementMap, arrPartDuck.get(length-2).getY()));
    }//end of Duck

    //updates ducks movement
    public void update(){
        for(int i = length-1; i > 0; i--)
        {
            arrPartDuck.get(i).setX(arrPartDuck.get(i-1).getX());
            arrPartDuck.get(i).setY(arrPartDuck.get(i-1).getY());
        }//end of for loop
        if(move_right)
        {
            arrPartDuck.get(0).setX(arrPartDuck.get(0).getX()+GameView.sizeElementMap);
            arrPartDuck.get(0).setBm(bm_duckL_right);
        }//end of if statement
         else if(move_down)
         {
            arrPartDuck.get(0).setY(arrPartDuck.get(0).getY()+GameView.sizeElementMap);
            arrPartDuck.get(0).setBm(bm_duckL_down);
         }//end of first else if statement
         else if(move_up)
         {
            arrPartDuck.get(0).setY(arrPartDuck.get(0).getY()-GameView.sizeElementMap);
            arrPartDuck.get(0).setBm(bm_duckL_up);
         }//end of second else if statment
         else
         {
            arrPartDuck.get(0).setX(arrPartDuck.get(0).getX()-GameView.sizeElementMap);
            arrPartDuck.get(0).setBm(bm_duckL_left);
         }//end of else statement
        for (int i = 1; i < length - 1; i++)
        {
            if(arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i-1).getrBody())
                    || arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i-1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_left);
            }//end of if statement
            else if (arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i-1).getrBody())
                    || arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i-1).getrBody())
                    && arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i+1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_left);
            }//end of else if statement
            else if (arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i-1).getrBody())
                    || arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i-1).getrBody())
                    && arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i+1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_right);
            }//end of else if statement
            else if(arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i-1).getrBody())
                    || arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i-1).getrBody())
                    && arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i+1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_right);
            }//end of else if statement
            else if(arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i-1).getrBody())
                    && arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i+1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_left);
            }//end of else if statement
            else if(arrPartDuck.get(i).getrLeft().intersect(arrPartDuck.get(i+1).getrBody())
                    && arrPartDuck.get(i).getrRight().intersect(arrPartDuck.get(i-1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_right);
            }//end of else if
            else if(arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i-1).getrBody())
                    &&arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i+1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_up);
            }//end of else if
            else if(arrPartDuck.get(i).getrTop().intersect(arrPartDuck.get(i+1).getrBody())
                    &&arrPartDuck.get(i).getrBottom().intersect(arrPartDuck.get(i-1).getrBody()))
            {
                arrPartDuck.get(i).setBm(bm_duckF_down);
            }//end of else if statement
            else
            {
                if(move_right)
                {
                    arrPartDuck.get(i).setBm(bm_duckF_right);
                }//end of if statement
                else if(move_down)
                {
                    arrPartDuck.get(i).setBm(bm_duckF_down);
                }//end of else if statement
                else if(move_up)
                {
                    arrPartDuck.get(i).setBm(bm_duckF_up);
                }//end of else if statement
                else
                {
                    arrPartDuck.get(i).setBm(bm_duckF_left);
                }//end of inner else statement
            }//end of outer else statement
        }//end of for loop
        if(arrPartDuck.get(length-1).getrRight().intersect(arrPartDuck.get(length-2).getrBody()))
        {
            arrPartDuck.get(length-1).setBm(bm_duckL_right);
        }//end of if statement
        else if(arrPartDuck.get(length-1).getrLeft().intersect(arrPartDuck.get(length-2).getrBody()))
        {
            arrPartDuck.get(length-1).setBm(bm_duckL_left);
        }//end of else if statement
        else if(arrPartDuck.get(length-1).getrBottom().intersect(arrPartDuck.get(length-2).getrBody()))
        {
            arrPartDuck.get(length-1).setBm(bm_duckL_down);
        }//end of else if
        else
        {
            arrPartDuck.get(length-1).setBm(bm_duckL_up);
        }//end of else statement
    }//end of update

    public void drawSnake(Canvas canvas){
        for(int i = length-1; i >= 0; i--)
        {
            canvas.drawBitmap(arrPartDuck.get(i).getBm(), arrPartDuck.get(i).getX(), arrPartDuck.get(i).getY(), null);
        }//end of for loop
    }//end of drawSnake

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    public Bitmap getBm_duckL_left() {
        return bm_duckL_left;
    }

    public void setBm_duckL_left(Bitmap bm_duckL_left) {
        this.bm_duckL_left = bm_duckL_left;
    }

    public Bitmap getBm_duckL_up() {
        return bm_duckL_up;
    }

    public void setBm_duckL_up(Bitmap bm_duckL_up) {
        this.bm_duckL_up = bm_duckL_up;
    }

    public Bitmap getBm_duckL_right() {
        return bm_duckL_right;
    }

    public void setBm_duckL_right(Bitmap bm_duckL_right) {
        this.bm_duckL_right = bm_duckL_right;
    }

    public Bitmap getBm_duckL_down() {
        return bm_duckL_down;
    }

    public void setBm_duckL_down(Bitmap bm_duckL_down) {
        this.bm_duckL_down = bm_duckL_down;
    }

    public Bitmap getBm2() {
        return bm2;
    }

    public void setBm2(Bitmap bm2) {
        this.bm2 = bm2;
    }

    public Bitmap getBm_duckF_left() {
        return bm_duckF_left;
    }

    public void setBm_duckF_left(Bitmap bm_duckF_left) {
        this.bm_duckF_left = bm_duckF_left;
    }

    public Bitmap getBm_duckF_up() {
        return bm_duckF_up;
    }

    public void setBm_duckF_up(Bitmap bm_duckF_up) {
        this.bm_duckF_up = bm_duckF_up;
    }

    public Bitmap getBm_duckF_right() {
        return bm_duckF_right;
    }

    public void setBm_duckF_right(Bitmap bm_duckF_right) {
        this.bm_duckF_right = bm_duckF_right;
    }

    public Bitmap getBm_duckF_down() {
        return bm_duckF_down;
    }

    public void setBm_duckF_down(Bitmap bm_duckF_down) {
        this.bm_duckF_down = bm_duckF_down;
    }

    public ArrayList<PartDuck> getArrPartDuck() {
        return arrPartDuck;
    }

    public void setArrPartSnake(ArrayList<PartDuck> arrPartDuck) {
        this.arrPartDuck = arrPartDuck;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isMove_left() {
        return move_left;
    }

    public void setMove_left(boolean move_left) {
        this.setup();
        this.move_left = move_left;
    }

    public boolean isMove_right() {
        return move_right;
    }

    public void setMove_right(boolean move_right) {
        this.setup();
        this.move_right = move_right;
    }

    public boolean isMove_up() {
        return move_up;
    }

    public void setMove_up(boolean move_up) {
        this.setup();
        this.move_up = move_up;
    }

    public boolean isMove_down() {
        return move_down;
    }

    public void setMove_down(boolean move_down) {
        this.setup();
        this.move_down = move_down;
    }

    public void setup(){
        this.move_right = false;
        this.move_down = false;
        this.move_left = false;
        this.move_up = false;
    }

    //adds another duck when it comes in contact with the food image
    public void addPart() {
        PartDuck p = this.arrPartDuck.get(length-1);
        this.length += 1;
        if(p.getBm()==bm_duckL_right)
        {
            this.arrPartDuck.add(new PartDuck(bm_duckL_right, p.getX()-GameView.sizeElementMap, p.getY()));
        }//end of if statement
        else if(p.getBm()==bm_duckL_left)
        {
            this.arrPartDuck.add(new PartDuck(bm_duckL_left, p.getX()+GameView.sizeElementMap, p.getY()));
        }//end of else if statement
        else if(p.getBm()==bm_duckL_up)
        {
            this.arrPartDuck.add(new PartDuck(bm_duckL_up, p.getX(), p.getY()+GameView.sizeElementMap));
        }//end of else if statement
        else if(p.getBm()==bm_duckL_down)
        {
            this.arrPartDuck.add(new PartDuck(bm_duckL_up, p.getX(), p.getY()-GameView.sizeElementMap));
        }//end of else if statement
    }//end of addPart
}//end of Duck
