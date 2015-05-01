package com.example.wechatsample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.waneye.util.ImageCache;
import com.waneye.util.ImageFetcher;
import com.waneye.util.RecyclingImageView;
import com.waneye.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class StarEyeDetailActivity extends FragmentActivity {
    private Intent mIntentMe;
    private Bundle mBundleData;
    private TextView mTVDescription;
    private String mUserName;
    private Integer mStarEyeInstance;
    private TextView mTVByUser;
    private LatLng mLatLng = null;
    private Button mBtAnswer;
    private LinearLayout mLLAnswers;
    private Button mBTComment;
    private EditText mETComment;
    private Button mBTAddComment;

    private static final String TAG = "StarEyeDetailActivity";
    private static final String IMAGE_CACHE_DIR = "bitmaps";
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter = null;
    private ImageFetcher mImageFetcher;
    //private ArrayList<String> urls;
    private ArrayList<HashMap<String,Object>> answers;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_eye_detail);

        //WindowManager wm = this.getWindowManager();
        //DisplayMetrics  dm = new DisplayMetrics();
        //wm.getDefaultDisplay().getMetrics(dm);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);//dm.widthPixels / 3;
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        answers = new ArrayList<HashMap<String, Object>>();
        mAdapter = new ImageAdapter(StarEyeDetailActivity.this,answers);
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(StarEyeDetailActivity.this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(StarEyeDetailActivity.this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        initViews();
    }

    private void initViews()
    {
        mGridView = (GridView)findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    // Before Honeycomb pause image loading on scroll to help with performance
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                                if (Utils.hasJellyBean()) {
                                    mGridView.getViewTreeObserver()
                                            .removeOnGlobalLayoutListener(this);
                                } else {
                                    mGridView.getViewTreeObserver()
                                            .removeGlobalOnLayoutListener(this);
                                }
                            }
                        }
                    }
                });

        mTVDescription = (TextView) findViewById(R.id.tv_description);
        mTVByUser = (TextView) findViewById(R.id.tv_byuser);
        mIntentMe = getIntent();
        mBundleData = mIntentMe.getExtras();
        if(! mBundleData.isEmpty())
        {
            mUserName = mBundleData.getString("username","");
            mTVDescription.setText(mBundleData.getCharSequence("description"));
            mTVByUser.setText("By:" + mUserName);
            mStarEyeInstance = mBundleData.getInt("instanceId",0);
            mLatLng = new LatLng(mBundleData.getDouble("latitude"),mBundleData.getDouble("longitude"));
        }
        mBTAddComment = (Button) findViewById(R.id.bt_comment);
        mBTAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(),"添加评论",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getBaseContext(),ChartInstanceActivity.class);
                Bundle bd = new Bundle();
                bd.putInt("instanceId",mStarEyeInstance);
                intent.putExtras(bd);
                startActivity(intent);
                /*InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);*/
            }
        });
        mETComment = (EditText) findViewById(R.id.et_comment);

        mBTComment = (Button) findViewById(R.id.bt_send_comment);
        //mBTComment.setFocusable(true);
        //mBTComment.requestFocus();
        mBTComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bd = new Bundle();
                bd.putInt("instanceId", mStarEyeInstance);
                Intent intent = new Intent(StarEyeDetailActivity.this,PostResponseActivity.class);
                intent.putExtras(bd);
                //StarEyeDetailActivity.this.startActivity(intent);
                if(mETComment.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(),"请输入内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                new doPostChartTask().execute(mETComment.getText().toString());
            }
        });
        mBtAnswer = (Button)findViewById(R.id.btAnser);
        mBtAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bd = new Bundle();
                bd.putInt("instanceId", mStarEyeInstance);
                Intent intent = new Intent(StarEyeDetailActivity.this,PostResponseActivity.class);
                intent.putExtras(bd);
                StarEyeDetailActivity.this.startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        new doGetInstancesPicsTask().execute("");
        mImageFetcher.setExitTasksEarly(false);
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_star_eye_detail, menu);
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

    private class doGetInstancesPicsTask extends AsyncTask<String, Void, Boolean> {
        protected Boolean doInBackground(String... strs)
        {
            String result = "";
            Boolean b = false;
            try
            {
                result = WanEyeUtil.doGetInstancePics(mStarEyeInstance.toString());
                Log.d("doGetInstancesPicsTask", result);
                //lls = getLinearLayoutsFromJson(result);
                //answers = getListsFromJson(result);
                answers.clear();
                ArrayList<HashMap<String,Object>> temp = getListsFromJson(result);
                answers.addAll(temp);
                b = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return b;
        }
        protected void onPostExecute(Boolean result)
        {
            if(result)
            {
                if(BuildConfig.DEBUG)
                {
                    Log.d("", "Do update.");
                }
                //mAdapter.setData(answers);
                mAdapter.notifyDataSetChanged();
                Log.d("Debug","" + mGridView.getNumColumns());
            }
        }
    }
    public ArrayList<HashMap<String , Object>> getListsFromJson(String json) throws JSONException
    {
        ArrayList<HashMap<String , Object>> lists = new ArrayList<HashMap<String, Object>>();
        HashMap<String,Object> item ;
        //DisplayMetrics dm = getResources().getDisplayMetrics();
        //final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, dm);
        //Log.d("get: height = ",height+"");
        JSONArray ja = new JSONArray(json);
        for( int i = 0; i < ja.length(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            item = new HashMap<String, Object>();
            item.put("ownerUserName",jo.getString("ownerUserName"));
            item.put("description",jo.getString("description"));
            item.put("pictureUrl",jo.getString("pictureUrl"));
            lists.add(item);
        }
        return lists;
    }
    public ArrayList<LinearLayout> getLinearLayoutsFromJson(String json) throws JSONException
    {
        ArrayList<LinearLayout> lls = new ArrayList<LinearLayout>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, dm);
        Log.d("get: height = ",height+"");
        JSONArray ja = new JSONArray(json);
        for( int i = 0; i < ja.length(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            String un = jo.getString("ownerUserName");
            String desc = jo.getString("description");
            String picUrl = jo.getString("pictureUrl");
            HttpUtil hu = new HttpUtil();
            DetailResponseProducer dr = new DetailResponseProducer(getBaseContext(),un,desc,hu.httpGetImageByUrl(picUrl,height,height));
            lls.add(dr.generateViewByInstances());
        }
        return lls;
    }

    public class doPostChartTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strs)
        {
            String result = null;
            try
            {
                result = WanEyeUtil.doPostChart(mStarEyeInstance.toString(),strs[0]);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if(isCancelled())
            {
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            if(isCancelled())
            {
                return;
            }
            if(result != null && result != "")
            {
                Toast.makeText(getBaseContext(),"发送成功！", Toast.LENGTH_SHORT).show();
                mETComment.setText("");
            }
            else
            {
                Toast.makeText(getBaseContext(),"发送失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;

        public void setData(ArrayList<HashMap<String, Object>> data) {
            this.data = data;
        }

        private ArrayList<HashMap<String,Object>> data;
        private GridView.LayoutParams mImageViewLayoutParams;


        public ImageAdapter(Context context, ArrayList<HashMap<String,Object>> al) {
            super();
            mContext = context;
            data = al;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return (data.get(position)).get("pictureUrl");
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //BEGIN_INCLUDE(load_gridview_item)

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new RecyclingImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage((data.get(position)).get("pictureUrl"), imageView);
            return imageView;
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageFetcher.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
