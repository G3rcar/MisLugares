package com.g3rdeveloper.lugares;

import com.g3rdeveloper.lugares.beans.LugarBean;
import com.g3rdeveloper.lugares.fragments.ConfirmFragment;
import com.g3rdeveloper.lugares.fragments.ConfirmFragment.ConfirmFragmentListener;
import com.g3rdeveloper.lugares.sqlite.SQLiteHelper;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemClickListener,ConfirmFragmentListener {
	
	private ActionBar ab;
	private SQLiteDatabase db = null;
	private ListView listView;
	private MenuItem actBtnBusqueda;
	private SimpleCursorAdapter adapter;
	private int idEnOperacion;
	public static final String LUGAR_VER = "com.g3rdeveloper.lugares.VISUALIZAR";
	public static final String LUGAR_KEY = "com.g3rdeveloper.lugares.LUGAR";
	public static final String LUGAR_MTO = "com.g3rdeveloper.lugares.LUGARMTO";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        ab = getSupportActionBar();
        ab.setTitle(getString(R.string.title_favoritos));
        
        listView = (ListView)findViewById(R.id.lstFavoritos);
        listView.setOnItemClickListener(this);
        iniciarBD();
        
        registerForContextMenu(listView);
        
    }
    
    private void iniciarBD(){
    	try{
    		SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
            db = sqliteHelper.getWritableDatabase();
            cargarLista();
            db.close();
    	}catch(SQLException sqlerror){        
            Toast.makeText(this, "DB: "+sqlerror.getMessage(), Toast.LENGTH_LONG).show();
            sqlerror.printStackTrace();
    	}catch(Exception e){
            Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
    	}
    }
    
    private void cargarLista() throws Exception{
    	Cursor cursor = db.rawQuery("select rowid _id,id, titulo, direccion, latitud, longitud from favorito", null);
        String[] from = {"id","titulo","direccion","latitud","longitud"};
        
        int[] to = {R.id.txvIdFavorito,R.id.txvTituloFavorito,R.id.txvDireccionFavorito,R.id.txvLatitud,R.id.txvLongitud};
        adapter = new SimpleCursorAdapter(this, R.layout.favorito_layout, cursor, from, to,0);
        listView.setAdapter(adapter);
        if(cursor.getCount()==0){
        	listView.setEmptyView((TextView)findViewById(R.id.txvNoResultado));
        }
    }
    
    
    private void initBorrarItem(int id){
    	idEnOperacion = id;
		FragmentManager fragmentManager = getSupportFragmentManager();
		ConfirmFragment dialogo = new ConfirmFragment();
		dialogo.setMessage("¿Estás seguro de borrar el registro seleccionado?");
        dialogo.show(fragmentManager, "tagAlerta");
    }
    
    private void borrarItem(int id){
    	SQLiteHelper sqliteHelper = new SQLiteHelper(this, "lugares.db", null, 1);
        db = sqliteHelper.getWritableDatabase();
    	db.delete("favorito", "id="+id, null);
    	db.close();
    	iniciarBD();
    }

	public void verItem(int id){
		Intent intent = new Intent(this, VisualizadorActivity.class);
        intent.putExtra(LUGAR_VER, ""+id);
        startActivity(intent);
	}
    
    
    
    

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.favoritos_contextual, menu);
	}
    
    @Override
	public void onDialogPositiveButton() {
		borrarItem(idEnOperacion);
		idEnOperacion=0;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Cursor cursor = (Cursor)listView.getItemAtPosition(info.position);
		switch(item.getItemId()){
		case R.id.itmBorrar:
	        initBorrarItem(cursor.getInt(1));
			break;
		case R.id.itmVer:
			verItem(cursor.getInt(1));
			break;
		case R.id.itmModificar:
			Bundle b = new Bundle();
            Intent intent = new Intent(this, MantoActivity.class);
            LugarBean bean = new LugarBean();
            bean.setId(cursor.getInt(1));
            bean.setTitulo(cursor.getString(2));
            bean.setDireccion(cursor.getString(3));
            bean.setLatitud(cursor.getDouble(4));
            bean.setLongitud(cursor.getDouble(5));
            
            b.putSerializable(LUGAR_KEY, bean);
            intent.putExtras(b);
            intent.putExtra(LUGAR_MTO, "MTO");
            startActivityForResult(intent,2);
            
			break;
		}
		return true;
	}
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favoritos, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		actBtnBusqueda = (MenuItem) menu.findItem(R.id.itmMenuSearch);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(actBtnBusqueda);
        
        if (null != searchView ){
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);   
        }

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener(){
            public boolean onQueryTextChange(String newText){
            	adapter.getFilter().filter(newText);
            	return true;
            }
            public boolean onQueryTextSubmit(String query){
            	adapter.getFilter().filter(query);
        		return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.itmNuevoLugar:
			Intent intent = new Intent(this, MantoActivity.class);
            intent.putExtra(LUGAR_MTO, "NVO");
            startActivityForResult(intent, 1);	
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
		// TODO Auto-generated method stub
		Cursor cursor = (Cursor)listview.getItemAtPosition(position);
		verItem(cursor.getInt(1));
	}
	
	
	@Override
    public void onActivityResult(int inputCode, int resultCode, Intent intent2){
		if(resultCode==RESULT_OK){
			iniciarBD();
		}
	}

}
