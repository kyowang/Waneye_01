package com.example.wechatsample;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

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

    public HttpUtil()
    {

    }
    public HttpUtil(String cookie)
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

    public String httpRequestGet(String url_string ,boolean cookie)
    {
        int response = -1;
        String result ="";
        in = null;
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
            if(mCookie != "" && cookie)
            {
                httpConn.setRequestProperty("Cookie",mCookie);
            }
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
                result = WanEyeUtil.readJsonData(in);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return result;
    }
    public Bitmap httpGetImageByUrl(String url_string, int width, int height)
    {
        int response = -1;
        String result ="";
        Bitmap bitmap = null;
        in = null;
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
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return bitmap;
    }
    public void closeConn()
    {
        httpConn.disconnect();
    }
    public String httpRequestPostReturnString(String url_string, String data, String contentType, boolean cookie)
    {
        Integer response = -1;
        String result ="";
        in = null;
        try
        {
            Log.d("MainActivity", "httpRequestPostReturnString");
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setDoOutput(true);
            if(mCookie != "" && cookie)
            {
                Log.d("MainActivity", "Cookie added to the header!");
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
                result = WanEyeUtil.readJsonData(in);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return result;
    }
    public Integer httpRequestPutPicId(String url_string, boolean cookie)
    {

        Integer response = -1;
        String result ="";

        in = null;
        try
        {
            Log.d("MainActivity", "httpRequestPutPicId");
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setDoOutput(false);
            //httpConn.setRequestProperty("Content-type","image/jpeg");
            //httpConn.setRequestProperty("Content-type","");
            if(mCookie != "" && cookie)
            {
                Log.d("MainActivity", "Cookie added to the header!");
                httpConn.setRequestProperty("Cookie",mCookie);
            }
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("PUT");

            response = httpConn.getResponseCode();
            Log.d("httpRequestPutPicId",response.toString());
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
                //result = WanEyeUtil.readJsonData(in);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return response;
    }
    public Integer httpRequestPutUploadPic(String url_string, InputStream is)
    {
        Integer response = -1;
        String result ="";

        in = null;
        try
        {
            Log.d("MainActivity", "httpRequestPutUploadPic");
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setDoOutput(true);
            //httpConn.setRequestProperty("Content-type","image/jpeg");
            httpConn.setRequestProperty("Content-type","");
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("PUT");

            // get outputStream and write data to request body
            OutputStream os = new BufferedOutputStream(httpConn.getOutputStream());
            byte[] buffer = new byte[1024];
            int i = 0;
            while ( ( i = is.read(buffer)) > 0)
            {
                os.write(buffer,0,i);
            }
            os.flush();
            os.close();

            response = httpConn.getResponseCode();
            Log.d("httpRequestPutUploadPic",response.toString());
            if(response == HttpURLConnection.HTTP_OK)
            {
                in = httpConn.getInputStream();
                //result = WanEyeUtil.readJsonData(in);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            httpConn.disconnect();
        }
        return response;
    }
    public InputStream httpRequestPost(String url_string, String data, String contentType, boolean cookie)
    {
        Integer response = -1;
        try
        {
            Log.d("MainActivity", "httpRequestPost");
            URL url = new URL(url_string);
            urlConnection = url.openConnection();
            if(! (urlConnection instanceof HttpURLConnection))
            {
                throw new IOException("Not an HTTP connection");
            }
            httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setDoOutput(true);
            if(mCookie != "" && cookie)
            {
                Log.d("MainActivity", "Cookie added to the header!");
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
