package org.mamkschools.mhs.fbla_mobileapp_2016.lib;


import android.content.Context;

import org.json.JSONObject;
import org.mamkschools.mhs.fbla_mobileapp_2016.R;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * SecureAPI singleton class
 */
public class SecureAPI {

    private static SecureAPI mySecureAPI = null;
    private SSLSocketFactory mySocketFactory = null;

    public static SecureAPI getInstance(Context c){
        if( mySecureAPI == null){
            try {
                mySecureAPI = new SecureAPI(c.getApplicationContext()
                        .getResources().openRawResource(R.raw.servercert));
            }catch (Exception e){
                Constants.log(e.getMessage());
            }
        }
        return mySecureAPI;

    }
    public static SecureAPI getInstance() throws Exception{
        if( mySecureAPI == null){
            throw new NullPointerException();
        }
        return getInstance(null);
    }
    private void createSocketFactory(InputStream is) throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca = cf.generateCertificate(is);
        System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());

        String kst = KeyStore.getDefaultType();
        KeyStore ks = KeyStore.getInstance(kst);
        ks.load(null, null);
        ks.setCertificateEntry("ca", ca);

        String tmfa = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfa);
        tmf.init(ks);

        SSLContext sslc = SSLContext.getInstance("TLS");
        sslc.init(null, tmf.getTrustManagers(), null);

        mySocketFactory = sslc.getSocketFactory();
    }

    private String getPostString(HashMap<String, String> params) throws Exception {
        StringBuilder res = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> p : params.entrySet()) {
            if(first)
                first = false;
            else
                res.append("&");

            res.append(URLEncoder.encode(p.getKey(), "UTF-8"));
            res.append("=");
            res.append(URLEncoder.encode(p.getValue(), "UTF-8"));
        }
        return res.toString();
    }

    private String getResponseFromStream(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private SecureAPI(InputStream is) throws Exception {
        this.createSocketFactory(is);
    }

   public JSONObject HTTPSGET(String action) throws Exception {
        URL url = new URL(Constants.API_BASE_URL + action);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setSSLSocketFactory(mySocketFactory);

        String response = getResponseFromStream(conn.getInputStream());
        return new JSONObject(response);
    }

    public JSONObject HTTPSPOST(String action, HashMap<String, String> params) throws Exception {
        URL url = new URL(Constants.API_BASE_URL + action);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

        conn.setSSLSocketFactory(mySocketFactory);
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        bw.write(getPostString(params));
        bw.flush();
        bw.close();
        os.close();
        conn.connect();

        String response = getResponseFromStream(conn.getInputStream());
        return new JSONObject(response);
    }
}
