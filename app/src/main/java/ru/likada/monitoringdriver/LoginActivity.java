package ru.likada.monitoringdriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Set;

import ru.likada.monitoringdriver.rest_api.AuthenticationService;
import ru.likada.monitoringdriver.rest_api.ServerConfig;

/**
 * Created by bumur on 23.04.2017.
 */

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    EditText login, password, ipAdress;
    Context context;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        Set<String> setOfNamesForImages = null;
        if(mPrefs.contains("setOfTokensOfUser")) {
            setOfNamesForImages = mPrefs.getStringSet("setOfTokensOfUser", null);
            if (mPrefs.contains("token") && mPrefs.contains("iPconfiguration")) {
                if(setOfNamesForImages!=null) {
                    if (setOfNamesForImages.contains(mPrefs.getString("token", ""))) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Spinner dynamicSpinner = (Spinner) findViewById(R.id.static_spinner);

        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.ip_addresses_array,
                        android.R.layout.simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        staticAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        dynamicSpinner.setAdapter(staticAdapter);

        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ServerConfig.setIpAddress((String) parent.getItemAtPosition(position));
                SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                prefsEditor.putString("iPconfiguration", (String) parent.getItemAtPosition(position));
                prefsEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Выберете IP адрес", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.relative_layoutLogin).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        login=(EditText) findViewById(R.id.etUserName);
        password=(EditText) findViewById(R.id.etPass);
//        ipAdress=(EditText) findViewById(R.id.etIPaddress);
        loginButton=(Button) findViewById(R.id.btnSingIn);
        context=this;


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!login.getText().toString().isEmpty() && !password.getText().toString().isEmpty()) {
//                    if(!ipAdress.getText().toString().isEmpty() && validIP(ipAdress.getText().toString())){
//                        ServerConfig.setIpAddress(ipAdress.getText().toString());
                        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                        prefsEditor.putString("iPconfiguration", ipAdress.getText().toString());
                        prefsEditor.putString("userLogin", login.getText().toString());
                        prefsEditor.apply();

//                    }
//                else{
//                        Toast.makeText(getApplicationContext(), "Введите IP адрес", Toast.LENGTH_LONG).show();
//                    }
                    initAuthorization(login.getText().toString(),password.getText().toString());
                }else {
                    Toast.makeText(getApplicationContext(), "Введите логин и пароль ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validIP (String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            if ( ip.endsWith(".") ) {
                return false;
            }

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public void showLoadDialog(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Соединение с сервером...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    public void stopShowDialog(){
        progressDialog.dismiss();
    }

    public void goToMainActivity(){
        Toast.makeText(getApplicationContext(), "Правильный пароль и логин", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private boolean initAuthorization(String username, String password){
        AuthenticationService authenticationService = new AuthenticationService(username,password,this);
        authenticationService.execute();
        return false;
    }
}
