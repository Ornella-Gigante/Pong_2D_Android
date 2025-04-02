package es.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
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

        // Update of the values
        mGame.setScoreText(String.valueOf(mPlayer.score), String.valueOf(mOpponent.score));

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
     * Checks for a collision between a player's paddle and the ball.
     * This method determines whether the ball intersects with the rectangular bounds
     * of the specified player's paddle. It calculates the bounding box of the ball
     * using its center coordinates and radius, and checks for intersection with
     * the paddle's bounds.
     * @param player The {@link Player} whose paddle is being checked for collision.
     * @param ball The {@link Ball} whose position is being checked against the paddle.
     * @return True if the ball intersects with the player's paddle, false otherwise.
     */


    private boolean checkCollisionPlayer(Player player, Ball ball){

        return player.bounds.intersects(

                ball.cx - ball.getRadius(),
                ball.cy - ball.getRadius(),
                ball.cx + ball.getRadius(),
                ball.cy + ball.getRadius()

        );
    }


    /**
     * Checks if the ball has collided with the top or bottom wall of the game table.
     *
     * This method determines whether the ball's position is outside the vertical
     * bounds of the table by comparing its center coordinates and radius against
     * the table's height. A collision occurs if the ball's top edge is at or above
     * the top boundary, or if its bottom edge is at or below the bottom boundary.
     *
     * @return True if the ball has collided with either the top or bottom wall, false otherwise.
     */

    private boolean checkCollisionWithTopOrBottomWall(){

        return ((mBall.cy <= mBall.getRadius()) || (mBall.cy + mBall.getRadius() >= mTableHeight -1));
    }


    /**
     * Checks if the ball has collided with the left wall of the game table.
     * This method determines whether the ball's position is outside the left boundary
     * of the table by comparing its center x-coordinate and radius. A collision occurs
     * if the ball's left edge is at or beyond the left boundary.
     * @return True if the ball has collided with the left wall, false otherwise.
     */

    private boolean checkCollisionWithLeftWall(){

        return mBall.cx <= mBall.getRadius();
      }


    /**
     * Checks if the ball has collided with the right wall of the game table.
     * This method determines whether the ball's position is outside the right boundary
     * of the table by comparing its center x-coordinate and radius against the table's width.
     * A collision occurs if the ball's right edge is at or beyond the right boundary.
     * @return True if the ball has collided with the right wall, false otherwise.
     */
      private boolean checkCollisionWithRightWall(){

        return mBall.cx + mBall.getRadius() >= mTableWidth - 1;
      }


    /**
     * Handles the collision between a player's paddle and the ball.
     * This method updates the ball's velocity and position based on the collision
     * with the specified player's paddle. The ball's horizontal velocity is reversed
     * and slightly increased to add difficulty over time. Additionally, the ball's
     * position is adjusted to prevent it from overlapping with the paddle.
     * If the collision is with the player's paddle, the ball is positioned just outside
     * the right edge of the paddle. If the collision is with the opponent's paddle,
     * the ball is positioned just outside the left edge of the paddle, and the opponent's
     * paddle speed is slightly increased.
     *
     * @param player The {@link Player} whose paddle collided with the ball.
     * @param ball The {@link Ball} involved in the collision.
     */
    
      private void handleCollision(Player player, Ball ball){

          ball.velocity_x = -ball.velocity_x * 1.05f;
          if(player == mPlayer){

              ball.cx = mPlayer.bounds.right + ball.getRadius();

          }else if(player == mOpponent){

              ball.cx = mOpponent.bounds.left - ball.getRadius();
              PHY_RACQUET_SPEED = PHY_RACQUET_SPEED * 1.05f;
          }
      }

    /**
     * Determines whether a touch event occurred within the racquet's bounds.
     * This method checks if the coordinates of a touch event fall within an
     * expanded area around the player's racquet. The detection area is extended
     * by 20 pixels in all directions to improve usability on touch devices.
     * @param event The MotionEvent representing the touch input.
     * @return True if the touch event occurred within the racquet's detection area, false otherwise.
     */

    private boolean isTouchRacket(MotionEvent event,Player mPlayer){

        return event.getX() >= (mPlayer.bounds.left - 20)
                && event.getX() <= (mPlayer.bounds.right + 20)
                && event.getY() >= (mPlayer.bounds.top - 20)
                && event.getY() <= (mPlayer.bounds.bottom + 20);
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
    private void movePlayerRacket(float dy, Player player) {
        // 1. Calcular nueva posición vertical
        float newTop = player.bounds.top + dy;
        float newBottom = newTop + player.getRacquetHeight();

        // 2. Aplicar límites verticales (evitar salir de pantalla)
        if (newTop < 0) { // Límite superior
            newTop = 0;
        } else if (newBottom > mTableHeight) { // Límite inferior
            newTop = mTableHeight - player.getRacquetHeight();
        }

        // 3. Actualizar posición de la raqueta
        player.bounds.offsetTo(
                player.bounds.left, // Mantener posición horizontal
                newTop // Nueva posición vertical ajustada
        );

        // 4. Redibujado optimizado (según API level)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            invalidate(new Rect(
                    (int) player.bounds.left - 20,
                    (int) newTop - 20,
                    (int) player.bounds.right + 20,
                    (int) newBottom + 20
            ));
        } else {
            // Para API 21+ usar invalidate() completo
            // debido a cambios en hardware acceleration [1][2]
            invalidate();
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
        mPlayer.bounds.offsetTo(2, (mTableHeight - mPlayer.getRacquetHeight())/2);
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


        // Collision checks 

        if(checkCollisionPlayer(mPlayer, mBall)){
            handleCollision(mPlayer, mBall);
        }else if(checkCollisionPlayer(mOpponent, mBall)){
            handleCollision(mOpponent, mBall);
        }else if(checkCollisionWithTopOrBottomWall()){
            mBall.velocity_y = - mBall.velocity_y;
        }else if(checkCollisionWithLeftWall()){
            mGame.setState(GameThread.STATE_LOSE);
            return;
        }else if(checkCollisionWithRightWall()){
            mGame.setState(GameThread.STATE_WIN);
            return;
        }




        if(new Random(System.currentTimeMillis()).nextFloat() < mAiMoveProbability)doAi();

        mBall.moveBall(canvas);


    }

    /**
     * Retrieves the player instance representing the user.
     *
     * This method provides access to the player's paddle, allowing
     * other components to query or manipulate its state.
     *
     * @return The {@link Player} instance representing the user.
     */
    public Player getmPlayer() {
        return mPlayer;
    }

    /**
     * Retrieves the opponent instance representing the AI-controlled paddle.
     *
     * This method provides access to the opponent's paddle, allowing
     * other components to query or manipulate its state.
     *
     * @return The {@link Player} instance representing the opponent.
     */
    public Player getmOpponent() {
        return mOpponent;
    }

    /**
     * Retrieves the ball instance used in the game.
     *
     * This method provides access to the ball object, allowing
     * other components to query or manipulate its state, such as
     * position or velocity.
     *
     * @return The {@link Ball} instance used in the game.
     */
    public Ball getmBall() {
        return mBall;
    }



    /**
     * Sets the TextView used to display the player's score.
     * @param view The TextView that will display the player's score.
     */

    public void setmScorePlayer(TextView view){
        mScorePlayer = view;
    }

    /**
     * Sets the TextView used to display the opponent's score.
     * @param view The TextView that will display the opponent's score.
     */

    public void setmScoreOpponent(TextView view){
        mScoreOpponent = view;
    }


    /**
     * Sets the TextView used to display the game status.
     * @param view The TextView that will display the game status.
     */
    public void setmStatus(TextView view){
        mStatus = view;
    }

}
