package com.example.wechatsample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;


public class PostStarEyeReuquestActivity extends Activity implements OnGetPoiSearchResultListener {
    private static final String LTAG = PostStarEyeReuquestActivity.class.getSimpleName();

    AutoCompleteTextView etSearch = null;
    Button btSearch = null;
    Spinner sp_address = null;
    private ArrayAdapter<String> locAddapter = null;
    private ArrayAdapter<String> sugAdapter = null;
    private boolean mIsCity = false;
    Button btPostRequest = null;
    EditText etDescription = null;


    //For Baidu Location SDK
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    public PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    //OverlayOptions mMyMarkerOption = null;
    private Marker myMarker = null;
    InfoWindow mInfoWindow = null;


    //for store the search result string
    private String mSearchResutlJson = "";

    //For Baidu MAP SDK
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    LatLng mLatLng = null;
    TextView mTV = null;
    boolean isFirstLoc = true;
    LatLng mLatLngTarget = null;
    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();
            Log.d(LTAG, "action: " + s);
            //mTV.setTextColor(Color.RED);
            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                mTV.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
                mTV.setVisibility(View.VISIBLE);
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
            option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
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
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(new MyOnMapClickListener());

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(PostStarEyeReuquestActivity.this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(new MyOnGetSuggestionResultListener());

        //mMyMarkerOption = new MarkerOptions();
        btPostRequest = (Button)findViewById(R.id.bt_post_request);
        btPostRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null == mLatLngTarget)
                {
                    mTV.setText("请选择目的地！");
                    mTV.setVisibility(View.VISIBLE);
                    return;
                }
                else if(etDescription.getText().toString().equals(""))
                {
                    mTV.setText("请简单描述你的需求哦！");
                    mTV.setVisibility(View.VISIBLE);
                    return;
                }
                mTV.setVisibility(View.GONE);
                StarEyeInstance sei = new StarEyeInstance(mLatLngTarget.latitude,mLatLngTarget.longitude,etDescription.getText().toString());
                new doPostStarEyeInstanceTask().execute(sei);
            }
        });
        etDescription = (EditText)findViewById(R.id.et_description);

        sp_address = (Spinner)findViewById(R.id.spinner_location);

        mTV = (TextView) findViewById(R.id.tv_alerm_show);
        etSearch = (AutoCompleteTextView)findViewById(R.id.et_search_key);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */

        etSearch.addTextChangedListener(new MyTextWatcher());

        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        etSearch.setAdapter(sugAdapter);
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
                new DoBaiduPoiQueryTask().execute("世界");
                /*
                try
                {
                    mPoiSearch.searchInCity((new PoiCitySearchOption()).city(URLEncoder.encode("世界","UTF-8"))
                            .keyword(URLEncoder.encode(key,"UTF-8"))
                            .pageNum(10));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                */
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
    public void onGetPoiResult(PoiResult result){
        //获取POI检索结果
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(this, "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            mBaiduMap.clear();
            PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
            mBaiduMap.setOnMarkerClickListener(overlay);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public void onGetPoiDetailResult(PoiDetailResult result){
        //获取Place详情页检索结果
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
        unregisterReceiver(mReceiver);
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
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
        mBaiduMap.clear();
        myMarker = null;
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                Toast.makeText(getBaseContext(),"mMapView may be null",Toast.LENGTH_LONG).show();
                return;
            }
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            //定义Maker坐标点
            mLatLngTarget = new LatLng(location.getLatitude(), location.getLongitude());

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
    public class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            Log.d(LTAG,"onPoiClick");
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }
    public class MyOnGetSuggestionResultListener implements OnGetSuggestionResultListener{
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }
            sugAdapter.clear();
            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null)
                    sugAdapter.add(info.key);
            }
            sugAdapter.notifyDataSetChanged();
        }
    }
    public class MyTextWatcher implements TextWatcher{
        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            if (cs.length() <= 0) {
                return;
            }
            String city = "世界";
            /**
             * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
             */
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .keyword(cs.toString()).city(city));
        }
    }
    private class MyOnMapClickListener implements BaiduMap.OnMapClickListener{
        @Override
        public void onMapClick(LatLng point)
        {
            //Toast.makeText(getBaseContext(),point.toString(),Toast.LENGTH_LONG).show();
            Log.d(LTAG,point.toString());
            //在地图上添加Marker，并显示
            setTargetMarker(point);
        }
        @Override
        public boolean onMapPoiClick(MapPoi poi)
        {
            //Toast.makeText(getBaseContext(),poi.toString(),Toast.LENGTH_LONG).show();
            Log.d(LTAG,poi.toString());
            //在地图上添加Marker，并显示
            setTargetMarker(poi.getPosition());
            return true;
        }
    }
    public void setTargetMarker(LatLng point){
        mLatLngTarget = point;
        //在地图上添加Marker，并显示
        if(null == myMarker)
        {
            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.map_marker_outside_pink);
            //构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option = new MarkerOptions()
                    .position(point)
                    .icon(bitmap)
                    .zIndex(9)  //设置marker所在层级
                    .draggable(true);  //设置手势拖拽;
            myMarker = (Marker)mBaiduMap.addOverlay(option);
        }
        else
        {
            myMarker.setPosition(point);
        }
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
        mBaiduMap.animateMapStatus(u);
        showInforWindow(point);
    }
    public void showInforWindow(LatLng point)
    {
        TextView tv_temp = new TextView(getApplicationContext());
        tv_temp.setText("点击设置为目的地址");
        tv_temp.setBackgroundColor(Color.BLACK);
        tv_temp.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
        tv_temp.setOnClickListener(new MyOnInfoWindowClickListener());
        mInfoWindow = new InfoWindow(tv_temp, point, -60);
        //显示InfoWindow
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    public class MyOnInfoWindowClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            Toast.makeText(getBaseContext(),"设置成功！",Toast.LENGTH_LONG).show();
            mBaiduMap.hideInfoWindow();
        }
    }

    private class DoBaiduPoiQueryTask extends AsyncTask<String , Void, String> {
        protected String doInBackground(String... urls)
        {
            String result = "";
            try
            {
                result = WanEyeUtil.doGetBaiduPoiInfo(etSearch.getText().toString(), urls[0]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {

            //TODO parse json from result.
            if("" == result)
            {
                Toast.makeText(getBaseContext(),"搜索失败",Toast.LENGTH_LONG).show();
                Log.d(LTAG,"搜索失败");
                return;
            }
            mSearchResutlJson = result;
            //Toast.makeText(getBaseContext(),result,Toast.LENGTH_LONG).show();
            Log.d(LTAG,result);
            try
            {
                locAddapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,parseSearchJson(result));
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
            sp_address.setAdapter(locAddapter);
            sp_address.setOnItemSelectedListener(new MyOnItemSelectedListener());
            return;
        }
    }

    public ArrayList<String> parseSearchJson(String result) throws JSONException
    {
        ArrayList<String> as = new ArrayList<String>();
        JSONObject jo = new JSONObject(result);
        int status = jo.getInt("status");
        boolean citys = false;
        if(0 != status)
        {
            return null;
        }
        JSONObject jo_temp;
        JSONArray ja = jo.getJSONArray("results");
        for (int i = 0; i < ja.length(); i++)
        {
            jo_temp = ja.getJSONObject(i);
            if(jo_temp.length() > 2)
            {
                mIsCity = false;
                as.add(jo_temp.getString("address"));
            }
            else
            {
                if(!citys)
                {
                    citys = true;
                    mIsCity = true;
                }
                as.add(jo_temp.getString("name") + " results:" + jo_temp.getString("num"));
            }
        }
        return as;
    }
    public class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener
    {
        private boolean mCityFirstTime = false;
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Log.d(LTAG,"onItemSelected");
            int index = parent.getSelectedItemPosition();
            if(mIsCity)
            {
                if(!mCityFirstTime)
                {
                    //first time for City, Do nothing.
                    mCityFirstTime = true;
                    return;
                }
                try
                {
                    String city = getCityFromJson(mSearchResutlJson, index);
                    new DoBaiduPoiQueryTask().execute(city);
                    mCityFirstTime = false;
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
            else
            {
                try
                {
                    LatLng ll = getLatLngFromJson(mSearchResutlJson,index);
                    Log.d(LTAG,ll.toString());
                    setTargetMarker(ll);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            //nothing to do
        }
    }

    public LatLng getLatLngFromJson(String json, int pos) throws JSONException
    {
        double lat, lng;
        JSONObject jo = new JSONObject(json);
        int status = jo.getInt("status");
        boolean citys = false;
        if(0 != status)
        {
            return null;
        }
        JSONObject jo_temp;
        JSONObject jo_temp2;
        JSONArray ja = jo.getJSONArray("results");
        jo_temp = ja.getJSONObject(pos);
        jo_temp2 = jo_temp.getJSONObject("location");
        return new LatLng(jo_temp2.getDouble("lat"),jo_temp2.getDouble("lng"));
    }
    public String getCityFromJson(String json, int pos) throws JSONException
    {
        String city;
        JSONObject jo = new JSONObject(json);
        int status = jo.getInt("status");
        if(0 != status)
        {
            return "";
        }
        JSONObject jo_temp;
        JSONArray ja = jo.getJSONArray("results");
        jo_temp = ja.getJSONObject(pos);
        city = jo_temp.getString("name");
        return city;
    }

    private class doPostStarEyeInstanceTask extends AsyncTask<StarEyeInstance , Void, Integer>{
        protected Integer doInBackground(StarEyeInstance... seis)
        {
            Integer result = -1;
            try
            {
                result = WanEyeUtil.doPostStarEyeInstance(seis[0]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(Integer result)
        {
            if(result == HttpURLConnection.HTTP_OK)
            {
                mTV.setText("请求发送成功！");
            }
            else if(result == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                //重新登录
                mTV.setText("用户验证失败，请重新登录！");

            }
            else
            {
                mTV.setText("请求失败，请重试！");
            }
            mTV.setVisibility(View.VISIBLE);
        }
    }
}
