package ru.likada.monitoringdriver;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import ru.likada.monitoringdriver.app.Config;
import ru.likada.monitoringdriver.modeTest.DestinationWrapper;
import ru.likada.monitoringdriver.modeTest.Drive;
import ru.likada.monitoringdriver.modeTest.Field;
import ru.likada.monitoringdriver.modeTest.SourceWrapper;
import ru.likada.monitoringdriver.rest_api.AsyncResponse;
import ru.likada.monitoringdriver.rest_api.AsyncResponseDriveNextState;
import ru.likada.monitoringdriver.rest_api.AsyncResponseGCMTokenSending;
import ru.likada.monitoringdriver.rest_api.AsynchResponseSendingStatePhoto;
import ru.likada.monitoringdriver.rest_api.AsynchSendingStatePhoto;
import ru.likada.monitoringdriver.rest_api.DriveGetAllService;
import ru.likada.monitoringdriver.rest_api.DriveGetByIdService;
import ru.likada.monitoringdriver.rest_api.DriveNextStateService;
import ru.likada.monitoringdriver.rest_api.DriveStopStateService;
import ru.likada.monitoringdriver.rest_api.GCMTokenSendingService;
import ru.likada.monitoringdriver.rest_api.ServerConfig;
import ru.likada.monitoringdriver.util.NotificationUtils;

import static ru.likada.monitoringdriver.R.id.tableLayout2;

