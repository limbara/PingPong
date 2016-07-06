package omega.soloplayer.android.pingpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Xfermode;

/**
 * Created by User on 10/04/2016.
 */
public class Paddle {
    // RectF is an object that holds four coordinates - just what we need
    private RectF rect;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the paddle will move
    private float paddleSpeed;
    private float PADDLESPEED;

    // Which ways can the paddle move
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    // Is the paddle moving and in which direction
    private int paddleMoving = STOPPED;

    private int lives;

    // This the the constructor method
    // When we create an object from this class we will pass
    // in the screen width and height
    public Paddle(int screenX, int screenY , int start_x , int start_y,int paddleSpeed,int lives){

        length = screenX/5;
        height = screenY/30;

        // Start paddle in roughly the sceen centre
        this.x = start_x-length/2;
        this.y = start_y;

        rect = new RectF(x, y, x + length, y + height);

        // How fast is the paddle in pixels per second
        this.paddleSpeed = paddleSpeed;
        this.PADDLESPEED = paddleSpeed;

        this.lives = lives;
    }

    // This is a getter method to make the rectangle that
    // defines our paddle available in BreakoutViewclass
    public RectF getRectF(){
        return rect;
    }

    // This method will be used to change/set if the paddle is going left, right or nowhere
    public void setMovementState(int state){
        paddleMoving = state;
    }

    // This update method will be called from update in BreakoutView
    // It determines if the paddle needs to move and changes the coordinates
    // contained in rect if necessary
    public void update(long fps){
        if(paddleMoving == LEFT){
            x = x - paddleSpeed / fps;
        }

        if(paddleMoving == RIGHT){
            x = x + paddleSpeed / fps;
        }

        rect.left = x;
        rect.right = x + length;
    }

    public void reset(int screenX){
        length = screenX/5;paddleSpeed =PADDLESPEED;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getHeight(){
        return height;
    }

    public float getLength(){
        return length;
    }

    public void setLives(int x){ this.lives = x; }

    public void cutlives(){this.lives--;}

    public int getLives(){
        return lives;
    }

    public int getPaddleMoving(){
        return paddleMoving;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setPaddleSpeed(float paddleSpeed) {
        this.paddleSpeed = paddleSpeed;
    }

    public float getPaddleSpeed() {
        return paddleSpeed;
    }

}
