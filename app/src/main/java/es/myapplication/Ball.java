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



}
