package com.example.wechatsample;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

/**
 * 高仿微信的主界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class MainActivity extends FragmentActivity {
    private static final String LTAG = MainActivity.class.getSimpleName();
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private LocationPara mLp = null;
	/**
	 * 主界面Fragment
	 */
	private ChatFragment chatFragment;

	/**
	 * 发现界面的Fragment
	 */
	private FoundFragment foundFragment;

	/**
	 * 消息界面的Fragment
	 */
	private ContactsFragment contactsFragment;

	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

		setContentView(R.layout.activity_main);
		setOverflowShowingAlways();
		dm = getResources().getDisplayMetrics();
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		setTabsValue();

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener( myListener );
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        //Don't set ScanSpan, do one time location
        option.setScanSpan(10000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(false);//返回的定位结果包含手机机头的方向
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        mLocationClient.requestLocation();


        //get cookie from sharedPref xml
        AppCommonData comData = new AppCommonData(MainActivity.this);
        String auth ;
        auth = comData.getString("authCookie","");
        //Log.d(LTAG,auth);
        if(auth != "")
        {
            Log.d(LTAG,"restore Cookie success!");
            Log.d(LTAG,auth);
            WanEyeUtil.setCookieAuth(auth);
        }
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.d(LTAG,""+maxMemory);
	}

	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#33b5e5"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#33b5e5"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		//private final String[] titles = { "首页", "发现", "消息" };
		private final String[] titles = { "首页", "附近" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (chatFragment == null) {
					chatFragment = new ChatFragment();
				}
				return chatFragment;
			case 1:
				if (foundFragment == null) {
					foundFragment = new FoundFragment();
				}
				return foundFragment;
			case 2:
				if (contactsFragment == null) {
					contactsFragment = new ContactsFragment();
				}
				return contactsFragment;
			default:
				return null;
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(this,"come on 1",Toast.LENGTH_SHORT).show();
        //switch(item.getItemId())
        //{
        //    case R.id.action_login:
        //        //Toast.makeText(this,"come on",Toast.LENGTH_SHORT).show();
        //        startActivity(new Intent(this,LoginActivity.class));
        //        return true;
        //    default:
        //        return super.onOptionsItemSelected(item);
        //}
        return false;
    }

    @Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod(
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
            }
            Log.d(LTAG, sb.toString());
            //Toast.makeText(getApplicationContext(),sb.toString(),Toast.LENGTH_LONG).show();

            if(location.getLocType() == BDLocation.TypeNetWorkException || location.getLocType() == BDLocation.TypeCriteriaException || location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail)
            {
                return;
            }
            //store my location to Shared Prefference xml file
            AppCommonData comData = new AppCommonData(MainActivity.this);
            comData.setStringValue("my_latitude",String.valueOf(location.getLatitude()));
            comData.setStringValue("my_longitude",String.valueOf(location.getLongitude()));
            comData.setStringValue("my_loc_time",location.getTime());
            comData.setStringValue("my_loc_add",location.getAddrStr());
            comData.commit();

            mLp = new LocationPara(location.getLatitude(),location.getLongitude());
            //update new location to server
            new PostLocationTask().execute("");
            //stop location
            mLocationClient.stop();
        }
    }
    private class PostLocationTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls)
        {
            Integer result = -1;
            try
            {
                //Toast.makeText(getBaseContext(),"Location Post Begin!!",Toast.LENGTH_LONG);
                Log.d(LTAG,"Location Post Begin!!");
                //Log.d(LTAG,urls[0].getClass().getSimpleName());
                result = WanEyeUtil.doPostLocation(mLp);
                return result;
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
                Log.d(LTAG,"Location Post success!!");
                //Toast.makeText(getBaseContext(),"Location Post success!",Toast.LENGTH_LONG).show();
            }
            else if(result == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                Log.d(LTAG,"Location Post HTTP_UNAUTHORIZED!!");
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
            else
            {
                Log.d(LTAG,"Location Post failed!!");
                //Toast.makeText(getBaseContext(),"Location Post failed!",Toast.LENGTH_LONG).show();
                new PostLocationTask().execute("");
            }
        }
    }
}