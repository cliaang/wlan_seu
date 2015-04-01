package com.liujilong.carson.wlan_seu;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;


public class AtyLogin extends ActionBarActivity {
    private TextView userInfo, tv_answer;
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
                Intent i = new Intent(AtyLogin.this, MainActivity.class);
                i.putExtra("has username",true);
                startActivity(i);
                finish();
            }
        });
        findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<String, Integer, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        if(!wifiManager.isWifiEnabled()){
                            publishProgress(0);  //opining wifi
                            wifiManager.setWifiEnabled(true);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            while (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException ie) {
                                }
                            }
                        }
                        publishProgress(1);  //wifi open success
                        Log.i("tag",wifiManager.isWifiEnabled()?"wife enabled":"wife disabled");
                        WifiConfiguration wifiConfig = new WifiConfiguration();
                        wifiConfig.SSID = "\""+"seu-wlan"+"\"";
                        wifiConfig.status = WifiConfiguration.Status.DISABLED;
                        wifiConfig.priority = 40;
                        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                        wifiConfig.allowedAuthAlgorithms.clear();
                        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//                        WifiConfiguration tempConfig = null;
////                        try {
//                            tempConfig = IsExsits("seu-wlan");
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

//                        if (tempConfig != null) {
//                            wifiManager.removeNetwork(tempConfig.networkId);
//                        }
                        publishProgress(2);  //connecting to seu_wlan
                        int netID = wifiManager.addNetwork(wifiConfig);
                        Log.i("tag","netId: "+netID);
                        boolean enabled = wifiManager.enableNetwork(netID, true);
                        Log.i("tag", "enableNetwork status enable=" + enabled);
                        boolean connected = wifiManager.reconnect();
                        Log.i("tag", "enableNetwork connected=" + connected);


                        publishProgress(3);  //start to login
                        HttpRequest hr = new HttpRequest();
                        String ans = null;
                        try {
                            ans = hr.httpsPost(params[0], params[1]);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return ans;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        String[] messages = new String[]{"opining wifi", "wifi open success","connecting to seu_wlan","start to login"};
                        tv_answer.setText(messages[values[0]]);
                        super.onProgressUpdate(values);
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if (s!=null){
                            char[] convertBuf=new char[2];
                            tv_answer.setText(loadConvert(s.toCharArray(),0,s.length(),convertBuf));
                        }
                        else
                            tv_answer.setText("null result");
                    }
                }.execute("https://w.seu.edu.cn/portal/login.php",res);
            }
        });
    }

    private  String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if (convtBuf.length < len) {
            int newLen = len * 2;
            if (newLen < 0) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
        char aChar;
        char[] out = convtBuf;
        int outLen = 0;
        int end = off + len;

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if(aChar == 'u') {
                    // Read the xxxx
                    int value=0;
                    for (int i=0; i<4; i++) {
                        aChar = in[off++];
                        switch (aChar) {
                            case '0': case '1': case '2': case '3': case '4':
                            case '5': case '6': case '7': case '8': case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a': case 'b': case 'c':
                            case 'd': case 'e': case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A': case 'B': case 'C':
                            case 'D': case 'E': case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed \\uxxxx encoding.");
                        }
                    }
                    out[outLen++] = (char)value;
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    out[outLen++] = aChar;
                }
            } else {
                out[outLen++] = (char)aChar;
            }
        }
        return new String (out, 0, outLen);
    }

}
