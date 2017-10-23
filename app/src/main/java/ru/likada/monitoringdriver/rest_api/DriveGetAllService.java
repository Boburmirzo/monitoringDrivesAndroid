package ru.likada.monitoringdriver.rest_api;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import ru.likada.monitoringdriver.LoginActivity;
import ru.likada.monitoringdriver.MainActivity;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.HTTP;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.IP_ADDRESS;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.SERVER_CONFIG;

/**
 * Created by bumur on 21.04.2017.
 */

public class DriveGetAllService extends AsyncTask<String, String, String> {

    private static String PARAMETERS;
    private static final String GET_ALL_DRIVES="getDrives";
    MainActivity mainActivity;
    public AsyncResponse delegate=null;
    public DriveGetAllService(String mToken, MainActivity mainActivity) {
        PARAMETERS="?token="+mToken;
        this.mainActivity=mainActivity;
    }

    @Override
    protected void onPreExecute() {
        mainActivity.showLoadDialog();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = null;

        String jaxrsmessage = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(HTTP+IP_ADDRESS+SERVER_CONFIG + GET_ALL_DRIVES + PARAMETERS);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            jaxrsmessage = read(instream);
            Log.d(TAG, jaxrsmessage);

        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        }catch (HttpHostConnectException e) {
            handleConnectionExceptions();
        }catch (ConnectException e) {
            handleConnectionExceptions();
        } catch (UnsupportedEncodingException e) {
            handleConnectionExceptions();
        }
        catch (IOException e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return jaxrsmessage;
    }

    @Override
    protected void onPostExecute(String result) {
        if(result!=null && !result.isEmpty()) {
            delegate.processFinish(result);
            mainActivity.stopShowDialog();
        }
        else
            Toast.makeText(mainActivity, "Произошла ошибка при получении данных", Toast.LENGTH_LONG).show();
        mainActivity.stopShowDialog();
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

    private void handleConnectionExceptions(){
        SharedPreferences myPrefs = mainActivity.getSharedPreferences("",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        mainActivity.startActivity(intent);
        mainActivity.finish();
    }
}
