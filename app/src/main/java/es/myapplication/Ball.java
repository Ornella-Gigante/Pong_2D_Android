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

    /**
     * Constructor that initializes a new ball with the given coordinates and velocities.
     * @param cx x-coordinate of the center of the ball
     * @param cy y-coordinate of the center of the ball
     * @param velocity_x Velocity in the x direction
     * @param velocity_y Velocity in the y direction
     */

    public Ball(float cx, float cy, float velocity_x, float velocity_y){

        this.cx = cx;
        this.cy = cy;
        this.velocity_x = velocity_x;
        this.velocity_y = velocity_y;
    }


    /**
     * Updates and draws the position of the ball on the canvas.
     * However, in this code, the ball is not actually being drawn.
     * @param canvas Canvas where the ball will be drawn
     */

    public void draw(Canvas canvas){

        cx += velocity_x;
        cy += velocity_y;


        // Limits the vertical position of the ball so it does not go off the screen

        if(cy < radius){
            cy = radius;
        }else if(cy + radius >= canvas.getHeight()){
            cy = canvas.getHeight() - radius - 1;
        }
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Cx = " + cx + "Cy" + cy + "velX = " + velocity_x + "velY = " + velocity_y;
    }



}
