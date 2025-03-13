package es.myapplication;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongTable extends SurfaceView {

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



    public PongTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PongTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
