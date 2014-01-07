package com.g3rdeveloper.lugares;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MostrarImagenActivity extends Activity {
	
	int idRecurso;
	
	Uri uriImagen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mostrar_imagen);
        
        try{
			Intent intent = getIntent();
			Bundle b = (Bundle) intent.getExtras();
            idRecurso = b.getInt(MainActivity.ID_KEY, 0);
            
            
            if(idRecurso==0){
            	String path = b.getString(MainActivity.URL_KEY);
            	uriImagen = Uri.parse(Environment.getExternalStorageDirectory()+MainActivity.APP_DIRECTORY+path);
            }else{
            	buscarUri(idRecurso);
            }
            
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
        
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(uriImagen);
        viewPager.setAdapter(adapter);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}
	
	private void buscarUri(int id){
		//uriImagen
	}
	
	private class ImagePagerAdapter extends PagerAdapter {

        private Uri[] mUrls = new Uri[1];
        String[] mFiles = new String[1];

        public ImagePagerAdapter(Uri imagen) {
                mUrls[0]=imagen;
                mFiles[0]=imagen.toString();
        }

        @Override
        public int getCount() {
            return mFiles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
	        Context context = MostrarImagenActivity.this;
	        ImageView imageView = new ImageView(context);
	        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
	        imageView.setPadding(padding, padding, padding, padding);
	        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
	        imageView.setImageURI(mUrls[position]);
	        ((ViewPager) container).addView(imageView, 0);
	        return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((ImageView) object);
        }
                
	}
	

}
