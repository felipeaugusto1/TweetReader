package tweetreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import felipe.tweetreader.R;

/**
 * Tela responsável pelo Splash Screen.
 * 
 * @author Felipe Augusto
 *
 */
public class SplashScreenActivity extends Activity {

	private static int TEMPO_SPLASH = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen_activity);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent i = new Intent(SplashScreenActivity.this, UserChooseActivity.class);
				startActivity(i);
				finish();
			}
		}, TEMPO_SPLASH);
	}
}