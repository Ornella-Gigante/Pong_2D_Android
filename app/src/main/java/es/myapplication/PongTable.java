package es.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * Class representing the Pong table, extending SurfaceView to display graphical content.
 * Implements SurfaceHolder.Callback to handle surface-related events.
 */
public class PongTable extends SurfaceView implements  SurfaceHolder.Callback{

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
    SurfaceHolder holder;
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
     * @param ctx Application context
     * @param attr Layout attributes
     */
    public void initPongTable(Context ctx, AttributeSet attr){

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

    }

    /**
     * Called immediately after any structural changes (format or size) have been made to the surface.
     * Updates the table dimensions to match the new surface size.
     * This method is always called at least once, after {@link #surfaceCreated}.
     *
     * @param holder The SurfaceHolder whose surface has changed.
     * @param format The new PixelFormat of the surface.
     * @param width  The new width of the surface.
     * @param height The new height of the surface.
     */

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

        mTableHeight = height;
        mTableWidth =width;
    }

    /**
     * This is called immediately before a surface is being destroyed. After
     * returning from this call, you should no longer try to access this
     * surface.  If you have a rendering thread that directly accesses
     * the surface, you must ensure that thread is no longer touching the
     * Surface before returning from this function.
     *
     * @param holder The SurfaceHolder whose surface is being destroyed.
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    /**
     * Draws the view's content on the provided canvas.
     * @param canvas Canvas where the content will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
     * Handles touch events on the PongTable.
     * Currently, it delegates the touch handling to the superclass implementation.
     * @param event The MotionEvent representing the touch input.
     * @return True if the event was handled, false otherwise.
     */

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
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
     * Moves the player's racket vertically by a specified distance.
     * Currently, this method is empty and needs implementation for racket movement logic.
     * @param dy The distance to move the racket vertically.
     * @param player The player whose racket is being moved.
     */
    public void movePlayerRacket(float dy, Player player){

    }

    /**
     * Moves the player's position to a specified location on the table.
     * This method is synchronized to ensure thread-safe updates to the player's position.
     * Currently, this method is empty and needs implementation for player movement logic.
     * @param player The player whose position is being updated.
     * @param left The new x-coordinate of the player's position.
     * @param top The new y-coordinate of the player's position.
     */
    public synchronized void movePlayer(Player player, float left, float top){

    }

}
