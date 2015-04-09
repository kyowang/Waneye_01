package com.example.wechatsample;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends Activity {
    private EditText username ;
    private EditText passwd ;
    private TextView tv;
    private Button login;
    private String um;
    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText)findViewById(R.id.userName);
        passwd = (EditText)findViewById(R.id.password);
        tv = (TextView)findViewById(R.id.textView);
        login = (Button)findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(Color.GRAY);
                um = username.getText().toString();
                pw = passwd.getText().toString();
                tv.setText("Login Ongoing...");
                tv.setVisibility(View.VISIBLE);
                new LoginTask().execute("Nothing");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private class LoginTask extends AsyncTask<String , Void, Boolean>{
        protected Boolean doInBackground(String... urls)
        {
            boolean result = false;
            try
            {
                result = WanEyeUtil.doLogin(um,pw);
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return result;
        }
        protected void onPostExecute(Boolean result)
        {
            if(result)
            {
                tv.setText("Login success...");
            }
            else
            {
                tv.setText("Login failed...");
            }
            tv.setVisibility(View.VISIBLE);
        }
    }
}
