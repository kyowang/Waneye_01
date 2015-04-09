package com.example.wechatsample;


import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by wg on 2015/4/8.
 */
public  class WanEyeUtil {
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
    static public String getRegisterUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.register_uri;
    }
    static public String getLoginUrl()
    {
        return "http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.register_uri;
    }
    static public String getPostLocationUrl()
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postLocation_uri);
    }
    static public String getPostStarEyeInstanceUrl()
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStarEyeInstance_uri);
    }
    static public String getInstanceUrl()
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.getInstance_uri);
    }
    static public String getPicUrl(String instanceId)
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStareyePic_uri_prefix + instanceId + WanEyeUtil.postStareyePic_uri_midfix + WanEyeUtil.postStareyePic_uri_pic + WanEyeUtil.postStareyePic_uri_postfix);
    }
    static public String getChartUrl(String instanceId)
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.getChart_prefix + instanceId + WanEyeUtil.getChart_postfix);
    }
    static public String getPicUrlFinish(String instanceId,String picId)
    {
        return encUrlNoParameter("http://" + WanEyeUtil.server_address + ":" + WanEyeUtil.server_port + WanEyeUtil.postStareyePic_uri_prefix + instanceId + WanEyeUtil.postStareyePic_uri_midfix + picId + WanEyeUtil.postStareyePic_uri_postfix);
    }

    static public boolean doLogin(String username, String passwd) throws IOException
    {
        Log.d("WanEyeUtil","doLogin");
        InputStream in = null;
        StringBuffer sb = new StringBuffer("j_username=");
        sb.append(username);
        sb.append("&j_password=");
        sb.append(passwd);
        sb.append("&login=");
        HttpUtil hu = new HttpUtil();
        in = hu.httpRequestPost(getLoginUrl(),sb.toString(),"application/x-www-form-urlencoded");

        if(hu.getResponseCode() == HttpURLConnection.HTTP_OK)
        {
            cookieAuth = hu.getHeaderField("Set-Cookie");
            return true;
        }
        return false;
    }

    static public int doRegister(RegisterPara rp) throws IOException
    {
        Log.d("WanEyeUtil","doRegister");
        InputStream in = null;
        HttpUtil hu = new HttpUtil();
        in = hu.httpRequestPost(getRegisterUrl(),rp.toString(),RegisterPara.contentType);

        return hu.getResponseCode();
    }
    public class RegisterPara
    {
        private String username = "";
        private String password = "";
        private String email = "";
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
}


