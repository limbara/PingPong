package omega.soloplayer.android.pingpong;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.logging.ConsoleHandler;

/**
 * Created by User on 13/04/2016.
 */
public class GameView extends SurfaceView implements Runnable {

    private String text_paused = "PAUSED";

    private Context myContext;
    private Bitmap background;
    // This is our thread
    private Thread gameThread = null;

    // This is new. We need a SurfaceHolder
    // When we use Paint and Canvas in a thread
    // We will see it in action in the draw method soon.
    private SurfaceHolder ourHolder;

    // A boolean which we will set and unset
    // when the game is running- or not.
    volatile boolean playing;

    // Game is paused at the start
    boolean paused = false;

    // This variable tracks the game frame rate
    private long fps;

    // This is used to help calculate the fps
    private long timeThisFrame;

    // A Canvas and a Paint object
    private Canvas canvas;
    private Paint paint;

    // For sound FX
    private SoundPool soundPool;
    int beep1ID = -1;
    int beep2ID = -1;
    int loseLifeID = -1;
    int winID = -1;

    // The size of the screen in pixels
    int screenX;
    int screenY;

    private boolean redplayer,blueplayer;
    private int paddleSpeed;
    private Paddle paddle1,paddle2;
    private int lives;

    private int init_ballSpeed;
    private int increase_ballSpeed;
    private Ball ball;


    private Control controlbox1,controlbox2,controlbox_pause;

    private Item item;
    private int itemEffect;
    private float RandomItemTime = 5000;
    private float ItemDismissTime = 3000;
    private boolean ItemExist=false;
    private int LastHit =0;

    private ArrayList<Integer> random_number = new ArrayList<Integer>();

    public GameView(Context context){
        super(context);

        myContext = context;
        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        WindowManager Wi = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        // Get a Display object to access screen details
        Display display = Wi.getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC,0);

        try{
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("paddle.ogg");
            beep1ID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("wall.ogg");
            beep2ID = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("ballmiss.ogg");
            loseLifeID = soundPool.load(descriptor, 0);

        }catch(IOException e){
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        prepare();
    }

    private void prepare(){
        lives = 3;
        //load gambar background
        background = BitmapFactory.decodeResource(myContext.getResources(),R.drawable.background);
        //menyesuaikan background dengan ukuran layar
        background = Bitmap.createScaledBitmap(background,screenX,screenY,false);

        // Inisialisasi nilai awal kecepatan paddle dengan ukuran layar X dan Y;
        paddleSpeed = (screenX+screenY)/6;

        //Inisialisasi paddle player 1 dan player 2
        //parameter 1 -> panjang paddle
        //parameter 2 -> tinggi paddle
        //parameter 3 -> letak paddle sumbu X
        //parameter 4 -> letak paddle sumbu Y
        //parameter 5 -> kecepatan paddle
        //parameter 6 -> nyawa paddle
        paddle1 = new Paddle(screenX,screenY,screenX/2,screenY-screenY/30,paddleSpeed,lives);
        paddle2 = new Paddle(screenX,screenY,screenX/2,0,paddleSpeed,lives);

        //Inisialisasi kontrol player 1 dan player 2
        // parameter 1 -> panjang kontrol
        // parameter 2 -> tinggi kontrol
        // parameter 3 -> letak kontrol sumbu X
        // parameter 4 -> letak kontrol sumbu Y
        controlbox1 = new Control(screenX,screenY/10,0,screenY-screenY/10);
        controlbox2 = new Control(screenX,screenY/10,0,0);

        //Inisialisasi kontrol pause;
        // parameter 1 -> panjang kontrol
        // parameter 2 -> tinggi kontrol
        // parameter 3 -> letak kontrol sumbu X
        // parameter 4 -> letak kontrol sumbu Y
        controlbox_pause = new Control(screenX,screenY-screenY/10-screenY/10,0,screenY/10);

        //Inisialissi kecepatan bola di awal permainan dengan ukuran layar X dan Y
        init_ballSpeed = (screenX+screenY)/7;

        //Inisialisasi bola
        // parameter 1 -> radius bola
        // parameter 2 -> letak bola sumbu X
        // parameter 3 -> letak bola sumbu Y
        // parameter 4 -> kecepatan bola
        ball = new Ball(screenX,screenX/2,screenY/2,init_ballSpeed);

        //Inisialisasi Efek dari Item dengan ukuran layar X dan Y
        itemEffect = (screenX + screenY)/650;

        //Inisialisasi nilai pertambahan kecepatan saat permainan berlangsung
        increase_ballSpeed = screenX / 60;

    }

