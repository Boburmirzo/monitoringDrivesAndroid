package ru.likada.monitoringdriver.util;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import ru.likada.monitoringdriver.R;

/**
 * Created by bumur on 18.05.2017.
 */

public class CustomMapFragmentForEventDetails extends SupportMapFragment {
    SupportMapFragment mSupportMapFragment;
    GoogleMap googleMap;
    OnMapReadyListener mOnMapReadyListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.v("Inside CustomMapFrag", "Success");
        View root = inflater.inflate(R.layout.google_map, null, false);
        initilizeMap();
        return root;
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mOnMapReadyListener = (OnMapReadyListener) activity;
    }

    private void initilizeMap()
    {
        mSupportMapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.map, mSupportMapFragment).commit();
        }
        if (mSupportMapFragment != null)
        {
//            googleMap = mSupportMapFragment.getMap();
            if (googleMap != null)

            {
                mOnMapReadyListener.onMapReady(googleMap);
            }

        }
    }
    public static interface OnMapReadyListener {

        void onMapReady(GoogleMap googleMap);
    }
}