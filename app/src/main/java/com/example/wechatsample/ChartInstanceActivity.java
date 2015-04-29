package com.example.wechatsample;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private EditText mChartActComment;
    private Button mChartButtonSend;
    private SimpleAdapter mMyAddapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_instance);
        mChartItems = (ListView) findViewById(R.id.lv_chartItems);
        mChartActComment = (EditText)findViewById(R.id.chart_activity_comment);
        mChartButtonSend = (Button) findViewById(R.id.chart_activity_send);
        mChartButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bd = new Bundle();
                bd.putInt("instanceId", mStarEyeInstance);
                Intent intent = new Intent(ChartInstanceActivity.this,PostResponseActivity.class);
                intent.putExtras(bd);
                //StarEyeDetailActivity.this.startActivity(intent);
                if(mChartActComment.getText().toString().equals(""))
                {
                    Toast.makeText(getBaseContext(),"请输入内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                new doPostChartTask().execute(mChartActComment.getText().toString());
            }
        });
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
            mMyAddapter = new SimpleAdapter(getBaseContext(),
                    mData,
                    R.layout.chart_me_item,
                    new String[]{ "ownerUserName", "content" },
                    new int[] {R.id.chart_item_me_name, R.id.chart_item_me_comment });
            mChartItems.setAdapter(mMyAddapter);
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
    public Map<String,Object> getSingleDataFromJson(String json) throws JSONException
    {
        Map<String,Object> map = null;
        if(null == json)
        {
            return null;
        }
        JSONObject jo = new JSONObject(json);

        map = new HashMap<String, Object>();
        String name = jo.getString("ownerUserName");
        if(null == name || "" == name || "null" == name)
        {
            name = getMyName();
        }
        map.put("ownerUserName",name);
        map.put("ownerId",jo.getInt("ownerId"));
        map.put("telescopeId",jo.getInt("telescopeId"));
        map.put("id", jo.getInt("id"));
        map.put("content",jo.getString("content"));

        return map;
    }
    public class doPostChartTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strs)
        {
            String result = null;
            try
            {
                result = WanEyeUtil.doPostChart(mStarEyeInstance.toString(), strs[0]);
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
                mChartActComment.setText("");
                if(BuildConfig.DEBUG)
                {
                    Log.d(LTAG,result);
                }
                try
                {
                    mData.add(getSingleDataFromJson(result));
                    mMyAddapter.notifyDataSetChanged();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(getBaseContext(),"发送失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected String getMyName()
    {
        String name = "";
        AppCommonData comData = new AppCommonData(ChartInstanceActivity.this);
        name  = comData.getString("username","");
        return name;
    }


}
