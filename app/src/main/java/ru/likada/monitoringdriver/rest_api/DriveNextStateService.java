package ru.likada.monitoringdriver.rest_api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ru.likada.monitoringdriver.MainActivity;

import static android.content.ContentValues.TAG;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.HTTP;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.IP_ADDRESS;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.SERVER_CONFIG;

/**
 * Created by bumur on 22.04.2017.
 */

public class DriveNextStateService extends AsyncTask<String, String, String> {

    private static String PARAMETERS;
    private static final String GET_DRIVE_NEXT_STATE="/nextState";
    MainActivity mainActivity;
    private String field;
    public AsyncResponseDriveNextState delegate=null;

    public DriveNextStateService(MainActivity mainActivity, String mId, String mStateTypeId, String mToken, String field) {
        this.mainActivity=mainActivity;
        this.field=field;
        PARAMETERS="?driveId="+mId+"&stateTypeId="+mStateTypeId+"&token="+mToken;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = null;
        String jaxrsmessage = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(HTTP+IP_ADDRESS+SERVER_CONFIG + "getDrives" + GET_DRIVE_NEXT_STATE + PARAMETERS);
            StringEntity stringEntity = new StringEntity(field, org.apache.http.protocol.HTTP.UTF_8);
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setEntity(stringEntity);
            HttpResponse httpResponse = new DefaultHttpClient().execute(post);
            if(httpResponse.getStatusLine().getStatusCode()==200) {
                InputStream instream = httpResponse.getEntity().getContent();
                jaxrsmessage = read(instream);
                Log.d(TAG, jaxrsmessage);
            }else {
                return null;
            }

        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in getting drives: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
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
//            SharedPreferences mPrefs = mainActivity.getSharedPreferences("", MODE_PRIVATE);
//            SharedPreferences.Editor prefsEditor = mPrefs.edit();
//            prefsEditor.putString("drives", result);
//            prefsEditor.apply();
            delegate.processFinishGetDriveNextState(result);
//            mainActivity.stopShowDialog();
        }
//        else
//            Toast.makeText(mainActivity, "Произошла ошибка при получении данных", Toast.LENGTH_LONG).show();
//        mainActivity.stopShowDialog();

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
