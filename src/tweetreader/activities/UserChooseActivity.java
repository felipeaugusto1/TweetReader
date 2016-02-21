package tweetreader.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import felipe.tweetreader.R;

/**
 * Activity responsável pelo recebimento do nome do usuário do twitter, e
 * redirecionamento para a activity responsável pela busca dos tweets.
 * 
 * @author Felipe Augusto
 * 
 */
public class UserChooseActivity extends Activity {

	private EditText editTextNomeUsuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_choose_activity);

		if (!(this.getIntent().getExtras() == null))
			Toast.makeText(this,
					this.getIntent().getExtras().getString("erro"),
					Toast.LENGTH_LONG).show();

		this.editTextNomeUsuario = (EditText) findViewById(R.id.edNomeUsuario);
	}

	/**
	 * Método responsável por tratar ação de clique do botão da página
	 * splash_screen_activity.
	 */
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnShowTweetsByUser:

			if (isValidUserName(this.editTextNomeUsuario.getText().toString())) {
				Intent tweets = new Intent(this, TweetReaderActivity.class);
				tweets.putExtra("nomeUsuario", this.editTextNomeUsuario
						.getText().toString());
				startActivity(tweets);
			} else
				Toast.makeText(getApplicationContext(),
						"Por favor informar um nome de usuário.",
						Toast.LENGTH_LONG).show();

			break;
		default:
			break;
		}
	}

	/**
	 * Método responsável por verificar se o nome do usuário informado é válido.
	 */
	private boolean isValidUserName(String userName) {
		if ((userName == null) || (userName.trim().length() == 0))
			return false;
		else
			return true;
	}

}