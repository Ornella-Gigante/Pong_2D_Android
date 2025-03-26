package es.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
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
    private boolean mRun = false;
    private int mGameState;
    private Object mRunLock;
    private static final int PHYS_FPS = 60;


    public GameThread(Context mCtx, SurfaceHolder mSurfaceHodler, PongTable mPongTable, Handler mGameStatusHandler, Handler mScoreHandler) {
        this.mSurfaceHodler = mSurfaceHodler;
        this.mPongTable = mPongTable;
        this.mGameStatusHandler = mGameStatusHandler;
        this.mScoreHandler = mScoreHandler;
        this.mCtx = mCtx;
        mRunLock = new Object();
    }


    /**
     * Main loop of the game thread, responsible for updating and rendering the game state.
     * This method runs continuously while the game is active, handling game logic and drawing on the canvas.
     */

    @Override
    public void run(){

        long mNextGameTick = SystemClock.uptimeMillis();
        int skipTicks = 1000/ PHYS_FPS;

        while(mRun){

            Canvas c = null;
            try{

                c = mSurfaceHodler.lockCanvas(null);
                if(c != null){
                    synchronized (mScoreHandler){
                        if(mGameState == STATE_RUNNING){
                            mPongTable.update(c);
                        }
                        synchronized (mRunLock){
                            if(mRun){
                                mPongTable.draw(c);
                            }
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(c != null){
                    mSurfaceHodler.unlockCanvasAndPost(c);
                }
            }

            mNextGameTick += skipTicks;
            long sleepTime = mNextGameTick - SystemClock.uptimeMillis();
            if(sleepTime > 0 ){
                try{
                    Thread.sleep(sleepTime);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }


    public void setState(int state){

    }
}
