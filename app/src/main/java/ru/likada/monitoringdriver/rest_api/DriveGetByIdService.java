package ru.likada.monitoringdriver.rest_api;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.likada.monitoringdriver.modeTest.Drive;

import static android.content.ContentValues.TAG;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.SERVER_CONFIG;

/**
 * Created by bumur on 22.04.2017.
 */

public class DriveGetByIdService extends AsyncTask<String, String, Drive> {

    private static String PARAMETERS;
    private static final String GET_DRIVE_BY_ID="/getById";
    public DriveGetByIdService(String mId, String mToken) {
        PARAMETERS="?driveId="+mId+"&token="+mToken;
    }
    @Override
    protected Drive doInBackground(String... params) {
        HttpClient httpclient = null;

        String jaxrsmessage = null;
        try {
            httpclient = new DefaultHttpClient();

            HttpGet request = new HttpGet(SERVER_CONFIG +"getDrives"+GET_DRIVE_BY_ID+PARAMETERS);

            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            jaxrsmessage = read(instream);
            Log.d(TAG, jaxrsmessage);

        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        Drive drive=null;
        if(jaxrsmessage!=null){
            Gson gson = new Gson();
            drive = gson.fromJson(jaxrsmessage, Drive.class);
            Log.d("status",drive.getStatus());
        }
        return drive;
    }

    private static String read(InputStream instream) {
        StringBuilder sb = null;
        try {
            sb = new StringBuilder();
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    instream));
            for (String line = r.readLine(); line != null; line = r.readLine()) {
                sb.append(line);
            }

            instream.close();
            r.close();

        } catch (IOException e) {
            Log.e(TAG, "Se ha producido un error: " + e.toString() + "\nMensaje: " + e.getMessage());
        }
        return sb.toString();

    }
}
