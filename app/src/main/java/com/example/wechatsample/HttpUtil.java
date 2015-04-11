package com.example.wechatsample;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kyo on 2015/4/8.
 */
public class HttpUtil {
    public void setmCookie(String mCookie) {
        this.mCookie = mCookie;
    }

    public String getmCookie() {
        return mCookie;
    }

    private String mCookie = "";
    private InputStream in = null;
    private URLConnection urlConnection = null;
    private HttpURLConnection httpConn = null;

    public void HttpUtil()
    {

    }
    public void HttpUtil(String cookie)
    {
        this.mCookie = cookie;
    }
    public void closeConnection()
    {
        if(httpConn != null)
        {
            httpConn.disconnect();
        }
    }

    public int getResponseCode() throws  IOException
    {
        return httpConn.getResponseCode();
    }

    public String getHeaderField(String key)
    {
        return httpConn.getHeaderField(key);
    }

    public InputStream httpRequestGet(String url_string)
    {
        int response = -1;
        try
        {
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            if(mCookie != "")
            {
                httpConn.setRequestProperty("Cookie",mCookie);
            }
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return in;
    }
    public InputStream httpRequestPost(String url_string, String data, String contentType)
    {
        int response = -1;
        try
        {
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setDoOutput(true);
            if(mCookie != "")
            {
                httpConn.setRequestProperty("Cookie",mCookie);
            }
            httpConn.setRequestProperty("Content-type",contentType);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("POST");

            // get outputStream and write data to request body
            OutputStream os = new BufferedOutputStream(httpConn.getOutputStream());
            os.write(data.getBytes());
            os.flush();
            os.close();

            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return in;
    }
}
