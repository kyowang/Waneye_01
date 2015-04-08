package com.example.wechatsample;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URL.*;
import com.example.wechatsample.WanEyeUtil;

/**
 * Created by kyo on 2015/4/8.
 */
public class HttpUtil {

    public InputStream httpRequestGet(String surl)
    {
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(surl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return in;
    }

}
