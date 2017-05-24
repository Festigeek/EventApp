package ch.festigeek.festiscan.communications;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import ch.festigeek.festiscan.interfaces.ICallback;
import ch.festigeek.festiscan.interfaces.IConstant;

public class RequestPUT extends Communication<String> implements IConstant{

    private String mToken;
    private String mRequest = "";
    private Map<String, Integer> mContent;

    public RequestPUT(ICallback<String> callback, String token, String request, Map<String, Integer> content) {
        setCallback(callback);
        mToken = token;
        mRequest = request;
        mContent = content;
    }

    @Override
    protected String communication() {
        StringBuilder body = new StringBuilder();
        try {
            URL url = new URL(mRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Authorization", "bearer " + mToken);
            connection.setUseCaches(false);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(getPostIntData(mContent));
            bw.flush();
            bw.close();

            int status = connection.getResponseCode();
            InputStream is;
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                body.append(line + "\n");
            }
            br.close();
            if (status != HttpURLConnection.HTTP_OK) {
                setException(new Exception(body.toString()));
            }
        } catch (IOException e) {
            setException(e);
        }
        return body.toString();
    }

    private String getPostIntData(Map<String, Integer> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, Integer> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }
}