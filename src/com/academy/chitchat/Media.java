package com.academy.chitchat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class Media extends Activity {

	MediaPlayer scary1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
       // scary1 = MediaPlayer.create(this, downloaded);
		scary1.start();
		scary1.setLooping(true);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
	    scary1.stop();
		scary1.release();
		super.onPause();
	}
}