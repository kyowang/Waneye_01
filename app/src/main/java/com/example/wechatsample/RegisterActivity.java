package com.example.wechatsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wechatsample.HttpUtil.*;
import com.example.wechatsample.WanEyeUtil.RegisterPara;


public class RegisterActivity extends Activity {
    private EditText um;
    private EditText pw;
    private EditText confirm;
    private EditText email;
    private EditText phone;
    private TextView alarm;
    private Button registerButton;

    private String usernameString;
    private String passwordString;
    private String confirmString;
    private String emailString;
    private String phoneString;
    WanEyeUtil weu = new WanEyeUtil();
    RegisterPara rp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        um = (EditText)findViewById(R.id.et_username);
        pw = (EditText)findViewById(R.id.et_password);
        confirm = (EditText)findViewById(R.id.et_confirm);
        email = (EditText)findViewById(R.id.et_email);
        phone = (EditText)findViewById(R.id.et_phone);
        alarm = (TextView)findViewById(R.id.tv_alarm);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameString = um.getText().toString();
                passwordString = pw.getText().toString();
                confirmString = confirm.getText().toString();
                emailString = email.getText().toString();
                phoneString = phone.getText().toString();
                if(paraValidation(usernameString,passwordString,confirmString,emailString,phoneString))
                {
                    rp = weu.new RegisterPara(usernameString,passwordString,emailString,phoneString);
                    try
                    {
                        WanEyeUtil.doRegister(rp);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally {

                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Parameter check failed!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean paraValidation(String um, String pw, String confirm, String email, String phone)
    {
        if(""== um || ""== pw || ""== confirm)
        {
            alarm.setText("username and password could not empty!");
            alarm.setVisibility(View.VISIBLE);
            return false;
        }
        if(! pw.equals(confirm))
        {
            alarm.setText("password not march!");
            alarm.setVisibility(View.VISIBLE);
            return false;
        }
        if( um.length() < 6)
        {
            alarm.setText("length of username should larger than 6!");
            alarm.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
}
