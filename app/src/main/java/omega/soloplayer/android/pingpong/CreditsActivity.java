package omega.soloplayer.android.pingpong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by User on 07/05/2016.
 */
public class CreditsActivity extends Activity{
    CreditsView creditsView;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Init gameView
        creditsView = new CreditsView(this);
        setContentView(creditsView);

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        creditsView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        creditsView.pause();
    }

}
