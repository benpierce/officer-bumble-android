package com.example.officerbumble;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

import com.example.officerbumble.engine.SoundManager;
import com.example.officerbumble.services.AdMobServices;
import com.example.officerbumble.interfaces.AdListener;
import com.sromku.simple.fb.SimpleFacebook;

public class MainActivity extends Activity implements AdListener {

    // Ad Specific States
    public enum AD_TYPE {
        NONE(0), BOTTOM_BANNER(1), TOP_BANNER(2), RECTANGLE(3), BOTTOM_BANNER_AND_RECTANGLE(4), INTERSTITIAL(5);

        private final int value;
        private AD_TYPE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

	private GLSurfaceView glSurfaceView;        // Holds our GL Context
    private RelativeLayout m_layout;
    private GameRenderer m_gameRenderer;        // Rendering class
	private SimpleFacebook m_simpleFacebook;    // Facebook Integration
	private AdMobServices m_adMobServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        m_simpleFacebook = SimpleFacebook.getInstance(this);        // Setup Facebook Connection
        getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);     // Keep the screen on at all times
        glSurfaceView = new GLSurfaceView(this);                    // Create new GLSurfaceView for rendering

        // Configure game ads and their layouts.
        m_layout = SetupGameLayout();
        m_adMobServices = new AdMobServices(this);

        // This view contains both the GLSurfaceView and the AdViews.
        m_gameRenderer = new GameRenderer(this, m_simpleFacebook, this);

        if(SupportsES2()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setPreserveEGLContextOnPause(true);   // So we don't lose our Open GL context if possible.

        	// Assign our renderer.
        	glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        	glSurfaceView.setRenderer(m_gameRenderer);

        	glSurfaceView.setOnTouchListener(new OnTouchListener() {
			    @Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
        		    if(arg1 != null) {
                        // get masked (not specific to a pointer) action
                        int maskedAction = arg1.getActionMasked();

        				if(maskedAction == MotionEvent.ACTION_DOWN || maskedAction == MotionEvent.ACTION_POINTER_DOWN) {
                            // get pointer index from the event object
                            int pointerIndex = arg1.getActionIndex();

                            // get pointer ID
                            int pointerId = arg1.getPointerId(pointerIndex);
                            final boolean isPrimary = (pointerId == 0);

                            // Convert touch coordinates into normalized device coordinates, keeping in mind that Android's Y coordinates are inverted.
                            final float normalizedX = (arg1.getX(pointerIndex) / (float) arg0.getWidth()) * 2 - 1;
                            final float normalizedY = -((arg1.getY(pointerIndex) / (float)arg0.getHeight()) * 2 - 1);

        					glSurfaceView.queueEvent(new Runnable() {
        					    @Override
        						public void run() {
        					        m_gameRenderer.handleTouchPress(isPrimary, normalizedX, normalizedY);
        					    }
        				    });
        				} else if (maskedAction == MotionEvent.ACTION_UP || maskedAction == MotionEvent.ACTION_POINTER_UP){
                            // get pointer index from the event object
                            int pointerIndex = arg1.getActionIndex();

                            // get pointer ID
                            int pointerId = arg1.getPointerId(pointerIndex);
                            final boolean isPrimary = (pointerId == 0);

                            // Convert touch coordinates into normalized device coordinates, keeping in mind that Android's Y coordinates are inverted.
                            final float normalizedX = (arg1.getX(pointerIndex) / (float) arg0.getWidth()) * 2 - 1;
                            final float normalizedY = -((arg1.getY(pointerIndex) / (float)arg0.getHeight()) * 2 - 1);

                            glSurfaceView.queueEvent(new Runnable() {
        					    @Override
        						public void run() {
        					        m_gameRenderer.handleTouchRelease(isPrimary, normalizedX, normalizedY);
        					    }
                            });
        				}

                        // Sleep UI thread so that we don't get input flooding.
                        try {
                            Thread.sleep(16);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

        			    return true;
        			} else {
        			    return false;
        			}
				}   // End onTouch
            }   // End OnTouchListener
            );  // End SetOnTouchListener

            setContentView(m_layout);
        } else {
        	Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_LONG).show();
        }
    }

    // Setup the game's ad layouts (not valid anymore, but we'll leave it in case we want banner ads down the road).
    private RelativeLayout SetupGameLayout() {
        RelativeLayout layout = new RelativeLayout(this);

        layout.addView(glSurfaceView);

        return layout;
    }

    @Override
    protected void onPause() {
    	SoundManager.PauseMusic();

    	super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    	SoundManager.StopMusic();
    }
        
    @Override
    protected void onResume() {
    	super.onResume();

    	m_simpleFacebook = SimpleFacebook.getInstance(this);
    	
    	glSurfaceView.onResume();
        m_gameRenderer.UpdateContext(this);

    	SoundManager.ResumeMusic();
    }
    
    private boolean SupportsES2() {
    	final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    	final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

        return configurationInfo.reqGlEsVersion >= 0x20000 ||
    			(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 &&     			    
    			(Build.FINGERPRINT.startsWith("generic") || 
    			     Build.FINGERPRINT.startsWith("Unknown") || 
    			     Build.FINGERPRINT.contains("google_sdk") || 
    			     Build.FINGERPRINT.contains("Emulator") || 
    			     Build.MODEL.contains("Android SDK built for x86")
    			 ));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    	m_simpleFacebook.onActivityResult(this, requestCode, resultCode, data); 
    }     

    /*
        Part of the ad listener implementation. Has to happen in here because we need the UI thread for this.
    */
	@Override
	public void HandleAdVisibility(AD_TYPE _adType) {
        switch(_adType) {
            case NONE:
                break;  // Do nothing
            case INTERSTITIAL:
                ShowInterstitialAd();
                break;
            default:
                Log.w("ADMOB", "Whoops, there's no ad type for " + _adType.toString());
                break;
        }
	}

    // Has to run on the main UI thread.
    private void ShowInterstitialAd() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                m_adMobServices.ShowAd();
            }
        });
    }
}