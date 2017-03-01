package com.help.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.amap.api.maps.AMap;

/**
 * Created by Administrator on 2017/2/20.
 */
public class BaseMapFragmentActivity extends FragmentActivity {

    private AMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basemap_fragment_activity);
//        setUpMapIfNeeded();
    }

}
