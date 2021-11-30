package com.example.atlanticgrains;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Notification.CATEGORY_REMINDER;
import static android.app.Notification.EXTRA_NOTIFICATION_ID;
import static java.lang.Double.parseDouble;

public class MainActivity extends AppCompatActivity {
    Button btnSignIn;
    api_class apic;
    urlList_class urlc = new urlList_class();
    utility_class utilityc = new utility_class();
    TextInputLayout txtUsername,txtPassword;

    DatabaseHelper myDb = new DatabaseHelper(this);
    DatabaseHelper2 myDb2;
    DatabaseHelper3 myDb3;
    DatabaseHelper4 myDb4;
    DatabaseHelper5 myDb5;
    DatabaseHelper8 myDb8;
    DatabaseHelper9 myDb9;
    String gText = "";
    long mLastClickTime = 0;
    long queueid;
    DownloadManager manager;

    int gI = 0;

//    CountDownTimer countDownTimer = null;

    private OkHttpClient client;
    int userid,isManager = 0,isSales = 0, isProduction = 0,isAdmin = 0;
    boolean isManagerB = false,isSalesB=false,isProductionB = false,isAdminB = false;
    String fullName="",whse = "",resultToken = "",branch = "",plant = "",assignDept = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        btnSignIn = findViewById(R.id.btnSignIn);
        txtUsername = findViewById(R.id.username);
        txtPassword = findViewById(R.id.password);

        myDb2 = new DatabaseHelper2(this);
        myDb3 = new DatabaseHelper3(this);
        myDb4 = new DatabaseHelper4(this);
        myDb5 = new DatabaseHelper5(this);
        myDb8 = new DatabaseHelper8(this);
        myDb9 = new DatabaseHelper9(this);
        client = new OkHttpClient();

        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");
        TextView txtURL = findViewById(R.id.txtURL);
        txtURL.setText("You are Connected to: \n" + IPAddress);
        txtURL.setPaintFlags(txtURL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Change URL");

                LinearLayout layout = new LinearLayout(getBaseContext());
                layout.setPadding(40, 40, 40, 40);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView lblIPAddress = new TextView(getBaseContext());
                lblIPAddress.setText("*Enter New URL:");
                lblIPAddress.setTextColor(Color.rgb(0, 0, 0));
                lblIPAddress.setTextSize(15);
                lblIPAddress.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblIPAddress);

                EditText txtIPAddress = new EditText(MainActivity.this);
                txtIPAddress.setTextSize(15);
                txtIPAddress.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                txtIPAddress.setText("https://abc-api.jpoonandsons.com");
                layout.addView(txtIPAddress);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setView(layout);
                builder.setPositiveButton("Submit", null);
                AlertDialog alertDialog = builder.show();
                Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!txtIPAddress.getText().toString().trim().isEmpty()) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Are you sure want to submit?\n" + txtIPAddress.getText().toString())
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("IPAddress", txtIPAddress.getText().toString().trim()).apply();
                                            dialog.dismiss();
                                            alertDialog.dismiss();
                                            Toast.makeText(getBaseContext(), "Successfully changed!", Toast.LENGTH_SHORT).show();
                                            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                            String IPAddress = sharedPreferences2.getString("IPAddress", "");
                                            txtURL.setText("You are Connected to: \n" + IPAddress);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            builder.show();
                        } else {
                            Toast.makeText(getBaseContext(), "URL field is required!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "2";
//            String description = "this is a description";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("2", name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    BackTask backTask = new BackTask();
                    backTask.execute("https://raw.githubusercontent.com/laikamanor/files/master/agi_file.txt");

                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                                DownloadManager.Query req_query = new DownloadManager.Query();
                                req_query.setFilterById(queueid);
                                Cursor c = manager.query(req_query);
                                if(c.moveToFirst()){
                                    int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                    if(DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)){
                                        openDownloads(MainActivity.this);
                                    }
                                }
                            }
                        }
                    };
                    registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }else{
                    isNoInternet();
                }
            }
        });

//        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
    }

