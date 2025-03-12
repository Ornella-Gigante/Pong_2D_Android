package es.myapplication;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Player {

    private int racquetWidth;
    private int racquetHeight;
    private int score;
    private Paint paint;
    public RectF bounds;


    public Player( int racquetWidth, int racquetHeight, Paint paint){

        this.racquetWidth = racquetWidth;
        this.racquetHeight = racquetHeight;
        this.paint = paint;
        score =0;
        bounds = new RectF(0,0, racquetWidth, racquetHeight);
    }


    public int getRacquetWidth() {
        return racquetWidth;
    }

    public int getRacquetHeight() {
        return racquetHeight;
    }

    public void draw(Canvas canvas){
        canvas.drawRoundRect(bounds,5,5,paint);
    }

    @Override
    public String toString() {
        return "Width = " + racquetWidth + "Height = " + racquetHeight + "score = " + score + "Top = " + bounds.top + "Left = "  + bounds.left;
    }
}
