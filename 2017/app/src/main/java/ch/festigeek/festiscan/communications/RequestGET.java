package ch.festigeek.festiscan.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.festigeek.festiscan.interfaces.ICallback;

public class RequestGET extends Communication<String> {

    private String mToken;
    private String mRequest = "";

    public RequestGET(ICallback<String> callback, String token, String request) {
        setCallback(callback);
        mToken = token;
        mRequest = request;
    }

    @Override
    protected String communication() {
        String body = null;
        try {
            URL url = new URL(mRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", "bearer " + mToken);
            connection.setRequestProperty("connection", "close");
            int status = connection.getResponseCode();
            InputStream is;
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String line;
            body = "";
            while ((line = br.readLine()) != null) {
                body += line + "\n";
            }
            br.close();
            connection.disconnect();
            if (status != HttpURLConnection.HTTP_OK) {
                setException(new Exception(body));
            }
        } catch (IOException ex) {
            setException(ex);
        }
        return body;
    }
}