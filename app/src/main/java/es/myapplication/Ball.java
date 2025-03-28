package es.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {

    public float cx;
    public float cy;
    public float velocity_x;
    public float velocity_y;
    public int radius;
    private Paint paint;

    /**
     * Constructor that initializes a new ball with the given radius and paint.
     * The ball's initial velocity is set to the physical ball speed defined in PongTable.
     * @param radius The radius of the ball
     * @param paint The paint used to draw the ball
     */

    public Ball(int radius, Paint paint){

        this.paint = paint;
        this.radius = radius;
        this.velocity_x = PongTable.PHY_BALL_SPEED;
        this.velocity_y = PongTable.PHY_BALL_SPEED;
    }


    /**
     * Draws the ball at its current position on the provided canvas.
     * @param canvas Canvas where the ball will be drawn
     */

    public void draw(Canvas canvas){

        canvas.drawCircle(cx,cy,radius,paint);
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Cx = " + cx + "Cy" + cy + "velX = " + velocity_x + "velY = " + velocity_y;
    }

    /**
     * Updates the position of the ball based on its current velocity.
     * Ensures the ball stays within the vertical bounds of the canvas.
     * @param canvas The canvas where the ball's new position will be drawn.
     */

    public void moveBall(Canvas canvas){

        cx += velocity_x;
        cy += velocity_y;

        if(cy < radius){
            cy = radius;


        }else if(cy + radius >= canvas.getHeight()){
            cy = canvas.getHeight() - radius - 1;
        }
    }


}