public class MainActivity extends AppCompatActivity implements AsyncResponse, AsyncResponseDriveNextState,AsyncResponseGCMTokenSending, AsynchResponseSendingStatePhoto,
        NavigationView.OnNavigationItemSelectedListener{

    RadioGroup radioGroup;
    Context context;
    ProgressDialog progressDialog;
    int driveId=0;
    final int DIALOG_EXIT = 1;
    int idButtonForAlertDialog ;
    List<Drive> drives = new ArrayList<>();
    List<List<View>> listchangeView;
    private final String LOGTAG = "FingerPrintActivity";
    private static final String TAG = MainActivity.class.getSimpleName();
    TabHost tabHost;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    Map<Integer, List<File>> integerListMapOfImages = new HashMap<>();
    String iPconfiguration="";
    int tabhostKeepState = 0;
    private CoordinatorLayout coordinatorLayout;
    ImageView dialog_imageview;
    ImageView dialog_imageview2;
    ImageView dialog_imageview3;
    TextView textDescroptionDialog;
    TextView textValueDialog;
    int idOfCurrentImage = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private boolean checkPermissionToUseExternalStorage = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initImagesForMapOfFiles();
        verifyStoragePermissions(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        //add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView text = (TextView) header.findViewById(R.id.navigationHeaderUserName);
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String userLogin="";
        if (mPrefs.contains("userLogin")) {
            userLogin=mPrefs.getString("userLogin","");
        }
        text.setText(userLogin);
        if (mPrefs.contains("iPconfiguration")) {
            iPconfiguration=mPrefs.getString("iPconfiguration","");
            ServerConfig.setIpAddress(iPconfiguration);
        }
        context=this;

        initDrives();
        sendGCMTokenToServer();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    initDrives();
                }
            }
        };

    }

    private void initImagesForMapOfFiles() {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        if(mPrefs.contains("numberOfTabHosts")){
            String numberOfTabHosts = mPrefs.getString("numberOfTabHosts","");
            for(int j=0; j<Integer.valueOf(numberOfTabHosts); j++){
                integerListMapOfImages.put(j,new ArrayList<File>());
            }
            for(int i=0; i<integerListMapOfImages.size();i++){
                Set<String> setOfNamesForImages = mPrefs.getStringSet("TabHostNumber"+i,null);
                List<String> listOfImagesName = new ArrayList<>(setOfNamesForImages);
                for (int x =0; x<listOfImagesName.size(); x++){
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    integerListMapOfImages.get(i).add(new File(dir, listOfImagesName.get(x)));
                }
            }
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }else {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "Вы дали доступ для загрузки фотографии",Toast.LENGTH_LONG).show();
                    SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putBoolean("flagForPermission", true);
                    prefsEditor.apply();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    checkPermissionToUseExternalStorage = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tools) {
            // Handle the camera action
        } else if (id == R.id.nav_exit) {
            SharedPreferences myPrefs = getSharedPreferences("",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.remove("token");
            editor.remove("iPconfiguration");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        } else if (id == R.id.nav_about) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onGroupItemClick(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.group_item2) {
            SharedPreferences myPrefs = getSharedPreferences("",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.remove("token");
            editor.remove("iPconfiguration");
            editor.apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    private void sendGCMTokenToServer() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String gcmToken = pref.getString("regId", null);
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        GCMTokenSendingService gcmTokenSendingService = new GCMTokenSendingService(token,gcmToken);
        gcmTokenSendingService.delegate=this;
        gcmTokenSendingService.execute();

    }

    @Override
    public void processFinishGCMTokenSending(String output) {
        Log.e(TAG, "Receiving response from server: " + output);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Для вызова одного рейса, для каждого таба будет отдельный вызов этого метода
     */
    private Drive getDriveById(String driveId,String token) throws ExecutionException, InterruptedException {
        DriveGetByIdService driveGetByIdService = new DriveGetByIdService(driveId,token);
        Drive drive = driveGetByIdService.execute().get();
        return drive;
    }
    /**
     *  метод для перехода на другое состание из текущего
     */
    private void goToNextState(String driveId, String driveStateId, String token) throws ExecutionException, InterruptedException {
        Field field = new Field();
        field.setCountLoad(textValueDialog.getText().toString());
        field.setDescription(textDescroptionDialog.getText().toString());
        Gson gson = new Gson();
        String fieldString = gson.toJson(field);
        DriveNextStateService driveNextStateService = new DriveNextStateService(this,driveId,driveStateId,token,fieldString);
        driveNextStateService.delegate=this;
        driveNextStateService.execute();
    }

    @Override
    public void processFinishGetDriveNextState(String output) {
        parseDriveJsonIntoDriveObject(output);
    }

    private Drive parseDriveJsonIntoDriveObject(String driveJson){
        Drive drive = null;
        if (driveJson != null) {
            Gson gson = new Gson();
            drive = gson.fromJson(driveJson, Drive.class);
            Log.d("status", drive.getId());
        }
        return drive;
    }

    private void initDrives(){
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        DriveGetAllService driveGetAllService = new DriveGetAllService(token,this);
        driveGetAllService.delegate=this;
        driveGetAllService.execute();
    }

    @Override
    public void processFinish(String output) {
        drives = parseDrivesJsonIntoDriveObjectList(output);
        if(!drives.isEmpty()) {
            View topLevelLayout = findViewById(R.id.top_layout_not_drive);
            if(topLevelLayout.getVisibility()==View.VISIBLE)
                topLevelLayout.setVisibility(View.INVISIBLE);
            listchangeView = new ArrayList<>();
            for (int i = 0; i < this.drives.size(); i++)
                listchangeView.add(i, new ArrayList<View>());
            initTabHost();
        }
        else {
            View topLevelLayout = findViewById(R.id.top_layout_not_drive);
            TextView stateMessage = (TextView)findViewById(R.id.txtValueAdditionalState_not_drive);
            stateMessage.setText("Без рейсов");
            topLevelLayout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onStop() {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        for(Drive drive : drives){
            prefsEditor.putString("driveStateDescription"+drive.getId(), drive.getDescription());
            prefsEditor.putString("driveStateValue"+drive.getId(), drive.getValue());
        }

        final String tabHostNumber = "TabHostNumber";
        int numberOfTabHosts = integerListMapOfImages.size();
        for(int i=0; i<integerListMapOfImages.size();i++){
            Set<String> setOfNamesForImages = new HashSet<>();
            for(File file : integerListMapOfImages.get(i)){
                setOfNamesForImages.add(file.getName());
            }
            prefsEditor.putStringSet(tabHostNumber+i,setOfNamesForImages);
        }
        prefsEditor.putString("numberOfTabHosts", String.valueOf(numberOfTabHosts));
        prefsEditor.apply();

        super.onStop();
    }

    private List<Drive> parseDrivesJsonIntoDriveObjectList(String drivesJson){
        List<Drive> drives = new ArrayList<>();
        if(drivesJson!=null){
            Gson gson = new Gson();
            try {
                JsonArray jsonArray = new JsonParser().parse(drivesJson).getAsJsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonElement str = jsonArray.get(i);
                    Drive drive = gson.fromJson(str, Drive.class);
                    drives.add(drive);
                    System.out.println(drive.getId());
                }
            } catch (Exception e){
                Log.e(TAG, "Error in getting token: " + e.toString() + "\nMessage: " + e.getMessage());
                SharedPreferences myPrefs = getSharedPreferences("",
                        MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.remove("token");
                editor.remove("iPconfiguration");
                editor.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        }
        return drives;
    }

    public void showLoadDialog(){
        progressDialog = new ProgressDialog(context);
//        progressDialog.setProgressStyle(R.style.ProgressBar)
        progressDialog.setMessage("Соединение с сервером...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.show();

    }

    public void stopShowDialog(){
        progressDialog.dismiss();
    }

    /**
     * Инициализация TabHost
     */
    private void initTabHost(){

        if(tabHost!=null) {
            tabHost.clearAllTabs();
        }
        tabHost = (TabHost) findViewById(android.R.id.tabhost);

        tabHost.setup();

        drawTabSpec(tabHost,filltabNamesList());
        setSelectedTabColor();
        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                driveId= tabHost.getCurrentTab();
                //tabHost.getTabWidget().getChildAt(driveId).setBackgroundColor(Color.parseColor("#FF8000"));
                setSelectedTabColor();
//                Toast.makeText(getBaseContext(), "tabId = " + driveId, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSelectedTabColor() {
        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            tabHost.getTabWidget().getChildAt(i)
                    .setBackgroundColor(Color.parseColor("#3c3e42"));
        }
        tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab())
                .setBackgroundColor(Color.parseColor("#c6ff8000"));
    }


    /**
     * Заполняем ListVIew Названиями поездок
     */
    public List<String> filltabNamesList(){
        List<String> tabNamesList = new ArrayList<>();
        for(int i=0; i<drives.size(); i++){
            tabNamesList.add(i,drives.get(i).getSerial());
            if(!integerListMapOfImages.containsKey(i))
                integerListMapOfImages.put(i,new ArrayList<File>());
        }
        return tabNamesList;
    }


    /**
     * метод отрисовки вкладок вверху экрана
     */
    private void drawTabSpec( TabHost tabHost, List<String> tabNamesList){
        TabHost.TabSpec tabSpec;
        for(int i=0;i<tabNamesList.size();i++){
            tabSpec = tabHost.newTabSpec(String.valueOf(i));
            tabSpec.setContent(tabFactory);
            tabSpec.setIndicator(tabNamesList.get(i));
            tabHost.addTab(tabSpec);
            tabHost.setCurrentTab(tabhostKeepState);
        }
    }

    /**
     * фактори для отрисовки layout в табах
     */
    TabHost.TabContentFactory tabFactory = new TabHost.TabContentFactory() {

        @Override
        public View createTabContent(final String tag) {
            final Drive drive = drives.get(Integer.valueOf(tag));
            View v = getLayoutInflater().inflate(R.layout.tabtest, null);

            final FloatingActionMenu floatingActionMenu = (FloatingActionMenu) v.findViewById(R.id.material_design_android_floating_action_menu);
            FloatingActionButton actionStop = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_stop);
            actionStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=3;
                }
            });

            FloatingActionButton actionSleep = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_sleep);
            actionSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=1;
                }
            });

            FloatingActionButton actionToilet = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_toilet);
            actionToilet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=8;
                }
            });

            FloatingActionButton actionBreaking = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_breaking);
            actionBreaking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=7;
                }
            });

            FloatingActionButton actionInvalidOrder = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_invalid_order);
            actionInvalidOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=9;
                }
            });

            FloatingActionButton actionCancel = (FloatingActionButton) floatingActionMenu.findViewById(R.id.material_design_floating_action_menu_cancel);
            actionCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_EXIT);
                    idButtonForAlertDialog=2;
                }
            });

            TextView txtview8 = (TextView) v.findViewById(R.id.textView8);
            TextView txtview9 = (TextView) v.findViewById(R.id.textView9);
            TextView txtViewCurrentStatus = (TextView) v.findViewById(R.id.txv_current_status);
            TextView currentStatusDate = (TextView) v.findViewById(R.id.currentStatusDate);
            txtViewCurrentStatus.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isActive())
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });

            StringBuilder localStringBuilder1 = new StringBuilder();
            if ((drive.getVehicle() != null) && (!drive.getVehicle().isEmpty()))
                localStringBuilder1.append(drive.getVehicle());
            if ((drive.getDriver() != null) && (!drive.getDriver().isEmpty()))
                localStringBuilder1.append(" ").append(drive.getDriver());
            txtview8.setText(localStringBuilder1.toString());
            StringBuilder localStringBuilder2 = new StringBuilder();
            localStringBuilder2.append("Логист:  ");
            if ((drive.getLogisticianNameFull() != null) && (!drive.getLogisticianNameFull().isEmpty()))
                localStringBuilder2.append(drive.getLogisticianNameFull());
            txtview9.setText(localStringBuilder2.toString());

            listchangeView.get(Integer.valueOf(tag)).add(0,txtViewCurrentStatus);
            StringBuilder stringBuilderCurrentStatusDate = new StringBuilder();
            stringBuilderCurrentStatusDate.append("Текущий статус: ");
            if((drive.getCurrentState() != null) && (drive.getCurrentState().getModifiedDate() != null) && (!drive.getCurrentState().getModifiedDate().isEmpty())){
                stringBuilderCurrentStatusDate.append(drive.getCurrentState().getModifiedDate());
            }
            currentStatusDate.setText(stringBuilderCurrentStatusDate.toString());
            if(drive.getCurrentState()!=null && drive.getCurrentState().getName()!=null) {
                txtViewCurrentStatus.setText(drive.getCurrentState().getName());
            }
            radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);

            //Отрисовываем все источники поставок в таблице

            for(int i=0;i<drive.getSourceWrappers().size();i++)
                addRowsToSourceTable(R.id.tablesource,drive,drive.getSourceWrappers().get(i),v);

            //Отрисовываем все пункты назначения в таблице
            for(int i=0;i<drive.getDestinationWrappers().size();i++)
                addRowsToDestinationTable(R.id.tabledestination,drive,drive.getDestinationWrappers().get(i),v);
            //Buttons



            TextView txtvdescriptionLabel = (TextView) v.findViewById(R.id.txtvdescriptionlabel);
            txtvdescriptionLabel.setText("Примечание");
            EditText txtvdescription = (EditText) v.findViewById(R.id.txtvdescription);
            txtvdescription.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    drives.get(Integer.valueOf(tag)).setDescription(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
            if(mPrefs.contains("driveStateDescription"+drive.getId())) {
                txtvdescription.setText(mPrefs.getString("driveStateDescription"+drive.getId(),""));
            }
            final Button btnNextState = (Button) v.findViewById(R.id.btn_nextstate);

            TextView txtvValueLabel = (TextView) v.findViewById(R.id.txtValuelabel);
            TableLayout tableLayoutValue = (TableLayout) v.findViewById(R.id.tableValue);
            LinearLayout linearLayoutValue = (LinearLayout) v.findViewById(R.id.linearLayout_value_upper);
            txtvValueLabel.setText("Количество");
            EditText txtValue = (EditText) v.findViewById(R.id.txtValue);
            txtValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    drives.get(Integer.valueOf(tag)).setValue(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
//                    if(!s.toString().isEmpty()) {
//                        btnNextState.setClickable(true);
//                    } else {
//                        Toast.makeText(this, "plz enter your name ", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
            if(mPrefs.contains("driveStateValue"+drive.getId())) {
                txtValue.setText(mPrefs.getString("driveStateValue"+drive.getId(),""));
            }
            if(drive.getCurrentState()!=null) {
                if (drive.getCurrentState().getTypeId().equals(4)) {
                    tableLayoutValue.setVisibility(View.VISIBLE);
                    tableLayoutValue.setEnabled(true);
                    linearLayoutValue.setVisibility(View.VISIBLE);
                } else {
                    tableLayoutValue.setVisibility(View.GONE);
                    tableLayoutValue.setEnabled(false);
                    linearLayoutValue.setVisibility(View.GONE);
                }
            }
            //Initializing image button1
            ImageView tableRowImage = (ImageView) v.findViewById(R.id.tableRow_image);

            tableRowImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isActive())
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });

            tableRowImage.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.i("MyTag","Image button is pressed, visible in LogCat");
                        idOfCurrentImage = 0;
                        createSetImageDialog();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });

            //Initializing image button2
            ImageView tableRowImage2 = (ImageView) v.findViewById(R.id.tableRow_image2);

            tableRowImage2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isActive())
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });
            tableRowImage2.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.i("MyTag","Image button2 is pressed, visible in LogCat");
                        idOfCurrentImage = 1;
                        createSetImageDialog();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
            tableRowImage2.setEnabled(false);

            //Initializing image button3
            ImageView tableRowImage3 = (ImageView) v.findViewById(R.id.tableRow_image3);

            tableRowImage3.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isActive())
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    return false;
                }
            });


            tableRowImage3.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.i("MyTag","Image button3 is pressed, visible in LogCat");
                        idOfCurrentImage = 2;
                        createSetImageDialog();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });

            tableRowImage3.setEnabled(false);

            List<File> files = integerListMapOfImages.get(Integer.valueOf(tag));
            if(files.size()>0){
                if(files.size()==1) {
                    tableRowImage.setImageBitmap(putPhotosAfterUpdatingData(files.get(0)));
                    tableRowImage2.setEnabled(true);
                }
                if(files.size()==2) {
                    tableRowImage.setImageBitmap(putPhotosAfterUpdatingData(files.get(0)));
                    tableRowImage2.setImageBitmap(putPhotosAfterUpdatingData(files.get(1)));
                    tableRowImage2.setEnabled(true);
                    tableRowImage3.setEnabled(true);
                }
                if(files.size()==3) {
                    tableRowImage.setImageBitmap(putPhotosAfterUpdatingData(files.get(0)));
                    tableRowImage2.setImageBitmap(putPhotosAfterUpdatingData(files.get(1)));
                    tableRowImage3.setImageBitmap(putPhotosAfterUpdatingData(files.get(2)));
                    tableRowImage2.setEnabled(true);
                    tableRowImage3.setEnabled(true);
                }
            }


            ImageView btnNextStateAdditional = (ImageView) v.findViewById(R.id.btncontinue);

            if(drive.getNextState()!=null && !drive.getCurrentState().isCheckContinue()) {
                btnNextState.setText(">> " + drive.getNextState().getName());
            }else if(drive.getNextState()!=null && drive.getCurrentState().isCheckContinue()){
                String additionalStateName="";
                if (mPrefs.contains("nextState"+drive.getId())) {
                    additionalStateName=mPrefs.getString("nextState"+drive.getId(),"");

                    View topLevelLayout = v.findViewById(R.id.top_layout);
                    TextView stateMessage = (TextView)v.findViewById(R.id.txtValueAdditionalState);
                    stateMessage.setText(additionalStateName);
                    topLevelLayout.setVisibility(View.VISIBLE);

                    btnNextState.setVisibility(View.INVISIBLE);

                    floatingActionMenu.setVisibility(View.INVISIBLE);

                    tableRowImage.setClickable(false);
                    tableRowImage2.setClickable(false);
                    tableRowImage3.setClickable(false);

                    txtvdescription.setFocusable(false);
                    txtvdescription.setFocusableInTouchMode(false);

                    txtValue.setFocusable(false);
                    txtValue.setFocusableInTouchMode(false);
                }
                btnNextState.setText(">> " + drive.getNextState().getName());
            }
            else{
                btnNextState.setText("Рейс Закончен");
                btnNextState.setClickable(false);    }
            TableLayout tableLayout = (TableLayout) v.findViewById(tableLayout2);
            tableLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if(imm.isActive()) {
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    return false;
                }
            });
            listchangeView.get(Integer.valueOf(tag)).add(0,btnNextState);
            listchangeView.get(Integer.valueOf(tag)).add(0,btnNextStateAdditional);
            return v;
        }
    };

    private Bitmap putPhotosAfterUpdatingData(File file){
        Bitmap bitmap;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
                bitmapOptions);
        OutputStream outFile = null;
        try {
            outFile = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
            outFile.flush();
            outFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bitmap.createScaledBitmap(bitmap, 300, 300, true);
    }

    private void addRowsToSourceTable(int tableloyoutId, Drive drive, SourceWrapper sourceWrapper, final View view) {

        TableLayout tableLayout = (TableLayout) view.findViewById(tableloyoutId);
        tableLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm.isActive())
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        TextView txtValueFrom = (TextView) tableLayout.findViewById(R.id.txtValueFrom);
        StringBuilder localStringForFromDate = new StringBuilder();
        localStringForFromDate.append("Товар, Откуда: ");
        if(drive.getLoadTime()!=null){
            localStringForFromDate.append(drive.getLoadTime());
        }
        txtValueFrom.setText(localStringForFromDate.toString());
        TableRow tr = (TableRow) inflater.inflate(R.layout.tablerow, null);
        TextView tv = (TextView) tr.findViewById(R.id.col1);
        StringBuilder localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append("<b>");
        if (drive.getProduct() != null)
            localStringBuilder2.append(drive.getProduct()).append(", ");
        if (sourceWrapper.getObject() != null)
            localStringBuilder2.append(sourceWrapper.getObject()).append(", ");
        if (sourceWrapper.getAddress() != null)
            localStringBuilder2.append(sourceWrapper.getAddress()).append(", ").append("\n");
        if (sourceWrapper.getContact() != null)
            localStringBuilder2.append("\n").append(sourceWrapper.getContact().toString());
        localStringBuilder2.append("</b><br>");
        if (sourceWrapper.getDescription() != null)
            localStringBuilder2.append("<i>").append(sourceWrapper.getDescription()).append("</i>");
        tv.setText(Html.fromHtml(localStringBuilder2.toString()));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(19);
        tv.setTextColor(Color.parseColor("#ffffff"));
        tableLayout.addView(tr);
        if(sourceWrapper.getCoordinates()!=null) {
            final String mapCoordinates = sourceWrapper.getCoordinates();
            final String addressTitle = sourceWrapper.getObject();
            ImageButton imageButtonMap = (ImageButton) tr.findViewById(R.id.btn_map);
            imageButtonMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, GoogleMapActivity.class);
                    i.putExtra("coordinates",mapCoordinates);
                    i.putExtra("addressTitle",addressTitle);
                    startActivity(i);
                }
            });
            imageButtonMap.setVisibility(ImageButton.VISIBLE);
        }
    }

    private void addRowsToDestinationTable(int tableloyoutId, Drive drive, DestinationWrapper destinationWrapper, final View view) {

        TableLayout tableLayout = (TableLayout) view.findViewById(tableloyoutId);
        tableLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FloatingActionMenu floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.material_design_android_floating_action_menu);
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if(imm.isActive())
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
        LayoutInflater inflater = LayoutInflater.from(this);
        TextView txtValueTo = (TextView) tableLayout.findViewById(R.id.txtValueTo);
        StringBuilder localStringForFromDate = new StringBuilder();
        localStringForFromDate.append("Куда: ");
        if(drive.getUnloadTime()!=null){
            localStringForFromDate.append(drive.getUnloadTime());
        }
        txtValueTo.setText(localStringForFromDate.toString());
        TableRow tr = (TableRow) inflater.inflate(R.layout.tablerow, null);
        TextView tv = (TextView) tr.findViewById(R.id.col1);

        StringBuilder localStringBuilder2 = new StringBuilder();
        localStringBuilder2.append("<b>");
        if (drive.getProduct() != null)
            localStringBuilder2.append(drive.getProduct()).append(", ");
        if (destinationWrapper.getObject() != null)
            localStringBuilder2.append(destinationWrapper.getObject()).append(", ");
        if (destinationWrapper.getAddress() != null)
            localStringBuilder2.append(destinationWrapper.getAddress()).append(", ").append("\n");
        if (destinationWrapper.getContact() != null)
            localStringBuilder2.append("\n").append(destinationWrapper.getContact());
        localStringBuilder2.append("</b><br>");
        if (destinationWrapper.getDescription() != null)
            localStringBuilder2.append("<i>").append(destinationWrapper.getDescription()).append("</i>");
        tv.setText(Html.fromHtml(localStringBuilder2.toString()));
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextSize(19);
        tv.setTextColor(Color.parseColor("#ffffff"));
        tableLayout.addView(tr);
        if(destinationWrapper.getCoordinates()!=null) {
            final String mapCoordinates = destinationWrapper.getCoordinates();
            ImageButton imageButtonMap = (ImageButton) tr.findViewById(R.id.btn_map);
            imageButtonMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, GoogleMapActivity.class);
                    i.putExtra("coordinates",mapCoordinates);
                    startActivity(i);;
                }
            });
            imageButtonMap.setVisibility(ImageButton.VISIBLE);
        }

    }

    public void onClickBtnNextState(View view) throws ExecutionException, InterruptedException {
        if(drives.get(driveId).getCurrentState().getTypeId().equals(4)) {
            if(drives.get(driveId).getValue()!=null && !drives.get(driveId).getValue().isEmpty())
                moveToTheNextLevel();
            else
                Toast.makeText(getBaseContext(), "Количество обязательное поле ", Toast.LENGTH_SHORT).show();
        } else
            moveToTheNextLevel();
    }

    private void moveToTheNextLevel(){
        LayoutInflater factory = LayoutInflater.from(MainActivity.this);
        View viewDialogImage = factory.inflate(R.layout.dialog_image, null);
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Вы хотите перейти на следующее состояние?");
        //adb.setMessage(drives.get(driveId).getNextState().getName());
        // иконка
        adb.setIcon(android.R.drawable.ic_dialog_info);
        adb.setMessage(drives.get(driveId).getNextState().getName());
        // кнопка положительного ответа
        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    onClickBtnNextStateCallRest();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        // кнопка отрицательного ответа
        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        textDescroptionDialog = (TextView) viewDialogImage.findViewById(R.id.txtDescriptionDialog);
        EditText txtvdescription = (EditText) tabHost.getCurrentView().findViewById(R.id.txtvdescription);
        textDescroptionDialog.setText(txtvdescription.getText().toString());
        textValueDialog = (TextView) viewDialogImage.findViewById(R.id.txtValueDialog);
        EditText txtValue = (EditText) tabHost.getCurrentView().findViewById(R.id.txtValue);
        textValueDialog.setText(txtValue.getText().toString());
        initializeDialogImages(viewDialogImage);
        adb.setView(viewDialogImage);
        adb.create();
        adb.show();
    }

    private void initializeDialogImages(View viewDialogImage){
        ImageView tableRowImage = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image);
        ImageView tableRowImage2 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image2);
        ImageView tableRowImage3 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image3);

        dialog_imageview = (ImageView) viewDialogImage.findViewById(R.id.dialog_imageview);
        if(tableRowImage.getDrawable() instanceof BitmapDrawable)
            dialog_imageview.setImageBitmap(((BitmapDrawable)tableRowImage.getDrawable()).getBitmap());
        dialog_imageview2 = (ImageView) viewDialogImage.findViewById(R.id.dialog_imageview2);
        if(tableRowImage2.getDrawable() instanceof BitmapDrawable) {
            dialog_imageview2.setImageBitmap(((BitmapDrawable) tableRowImage2.getDrawable()).getBitmap());
        }
        dialog_imageview3 = (ImageView) viewDialogImage.findViewById(R.id.dialog_imageview3);
        if(tableRowImage3.getDrawable() instanceof BitmapDrawable)
            dialog_imageview3.setImageBitmap(((BitmapDrawable)tableRowImage3.getDrawable()).getBitmap());
    }

    private void putTablePhotoToDialogIfExists(){
        List<File> files = integerListMapOfImages.get(tabHost.getCurrentTab());
        if(files.get(idOfCurrentImage)!=null) {
            Bitmap bitmap;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(files.get(idOfCurrentImage).getAbsolutePath(),
                    bitmapOptions);
            OutputStream outFile = null;
            try {
                outFile = new FileOutputStream(files.get(idOfCurrentImage));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                outFile.flush();
                outFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
            // Bitmap resizedForDialog = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            ImageView tableRowImage = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image);
            ImageView tableRowImage2 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image2);
            ImageView tableRowImage3 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image3);
            if(idOfCurrentImage == 0) {
                tableRowImage.setImageBitmap(resized);
                tableRowImage2.setEnabled(true);
            }

            if(idOfCurrentImage == 1) {
                tableRowImage2.setImageBitmap(resized);
                tableRowImage3.setEnabled(true);
            }
            if(idOfCurrentImage == 2) {
                tableRowImage3.setImageBitmap(resized);
            }
        }
    }

    public void onClickBtnNextStateCallRest() throws ExecutionException, InterruptedException {
        Toast.makeText(getBaseContext(), "id drive " + driveId, Toast.LENGTH_SHORT).show();
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token = "";
        if (mPrefs.contains("token")) {
            token = mPrefs.getString("token", "");
        }
        if (drives.get(driveId).getNextState() != null) {
            goToNextState(drives.get(driveId).getId(), String.valueOf(drives.get(driveId).getNextState().getTypeId()), token);
        }
        List<File> files = integerListMapOfImages.get(tabHost.getCurrentTab());
        if(files.size()>0) {
            for (File file:files) {
                AsynchSendingStatePhoto asynchSendingStatePhoto = new AsynchSendingStatePhoto(file);
                asynchSendingStatePhoto.delegate = this;
                asynchSendingStatePhoto.execute();
            }
        }
        integerListMapOfImages.get(tabHost.getCurrentTab()).clear();
        tabhostKeepState = driveId;
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.remove("driveStateDescription"+drives.get(driveId).getId());
        prefsEditor.remove("driveStateValue"+drives.get(driveId).getId());
        final String tabHostNumber = "TabHostNumber";
        for(int i=0; i<integerListMapOfImages.size();i++){
            prefsEditor.remove(tabHostNumber+i);
        }
        prefsEditor.remove("numberOfTabHosts");
        prefsEditor.apply();
        initDrives();
    }

    @Override
    public void processFinishSendingStatePhoto(String output) {
        Log.d("The photo was sent", output);
    }

    public void  onClickSleepButton() throws ExecutionException, InterruptedException {

        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(driveId).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Состояние сна");
            prefsEditor.apply();
            stopDriveState(drives.get(Integer.valueOf(driveId)).getId(), String.valueOf(drives.get(Integer.valueOf(driveId)).getSleepState().getSleepStateId()),token);
            Toast.makeText(getBaseContext(), "Состояние сна", Toast.LENGTH_SHORT).show();
        }

    }

    public void  onClickStopButton() throws ExecutionException, InterruptedException {
        //дублируется код в трех методах с токеном вынести надо в отдельный метод
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(Integer.valueOf(driveId)).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Состояние остановки");
            prefsEditor.apply();
            stopDriveState(drives.get(Integer.valueOf(driveId)).getId(), String.valueOf(drives.get(Integer.valueOf(driveId)).getStopState().getStopeStateId()),token);
            Toast.makeText(getBaseContext(), "Состояние остановки", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickInvalidOrderButton() throws ExecutionException, InterruptedException {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(Integer.valueOf(driveId)).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Состояние неверной заявки");
            prefsEditor.apply();
            stopDriveState(drives.get(Integer.valueOf(driveId)).getId(), String.valueOf(drives.get(Integer.valueOf(driveId)).getInvalidOrderState().getInvalidOrderStateId()),token);
            Toast.makeText(getBaseContext(), "Состояние неверной заявки", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickToiletButton() throws ExecutionException, InterruptedException {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(Integer.valueOf(driveId)).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Cостояние отдыха");
            prefsEditor.apply();
            stopDriveState(drives.get(Integer.valueOf(driveId)).getId(), String.valueOf(drives.get(Integer.valueOf(driveId)).getToiletState().getToiletStateId()),token);
            Toast.makeText(getBaseContext(), "Cостояние отдыха", Toast.LENGTH_SHORT).show();
        }
    }

    private void onClickBreakingButton() throws ExecutionException, InterruptedException {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(Integer.valueOf(driveId)).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Состояние поломки");
            prefsEditor.apply();
            stopDriveState(drives.get(Integer.valueOf(driveId)).getId(), String.valueOf(drives.get(Integer.valueOf(driveId)).getBreakingState().getBreakingStateId()),token);
            Toast.makeText(getBaseContext(), "Состояние поломки", Toast.LENGTH_SHORT).show();
        }
    }

    public void  onClickCancelutton() throws ExecutionException, InterruptedException {
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        if(drives.get(driveId).getNextState()==null){
            Toast.makeText(getBaseContext(), "Рейс закончен", Toast.LENGTH_SHORT).show();
        }
        else{
            prefsEditor.putString("nextState"+drives.get(driveId).getId(), "Рейс закончен");
            prefsEditor.apply();
            stopDriveState(drives.get(driveId).getId(), String.valueOf(drives.get(driveId).getCancelState().getCancelStateId()),token);
            Toast.makeText(getBaseContext(), "Рейс отменен", Toast.LENGTH_SHORT).show();
        }
    }

    private String stopDriveState(String driveId, String driveStateId, String token) throws ExecutionException, InterruptedException {
        DriveStopStateService driveStopStateService = new DriveStopStateService(driveId,driveStateId,token);
        return driveStopStateService.execute().get();
    }

    public void OnClickGoToNextState(View view) throws ExecutionException, InterruptedException {
        onClickBtnContinue();
    }
    public void onClickBtnContinue() throws ExecutionException, InterruptedException {
        Log.d(LOGTAG,"Нажали кнопку продолжить");
        SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
        String token="";
        if (mPrefs.contains("token")) {
            token=mPrefs.getString("token","");
        }
        stopDriveState(drives.get(driveId).getId(), String.valueOf(drives.get(driveId).getContinueState().getContinueStateId()),token);
        Toast.makeText(getBaseContext(), "Продолжение", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.remove("nextState"+drives.get(driveId).getId());
        editor.apply();

        View topLevelLayout = tabHost.getCurrentView().findViewById(R.id.top_layout);
        topLevelLayout.setVisibility(View.INVISIBLE);
        Button nextStateButton = (Button)listchangeView.get(driveId).get(1);
        nextStateButton.setVisibility(View.VISIBLE);

//        ImageView additionalStateMenu = (ImageView)listchangeView.get(driveId).get(2);
//        additionalStateMenu.setVisibility(View.VISIBLE);
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) tabHost.getCurrentView().findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionMenu.setVisibility(View.VISIBLE);
        floatingActionMenu.close(true);
        ImageView tableRowImage = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image);
        tableRowImage.setClickable(true);
        ImageView tableRowImage2 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image2);
        tableRowImage2.setClickable(true);
        ImageView tableRowImage3 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image3);
        tableRowImage3.setClickable(true);

        EditText txtvdescription = (EditText) tabHost.getCurrentView().findViewById(R.id.txtvdescription);
        txtvdescription.setFocusable(true);
        txtvdescription.setFocusableInTouchMode(true);

        EditText txtValue = (EditText) tabHost.getCurrentView().findViewById(R.id.txtValue);
        txtValue.setFocusable(true);
        txtValue.setFocusableInTouchMode(true);
    }

    public void setOverlayForTabhost(String additionalStateMessage){
        View topLevelLayout = tabHost.getCurrentView().findViewById(R.id.top_layout);
        TextView stateMessage = (TextView)topLevelLayout.findViewById(R.id.txtValueAdditionalState);
        stateMessage.setText(additionalStateMessage);
        topLevelLayout.setVisibility(View.VISIBLE);

        Button nextStateButton = (Button)listchangeView.get(driveId).get(1);
        nextStateButton.setVisibility(View.INVISIBLE);

        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) tabHost.getCurrentView().findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionMenu.setVisibility(View.INVISIBLE);

