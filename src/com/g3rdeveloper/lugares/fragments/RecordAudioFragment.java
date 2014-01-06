package com.g3rdeveloper.lugares.fragments;

import java.io.IOException;

import com.g3rdeveloper.lugares.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class RecordAudioFragment extends DialogFragment {

	private static String mFilePath = null;
	private static String mFileName = null;
	private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton	mPlayButton = null;
    private MediaPlayer	mPlayer = null;
    
    static String LOG_TAG = "AUDIO_TAG";
    
    private boolean estaGrabado = false;
    
    
    RecordAudioFragmentListener listener;
	
	public interface RecordAudioFragmentListener{
		public void onRecordPositiveButton(String name);
	}
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{ listener = (RecordAudioFragmentListener) activity; }
		catch(ClassCastException e){  }
	}
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+MainActivity.APP_DIRECTORY+"/tmp"+"/";
        mFileName = System.currentTimeMillis()+".3gp";
        mFilePath += mFileName;
        
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LinearLayout linearlayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams buttons = new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT);
        buttons.weight = 1;
        mRecordButton = new RecordButton(getActivity());
        linearlayout.addView(mRecordButton, buttons);
        mPlayButton = new PlayButton(getActivity());
        linearlayout.addView(mPlayButton, buttons);
        
		builder.setView(linearlayout)
			.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()  {
				public void onClick(DialogInterface dialog, int id) {
					if(estaGrabado){
						Log.i("Dialogos", "Confirmacion Aceptada.");
						listener.onRecordPositiveButton(mFileName);
					}else{
						Toast.makeText(getActivity(), "No has grabado nada aún", Toast.LENGTH_SHORT).show();
					}
				}
			})
	        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	dialog.cancel();
	            }
	        });
		
		
		
		return builder.create();
	}
	
	
	
	
	@Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
        
        
	private void onRecord(boolean start) {
		if (start) {
			startRecording();
			mPlayButton.setEnabled(false);
		} else {
			stopRecording();
			mPlayButton.setEnabled(true);
		}
	}

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
            mRecordButton.setEnabled(false);
        } else {
            stopPlaying();
            mRecordButton.setEnabled(true);
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
    
    
    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "error:"+e.getMessage());
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        estaGrabado = true;
    }
    
    
    
	
	
	
	class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Detener");
                } else {
                    setText("Grabar");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Grabar");
            setOnClickListener(clicker);
        }
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
