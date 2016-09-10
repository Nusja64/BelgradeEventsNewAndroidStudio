package com.example.nikola.belgradeevents;






import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



public class SplashScreenActivity extends AppCompatActivity {
	
	ImageView image;
	Animation animMove;

	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initToolbar();
        init();
        setAnimation();


        
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreenActivity.this,BelgradeMapsActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, 1500);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.drawable.my_borders);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Welcome");
        title.setTextSize(24);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }
	private void init() {
		// TODO Auto-generated method stub
		image = (ImageView) findViewById(R.id.imageViewWlcome);
	       
	      
	        
	        
		
	}

		
	


	private void setAnimation() {
		// TODO Auto-generated method stub
		TranslateAnimation tAnimation = new TranslateAnimation(0, 0 , 180, 0);
        tAnimation.setDuration(1200);
        tAnimation.setRepeatCount(0);
        tAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        tAnimation.setFillAfter(true);
        tAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            	
                
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
            
                
            }
        });

        image.startAnimation(tAnimation);
        
        
//       
		
	}


        
    }




  

