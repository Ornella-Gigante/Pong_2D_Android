package es.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    public float cx;
    public float cy;
    public float velocity_x;
    public float velocity_y;

    private int radius;
    private Paint paint;


    public Ball(float cx, float cy, float velocity_x, float velocity_y){

        this.cx = cx;
        this.cy = cy;
        this.velocity_x = velocity_x;
        this.velocity_y = velocity_y;
    }


    public void draw(Canvas canvas){

        cx += velocity_x;
        cy += velocity_y;


        if(cy < radius){
            cy = radius;
        }else if(cy + radius >= canvas.getHeight()){
            cy = canvas.getHeight() - radius - 1;
        }
    }

    public int getRadius() {
        return radius;
    }



}
