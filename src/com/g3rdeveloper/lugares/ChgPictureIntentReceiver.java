package com.g3rdeveloper.lugares;

import java.io.File;
import java.io.FilenameFilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

public class ChgPictureIntentReceiver extends BroadcastReceiver {

	private static int clickCount = 0;
	
	private Uri[] mUrls;
    String[] mFiles = null;

    @Override
    public void onReceive(Context context, Intent intent) {
    	if(intent.getAction().equals(WidgetProvider.ACTIONKEY)){
    		obtenerArchivos();
    		updateWidgetPictureAndButtonListener(context);
    	}
    }

    private void updateWidgetPictureAndButtonListener(Context context) {
    	RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_principal);
    	if(mFiles.length==0){
    		remoteViews.setImageViewResource(R.id.widget_image, R.drawable.nada);
    	}else{
    		remoteViews.setImageViewUri(R.id.widget_image, getImageToSet());
    	}
    	remoteViews.setOnClickPendingIntent(R.id.widget_button, WidgetProvider.buildButtonPendingIntent(context));

    	WidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }
    
    private void obtenerArchivos(){
    	File images = new File(Environment.getExternalStorageDirectory(),MainActivity.APP_DIRECTORY+"/tmp/");
        File[] imagelist = images.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                        return (name.endsWith(".jpg"));
                }
        });
        mFiles = new String[imagelist.length];
        for (int i = 0; i < imagelist.length; i++) {
                mFiles[i] = imagelist[i].getAbsolutePath();
        }
        mUrls = new Uri[mFiles.length];

        for (int i = 0; i < mFiles.length; i++) {
                mUrls[i] = Uri.parse(mFiles[i]);
        }
    }
    
    

    private Uri getImageToSet() {
        clickCount++;
        if(clickCount>mFiles.length){
        	clickCount = 0;
        }
        Log.d("Widget",mUrls[clickCount].toString());
        return mUrls[clickCount];
    }

}
