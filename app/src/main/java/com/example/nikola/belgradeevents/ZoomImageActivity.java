package com.example.nikola.belgradeevents;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Text;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ZoomImageActivity extends AppCompatActivity {
	
	ImageView imageView;
	TextView textView;
	RelativeLayout comentLayout;
	Bitmap bitmap;
	URL newurl;
	
	String comm ="";
	String url="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zoom_image);

		init();
		initToolbar();
		setData();
	}

	private void initToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarZoom);
		setSupportActionBar(toolbar);
		toolbar.setBackgroundResource(R.drawable.my_borders);
		TextView title = (TextView) toolbar.findViewById(R.id.toolbar_titleZoom);
		title.setText("Image");

		title.setTextSize(24);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			//	Toast.makeText(ZoomImageActivity.this,"asasa", Toast.LENGTH_SHORT).show();
					finish();
			}
		});

	}

	private void setData() {
		// TODO Auto-generated method stub

		if (comm.equalsIgnoreCase("null")|| comm.isEmpty()) {
			comentLayout.setVisibility(View.INVISIBLE);
			textView.setText("");
		}
		else
		{
			comentLayout.setVisibility(View.VISIBLE);
			textView.setText(comm);
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					newurl = new URL(url);

					bitmap = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());

					runOnUiThread(new Runnable() {
						
								        @Override
								        public void run() {
								        	
								        	imageView.setImageBitmap(bitmap);;
								        	
								        }
								    });
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}) ;
		
			thread.start();

		
	}

	private void init() {
		// TODO Auto-generated method stub

		comentLayout = (RelativeLayout) findViewById(R.id.rlComent);
		comm = getIntent().getExtras().getString("comment");
		url = getIntent().getExtras().getString("image");
		
		imageView = (ImageView) findViewById(R.id.imageViewZoom);
		textView = (TextView) findViewById(R.id.txtComment);
		
	}

}