    //fungsi run
    //fungsi yang berlangsung terus menerus sampai activity dihentikan
    @Override
    public void run() {
        while (playing) {
            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();
            // Draw the frame
            draw();
            // Update the frame
            // Update the frame
            if(!paused){
                update();
            }
            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

            //cek jika Item telah berada di layar
            if(ItemExist == true){
                ItemDismissTime -=timeThisFrame;
                //jika item tidak dimakan
                //cek jika waktu Dismiss ( penghilagan ) Item telah berakhir
                if(ItemDismissTime <= 0){
                    ItemExist = false;
                    //hapus item dati layar
                    item.ClearItem(screenX,screenY);
                    ItemDismissTime = 3000;
                }
            }
            //jika item tidak berada dilayar
            else{
                RandomItemTime -= timeThisFrame;
                //jika waktu Summon Item berakir
                if(RandomItemTime <=0){
                    //panggil Item ke layar secara acak
                    SummonRandomItem();
                    ItemExist = true;
                    RandomItemTime = 5000;
                }
            }

        }
    }
    //fungsi Update
    //untuk mengecek variabel yang akan berubah saat permainan berlangsung
    public void update(){
        if(redplayer == true && blueplayer == true){
            paddle1.update(fps);
            paddle2.update(fps);
            ball.update(fps);
        }
        // Cek jika paddle player 1 bersentuhan dengan bola
        if(RectF.intersects(paddle1.getRectF(),ball.getRect())) {
            //set terakhir bola dipukul
            LastHit = 1;
            //set arah bola ke kiri atau ke kanan secara acak
            ball.setRandomXVelocity();
            //set arah bola berlawanan
            ball.reverseYVelocity();
            //membersihkan penhalang saat bola bersentuhan dengan objek
            ball.clearObstacleY(paddle1.getRectF().top - screenY/80);
            //meningkatkan kecepatan bola
            ball.increasespeed(increase_ballSpeed);
            //memainkan sound effect paddle.ogg
            soundPool.play(beep1ID, 1, 1, 0, 0, 1);
        }
        // Cek jika paddle player  2 bersentuhan dengan bola
        if(RectF.intersects(paddle2.getRectF(),ball.getRect())) {
            LastHit = 2;
            ball.setRandomXVelocity();
            ball.reverseYVelocity();
            ball.clearObstacleY(paddle2.getRectF().bottom + screenY/20);
            ball.increasespeed(increase_ballSpeed);
            soundPool.play(beep1ID, 1, 1, 0, 0, 1);
        }

        // Cek jika bola bersentuhan dengan dinding kiri
        if(ball.getRect().left < 0){
            ball.reverseXVelocity();// set arah bola berlawanan dengan dinding
            ball.clearObstacleX(screenX/8);
            soundPool.play(beep2ID, 1, 1, 0, 0, 1);// memainkan sound effect wall.ogg
        }

        // Cek jika bola bersentuhan dengan dinding kanan
        if(ball.getRect().right > screenX ){
            ball.reverseXVelocity(); // set arah bola berlawanan dengan dinding
            ball.clearObstacleX(screenX - screenX/8);
            soundPool.play(beep2ID, 1, 1, 0, 0, 1); // memainkan sound effect wall.ogg
        }

        // Cek jika bola melewati dinding atas ( player 2 )
        if(ball.getRect().top < 0) {
            ball.reset(screenX,screenY);// Reset bola pada tengah layar
            paddle1.reset(screenX);// Reset posisi paddle player 1
            paddle2.reset(screenX); // Reset posisi paddle player 2
            ball.setyVelocity(init_ballSpeed);// Set kecepatan bola dengan nilai awal
            ball.reverseYVelocity();// Set Arah bola berlawanan
            if(paused == false){
                paddle2.setLives(paddle2.getLives()-1);// mengurangi nyawa player 2
            }
            //pause game
            paused = true;
        }

        // Cek jika bola melewati dinding bawah ( player 1 )
        if(ball.getRect().bottom > screenY ) {
            ball.reset(screenX,screenY); // Reset bola pada tengah layar
            paddle1.reset(screenX);// Reset posisi paddle player 1
            paddle2.reset(screenX);// Reset posisi paddle player 2
            ball.setyVelocity(init_ballSpeed); // Set kecepatan bola denagn nilai awal
            ball.reverseYVelocity();//Set arah bola berlawanan
            if(paused == false){
                paddle1.setLives(paddle1.getLives()-1);// mengurangi nyawa player 1
            }
            //pause game
            paused = true;
        }

        //Cek jika item berada di layar
        if(ItemExist){
            // jika bola mengenai Item
            if(RectF.intersects(ball.getRect(),item.getRectF())){
                //Cek jika pukulan terakhir dari player 1
                if(LastHit == 1){
                    item.ExexuteItem(paddle1);// eksekusi item ke paddle 1
                }
                //Cek jika pukulan terakhir dari player 2
                else if(LastHit == 2){
                    item.ExexuteItem(paddle2);// eksekusi item ke paddle 2
                }
                item.ClearItem(screenX,screenY);//Bersihkan Item dari layar
            }
        }
        // Cek jika nyawa player 1  = 0
        if(paddle1.getLives() <=0){
            prepare();//preparai semua nilai awal permainan
            //pause game
            paused=true;
        }
        // Cek jika nyawa player 2  = 0
        else if(paddle2.getLives()<= 0 ){
            prepare(); //preparai semua nilai awal permainan
            //pause game
            paused= true;
        }
    }

