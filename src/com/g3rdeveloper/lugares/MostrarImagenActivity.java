package com.g3rdeveloper.lugares;

//import java.io.File;
//import java.io.FilenameFilter;

import android.net.Uri;
import android.os.Bundle;
//import android.os.Environment;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MostrarImagenActivity extends ActionBarActivity {
	
	Uri uriImagen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mostrar_imagen);
        setupActionBar();

		buscarUri(0);
        
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter(uriImagen);
        viewPager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mostrar_imagen, menu);
		return true;
	}
	
	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
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

        private Uri[] mUrls;
        String[] mFiles = null;

        public ImagePagerAdapter(Uri imagen) {
                mUrls[0]=imagen;
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
                // imageView.setImageResource(mImages[position]);
                ((ViewPager) container).addView(imageView, 0);
                return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((ImageView) object);
        }
        
        /*
        public void onLoadpictures() {
                File images = new File(Environment.getExternalStorageDirectory(),"MyPictures");
                File[] imagelist = images.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                                return (name.endsWith(".jpg"));
                        }
                });
                mFiles = new String[imagelist.length];
                for (int i = 0; i < imagelist.length; i++) {
                        mFiles[i] = imagelist[i].getAbsolutePath();
                }
                mUrls = new Uri[mFiles.length];

                for (int i = 0; i < mFiles.length; i++) {
                        mUrls[i] = Uri.parse(mFiles[i]);
                }
        }
        */
	}
	

}
