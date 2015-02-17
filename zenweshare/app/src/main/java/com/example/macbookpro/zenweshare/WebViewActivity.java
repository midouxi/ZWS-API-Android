package com.example.macbookpro.zenweshare;
// importation des librairies
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
//la classe de la webviewactivity se declenche lorsque on clique sur le bouton widget
public class WebViewActivity extends Activity {
	

	public static final String ZWS_CLIENT_ID = "CV5mavPrhEAfWD1IgfVeZscCAYu6X";
	public static final int user_id = 344; // le user id du client dans Zenweshare cette variable doit etre importé depuis la BD
	private WebView webView; //declaration de la webview (la webview c'est une fenetre du navigateur au sein de l'app)
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);//liaison de cette activité avec sa view
		webView = (WebView) findViewById(R.id.webView1);//liaison la webview avec le composant webview de la view :P !!
		webView.getSettings().setJavaScriptEnabled(true); // activer le javascript (puisque notre popup requiert js)
		
		webView.loadUrl("http://192.168.1.117:80/fr/widget/"+ZWS_CLIENT_ID+"/"+user_id+"?username=1"); // le lien qui va être lancer dans cette webview vous devez remplacer l'adresse IP avec www.zenweshare.com
	}
}