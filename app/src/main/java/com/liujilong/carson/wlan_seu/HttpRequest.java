package com.liujilong.carson.wlan_seu;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpRequest {

    private static int tick = 0;
    private class MyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    private class MyTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
    public  String httpsPost(String url, String input) throws InterruptedException {
        try{
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
            HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();

            conn.setConnectTimeout(10000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();

            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(input);
            bw.flush();
            bw.close();



            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line);
            return sb.toString();
        }catch(Exception e){
            Log.e(this.getClass().getName(), e.getMessage());
            if (tick>=20){
                tick=0;
                return "unable to find host";
            }
            tick++;
            Log.i("tag","tick="+tick);
            Thread.sleep(2000);
            return this.httpsPost(url,input);
        }
    }
}
