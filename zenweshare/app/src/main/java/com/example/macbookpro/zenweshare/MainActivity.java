package com.example.macbookpro.zenweshare;
// importation des librairies
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

// la classe main
public class MainActivity extends Activity {

	private Button buttonwidget; // declaration bouton du widget
	private Button buttonconnex; // declaration bouton de connexion 
	static final String ZWS_CALLBACK_URL = "allplayer://site.com"; // le site referant à l'app
	public static final String ZWS_CLIENT_ID = "CV5mavPrhEAfWD1IgfVeZscCAYu6X"; // Client ID
	
	
	// le contenu de cette activité
	public void onCreate(Bundle savedInstanceState) {
		final Context context = this; // setteur du context
		super.onCreate(savedInstanceState); //super class canonique s'herite de la classe Activity se trouve dans tous les apps !!
		setContentView(R.layout.main); // liaison de ce contenu avec sa view (dans le fichier res > layout > main.xml)
		buttonwidget = (Button) findViewById(R.id.buttonwidget); // liaison du bouton connexion avec le bouton ds la view
		buttonconnex = (Button) findViewById(R.id.buttonconnex);  // liaison du bouton widget avec le bouton ds la view
		
		// if (user_id == null & access_token = null) { //ici on doit verifier est-ce que le client a déja l'user_id et access_token ? si oui on cache ou on grise le bouton !
		//buttonconnex.setVisibility(View.GONE);//cacher le bouton 
		//buttonconnex.setEnabled(false); // griser le bouton 
		//}
		
		buttonwidget.setOnClickListener(new OnClickListener() { // integration de l'écouteur
			@Override
			public void onClick(View arg0) { // si le bouton est cliqué
				Intent intent = new Intent(context, WebViewActivity.class); // on cr�e un intent qui contient l'activit� suivante
				startActivity(intent); // démarrage de l'intent
			}

		});
		
		buttonconnex.setOnClickListener(new OnClickListener() { // integration de l'�couteur
			@Override
			public void onClick(View arg0) {  // si le bouton  est cliqu� 
				String url = "http://192.168.1.116:80/fr/app/login/mobile/"+ZWS_CLIENT_ID; // on compose l'url = adresse zenweshare + client id + l'encryptage du secret key
				System.out.println(Uri.parse(url)); // pour le test
				try{ // sniffer l'exception en cas ou ! 
				Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) ); //on cr�e un intent qui contient une page web
				startActivity(intent); // démarrage de l'intent
				}catch(Exception e){ // en cas de détection de l'exception
			    	System.out.println(e.toString()); // afficher cette exception pour le diagnostic
			    }
			}
		});
		
		// après la connexion si tout se passe bien il y aura une redirection vers l'app á travers le lien de allplayer://site.com
		// on profite de cette redirection vers l'app pour passer la valeur du requestcode et le lien sera comme ceci : allplayer://site.com/request_code
		Uri data = getIntent().getData(); // on prend le site de redirection
        if (data != null && data.toString().startsWith(ZWS_CALLBACK_URL) && data.toString().length()>20) { // ce process ne se déclenche que si data n'est pas une valeur nulle ainsi qu'elle débute par allplayer://site.com
            List<String> params = data.getPathSegments(); // on décortique le lien dans une liste chainée , il s'arrete dans chaque slash et il le stocke dans une case
            final String request_code = params.get(0); // premiere valeur apres le slash c'est le request code
            new FetchTask().execute(request_code);
                //buttonconnex.setEnabled(false);
			}
	}
        // creation du thread l'execution se fait en 3 temps : onPreExecute => DoInBackground => OnPostExecute
        public class FetchTask extends AsyncTask<String, Void, String> {
            // étape 1
            @Override
            protected void onPreExecute() {
                Toast.makeText(getApplicationContext(), "Importation des commentaires en cours", Toast.LENGTH_SHORT).show();
            }
            // etape 2
            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost("http://192.168.1.116:83/APIZWS/index44.php"); //

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("requestcode", params[0]));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    // Execution HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    // dans ce bloc on recoit la réponse et on l'encode
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    sb.append(reader.readLine() + "\n");
                    String line = "0";
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    reader.close();
                    String result11 = sb.toString(); // cast du resultat

                    // Parsage des données !!
                    return new String(result11);
                } catch (Exception e) {
                    e.printStackTrace(); // sniff d'erreur
                    return null;
                }
            }
            // etape 3
            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    //le process est réussi
                    String ctext =  "Importation réussite du compte "+ result.trim(); // trim pour enlever l'espace devant result
                    Toast.makeText(getApplicationContext(),ctext, Toast.LENGTH_LONG).show(); // toast = petite popup comportant le message ainsi la durée d'affichage
                } else {
                    // erreur
                    System.out.println("Importation échouée");
                }
            }
        }

        }