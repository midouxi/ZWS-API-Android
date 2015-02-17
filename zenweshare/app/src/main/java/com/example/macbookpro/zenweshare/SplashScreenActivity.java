package com.example.macbookpro.zenweshare;

//SPLASH d'écran d'accueil !!
//AGOUZAL Mehdi 




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
// cette classe est responsable de l'image d accueil normalement elle ne sert à rien c'est seulement pour l'esthétique
public class SplashScreenActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		// le temps du splash
		final int welcomeScreenDisplay = 5000; // en milliseconde
		//creation du thread qui se termine des que le splash se termine , sa creation est faite pour ne pas
		Thread welcomeThread = new Thread() {
			int wait = 0;
			@Override
			public void run() {
				try {
					super.run();
					// pour durer le truc 5 secondes , on sleep l'action
					while (wait < welcomeScreenDisplay) {
						sleep(100);
						wait += 100;
					}
				} catch (Exception e) {
					System.out.println("EXc=" + e); // detection et affichage de l'erreur
				} finally {
					// apres le splash !
					startActivity(new Intent(SplashScreenActivity.this,
							MainActivity.class)); // apres le splash passation vers la classe MainActivity.class
					finish();
				}
			}
		};
		welcomeThread.start(); // declenchement du thread qui est cité au dessus

	}
}