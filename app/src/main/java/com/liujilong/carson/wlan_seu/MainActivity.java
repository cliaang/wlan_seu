package com.liujilong.carson.wlan_seu;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends Activity {

    public final static String FILE_NAME = "com.liujilong.carson.wlan_seu";
    public  File file;
    private TextView username, password,result;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = new File(MainActivity.this.getFilesDir(), FILE_NAME);
        Log.i("tag",file.exists()?"exist":"not exist");
        boolean has_username=getIntent().getBooleanExtra("has username",false);
        if (!file.exists()||has_username) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            username = (TextView) findViewById(R.id.username);
            password = (TextView) findViewById(R.id.password);
            result = (TextView) findViewById(R.id.result);
            findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("tag",file.exists()?"exist":"not exist");
                    String input = "username=" + username.getText().toString() + "&password=" + password.getText().toString();
                    try {
                        FileOutputStream ops = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                        ops.write(input.getBytes());
                        ops.close();
                        Log.i("tag",file.exists()?"exist":"not exist");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(MainActivity.this, AtyLogin.class));
                    finish();
                }
            });
            if (has_username){
                String res = "";
                try{
                    FileInputStream fin = openFileInput(MainActivity.FILE_NAME);
                    int length = fin.available();
                    byte [] buffer = new byte[length];
                    fin.read(buffer);
                    res = EncodingUtils.getString(buffer, "UTF-8");
                    fin.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                int stringIndex = res.indexOf("&password");
                String user_name = res.substring(9, stringIndex);
                String pass=res.substring(stringIndex+10);
                username.setText(user_name);
                password.setText(pass);
            }
        }
        else {
            Intent i = new Intent(MainActivity.this, AtyLogin.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}