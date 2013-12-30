package com.g3rdeveloper.lugares;

import com.g3rdeveloper.lugares.sqlite.SQLiteHelper;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemClickListener {
	
	private ActionBar ab;
	private SQLiteDatabase db = null;
	private ListView listView;
	
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
    	Cursor cursor = db.rawQuery("select rowid _id,code, name, age from students", null);
        String[] from = {"code","name","age"};
        
        int[] to = {R.id.txvTitulo,R.id.txvDireccion};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.favorito_layout, cursor, from, to,0);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
		// TODO Auto-generated method stub
		
	}
    
}