    //fungsi draw
    //menggambar semua objek permainan
    public void draw(){
        // Cek our drawing surface valid atau tidak ( crash )
        if (ourHolder.getSurface().isValid()) {
            // mengunci canvas agar ready to draw
            canvas = ourHolder.lockCanvas();
            if(redplayer == true && blueplayer == true){
                // Draw the background color
                canvas.drawBitmap(background,0,0,null);
                // Draw ControlBox
                // set warna dari paint
                paint.setColor(Color.argb(255,  255, 255,0));
                canvas.drawRect(controlbox1.getRectF(),paint);
                paint.setColor(Color.argb(255,  255, 0,255));
                canvas.drawRect(controlbox2.getRectF(),paint);

                // draw the Ball
                paint.setColor(Color.argb(255, 255, 0, 0));
                canvas.drawCircle(ball.getRect().left,ball.getRect().top,ball.getRadius(),paint);

                // draw the paddle1
                paint.setColor(Color.argb(255,  0, 0, 0));
                canvas.drawRect(paddle1.getRectF(),paint);
                // draw the paddle2
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawRect(paddle2.getRectF(),paint);

                // cek jika item berada di layar
                if(ItemExist){
                    // gambar Item
                    canvas.drawBitmap(item.getBitmap(),item.getX(),item.getY(),paint);
                }
                // Cek jika game paused = true
                if(paused){
                    paint.setColor(Color.argb(255,  0, 0,0));
                    setTextSizeForWidth(paint,(float)screenX/5,text_paused);
                    canvas.drawText(text_paused,(screenX-screenX/5)/2,screenY/2,paint);
                }
                //Draw paddle 1 lives
                paint.setColor(Color.argb(255,0,0,0));
                canvas.scale(1f, 1f, screenX/2, screenY/2);
                canvas.drawText(Integer.toString(paddle1.getLives()),screenX-screenX/10, screenY/2+30,paint);
                //Draw paddle 2 lives
                paint.setColor(Color.argb(255,0,0,0));
                canvas.scale(-1f, -1f, screenX/2, screenY/2);
                canvas.drawText(Integer.toString(paddle2.getLives()),screenX-screenX/10, screenY/2+30,paint);

            }
            // Draw everything to the screen
            ourHolder.unlockCanvasAndPost(canvas);
        }

    }

