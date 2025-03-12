package es.myapplication;

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

    public void setRacquetWidth(int racquetWidth) {
        this.racquetWidth = racquetWidth;
    }


    public int getRacquetHeight() {
        return racquetHeight;
    }

    public void setRacquetHeight(int racquetHeight) {
        this.racquetHeight = racquetHeight;
    }

    public RectF getBounds() {
        return bounds;
    }

    public void setBounds(RectF bounds) {
        this.bounds = bounds;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }



}
