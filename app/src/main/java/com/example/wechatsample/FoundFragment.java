package com.example.wechatsample;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.jar.Attributes;

/**
 * 发现Fragment的界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class FoundFragment extends Fragment {
    private LinearLayout mLLFoundMain;
    private String mJson = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        /*
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		FrameLayout fl = new FrameLayout(getActivity());
		fl.setLayoutParams(params);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, dm);
		TextView v = new TextView(getActivity());
		params.setMargins(margin, margin, margin, margin);
		v.setLayoutParams(params);
		v.setLayoutParams(params);
		v.setGravity(Gravity.CENTER);
		v.setText("发现界面");
		v.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, dm));
		fl.addView(v);
		return fl;*/
        LinearLayout view = (LinearLayout)inflater.inflate(R.layout.found_fragment_activity, container, false );
        return view;
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLLFoundMain = (LinearLayout) getActivity().findViewById(R.id.llFoundMain);
        //RequestEntity re = new RequestEntity("ss","hashjfjkdsfhksjdhfksdfhskjdhfkadad","jinluu",1,1,11.111,22.222,null);
        //mLLFoundMain.addView(new EntityProducer(getActivity(),re).generateViewByInstances());
        new GetStarEyeByNearBy().execute("");
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetStarEyeByNearBy().execute("");
    }

    private class GetStarEyeByNearBy extends AsyncTask<String , Void, String> {
        protected String doInBackground(String... urls)
        {
            String result = "";
            try
            {
                result = WanEyeUtil.doGetStarEyeByMe();
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
                mLLFoundMain.removeAllViews();
                for(int i = 0; i < al.size(); i++)
                {
                    mLLFoundMain.addView(new EntityProducer(getActivity(),al.get(i)).generateViewByInstances());
                }
                //mLVStarEyeInstance.setOnItemSelectedListener(new MyOnItemSelectedListener());
            }
            catch (JSONException e)
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
