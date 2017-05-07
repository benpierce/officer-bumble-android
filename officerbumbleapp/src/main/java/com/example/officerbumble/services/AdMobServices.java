package com.example.officerbumble.services;

import com.google.android.gms.ads.*;
import android.content.Context;
import com.example.officerbumble.R;
import android.util.Log;
import java.util.Random;

/*
===============================================================================

	AdMobServices is responsible for loading ads from Google Play, caching them,
	and allowing the caller to display the cached ad at any time.

===============================================================================
*/
public class AdMobServices {

    // Ad constants.
    private static final boolean ENABLED = true;    // Whether or not ads are even enabled.
    private static final int AD_CHANCE = 99;        // % chance that an ad will show - fill rate should take care of ads not showing all the time :)

    // These are all needed in order to cache and display ads.
    private InterstitialAd m_interstitial;
    private Context m_context;
    private String m_adUnitId;
    private int m_errorCode = -1;
    private Random m_rand = new Random();

    public AdMobServices(Context _context) {
        m_context = _context;
        m_adUnitId = _context.getResources().getString(R.string.adunitid);

        PrepareAd();    // Get our first ad cached up.
    }

    /*
        ====================
        ShowAd

        The only publicly visible method to this class - call this whenever there's a possibility
        of showing an ad.

        So that we're not annoying our players with ads, we're going to use the AD_CHANCE constant (which is an
        integer percentage) to determine if we should show the ad or not. For instance, if AD_CHANCE is 50, then
        there's a 50% chance that an ad call will produce an ad.

        NOTE: This method MUST be called from the UI thread or an exception will be thrown by Google Play Service.
        ====================
    */
    public void ShowAd() {
        // Generate a random number between 1 and 100.
        int randomNum = m_rand.nextInt((100 - 1) + 1) + 1;

        // Only show the ad randomly so we don't annoy the crap out of our players.
        if( ENABLED && randomNum <= AD_CHANCE ) {
            if ( m_interstitial.isLoaded() ) {
                m_interstitial.show();
            } else {
                Log.w("ADMOB", "Unable to display ad because it wasn't loaded!");
            }
        }
    }

    /*
        ====================
        PrepareAd

        This function does all of the heavy lifting in terms of preparing an ad request, taking
        care of adding test devices, setting callbacks, and setting the error responses.
        ====================
    */
    private void PrepareAd() {
        if( ENABLED ) {
            // Create the interstitial.
            m_interstitial = new InterstitialAd(m_context);
            m_interstitial.setAdUnitId(m_adUnitId);

            // Create ad request.
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice("EB4BA455D3CCAAEF47CB024A6E661F90")
                    .addTestDevice("9BA5F5A006243592A71CA05A649CA7E3")
                    .addTestDevice("FC60905816CCE1658927DC2F044FF9BC")
                    .addTestDevice("5EC5EEC49CB89E10D63BB0BCE14FAD28")
                    .build();

            m_interstitial.setAdListener(new com.google.android.gms.ads.AdListener() {
                public void onAdLoaded() {
                    m_errorCode = -1;
                    Log.w("ADMOB", "Ad loaded successfully!");
                }

                public void onAdFailedToLoad(int errorCode) {
                    m_errorCode = errorCode;
                    Log.w("ADMOB", "Error loading the ad: " + GetLastError());
                }

                public void onAdClosed() {
                    PrepareAd();   // Load a new ad.
                }
            });

            m_interstitial.loadAd(adRequest);
        }
    }

   private String GetLastError() {
        String error = "";

        switch( m_errorCode ) {
            case -1:
                error = "No error";
                break;
            case 0:
                error = "There was an internal error loading ad! Ad not loaded.";
                break;
            case 1:
                error = "There was an invalid ad request! Ad not loaded.";
                break;
            case 2:
                error = "There was a network error loading the ad request! Ad not loaded.";
                break;
            case 3:
                error = "There was no fill for the ad request! Ad not loaded.";
                break;
            default:
                error = "Unknown error! Ad not loaded.";
                break;
        }

        return error;
    }
}
