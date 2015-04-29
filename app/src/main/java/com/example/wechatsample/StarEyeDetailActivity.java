package com.example.wechatsample;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


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
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_eye_detail);
        initViews();
        WindowManager wm = this.getWindowManager();
        DisplayMetrics  dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mImageThumbSize = dm.widthPixels / 3;
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        mAdapter = new ImageAdapter(StarEyeDetailActivity.this);
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(StarEyeDetailActivity.this, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(StarEyeDetailActivity.this, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
    }

    private void initViews()
    {
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
        mLLAnswers = (LinearLayout)findViewById(R.id.llAnwsers);
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

    private class doGetInstancesPicsTask extends AsyncTask<String, Void, ArrayList<LinearLayout>> {
        protected ArrayList<LinearLayout> doInBackground(String... strs)
        {
            String result = "";
            ArrayList<LinearLayout> lls = null;
            try
            {
                result = WanEyeUtil.doGetInstancePics(mStarEyeInstance.toString());
                Log.d("doGetInstancesPicsTask", result);
                lls = getLinearLayoutsFromJson(result);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return lls;
        }
        protected void onPostExecute(ArrayList<LinearLayout> views)
        {
            //mJson = result;
            mLLAnswers.removeAllViews();
            for(int i = 0; i < views.size(); i++)
            {
                mLLAnswers.addView(views.get(i));
            }
        }
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
        private int mActionBarHeight = 0;
        private GridView.LayoutParams mImageViewLayoutParams;


        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }
        @Override
        public int getCount() {
            // If columns have yet to be determined, return no items
            if (getNumColumns() == 0) {
                return 0;
            }

            // Size + number of columns for top empty row
            return urls.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : urls.get(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

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
            mImageFetcher.loadImage(urls.get(position - mNumColumns), imageView);
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
