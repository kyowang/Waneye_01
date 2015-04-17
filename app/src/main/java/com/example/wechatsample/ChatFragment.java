package com.example.wechatsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 聊天Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class ChatFragment extends Fragment {

    //轮播图图片数量
    private final static int IMAGE_COUNT = 3;
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 3;
    //自动轮播启用开关
    private final static boolean isAutoPlay = true;

    //自定义轮播图的资源ID
    private int[] imagesResIds;
    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
    //放圆点的View的list
    private List<View> dotViewsList;

    private LinearLayout mChatLLMain;
    private ListView mLVStarEyeInstance;
    private String mJson = "";
    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;
    //Handler
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        return inflater.inflate(R.layout.first_fragment, container, false );
	}
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mLVStarEyeInstance = (ListView)getActivity().findViewById(R.id.listHot);

        mChatLLMain = (LinearLayout)getActivity().findViewById(R.id.mChatLLMain);
        viewPager = (ViewPager)getActivity().findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        initData();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetStarEyeByMeTask().execute("");
    }

    /**
     * 开始轮播图切换
     */
    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4, TimeUnit.SECONDS);
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }
    /**
     * 初始化相关Data
     */
    private void initData(){
        imagesResIds = new int[]{
                R.drawable.santorini_04,
                R.drawable.santorini_05,
                R.drawable.whitehouse_02,
        };
        imageViewsList = new ArrayList<ImageView>();
        dotViewsList = new ArrayList<View>();

    }

    /**
     * 初始化Views等UI
     */
    private void initUI(){
        //LayoutInflater.from(context).inflate(R.layout.first_fragment, this, true);
        for(int imageID : imagesResIds){
            ImageView view =  new ImageView(getActivity());
            view.setImageResource(imageID);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageViewsList.add(view);
        }
        dotViewsList.add(getActivity().findViewById(R.id.v_dot1));
        dotViewsList.add(getActivity().findViewById(R.id.v_dot2));
        dotViewsList.add(getActivity().findViewById(R.id.v_dot3));

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
        if(isAutoPlay){
            startPlay();
        }
    }

    /**
     * 填充ViewPager的页面适配器
     * @author caizhiming
     */
    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            // TODO Auto-generated method stub
            //((ViewPag.er)container).removeView((View)object);
            ((ViewPager)container).removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            // TODO Auto-generated method stub
            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

    }
    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     * @author caizhiming
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub

            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    ((View)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_black);
                }else {
                    ((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_white);
                }
            }
        }

    }

    /**
     *执行轮播图切换任务
     *@author caizhiming
     */
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }
    /**
     * 销毁ImageView资源，回收内存
     * @author caizhiming
     */
    private void destoryBitmaps() {

        for (int i = 0; i < IMAGE_COUNT; i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                drawable.setCallback(null);
            }
        }
    }

    private class GetStarEyeByMeTask extends AsyncTask<String , Void, String> {
        protected String doInBackground(String... urls)
        {
            String result = "";
            try
            {
                result = WanEyeUtil.doGetStarEyeByMe();
                //mJson = result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            mJson = result;
            if("" == result)
            {
                return;
            }
            try
            {
                ArrayList<RequestEntity> al = getEntityFromJson(result);
                mChatLLMain.removeAllViews();
                for(int i = 0; i < al.size(); i++)
                {
                    mChatLLMain.addView(new EntityProducer(getActivity(),al.get(i)).generateViewByInstances());
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<String> getListFromJson(String json) throws JSONException
    {
        ArrayList<String> result = new ArrayList();
        JSONArray ja = new JSONArray(json);
        for(int i = 0; i < ja.length(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            result.add(jo.getString("description"));
        }
        return result;
    }
    public String getStringFromIndex(String key, int i) throws JSONException
    {
        JSONArray ja = new JSONArray(mJson);
        if(i < ja.length())
        {
            JSONObject jo = ja.getJSONObject(i);
            return jo.getString(key);
        }
        return "";
    }
    public Integer getIntFromIndex(String key, int i) throws JSONException
    {
        JSONArray ja = new JSONArray(mJson);
        if(i < ja.length())
        {
            JSONObject jo = ja.getJSONObject(i);
            return jo.getInt(key);
        }
        return -1;
    }
    public LatLng getLatLngFromIndex(int i) throws JSONException
    {
        LatLng ll = null;
        JSONArray ja = new JSONArray(mJson);
        if(i < ja.length())
        {
            JSONObject jo = ja.getJSONObject(i);
            ll = new LatLng(jo.getDouble("latitude"),jo.getDouble("longitude"));
        }
        return ll;
    }
    public Double getDoubleFromIndex(String key,int i) throws JSONException
    {
        JSONArray ja = new JSONArray(mJson);
        if(i < ja.length())
        {
            JSONObject jo = ja.getJSONObject(i);
            return jo.getDouble(key);
        }
        return -1.0;
    }
    public class MyOnItemClickedListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(getActivity(),""+ i + " :" + l + view.getClass().getSimpleName(),Toast.LENGTH_LONG).show();
            Bundle bd = new Bundle();
            try {
                bd.putCharSequence("description", getStringFromIndex("description", i));
                bd.putCharSequence("username",getStringFromIndex("ownerUserName",i));
                bd.putInt("instanceId",getIntFromIndex("id",i));
                bd.putDouble("latitude", getDoubleFromIndex("latitude", i));
                bd.putDouble("longitude",getDoubleFromIndex("longitude",i));
                Intent intent = new Intent(getActivity(),StarEyeDetailActivity.class);
                intent.putExtras(bd);
                startActivity(intent);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public ArrayList<RequestEntity> getEntityFromJson(String json) throws JSONException
    {
        ArrayList<RequestEntity> al = new ArrayList<RequestEntity>();
        JSONArray ja = new JSONArray(json);
        for(int i = 0; i < ja.length(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            al.add(new RequestEntity(
                    "中山路1097号附近",
                    jo.getString("description"),
                    jo.getString("ownerUserName"),
                    jo.getInt("ownerId"),
                    jo.getInt("id"),
                    jo.getDouble("latitude"),
                    jo.getDouble("longitude"),
                    null));
        }
        return al;
    }
}
