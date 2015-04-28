package com.example.wechatsample;


import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by wg on 2015/4/8.
 */
public  class WanEyeUtil {
    private static final String LTAG = WanEyeUtil.class.getSimpleName();
    private static final String server_address = "112.126.81.167";
    private static final String server_port = "10086";
    private static final String register_uri = "/services/api/user.json";
    private static final String login_uri = "/j_security_check";
    private static final String postLocation_uri = "/services/api/pos.json";
    private static final String postStarEyeInstance_uri = "/services/api/stareye.json";
    private static final String getInstance_uri = "/services/api/stareye.json";
    private static final String getChart_prefix = "/services/api/stareye/";
    private static final String getChart_postfix = "/chart.json";

    // /services/api/stareye/${id}/pic.json or /services/api/stareye/${id}/${picId}.json
    private static final String postStareyePic_uri_prefix="/services/api/stareye/";
    private static final String postStareyePic_uri_pic = "pic";
    private static final String postStareyePic_uri_midfix="/";
    private static final String postStareyePic_uri_postfix=".json";

    private static final String getBaiduPoiInterface_uri_prefix = "http://api.map.baidu.com/place/v2/";
    private static final String getBaiduPoiInterface_uri_midfix = "search?q=";
    private static final String getBaiduPoiInterface_uri_midfix2 = "&region=";
    private static final String getBaiduPoiInterface_uri_postfix = "&output=json&ak=ZHES86tX8Ij6ZT2aMXKz5wV7";

    public static String getCookieAuth() {
        return cookieAuth;
    }

    public static void setCookieAuth(String cookieAuth) {
        WanEyeUtil.cookieAuth = cookieAuth;
    }