//    public void startTimer() {
//        countDownTimer = new CountDownTimer(5000, 5000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//            }
//
//            @Override
//            public void onFinish() {
//                new Thread(new Runnable() {
//                    public void run() {
//                        Intent likeIntent = new Intent(MainActivity.this, TemperingDue.class);
//                        likeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        PendingIntent likePIntent = PendingIntent.getActivity(MainActivity.this, 0, likeIntent, PendingIntent.FLAG_ONE_SHOT);
//
//                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "2")
//                                .setSmallIcon(R.drawable.ic_android)
//                                .setContentTitle("title")
//                                .setContentText("teeext")
//                                .setAutoCancel(true)
//                                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
//                                .setCategory(CATEGORY_REMINDER)
//                                .addAction(R.drawable.ic_android, "View", likePIntent)
//                                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
//                        notificationManager.notify(1, builder.build());
//                        countDownTimer.start();
//                    }
//                }).start();
//            }
//        };
//        countDownTimer.start();
//    }


    public String getFromSharedPref(String name, String key){
        SharedPreferences sharedPreferences2 = getSharedPreferences(name, MODE_PRIVATE);
        return sharedPreferences2.getString(key, "");
    }

    private class showAssignedDepartment extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
        String token = getFromSharedPref("TOKEN", "token");
        JSONArray jaAssignDept = new JSONArray();
        int selectedID = 0;
        public showAssignedDepartment(JSONArray jaAD, int id){
            jaAssignDept = jaAD;
            selectedID = id;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            return jaAssignDept.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dept = !jsonObject.has("department") ? "NO_DEPARTMENT_FOUND" : jsonObject.isNull("department") ? "NO_DEPARTMENT_FOUND" : jsonObject.getString("department");
                    arrayList.add(dept);
                }
                if(jsonArray.length() > 0) {
                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Select Assigned Department");
                    dialog.setCancelable(false);

                    LinearLayout layout = new LinearLayout(getBaseContext());
                    layout.setPadding(20, 20, 20, 10);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    ListView listView = new ListView(getBaseContext());
                    layout.addView(listView);

                    dialog.setView(layout);
                    androidx.appcompat.app.AlertDialog alertDialog2 = dialog.show();
                    ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            listView.setEnabled(false);
                            view.setEnabled(false);
                            parent.setEnabled(false);
                            String selectedDept = "";
                            for (String list : arrayList) {
                                if (arrayList.indexOf(list) == position) {
                                    selectedDept = list;
                                        break;
                                }
                            }
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Confirmation");
                            dialog.setMessage("Are you sure you want to select " + selectedDept + "?");
                            dialog.setCancelable(false);
                            String finalSelectedDept = selectedDept;
                            dialog.setPositiveButton("Yes", null);
                            dialog.setNegativeButton("Cancel", null);
                            androidx.appcompat.app.AlertDialog alertDialog = dialog.show();
                            Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button btn2 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!finalSelectedDept.trim().isEmpty()) {
                                        alertDialog2.dismiss();
                                        alertDialog.dismiss();
                                        loadingDialog.dismissDialog();
                                        updateBranch updateBranch = new updateBranch(finalSelectedDept, selectedID);
                                        updateBranch.execute("");
                                    } else {
                                        listView.setEnabled(true);
                                        view.setEnabled(true);
                                        parent.setEnabled(true);
                                        Toast.makeText(getBaseContext(), "No selected Department!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                            btn2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    listView.setEnabled(true);
                                    view.setEnabled(true);
                                    parent.setEnabled(true);
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    });
                }else{
                    showShift showShift = new showShift();
                    showShift.execute("");
                }


