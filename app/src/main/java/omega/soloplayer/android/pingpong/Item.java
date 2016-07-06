package omega.soloplayer.android.pingpong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

/**
 * Created by User on 25/04/2016.
 */
public class Item  {

    private static final int Shrink = 1;
    private static final int Growth = 2;
    private static final int Speed = 3;
    private int ItemType;
    private RectF rectF;
    private Bitmap bitmap;
    private float length;
    private float height;
    private float x;
    private float y;
    private int effect;

    public Item(int ItemType, Context mycontext, int screenX, int screenY, int x, int y, int effect){

        if(ItemType!=0){
            this.ItemType = ItemType;

            length = screenX/5;
            height = screenY/30;

            if(this.ItemType == Shrink){
                this.bitmap = BitmapFactory.decodeResource(mycontext.getResources(),R.drawable.item_shrink);
            }
            else if(this.ItemType == Growth){
                this.bitmap = BitmapFactory.decodeResource(mycontext.getResources(),R.drawable.item_growth);
            }
            else if(this.ItemType == Speed){
                this.bitmap = BitmapFactory.decodeResource(mycontext.getResources(),R.drawable.item_speed);
            }
            this.bitmap = Bitmap.createScaledBitmap(this.bitmap,(int)length,(int)height,false);

            this.x = x;
            this.y = y;
            this.effect = effect;

            this.rectF = new RectF((float)x,(float)y,x+bitmap.getWidth(),y+bitmap.getHeight());
        }
       }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public RectF getRectF() {
        return rectF;
    }

    public float getLength() {
        return length;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getEffect() {
        return effect;
    }

    public void ExexuteItem(Paddle paddle){
        if(ItemType == Shrink){
            paddle.setLength(paddle.getLength() - getEffect());
        }
        else if(ItemType == Growth){
            paddle.setLength(paddle.getLength()+ getEffect());
        }
        else if(ItemType == Speed){
            paddle.setPaddleSpeed((float) (paddle.getPaddleSpeed() + getEffect()));
        }
    }

    public void ClearItem(int screenX, int screenY){
        this.x = screenX;
        this.y = screenY;
    }
}


