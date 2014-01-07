package com.g3rdeveloper.lugares;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;



public class WidgetProvider extends AppWidgetProvider {

	public static final String ACTIONKEY = "com.g3rdeveloper.intent.action.CHANGE_PICTURE";
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

         RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_principal);
         remoteViews.setOnClickPendingIntent(R.id.widget_button, buildButtonPendingIntent(context));

         pushWidgetUpdate(context, remoteViews);
    }

    public static PendingIntent buildButtonPendingIntent(Context context) {
    	Intent intent = new Intent();
    	intent.setAction(ACTIONKEY);
    	return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void pushWidgetUpdate(Context context, RemoteViews remoteViews) {
    	ComponentName myWidget = new ComponentName(context, WidgetProvider.class);
    	AppWidgetManager manager = AppWidgetManager.getInstance(context);
    	manager.updateAppWidget(myWidget, remoteViews);                
    }
}