//                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        }
    }

    private class updateBranch extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        String gSelectedDepartment = "";
        int selectedID = 0;

        public updateBranch(String selectedDept, int id){
            gSelectedDepartment= selectedDept;
            selectedID = id;
        }

        protected void onPreExecute(String selectedDepartment, int id) {
            gSelectedDepartment = selectedDepartment;
            selectedID = id;
            loadingDialog.startLoadingDialog();
        }
        @Override
        protected String doInBackground(String... strings) {
            String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
            String token = getFromSharedPref("TOKEN", "token");
            try {
                JSONObject joBody = new JSONObject();
                joBody.put("department", gSelectedDepartment);
                String sURL = IPAddress +  "/api/auth/curr_user/update/department";
                System.out.println("SURL " + sURL);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, joBody.toString());
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(sURL)
                        .method("PUT", body)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String s = response.body().string();
                return s;
            } catch (Exception ex) {
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if(!s.isEmpty()){
                    if(s.startsWith("{")){
                        JSONObject joResult = new JSONObject(s);
                        JSONObject joData = !joResult.has("data") ? new JSONObject() : joResult.isNull("data") ? new JSONObject(): joResult.getJSONObject("data");
                        boolean isSuccess = joResult.has("success") && joResult.getBoolean("success");
                        if(isSuccess){
                            branch = !joData.has("branch") ? "" : joData.isNull("branch") ? "" : joData.getString("branch");
                            showShift showShift = new showShift();
                            showShift.execute("");
                        }else{
                            String msg = joResult.has("message") ? !joResult.isNull("message") ? joResult.getString("message") : "" : "";
                            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();;
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class showShift extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
        String token = getFromSharedPref("TOKEN", "token");
        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                String sURL = IPAddress + "/api/production/shift/get_all";
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(sURL)
                        .method("GET", null)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();
                String s = response.body().string();
                return s;
            }catch (Exception ex){
                return ex.toString();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null) {
                    if (s.startsWith("{")) {
                        loadingDialog.dismissDialog();
                        JSONObject joResult = new JSONObject(s);
                        JSONArray jaData = joResult.has("data") ? joResult.isNull("data") ? new JSONArray() : joResult.getJSONArray("data") : new JSONArray();
                        ArrayList<String> arrayList = new ArrayList<>();
                        for (int i = 0; i < jaData.length(); i++) {
                            JSONObject joData = jaData.getJSONObject(i);
                            String sCode = joData.getString("code");
                            arrayList.add(sCode);
                        }
                        if(jaData.length() >0) {
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Select Shift");
                            dialog.setCancelable(false);

                            LinearLayout layout = new LinearLayout(getBaseContext());
                            layout.setPadding(20, 20, 20, 10);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            ListView listView = new ListView(getBaseContext());
                            layout.addView(listView);

                            dialog.setView(layout);
                            androidx.appcompat.app.AlertDialog alertDialog2 = dialog.show();
                            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                        return;
                                    }
                                    mLastClickTime = SystemClock.elapsedRealtime();
                                    parent.setEnabled(false);
                                    view.setEnabled(false);
                                    String selectedShift = "";
                                    for (String list : arrayList) {
                                        if (arrayList.indexOf(list) == position) {
                                            selectedShift = list;
                                        }
                                    }
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                    dialog.setTitle("Confirmation");
                                    dialog.setMessage("Are you sure you want to select " + selectedShift + "?");
                                    dialog.setCancelable(false);
                                    String finalSelectedShift = selectedShift;
                                    dialog.setPositiveButton("Yes", null);
                                    dialog.setNegativeButton("Cancel", null);
                                    androidx.appcompat.app.AlertDialog alertDialog = dialog.show();
                                    Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn2 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!finalSelectedShift.trim().isEmpty()) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("shift", finalSelectedShift).apply();
                                                saveLoggedIn();
                                                alertDialog2.dismiss();
                                                alertDialog.dismiss();
                                                myDb8.truncateTable();
                                                downloadsJSONS(token);
                                            } else {
                                                parent.setEnabled(true);
                                                view.setEnabled(true);
                                                Toast.makeText(getBaseContext(), "No selected shift!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    btn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            parent.setEnabled(true);
                                            view.setEnabled(true);
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            });

                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }else {
                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("shift", "").apply();
                            saveLoggedIn();
                            myDb8.truncateTable();
                            downloadsJSONS(token);
                        }

                    } else {
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        }
    }

    public void generateShit(){
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz"+ "0123456789";
        StringBuilder sb = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        for(int i = 0; i <= 10; i ++){
            System.out.println("HPH1004" + sb.toString());
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void openDownloads(@NonNull Activity activity) {
        if (isSamsung()) {
            Intent intent = activity.getPackageManager()
                    .getLaunchIntentForPackage("com.sec.android.app.myfiles");
            intent.setAction("samsung.myfiles.intent.action.LAUNCH_MY_FILES");
            intent.putExtra("samsung.myfiles.intent.extra.START_PATH",
                    getDownloadsFile().getPath());
            activity.startActivity(intent);
        }
        else activity.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    public static boolean isSamsung() {
        String manufacturer = Build.MANUFACTURER;
        if (manufacturer != null) return manufacturer.toLowerCase().equals("samsung");
        return false;
    }

    public static File getDownloadsFile() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    public void saveToken(String token){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",token).apply();
    }

    public  void saveLoggedIn(){
        String susername = txtUsername.getEditText().getText().toString();
        String spassword = txtPassword.getEditText().getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username",susername).apply();
        editor.putString("password",spassword).apply();
        editor.putString("plant",plant).apply();
        editor.putString("fullname",fullName).apply();
        editor.putString("branch",branch).apply();
        editor.putString("userid",Integer.toString(userid)).apply();
        editor.putString("whse",whse).apply();
        editor.putString("isManager",Integer.toString(isManager)).apply();
        editor.putString("isSales",Integer.toString(isSales)).apply();
        editor.putString("isProduction",Integer.toString(isProduction)).apply();
        editor.putString("isAdmin",Integer.toString(isAdmin)).apply();
        editor.putString("assigned_dep",assignDept).apply();
    }

    //background process to download the file from internet
    private class BackTask extends AsyncTask<String,Integer,Void> {
        LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
        String text="";
        protected void onPreExecute(){
            super.onPreExecute();
            //display progress dialog
            loadingDialog.startLoadingDialog();
        }
        protected Void doInBackground(String...params){
            URL url;
            try {
                //create url object to point to the file location on internet
                url = new URL(params[0]);
                //make a request to server
                HttpURLConnection con=(HttpURLConnection)url.openConnection();
                //get InputStream instance
                InputStream is=con.getInputStream();
                //create BufferedReader object
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                String line;
                //read content of the file line by line
                while((line=br.readLine())!=null){
                    text+=line;

                }

                br.close();

            }catch (Exception e) {
                e.printStackTrace();
                loadingDialog.dismissDialog();
            }

            return null;

        }


        //        https://github.com/laikamanor/mobile-pos-v2/releases/download/v1.17/AtlanticBakery.apk
        protected void onPostExecute(Void result){
            btnSignIn.setEnabled(false);
            btnSignIn.setText("Wait...");
            loadingDialog.dismissDialog();
            gText = text;
            double currentVersion = 0.00;
            try{
                currentVersion = Double.parseDouble(text);
//                currentVersion = 2.0;
            }catch (NumberFormatException ex){
                currentVersion = 0.00;
            }
            if(currentVersion > Double.parseDouble(BuildConfig.VERSION_NAME)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission,1000);
                    }else {
                        startDownload(text);
                    }
                }
            }else{
                LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);
                String sUsername = txtUsername.getEditText().getText().toString().trim();
                String sPassword = txtPassword.getEditText().getText().toString().trim();
//                Toast.makeText(getBaseContext(), utilityc.getIPAddress(MainActivity.this), Toast.LENGTH_SHORT).show();
                apic = new api_class(urlc.UserLogin + "?username=" + sUsername + "&password=" + sPassword ,"GET","", utilityc.getIPAddress(MainActivity.this),false, MainActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog.startLoadingDialog();
                            }
                        });

                        String response = apic.getResponse();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    System.out.println("hello");
                                    loadingDialog.dismissDialog();
                                    String msg = "";
                                    JSONObject jsonObject = new JSONObject();
                                    if(response.startsWith("{")){
                                        jsonObject = new JSONObject(response);
                                        msg = jsonObject.getString("message");

                                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
                                        builder.setTitle("Message");
                                        builder.setMessage(msg);
                                        builder.setCancelable(false);
                                        JSONObject finalJsonObject = jsonObject;
                                        String finalMsg = msg;
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    if (finalJsonObject.getBoolean("success")) {
                                                        JSONObject jsonObjectData = finalJsonObject.getJSONObject("data");
                                                        userid = jsonObjectData.getInt("id");
                                                        resultToken = finalJsonObject.getString("token");
                                                        fullName = jsonObjectData.getString("fullname");
                                                        assignDept = jsonObjectData.getJSONArray("assigned_dep").toString();
                                                        plant = jsonObjectData.has("plant") ? jsonObjectData.getString("plant") : "";
                                                        whse = jsonObjectData.has("whse") ?  jsonObjectData.getString("whse"): "";
//                                                        branch = jsonObjectData.has("branch") ? jsonObjectData.getString("branch") : "";
                                                        isManagerB = (!jsonObjectData.isNull("isManager") && jsonObjectData.getBoolean("isManager"));
                                                        isSalesB = (!jsonObjectData.isNull("isSales") && jsonObjectData.getBoolean("isSales"));
                                                        isProductionB = (!jsonObjectData.isNull("isProduction") && jsonObjectData.getBoolean("isProduction"));
                                                        isAdminB = (!jsonObjectData.isNull("isAdmin") && jsonObjectData.getBoolean("isAdmin"));
                                                        isAdmin = (isAdminB ? 1 : 0);
                                                        isManager = (isManagerB ? 1 : 0);
                                                        isSales = (isSalesB ? 1 : 0);
                                                        isProduction = (isProductionB ? 1 : 0);
                                                        saveToken(resultToken);
                                                        JSONArray jaAssignDept = !jsonObjectData.has("assigned_dep") ? new JSONArray() : jsonObjectData.isNull("assigned_dep") ? new JSONArray() : jsonObjectData.getJSONArray("assigned_dep");
//                                                    finish();

//                                                        showShift showShift = new showShift();
//                                                        showShift.execute("");
                                                        showAssignedDepartment showAssignedDepartment = new showAssignedDepartment(jaAssignDept, userid);
                                                        showAssignedDepartment.execute("");
                                                    }
                                                } catch (JSONException e) {
                                                    btnSignIn.setText("Sign In");
                                                    btnSignIn.setEnabled(true);
                                                    e.printStackTrace();
                                                }
                                                dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                        btnSignIn.setText("Sign In");
                                        btnSignIn.setEnabled(true);
                                    }else {
                                        msg = response;
                                        String finalMsg1 = msg;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                btnSignIn.setText("Sign In");
                                                btnSignIn.setEnabled(true);
                                                if (finalMsg1.contains("Failed to connect to") || finalMsg1.contains("timeout") || finalMsg1.contains("Unable to resolve host")) {
                                                    isNoInternet();
                                                }else{
                                                    Toast.makeText(getBaseContext(), finalMsg1, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }catch (Exception ex){
                                    btnSignIn.setText("Sign In");
                                    btnSignIn.setEnabled(true);
                                    ex.printStackTrace();
                                    Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000 : {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownload(gText);
                }
            }
        }
    }

    public void startDownload(String text){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage("There is a version (v" + text + ") available do you want to update now?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override

//            https://github.com/laikamanor/mobile-pos-v2/releases/download/v1.17/Atlantic.Bakery.apk
//            https://github.com/laikamanor/mobile-pos-v2/releases/download/v1.17/Atlantic.Bakery.apk
            public void onClick(DialogInterface dialog, int which) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("https://github.com/laikamanor/mobile-pos-v2/releases/download/v1.17/Atlantic.Grains.apk"));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                request.setTitle("Download Atlantic Grains");
                request.setDescription("Downloading File...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Atlantic Grains_v" + text + ".apk");
                manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                queueid = manager.enqueue(request);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnSignIn.setText("Sign In");
                btnSignIn.setEnabled(true);
            }
        });

        builder.show();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void  isNoInternet(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Validation");
        builder.setMessage("You can't connect to the server, You want to Try Again or Go to Offline Mode?");

        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                btnSignIn.performClick();
            }
        });

        builder.setNegativeButton("Go to Offline Mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userid = 0;
                resultToken = "N/A";
                fullName = "Offline Mode";
                whse = "N/A";
                assignDept = "";
                isManagerB = false;
                isManager = (isManagerB ? 1 : 0);
                isSalesB = false;
                isSales = (isSalesB ? 1 : 0);
                isProductionB = false;
                isAdminB = false;
                isProduction = (isProductionB ? 1 : 0);;
                isAdmin = (isAdminB ? 1 : 0);
                saveToken(resultToken);

                myDb.truncateTable();
                myDb2.truncateTable();
                myDb3.truncateTable();
                myDb4.truncateTable();
                myDb5.truncateTable();

                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("shift","").apply();

                saveLoggedIn();
                Intent intent = new Intent(MainActivity.this, API_Nav2.class);
                startActivity(intent);
                finish();

                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void downloadsJSONS(String token){
        JSONObject jsonObjectData2 = new JSONObject();
        JSONArray jsonArrays = new JSONArray();
//                        DATE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        try{
            JSONObject jsonObjectItems = new JSONObject();
            jsonObjectItems.put("sURL", "/api/item/getall");
            jsonObjectItems.put("method", "GET");
            jsonObjectItems.put("from_module", "Item");
            jsonObjectItems.put("date_created", currentDate);
            jsonArrays.put(jsonObjectItems);

            JSONObject jsonObjectCustomers = new JSONObject();
            jsonObjectCustomers.put("sURL", "/api/mill/get_all");
            jsonObjectCustomers.put("method", "GET");
            jsonObjectCustomers.put("from_module", "Mill");
            jsonObjectCustomers.put("date_created", currentDate);
            jsonArrays.put(jsonObjectCustomers);

            JSONObject jsonObjectSalesType= new JSONObject();
            jsonObjectSalesType.put("sURL", "/api/production/shift/get_all");
            jsonObjectSalesType.put("method", "GET");
            jsonObjectSalesType.put("from_module", "Shift");
            jsonObjectSalesType.put("date_created", currentDate);
            jsonArrays.put(jsonObjectSalesType);

            JSONObject jsonObjectWarehouse= new JSONObject();
            jsonObjectWarehouse.put("sURL", "/api/whse/get_all");
            jsonObjectWarehouse.put("method", "GET");
            jsonObjectWarehouse.put("from_module", "Warehouse");
            jsonObjectWarehouse.put("date_created", currentDate);
            jsonArrays.put(jsonObjectWarehouse);

            JSONObject jsonObjectBranch = new JSONObject();
            jsonObjectBranch.put("sURL", "/api/branch/get_all");
            jsonObjectBranch.put("method", "GET");
            jsonObjectBranch.put("from_module", "Branch");
            jsonObjectBranch.put("date_created", currentDate);
            jsonArrays.put(jsonObjectBranch);

            JSONObject jsonObjectPerWhse = new JSONObject();
            jsonObjectPerWhse.put("sURL", "/api/inv/per_whse/get_all");
            jsonObjectPerWhse.put("method", "GET");
            jsonObjectPerWhse.put("from_module", "Per Warehouse");
            jsonObjectPerWhse.put("date_created", currentDate);
            jsonArrays.put(jsonObjectPerWhse);

            JSONObject jsonObjectStock = new JSONObject();
            jsonObjectStock.put("sURL", "/api/inv/whseinv/getall");
            jsonObjectStock.put("method", "GET");
            jsonObjectStock.put("from_module", "Stock");
            jsonObjectStock.put("date_created", currentDate);
            jsonArrays.put(jsonObjectStock);

            JSONObject jsonObjectTruck = new JSONObject();
            jsonObjectTruck.put("sURL", "/api/trucks/get_all");
            jsonObjectTruck.put("method", "GET");
            jsonObjectTruck.put("from_module", "Truck");
            jsonObjectTruck.put("date_created", currentDate);
            jsonArrays.put(jsonObjectTruck);

            JSONObject jsonObjectVessel = new JSONObject();
            jsonObjectVessel.put("sURL", "/api/vessel/get_all");
            jsonObjectVessel.put("method", "GET");
            jsonObjectVessel.put("from_module", "Vessel");
            jsonObjectVessel.put("date_created", currentDate);
            jsonArrays.put(jsonObjectVessel);

            JSONObject jsonObjectDriver = new JSONObject();
            jsonObjectDriver.put("sURL", "/api/driver/get_all");
            jsonObjectDriver.put("method", "GET");
            jsonObjectDriver.put("from_module", "Driver");
            jsonObjectDriver.put("date_created", currentDate);
            jsonArrays.put(jsonObjectDriver);

            JSONObject jsonObjectMill= new JSONObject();
            jsonObjectMill.put("sURL", "/api/mill/get_all");
            jsonObjectMill.put("method", "GET");
            jsonObjectMill.put("from_module", "Mill");
            jsonObjectMill.put("date_created", currentDate);
            jsonArrays.put(jsonObjectMill);

            JSONObject jsonObjectFGItem = new JSONObject();
            jsonObjectFGItem.put("sURL", "/api/item/getall?in_item_group=FG - Soft Flour&in_item_group=FG - Hard Flour&in_item_group=By-Products&in_item_group=Specialty Flour");
            jsonObjectFGItem.put("method", "GET");
            jsonObjectFGItem.put("from_module", "FG Item");
            jsonObjectFGItem.put("date_created", currentDate);
            jsonArrays.put(jsonObjectFGItem);

            JSONObject jsonObjectItemGroup = new JSONObject();
            jsonObjectItemGroup.put("sURL", "/api/item/item_grp/getall");
            jsonObjectItemGroup.put("method", "GET");
            jsonObjectItemGroup.put("from_module", "Item Group");
            jsonObjectItemGroup.put("date_created", currentDate);
            jsonArrays.put(jsonObjectItemGroup);

            jsonObjectData2.put("data", jsonArrays);
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        MyDownloads myDownloads = new MyDownloads(jsonObjectData2, token);
        myDownloads.execute("");
    }

    private class MyDownloads extends AsyncTask<String, Void, String> {
        JSONObject gJData;
        String gToken = "";
        @Override
        protected void onPreExecute() {
            btnSignIn.setText("Downloading Resources...");
            btnSignIn.setEnabled(false);
        }

        public MyDownloads(JSONObject jsonObjectData, String token){
            gJData = jsonObjectData;
            gToken = token;
        }

        @Override
        protected String doInBackground(String... strings) {
//            int counter = 0;

//            HashMap<String, String> modules = new HashMap<String, String>();
//            modules.put("items", )


            try{
                JSONArray jsonArray = gJData.getJSONArray("data");
                String appendJsons = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                    client = new OkHttpClient();
                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPAddress = sharedPreferences2.getString("IPAddress", "");
//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                    System.out.println("BODY: " + jsonObjectData.getString("body"));

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(IPAddress + jsonObjectData.getString("sURL"))
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + gToken)
                            .build();
                    Response response = null;
                    try {
                        response = client.newCall(request).execute();
                        String s = response.body().string();
                        if(s.startsWith("{")){
                            JSONObject jsonObjectResponse = new JSONObject(s);
                            boolean apiSuccess = jsonObjectResponse.has("success") && (!jsonObjectResponse.isNull("success") && jsonObjectResponse.getBoolean("success"));
                            if(apiSuccess){
                                String re = jsonObjectResponse.toString();
                                boolean isSuccess = myDb8.insertData(jsonObjectData.getString("sURL"), jsonObjectData.getString("method"), jsonObjectData.getString("from_module"),re, jsonObjectData.getString("date_created"));
                                if(isSuccess){
                                    appendJsons += jsonObjectData.getString("from_module") + " Resources downloaded \n";
                                }else {
                                    appendJsons += jsonObjectData.getString("from_module") + " Resources failed to download \n";
                                }
                            }
                        }else{
                            System.out.println("sssss " + s);
                            appendJsons += jsonObjectData.getString("from_module") + " Resources failed to download \n";
                        }

                    } catch (Exception ex) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnSignIn.setText("Sign In");
                                btnSignIn.setEnabled(true);
                                ex.printStackTrace();
                                Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                return appendJsons;
            }catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSignIn.setText("Sign In");
                        btnSignIn.setEnabled(true);
                        ex.printStackTrace();
                        Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null) {
                    btnSignIn.setText("Sign In");
                    btnSignIn.setEnabled(true);
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    btnSignIn.setEnabled(true);
                    myDb.truncateTable();
                    myDb2.truncateTable();
                    myDb3.truncateTable();
                    myDb4.truncateTable();
                    myDb5.truncateTable();
                    myDb9.truncateTable();

                    Intent intent = new Intent(MainActivity.this, API_Nav2.class);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSignIn.setText("Sign In");
                        btnSignIn.setEnabled(true);
                        ex.printStackTrace();
                        Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}