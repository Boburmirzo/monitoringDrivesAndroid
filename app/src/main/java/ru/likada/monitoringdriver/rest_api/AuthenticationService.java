package ru.likada.monitoringdriver.rest_api;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import ru.likada.monitoringdriver.LoginActivity;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.HTTP;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.IP_ADDRESS;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.SERVER_CONFIG;

/**
 * Created by bumur on 21.04.2017.
 */

public class AuthenticationService extends AsyncTask<String, String, String> {

    private static String PARAMETERS;
    private static final String LOGIN="login";
    LoginActivity loginActivity;

    public AuthenticationService(String mLogin, String mPassword, LoginActivity loginActivity) {
        PARAMETERS="?username="+mLogin+"&password="+mPassword;
        this.loginActivity=loginActivity;

    }
    @Override
    protected void onPreExecute() {
        loginActivity.showLoadDialog();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = null;

        String retSrc = null;
        try {
            httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(HTTP+IP_ADDRESS+SERVER_CONFIG + LOGIN + PARAMETERS);
            post.setHeader("Content-Type", "application/json");
            HttpResponse httpResponse = new DefaultHttpClient().execute(post);
            if(httpResponse.getStatusLine().getStatusCode()==200) {
                retSrc = EntityUtils.toString(httpResponse.getEntity());
                Log.d(TAG, retSrc);
            }else {
                return null;
            }

        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in authentication: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Error in authentication: " + e.toString() + "\nMessage: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error in authentication: " + e.toString() + "\nMessage: " + e.getMessage());
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return retSrc;
    }

    @Override
    protected void onPostExecute(String token) {
        if(token!=null && !token.isEmpty()) {
            SharedPreferences mPrefs = loginActivity.getSharedPreferences("", MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Set<String> setOfNamesForImages = null;
            if(mPrefs.contains("setOfTokensOfUser")){
                setOfNamesForImages = mPrefs.getStringSet("setOfTokensOfUser",null);
            }
            setOfNamesForImages = new HashSet<>();
            setOfNamesForImages.add(token);
            prefsEditor.putStringSet("setOfTokensOfUser", setOfNamesForImages);
            prefsEditor.putString("token", token);
            prefsEditor.apply();
            loginActivity.goToMainActivity();
            loginActivity.stopShowDialog();
        }
        else
            Toast.makeText(loginActivity, "Не правильный пароль или логин", Toast.LENGTH_LONG).show();
        loginActivity.stopShowDialog();

    }
}
