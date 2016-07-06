package omega.soloplayer.android.pingpong;

import android.graphics.RectF;

/**
 * Created by User on 13/04/2016.
 */
public class Control {
    private RectF rectF;

    // How long and high our control will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    public Control(int screenX,int screenY,int x, int y){
        length = screenX;
        height = screenY;
        this.x = x;
        this.y = y;

        rectF = new RectF(x,y,x+length,y+height);
    }

    public RectF getRectF(){
        return  rectF;
    }

    public float getLength(){
        return length;
    }

    public float getHeight(){
        return height;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public boolean inTouchbox(RectF Rect,int x, int y) {
        return Rect.contains(x, y);
    }


}
