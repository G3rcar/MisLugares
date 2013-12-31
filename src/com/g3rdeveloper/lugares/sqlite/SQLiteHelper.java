package com.g3rdeveloper.lugares.sqlite;

import com.g3rdeveloper.lugares.beans.LugarBean;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists " +
                "favorito(id integer primary key autoincrement, titulo text, direccion text,"+
				"latitud text, longitud text);");
		db.execSQL("create table if not exists " +
                "foto(id integer primary key autoincrement, idfavorito integer, referencia text);");
		db.execSQL("create table if not exists " +
                "audio(id integer primary key autoincrement, idfavorito integer, referencia text);");
		db.execSQL("create table if not exists " +
                "video(id integer primary key autoincrement, idfavorito integer, referencia text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public void nuevoLugar(LugarBean lugar){
		
	}

}
