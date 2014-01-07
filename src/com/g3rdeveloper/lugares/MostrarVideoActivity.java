package com.g3rdeveloper.lugares;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.Window;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MostrarVideoActivity extends Activity {
	
	Uri mUrl;
	int idRecurso;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mostrar_video);
        
        try{
			Intent intent = getIntent();
			Bundle b = (Bundle) intent.getExtras();
            idRecurso = b.getInt(MainActivity.ID_KEY);
            
            if(idRecurso==0){
            	String path = b.getString(MainActivity.URL_KEY);
            	mUrl = Uri.parse(Environment.getExternalStorageDirectory()+MainActivity.APP_DIRECTORY+path);
            }else{
            	buscarUri(idRecurso);
            }
            
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		reproducirVideo();
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
