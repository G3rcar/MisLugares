package com.g3rdeveloper.lugares;

import com.g3rdeveloper.lugares.sqlite.SQLiteHelper;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {
	
	private ActionBar ab;
	private SQLiteDatabase db = null;
	private ListView listView;
	private MenuItem actBtnBusqueda;
	private SimpleCursorAdapter adapter;
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
    	Cursor cursor = db.rawQuery("select rowid _id,id, titulo, direccion from favorito", null);
        String[] from = {"id","titulo","direccion"};
        
        int[] to = {R.id.txvIdFavorito,R.id.txvTituloFavorito,R.id.txvDireccionFavorito};
        adapter = new SimpleCursorAdapter(this, R.layout.favorito_layout, cursor, from, to,0);
        listView.setAdapter(adapter);
        if(cursor.getCount()==0){
        	listView.setEmptyView((TextView)findViewById(R.id.txvNoResultado));
        }
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
			Intent intent = new Intent(this, AgregarActivity.class);
            intent.putExtra(LUGAR_MTO, "NVO");
            startActivityForResult(intent, 1);	
		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
		// TODO Auto-generated method stub
		Cursor cursor = (Cursor)listview.getItemAtPosition(position);
		Toast.makeText(this, cursor.getString(1), Toast.LENGTH_SHORT).show();
	}
	
	
	@Override
    public void onActivityResult(int inputCode, int resultCode, Intent intent2){
		if(resultCode==RESULT_OK){
			iniciarBD();
		}
	}
    
}
