package com.example.wechatsample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import java.io.InputStream;
import java.net.HttpURLConnection;


public class StarEyeDetailActivity extends Activity {
    private Intent mIntentMe;
    private Bundle mBundleData;
    private TextView mTVDescription;
    private String mUserName;
    private Integer mStarEyeInstance;
    private LatLng mLatLng = null;
    private Button mBtAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_eye_detail);
        mTVDescription = (TextView) findViewById(R.id.tv_description);
        mIntentMe = getIntent();
        mBundleData = mIntentMe.getExtras();
        if(! mBundleData.isEmpty())
        {
            mUserName = mBundleData.getString("username","");
            mTVDescription.setText(mBundleData.getCharSequence("description") + "\nBy:" + mUserName);
            mStarEyeInstance = mBundleData.getInt("instanceId",0);
            mLatLng = new LatLng(mBundleData.getDouble("latitude"),mBundleData.getDouble("longitude"));
        }
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

    private class doGetInstancesPicsTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strs)
        {
            String result = "";
            try
            {
                result = WanEyeUtil.doGetInstancePics(mStarEyeInstance.toString());
                Log.d("doGetInstancesPicsTask", result);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(String result)
        {
            //mJson = result;

        }
    }
}
