package es.myapplication;

import android.graphics.Paint;
import android.view.SurfaceHolder;

public class PongTable {

    private Player mPlayer;
    private Player mOpponent;
    private Ball mBall;
    private Paint mNetPaint;
    private Paint mTalbeBoundsPaint;
    private int mTableWidth;
    private int mTableHeight;

    SurfaceHolder holder;

    public static float PHY_RACQUET_SPEED = 15.0f;
    public static float PHY_BALL_SPEED = 15.0f;

    private float mAiMoveProbability;
    private boolean moving = false;
    private float mlastTouchy;

}
