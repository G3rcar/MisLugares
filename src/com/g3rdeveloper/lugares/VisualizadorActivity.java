package com.g3rdeveloper.lugares;

import java.util.ArrayList;
import java.util.List;

import com.g3rdeveloper.lugares.beans.ItemsBean;
import com.g3rdeveloper.lugares.fragments.ConfirmFragment;
import com.g3rdeveloper.lugares.fragments.ReproducirAudioFragment;
import com.g3rdeveloper.lugares.fragments.ConfirmFragment.ConfirmFragmentListener;
import com.g3rdeveloper.lugares.sqlite.SQLiteHelper;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class VisualizadorActivity extends ActionBarActivity implements ConfirmFragmentListener {
	
	private GoogleMap mMap;
	private SQLiteDatabase db = null;
	
	private int idFavorito = 0;
	private TextView txvTitulo;
	private TextView txvDireccion;
	private Double latitud;
	private Double longitud;
	
    List<ItemsBean> data;
	
	@Override
    protected void onResume() {
        super.onResume();
        setUpMap();
    }
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizador);
		setupActionBar();
		setUpMap();

		try{
			Intent intent = getIntent();
            idFavorito = Integer.parseInt(intent.getStringExtra(MainActivity.LUGAR_VER));
            Log.d("CARGANDO",""+idFavorito);
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
		txvTitulo = (TextView)findViewById(R.id.txvTitulo);
		txvDireccion = (TextView)findViewById(R.id.txvDireccionAproximada);
        cargarDatos();
	}
	
	private void initBorrarItem(int id){
    	idFavorito = id;
		FragmentManager fragmentManager = getSupportFragmentManager();
		ConfirmFragment dialogo = new ConfirmFragment();
		dialogo.setMessage("¿Estás seguro de borrar el registro seleccionado?");
        dialogo.show(fragmentManager, "tagAlerta");
    }
	
	private void cargarDatos(){
		SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
        db = sqliteHelper.getWritableDatabase();
        String[] campos = new String[] {"titulo", "direccion", "latitud", "longitud"};
		Cursor cursor = db.query("favorito", campos, "id="+idFavorito, null, null, null, null);
		cursor.moveToFirst();
		
		txvTitulo.setText(cursor.getString(0));
        txvDireccion.setText(cursor.getString(1));
        latitud = cursor.getDouble(2);
        longitud = cursor.getDouble(3);
        ponerMarcador();
        llenarGrid();

		db.close();
		sqliteHelper.close();
	}
	
	private void llenarGrid(){
		data = new ArrayList<ItemsBean>();
		
		Cursor cursor = db.rawQuery("select id,referencia,2 tipo from foto where idfavorito = "+idFavorito+
									" union all select id,referencia,1 tipo from audio where idfavorito = "+idFavorito+
									" union all select id,referencia,0 tipo from video where idfavorito = "+idFavorito, null);
		if (cursor.moveToFirst()) {
            do {
            	ItemsBean item = new ItemsBean();
            	item.setId(cursor.getInt(0));
            	item.setTipo(cursor.getInt(2));
            	item.setUrl(cursor.getString(1));
            	data.add(item);
            } while(cursor.moveToNext());
        }
		
		
		GridView gridView = (GridView)findViewById(R.id.gdvExtras);
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
	        		intent = new Intent(VisualizadorActivity.this,MostrarImagenActivity.class);
	        		intent.putExtra(MainActivity.ID_KEY, 0);
	        		intent.putExtra(MainActivity.URL_KEY, "/tmp/"+item.getUrl());
	        		startActivity(intent);
	        		break;
	        	case MainActivity.TIPO_VIDEO:
	        		intent = new Intent(VisualizadorActivity.this,MostrarVideoActivity.class);
	        		intent.putExtra(MainActivity.ID_KEY, 0);
	        		intent.putExtra(MainActivity.URL_KEY, "/tmp/"+item.getUrl());
	        		startActivity(intent);
	        		break;
	        	}
	        }
	    });
	}
	
	private void ponerMarcador(){
		LatLng latLng = new LatLng(latitud, longitud);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);
        mMap.addMarker(new MarkerOptions().position(latLng));
	}
	
	private void borrarItem(int id){
    	SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
        db = sqliteHelper.getWritableDatabase();
    	db.delete("favorito", "id="+id, null);
    	db.close();
    	Toast.makeText(this, "El registro se ha eliminado", Toast.LENGTH_SHORT).show();
    	

		Intent intent2 = new Intent();
		setResult(RESULT_OK, intent2);
    	finish();
    }
	
	private void setUpMap() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }
	
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.visualizador, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		case R.id.itmBorrar:
	        initBorrarItem(idFavorito);
	        break;
		/*
		case R.id.itmModificar:
			Bundle b = new Bundle();
            Intent intent = new Intent(this, MantoActivity.class);
            LugarBean bean = new LugarBean();
            bean.setId(idFavorito);
            bean.setTitulo(txvTitulo.getText().toString());
            bean.setDireccion(txvDireccion.getText().toString());
            bean.setLatitud(latitud);
            bean.setLongitud(longitud);
            
            b.putSerializable(MainActivity.LUGAR_KEY, bean);
            intent.putExtras(b);
            intent.putExtra(MainActivity.LUGAR_MTO, "MTO");
            startActivityForResult(intent,2);
			break;
		*/
		}
        return true;
	}

	@Override
	public void onDialogPositiveButton() {
		borrarItem(idFavorito);
	}
	
	@Override
    public void onActivityResult(int inputCode, int resultCode, Intent intent2){
		if(resultCode==RESULT_OK){
			cargarDatos();
		}
	}
	
	
	
	
	
	/*
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;

	    public ImageAdapter(Context c) {
	        mContext = c;
	    }

	    public int getCount() {
	        return mThumbIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
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

	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	    private Integer[] mThumbIds = {
	            R.drawable.grid_video,
	            R.drawable.grid_image,
	            R.drawable.grid_audio,
	            R.drawable.grid_video,
	            R.drawable.grid_video,
	            R.drawable.grid_image,
	            R.drawable.grid_audio,
	            R.drawable.grid_image
	    };
	}*/
	
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
