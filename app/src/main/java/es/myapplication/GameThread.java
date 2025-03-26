package es.myapplication;

import android.content.Context;
import android.view.SurfaceHolder;
import java.util.logging.Handler;

public class GameThread extends Thread{

    public static final int STATE_READY = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_WIN = 3;
    public static final int STATE_LOSE = 4;


    private boolean mSensorsOn;
    private Context mCtx;
    private final SurfaceHolder mSurfaceHodler;
    private final PongTable mPongTable;
    private final Handler mGameStatusHandler;
    private final Handler mScoreHandler;

    public GameThread(Context mCtx, SurfaceHolder mSurfaceHodler, PongTable mPongTable, Handler mGameStatusHandler, Handler mScoreHandler) {
        this.mSurfaceHodler = mSurfaceHodler;
        this.mPongTable = mPongTable;
        this.mGameStatusHandler = mGameStatusHandler;
        this.mScoreHandler = mScoreHandler;
        this.mCtx = mCtx;
    }


    @Override
    public void run(){
        super.run();
    }
}