    private static String cookieAuth = "";
    // URL Utility part
    static public String encUrlNoParameter(String url)
    {
        String result = "";
        try{
            result =  URLEncoder.encode(url,"UTF-8");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    static public String getStarEyeChartUrl(String instanceId)
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.getChart_prefix + instanceId + WanEyeUtil.getChart_postfix;
    }
    static public String getBaiduPoiInterface_Url(String key, String city)
    {
        return WanEyeUtil.getBaiduPoiInterface_uri_prefix + WanEyeUtil.getBaiduPoiInterface_uri_midfix + encUrlNoParameter(key) + WanEyeUtil.getBaiduPoiInterface_uri_midfix2 + encUrlNoParameter(city) + WanEyeUtil.getBaiduPoiInterface_uri_postfix;
    }
    static public String getRegisterUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.register_uri;
    }
    static public String getLoginUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.login_uri;
    }
    static public String getPostLocationUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postLocation_uri;
    }
    static public String getPostStarEyeInstanceUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStarEyeInstance_uri;
    }
    static public String getInstanceUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.getInstance_uri;
    }
    static public String getPicUrl(String instanceId)
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStareyePic_uri_prefix + instanceId + WanEyeUtil.postStareyePic_uri_midfix + WanEyeUtil.postStareyePic_uri_pic + WanEyeUtil.postStareyePic_uri_postfix;
    }
    static public String getChartUrl(String instanceId)
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.getChart_prefix + instanceId + WanEyeUtil.getChart_postfix;
    }
    static public String getPicUrlFinish(String instanceId,String picId)
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStareyePic_uri_prefix + instanceId + "/pic" + WanEyeUtil.postStareyePic_uri_midfix + picId + WanEyeUtil.postStareyePic_uri_postfix;
    }
    static public String doGetBaiduPoiInfo(String key) throws  IOException
    {
        Log.d("MainActivity","doGetBaiduPoiInfo");
        String result = "";
        HttpUtil hu = new HttpUtil();
        result = hu.httpRequestGet(WanEyeUtil.getBaiduPoiInterface_Url(key, "世界"), false);
        if(hu.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return null;
        }
        return result;
    }
    static public String doGetBaiduPoiInfo(String key, String city)throws  IOException
    {
        Log.d("MainActivity","doGetBaiduPoiInfo");
        String result = "";
        HttpUtil hu = new HttpUtil();
        result = hu.httpRequestGet(WanEyeUtil.getBaiduPoiInterface_Url(key, city), false);
        if(hu.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return "";
        }
        return result;
    }
    static public String doGetInstancePics(String instanceId) throws IOException
    {
        Log.d("MainActivity","doGetInstancePics");
        String result = "";
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        result = hu.httpRequestGet(WanEyeUtil.getPicUrl(instanceId), true);
        if(hu.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return "";
        }
        return result;
    }
    static public String doPostPicOne(Integer instanceId,String data, String contentType) throws IOException
    {
        String body;
        Log.d("MainActivity","doPostPicOne");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        body = hu.httpRequestPostReturnString(getPicUrl(instanceId.toString()), data, contentType, true);

        return body;
    }
    static public Integer doPostPicTwo(String url,InputStream is) throws IOException
    {
        Integer responseCode;
        Log.d("MainActivity","doPostPicTwo");
        HttpUtil hu = new HttpUtil();
        responseCode = hu.httpRequestPutUploadPic(url,is);
        return responseCode;
    }
    static public Integer doPostPicThree(Integer instanceId, String picId) throws IOException
    {
        Integer responseCode;
        Log.d("MainActivity","doPostPicThree");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        responseCode = hu.httpRequestPutPicId(getPicUrlFinish(instanceId.toString(),picId), true);
        return responseCode;
    }
    static public Integer doPostStarEyeInstance(StarEyeInstance sei) throws IOException
    {
        Log.d("MainActivity","doPostStarEyeInstance");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        hu.httpRequestPost(getPostStarEyeInstanceUrl(),sei.toString(),StarEyeInstance.contentType,true);

        return hu.getResponseCode();
    }
    static public String doGetStarEyeByMe() throws IOException
    {

        String body;
        Log.d("MainActivity","doGetStarEyeByMe");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        body = hu.httpRequestGet(getInstanceUrl(),true);

        return body;
    }
    static public String doGetStarEyeByLocation(String lat, String lng, String radius) throws IOException
    {
        //Log.d("MainActivity","doGetStarEyeByLocation");
        String body;
        StringBuilder query = new StringBuilder();
        query.append("?latitude=");
        query.append(lat);
        query.append("&longitude=");
        query.append(lng);
        query.append("&searchRadius=");
        query.append(radius);
        Log.d("MainActivity","doGetStarEyeByLocation");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        body = hu.httpRequestGet(getInstanceUrl() + query.toString(),true);

        return body;
    }
    static public boolean doPostChart(String instanceId, String comment) throws Exception
    {
        if(null == instanceId || null == comment)
        {
            throw new NullPointerException("instanceId and comment can not be null!");
        }
        if(BuildConfig.DEBUG)
        {
            Log.d(LTAG, "doPostChart: instanceId = " + instanceId + " comment = " + comment);
        }
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        sb.append("{\"content\":\"");
        sb.append(comment);
        sb.append("\"}");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        in = hu.httpRequestPost(getChartUrl(instanceId),sb.toString(),"application/json", true);
        if(hu.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            return true;
        }
        return false;
    }
    static public boolean doLogin(String username, String passwd) throws IOException
    {
        Log.d("MainActivity","doLogin");
        InputStream in = null;
        StringBuffer sb = new StringBuffer("j_username=");
        sb.append(username);
        sb.append("&j_password=");
        sb.append(passwd);
        sb.append("&login=");
        HttpUtil hu = new HttpUtil();
        in = hu.httpRequestPost(getLoginUrl(),sb.toString(),"application/x-www-form-urlencoded", true);

        if(hu.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            cookieAuth = WanEyeUtil.getJsessionPair(hu.getHeaderField("Set-Cookie"));
            return true;
        }
        return false;
    }
    static public String getJsessionPair(String value)
    {
        if(null == value)
        {
            return "";
        }
        String temp[];
        String result = "";
        temp = value.split(";");
        if(temp.length <= 0 || temp == null)
        {
            return "";
        }
        return temp[0];
    }
    static public int doRegister(RegisterPara rp) throws IOException
    {
        Log.d("MainActivity","doRegister");
        InputStream in = null;
        HttpUtil hu = new HttpUtil();
        in = hu.httpRequestPost(getRegisterUrl(),rp.toString(),RegisterPara.contentType, true);

        return hu.getResponseCode();
    }
    static public Integer doPostLocation(LocationPara lp) throws IOException
    {
        Log.d("MainActivity","doPostLocation");
        HttpUtil hu = new HttpUtil(WanEyeUtil.cookieAuth);
        hu.httpRequestPost(getPostLocationUrl(),lp.toString(),LocationPara.contentType,true);

        return hu.getResponseCode();
    }

    public class RegisterPara
    {
        public String getUsername() {
            return username;
        }

        private String username = "";
        private String password = "";

        public String getEmail() {
            return email;
        }

        private String email = "";

        public String getPhoneNumber() {
            return phoneNumber;
        }

        private String phoneNumber = "";
        public static final String contentType = "application/json";
        public RegisterPara(String username,String password)
        {
            this.username = username;
            this.password = password;
            this.email = "";
            this.phoneNumber = "";
        }
        public RegisterPara(String username,String password,String email)
        {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phoneNumber = "";
        }
        public RegisterPara(String username,String password,String email,String phoneNumber)
        {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public String toString()
        {
            StringBuilder sb = new StringBuilder("{");
            sb.append("\"username\":\"");
            sb.append(this.username.toString());
            sb.append("\",\"password\":\"");
            sb.append(this.password.toString());

            sb.append("\",\"email\":\"");
            sb.append(this.email.toString());

            sb.append("\",\"phoneNumber\":\"");
            sb.append(this.phoneNumber.toString());
            sb.append("\"}");
            return sb.toString();
        }
    }
    static public String readJsonData(InputStream in) throws IOException
    {
        if(null == in)
        {
            return "";
        }
        String line = "";
        StringBuilder sb = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(in));
        while ((line = rd.readLine()) != null)
        {
            sb.append(line);
        }
        return sb.toString();
    }
}


