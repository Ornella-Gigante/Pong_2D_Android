package es.myapplication;

import android.content.Context;
import android.content.res.Resources;
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

    /**
     * Sets the current state of the game.
     * This method is synchronized to ensure thread safety when updating the game state.
     * It uses a switch statement to handle different game states, such as ready or running.
     * @param state The new state of the game.
     */

    public void setState(int state){

        synchronized (mSurfaceHodler){
            mGameState = state;
            Resources res = mCtx.getResources();
            switch(mGameState){

                case STATE_READY:

                    // setUpNewRound();

                break;

                case STATE_RUNNING:

                    //hideStatus();

                break;

            }
        }
    }

    /**
     * Sets up a new round by initializing the Pong table.
     * This method is synchronized to ensure thread safety during setup.
     */

    public void setUpNewRound(){
        synchronized (mSurfaceHodler){
            mPongTable.setupTable();
        }
    }


    /**
     * Sets the running state of the game.
     * This method is synchronized to ensure thread safety when updating the game state.
     * @param running True if the game should be running, false otherwise.
     */
    public void setRunning(boolean running){
        synchronized (mRunLock){
            mRun = running;
        }
    }

    /**
     * Checks if the sensors are currently enabled.
     * @return True if sensors are on, false otherwise.
     */

    public boolean SensorsOn(){
        return mSensorsOn;
    }

    /**
     * Checks if the game is currently between rounds.
     * This method returns true if the game state is not running.
     * @return True if the game is between rounds, false otherwise.
     */
    private boolean isBetweenRounds(){
        return mGameState != STATE_RUNNING;
    }
}
