package com.g3rdeveloper.lugares;

import com.g3rdeveloper.lugares.beans.LugarBean;
import com.g3rdeveloper.lugares.sqlite.SQLiteHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class AgregarActivity extends ActionBarActivity implements ConnectionCallbacks,
									OnConnectionFailedListener,LocationListener {
	
	private String type = "nvo";
	private LugarBean bean;
	private SQLiteDatabase db;
	private GoogleMap mMap;
    private LocationClient mLocationClient;
    private boolean primeraPosicionCargada = false;
    
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
        setUpLocationClient();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
    
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agregar);
		
		setupActionBar();
		try{
			Intent intent = getIntent();
            type = intent.getStringExtra(MainActivity.LUGAR_MTO);
            if(type==null){
            	type = "NVO";
            }
            if(type.equalsIgnoreCase("MTO")){
            	setTitulo("Editar");
				Bundle b = (Bundle) intent.getExtras();
				bean = (LugarBean) b.get(MainActivity.LUGAR_KEY);
				//txtCodigo.setText("Codigo: "+bean.getId());
				//edtName.setText(bean.getName());
				//edtAge.setText(bean.getAge()+"");
            }else{
            	setTitulo("Agregar");
            }
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	private void setTitulo(String titulo){
		getSupportActionBar().setTitle(titulo);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
		case R.id.itmGuardarFavorito:
			
			break;
			
		case R.id.itmRefrescarMapa:
			
			break;
		}
		return true;
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agregar, menu);
		return true;
	}
	
	
	public void guardarLugar(View view){
        try{
                SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
                db = sqliteHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
               // values.put("name", edtName.getText().toString());
                //values.put("age", Integer.valueOf(edtAge.getText().toString()));
                if(type.equals("NVO")){
                	db.insert("students", null, values);
                }else{
                	db.update("students", values, "code="+bean.getId(), null);
                }
                db.close();
                Intent intent2 = new Intent();
                setResult(RESULT_OK, intent2);
                finish();
        }catch(Exception e){
                Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
	
	
	
	private void setUpMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                //mMap.setMyLocationEnabled(true);
                //mMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }
	
	private void setUpLocationClient() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
	
	
	

	@Override
	public void onLocationChanged(Location location) {
		if(!primeraPosicionCargada){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(cameraUpdate);
            primeraPosicionCargada=true;
		}
	}
	
	@Override
    public void onConnectionFailed(ConnectionResult arg0) {
            // TODO Auto-generated method stub
            
    }

    @Override
    public void onConnected(Bundle arg0) {
            mLocationClient.requestLocationUpdates(REQUEST, this);

    }

    @Override
    public void onDisconnected() {
            // TODO Auto-generated method stub
            
    }

}
