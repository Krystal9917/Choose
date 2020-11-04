package com.info.choose;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    private static String prefix = "http://10.61.186.127:9999/android";

    public static String request(String path, JSONObject data) {

        String response = null;
        HttpURLConnection connection;
        try {
            URL url = new URL(prefix+path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.getOutputStream().write(String.valueOf(data).getBytes());
            InputStream in = connection.getInputStream();
            // read output stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();
        } catch (Exception e) {
            Log.i(GlobalData.ERROR_TAG,e.toString());
        }
        return response;
    }
}
