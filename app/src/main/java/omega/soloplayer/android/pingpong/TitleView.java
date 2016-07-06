package omega.soloplayer.android.pingpong;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by User on 31/03/2016.
 */
public class TitleView extends View {

    private Bitmap Btn_VsPlayer,Btn_VsPlayer_Clicked,Btn_Credits,Btn_Credits_Clicked,Background;
    private Context myContext;
    private boolean Btn_VsPlayer_Pressed,Btn_Credits_Pressed;
    private int screenW;
    private int screenH;

    public TitleView(Context context){
        super(context);
        myContext = context;
        //load semua resource gambar
        Background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        Btn_VsPlayer = BitmapFactory.decodeResource(getResources(),R.drawable.button_vs_player);
        Btn_VsPlayer_Clicked = BitmapFactory.decodeResource(getResources(),R.drawable.button_vs_player_clicked);
        Btn_Credits = BitmapFactory.decodeResource(getResources(),R.drawable.button_credits);
        Btn_Credits_Clicked = BitmapFactory.decodeResource(getResources(),R.drawable.button_credits_clicked);
    }

    //fungsi OnSizeChanged
    //fungsi yang mengambil ukuran layar
    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        // lebar layar
        screenW = w;
        // tinggi layar
        screenH = h;
        // Set Ukuran skala Gambar Background sesuai ukuran layar
        Background = Bitmap.createScaledBitmap(Background,screenW,screenH,true);
    }

    //fungsi onDraw
    //fungsi yang dieksekusi saat TitleView berhasil di buat oleh Constructur
    // menggambar semua objeck dari layar judul
    @Override
    protected  void onDraw(Canvas canvas){
        // Draw gambar Backgrougd
        canvas.drawBitmap(Background,0,0,null);
        // Cek jika Tombol VsPlayer ditekan
        if(Btn_VsPlayer_Pressed){
            // Draw button VsPlayer normal
            canvas.drawBitmap(Btn_VsPlayer_Clicked,(screenW-Btn_VsPlayer.getWidth())/2,(int)(screenH*0.7), null);
        }
        else{
            // Draw button VsPlayer ditekan
            canvas.drawBitmap(Btn_VsPlayer,(screenW-Btn_VsPlayer.getWidth())/2,(int)(screenH*0.7), null);
        }
        // Cek jika Tombol Credits ditekan
        if(Btn_Credits_Pressed){
            // Draw button Credits normal
            canvas.drawBitmap(Btn_Credits_Clicked,(screenW-Btn_Credits.getWidth())/2,(int)(screenH*0.8), null);
        }
        else{
            // Draw button Credits ditekan
            canvas.drawBitmap(Btn_Credits,(screenW-Btn_Credits.getWidth())/2,(int)(screenH*0.8), null);
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        int eventaction = event.getAction();
        // posisi X
        int X = (int)event.getX();
        // posisi Y
        int Y = (int)event.getY();

        switch (eventaction){
            case MotionEvent.ACTION_DOWN:
                // Cek jika Sentuhan berada pada tombol VsPlayer
                if(X > (screenW-Btn_VsPlayer.getWidth())/2 &&
                        X < (((screenW-Btn_VsPlayer.getWidth())/2) + Btn_VsPlayer.getWidth()) &&
                        Y > (int)(screenH*0.7) &&
                        Y < (int)(screenH*0.7) + Btn_VsPlayer.getHeight()){
                    Btn_VsPlayer_Pressed = true;
                }
                //Cek jika Sentuhan pada tombol Credits
                else if(X > (screenW-Btn_Credits.getWidth())/2 &&
                        X < (((screenW-Btn_Credits.getWidth())/2) + Btn_Credits.getWidth()) &&
                        Y > (int)(screenH*0.8) &&
                        Y < (int)(screenH*0.8) + Btn_Credits.getHeight()){
                    Btn_Credits_Pressed= true;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                // Jika button VsPlayer tertekan
                if(Btn_VsPlayer_Pressed){
                    startGame(true,true);
                }
                Btn_VsPlayer_Pressed = false;

                // Jika button Credits tertekan
                if(Btn_Credits_Pressed){
                    Intent i = new Intent(myContext, CreditsActivity.class);
                    myContext.startActivity(i);
                }
                Btn_Credits_Pressed = false;
                break;
        }
        invalidate();
        return true;
    }

    // Fungsi startGame
    // memulai permainan ke Game Activity
    protected void startGame(boolean redPlayer, boolean bluePlayer) {
        Intent i = new Intent(myContext, GameActivity.class);
        i.putExtra(GameActivity.EXTRA_BLUE_PLAYER, bluePlayer);
        i.putExtra(GameActivity.EXTRA_RED_PLAYER, redPlayer);
        myContext.startActivity(i);
    }

}
