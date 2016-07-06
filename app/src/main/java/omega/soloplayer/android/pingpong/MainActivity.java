package omega.soloplayer.android.pingpong;

        import android.app.Activity;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.Window;
        import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Inisialisasi TitleView ke TView
        TitleView TView = new TitleView(this);
        // Set TView agar layar tetap terbuka saat applikasi dibuka
        TView.setKeepScreenOn(true);
        // Set Window tidak memiliki judul applikasi
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set Window full Screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Mulai TView
        setContentView(TView);
    }
}
