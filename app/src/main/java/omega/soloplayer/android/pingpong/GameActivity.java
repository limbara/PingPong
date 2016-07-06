package omega.soloplayer.android.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by User on 13/04/2016.
 */
public class GameActivity extends Activity {
    GameView gameView;

    public static final String
            EXTRA_RED_PLAYER = "red-is-player",
            EXTRA_BLUE_PLAYER = "blue-is-player";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Init gameView
        gameView = new GameView(this);
        setContentView(gameView);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        // Set Player 1 True dan Player 2 True dari Vs Player
        gameView.setPlayerControl(b.getBoolean(EXTRA_RED_PLAYER, false),
                b.getBoolean(EXTRA_BLUE_PLAYER, false)
        );
    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}
