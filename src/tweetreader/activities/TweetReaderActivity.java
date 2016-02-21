package tweetreader.activities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import tweetreader.list.AdapterListView;
import tweetreader.models.Authenticated;
import tweetreader.models.Tweet;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import felipe.tweetreader.R;

/**
 * Classe responsável por mostrar a lista de tweets na view, e também, por
 * realizar apenas uma requisição de busca dos últimos 200 tweets da timeline do
 * usuário.
 * 
 * @author Felipe Augusto
 * 
 */
public class TweetReaderActivity extends ListActivity {

	private final static String LOG_TAG = "TweetReader";
	private ListActivity activity;
	private String userName = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.activity = this;
		// progress bar
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminateVisibility(true);

		setContentView(R.layout.tweets_list);

		Bundle b = getIntent().getExtras();
		this.userName = String.valueOf(b.getString("nomeUsuario"));

		verifyConnection();
	}

	/**
	 * Realiza a busca de Tweets após verificar a conexão com a internet.
	 */
	private void verifyConnection() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			new GetTimeline().execute(this.userName);
		} else {
			Toast.makeText(getApplicationContext(),
					"Conexão com internet indisponível.", Toast.LENGTH_LONG)
					.show();

			startActivity(new Intent(TweetReaderActivity.this,
					UserChooseActivity.class));
			
			finish();
		}
	}

	/**
	 * Thread responsável por baixar a timeline, e todas as conversões entre
	 * JSon e objetos.
	 */
	private class GetTimeline extends AsyncTask<String, Void, String> {
		private final static String CONSUMER_KEY = "mxwZWN4UDjNYUIEW0q46xcs6O";
		private final static String CONSUMER_SECRET = "7TB6c9SVEpHolJunR1SAdFRl1m3GkWRWGAVJoTS77vVBaOsA9Q";
		private final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
		private final static String TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?count=200&screen_name=";

		private final static int QTD_TWEETS = 200;
		private List<Tweet> tweets = new ArrayList<>(QTD_TWEETS);
		private LinearLayout layoutProgressBar = (LinearLayout) findViewById(R.id.progressBarLayout);

		@Override
		protected String doInBackground(String... screenNames) {
			String result = null;
			if (screenNames.length > 0) {
				result = getTwitterStream(screenNames[0]);
			}

			if (result.equals("Not Found")) {
				Bundle bundle = new Bundle();
				bundle.putString("erro", "Usuário não encontrado.");

				startActivity(new Intent(TweetReaderActivity.this,
						UserChooseActivity.class).putExtras(bundle));
			}

			return result;
		}

		@Override
		protected void onPreExecute() {
			this.layoutProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onPostExecute(String result) {
			setListAdapter(new AdapterListView(activity, this.tweets));
			this.layoutProgressBar.setVisibility(View.GONE);
		}

		/**
		 * Método responsável por remover a timezone.
		 */
		private String removerTimeZone(String data) {
			return data.replaceFirst("(\\s[+|-]\\d{4})", "");
		}

		/**
		 * Método responsável por fazer o tratamento da data para formato
		 * brasileiro.
		 */
		private String formatarData(String data) {
			String strData = null;
			TimeZone tzUTC = TimeZone.getTimeZone("UTC");
			DateFormat formatoEntrada = new SimpleDateFormat(
					"EEE MMM dd HH:mm:ss yyyy", Locale.US);
			formatoEntrada.setTimeZone(tzUTC);
			DateFormat formatoSaida = new SimpleDateFormat(
					"dd/MM/yyyy, 'às' HH:mm");

			try {
				strData = formatoSaida.format(formatoEntrada.parse(data));
			} catch (ParseException e) {
				Log.e("Erro parsear data: ", Log.getStackTraceString(e));
			}
			return strData;
		}

		/**
		 * Método responsável pela conversão do objeto JSon, para o objeto
		 * Authenticated.
		 */
		private Authenticated jsonToAuthenticated(String rawAuthorization) {
			Authenticated auth = null;
			if (rawAuthorization != null && rawAuthorization.length() > 0) {
				try {
					Gson gson = new Gson();
					auth = gson.fromJson(rawAuthorization, Authenticated.class);
				} catch (IllegalStateException ex) {
					Log.e("Erro de autenticação: ", ex.getMessage());
				}
			}
			return auth;
		}

		/**
		 * Método responsável por realizar a requisiçao HTTP, pegar o corpo da
		 * página, tratar o objeto JSon e criar os objetos tweet.
		 */
		private String getResponseBody(HttpRequestBase request) {
			final int REQ_HTTP_SUCESSO = 200;

			StringBuilder sb = new StringBuilder();
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient(
						new BasicHttpParams());
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String reason = response.getStatusLine().getReasonPhrase();

				if (statusCode == REQ_HTTP_SUCESSO) {

					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();

					BufferedReader bReader = new BufferedReader(
							new InputStreamReader(inputStream, "UTF-8"), 8);
					String line = null;
					while ((line = bReader.readLine()) != null) {
						sb.append(line);
					}
				} else {
					sb.append(reason);
				}
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex.toString());
			}

			// realiza a busca nos objetos JSon.
			try {
				JSONArray jsonArray = new JSONArray(sb.toString());
				JSONObject jsonObject;

				for (int i = 0; i < jsonArray.length(); i++) {

					jsonObject = (JSONObject) jsonArray.get(i);
					Tweet tweet = new Tweet();

					tweet.setUsuario("@"
							+ jsonObject.getJSONObject("user").getString(
									"screen_name") + " - ");
					tweet.setMensagem(jsonObject.getString("text"));
					String dataSemTimeZone = removerTimeZone(jsonObject
							.getString("created_at"));
					tweet.setDataEnvio(formatarData(dataSemTimeZone));

					this.tweets.add(tweet);
				}
			} catch (JSONException e) {
				Log.e("Erro ao parsear JSon", e.getMessage());
			}

			return sb.toString();
		}

		/**
		 * Método responsável por realizar autenticação da app.
		 */
		private String getTwitterStream(String screenName) {
			String results = null;

			try {
				String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
				String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET,
						"UTF-8");

				String combined = urlApiKey + ":" + urlApiSecret;

				String base64Encoded = Base64.encodeToString(
						combined.getBytes(), Base64.NO_WRAP);

				HttpPost httpPost = new HttpPost(TwitterTokenURL);
				httpPost.setHeader("Authorization", "Basic " + base64Encoded);
				httpPost.setHeader("Content-Type",
						"application/x-www-form-urlencoded;charset=UTF-8");
				httpPost.setEntity(new StringEntity(
						"grant_type=client_credentials"));
				String rawAuthorization = getResponseBody(httpPost);
				Authenticated auth = jsonToAuthenticated(rawAuthorization);

				if (auth != null && auth.getToken_type().equals("bearer")) {

					HttpGet httpGet = new HttpGet(TwitterStreamURL + screenName);

					httpGet.setHeader("Authorization",
							"Bearer " + auth.getAccess_token());
					httpGet.setHeader("Content-Type", "application/json");
					results = getResponseBody(httpGet);
				}
			} catch (UnsupportedEncodingException ex) {
				Log.d(LOG_TAG, ex.getMessage());
			} catch (IllegalStateException ex1) {
				Log.d(LOG_TAG, ex1.getMessage());
			}

			return results;
		}
	}
}