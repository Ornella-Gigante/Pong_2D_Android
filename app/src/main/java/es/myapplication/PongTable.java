package es.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.logging.LogRecord;

/**
 * Class representing the Pong table, extending SurfaceView to display graphical content.
 * Implements SurfaceHolder.Callback to handle surface-related events.
 */
public class PongTable extends SurfaceView implements  SurfaceHolder.Callback{

    private GameThread mGame;
    private TextView mStatus;
    private TextView mScorePlayer;
    private TextView mScoreOpponent;

    // Players and ball in the game
    private Player mPlayer;
    private Player mOpponent;
    private Ball mBall;
    // Paints for drawing the net and table boundaries
    private Paint mNetPaint;
    private Paint mTalbeBoundsPaint;
    // Table dimensions
    private int mTableWidth;
    private int mTableHeight;
    // Application context
    private Context mContext;
    // SurfaceHolder to manage the surface
    SurfaceHolder mHolder;
    // Physical speed constants for rackets and ball
    public static float PHY_RACQUET_SPEED = 15.0f;
    public static float PHY_BALL_SPEED = 15.0f;
    // Probability of AI opponent movement
    private float mAiMoveProbability;
    // Movement state
    private boolean moving = false;
    // Last touched position
    private float mlastTouchy;



    /**
     * Initialization method for the Pong table.
     * Sets up the application context, SurfaceHolder, and retrieves layout attributes for racket dimensions.
     * @param ctx Application context
     * @param attr Layout attributes
     */

