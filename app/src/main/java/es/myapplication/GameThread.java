package es.myapplication;

public class GameThread extends Thread{

    public static final int STATE_READY = 0;
    public static final int STATE_PAUSED = 1;
    public static final int STATE_RUNNING = 2;
    public static final int STATE_WIN = 3;
    public static final int STATE_LOSE = 4;




    @Override
    public void run(){
        super.run();
    }
}
