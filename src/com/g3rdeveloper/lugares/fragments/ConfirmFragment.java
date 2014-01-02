package com.g3rdeveloper.lugares.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ConfirmFragment extends DialogFragment {
	
	ConfirmFragmentListener listener;
	private String mensaje;
	
	public interface ConfirmFragmentListener{
		public void onDialogPositiveButton();
	}
		
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{ listener = (ConfirmFragmentListener) activity; }
		catch(ClassCastException e){  }
	}

	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
 
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
 
        builder.setMessage(mensaje)
        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()  {
               public void onClick(DialogInterface dialog, int id) {
                    Log.i("Dialogos", "Confirmacion Aceptada.");
                    listener.onDialogPositiveButton();
               }
        })
        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                    Log.i("Dialogos", "Confirmacion Cancelada.");
                    dialog.cancel();
               }
        });
 
        return builder.create();
    }
	
	public void setMessage(String m){
		mensaje=m;
	}
	
	
	
}