    // shutdown our thread.
    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }

    }

    // start our thread.
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // fungsi onTouch
    // mengecek jika terjadi sentuhan pada layar
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // We want to support multiple touch and single touch
        InputHandler handle = InputHandler.getInstance();
        // Loop through all the pointers that we detected and
        // process them as normal touch events.
        for(int i = 0; i < handle.getTouchCount(motionEvent); i++) {
            int tx = (int) handle.getX(motionEvent, i);
            int ty = (int) handle.getY(motionEvent, i);
            if (redplayer == true && blueplayer == true) {
                    // Cek jika sentuhan terjadi pada kontrol player 1 bagian kanan
                    if (controlbox1.inTouchbox(controlbox1.getRectF(), tx, ty) && tx > controlbox1.getLength() / 2 ) {
                        if(paddle1.getRectF().right <= screenX)
                            paddle1.setMovementState(paddle1.RIGHT);
                        else if(paddle1.getRectF().right > screenX)
                            paddle1.setMovementState(paddle1.STOPPED);
                    // Cek jika sentuhan terjadi pada kontrol player 1 bagian kiri
                    } else if (controlbox1.inTouchbox(controlbox1.getRectF(), tx, ty) && tx < controlbox1.getLength() / 2 ) {
                        if(paddle1.getRectF().left >= 0)
                            paddle1.setMovementState(paddle1.LEFT);
                        else if (paddle1.getRectF().left < 0)
                            paddle1.setMovementState(paddle1.STOPPED);
                    }
                    // Cek jika sentuhan terjadi pada kontrol player 2 bagian kanan
                    if (controlbox2.inTouchbox(controlbox2.getRectF(), tx, ty) && tx > controlbox2.getLength() / 2 ) {
                        if(paddle2.getRectF().right <= screenX)
                            paddle2.setMovementState(paddle2.RIGHT);
                        else if(paddle2.getRectF().right > screenX)
                            paddle2.setMovementState(paddle2.STOPPED);
                    // Cek jika sentuhan terjadi pada kontrol player 2 bagian kiri
                    } else if (controlbox2.inTouchbox(controlbox2.getRectF(), tx, ty) && tx < controlbox2.getLength() / 2) {
                        if(paddle2.getRectF().left >= 0)
                            paddle2.setMovementState(paddle2.LEFT);
                        else if(paddle2.getRectF().left < 0 )
                            paddle2.setMovementState(paddle2.STOPPED);
                    }
                // Cek jika sentuhan terjadi pada kontrol pause
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && !(controlbox_pause.inTouchbox(controlbox_pause.getRectF(),tx,ty))){
                    paused = false;
                }

                // Cek jika sentuhan telah diangkat
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    paddle1.setMovementState(paddle1.STOPPED);
                    paddle2.setMovementState(paddle2.STOPPED);
                }

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN && controlbox_pause.inTouchbox(controlbox_pause.getRectF(),tx,ty)) {
                    if (paused == false) {
                        paused = true;
                    }
                    else
                        paused = false;
                }
            }
        }
        return true;

    }

    //fungsi setPlayerControl
    //fungsi untuk set Player 1 dan Player 2 berada dalam permainan
    public void setPlayerControl(boolean red, boolean blue) {
        redplayer = red;
        blueplayer = blue;
    }

    //fungsi SummonRandomItem
    //fungsi untuk Memangil Item ke permainan secara Acak
    private void SummonRandomItem(){
        random_number.clear();
        for (int i = 3; i >= 1; --i) random_number.add(i);
        Collections.shuffle(random_number);
        // set nilai posisi X dan Y Item secara acak sesuai dengan ukuran layar
        int positionX = new Random().nextInt((screenX-screenX/5)-(0+screenX/5))+screenX/5;
        int positionY = new Random().nextInt((screenY-screenY/10) - (0+screenY/10))+screenY/10;
        // Inisialisai Item
        // parameter 1 - > Tipe Item;
        // parameter 2 -> context dari activity
        // parameter 3 -> panjang Item
        // parameter 4 -> tinggi Item
        // parameter 5 -> posisi Item Sumbu X
        // parameter 6 -> posisi Item Sumbu Y
        // parameter 7 -> Besaran Efek dari Item
        item = new Item(random_number.get(0),myContext,screenX,screenY,positionX,positionY,itemEffect);

    }

    //Fungsi SetTextSize
    //fungsi yang digunakan untuk mengatur font size dari tulisan
    private static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }
}
