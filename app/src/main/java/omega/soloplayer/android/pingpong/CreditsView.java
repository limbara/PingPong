package omega.soloplayer.android.pingpong;

import android.content.Context;
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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.IOException;


/**
 * Created by User on 07/05/2016.
 */
public class CreditsView extends SurfaceView implements Runnable {

    private String text_paused = "PAUSED";
    private String Title;
    private float X1 = 0;
    private float X2 = 0;
    private String Credits_opening;
    private String Member1;
    private String Member2;
    private String Member3;
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

    // The size of the screen in pixels
    int screenX;
    int screenY;

    int textSpeed;

    int ballSpeed;
    private Ball ball;
    private RectF rectF;

    public CreditsView(Context context) {
        super(context);

        myContext = context;
        // Initialize ourHolder and paint objects
        ourHolder = getHolder();
        paint = new Paint();

        WindowManager Wi = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // Get a Display object to access screen details
        Display display = Wi.getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);

        screenX = size.x;
        screenY = size.y;

        ballSpeed = (screenX + screenY )/ 8;

        // This SoundPool is deprecated but don't worry
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Load our fx in memory ready for use
            descriptor = assetManager.openFd("paddle.ogg");
            beep1ID = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }
        background = BitmapFactory.decodeResource(myContext.getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);

        Title = getResources().getString(R.string.Credits);
        Credits_opening = getResources().getString(R.string.Credits_opening);
        Member1 = getResources().getString(R.string.member1);
        Member2 = getResources().getString(R.string.member2);
        Member3 = getResources().getString(R.string.member3);
        ball = new Ball(screenX,screenX/2,screenY/2,ballSpeed);
        rectF = new RectF(screenX/12,screenY/2,screenX-screenX/12,screenY-screenY/20);
        X1 = 0;
        X2 = screenX;

        textSpeed = (screenX + screenY ) / 10;
    }

    @Override
    public void run() {
        while (playing) {

            // Capture the current time in milliseconds in startFrameTime
            long startFrameTime = System.currentTimeMillis();

            // Update the frame
            // Update the frame
            if (!paused) {
                update(fps);
            }

            // Draw the frame
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

        }
    }

    public void update(long fps) {
        if(fps != 0){
            X1 = X1 + (textSpeed/fps);
            if(X1 >= screenX+200){
                X1 = 0-200;
            }
            X2 = X2 - (textSpeed/fps);
            if(X2 <= 0-200){
                X2 = screenX+200;
            }
            ball.update(fps);
            if(ball.getRect().top <= screenY/2){
                ball.clearObstacleY(screenY/2+100);
                ball.reverseYVelocity();
            }
            else if(ball.getRect().bottom >= screenY - screenY/20){
                ball.clearObstacleY(screenY-screenY/20-50);
                ball.reverseYVelocity();
            }
            else if(ball.getRect().right >= screenX-screenX/12){
                ball.clearObstacleX(screenX-screenX/12-50);
                ball.reverseXVelocity();
            }
            else if(ball.getRect().left <= screenX/12){
                ball.clearObstacleX(screenX/12+50);
                ball.reverseXVelocity();
            }
        }

    }

    public void draw() {
        // Make sure our drawing surface is valid or we crash
        if (ourHolder.getSurface().isValid()) {
            // Lock the canvas ready to draw
            canvas = ourHolder.lockCanvas();
            // Draw the background color
            canvas.drawBitmap(background, 0, 0, null);

            if (paused) {
                paint.setColor(Color.argb(255, 0, 0, 0));
                canvas.drawText(text_paused, (screenX - screenX / 5) / 2, screenY / 2, paint);
            }

            paint.setColor(Color.argb(255, 0, 0, 0));
            paint.setTextSize(screenX/15);
            paint.setColor(Color.argb(255, 255, 0, 0));
            canvas.drawText(Title.toUpperCase(),X1,(int)(screenY*0.1),paint);
            paint.setColor(Color.argb(255, 0, 0, 0));
            paint.setTextSize(screenX/25);
            canvas.drawText(Credits_opening,(int)(screenX*0.1),(int)(screenY*0.2),paint);
            canvas.drawText(Member1,(int)(screenX*0.1),(int)(screenY*0.3),paint);
            canvas.drawText(Member2,(int)(screenX*0.1),(int)(screenY*0.33),paint);
            canvas.drawText(Member3,(int)(screenX*0.1),(int)(screenY*0.36),paint);
            paint.setTextSize(screenX/15);
            paint.setColor(Color.argb(255, 255, 0, 0));
            canvas.drawText(Title.toUpperCase(),X2,(int)(screenY*0.45),paint);

            paint.setColor(Color.argb(255, 0, 0, 0));
            canvas.drawRect(rectF,paint);
            paint.setColor(Color.argb(255, 255, 0, 0));
            canvas.drawCircle(ball.getRect().left,ball.getRect().top,ball.getRadius(),paint);

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

}
