package com.example.wechatsample;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kyo on 2015/4/11.
 */
public class AppCommonData {
    private Activity mContext = null;
    SharedPreferences mPref = null;
    SharedPreferences.Editor mEditor = null;
    private static final String mSharedPrefName = "appSharedValues";
    public AppCommonData(Context context)
    {
        this.mContext = (Activity)context;
        mPref = mContext.getSharedPreferences(mSharedPrefName,Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
    }
    public void setStringValue(String key, String value)
    {
        mEditor.putString(key, value);
    }
    public void setBooleanValue(String key, boolean value)
    {
        mEditor.putBoolean(key, value);
    }
    public void setFloatValue(String key, float value)
    {
        mEditor.putFloat(key, value);
    }
    public void setIntValue(String key, int value)
    {
        mEditor.putInt(key, value);
    }
    public String getString(String key, String defVal)
    {
        return mPref.getString(key,defVal);
    }
    public boolean getBoolean(String key, boolean defVal)
    {
        return mPref.getBoolean(key,defVal);
    }
    public int getInt(String key, int defVal)
    {
        return mPref.getInt(key,defVal);
    }
    public void commit()
    {
        mEditor.commit();
    }
}
