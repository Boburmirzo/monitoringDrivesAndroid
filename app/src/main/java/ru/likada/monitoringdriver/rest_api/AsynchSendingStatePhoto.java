package ru.likada.monitoringdriver.rest_api;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static ru.likada.monitoringdriver.rest_api.ServerConfig.IP_ADDRESS;
import static ru.likada.monitoringdriver.rest_api.ServerConfig.SERVER_CONFIG;

/**
 * Created by bumur on 02.05.2017.
 */

public class AsynchSendingStatePhoto extends AsyncTask<String, String, String> {

    private static final String GET_DRIVE_UPLOAD_PHOTO="/uploadDriveStateFile";
    private File photoFile;
    public AsynchResponseSendingStatePhoto delegate=null;

    public AsynchSendingStatePhoto(File photoFile) {
        this.photoFile = photoFile;
    }

    @Override
    protected String doInBackground(String... params) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ServerConfig.HTTP+IP_ADDRESS+SERVER_CONFIG + "getDrives" +GET_DRIVE_UPLOAD_PHOTO);
        FileBody fileContent= new FileBody(photoFile);
        try {
            StringBody comment = new StringBody("Filename: " + photoFile.getName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", fileContent);
        httppost.setEntity(reqEntity);
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if(result!=null && !result.isEmpty()) {
            delegate.processFinishSendingStatePhoto(result);
        }


    }
}
