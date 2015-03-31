package com.liujilong.carson.wlan_seu;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;


public class AtyLogin extends ActionBarActivity {
    private TextView userInfo, tv_answer;
    private Button changeBtn;
    private Button connectBtn;
    public  File file;
    private String res="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        file = new File(AtyLogin.this.getFilesDir(), MainActivity.FILE_NAME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);
        userInfo= (TextView) findViewById(R.id.textView);
        tv_answer= (TextView) findViewById(R.id.tv_answer);
        Log.i("tag", file.exists() ? "exist" : "not exist");
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
        String username = res.substring(9, stringIndex);
        userInfo.setText(userInfo.getText()+username);

        findViewById(R.id.btn_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                startActivity(new Intent(AtyLogin.this, MainActivity.class));
            }
        });
        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        HttpRequest hr = new HttpRequest();
                        String ans = hr.httpsPost(params[0], params[1]);
                        return ans;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        tv_answer.setText(s);
                    }
                }.execute("https://w.seu.edu.cn/portal/login.php",res);
            }
        });
    }

}
