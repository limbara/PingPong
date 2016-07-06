package omega.soloplayer.android.pingpong;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.Random;

/**
 * Created by User on 10/04/2016.
 */
public class Ball {
    private RectF rect;
    // A rectangle to define an area of the
    // sprite sheet that represents 1 frame

    private Bitmap bitmap;
    private Float Radius;
    float xVelocity;
    float yVelocity;

    public Ball(int screenX, int startX , int startY,int ballSpeed){

        Radius = (float) screenX/20;

        xVelocity = ballSpeed;
        yVelocity = ballSpeed * -1;
        rect = new RectF((float)startX,(float)startY,startX+Radius,startY+Radius);

    }

    public void setRadius(Float radius) {
        Radius = radius;
    }

    public Float getRadius() {
        return Radius;
    }

    public Bitmap getBitmap(){
        return  bitmap;
    }

    public RectF getRect(){
        return rect;
    }

    public void update(long fps){
        rect.left = rect.left + (xVelocity / fps);
        rect.top = rect.top + (yVelocity / fps);
        rect.right = rect.left + Radius;
        rect.bottom = rect.top + Radius;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - Radius;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + Radius;
    }

    public void reset(int x, int y){
        rect.left = x / 2;
        rect.top = y / 2 - 20 ;
        rect.right = x / 2 + Radius;
        rect.bottom = y / 2 - 20 - Radius;
    }

    public void setxVelocity(int x){
        if(xVelocity > 0)
            xVelocity = x;
        else
            xVelocity = x *-1;
    }

    public void setyVelocity(int x){
        if(yVelocity > 0)
            yVelocity = x;
        else
            yVelocity = x *-1;
    }

    public void increasespeed(int X){
        if(yVelocity < 0) {
            yVelocity -= X;
            xVelocity -= X;
        }
        else{
            yVelocity += X;
            xVelocity += X;
        }
    }

}

