package ch.festigeek.festiscan.communications;

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

public class RequestPOST extends Communication<String> {

    private String mToken;
    private String mRequest = "";
    private Map<String, String> mContent;

    public RequestPOST(ICallback<String> callback, String token, String request, Map<String, String> content) {
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
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Authorization", "bearer " + mToken);
            connection.setUseCaches(false);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            bw.write(getPostData(mContent));
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

    private String getPostData(Map<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}