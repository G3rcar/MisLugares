package com.g3rdeveloper.lugares.fragments;

import java.io.IOException;

import com.g3rdeveloper.lugares.MainActivity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class ReproducirAudioFragment extends DialogFragment {

	private static String mFilePath = null;
	//private static String mFileName = null;
	
    private PlayButton	mPlayButton = null;
    private MediaPlayer	mPlayer = null;
    
    static String LOG_TAG = "AUDIO_TAG";
    
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		//mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+MainActivity.APP_DIRECTORY+"/tmp"+"/";
        //mFileName = System.currentTimeMillis()+".3gp";
        //mFilePath += mFileName;
        
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LinearLayout linearlayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams buttons = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        mPlayButton = new PlayButton(getActivity());
        linearlayout.addView(mPlayButton, buttons);
        
		builder.setView(linearlayout)
			.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	dialog.cancel();
	            }
	        });
		return builder.create();
	}
	
	public void setIdFile(int id){
		//id
		mFilePath="";
	}
	
	public void setUrlFile(String url){
		mFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+MainActivity.APP_DIRECTORY+url;
	}
	
	
	@Override
    public void onPause() {
        super.onPause();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
        
        
	private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "error:"+e.getMessage());
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    
	
	class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Detener");
                } else {
                    setText("Reproducir");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Reproducir");
            setOnClickListener(clicker);
        }
    }
	

}
