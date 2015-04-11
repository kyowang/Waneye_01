package com.example.wechatsample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;


public class PostStarEyeReuquestActivity extends Activity {
    private static final String LTAG = PostStarEyeReuquestActivity.class.getSimpleName();

    EditText etSearch = null;
    Button btSearch = null;
    //For Baidu Location SDK
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();

    //For Baidu MAP SDK
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    LatLng mLatLng = null;
    TextView mTV = null;
    boolean isFirstLoc = true;
    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(LTAG, "action: " + s);
            mTV = (TextView) findViewById(R.id.tv_alarm);
            //mTV.setTextColor(Color.RED);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                mTV.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                mTV.setText("网络出错");
                mTV.setVisibility(View.VISIBLE);
            }
            else
            {
                mTV.setVisibility(View.INVISIBLE);
            }
        }
    }
    private SDKReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            mLatLng = new LatLng(b.getDouble("y"), b.getDouble("x"));
            if (isFirstLoc) {
                isFirstLoc = false;
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
                mBaiduMap.animateMapStatus(u);
            }
        }
        else
        {
            //声明LocationClient类
            mLocationClient = new LocationClient(getApplicationContext());
            //注册监听函数
            mLocationClient.registerLocationListener( myListener );
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
            option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
            //Don't set ScanSpan, do one time location
            //option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
            option.setIsNeedAddress(true);//返回的定位结果包含地址信息
            option.setNeedDeviceDirect(false);//返回的定位结果包含手机机头的方向
            mLocationClient.setLocOption(option);
            mLocationClient.start();
            mLocationClient.requestLocation();
        }

        setContentView(R.layout.activity_post_star_eye_reuquest);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        etSearch = (EditText)findViewById(R.id.et_search_key);
        btSearch = (Button)findViewById(R.id.bt_search);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etSearch.getText().toString().equals(""))
                {
                    mTV.setText("请输入关键字");
                    mTV.setVisibility(View.VISIBLE);
                    return;
                }
                mTV.setVisibility(View.INVISIBLE);
                String key = etSearch.getText().toString();

            }
        });
        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post_star_eye_reuquest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
            mLocationClient.stop();
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
}