//        ImageView additionalStateMenu = (ImageView)listchangeView.get(driveId).get(2);
//        additionalStateMenu.setVisibility(View.INVISIBLE);

        ImageView tableRowImage = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image);
        tableRowImage.setClickable(false);
        ImageView tableRowImage2 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image2);
        tableRowImage2.setClickable(false);
        ImageView tableRowImage3 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image3);
        tableRowImage3.setClickable(false);

        EditText txtvdescription = (EditText) tabHost.getCurrentView().findViewById(R.id.txtvdescription);
        txtvdescription.setFocusable(false);
        txtvdescription.setFocusableInTouchMode(false);

        EditText txtValue = (EditText) tabHost.getCurrentView().findViewById(R.id.txtValue);
        txtValue.setFocusable(false);
        txtValue.setFocusableInTouchMode(false);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // положительная кнопка
                case Dialog.BUTTON_POSITIVE:
                    //                    ImageView imageView = (ImageView) viewDialogImage.findViewById(R.id.dialog_imageview);
//                    imageView.setImageResource(R.drawable.camera_off);
                    FloatingActionMenu floatingActionMenu = (FloatingActionMenu) tabHost.getCurrentView().findViewById(R.id.material_design_android_floating_action_menu);
                    floatingActionMenu.close(true);
                    Toast.makeText(getBaseContext(), "рейс Продолжен", Toast.LENGTH_SHORT).show();
                    break;
                // негативная кнопка
                case Dialog.BUTTON_NEGATIVE:

                    switch (idButtonForAlertDialog) {
                        case 1:
                            // кнопка спать
                            try {
                                onClickSleepButton();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Состояние сна");
                            break;

                        case 2:
                            // кнопка cancel
                            try {
                                onClickCancelutton();

                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Состояние отмены заявки");
                            Toast.makeText(getBaseContext(), "кнопка отмена", Toast.LENGTH_SHORT).show();
                            break;

                        case 3:
                            // кнопка остановка
                            try {
                                onClickStopButton();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Cостояние остановки");
                            break;
                        case 7:
                            // кнопка остановка
                            try {
                                onClickBreakingButton();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Cостояние поломки");
                            break;
                        case 8:
                            // кнопка остановка
                            try {
                                onClickToiletButton();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Cостояние отдыха");
                            break;
                        case 9:
                            // кнопка остановка
                            try {
                                onClickInvalidOrderButton();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setOverlayForTabhost("Cостояние неверной заявки");
                            break;
                        case 5:
                            // кнопка далее
                            try {
                                onClickBtnContinue();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                    break;
                case Dialog.BUTTON_NEUTRAL:

//                    final CharSequence[] options = { "Сделать фото","Отмена" };
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(FingerPrintActivity.this);
//
//                    builder.setTitle("Загрузить фото!");
//                    builder.setItems(options, new DialogInterface.OnClickListener() {
//
//                        @Override
//
//                        public void onClick(DialogInterface dialog, int item) {
//
//                            if (options[item].equals("Сделать фото"))
//
//                            {
//
//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                File dir=
//                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//
//                                photo=new File(dir, "temp"+idOfCurrentImage+"_"+drives.get(driveId).getId()+".jpg");
//
//                                // File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp_"+drives.get(driveId).getId()+".jpg");
//
//                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//                                //pic = f;
//
//                                startActivityForResult(intent, 1);
//                                idButtonForAlertDialog=4;
//                                showDialog(DIALOG_NEXT_STATE);
//
//                            }
//
////                            else if (options[item].equals("Выбрать из галереи"))
////
////                            {
////
////                                Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
////                                startActivityForResult(intent, 2);
////
////                                idButtonForAlertDialog=4;
////                                showDialog(DIALOG_NEXT_STATE);
////
////                            }
//
//                            else if (options[item].equals("Отмена")) {
//
//                                dialog.dismiss();
//                                idButtonForAlertDialog=4;
//                                showDialog(DIALOG_NEXT_STATE);
//
//                            }
//
//                        }
//
//                    });
//
//                    builder.show();

            }


        }
    };


    public void createSetImageDialog(){
        //TODO switch on delete function
        final CharSequence[] options = { "Сделать фото","Удалить", "Отмена" };
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Загрузить фото!");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Сделать фото"))

                {
                    SharedPreferences mPrefs = getSharedPreferences("", MODE_PRIVATE);
//                    if(mPrefs.contains("flagForPermission")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    List<File> files = integerListMapOfImages.get(tabHost.getCurrentTab());

                    files.add(idOfCurrentImage, new File(dir, "temp"+drives.get(driveId).getCurrentState().getId().toString() + idOfCurrentImage + "_" + drives.get(driveId).getId() + ".jpg"));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(files.get(idOfCurrentImage)));

                    startActivityForResult(intent, idOfCurrentImage);
//                    }

                }

                else if (options[item].equals("Удалить")) {
                    List<File> files = integerListMapOfImages.get(tabHost.getCurrentTab());


                    ImageView tableRowImage = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image);
                    ImageView tableRowImage2 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image2);
                    ImageView tableRowImage3 = (ImageView) tabHost.getCurrentView().findViewById(R.id.tableRow_image3);
                    if (idOfCurrentImage == 0) {
                        if(files.size()>0){
                            if (files.contains(files.get(idOfCurrentImage))) {
                                integerListMapOfImages.get(tabHost.getCurrentTab()).remove(idOfCurrentImage);
                            }
                        }
                        tableRowImage.setImageResource(R.drawable.camera_off);
                        tableRowImage2.setEnabled(false);
                        tableRowImage3.setEnabled(false);
                    }
                    if (idOfCurrentImage == 1) {
                        if(files.size()>1){
                            if (files.contains(files.get(idOfCurrentImage))) {
                                integerListMapOfImages.get(tabHost.getCurrentTab()).remove(idOfCurrentImage);
                            }
                        }
                        tableRowImage2.setImageResource(R.drawable.camera_off);
                        tableRowImage2.setEnabled(false);

                    }
                    if (idOfCurrentImage == 2) {
                        if(files.size()>2){
                            if (files.contains(files.get(idOfCurrentImage))) {
                                integerListMapOfImages.get(tabHost.getCurrentTab()).remove(idOfCurrentImage);
                            }
                        }
                        tableRowImage3.setImageResource(R.drawable.camera_off);
                        tableRowImage3.setEnabled(false);

                    }

                    dialog.dismiss();
                }

                else if (options[item].equals("Отмена")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            // заголовок
            adb.setTitle(R.string.header);
            // сообщение
            adb.setMessage(R.string.save_data);
            // иконка
            adb.setIcon(android.R.drawable.ic_dialog_info);
            // кнопка положительного ответа
            adb.setPositiveButton(R.string.no, myClickListener);
            // кнопка отрицательного ответа
            adb.setNegativeButton(R.string.yes, myClickListener);
            // создаем диалог
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

//    public void onClickFabMenu(View v) {
//        showDialog(DIALOG_EXIT);
//        // по id определеяем кнопку, вызвавшую этот обработчик
//        switch (v.getId()) {
//            case R.id.material_design_floating_action_menu_item2:
//                // кнопка спать
//                idButtonForAlertDialog=1;
//                break;
//
//            case R.id.material_design_floating_action_menu_item3:
//                // кнопка cancel
//                idButtonForAlertDialog=2;
//                break;
//
//            case R.id.material_design_floating_action_menu_item1:
//                // кнопка cancel
//                idButtonForAlertDialog=3;
//                break;
//            case R.id.material_design_floating_action_menu_breaking:
//                // кнопка cancel
//                idButtonForAlertDialog=7;
//                break;
//            case R.id.material_design_floating_action_menu_toilet:
//                // кнопка cancel
//                idButtonForAlertDialog=8;
//                break;
//            case R.id.material_design_floating_action_menu_invalid_order:
//                // кнопка cancel
//                idButtonForAlertDialog=9;
//                break;
//        }
//    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == idOfCurrentImage) {

//                File f = new File(Environment.getExternalStorageDirectory().toString());
//
//                for (File temp : f.listFiles()) {
//
//                    if (temp.getName().equals("temp_"+drives.get(driveId).getId()+".jpg")) {
//
//                       photo = new File(Environment.getExternalStorageDirectory(), "temp_"+drives.get(driveId).getId()+".jpg");
//                AsynchSendingStatePhoto asynchSendingStatePhoto = new AsynchSendingStatePhoto(photo);
//                asynchSendingStatePhoto.delegate = this;
//                asynchSendingStatePhoto.execute();
//                        break;
                putTablePhotoToDialogIfExists();
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                byte[] bitmapdata = bos.toByteArray();
//                try {
//                    FileOutputStream fos = new FileOutputStream(photo);
//                    fos.write(bitmapdata);
//                    fos.flush();
//                    fos.close();
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
//            else if (requestCode == 2) {
//
//
//                Uri selectedImage = data.getData();
//
//                String[] filePath = {MediaStore.Images.Media.DATA};
//
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//
//                c.moveToFirst();
//
//                int columnIndex = c.getColumnIndex(filePath[0]);
//
//                String picturePath = c.getString(columnIndex);
//
//                c.close();
//
//                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//
//
//                Log.d("", picturePath);
//
//                imageView.setImageBitmap(thumbnail);
//
//            }

        }
    }



//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        final Dialog dialog = new Dialog(this,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialogmap);
//        ImageButton btnCloseMapDialog = (ImageButton) dialog.findViewById(R.id.btn_tutup);
//        btnCloseMapDialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//
//
//        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
//        MapsInitializer.initialize(this);
//
//        mMapView = (MapView) dialog.findViewById(R.id.mapView);
//        mMapView.onCreate(dialog.onSaveInstanceState());
//        mMapView.onResume();// needed to get the map to display immediately
//        googleMap = mMapView.getMap();
//        LatLng sydney = new LatLng(langtitude, longtitude);
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
//    }
}

