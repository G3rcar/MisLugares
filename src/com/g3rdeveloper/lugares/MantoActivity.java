package com.g3rdeveloper.lugares;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.g3rdeveloper.lugares.beans.ItemsBean;
import com.g3rdeveloper.lugares.beans.LugarBean;
import com.g3rdeveloper.lugares.fragments.RecordAudioFragment;
import com.g3rdeveloper.lugares.fragments.RecordAudioFragment.RecordAudioFragmentListener;
import com.g3rdeveloper.lugares.fragments.ReproducirAudioFragment;
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
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MantoActivity extends ActionBarActivity implements ConnectionCallbacks,
									OnConnectionFailedListener,LocationListener,RecordAudioFragmentListener {
	
	private String type = "nvo";
	private LugarBean bean;
	private SQLiteDatabase db;
	private GoogleMap mMap;
    private LocationClient mLocationClient;
    private boolean primeraPosicionCargada = false;
    private boolean estaCargando = true;
    
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private static final int RESULT_LOAD_VIDEO = 300;
    
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
    EditText edtTitulo;
    TextView txvDireccion;
    Double latitud = 0d;
    Double longitud = 0d;
    int idFavorito = 0;
    String direccionRest="";
    String nameNewFile="";
    String[] files;
    GridView gridView;
    
    List<ItemsBean> data;
    
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
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_manto);
		
		setupActionBar();
		setSupportProgressBarIndeterminateVisibility(true);
		

		createDirIfNotExists(MainActivity.APP_DIRECTORY);
		createDirIfNotExists(MainActivity.APP_DIRECTORY+"/tmp");
		
		
		edtTitulo = (EditText)findViewById(R.id.edtTitulo);;
		txvDireccion = (TextView)findViewById(R.id.txvDireccionAproximada);
		
		try{
			Intent intent = getIntent();
            type = intent.getStringExtra(MainActivity.LUGAR_MTO);
            Log.d("CARGANDO",type);
            if(type==null){
            	type = "NVO";
            }
            if(type.equalsIgnoreCase("MTO")){
            	setTitulo("Editar");
				Bundle b = (Bundle) intent.getExtras();
				bean = (LugarBean) b.get(MainActivity.LUGAR_KEY);
				idFavorito = bean.getId();
				latitud = bean.getLatitud();
				longitud = bean.getLongitud();
				
				edtTitulo.setText(bean.getTitulo());
				txvDireccion.setText(bean.getDireccion());
            }else{
            	setTitulo("Agregar");
            }
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
		gridView = (GridView)findViewById(R.id.gdvExtrasManto);
		
		data = new ArrayList<ItemsBean>();
		
		ImageAdapter adaptador = new ImageAdapter(this,data);
		gridView.setAdapter(adaptador);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	ItemsBean item = data.get(position);
	        	Intent intent;
	        	switch(item.getTipo()){
	        	case MainActivity.TIPO_AUDIO:
	        		FragmentManager fragmentManager = getSupportFragmentManager();
	    			ReproducirAudioFragment dialogo = new ReproducirAudioFragment();
	    			dialogo.setUrlFile("/tmp/"+item.getUrl());
	    			dialogo.show(fragmentManager, "tagReproducir");
	        		break;
	        	case MainActivity.TIPO_FOTO:
	        		intent = new Intent(MantoActivity.this,MostrarImagenActivity.class);
	        		intent.putExtra(MainActivity.ID_KEY, 0);
	        		intent.putExtra(MainActivity.URL_KEY, "/tmp/"+item.getUrl());
	        		startActivity(intent);
	        		break;
	        	case MainActivity.TIPO_VIDEO:
	        		intent = new Intent(MantoActivity.this,MostrarVideoActivity.class);
	        		intent.putExtra(MainActivity.ID_KEY, 0);
	        		intent.putExtra(MainActivity.URL_KEY, "/tmp/"+item.getUrl());
	        		startActivity(intent);
	        		break;
	        	}
	        }
	    });
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
			break;
		case R.id.itmGuardarFavorito:
			if(latitud!=0 && longitud!=0 && !edtTitulo.getText().toString().equals("") && !estaCargando){
				guardarLugar();
			}else{
				Toast.makeText(this, "Completa los campos para poder guardar", Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.itmRefrescarMapa:
			estaCargando=true;
			primeraPosicionCargada=false;
			setSupportProgressBarIndeterminate(true);
            mLocationClient.connect();
			break;
		case R.id.itmAudio:
			FragmentManager fragmentManager = getSupportFragmentManager();
			RecordAudioFragment dialogo = new RecordAudioFragment();
			dialogo.show(fragmentManager, "tagAlerta");
			break;
		case R.id.itmFoto:
			llamarCamara(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			break;
		case R.id.itmVideo:
			llamarCamara(CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
			break;
		}
		return true;
	}
	
	private void llamarCamara(int codigo){
		String extension = (codigo==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)?".jpg":".mp4";
		String tipo = (codigo==CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)?MediaStore.ACTION_IMAGE_CAPTURE:MediaStore.ACTION_VIDEO_CAPTURE;
		
		Intent intent = new Intent(tipo);
		
		nameNewFile = System.currentTimeMillis()+extension;
		File image = new File(Environment.getExternalStorageDirectory(),MainActivity.APP_DIRECTORY+"/tmp/"+nameNewFile);
		Uri fileUri = Uri.fromFile(image); 
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); 
        startActivityForResult(intent, codigo);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manto, menu);
		return true;
	}
	
	
	public void guardarLugar(){
        try{
			int id;
        	SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
			db = sqliteHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("titulo", edtTitulo.getText().toString());
			values.put("direccion", txvDireccion.getText().toString());
			values.put("latitud", Double.valueOf(latitud));
			values.put("longitud", Double.valueOf(longitud));
			if(type.equals("NVO")){
				id = (int)db.insert("favorito", null, values);
			}else{
				id = bean.getId();
				db.update("favorito", values, "id="+bean.getId(), null);
			}
			guardarItems(id);
			
			db.close();
			Intent intent2 = new Intent();
			setResult(RESULT_OK, intent2);
			finish();
        }catch(Exception e){
        	Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }
	}
	
	private void guardarItems(int id){
		String[] tablas = new String[3];
		tablas[MainActivity.TIPO_AUDIO]="audio";
		tablas[MainActivity.TIPO_VIDEO]="video";
		tablas[MainActivity.TIPO_FOTO]="foto";
		for(int i=0;i<data.size();i++){
			ItemsBean d = data.get(i);
			ContentValues values = new ContentValues();
			values.put("referencia", d.getUrl());
			values.put("idfavorito", id);
			
			db.insert(tablas[d.getTipo()], null, values);
		}
	}
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Foto
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	agregarItem(nameNewFile,MainActivity.TIPO_FOTO);
            } else if (resultCode == RESULT_CANCELED) {
            	//
            } else {
            	Toast.makeText(this, "Error al tomar la foto", Toast.LENGTH_LONG).show();
            }
        }
		
		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	agregarItem(nameNewFile,MainActivity.TIPO_VIDEO);
            } else if (resultCode == RESULT_CANCELED) {
            	//
            } else {
                    Toast.makeText(this, "Error al tomar el Video", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == RESULT_LOAD_VIDEO ){
                Toast.makeText(this, "Error al tomar el Video", Toast.LENGTH_LONG).show();
        }
    }
	
	private void agregarItem(String archivo,int tipo){
		ItemsBean item = new ItemsBean();
		item.setId(0);
		item.setTipo(tipo);
		item.setUrl(archivo);
		data.add(item);
		ImageAdapter adaptador = new ImageAdapter(this,data);
		gridView.setAdapter(adaptador);
		nameNewFile="";
	}
	
	
	
	private void setUpMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        if(type.equalsIgnoreCase("MTO")){
        	ponerMarcador();
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
			if(type.equalsIgnoreCase("NVO")){
				latitud = location.getLatitude();
				longitud = location.getLongitude();
				ponerMarcador();
			}
		}
	}
	
	private void ponerMarcador(){
		LatLng latLng = new LatLng(latitud, longitud);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(latLng));
        buscarDireccion();
        primeraPosicionCargada=true;
	}
	
	private void buscarDireccion(){
		//http://maps.googleapis.com/maps/api/geocode/json?latlng=13.6682367,-89.2857764&sensor=false
		String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng="+
						Double.valueOf(latitud)+","+Double.valueOf(longitud)+"&sensor=false";
		Log.d("DIRECCION",url);
		new JSONTask().execute(url);
		
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

	@Override
	public void onRecordPositiveButton(String name) {
    	agregarItem(name,MainActivity.TIPO_AUDIO);
	}
    
    
    
    private class JSONTask extends AsyncTask<String, Void, String> {
    	protected void onPostExecute(String string) {
    		Log.d("DIRECCION","Ya cargo");
    		String readFeed = string;
    		try {
    			JSONObject jsonobj = new JSONObject(readFeed);
    			JSONArray jsonArray = jsonobj.getJSONArray("results");
    			Log.i(MainActivity.class.getName(), "Numero de Entradas " + jsonArray.length());
    			
    			JSONObject jsonObject = jsonArray.getJSONObject(0);
				direccionRest = jsonObject.getString("formatted_address");
				
    			txvDireccion.setText(direccionRest);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		estaCargando = false;
            mLocationClient.disconnect();
			setSupportProgressBarIndeterminateVisibility(false);
    	}
    	
    	@Override
    	protected String doInBackground(String... params) {
    		return readFeed(params[0]);
    	}

    	public String readFeed(String url) {
    		Log.d("DIRECCION","Empezando a cargar "+url);
    		StringBuilder builder = new StringBuilder();
    		HttpClient client = new DefaultHttpClient();
    		HttpGet httpGet = new HttpGet(url);
    		try {
    			HttpResponse response = client.execute(httpGet);
    			StatusLine statusLine = response.getStatusLine();
    			int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				HttpEntity entity = response.getEntity();
    				InputStream content = entity.getContent();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    				String line;
    				while ((line = reader.readLine()) != null) {
    					builder.append(line);
    				}
    			} else {
    				Log.e(MainActivity.class.toString(), "Failed to download file");
    			}
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		return builder.toString();
    	}
    }
    
    
    
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(Environment.getExternalStorageDirectory(), path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating Image folder");
                ret = false;
            }
        }
        return ret;
    }
    
    
    
    
    public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    private List<ItemsBean> items;

	    public ImageAdapter(Context c, List<ItemsBean> data) {
	        mContext = c;
	        items = data;
	    }

	    public int getCount() {
	        return items.size();
	    }

	    public ItemsBean getItem(int position) {
	        return items.get(position);
	    }

	    public long getItemId(int position) {
	        return items.get(position).getId();
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {
	            imageView = new ImageView(mContext);
	            GridView.LayoutParams layoutParams = new GridView.LayoutParams(85, 85);
	            imageView.setLayoutParams(layoutParams);
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(3, 3, 3, 3);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(mThumbIds[items.get(position).getTipo()]);
	        return imageView;
	    }

	    private Integer[] mThumbIds = {
	            R.drawable.grid_video,
	            R.drawable.grid_audio,
	            R.drawable.grid_image
	    };
	}

}