    public void initPongTable(Context ctx, AttributeSet attr){


        mContext = ctx;
        mHolder = getHolder();
        mHolder.addCallback(this);


        // Initialize the game thread with necessary handlers for game status and score updates
        mGame = new GameThread(this.getContext(), mHolder, this, new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (mStatus != null) {
                    mStatus.setVisibility(msg.getData().getInt("visibility"));
                    mStatus.setText(msg.getData().getString("text"));
                }
            }

        }, new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (mScorePlayer != null && mScoreOpponent != null) {
                    mScorePlayer.setText(msg.getData().getString("player"));
                    mScoreOpponent.setText(msg.getData().getString("opponent"));
                }
            }

        });


        TypedArray a = ctx.obtainStyledAttributes(attr,R.styleable.PongTable);
        int racketHeight = a.getInteger(R.styleable.PongTable_racketHeight, 340);
        int racketWidth = a.getInteger(R.styleable.PongTable_racketWight,100);
        int ballRadius = a.getInteger(R.styleable.PongTable_ballRadius, 20);


        //Set Player

        Paint playerPaint = new Paint();
        playerPaint.setAntiAlias(true);
        playerPaint.setColor(ContextCompat.getColor(mContext,R.color.player_color));
        mPlayer = new Player(racketWidth,racketHeight,playerPaint);


        // Set Opponent

        Paint opponentPaint = new Paint();
        opponentPaint.setAntiAlias(true);
        opponentPaint.setColor(ContextCompat.getColor(mContext,R.color.opponent_color));
        mOpponent = new Player(racketWidth, racketHeight, opponentPaint);

        // Set Ball

        Paint ballPaint = new Paint();
        ballPaint.setAntiAlias(true);
        ballPaint.setColor(ContextCompat.getColor(mContext,R.color.ball_color));
        mBall = new Ball( ballRadius, ballPaint);


        // Drawing of middle lines

        mNetPaint = new Paint();
        mNetPaint.setAntiAlias(true);
        mNetPaint.setColor(Color.WHITE);
        mNetPaint.setAlpha(80);
        mNetPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mNetPaint.setStrokeWidth(10.0f);
        mNetPaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));


        // Drawing of Bounds

        mTalbeBoundsPaint = new Paint();
        mTalbeBoundsPaint.setAntiAlias(true);
        mTalbeBoundsPaint.setColor(ContextCompat.getColor(mContext,R.color.table_color));
        mTalbeBoundsPaint.setStyle(Paint.Style.STROKE);
        mTalbeBoundsPaint.setStrokeWidth(15.0f);


        mAiMoveProbability = 0.86f;


        // Recycle to avoid memory leaks!
        a.recycle();



    }

    /**
     * Constructor that initializes the view with layout attributes.
     * @param context Application context
     * @param attrs Layout attributes
     */
    public PongTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPongTable(context,attrs);
    }


    /**
     * Constructor that initializes the view with layout attributes and a default style.
     * @param context Application context
     * @param attrs Layout attributes
     * @param defStyleAttr Default style
     */

    public PongTable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPongTable(context, attrs);
    }

    /**
     * This is called immediately after the surface is first created.
     * Implementations of this should start up whatever rendering code
     * they desire.  Note that only one thread can ever draw into
     * a {@link Surface}, so you should not draw into the Surface here
     * if your normal rendering will be in another thread.
     *
     * @param holder The SurfaceHolder whose surface is being created.
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        mGame.setRunning(true);
        mGame.start();

    }

    /**
     * Handles changes to the surface, updating the table dimensions and resetting the game state.
     * This method is called when the surface's size or format changes.
     * @param holder The SurfaceHolder that has changed.
     * @param format The new pixel format of the surface.
     * @param width The new width of the surface.
     * @param height The new height of the surface.
     */

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        mTableHeight = height;
        mTableWidth =width;
        mGame.setUpNewRound();
    }

    /**
     * Handles the destruction of the surface, stopping the game thread and waiting for it to finish.
     * This method is called when the surface is about to be destroyed.
     * @param holder The SurfaceHolder that is being destroyed.
     */

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        boolean retry = true;
        mGame.setRunning(false);
        while(retry){
            try{
                mGame.join();
                retry = false;
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }


    }

    /**
     * Draws the view's content on the provided canvas.
     * @param canvas Canvas where the content will be drawn
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Draw the table background with a specific color
        canvas.drawColor(ContextCompat.getColor(mContext,R.color.table_color));
        // Draw the table boundaries
        canvas.drawRect(0,0,mTableWidth,mTableHeight,mTalbeBoundsPaint);
        // Calculate the middle point of the table to draw the net
        int middle = mTableWidth/2;
        // Draw the net at the middle point of the table
        canvas.drawLine(middle,1,middle,mTableHeight-1,mNetPaint);

        // Draw the players and ball on the table
        mPlayer.draw(canvas);
        mOpponent.draw(canvas);
        mBall.draw(canvas);

    }

    /**
     * Method that handles AI logic for the opponent player.
     * It moves the opponent's racket to track the ball's vertical position.
     */
    private void doAi(){

        if(mOpponent.bounds.top > mBall.cy){
            movePlayer(mOpponent, mOpponent.bounds.left,
                    mOpponent.bounds.top - PHY_RACQUET_SPEED);
        }else if(mOpponent.bounds.top + mOpponent.getRacquetHeight() < mBall.cy){
            movePlayer(mOpponent, mOpponent.bounds.left, mOpponent.bounds.top + PHY_RACQUET_SPEED);
        }

    }

    /**
     * Handles touch events on the PongTable.
     * If sensors are not enabled, it handles touch events for moving the player's racket.
     * If the game is between rounds, a touch event starts the game.
     * @param event The MotionEvent representing the touch input.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!mGame.SensorsOn()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mGame.isBetweenRounds()) {
                        mGame.setState(GameThread.STATE_RUNNING);
                    } else {
                        if (isTouchRacket(event, mPlayer)) {
                            moving = true;
                            mlastTouchy = event.getY();
                        }
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (moving) {
                        float y = event.getY();
                        float dy = y - mlastTouchy;
                        mlastTouchy = y;
                        movePlayerRacket(dy, mPlayer);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    moving = false;
                    break;

                default:
                    break; // Agregado un default para manejar acciones no especificadas
            }
        } else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (mGame.isBetweenRounds()) {
                    mGame.setState(GameThread.STATE_RUNNING);
                }
            }
        }

        return true;
    }


    /**
     * Checks if a touch event occurred within the bounds of the player's racket.
     * @param event The MotionEvent representing the touch input.
     * @param mPlayer The player whose racket is being checked.
     * @return True if the touch is within the racket's bounds, false otherwise.
     */

    private boolean isTouchRacket(MotionEvent event,Player mPlayer){

        return mPlayer.bounds.contains(event.getX(),event.getY());
    }


    /**
     * Returns the GameThread instance associated with this PongTable.
     * @return The GameThread instance.
     */

    public GameThread getGame(){
        return mGame;
    }

    /**
     * Moves the player's racket vertically by a specified distance.
     * This method synchronizes the movement to ensure thread safety.
     * @param dy The distance to move the racket vertically.
     * @param player The player whose racket is being moved.
     */
    public void movePlayerRacket(float dy, Player player){

        synchronized (mHolder){
            movePlayer(player,player.bounds.left, player.bounds.top + dy);
        }
    }

    /**
     * Moves the player's position to a specified location on the table while ensuring they stay within the table boundaries.
     * This method is synchronized to ensure thread-safe updates to the player's position.
     * @param player The player whose position is being updated.
     * @param left The new x-coordinate of the player's position.
     * @param top The new y-coordinate of the player's position.
     */

    public synchronized void movePlayer(Player player, float left, float top){

        if(left < 2){
            left = 2;

        }else if(left + player.getRacquetWidth() >= mTableWidth - 2){
            left = mTableWidth - player.getRacquetWidth() -2;

        }else if(top + player.getRacquetHeight()>= mTableHeight - 1){
            top = mTableHeight - player.getRacquetHeight()-1;

    }

        // Move the player's bounds to the specified position (left, top)

        player.bounds.offsetTo(left,top);

}

    /**
     * Configures the table by placing the ball and players at their initial positions.
     * This method ensures that the game starts with the ball at the center and players at the sides.
     */

    public void setupTable(){
        placeBall();
        placePlayers();
    }

    /**
     * Places the players at their initial positions on the table.
     * The player is positioned at the left side, and the opponent is positioned at the right side,
     * both centered vertically.
     */
    private void placePlayers(){
        mPlayer.bounds.offsetTo(2,(mTableHeight-mPlayer.getRacquetHeight()/2));
        mOpponent.bounds.offsetTo(mTableWidth-mOpponent.getRacquetWidth()-2,
                (mTableHeight - mOpponent.getRacquetHeight())/2);
    }


    /**
     * Places the ball at the center of the table and initializes its velocity.
     * The ball's velocity is set to a consistent speed in both the x and y directions.
     */
    private void placeBall(){
        mBall.cx = mTableWidth/2;
        mBall.cy = mTableHeight/2;
        mBall.velocity_y = (mBall.velocity_y / Math.abs(mBall.velocity_y) * PHY_BALL_SPEED );
        mBall.velocity_x = (mBall.velocity_x / Math.abs(mBall.velocity_x) * PHY_BALL_SPEED );
    }

    /**
     * Updates the game state and prepares the canvas for rendering.
     * This method should be called repeatedly to update the positions of game elements.
     * It currently handles AI logic for the opponent and moves the ball.
     * @param canvas The Canvas where the updated game state will be drawn.
     */


    public void update(Canvas canvas){

        if(new Random(System.currentTimeMillis()).nextFloat() < mAiMoveProbability)doAi();

        mBall.moveBall(canvas);


    }

}
