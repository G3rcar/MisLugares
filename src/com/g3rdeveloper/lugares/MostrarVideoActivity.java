package com.g3rdeveloper.lugares;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

public class MostrarVideoActivity extends ActionBarActivity {
	
	Uri mUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mostrar_video);
        setupActionBar();
		
		buscarUri(0);
		reproducirVideo();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mostrar_video, menu);
		return true;
	}
	
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
	
	private void buscarUri(int id){
		//mUrl
	}
	
	private void reproducirVideo(){
		VideoView videoView = (VideoView) findViewById(R.id.vdoReproductor);
        videoView.setVideoURI(mUrl);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
	}

}
