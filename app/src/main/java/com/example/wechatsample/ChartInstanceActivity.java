package com.example.wechatsample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChartInstanceActivity extends Activity {
    private ListView mChartItems;
    private Integer mStarEyeInstance = -1;
    private ArrayList<Map<String , Object>> mData;
    private final static String LTAG = ChartInstanceActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_instance);
        mChartItems = (ListView) findViewById(R.id.lv_chartItems);
        Intent intent = getIntent();
        if(null != intent)
        {
            Bundle bd = intent.getExtras();
            if(bd != null && !bd.isEmpty())
            {
                mStarEyeInstance = bd.getInt("instanceId",-1);
            }
            if( -1 != mStarEyeInstance)
                new doGetChartTask().execute(mStarEyeInstance.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chart_instance, menu);
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

    private class doGetChartTask extends AsyncTask<String, Void, ArrayList<Map<String,Object>>> {
        protected ArrayList<Map<String,Object>> doInBackground(String... strs)
        {
            ArrayList<Map<String,Object>> result = null;
            String json = null;
            try
            {
                json = WanEyeUtil.doGetChartById(strs[0]);
                if(BuildConfig.DEBUG)
                {
                    Log.d(LTAG, json);
                }
                result = getDataFromJson(json);
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
        protected void onPostExecute(ArrayList<Map<String,Object>> result)
        {
            if(isCancelled() || result == null)
            {
                return;
            }
            mData = result;
            SimpleAdapter sa = new SimpleAdapter(getBaseContext(),
                    mData,
                    R.layout.chart_me_item,
                    new String[]{ "ownerUserName", "content" },
                    new int[] {R.id.chart_item_me_name, R.id.chart_item_me_comment });
            mChartItems.setAdapter(sa);
        }
    }
    public ArrayList<Map<String,Object>> getDataFromJson(String json) throws JSONException
    {
        ArrayList<Map<String,Object>> al = new ArrayList<Map<String,Object>>();
        if(null == json)
        {
            return null;
        }
        Map<String, Object> map ; //new HashMap<String, Object>();
        JSONArray ja = new JSONArray(json);
        for(int i = 0; i < ja.length(); i++)
        {
            JSONObject jo = ja.getJSONObject(i);
            map = new HashMap<String, Object>();
            map.put("ownerUserName",jo.getString("ownerUserName"));
            map.put("ownerId",jo.getInt("ownerId"));
            map.put("telescopeId",jo.getInt("telescopeId"));
            map.put("id", jo.getInt("id"));
            map.put("content",jo.getString("content"));
            al.add(map);
        }
        return al;
    }
}
