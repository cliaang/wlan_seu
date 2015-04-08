package com.liujilong.carson.wlan_seu;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends Activity {

//    public final static String FILE_NAME = "com.liujilong.carson.wlan_seu";
//    public  File file;
    private TextView username, password;
    private String userName, passWord;

    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         sp = getApplicationContext().getSharedPreferences("USER_INFO",Context.MODE_PRIVATE);
        userName = sp.getString("userName","");
        passWord = sp.getString("passWord","");
//        file = new File(MainActivity.this.getFilesDir(), FILE_NAME);
//        Log.i("tag",file.exists()?"exist":"not exist");
        boolean needToChange=getIntent().getBooleanExtra("Change User Info",false);
        if (userName.equals("")||passWord.equals("")||needToChange) {
            setContentView(R.layout.activity_main);
            username = (TextView) findViewById(R.id.username);
            password = (TextView) findViewById(R.id.password);
            username.setText(userName);
            password.setText(passWord);
            findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("userName",username.getText().toString());
                    editor.putString("passWord",password.getText().toString());
                    editor.apply();
                    startActivity(new Intent(MainActivity.this, AtyLogin.class));
                    finish();
                }
            });
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