package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.atlanticgrains.Adapter.CustomExpandableListAdapter;
import com.example.atlanticgrains.Helper.FragmentNavigationManager_APIReceived;
import com.example.atlanticgrains.Interface.NavigationManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIReceived extends AppCompatActivity {
    private RequestQueue mQueue;
    ProgressBar progressBar;
    Button btnDone;

    String title, hidden_title;

    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper myDb;
    DatabaseHelper8 myDb8;
    DatabaseHelper7 myDb7;
    DatabaseHelper9 myDb9;

    DecimalFormat df = new DecimalFormat("#,##0.000");

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();

    int gI = 0;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    //    private String[] items;

    private ExpandableListView expandableListView;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;

    boolean gIsShowAvailableSubmit = false;

    Button btnSearch;

    long mLastClickTime = 0;
    private OkHttpClient client;
    JSONObject globalJsonObject;
    Button btnRefresh;
    String appName = "";
    String gSelectedBranch = "",gBranch = "",gFromWhse = "",gTemp = "";
    AutoCompleteTextView cmbItemGroup,txtSearch;
    View listReaderView = null, listReaderViewTemp = null;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"WrongConstant", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i_received);
        mQueue = Volley.newRequestQueue(this);
        progressBar = findViewById(R.id.progWait);
        btnDone = findViewById(R.id.btnDone);
        myDb4 = new DatabaseHelper4(this);
        myDb3 = new DatabaseHelper3(this);
        myDb = new DatabaseHelper(this);
        myDb8 = new DatabaseHelper8(this);
        myDb7 = new DatabaseHelper7(this);
        myDb9 = new DatabaseHelper9(this);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnSearch = findViewById(R.id.btnSearch);
        appName = getString(R.string.app_name);
        cmbItemGroup = findViewById(R.id.cmbItemGroup);
        txtSearch = findViewById(R.id.txtSearch);

        GridLayout gridLayout = findViewById(R.id.grid);
        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3);

        client = new OkHttpClient();

        globalJsonObject = new JSONObject();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        expandableListView = (ExpandableListView)findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_APIReceived.getmInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));
        String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));

        listReaderView = getLayoutInflater().inflate(R.layout.nav_header, null,false);
        TextView txtName = listReaderView.findViewById(R.id.txtName);
        txtName.setText("Name: " +fullName + "\nDept: " + currentDepartment + "\nShift: " + currentShift +  "\nVersion: v" + BuildConfig.VERSION_NAME);
        expandableListView.addHeaderView(listReaderView);


        genData();
        addDrawersItem();
        setupDrawer();

        if(savedInstanceState == null){
            selectFirstItemDefault();
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(getBaseContext());
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        ((TextView)v.findViewById(R.id.title)).setText(title);
        ((TextView)v.findViewById(R.id.title2)).setText(currentDepartment + " - " + currentShift);
        getSupportActionBar().setCustomView(v);

//        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gI++;
                btnRefresh.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        if(gI == 1){
                            btnRefresh.setEnabled(true);
                            globalJsonObject = new JSONObject();
                            loadData();
                        }else{
                            btnRefresh.setEnabled(true);
//                            Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Refresh once only", Toast.LENGTH_SHORT).show();
                        }
                        gI = 0;
                    }
                },500);

            }
        });

        if(hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")){
            myItemGroups myItemGroups = new myItemGroups();
            myItemGroups.execute();
        }

        if(hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Item Request For Transfer")){
            try{
                showWarehouses warehouses = new showWarehouses("/api/whse/get_all","","whsename","Warehouse",false,false);
                warehouses.execute("");
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

//        if(hidden_title.equals("API Inventory Count")) {
//            try {
//                myWarehouse myWarehouse = new myWarehouse("");
//                while (gBranch.isEmpty()) {
//                    gBranch = myWarehouse.execute("").get();
//                }
//                if (gBranch.substring(0, 1).equals("{")) {
//                    showWarehouses(gBranch,"");
//                }
//
//                TextView lblInformation = findViewById(R.id.lblInformation);
//                lblInformation.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        btnRefresh.performClick();
//                    }
//                });
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                loadData();
            }
        });

        cmbItemGroup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void afterTextChanged(Editable s) {
                loadData();
            }
        });
        btnDone.setVisibility(hidden_title.equals("API Production Order List") ? View.GONE : View.VISIBLE);


        loadData();
    }

    private class showAssignedDepartment extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        String assignDep;
        JSONArray jaAssignDept;

        private showAssignedDepartment() throws JSONException {
            assignDep= getFromSharedPref("LOGIN", "assigned_dep");
            if(!assignDep.trim().isEmpty()){
                if(assignDep.startsWith("[")){
                    jaAssignDept = new JSONArray(assignDep);
                }
            }
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
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(APIReceived.this);
                dialog.setTitle("Select Department");
                dialog.setCancelable(false);

                LinearLayout layout = new LinearLayout(getBaseContext());
                layout.setPadding(20, 20, 20, 10);
                layout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout linearLayout1 = new LinearLayout(getBaseContext());
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.setMargins(0,0,0,10);
                linearLayout1.setLayoutParams(layoutParams1);
                linearLayout1.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50,50);
                layoutParams.setMargins(0,0,10,0);
                LinearLayout linearLayout = new LinearLayout(getBaseContext());
                linearLayout.setBackgroundColor(Color.YELLOW);
                linearLayout.setLayoutParams(layoutParams);
                linearLayout1.addView(linearLayout);

                TextView t1 = new TextView(getBaseContext());
                t1.setText("Current Department");
                t1.setTextSize(15);
                linearLayout1.addView(t1);

                layout.addView(linearLayout1);

                ListView listView = new ListView(getBaseContext());
                layout.addView(listView);
                JSONArray jsonArray = new JSONArray(s);
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String dept = !jsonObject.has("department") ? "NO_DEPARTMENT_FOUND" : jsonObject.isNull("department") ? "NO_DEPARTMENT_FOUND" : jsonObject.getString("department");
                    arrayList.add(dept);
                }
                dialog.setView(layout);

                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        loadingDialog.dismissDialog();
                        dialogInterface.dismiss();
                    }
                });

                androidx.appcompat.app.AlertDialog alertDialog2 = dialog.show();
                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                ArrayAdapter adapter = new ArrayAdapter(APIReceived.this,android.R.layout.simple_list_item_1, arrayList){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        String a111 = text.getText().toString();
                        if (a111.equals(currentDepartment)) {
                            text.setBackgroundColor(Color.YELLOW);
                        } else {
                            text.setBackgroundColor(Color.WHITE);
                        }
                        return view;
                    }
                };
                listView.setAdapter(adapter);
                try{
                }catch (Exception ex){
                    ex.printStackTrace();
                }


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String selectedDept = "";
                        for (String list : arrayList){
                            if(arrayList.indexOf(list) == position) {
                                selectedDept = list;
//                                        break;
                            }
                        }
                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(APIReceived.this);
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
                                    updateBranch updateBranch = new updateBranch(finalSelectedDept);
                                    updateBranch.execute("");
                                }
                                else {
                                    Toast.makeText(getBaseContext(), "No selected Department!", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                            }
                        });
                        btn2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
            } catch (Exception ex) {
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismissDialog();
            }
        }
    }

    private class updateBranch extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        String gSelectedDepartment = "";

        public updateBranch(String selectedDept){
            gSelectedDepartment= selectedDept;
        }

        protected void onPreExecute(String selectedDepartment) {
            gSelectedDepartment = selectedDepartment;
            loadingDialog.startLoadingDialog();
        }
        @Override
        protected String doInBackground(String... strings) {
            String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
            String token = getFromSharedPref("TOKEN", "token");
            try {
                JSONObject joBody = new JSONObject();
                joBody.put("department", gSelectedDepartment);
                String sURL = IPAddress + "/api/auth/curr_user/update/department";
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String s) {
            try{
                if(!s.isEmpty()){
                    if(s.startsWith("{")){
                        JSONObject joResult = new JSONObject(s);
                        JSONObject joData = !joResult.has("data") ? new JSONObject() : joResult.isNull("data") ? new JSONObject(): joResult.getJSONObject("data");
                        boolean isSuccess = joResult.has("success") && joResult.getBoolean("success");
                        String msg = joResult.has("message") ? !joResult.isNull("message") ? joResult.getString("message") : "" : "";
                        String branch = !joData.has("branch") ? "" : joData.isNull("branch") ? "" : joData.getString("branch");
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("branch",branch).apply();

                        expandableListView.removeHeaderView(listReaderView);
                        expandableListView.removeHeaderView(listReaderViewTemp);
                        listReaderViewTemp   = getLayoutInflater().inflate(R.layout.nav_header, null,false);
                        expandableListView.removeHeaderView(listReaderView);
                        TextView txtName = listReaderViewTemp.findViewById(R.id.txtName);
                        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));
                        String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                        txtName.setText("Name: " +fullName + "\nDept: " + branch + "\nShift: " + currentShift + "\nVersion: v" + BuildConfig.VERSION_NAME);
                        expandableListView.addHeaderView(listReaderViewTemp);

                        LayoutInflater inflator = LayoutInflater.from(getBaseContext());
                        View v = inflator.inflate(R.layout.custom_action_bar, null);
                        ((TextView)v.findViewById(R.id.title)).setText(title);
                        ((TextView)v.findViewById(R.id.title2)).setText( branch + " - " + currentShift);
                        getSupportActionBar().setCustomView(v);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                        builder.setCancelable(false);
                        builder.setTitle("Message");
                        builder.setMessage(msg + (isSuccess ? "\n\nPlease press refresh button to sync " + branch + " data" : ""));
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
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
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
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
                        if(jaData.length() >0){
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(APIReceived.this);
                            dialog.setTitle("Select Shift");
                            dialog.setCancelable(false);

                            LinearLayout layout = new LinearLayout(getBaseContext());
                            layout.setPadding(20, 20, 20, 10);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            ListView listView = new ListView(getBaseContext());
                            layout.addView(listView);

                            dialog.setView(layout);

                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            androidx.appcompat.app.AlertDialog alertDialog2 = dialog.show();
                            ArrayAdapter adapter = new ArrayAdapter(APIReceived.this,android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String selectedShift = "";
                                    for (String list : arrayList){
                                        if(arrayList.indexOf(list) == position) {
                                            selectedShift = list;
//                                        break;
                                        }
                                    }
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(APIReceived.this);
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
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onClick(View v) {
                                            if (!finalSelectedShift.trim().isEmpty()) {
                                                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                                                String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("shift", finalSelectedShift).apply();
                                                String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));

                                                expandableListView.removeHeaderView(listReaderView);
                                                expandableListView.removeHeaderView(listReaderViewTemp);
                                                listReaderViewTemp   = getLayoutInflater().inflate(R.layout.nav_header, null,false);
                                                expandableListView.removeHeaderView(listReaderView);

                                                TextView txtName = listReaderViewTemp.findViewById(R.id.txtName);
                                                txtName.setText("Name: " +fullName + "\nDept: " + currentDepartment + "\nShift: " + currentShift + "\nVersion: v" + BuildConfig.VERSION_NAME);
                                                expandableListView.addHeaderView(listReaderViewTemp);

                                                LayoutInflater inflator = LayoutInflater.from(getBaseContext());
                                                View vv = inflator.inflate(R.layout.custom_action_bar, null);
                                                ((TextView)vv.findViewById(R.id.title)).setText(title);
                                                ((TextView)vv.findViewById(R.id.title2)).setText(currentDepartment + " - " + currentShift);
                                                getSupportActionBar().setCustomView(vv);

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                                builder.setCancelable(false);
                                                builder.setTitle("Message");
                                                builder.setMessage("Shift Successfully Changed!");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.show();

                                                alertDialog2.dismiss();
                                                alertDialog.dismiss();
                                            }
                                            else {
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                                builder.setCancelable(false);
                                                builder.setTitle("Message");
                                                builder.setMessage("No selected shift!");
                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                                builder.show();
                                            }
                                        }
                                    });
                                    btn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            });
                        }else {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                            builder.setCancelable(false);
                            builder.setTitle("Message");
                            builder.setMessage("No response result!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
//                            Toast.makeText(getBaseContext(), "No response result!", Toast.LENGTH_SHORT).show();
//                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sharedPreferences.edit();
//                            editor.putString("shift", "").apply();
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

    private class showWarehouses extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        String gURL = "", gParams = "", gKeyName = "", gTitle = "";
        boolean gNeedDialog = false,gIsRefresh = false;

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        public showWarehouses(String url, String paramss, String keyName, String title, boolean needDialog,boolean isRefresh) {
            gParams = paramss;
            gURL = url;
            gKeyName = keyName;
            gTitle = title;
            gNeedDialog = needDialog;
            gIsRefresh = isRefresh;
        }

        @Override
        protected String doInBackground(String... strings) {
            if (gIsRefresh) {
                try {
                    String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
                    String token = getFromSharedPref("TOKEN", "token");
                    String sURL = IPAddress + gURL + gParams;
                    System.out.println("hey " + sURL);

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(sURL)
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    return s;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Cursor cursor = myDb8.getAllData();
                    while (cursor.moveToNext()) {
                        String module = cursor.getString(4);
                        if (module.trim().toLowerCase().equals(gTitle.toLowerCase().trim())) {
                            ex.printStackTrace();
                            return cursor.getString(3);
                        }
                    }
                    return ex.toString();
                }
            } else {
                System.out.println("yed dito " + gTitle);
                String result = "{}";
                Cursor cursor = myDb8.getAllData();
                while (cursor.moveToNext()) {
                    String module = cursor.getString(4);
                    if (module.trim().toLowerCase().equals(gTitle.toLowerCase().trim())) {
                        System.out.println("asintahin " + module + "/" + gTitle + "/" + cursor.getString(1));
                        result = cursor.getString(3);
                    }
                }
                System.out.println("ano dapat? " + result);
                return result;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if (s != null) {
                    if (s.startsWith("{")) {
                        JSONObject joResult = new JSONObject(s);
                        if (hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Item Request For Transfer")) {
                            gBranch = s;
                        }
                        if (gNeedDialog) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(APIReceived.this);
                            alertDialog.setTitle("Select " + gTitle);
                            LinearLayout layout = new LinearLayout(getBaseContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//                    layout.setLayoutParams(layoutParams);
                            layout.setPadding(20, 20, 20, 10);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            ListView listView = new ListView(getBaseContext());
                            listView.setLayoutParams(layoutParams);
                            layout.addView(listView);

                            JSONArray jaData = joResult.has("data") ? joResult.isNull("data") ? new JSONArray() : joResult.getJSONArray("data") : new JSONArray();
                            ArrayList<String> arrayList = new ArrayList<>();
                            if (jaData.length() > 0) {
                                for (int i = 0; i < jaData.length(); i++) {
                                    JSONObject joData = jaData.getJSONObject(i);
                                    String keyValue = joData.getString(gKeyName);
                                    arrayList.add(keyValue);
                                }
                            }

                            ArrayAdapter adapter = new ArrayAdapter(APIReceived.this, android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(adapter);
                            alertDialog.setView(layout);

                            alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                System.out.println("error found in whse");
                ex.printStackTrace();
            }
            loadingDialog.dismissDialog();
        }
    }

    public String getFromSharedPref(String name, String key){
        SharedPreferences sharedPreferences2 = getSharedPreferences(name, MODE_PRIVATE);
        return sharedPreferences2.getString(key, "");
    }

    @SuppressLint("StaticFieldLeak")
    private class myWarehouse extends AsyncTask<String, Void, String> {
        String sParams = "",gItemCode = "",gItemName = "",gFromModule = "",gIfKey ="",gIfValue = "";
        TextView gLbl;
        boolean gIsRefresh = false;
        int gID = 0;
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        public myWarehouse(String params, TextView lbl,String itemName, String itemCode, String fromModule, boolean isRefresh, String ifKey, String ifValue){
            sParams = params;
            gLbl = lbl;
            gItemCode = itemCode;
            gItemName = itemName;
            gFromModule = fromModule;
            gIsRefresh = isRefresh;
            gIfKey = ifKey;
            gIfValue = ifValue;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {
//            Cursor cursor = myDb8.getAllData();
//            String cURL = "", cMethod = "",cResponse = "";
//            while (cursor.moveToNext()) {
//                String module = cursor.getString(4);
//                System.out.println("moduleeeed " + module + "/" + gFromModule + "/" + gIsRefresh);
//                if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase()) && !gIsRefresh) {
//                    System.out.println("first last");
//                    cResponse = cursor.getString(3);
//                    return cursor.getString(3);
//                } else if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase()) && gIsRefresh) {
//                    cURL = cursor.getString(1);
//                    cMethod = cursor.getString(2);
//                    cResponse = cursor.getString(3);
//                    gID = cursor.getInt(0);
//                }
//            }
//            if (gIsRefresh && !cURL.trim().isEmpty() && !cMethod.trim().isEmpty()) {
//                try {
//                    utility_class utilityc = new utility_class();
//                    SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
//                    String bearerToken = Objects.requireNonNull(sharedPreferences2.getString("token", ""));
//
//                    OkHttpClient client;
//                    client = new OkHttpClient();
//                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                    okhttp3.Request request = null;
//                    request = new okhttp3.Request.Builder()
//                            .url(utilityc.getIPAddress(APIReceived.this) + cURL)
//                            .method(cMethod, null)
//                            .addHeader("Authorization", "Bearer " + bearerToken)
//                            .build();
//                    Response response;
//                    response = client.newCall(request).execute();
//                    System.out.println("second last");
//                    return response.body().string();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    APIReceived.this.runOnUiThread(() -> {
//                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//                    });
//
//                    while (cursor.moveToNext()) {
//                        String module = cursor.getString(4);
//                        if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase())) {
//                            ex.printStackTrace();
//                            System.out.println("third last");
//                            return cursor.getString(3);
//                        }
//                    }
//                    return "{}";
//                }
//            }else{
////                while (cursor.moveToNext()) {
////                    String module = cursor.getString(3);
////                    if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase())) {
////                        System.out.println("second last");
////                        return cursor.getString(4);
////                    }
////                }
//                System.out.println("te heck?" + cURL + "/" + cMethod);
//                return cResponse;
//            }
            if (gIsRefresh) {
                try {
                    String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
                    String token = getFromSharedPref("TOKEN", "token");
                    String sURL = IPAddress + "/api/whse/get_all" + sParams;
                    System.out.println("hey " + sURL);

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(sURL)
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    Response response = client.newCall(request).execute();
                    String s = response.body().string();
                    if (sParams.trim().isEmpty()) {
                        if (hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Item Request For Transfer")) {
                            gBranch = s;
                        }
                    }
                    return s;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Cursor cursor = myDb8.getAllData();
                    while (cursor.moveToNext()) {
                        String module = cursor.getString(4);
                        System.out.println("module: " + module);
                        if (module.trim().toLowerCase().equals("warehouse")) {
                            System.out.println("ano naman " + cursor.getString(3));
                            ex.printStackTrace();
                            return cursor.getString(3);
                        }
                    }
                    return ex.toString();
                }
            } else {
                String result = "";
                Cursor cursor = myDb8.getAllData();
                while (cursor.moveToNext()) {
                    String module = cursor.getString(4);
                    if (module.trim().toLowerCase().equals("warehouse")) {
                        result = cursor.getString(3);
                    }
                }
                return result;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            gTemp = s;
            if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received from Production")){
                gBranch= s;
                System.out.println("ganun pa rin natuto lang");
            }

            showFromWarehouse(gLbl,s,gItemName, gItemCode, gIfKey, gIfValue,sParams);
           loadingDialog.dismissDialog();
        }
    }


    public String findWarehouseCode(String value, String source){
        System.out.println("source: " + source);
        String result = "";
        try{
            if(source.length() > 0) {
                JSONObject jsonObjectResponse = new JSONObject(source);
                JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String branch = jsonObject.getString("whsecode") + "," + jsonObject.getString("whsename");
                    if (value.equals(jsonObject.getString("whsename"))) {
                        result = jsonObject.getString("whsecode");
                    }
                }
            }
        }catch (Exception ex){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ex.printStackTrace();
                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        return result;
    }

    public List<String> returnBranches(String value,String ifKey, String ifValue) {
        List<String> result = new ArrayList<>();
        result.add("Select Warehouse");
//        System.out.println(gBranch);
        try {
            System.out.println("ifKey: " + ifKey + "\nifValue: " + ifValue);
            JSONObject jsonObjectResponse = new JSONObject(value);
            JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String branch = jsonObject.getString("whsename");
                if (!ifValue.trim().isEmpty()) {
                    if (jsonObject.getString(ifKey).toLowerCase().trim().equals(ifValue.toLowerCase().trim())) {
                        result.add(branch);
                    }
                } else {
                    result.add(branch);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),  ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public void showWarehouses(String value,String itemCode){
        AlertDialog _dialog = null;
        AlertDialog.Builder dialogSelectWarehouse = new AlertDialog.Builder(APIReceived.this);
        dialogSelectWarehouse.setTitle("Select Warehouse");
        dialogSelectWarehouse.setCancelable(false);
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);

        AutoCompleteTextView txtSearchBranch = new AutoCompleteTextView(getBaseContext());
        txtSearchBranch.setTextSize(13);
        layout.addView(txtSearchBranch);

        final List<String>[] warehouses = new List[]{returnBranches(value,"","")};
        final ArrayList<String>[] myReference = new ArrayList[]{getReference(warehouses[0], txtSearchBranch.getText().toString().trim())};
        final ArrayList<String>[] myID = new ArrayList[]{getID(warehouses[0], txtSearchBranch.getText().toString().trim())};
        final List<String>[] listItems = new List[]{getListItems(warehouses[0])};

        TextView btnSearchBranch = new TextView(getBaseContext());
        btnSearchBranch.setBackgroundColor(Color.parseColor("#0b8a0f"));
        btnSearchBranch.setPadding(20,20,20,20);
        btnSearchBranch.setTextColor(Color.WHITE);
        btnSearchBranch.setTextSize(13);
        btnSearchBranch.setText("Search");
        ListView listView = new ListView(getBaseContext());
        btnSearchBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myReference[0] = getReference(warehouses[0], txtSearchBranch.getText().toString().trim());
                myID[0] = getID(warehouses[0], txtSearchBranch.getText().toString().trim());
                listItems[0] = getListItems(warehouses[0]);

                APIReceived.MyAdapter adapter = new APIReceived.MyAdapter(APIReceived.this, myReference[0], myID[0],itemCode);

                listView.setAdapter(adapter);
            }
        });

        layout.addView(btnSearchBranch);

        LinearLayout.LayoutParams layoutParamsWarehouses = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,300);
        layoutParamsWarehouses.setMargins(10,10,10,10);
        listView.setLayoutParams(layoutParamsWarehouses);

        txtSearchBranch.setAdapter(fillItems(listItems[0]));
        APIReceived.MyAdapter adapter = new APIReceived.MyAdapter(APIReceived.this, myReference[0], myID[0],itemCode);
        dialogSelectWarehouse.setView(layout);

        dialogSelectWarehouse.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        _dialog = dialogSelectWarehouse.show();
        listView.setAdapter(adapter);

        AlertDialog final_dialog = _dialog;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = view.findViewById(R.id.txtIDs);
                        TextView textView1 = view.findViewById(R.id.txtReference);
                        gSelectedBranch = textView1.getText().toString();
                        TextView lblInfo = findViewById(R.id.lblInformation);
                        lblInfo.setText("Branch: " +  gSelectedBranch);
                        globalJsonObject = new JSONObject();
                        loadData();
                        final_dialog.dismiss();
                    }
                });
            }
        });
        layout.addView(listView);
    }

    public void showFromWarehouse(TextView lblFrom, String value,String itemName, String itemCode, String ifKey, String ifValue, String sParams) {
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        try {

            loadingDialog.startLoadingDialog();
            AlertDialog _dialog = null;
            AlertDialog.Builder dialogSelectWarehouse = new AlertDialog.Builder(APIReceived.this);
            dialogSelectWarehouse.setTitle("Select Warehouse");
            dialogSelectWarehouse.setCancelable(false);
            LinearLayout layout = new LinearLayout(getBaseContext());
            layout.setPadding(40, 40, 40, 40);
            layout.setOrientation(LinearLayout.VERTICAL);

            TextInputLayout textInputLayout = new TextInputLayout(APIReceived.this);
            textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
            textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            textInputLayout.setBoxStrokeColor(Color.parseColor("#1687a7"));
            textInputLayout.setHint("Search Warehouse");

            AutoCompleteTextView txtSearchBranch = new AutoCompleteTextView(getBaseContext());
            txtSearchBranch.setTextSize(15);
            textInputLayout.addView(txtSearchBranch);
            layout.addView(textInputLayout);

            LinearLayout.LayoutParams layoutParamsLa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout la = new LinearLayout(APIReceived.this);
            la.setWeightSum(2f);
            la.setOrientation(LinearLayout.HORIZONTAL);
            la.setLayoutParams(layoutParamsLa);

            final List<String>[] warehouses = new List[]{returnBranches(value,ifKey, ifValue)};
            final ArrayList<String>[] myReference = new ArrayList[]{getReference(warehouses[0], txtSearchBranch.getText().toString().trim())};
            final ArrayList<String>[] myID = new ArrayList[]{getID(warehouses[0], txtSearchBranch.getText().toString().trim())};
            final List<String>[] listItems = new List[]{getListItems(warehouses[0])};

            MaterialButton btnSearchBranch = new MaterialButton(APIReceived.this);
            btnSearchBranch.setCornerRadius(20);
            LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            btnSearchBranch.setLayoutParams(layoutParamsBtn);
            btnSearchBranch.setBackgroundResource(R.color.colorPrimary);
            btnSearchBranch.setTextColor(Color.WHITE);
//        btnSearchBranch.setPadding(20,20,20,20);
            btnSearchBranch.setTextSize(13);
            btnSearchBranch.setText("Search");
            ListView listView = new ListView(getBaseContext());
            btnSearchBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myReference[0] = getReference(warehouses[0], txtSearchBranch.getText().toString().trim());
                    myID[0] = getID(warehouses[0], txtSearchBranch.getText().toString().trim());
                    listItems[0] = getListItems(warehouses[0]);

                    APIReceived.MyAdapter adapter = new APIReceived.MyAdapter(APIReceived.this, myReference[0], myID[0],itemCode);

                    listView.setAdapter(adapter);
                }
            });

            AppCompatButton btnRefresh = new AppCompatButton(APIReceived.this);
//        btnRefresh.setCornerRadius(20);
            LinearLayout.LayoutParams layoutParamsRefresh = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1f);
            layoutParamsRefresh.setMargins(10,0,0,0);
            btnRefresh.setLayoutParams(layoutParamsRefresh);
            btnRefresh.setBackgroundColor(Color.rgb(157, 203, 242));
//        btnSearchBranch.setPadding(20,20,20,20);
            btnRefresh.setTextColor(Color.WHITE);
            btnRefresh.setTextSize(13);
            btnRefresh.setText("Refresh");

//        layout.addView(btnSearchBranch);
            la.addView(btnSearchBranch);
            la.addView(btnRefresh);
            layout.addView(la);

//            layout.addView(btnSearchBranch);


            LinearLayout.LayoutParams layoutParamsWarehouses = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
            layoutParamsWarehouses.setMargins(10, 10, 10, 10);
            listView.setLayoutParams(layoutParamsWarehouses);

            txtSearchBranch.setAdapter(fillItems(listItems[0]));
            APIReceived.MyAdapter adapter = new APIReceived.MyAdapter(APIReceived.this, myReference[0], myID[0],itemCode);

//            if(!hidden_title.equals("API Received from Production")) {
//                Button btn = new Button(getBaseContext());
//                btn.setText("View Available Qty Per Whse");
//                btn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, itemName,null);
//                        if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
//                            showAvailabelQuantity.execute("");
//                        }
//                    }
//                });
//
//                btn.setBackgroundResource(R.color.colorAccent);
//                LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                lay.setMargins(0, 5, 0, 0);
//                btn.setLayoutParams(lay);
//                btn.setTextColor(Color.WHITE);
//                layout.addView(btn);
//            }

            dialogSelectWarehouse.setView(layout);

            dialogSelectWarehouse.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            _dialog = dialogSelectWarehouse.show();
            AlertDialog final_dialog2 = _dialog;

            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
            String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

            btnRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(fullName.equals("Offline Mode")){
                        Toast.makeText(getBaseContext(), "You can't refresh " + title + " because you are in offline mode!", Toast.LENGTH_SHORT).show();
                    }else{
                        final_dialog2.dismiss();
                        myWarehouse myWarehouse = new myWarehouse(sParams,lblFrom, itemName,itemCode,"Warehouse", true,ifKey,ifValue);
                        if(myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING){
                            myWarehouse.execute("");
                        }
                    }
                }
            });


            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView textView = view.findViewById(R.id.txtIDs);
                            TextView textView1 = view.findViewById(R.id.txtReference);
                            if (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")) {
                                if(myDb4.checkItemFWhse(title,itemCode, textView1.getText().toString())){
                                    Toast.makeText(getBaseContext(), textView1.getText().toString() + " is already selected", Toast.LENGTH_SHORT).show();
                                }else{
                                    lblFrom.setText(textView1.getText().toString());
                                    final_dialog2.dismiss();
                                }
                            } else {
                                lblFrom.setText(textView1.getText().toString());
                                final_dialog2.dismiss();
                            }
                        }
                    });
                }
            });
            layout.addView(listView);
            loadingDialog.dismissDialog();
        }
        catch (Exception ex) {
            loadingDialog.dismissDialog();
            ex.printStackTrace();
        }
    }

    public List<String> getListItems(List<String> warehouses){
        List<String> result = new ArrayList<String>();
        for(String temp : warehouses){
            if(!temp.contains("Select Warehouse")){
                result.add(temp);
            }
        }
        return result;
    }

    public ArrayList<String> getReference(List<String> warehouses,String value){
        ArrayList<String> result = new ArrayList<String>();
        for(String temp : warehouses){
            if(!temp.contains("Select Warehouse")){
                if (!value.isEmpty()) {
                    if (value.trim().toLowerCase().equals(temp.toLowerCase())) {
                        result.add(temp);
                    }
                }else{
                    result.add(temp);
//                    myID.add("0");
                }
            }
        }
        return result;
    }

    public ArrayList<String> getID(List<String> warehouses,String value){
        ArrayList<String> result = new ArrayList<String>();
        for(String temp : warehouses){
            if(!temp.contains("Select Warehouse")){
                if (!value.isEmpty()) {
                    if (value.trim().contains(temp)) {
                        result.add("0");
//                        myID.add("0");
                    }
                }else{
                    result.add("0");
//                    myID.add("0");
                }
            }
        }
        return result;
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context rContext;
        ArrayList<String> myReference;
        ArrayList<String> myIds;
        String gItemCode = "";

        MyAdapter(Context c, ArrayList<String> reference, ArrayList<String> id, String itemCode) {
            super(c, R.layout.custom_list_view_sales_logs, R.id.txtReference, reference);
            this.rContext = c;
            this.myReference = reference;
            this.myIds = id;
            gItemCode = itemCode;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.custom_list_view_sales_logs, parent, false);
            TextView textView1 = row.findViewById(R.id.txtReference);
            TextView textView2 = row.findViewById(R.id.txtIDs);
            TextView textView3 = row.findViewById(R.id.txtAmount);
            textView1.setText(myReference.get(position));
            textView2.setText(myIds.get(position));
            textView2.setVisibility(View.GONE);
            textView3.setVisibility(View.GONE);
            if (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")) {
//                Cursor cursor = myDb4.allWhse(title);
//                if (cursor != null) {
//                    while (cursor.moveToNext()) {
//                        if (textView1.getText().toString().toLowerCase().contains(cursor.getString(0).toLowerCase())) {
//                            textView1.setBackgroundColor(Color.parseColor("#ff764d"));
//                        }
//                    }
//                }

                if (myDb4.checkItemFWhse(title, gItemCode, myReference.get(position))) {
                    textView1.setBackgroundColor(Color.parseColor("#ff764d"));
                }
            }


            return row;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);

    }

    public void selectFirstItemDefault(){
        if(navigationManager != null){
            String firstItem = listTitle.get(0);
            navigationManager.showFragment(firstItem);
            getSupportActionBar().setTitle(firstItem);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void loadData(){
        try{
            LinearLayout layoutItemGroup = findViewById(R.id.layoutItemGroup);
        TextView lblInformation = findViewById(R.id.lblInformation);
        Button btnBack = findViewById(R.id.btnBack);
        if(hidden_title.equals("API Item Request For Transfer")){
            lblInformation.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
            layoutItemGroup.setVisibility(View.GONE);
            if(myDb3.countSelected(hidden_title) <= 0){
                getItems(0);
            }else {
                lblInformation.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);
                loadSelectedSAPNumberItems();
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb3.truncateTable();
                        lblInformation.setVisibility(View.GONE);
                        btnBack.setVisibility(View.GONE);
                        loadData();
                    }
                });
            }
        }else if(hidden_title.equals("API Inventory Count")){
            layoutItemGroup.setVisibility(View.GONE);
            LinearLayout linearLayoutItemGroup = findViewById(R.id.layoutItemGroup);
            linearLayoutItemGroup.setVisibility(View.GONE);
            lblInformation.setVisibility(View.VISIBLE);
            getItems(0);
        }
        else if (hidden_title.equals("API System Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
            if(hidden_title.equals("API System Transfer Item")){
                layoutItemGroup.setVisibility(View.GONE);
                @SuppressLint("CutPasteId") LinearLayout linearLayoutItemGroup = findViewById(R.id.layoutItemGroup);
                linearLayoutItemGroup.setVisibility(View.GONE);
            }
            lblInformation.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
//            layoutItemGroup.setVisibility(View.GONE);
            if(myDb3.countItems(hidden_title) <= 0){
                getItems(0);
            }else {
                lblInformation.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.VISIBLE);    

                loadSelectedSAPNumberItems();

                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDb3.truncateTable();
                        lblInformation.setVisibility(View.GONE);
                        btnBack.setVisibility(View.GONE);
                        loadData();
                    }
                });
            }
        }else if(hidden_title.equals("API Pending Issue For Production")){
            layoutItemGroup.setVisibility(View.GONE);
            if(myDb9.countItems(hidden_title) > 0){
                Intent intent;
                intent = new Intent(getBaseContext(), API_IssueProductionItems.class);
                intent.putExtra("title", title);
                intent.putExtra("hiddenTitle", hidden_title);
                startActivity(intent);
            }else{
                getItems(0);
            }
        }
        else{
            getItems(0);
        }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    public void loadSelectedSAPNumberItems() {
        try{
            Handler handler = new Handler();
            progressBar.setVisibility(View.VISIBLE);
            handler.postDelayed(() -> {
                GridLayout gridLayout = findViewById(R.id.grid);
                gridLayout.removeAllViews();
                Cursor cursor = myDb3.getAllData(hidden_title);
                int iterate = 1;
                List<String> listItems = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    final int id = cursor.getInt(0);
                    final String sapNumber = cursor.getString(1);
                    final String fromBranch = cursor.getString(2);
                    final String toBranch = cursor.getString(8);
                    final String itemName = cursor.getString(3);
                    System.out.println("OK  " + itemName);
                    final double quantity = cursor.getDouble(4);
                    final boolean isSelected = (cursor.getInt(6) > 0);
                    JSONObject joResult = new JSONObject();
                    JSONArray jaData = new JSONArray();
                    String remarks = "",transDate = "";
                    try {
                        joResult = new JSONObject(cursor.getString(17));
                        jaData = joResult.getJSONArray("data");
                        for(int i =0; i < jaData.length();i++){
                            JSONObject joData = jaData.getJSONObject(i);
                            remarks = joData.has("remarks") ? joData.getString("remarks") : "";
                            transDate = joData.has("transdate") ? joData.getString("transdate"): "";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TextView lblInformation = findViewById(R.id.lblInformation);
                    lblInformation.setText("Reference#: " + sapNumber + (hidden_title.equals("API Item Request For Transfer") ? "\nFrom Department: " : "\nFrom Warehouse: ") + fromBranch + (hidden_title.equals("API Item Request For Transfer") ? "\nRequestor Department: " + toBranch + "\nRemarks: " + remarks : hidden_title.equals("API System Transfer Item") ? "\nTo Warehouse: " + toBranch + "\nRemarks: " + remarks + "\nTransdate: " + transDate : ""));
                    listItems.add(itemName);
                    String uom = cursor.getString(11);
                    int received_quantity = cursor.getInt(12);
                    int itemID = cursor.getInt(13);
                    String itemCode = cursor.getString(16);
                    boolean isClosed = cursor.getInt(15) > 0;

                    if (!txtSearch.getText().toString().trim().isEmpty()) {
                        if (itemName.trim().toLowerCase().contains(txtSearch.getText().toString().trim().toLowerCase())) {
                            uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected,received_quantity,uom,itemID,isClosed,iterate,itemCode,"",0);
                            iterate+= 1;
                        }
                    }else{
                        uiItems2(id,itemName,sapNumber,quantity,fromBranch,isSelected,received_quantity,uom,itemID,isClosed,iterate,itemCode,"",0);
                        iterate+= 1;
                    }
                }
                txtSearch.setAdapter(fillItems(listItems));
                progressBar.setVisibility(View.GONE);
            },500);
            btnDone.setOnClickListener(view -> navigateDone());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    public void addDrawersItem(){
        ExpandableListAdapter adapter = new CustomExpandableListAdapter(this, listTitle, listChild);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List)listChild.get(listTitle.get(groupPosition)))
                        .get(childPosition).toString();
//                getSupportActionBar().setTitle(selectedItem);
                Intent intent;
                if(selectedItem.equals("Received from SAP")){
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from SAP");
                    intent.putExtra("hiddenTitle", "API Received from SAP");
                    startActivity(intent);
                }
                else if(selectedItem.equals("System Receive Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "System Receive Item");
                    intent.putExtra("hiddenTitle", "API System Transfer Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Manual Receive Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Manual Receive Item");
                    intent.putExtra("hiddenTitle", "API Received Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("System Transfer Item")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "System Transfer Item");
                    intent.putExtra("hiddenTitle", "API Transfer Item");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Item Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Item Request");
                    intent.putExtra("hiddenTitle", "API Item Request");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Pending Item Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pending Item Request");
                    intent.putExtra("hiddenTitle", "API Item Request For Transfer");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Sales")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Sales");
                    intent.putExtra("hiddenTitle", "API Menu Items");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Issue For Production")) {
                    utility_class utilityc = new utility_class();
                    String[] prodList = utility_class.prodList;
                    if(utilityc.isAllowProdPacking(prodList, APIReceived.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Production");
                        intent.putExtra("hiddenTitle", "API Issue For Production");
                        startActivity(intent);
                    }else{
                        String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                        for(String list : prodList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, APIReceived.this);
                    }
                }
                else if(selectedItem.equals("Issue For Packing")) {
                    utility_class utilityc = new utility_class();
                    String[] packingList = utility_class.packingList;
                    if(utilityc.isAllowProdPacking(packingList, APIReceived.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Packing");
                        intent.putExtra("hiddenTitle", "API Issue For Packing");
                        startActivity(intent);
                        finish();
                    }else{
                        String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                        for(String list : packingList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, APIReceived.this);
                    }
                }
                else if(selectedItem.equals("Pending Issue For Production/Packing")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pending Issue For Production/Packing");
                    intent.putExtra("hiddenTitle", "API Pending Issue For Production");
                    startActivity(intent);
                    finish();
                }if(selectedItem.equals("Received from Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from Production");
                    intent.putExtra("hiddenTitle", "API Received from Production");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Production Order List")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Production Order List");
                    intent.putExtra("hiddenTitle", "API Production Order List");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Inventory Count")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Inventory Count");
                    intent.putExtra("hiddenTitle", "API Inventory Count");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Pull out Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pull Out Request");
                    intent.putExtra("hiddenTitle", "API Pull Out Count");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Logout")){
                    onBtnLogout();
                }
                else if(selectedItem.equals("Logs")){
                    intent = new Intent(getBaseContext(), API_SalesLogs.class);
                    intent.putExtra("title", "Inventory Logs");
                    intent.putExtra("hiddenTitle", "API Inventory Logs");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Cut Off")){
                    intent = new Intent(getBaseContext(), CutOff.class);
                    intent.putExtra("title", "Cut Off");
                    intent.putExtra("hiddenTitle", "API Cut Off");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Inventory Confirmation")){
                    intent = new Intent(getBaseContext(), API_InventoryConfirmation.class);
                    intent.putExtra("title", "Inv. and P.O Count Confirmation");
                    intent.putExtra("hiddenTitle", "API Inventory Count Confirmation");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Change Password")){
                    changePassword();
                }
                else if(selectedItem.equals("Offline Pending Transactions")){
                    intent = new Intent(getBaseContext(), OfflineList.class);
                    intent.putExtra("title", "Offline Pending Transactions");
                    intent.putExtra("hiddenTitle", "API Offline List");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Change Department")){
                    showAssignedDepartment showAssignedDepartment = null;
                    try {
                        showAssignedDepartment = new showAssignedDepartment();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    showAssignedDepartment.execute("");
                }
                else if(selectedItem.equals("Change Shift")){
                    showShift showShift = null;
                    showShift = new showShift();
                    showShift.execute("");
                }
                return true;
            }
        });
    }

    public void setupDrawer(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.string.open,R.string.close){
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void genData(){
        List<String>title = navc.getTitles(getString(R.string.app_name));
        listChild = new TreeMap<>();
        int iterate = 5;
        int titleIndex = 0;
        while (iterate >= 0){
            listChild.put(title.get(titleIndex),navc.getItem(title.get(titleIndex)));
            titleIndex += 1;
            iterate -= 1;
        }
        listTitle = new ArrayList<>(listChild.keySet());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu item) {
        getMenuInflater().inflate(R.menu.main_menu,item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }



    @SuppressLint("SetTextI18n")
    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
        myDialog.setCancelable(false);
        myDialog.setMessage("*Enter Your New Password");
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(40, 0, 40, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,20);
        EditText txtPassword = new EditText(getBaseContext());
        txtPassword.setTextSize(15);
        txtPassword.setGravity(View.TEXT_ALIGNMENT_CENTER);
        txtPassword.setTransformationMethod(new PasswordTransformationMethod());
        txtPassword.setLayoutParams(layoutParams);
        layout.addView(txtPassword);

        CheckBox checkPassword = new CheckBox(getBaseContext());
        checkPassword.setText("Show Password");
        checkPassword.setTextSize(15);
        checkPassword.setGravity(View.TEXT_ALIGNMENT_CENTER);
        checkPassword.setLayoutParams(layoutParams);

        checkPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    txtPassword.setTransformationMethod(null);
                }else{
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                txtPassword.setSelection(txtPassword.length());
            }
        });

        layout.addView(checkPassword);

        myDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(txtPassword.getText().toString().trim().isEmpty()){
                    Toast.makeText(getBaseContext(), "Password field is required", Toast.LENGTH_SHORT).show();
                }else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    APIReceived.myChangePassword myChangePassword = new APIReceived.myChangePassword(txtPassword.getText().toString().trim());
                                    myChangePassword.execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                }
            }
        });

        myDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        myDialog.setView(layout);
        myDialog.show();
    }

    private class myChangePassword extends AsyncTask<String, Void, String> {
        String password;
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        public myChangePassword(String sPassword) {
            password = sPassword;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPAddress = sharedPreferences2.getString("IPAddress", "");

                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("password", password);

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPAddress + "/api/user/change_pass")
                        .method("PUT", body)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = null;

                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null) {
                    JSONObject jsonObjectResponse = new JSONObject(s);
                    loadingDialog.dismissDialog();
                    Toast.makeText(getBaseContext(), jsonObjectResponse.getString("message"), Toast.LENGTH_SHORT).show();

                    if(jsonObjectResponse.getBoolean("success")){

                        AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(APIReceived.this);
                                        pc.removeToken(APIReceived.this);
                                        startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                });
            }
        }
    }

    private class myItemGroups extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                client = new OkHttpClient();
                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPAddress = sharedPreferences2.getString("IPAddress", "");

                SharedPreferences sharedPreferences1 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = sharedPreferences1.getString("token", "");

//                System.out.println("IP Address: " + IPAddress);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPAddress + "/api/item/item_grp/getall")
                        .addHeader("Authorization", "Bearer " + token)
                        .method("GET", null)
                        .build();
                Response response = null;

                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null) {
                    if(s.substring(0,1).equals("{")){
                        JSONObject jsonObject1 = new JSONObject(s);
                        String msg = jsonObject1.getString("message");
                        if (jsonObject1.getBoolean("success")) {
                            List<String> tenderTypes = new ArrayList<>();
                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                            tenderTypes.add("All");
                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(ii);
                                tenderTypes.add(jsonObject.getString("code"));
                            }
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(APIReceived.this, android.R.layout.simple_spinner_item, );
//                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            cmbItemGroup.setAdapter(fillItems(tenderTypes));
//                            cmbItemGroup.setText("All");
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    globalJsonObject = new JSONObject();
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getBaseContext(), "Validation \n" + msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    else{
                        globalJsonObject = new JSONObject();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Validation \n" + s, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            Cursor cursor = myDb8.getAllData();
                            while (cursor.moveToNext()){
                                String module = cursor.getString(4);
//                                System.out.println("Moduleee: " + module);
                                if(module.contains("Item Group")){
                                    try {
                                        JSONObject jsonObject1 = new JSONObject(cursor.getString(3));
                                        String msg = jsonObject1.getString("message");
                                        if (jsonObject1.getBoolean("success")) {
                                            List<String> tenderTypes = new ArrayList<>();
                                            tenderTypes.add("All");
                                            JSONArray jsonArray = jsonObject1.getJSONArray("data");
                                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                            String isSales = Objects.requireNonNull(sharedPreferences.getString("isSales", ""));
                                            String isProduction = Objects.requireNonNull(sharedPreferences.getString("isProduction", ""));
                                            for (int ii = 0; ii < jsonArray.length(); ii++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(ii);
                                                tenderTypes.add(jsonObject.getString("code"));
                                            }
                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(APIReceived.this, android.R.layout.simple_spinner_item, tenderTypes);
                                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                                            spinnerItemGroup.setAdapter(adapter);
                                            globalJsonObject = new JSONObject();
                                            loadData();
                                        }else{
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    globalJsonObject = new JSONObject();
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getBaseContext(), "Validation \n" + msg, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } catch (JSONException ex) {
                                        globalJsonObject = new JSONObject();
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        globalJsonObject = new JSONObject();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Validation \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(hidden_title.equals("API Transfer Item")){
//            globalJsonObject = new JSONObject();
//            getItems(0);
//        }else if(hidden_title.equals("API Received Item")){
//            globalJsonObject = new JSONObject();
//            getItems(0);;
//        }
//        else {
//            globalJsonObject = new JSONObject();
//            getItems(0);
//        }
//        if(API_ItemInfo.isSubmit){
//            API_ItemInfo.isSubmit = false;
//            loadData();
//        }
        btnRefresh.performClick();
    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    pc.loggedOut(APIReceived.this);
                    pc.removeToken(APIReceived.this);
                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public ArrayAdapter<String> fillItems(List<String> items){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, items);
    }

    @SuppressLint("SetTextI18n")
    public void uiItems2(int id, String itemName, String sapNumber, double quantity, String fromBranch, boolean isSelected, int receivedQuantity, String uom, int itemID, boolean isClosed, int iterate,String itemCode,String fromWhsee, double qty){
        try{
            GridLayout gridLayout = findViewById(R.id.grid);
            MaterialCardView cardView = new MaterialCardView(APIReceived.this);
            cardView.setRadius(15);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int boxWidth = 0,boxHeight = 0;
            if(width <= 720){
                boxHeight = 230;
                boxWidth = 190;
            }else{
                boxHeight = 320;
                boxWidth = 280;
            }

            int cardViewMarginLeft = 0;
            if(iterate % 2 == 0){
                cardViewMarginLeft = 5;
            }

            LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(boxWidth, boxHeight);
            layoutParamsCv.setMargins(cardViewMarginLeft, 5, 5, 5);
            cardView.setLayoutParams(layoutParamsCv);
            cardView.setRadius(12);
            cardView.setCardElevation(5);

//        System.out.println("item: " + itemName);
//
            cardView.setVisibility(View.VISIBLE);
            gridLayout.addView(cardView);
            final LinearLayout linearLayout = new LinearLayout(APIReceived.this);
            LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
            linearLayout.setLayoutParams(layoutParamsLinear);
            linearLayout.setTag(id);

            linearLayout.setBackgroundColor(myDb3.checkItemSelected(itemName, hidden_title) ? Color.RED : Color.WHITE);

            linearLayout.setOnClickListener(view -> {
                if (hidden_title.equals("API Production Order List")) {
                    if (isSelected || isClosed) {
                        Toast.makeText(getBaseContext(), "'" + itemName + "' is already closed!", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
                        myDialog.setCancelable(false);
                        myDialog.setTitle(itemName);
//            System.out.println("ID: " + finalDocEntry);
                        myDialog.setMessage("Are you sure you want to close?");
                        myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                JSONObject jsonObject = new JSONObject();
                                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                                String IPaddress = sharedPreferences2.getString("IPAddress", "");
                                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                                okhttp3.Request request = new okhttp3.Request.Builder()
                                        .url(IPaddress + "/api/production/order/details/close/" + itemID)
                                        .method("PUT", body)
                                        .addHeader("Authorization", "Bearer " + token)
                                        .addHeader("Content-Type", "application/json")
                                        .build();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onResponse(Call call, okhttp3.Response response) {
                                        String result = "";
                                        try {
                                            result = response.body().string();
//                            System.out.println(result);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String finalResult = result;
                                        APIReceived.this.runOnUiThread(() -> {
                                            try {
                                                JSONObject jj = new JSONObject(finalResult);
                                                boolean isSuccess = jj.getBoolean("success");
                                                if (isSuccess) {
                                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                                    boolean isInserted = myDb3.updateSelected(Integer.toString(id), 1, quantity, "", "");
                                                    if (isInserted) {
                                                        loadData();
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Failed to Close", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    String msg = jj.getString("message");
                                                    if (msg.equals("Token is invalid")) {
                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                                        builder.setCancelable(false);
                                                        builder.setMessage("Your session is expired. Please login again.");
                                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                                            pc.loggedOut(APIReceived.this);
                                                            pc.removeToken(APIReceived.this);
                                                            startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                                            finish();
                                                            dialog.dismiss();
                                                        });
                                                        builder.show();
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        myDialog.setNegativeButton("No", (dialogInterface, i1) -> dialogInterface.dismiss());
                        myDialog.show();
                    }
                } else {
                    if (myDb3.checkItemSelected(itemName, hidden_title)) {
                        Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                    } else {
                        anotherFunction2(id, itemName, sapNumber, quantity, fromBranch, isSelected, receivedQuantity, uom, itemID, isClosed, iterate, itemCode, linearLayout, fromWhsee, qty);
                    }
                }
            });
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.setVisibility(View.VISIBLE);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(20, 0, 20, 0);
            LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsItemLeft.setMargins(20, (hidden_title.equals("API Received from Production") || hidden_title.equals("API Item Request For Transfer") ? - 100 : -100), 0, 10);

            TextView txtItemName = new TextView(APIReceived.this);
            String cutWord = cutWord(itemName, 25);
            txtItemName.setText(cutWord);
            txtItemName.setLayoutParams(layoutParams);
            txtItemName.setTextSize(15);
            txtItemName.setVisibility(View.VISIBLE);

            TextView txtItemLeft = new TextView(APIReceived.this);
            txtItemLeft.setLayoutParams(layoutParamsItemLeft);
            txtItemLeft.setTextSize(13);
            txtItemLeft.setVisibility(View.VISIBLE);
            txtItemLeft.setText("Del. Qty: " +df.format(quantity));
            txtItemLeft.setTextColor(Color.parseColor("#34A853"));
            if (isSelected || isClosed) {
                linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                txtItemName.setTextColor(Color.rgb(250, 250, 250));
                txtItemLeft.setTextColor(Color.rgb(250, 250, 250));
            } else if(hidden_title.equals("API Received from Production") && receivedQuantity > 0) {
                linearLayout.setBackgroundColor(Color.rgb(250, 208, 17));
                txtItemName.setTextColor(Color.BLACK);
                txtItemLeft.setTextColor(Color.BLACK);
            }else {
                linearLayout.setBackgroundColor(Color.rgb(250, 250, 250));
                txtItemName.setTextColor(Color.rgb(28, 28, 28));
                txtItemLeft.setTextColor(Color.parseColor("#34A853"));
            }
            cardView.setVisibility(View.VISIBLE);
            cardView.addView(linearLayout);
            linearLayout.addView(txtItemName);
            linearLayout.addView(txtItemLeft);
            gridLayout.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.VISIBLE);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void anotherFunction2(int id, String itemName, String sapNumber, double quantity, String fromBranch, boolean isSelected, int receivedQuantity, String uom, int itemID, boolean isClosed, int iterate,String itemCode,LinearLayout linearLayout, String fromWhsee, double qty){
        final String[] sResult = {""};
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(APIReceived.this);
        builder.setTitle(itemName);
        builder.setCancelable(false);
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView lblFromSelectedBranch = new TextView(getBaseContext());
        TextView lblToSelectedBranch = new TextView(getBaseContext());

        TextInputLayout lblQuantity = new TextInputLayout(APIReceived.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        LinearLayout.LayoutParams layoutParamsLblQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsLblQuantity.setMargins(0, 5, 0, 5);
        lblQuantity.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        lblQuantity.setBoxCornerRadii(5, 5, 5, 5);
        lblQuantity.setLayoutParams(layoutParamsLblQuantity);

        TextInputEditText txtQuantity = new TextInputEditText(lblQuantity.getContext());
        LinearLayout.LayoutParams layoutParamsTxtQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtQuantity.setLayoutParams(layoutParamsTxtQuantity);
        txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtQuantity.setHint("*Enter Quantity");

        if(hidden_title.equals("API Item Request For Transfer")){
            DecimalFormat df2 = new DecimalFormat(".000");
            txtQuantity.setText(df2.format(qty));
        }

        TextView lblVariance = new TextView(getBaseContext());

        txtQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double deliveredQty = quantity;
                    double inputQty = lblQuantity.getEditText().getText().toString().trim().isEmpty() || lblQuantity.getEditText().getText().toString().trim().equals(".") ? 0.00 : Double.parseDouble(lblQuantity.getEditText().getText().toString().trim());
                    double variance = inputQty - deliveredQty;
                    if (lblVariance != null) {
                        lblVariance.setText("Variance: " + variance);
                        if (variance == 0) {
                            lblVariance.setTextColor(Color.BLACK);
                        } else if (variance > 0) {
                            lblVariance.setTextColor(Color.BLUE);
                        } else if (variance < 0) {
                            lblVariance.setTextColor(Color.RED);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        lblQuantity.addView(txtQuantity);
        lblQuantity.getEditText().setFocusable(true);

        layout.addView(lblQuantity);
        double inputQty = lblQuantity.getEditText().getText().toString().trim().isEmpty() || lblQuantity.getEditText().getText().toString().trim().equals(".") ? 0.00 : Double.parseDouble(lblQuantity.getEditText().getText().toString().trim());
        lblVariance.setText("Variance: " + (inputQty - quantity));
        lblVariance.setTextColor(Color.RED);
        lblVariance.setTextSize(15);
        lblVariance.setGravity(View.TEXT_ALIGNMENT_CENTER);
        try {
            double deliveredQty = quantity;
            double variance = inputQty - deliveredQty;
            if (lblVariance != null) {
                lblVariance.setText("Variance: " + variance);
                if (variance == 0) {
                    lblVariance.setTextColor(Color.BLACK);
                } else if (variance > 0) {
                    lblVariance.setTextColor(Color.BLUE);
                } else if (variance < 0) {
                    lblVariance.setTextColor(Color.RED);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
        }

        layout.addView(lblVariance);
        AlertDialog alertDialog = null;
        if (hidden_title.equals("API Item Request For Transfer")) {
            Cursor cursor = myDb3.getAllData(hidden_title);
            String cFromBranch = "",cToBranch = "";
            if(cursor != null){
                while (cursor.moveToNext()){
                    System.out.println("from branch " + cursor.getString(2));
                    cFromBranch = cursor.getString(2);
                    cToBranch = cursor.getString(8);
                }
            }

            LinearLayout.LayoutParams layoutParamsBranch2 = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView lblFromBranch = new TextView(getBaseContext());
            lblFromBranch.setText("*From Warehouse");
            lblFromBranch.setTextColor(Color.rgb(0, 0, 0));
            lblFromBranch.setTextSize(15);
            lblFromBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblFromBranch);

            LinearLayout layoutFromBranch = new LinearLayout(getBaseContext());
            layoutFromBranch.setLayoutParams(layoutParamsLblQuantity);
            layoutFromBranch.setOrientation(LinearLayout.HORIZONTAL);
            if(hidden_title.equals("API Item Request For Transfer")) {
                lblFromSelectedBranch.setText(fromWhsee.trim().isEmpty() ? "N/A" : fromWhsee);
            }else{
                lblFromSelectedBranch.setText("N/A");
            }
//                            lblFromSelectedBranch.setText("N/A");

            lblFromSelectedBranch.setTextColor(Color.BLACK);
            lblFromSelectedBranch.setTextSize(15);
            lblFromSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblFromSelectedBranch.setLayoutParams(layoutParamsBranch2);
            layoutFromBranch.addView(lblFromSelectedBranch);

            TextView btnFromSelectBranch = new TextView(APIReceived.this);
            btnFromSelectBranch.setPadding(20, 10, 20, 10);
            btnFromSelectBranch.setText("...");
            btnFromSelectBranch.setBackgroundResource(R.color.colorPrimary);
            btnFromSelectBranch.setTextColor(Color.WHITE);
            btnFromSelectBranch.setTextSize(13);
            btnFromSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                btnFromSelectBranch.setLayoutParams(layoutParamsBranch2);

            layoutFromBranch.addView(btnFromSelectBranch);
            layout.addView(layoutFromBranch);

            TextView lblToBranch = new TextView(getBaseContext());
            lblToBranch.setText("*To Warehouse");
            lblToBranch.setTextColor(Color.rgb(0, 0, 0));
            lblToBranch.setTextSize(15);
            lblToBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
            layout.addView(lblToBranch);

            LinearLayout layoutToBranch = new LinearLayout(getBaseContext());
            layoutToBranch.setLayoutParams(layoutParamsLblQuantity);
            layoutToBranch.setOrientation(LinearLayout.HORIZONTAL);

            lblToSelectedBranch.setText("N/A");
            lblToSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
            lblToSelectedBranch.setTextSize(15);
            lblToSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblToSelectedBranch.setLayoutParams(layoutParamsBranch2);
            layoutToBranch.addView(lblToSelectedBranch);

            TextView btnToSelectBranch = new TextView(APIReceived.this);
            btnToSelectBranch.setPadding(20, 10, 20, 10);
            btnToSelectBranch.setText("...");
            btnToSelectBranch.setBackgroundResource(R.color.colorPrimary);
            btnToSelectBranch.setTextColor(Color.WHITE);
            btnToSelectBranch.setTextSize(13);
            btnToSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                btnFromSelectBranch.setLayoutParams(layoutParamsBranch2);

            layoutToBranch.addView(btnToSelectBranch);
            layout.addView(layoutToBranch);
            builder.setPositiveButton("Add to Cart", null);
        builder.setView(layout);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

            alertDialog = builder.show();

            String finalCFromBranch = cFromBranch;
            AlertDialog finalAlertDialog = alertDialog;
            btnFromSelectBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    double qtyy = 0.00;
                    try{
                        qtyy= Double.parseDouble(txtQuantity.getText().toString());
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    try {
                        if(hidden_title.equals("API Item Request For Transfer")){

                            Object[] ooo = {id,itemName,sapNumber,quantity,fromBranch,isSelected,receivedQuantity,uom,itemID,isClosed,iterate,itemCode,linearLayout, fromWhsee, qtyy};
                            showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, itemName,null, ooo);
                            if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                                finalAlertDialog.dismiss();
                                showAvailabelQuantity.execute("");
                            }
                        }else {
                            myWarehouse myWarehouse = new myWarehouse("?branch=" + finalCFromBranch, lblFromSelectedBranch, itemName, itemCode,"Warehouse", false,"branch",finalCFromBranch);
                            if (myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING) {
                                try {
                                    myWarehouse.execute("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            String finalCToBranch = cToBranch;
            btnToSelectBranch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    myWarehouse myWarehouse = new myWarehouse("?branch=" + finalCToBranch,lblToSelectedBranch,itemName, itemCode,"Warehouse", false,"branch",finalCToBranch);
                    myWarehouse.execute("");
//                                    showFromWarehouse(lblToSelectedBranch, gTemp,itemName);
                }
            });
        }else{
            builder.setPositiveButton("Add to Cart", null);
            builder.setView(layout);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog = builder.show();
        }

        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        AlertDialog finalAlertDialog1 = alertDialog;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gI++;
                btn.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(gI == 1){
                            double qty = 0.00;
                            try {
                                qty = Double.parseDouble(lblQuantity.getEditText().getText().toString());
                            } catch (NumberFormatException ex) {
                                qty = 0.00;
                            }
                            if (qty <= 0 && (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Item Request For Transfer"))) {
                                Toast.makeText(getBaseContext(), "Please input atleast 1", Toast.LENGTH_SHORT).show();
                            }
                            else if((lblFromSelectedBranch.getText().toString().equals("N/A") || lblFromSelectedBranch.getText().toString().trim().isEmpty()) && hidden_title.equals("API Item Request For Transfer")) {
                                Toast.makeText(getBaseContext(), "Please select From Warehouse", Toast.LENGTH_SHORT).show();
                            }
                            else if((lblToSelectedBranch.getText().toString().equals("N/A") || lblToSelectedBranch.getText().toString().trim().isEmpty()) && hidden_title.equals("API Item Request For Transfer")) {
                                Toast.makeText(getBaseContext(), "Please select To Warehouse", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                boolean isInserted = false;
                                if (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production")  || hidden_title.equals("API Issue For Packing")|| hidden_title.equals("API Received from Production")) {
                                    System.out.println("dito ka ba?");
                                    isInserted = myDb4.insertData(itemName, qty, title, 1, uom, itemCode, "", "");
                                } else if (hidden_title.equals("API Inventory Count")) {
                                    isInserted = myDb3.insertData("", "", itemName, qty, qty, 0, "", 0, hidden_title, 1, uom, 0, 0, 0, 0, itemCode, "", "", "");
                                } else if (hidden_title.equals("API Item Request For Transfer") || hidden_title.equals("API System Transfer Item")) {
//                                    Toast.makeText(getBaseContext(), String.valueOf(qty), Toast.LENGTH_SHORT).show();
                                    String fromWhse = findWarehouseCode(lblFromSelectedBranch.getText().toString().trim());
                                    String toWhse = findWarehouseCode(lblToSelectedBranch.getText().toString().trim());
                                    isInserted = myDb3.updateSelected(Integer.toString(id), 1, qty, fromWhse, toWhse);
                                }
                                if (isInserted) {
                                    Toast.makeText(getBaseContext(), "Item Added", Toast.LENGTH_SHORT).show();
                                    linearLayout.setBackgroundColor(Color.RED);
                                    int count = linearLayout.getChildCount();
                                    for (int i = 0; i < count; i++) {
                                        TextView tv = (TextView) linearLayout.getChildAt(i);
                                        tv.setTextColor(Color.WHITE);
                                    }
                                    finalAlertDialog1.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), "Item not Added", Toast.LENGTH_SHORT).show();
                                }
                            }
                            btn.setEnabled(true);
                        }else{
                            btn.setEnabled(true);
//                            Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Add to Cart once only", Toast.LENGTH_SHORT).show();
                        }
                        gI = 0;
                    }
                },500);
            }
        });
    }

    public String findWarehouseCode(String value){
        String result = "";
        try{
            if(gBranch != null) {
                if (gBranch.startsWith("{")) {
                    JSONObject jsonObjectResponse = new JSONObject(gBranch);
                    JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String whseCode = jsonObject.has("whsename") ? jsonObject.isNull("whsename") ? "" : jsonObject.getString("whsename") : "";
                        if (whseCode.toLowerCase().trim().equals(value.toLowerCase().trim())) {
                            result = jsonObject.has("whsecode") ? jsonObject.isNull("whsecode") ? "" : jsonObject.getString("whsecode") : "";
                        }
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public void getItems(int docEntry) {
        try{
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String currentBranch = Objects.requireNonNull(sharedPreferences2.getString("branch", ""));
        String currentWhse = Objects.requireNonNull(sharedPreferences2.getString("whse", ""));
        btnRefresh.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(10);
                    } catch (InterruptedException ex) {
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String currentDate = sdf.format(new Date());
                    String appendURL = "";
                    if (docEntry > 0) {
                        appendURL = "/api/sapb1/itdetails/" + docEntry;
                    } else if (hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request")) {
                        appendURL = "/api/item/getall";
                    } else if (hidden_title.equals("API Transfer Item")) {
                        appendURL = "/api/inv/whseinv/getall";
                    } else if (hidden_title.equals("API System Transfer Item")) {
                        appendURL = "/api/inv/trfr/forrec?mode=For Sales Items";
                    } else if (hidden_title.equals("API Item Request For Transfer")) {
                        appendURL = "/api/inv/item_request/for_transfer/get_all";
                    } else if (hidden_title.equals("API Pending Issue For Production")) {
                        appendURL = "/api/production/issue_for_prod/pending/get_all?docstatus=O";
                    } else if (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Received from Production") || hidden_title.equals("API Issue For Packing")) {
                        appendURL = "/api/inv/whseinv/getall";
                    }
                    else if (hidden_title.equals("API Inventory Count")) {
                        System.out.println(gSelectedBranch);
                        appendURL = "/api/inv/count/create?whsecode=" + findWarehouseCode(gSelectedBranch,gBranch);
                    }
                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPAddress = sharedPreferences2.getString("IPAddress", "");
                    System.out.println(hidden_title + "" + appendURL);
                    String URL = IPAddress + appendURL;
                    if (globalJsonObject.toString().equals("{}")) {
                        okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(URL)
                                .method("GET", null)
                                .addHeader("Authorization", "Bearer " + token)
                                .addHeader("Content-Type", "application/json")
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.printStackTrace();
                                        if (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Menu Items") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {

                                        } else {
                                            Toast.makeText(getBaseContext(), "Error Connection \n" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

//                                        Toast.makeText(getBaseContext(), "Error Connection" + (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Menu Items") ? "\n" + e.getMessage() + "\n" + "The data is from Resources" : "\n" + e.getMessage()) , Toast.LENGTH_SHORT).show();

                                        if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
                                            loadOffline("Stock");
                                        } else if (hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request")) {
                                            loadOffline("Item");
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, okhttp3.Response response) {
                                try {
                                    System.out.println("tangg..........");
                                    assert response.body() != null;
                                    String sResult = response.body().string();
                                    MyAppendData myAppendData = new MyAppendData(sResult == null ? "" : sResult, false);
                                    myAppendData.execute("");

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            btnRefresh.setEnabled(true);
                                            ex.printStackTrace();
//                                            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnRefresh.setEnabled(true);
                            }
                        });

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                GridLayout gridLayout = findViewById(R.id.grid);
//                                gridLayout.removeAllViews();
//                            }
//                        });

                        MyAppendData myAppendData = new MyAppendData(globalJsonObject.toString(), false);
                        myAppendData.execute("");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRefresh.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gI++;
                btnDone.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        if(gI == 1){
                            btnDone.setEnabled(true);
                            navigateDone();
                        }else{
                            btnDone.setEnabled(true);
//                            Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Done once only", Toast.LENGTH_SHORT).show();
                        }
                        gI = 0;
                    }
                },500);

            }
        });
        }catch (Exception ex){
            btnRefresh.setEnabled(true);
            ex.printStackTrace();
        }
    }

    public void loadOffline(String fromModule){
        Cursor cursor = myDb8.getAllData();
        while (cursor.moveToNext()){
            String module = cursor.getString(4);
            if(module.equals(fromModule)){
                try {
                    if(!module.equals("Item Group")) {
                        globalJsonObject = new JSONObject(cursor.getString(3));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if(!module.equals("Item Group")) {
                    runOnUiThread(new Runnable(){
                        public void run() {
                            System.out.println("hooooooooooy");
                            MyAppendData myAppendData = new MyAppendData(cursor.getString(3),false);
                            myAppendData.execute("");
                        }
                    });
                }
            }
        }
    }

    private class MyAppendData extends AsyncTask<String, Void, String> {
        String sResult = "";
        boolean sItemGroup = false;
        public MyAppendData(String result,boolean isItemGroup){
            sResult = result;
            sItemGroup = isItemGroup;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {
            return sResult;
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                JSONObject jsonObjectResponse = new JSONObject();
                List<String> listItems = new ArrayList<String>();
                if(s.startsWith("{")){
                    if (!globalJsonObject.toString().equals("{}")) {
                        jsonObjectResponse = globalJsonObject;
                    } else {
                        globalJsonObject = new JSONObject(s);
                        jsonObjectResponse = new JSONObject(s);
                    }
                    if (jsonObjectResponse.getBoolean("success")) {
                        JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                        runOnUiThread(new Runnable() {
                            @SuppressLint({"ResourceType", "SetTextI18n"})
                            @Override
                            public void run() {
                                try {
                                    GridLayout gridLayout = findViewById(R.id.grid);
                                    gridLayout.removeAllViews();
                                    int iterate = 1;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                        String item,itemCode = "";
                                        String uom = "", uomGroup = "",warehouse="";
                                        double price = 0.00;
                                        double stockQuantity = 0.00;
                                        int docEntry1 = 0;
                                        int store_quantity = 0, auditor_quantity = 0, variance_quantity = 0;
                                        boolean isIssued = (hidden_title.equals("API Production Order List") && (!jsonObject1.isNull("issued")));
                                        String prodStatus = (hidden_title.equals("API Production Order List") ? jsonObject1.isNull("status") ? "" : jsonObject1.getString("status") : "");
                                        switch (hidden_title) {
                                            case "API Item Request":
                                                item = jsonObject1.has("item_name") ?  jsonObject1.getString("item_name") : "";
                                                itemCode = jsonObject1.has("item_code") ? jsonObject1.getString("item_code") : "";
                                                break;
                                            case "API Menu Items":
                                            case "API Transfer Item":
                                            case "API Inventory Count":
                                            case "API Pull Out Count":
                                            case "API Received Item":
                                            case "API Issue For Production":
                                            case "API Issue For Packing":
                                            case "API Received from Production":
                                                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                String isManager = sharedPreferences2.getString("isManager", "");
                                                item = jsonObject1.has("item_name") ? jsonObject1.getString("item_name") : hidden_title.equals("API Transfer Item") && jsonObject1.has("item_code") ? jsonObject1.getString("item_code") : jsonObject1.has("code") ?  jsonObject1.getString("code") : "";
                                                itemCode = jsonObject1.has("item_code") ? jsonObject1.getString("item_code") : "";
//                                    JSONObject jsonObjectItem = jsonObject1.getJSONObject("item");
                                                if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item")) {
                                                    price = jsonObject1.has("price") ? jsonObject1.getDouble("price") : 0.00;
                                                    uom = jsonObject1.getString("uom");
                                                }
                                                if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
                                                    stockQuantity = jsonObject1.isNull("quantity") ? 0.00 : jsonObject1.getDouble("quantity");
                                                    uom = jsonObject1.getString("uom");
                                                } else if (hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                                                    stockQuantity = jsonObject1.getDouble("quantity");
                                                    uom = jsonObject1.getString("uom");
                                                }
                                                if (hidden_title.equals("API Transfer Item")) {
                                                    warehouse = jsonObject1.has("warehouse") ? jsonObject1.getString("warehouse") : "";
                                                }

                                                if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")) {
                                                    store_quantity = jsonObject1.getInt("sales_count");
                                                    auditor_quantity = jsonObject1.getInt("auditor_count");
                                                    variance_quantity = jsonObject1.getInt("variance");
                                                    uom = jsonObject1.getString("uom");
                                                }

                                                break;
                                            case "API System Transfer Item":
                                            case "API Confirm Issue For Production":
                                            case "API Item Request For Transfer":
                                            case "API Pending Issue For Production":
                                            case "API Production Order List":
                                                item = jsonObject1.getString("reference");
                                                docEntry1 = jsonObject1.getInt("id");
                                                break;
                                            default:
                                                item = jsonObject1.getString("docnum");
                                                docEntry1 = jsonObject1.getInt("docentry");
                                                break;
                                        }

                                        String supplier = "";
//                                    if (hidden_title.equals("API Received from SAP") && spinner.getSelectedItemPosition() == 1) {
//                                        supplier = jsonObject1.getString("cardcode");
//                                    }
                                        if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production") ||  hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
                                            stockQuantity -= myDb7.getDecreaseQuantity(item);
                                            stockQuantity += myDb7.getIncreaseQuantity(item);
                                            uom = jsonObject1.has("uom") ? jsonObject1.getString("uom") : "";
                                            if (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
                                                uomGroup = !jsonObject1.isNull("uom_group") ? jsonObject1.getString("uom_group") : "0";
                                            }
                                        }
                                        if(!hidden_title.equals("API Item Request For Transfer") && !hidden_title.equals("API Inventory Count")){
                                            uomGroup = jsonObject1.isNull("item_group") ? "" : jsonObject1.getString("item_group");
                                        }
                                        listItems.add(item);
                                        if(!txtSearch.getText().toString().trim().isEmpty()){
                                            if(item.trim().toLowerCase().contains(txtSearch.getText().toString().trim().toLowerCase())){
                                                if(!cmbItemGroup.getText().toString().trim().isEmpty()){
                                                    if(cmbItemGroup.getText().toString().trim().equals("All")){
                                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                        iterate+= 1;
                                                    }
                                                    else if(jsonObject1.has("item_group")){
                                                        if(cmbItemGroup.getText().toString().trim().toLowerCase().contains(jsonObject1.getString("item_group").toLowerCase())){
                                                            loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                            iterate+= 1;
                                                        }
                                                        else{

                                                        }
                                                    }else {
                                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                        iterate+= 1;
                                                    }
                                                }else{
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                    iterate+= 1;
                                                }
                                            }
                                        }else{
                                            if(!cmbItemGroup.getText().toString().trim().isEmpty()){
                                                if(cmbItemGroup.getText().toString().trim().equals("All")){
                                                    System.out.println("111");
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                    iterate+= 1;
                                                }
                                                else if(jsonObject1.has("item_group")){
                                                    System.out.println("222");
                                                    if(cmbItemGroup.getText().toString().trim().toLowerCase().contains(jsonObject1.getString("item_group").toLowerCase())){
                                                        loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                        iterate+= 1;
                                                    }
                                                }else {
                                                    System.out.println("333");
                                                    loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                    iterate+= 1;
                                                }

                                            }
                                            else{
                                                loadUIItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity, uom, isIssued, prodStatus, uomGroup,iterate,itemCode,warehouse);
                                                iterate+= 1;
                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setVisibility(View.GONE);
                                            ex.printStackTrace();
                                            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        progressBar.setVisibility(View.GONE);
                        String msg = jsonObjectResponse.getString("message");
                        if (msg.equals("Token is invalid")) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                            builder.setCancelable(false);
                            builder.setMessage("Your session is expired. Please login again.");
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                pc.loggedOut(APIReceived.this);
                                pc.removeToken(APIReceived.this);
                                startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                finish();
                                dialog.dismiss();
                            });
                            builder.show();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtSearch.setAdapter(fillItems(listItems));
                        }
                    });
                }else{
                    Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getBaseContext(), "Front-end Error: \n" + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        ex.printStackTrace();
                    }
                });
            }

            runOnUiThread(new Runnable() {
                @SuppressLint({"ResourceType", "SetTextI18n"})
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadUIItems(String item, double price, double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity, String uom, boolean isIssued, String prodStatus,String uomGroup, int iterate, String itemCode,String warehouse){
//        if (!txtSearch.getText().toString().trim().isEmpty()) {
//            if (txtSearch.getText().toString().trim().contains(item)) {
//                uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity,uom,isIssued, prodStatus,uomGroup);
//            }
//        }else{
//            uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity,uom,isIssued, prodStatus,uomGroup);
//        }
        uiItems(item, price, stockQuantity, docEntry1, supplier, store_quantity, auditor_quantity, variance_quantity,uom,isIssued, prodStatus,uomGroup,iterate,itemCode,warehouse );
    }

    private int getWidthResolution(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width;
    }

    @SuppressLint("SetTextI18n")
    public void uiItems(String item, Double price, Double stockQuantity, int docEntry1, String supplier, int store_quantity, int auditor_quantity, int variance_quantity,String uom,boolean isIssued, String prodStatus,String uomGroup, int iterate,String itemCode,String warehouse) {
        GridLayout gridLayout = findViewById(R.id.grid);
        MaterialCardView cardView = new MaterialCardView(APIReceived.this);
        cardView.setRadius(15);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int boxWidth = 0,boxHeight = 0;
        if(width <= 720){
           boxHeight = 230;
           boxWidth = 190;
        }else{
            boxHeight = 320;
            boxWidth = 280;
        }

        int cardViewMarginLeft = 0;
        if(iterate % 2 == 0){
            cardViewMarginLeft = 5;
        }
        LinearLayout.LayoutParams layoutParamsCv = new LinearLayout.LayoutParams(boxWidth, boxHeight);
        layoutParamsCv.setMargins(cardViewMarginLeft, 5, 5, 5);
        cardView.setLayoutParams(layoutParamsCv);
        cardView.setRadius(12);
        cardView.setCardElevation(5);

        cardView.setVisibility(View.VISIBLE);
        gridLayout.addView(cardView);
        final LinearLayout linearLayout = new LinearLayout(getBaseContext());
        linearLayout.setBackgroundColor(Color.rgb(255, 255, 255));
        LinearLayout.LayoutParams layoutParamsLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 5f);
        linearLayout.setLayoutParams(layoutParamsLinear);
        linearLayout.setTag("Linear" + item);

        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(View.TEXT_ALIGNMENT_CENTER);
        linearLayout.setVisibility(View.VISIBLE);

        String finalItem = item;
        int finalDocEntry = docEntry1;
        double finalPrice = price;
        double finalStockQuantity = stockQuantity;
        String finalSupplier = supplier;

        linearLayout.setOnClickListener(view -> {
//            linearLayout.setEnabled(false);
            if (hidden_title.equals("API Issue For Production")) {
                utility_class utilityc = new utility_class();
                String[] prodList = utility_class.prodList;
                if (utilityc.isAllowProdPacking(prodList, APIReceived.this)) {
                    Object[] ooo = {finalItem, finalPrice, finalDocEntry, finalSupplier, 0.000, 0, 0, 0, uom, uomGroup, linearLayout, itemCode, warehouse};
                    showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, finalItem, ooo, null);
                    if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                        showAvailabelQuantity.execute("");
                    }
                } else {
                    String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                    for (String list : prodList) {
                        appendMsg += "- " + list + "\n";
                    }
                    String title = "Validation";
                    String msg = "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                    utilityc.customAlertDialog(title, msg, APIReceived.this);
                }
            } else if (hidden_title.equals("API Issue For Packing")) {
                utility_class utilityc = new utility_class();
                String[] packingList = utility_class.packingList;
                if (utilityc.isAllowProdPacking(packingList, APIReceived.this)) {
                    Object[] ooo = {finalItem, finalPrice, finalDocEntry, finalSupplier, 0.000, 0, 0, 0, uom, uomGroup, linearLayout, itemCode, warehouse};
                    gIsShowAvailableSubmit = false;
                    showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, finalItem, ooo, null);
                    if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                        showAvailabelQuantity.execute("");
                        //                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom,uomGroup,linearLayout, itemCode);
                    }
                } else {
                    String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                    for (String list : packingList) {
                        appendMsg += "- " + list + "\n";
                    }
                    String title = "Validation";
                    String msg = "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                    utilityc.customAlertDialog(title, msg, APIReceived.this);
                }
            } else if (hidden_title.equals("API Menu Items")) {
                if (myDb.checkItem(item)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }

            } else if (hidden_title.equals("API Received Item")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {

                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }
            } else if (hidden_title.equals("API Transfer Item")) {
                Object[] ooo = {finalItem, finalPrice, finalDocEntry, finalSupplier, 0.000, 0, 0, 0, uom, uomGroup, linearLayout, itemCode, warehouse};
                showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, finalItem, ooo, null);
                if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                    showAvailabelQuantity.execute("");
                }
//                if (myDb4.checkItem(item, title)) {
//                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
//                } else {
//                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom,uomGroup,linearLayout, itemCode,"");
//                }
            } else if (hidden_title.equals("API Item Request")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }
            } else if (hidden_title.equals("API Inventory Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }
            } else if (hidden_title.equals("API Received from Production")) {
                if (myDb4.checkItem(item, title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }
            } else if (hidden_title.equals("API Issue For Production")) {
//                if (myDb4.checkItem(item, title)) {
//                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
//                } else {
//
//                }
                anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
            } else if (hidden_title.equals("API Issue For Packing")) {
                anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
            } else if (hidden_title.equals("API Pull Out Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    Toast.makeText(getBaseContext(), "This item is selected", Toast.LENGTH_SHORT).show();
                } else {
                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
                }
            } else {
                anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode, "");
            }
        });

        cardView.addView(linearLayout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 0, 20, 0);
        LinearLayout.LayoutParams layoutParamsItemLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsItemLeft.setMargins(20,  -70, 0, 10);

        TextView txtItemName = new TextView(getBaseContext());
        txtItemName.setTag(item);
        txtItemName.setText(cutWord(item, 35));
        txtItemName.setTextColor(Color.rgb(0, 0, 0));
        txtItemName.setLayoutParams(layoutParams);
        txtItemName.setTextSize(15);
        txtItemName.setVisibility(View.VISIBLE);
        linearLayout.addView(txtItemName);

        if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count")  || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
            TextView txtItemLeft = new TextView(getBaseContext());
            txtItemLeft.setLayoutParams(layoutParamsItemLeft);
            txtItemLeft.setTextColor(Color.rgb(0, 0, 0));
            txtItemLeft.setTextSize(13);
            txtItemLeft.setVisibility(View.VISIBLE);
            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")) {
                txtItemLeft.setText(df.format(stockQuantity)+ " available");
                if (stockQuantity <= 0) {
                    txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                } else if (stockQuantity <= 10) {
                    txtItemLeft.setTextColor(Color.rgb(247, 154, 22));
                } else if (stockQuantity > 11) {
                    txtItemLeft.setTextColor(Color.rgb(30, 203, 6));
                }
            }

            SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
            String isManager = sharedPreferences2.getString("isManager", "");
            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Inventory Count")) {
                txtItemLeft.setText(df.format(variance_quantity) + " variance");
                if (variance_quantity < 0) {
                    txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                } else {
                    txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                }
            }
            if (Integer.parseInt(isManager) > 0 && hidden_title.equals("API Pull Out Count")) {
                txtItemLeft.setText(df.format(variance_quantity) + " variance");
                if (variance_quantity < 0) {
                    txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                } else {
                    txtItemLeft.setTextColor(Color.rgb(6, 188, 212));
                }
            }

            if (stockQuantity <= 0 && hidden_title.equals("API Inventory Count")) {
                linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                txtItemName.setTextColor(Color.rgb(255, 255, 255));
                txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
            }
            if (stockQuantity <= 0 && hidden_title.equals("API Pull Out Count") && Integer.parseInt(isManager) <= 0) {
                linearLayout.setBackgroundColor(Color.rgb(94, 94, 94));
                txtItemName.setTextColor(Color.rgb(255, 255, 255));
                txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
            }

            if (hidden_title.equals("API Received Item")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Transfer Item")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Item Request")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(252, 28, 28));
                }
            } else if (hidden_title.equals("API Menu Items")) {
                if (myDb.checkItem(item)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Inventory Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Pull Out Count")) {
                if (myDb3.checkItem(item, hidden_title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Issue For Production")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Issue For Packing")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            } else if (hidden_title.equals("API Received from Production")) {
                if (myDb4.checkItem(item, title)) {
                    linearLayout.setBackgroundColor(Color.rgb(252, 28, 28));
                    txtItemName.setTextColor(Color.rgb(255, 255, 255));
                    txtItemLeft.setTextColor(Color.rgb(255, 255, 255));
                }
            }
            linearLayout.addView(txtItemLeft);
        }

        if(hidden_title.equals("API Production Order List")){
            System.out.println("is issued: " + isIssued + "\n status: " + prodStatus);
            LinearLayout.LayoutParams layoutParamsItemLeft2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsItemLeft2.setMargins(20,  -100, 0, 10);
            TextView txtItemLeft2 = new TextView(APIReceived.this);
            txtItemLeft2.setLayoutParams(layoutParamsItemLeft2);
            txtItemLeft2.setTextSize(13);
            txtItemLeft2.setVisibility(View.VISIBLE);
            txtItemLeft2.setText(isIssued ? "✓ \n" + prodStatus : prodStatus);
            txtItemLeft2.setTextColor(Color.parseColor("#34A853"));
            linearLayout.addView(txtItemLeft2);
        }
    }

    private class showAvailabelQuantity extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(APIReceived.this);
        String gItemCode = "",gItemName = "";
        Object[] gO = null,gO2 = null;
        public showAvailabelQuantity(String itemCode,String itemName, Object[] o, Object[] o2){
            gItemCode = itemCode;
            gItemName = itemName;
            gO = o;
            gO2 = o2;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
                String token = getFromSharedPref("TOKEN", "token");
                String sURL = IPAddress + "/api/inv/per_whse/get_all?item_code=" + gItemCode;
                System.out.println("show avail " + sURL);
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
                ex.printStackTrace();
                Cursor cursor = myDb8.getAllData();
                while (cursor.moveToNext()) {
                    String module = cursor.getString(4);
                    if (module.trim().toLowerCase().equals("per warehouse")) {
                        ex.printStackTrace();
                        return cursor.getString(3);
                    }
                }
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

                        String itemCode = "";
                        ArrayList<String> arrayList = new ArrayList<>();
                        if(jaData.length() > 0) {
                            for (int i = 0; i < jaData.length(); i++) {
                                JSONObject joData = jaData.getJSONObject(i);
                                itemCode = joData.has("item_code") ? joData.isNull("item_code") ? "" : joData.getString("item_code") : "";
                                if(itemCode.trim().toLowerCase().contains(gItemCode.trim().toLowerCase())){
                                    double qty =  joData.has("quantity") ? joData.isNull("quantity") ? 0.00 : joData.getDouble("quantity") : 0.00;
                                    double stockAge =  joData.has("stock_age") ? joData.isNull("stock_age") ? 0.00 : joData.getDouble("stock_age") : 0.00;

                                    String sWhse = joData.has("warehouse") ? joData.isNull("warehouse") ? "" : joData.getString("warehouse") : "";
                                    String sVessel = joData.has("vessel") ? joData.isNull("vessel") ? "" : joData.getString("vessel") : "";
                                    arrayList.add("Warehouse: " + sWhse + "\nQty: " + df.format(qty) + "\nVessel: " + sVessel + "\nStock Age: " + df.format(stockAge));
                                }
                            }
                            
                            AlertDialog.Builder dialog = new AlertDialog.Builder(APIReceived.this);
                            dialog.setTitle(gItemName);
                            dialog.setCancelable(false);

                            LinearLayout layout = new LinearLayout(getBaseContext());
                            layout.setPadding(20, 20, 20, 10);
                            layout.setOrientation(LinearLayout.VERTICAL);

                            ListView listView = new ListView(getBaseContext());
                            layout.addView(listView);
                            ArrayAdapter adapter = new ArrayAdapter(APIReceived.this, android.R.layout.simple_list_item_1, arrayList);
                            listView.setAdapter(adapter);

                            dialog.setView(layout);

                            String positiveText =!(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request For Transfer")) ? "Ok" : "Cancel";
                            dialog.setPositiveButton(positiveText, null);

                            AlertDialog alertDialog = dialog.show();

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if(hidden_title.equals("API Issue For Production") ||hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request For Transfer")){
//                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        String a111 = text.getText().toString();
                                        int firstWordIndex = a111.indexOf("Warehouse: ");
                                        int twoWordIndex = a111.indexOf("\nVessel");
                                        String getWhse = a111.substring(firstWordIndex,twoWordIndex);
                                        getWhse =  getWhse.replace("Warehouse: ","").trim();

                                        String fromWhse = getWhse.replace(getWhse.substring(getWhse.indexOf("\n")),"");
                                        double quantity=0;
                                        try{
                                            quantity = Double.parseDouble(getWhse.substring(getWhse.indexOf("\n"),getWhse.length()).replace("\nQty: ","").replace(",",""));
                                        }catch(Exception ex){
                                            ex.printStackTrace();
                                        }

//                                        ClipData clip = ClipData.newPlainText("Quantity copied!",df.format(quantity));
//                                        clipboard.setPrimaryClip(clip);
                                        String selectedWhse = "";

                                        gIsShowAvailableSubmit= true;
                                        alertDialog.dismiss();

//                                        String finalItem, double finalPrice, Integer finalDocEntry, String finalSupplier, double quantity, int store_quantity, int auditor_quantity, int variance_quantity, String uom, String uomGroup, LinearLayout linearLayout, String itemCode
                                        if(gO != null){
                                            anotherFunction(String.valueOf(gO[0]),(double) gO[1],(int) gO[2],String.valueOf( gO[3]),quantity,(int) gO[5],(int)gO[6], (int) gO[7],String.valueOf(gO[8]),String.valueOf(gO[9]),(LinearLayout) gO[10],String.valueOf(gO[11]),fromWhse);
                                        }else{
                                            anotherFunction2((int) gO2[0],(String) gO2[1], (String) gO2[2],(double)gO2[3],(String) gO2[4],(boolean)gO2[5],(int)gO2[6],(String)gO2[7],(int)gO2[8],(boolean)gO2[9],(int)gO2[10],(String)gO2[11],(LinearLayout)gO2[12], fromWhse,quantity);
                                        }
                                    }
                                }
                            });

                            Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            AlertDialog finalAlertDialog1 = alertDialog;
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finalAlertDialog1.dismiss();
                                    if(gO != null && (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item"))) {
                                        anotherFunction(String.valueOf(gO[0]), (double) gO[1], (int) gO[2], String.valueOf(gO[3]), (double)gO[4], (int) gO[5], (int) gO[6], (int) gO[7], String.valueOf(gO[8]), String.valueOf(gO[9]), (LinearLayout) gO[10], String.valueOf(gO[11]), String.valueOf(gO[12]));
                                    }else{
                                        anotherFunction2((int) gO2[0],(String) gO2[1], (String) gO2[2],(double)gO2[3],(String) gO2[4],(boolean)gO2[5],(int)gO2[6],(String)gO2[7],(int)gO2[8],(boolean)gO2[9],(int)gO2[10],(String)gO2[11],(LinearLayout)gO2[12], (String)gO2[13],(double)gO2[14]);
                                    }
                                }
                            });
                        }
                        else{
                            AlertDialog.Builder dialog = new AlertDialog.Builder(APIReceived.this);
                            dialog.setCancelable(false);
                            dialog.setTitle("Alert")
                                    .setMessage("No available quantity for " + gItemCode);
                            dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }

                    } else {
                        loadingDialog.dismissDialog();
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

    public List<String> returnAvailableQuantity(JSONArray jaData,String findKey,String title) {
        List<String> result = new ArrayList<>();
        result.add(title);
        try {
            for (int i = 0; i < jaData.length(); i++) {
                JSONObject jsonObject = jaData.getJSONObject(i);
                String branch = jsonObject.getString(findKey);
                result.add(branch);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(),  ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @SuppressLint("SetTextI18n")
    public void anotherFunction(String finalItem, double finalPrice, Integer finalDocEntry, String finalSupplier, double quantity, int store_quantity, int auditor_quantity, int variance_quantity, String uom, String uomGroup, LinearLayout linearLayout, String itemCode,String fromWhse) {
        if (hidden_title.equals("API Received Item") || hidden_title.equals("API Menu Items") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Inventory Count") || hidden_title.equals("API Pull Out Count") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")|| hidden_title.equals("API Received from Production")) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(APIReceived.this);
            LinearLayout layout = new LinearLayout(getBaseContext());
            layout.setPadding(40, 40, 40, 40);
            layout.setOrientation(LinearLayout.VERTICAL);
            builder.setTitle(finalItem);
            builder.setCancelable(false);

            Button btn = null;
            AlertDialog finalAlertDialog1 = null;

            TextView lblInformation = new TextView(getBaseContext());
            if (hidden_title.equals("API Menu Items") || hidden_title.equals("API Inventory Count")) {
                lblInformation.setText("Del. Qty: ");
                lblInformation.setTextColor(Color.rgb(0, 0, 0));
                lblInformation.setTextSize(15);
                lblInformation.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblInformation);

                if(hidden_title.equals("API Menu Items")){
                    TextView lblPrice = new TextView(getBaseContext());
                    lblPrice.setText("₱" + finalPrice);
                    lblPrice.setTextColor(Color.rgb(0, 0, 0));
                    lblPrice.setTextSize(15);
                    lblPrice.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(lblPrice);
                }
            }
            TextView lblFromSelectedBranch = new TextView(getBaseContext());
            TextView lblToSelectedBranch = new TextView(getBaseContext());
            final String[] sResult = {""};
            TextInputLayout lblQuantity = new TextInputLayout(APIReceived.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
            LinearLayout.LayoutParams layoutParamsLblQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsLblQuantity.setMargins(0, 5, 0, 5);
            lblQuantity.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
            lblQuantity.setBoxCornerRadii(5, 5, 5, 5);
            lblQuantity.setLayoutParams(layoutParamsLblQuantity);

            TextInputEditText txtQuantity = new TextInputEditText(lblQuantity.getContext());
            LinearLayout.LayoutParams layoutParamsTxtQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txtQuantity.setLayoutParams(layoutParamsTxtQuantity);
            txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            txtQuantity.setHint("*Enter Quantity");
            if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item")){
                DecimalFormat df2 = new DecimalFormat(".000");
                txtQuantity.setText(df2.format(quantity));
            }

            TextView lblVariance = new TextView(getBaseContext());
            txtQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double deliveredQty = lblInformation == null ? 0 : lblInformation.getText().toString().trim().isEmpty() ? 0.00 : Double.parseDouble(lblInformation.getText().toString().replace("Del. Qty: ", "").trim());
                        double inputQty = lblQuantity.getEditText().getText().toString().trim().isEmpty() || lblQuantity.getEditText().getText().toString().trim().equals(".") ? 0.00 : Double.parseDouble(lblQuantity.getEditText().getText().toString().trim().replace(",",""));
                        double variance = inputQty - deliveredQty;
                        lblVariance.setText("Variance: " + variance);
                        if (variance == 0) {
                            lblVariance.setTextColor(Color.BLACK);
                        } else if (variance > 0) {
                            lblVariance.setTextColor(Color.BLUE);
                        } else if (variance < 0) {
                            lblVariance.setTextColor(Color.RED);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            lblQuantity.addView(txtQuantity);
            lblQuantity.getEditText().setFocusable(true);

            layout.addView(lblQuantity);
            if (hidden_title.equals("API Inventory Count")) {
                lblVariance.setText("Variance: " + (0 - 2));
                lblVariance.setTextColor(Color.RED);
                lblVariance.setTextSize(15);
                lblVariance.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblVariance);
            }
            if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")  || hidden_title.equals("API Transfer Item")) {
                LinearLayout.LayoutParams layoutParamsBranch2 = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
                TextView lblFromBranch = new TextView(getBaseContext());
                lblFromBranch.setText(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item") ? "*From Warehouse" : "*To Warehouse");
                lblFromBranch.setTextColor(Color.rgb(0, 0, 0));
                lblFromBranch.setTextSize(15);
                lblFromBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblFromBranch);

                LinearLayout layoutFromBranch = new LinearLayout(getBaseContext());
                layoutFromBranch.setLayoutParams(layoutParamsLblQuantity);
                layoutFromBranch.setOrientation(LinearLayout.HORIZONTAL);

                lblFromSelectedBranch.setText(fromWhse);
                lblFromSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                lblFromSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                lblFromSelectedBranch.setTextSize(15);
                lblFromSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblFromSelectedBranch.setLayoutParams(layoutParamsBranch2);
                layoutFromBranch.addView(lblFromSelectedBranch);

                TextView btnFromSelectBranch = new TextView(APIReceived.this);
                btnFromSelectBranch.setPadding(20, 10, 20, 10);
                btnFromSelectBranch.setText("...");
                btnFromSelectBranch.setBackgroundResource(R.color.colorPrimary);
                btnFromSelectBranch.setTextColor(Color.WHITE);
                btnFromSelectBranch.setTextSize(13);
                btnFromSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                btnFromSelectBranch.setLayoutParams(layoutParamsBranch2);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                layoutFromBranch.addView(btnFromSelectBranch);
                layout.addView(layoutFromBranch);
                builder.setPositiveButton("Add to Cart", null);
                builder.setView(layout);
                AlertDialog alertDialog = builder.show();
                btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                finalAlertDialog1 = alertDialog;

                AlertDialog finalAlertDialog2 = finalAlertDialog1;
                btnFromSelectBranch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double qty =0.0;
                        finalAlertDialog2.dismiss();
                        try{
                            qty = Double.parseDouble(txtQuantity.getText().toString());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        Object[] ooo = {finalItem, finalPrice, finalDocEntry, finalSupplier, qty, store_quantity, auditor_quantity, variance_quantity, uom, uomGroup, linearLayout, itemCode,fromWhse};
                        gIsShowAvailableSubmit = false;
                        showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode, finalItem,ooo,null);
                        if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                            showAvailabelQuantity.execute("");
                            //                    anotherFunction(finalItem, finalPrice, finalDocEntry, finalSupplier, stockQuantity, store_quantity, auditor_quantity, variance_quantity, uom,uomGroup,linearLayout, itemCode);
                        }
                    }
                });
            }
            LinearLayout.LayoutParams layoutParamsBranch2 = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received from Production")) {
//                if (hidden_title.equals("API Transfer Item")) {
//                    LinearLayout.LayoutParams layoutParamsFromBranch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    LinearLayout.LayoutParams layoutParamsBranch3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                    TextView lblFromBranch = new TextView(getBaseContext());
//                    lblFromBranch.setText("*From Warehouse: ");
//                    lblFromBranch.setTextColor(Color.rgb(0, 0, 0));
//                    lblFromBranch.setTextSize(15);
//                    lblFromBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    layout.addView(lblFromBranch);
//
//                    LinearLayout layoutFromBranch = new LinearLayout(getBaseContext());
//                    layoutParamsFromBranch.setMargins(20, 0, 0, 20);
//                    layoutFromBranch.setLayoutParams(layoutParamsFromBranch);
//                    layoutFromBranch.setOrientation(LinearLayout.HORIZONTAL);
//                    layoutParamsBranch3.setMargins(10, 0, 0, 0);
//
//
//                    lblFromSelectedBranch.setText("N/A");
//                    lblFromSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
//                    lblFromSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
//                    lblFromSelectedBranch.setTextSize(15);
//                    lblFromSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    lblFromSelectedBranch.setLayoutParams(layoutParamsBranch2);
//                    layoutFromBranch.addView(lblFromSelectedBranch);
//
//                    TextView btnFromSelectBranch = new TextView(APIReceived.this);
//                    btnFromSelectBranch.setPadding(20, 10, 20, 10);
//                    btnFromSelectBranch.setText("...");
//                    btnFromSelectBranch.setBackgroundResource(R.color.colorPrimary);
//                    btnFromSelectBranch.setTextColor(Color.WHITE);
//                    btnFromSelectBranch.setTextSize(13);
//                    btnFromSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                    btnFromSelectBranch.setLayoutParams(layoutParamsBranch3);
//
//                    btnFromSelectBranch.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                                return;
//                            }
//                            mLastClickTime = SystemClock.elapsedRealtime();
//                            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
//                            String currentBranch = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
//                            try {
//                                myWarehouse myWarehouse = new myWarehouse("?branch=" + currentBranch,lblFromSelectedBranch,finalItem , itemCode);
//                                if(myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING){
//                                    myWarehouse.execute("");
//                                }
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
////                            showFromWarehouse(lblFromSelectedBranch, gTemp,finalItem);
//                        }
//                    });
//                    layoutFromBranch.addView(btnFromSelectBranch);
//                    layout.addView(layoutFromBranch);
//                }

                if (hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received from Production")) {
                    TextView lblToBranch = new TextView(getBaseContext());
                    lblToBranch.setText("*To Warehouse");
                    lblToBranch.setTextColor(Color.rgb(0, 0, 0));
                    lblToBranch.setTextSize(15);
                    lblToBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    layout.addView(lblToBranch);

                    LinearLayout layoutToBranch = new LinearLayout(getBaseContext());
                    layoutToBranch.setLayoutParams(layoutParamsLblQuantity);
                    layoutToBranch.setOrientation(LinearLayout.HORIZONTAL);

                    lblToSelectedBranch.setText("N/A");
                    lblToSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                    lblToSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                    lblToSelectedBranch.setTextSize(15);
                    lblToSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                    lblToSelectedBranch.setLayoutParams(layoutParamsBranch2);
                    layoutToBranch.addView(lblToSelectedBranch);

                    TextView btnToSelectBranch = new TextView(APIReceived.this);
                    btnToSelectBranch.setPadding(20, 10, 20, 10);
                    btnToSelectBranch.setText("...");
                    btnToSelectBranch.setBackgroundResource(R.color.colorPrimary);
                    btnToSelectBranch.setTextColor(Color.WHITE);
                    btnToSelectBranch.setTextSize(13);
                    btnToSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
//                btnFromSelectBranch.setLayoutParams(layoutParamsBranch2);

                    btnToSelectBranch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            try {
                                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                String currentPlant = Objects.requireNonNull(sharedPreferences.getString("plant", ""));
                                String currentBranch = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                                String sParams = hidden_title.equals("API Transfer Item") ? "?plant=" + currentPlant : "?branch=" + currentBranch;
                                String ifKey = hidden_title.equals("API Transfer Item") ? "plant" : "branch";
                                String ifValue  = hidden_title.equals("API Transfer Item") ? currentPlant : currentBranch;
                                myWarehouse myWarehouse = new myWarehouse(sParams,lblToSelectedBranch, finalItem,itemCode,"Warehouse", false,ifKey,ifValue);
                                if(myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING){
                                    myWarehouse.execute("");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            showFromWarehouse(lblToSelectedBranch, sResult[0],finalItem);
                        }
                    });
                    layoutToBranch.addView(btnToSelectBranch);
                    layout.addView(layoutToBranch);
                }
            }
            if(!(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")  || hidden_title.equals("API Transfer Item"))){
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("Add to Cart", null);
                builder.setView(layout);
                AlertDialog alertDialog = builder.show();
                btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                finalAlertDialog1 = alertDialog;
            }
            AlertDialog finalAlertDialog = finalAlertDialog1;
            Button finalBtn = btn;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gI++;
                    finalBtn.setEnabled(false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(gI == 1){
                                finalBtn.setEnabled(true);
                                double qty = 0.00;
                                try{
                                    qty = Double.parseDouble(lblQuantity.getEditText().getText().toString());
                                }catch (NumberFormatException ex){
                                    qty = 0.00;
                                }
                                utility_class utilityc = new utility_class();
                                String[] prodList = utility_class.prodList;
                                String[] packingList = utility_class.packingList;
                                if(hidden_title.equals("API Issue For Production") && !utilityc.isAllowProdPacking(prodList, APIReceived.this)) {
                                    String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                                    for(String list : prodList){
                                        appendMsg+= "- " + list + "\n";
                                    }
                                    String title = "Validation";
                                    String msg =  "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                                    utilityc.customAlertDialog(title,msg, APIReceived.this);

                                }else if(hidden_title.equals("API Issue For Packing") && !utilityc.isAllowProdPacking(packingList, APIReceived.this)) {
                                    String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                                    for (String list : packingList) {
                                        appendMsg += "- " + list + "\n";
                                    }
                                    String title = "Validation";
                                    String msg = "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                                    utilityc.customAlertDialog(title, msg, APIReceived.this);
                                }
                                else if(qty <= 0 && (hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Received from Production"))){
                                    Toast.makeText(getBaseContext(), "Please input atleast 1", Toast.LENGTH_SHORT).show();
                                }
                                else if(lblFromSelectedBranch.getText().toString().equals("N/A") && hidden_title.equals("API Issue For Production")) {
                                    Toast.makeText(getBaseContext(), "Please select From Warehouse", Toast.LENGTH_SHORT).show();
                                }
                                else if(lblFromSelectedBranch.getText().toString().equals("N/A") && hidden_title.equals("API Issue For Packing")) {
                                    Toast.makeText(getBaseContext(), "Please select From Warehouse", Toast.LENGTH_SHORT).show();
                                }
                                else if(lblToSelectedBranch.getText().toString().equals("N/A") && hidden_title.equals("API Received from Production")) {
                                    Toast.makeText(getBaseContext(), "Please select To Warehouse", Toast.LENGTH_SHORT).show();
                                }
                                else if((lblFromSelectedBranch.getText().toString().equals("N/A")  || lblFromSelectedBranch.getText().toString().trim().isEmpty()) && hidden_title.equals("API Transfer Item")) {
                                    Toast.makeText(getBaseContext(), "Please select From Warehouse", Toast.LENGTH_SHORT).show();
                                }
                                else if((lblToSelectedBranch.getText().toString().equals("N/A")  || lblToSelectedBranch.getText().toString().trim().isEmpty()) && hidden_title.equals("API Transfer Item")) {
                                    Toast.makeText(getBaseContext(), "Please select To Warehouse", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    int idd = 0;
                                    boolean isInserted = false;
                                    if(hidden_title.equals("API Received Item") || hidden_title.equals("API Transfer Item") || hidden_title.equals("API Item Request") || hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Received from Production")){
                                        System.out.println("im here!");
                                        String fromWhse = lblFromSelectedBranch != null && (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item"))  ? findWarehouseCode(lblFromSelectedBranch.getText().toString(),gBranch) : "";
                                        String toWhse = lblToSelectedBranch != null && (hidden_title.equals("API Transfer Item") || hidden_title.equals("API Received from Production"))  ? findWarehouseCode(lblToSelectedBranch.getText().toString(),gBranch) : "";
                                        if(hidden_title.equals("API Transfer Item")){
                                            idd = myDb4.isSameItemWhse(finalItem,fromWhse, toWhse);
                                            if(idd > 0){
                                                isInserted = myDb4.updateQuantity(String.valueOf(idd), qty,true);
                                            }
                                            else{
                                                isInserted = myDb4.insertData(finalItem, qty, title, 1,uom,itemCode,fromWhse,toWhse);
                                            }
                                        }else if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")){
                                            idd = myDb4.isSameItemFromWhse(finalItem,fromWhse);
                                            if(idd > 0){
                                                isInserted = myDb4.updateQuantityWhse(String.valueOf(idd),qty,"fromWhse",fromWhse,true);
                                            }
                                            else{
                                                isInserted = myDb4.insertData(finalItem, qty, title, 1,uom,itemCode,fromWhse,toWhse);
                                            }
                                        }
                                        else{
                                            isInserted = myDb4.insertData(finalItem, qty, title, 1,uom,itemCode,fromWhse,toWhse);
                                        }
                                    }else if(hidden_title.equals("API Inventory Count")){
                                        isInserted = myDb3.insertData("", "",finalItem, qty, qty, 0, "", 0,hidden_title,1,uom,0,0,0,0, itemCode,"","","");
                                    }
                                    if(isInserted){
                                        Toast.makeText(getBaseContext(), "Item "+ (idd <= 0 ? "Added!" : "Updated!"),Toast.LENGTH_SHORT).show();
                                        linearLayout.setBackgroundColor(Color.RED);
                                        int count = linearLayout.getChildCount();
                                        for(int i=0; i<count; i++) {
                                            TextView tv =  (TextView)linearLayout.getChildAt(i);
                                            tv.setTextColor(Color.WHITE);
                                        }
                                        finalAlertDialog.dismiss();
                                    }else{
                                        Toast.makeText(getBaseContext(), "Item not " + (idd <= 0 ? "Added!" : "Updated!") ,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                finalBtn.setEnabled(true);
//                                Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Add to Cart once only", Toast.LENGTH_SHORT).show();
                            }
                            gI = 0;
                        }
                    },500);

                }
            });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.show();
        }
        else if (hidden_title.equals("API Received from SAP") || hidden_title.equals("API System Transfer Item")  || hidden_title.equals("API Pending Issue For Production") || hidden_title.equals("API Item Request For Transfer")) {
            AlertDialog.Builder myDialog = new AlertDialog.Builder(APIReceived.this);
            myDialog.setCancelable(false);
            myDialog.setTitle("Confirmation");
//            System.out.println("ID: " + finalDocEntry);
            myDialog.setMessage("Are you sure you want to select '" + finalItem + "'?");
            myDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    if(hidden_title.equals("API Received from SAP")){
//                        insertSAPItems(finalDocEntry, finalSupplier);
//                     if(hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing")){
//                        insertIssueProduction(finalDocEntry, finalItem);
//                    }
//                    else if(hidden_title.equals("API Received from Production")){
////                        System.out.println("hidden: " + hidden_title);
//                        insertReceivedProduction(finalDocEntry, finalItem);
//                    }
                    if (hidden_title.equals("API Pending Issue For Production")) {
                        insertPendingIssueProduction(finalDocEntry, finalItem);
                    } else if (hidden_title.equals("API Item Request For Transfer")) {
//                        System.out.println("hidden: " + hidden_title);
                        insertReceivedItemRequest(finalDocEntry, finalItem);
                    } else {
                        insertSystemTransfer(finalDocEntry, finalItem);
                    }
                }
            });
            myDialog.setNegativeButton("No", (dialogInterface, i1) -> dialogInterface.dismiss());
            myDialog.show();
        }
    }

    public void insertSystemTransfer(Integer id,String referenceNumber){
        String appendURL= "/api/inv/trfr/forrec/getdetails/" + id;
        System.out.println(appendURL);
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");
        System.out.println(appendURL);
        String URL = IPAddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray;
                            System.out.println("res: " + response);
                            jsonArray = response.getJSONArray("data");
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        itemCode,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("from_whse");
                                itemCode = itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("quantity");
                                toBranch = jsonObject.getString("to_whse");
                                uom = jsonObject.getString("uom");
                                double int_quantity = jsonObject.getDouble("quantity");
                                String remarks = jsonObject.isNull("remarks") ? "" : jsonObject.getString("remarks");
                                String transDate = jsonObject.isNull("transdate") ? "" : jsonObject.getString("transdate").replace("T"," ");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,0,0,0,0,itemCode,response.toString(),"","");
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                runOnUiThread(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void run() {
                                        Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
//                                        Intent intent = new Intent(getBaseContext(), APIReceived.class);
//                                        intent.putExtra("title", title);
//                                        intent.putExtra("hiddenTitle", hidden_title);
//                                        startActivity(intent);
//                                        finish();
                                        loadData();
                                    }
                                });

                            } else {
                                Toast.makeText(APIReceived.this, "'" + id + "' not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void insertProductionOrderItems(int id, String referenceNumber){
        String appendURL= "/api/production/order/details/" + id;
//        System.out.println("URL: " +  appendURL);
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray;
                            jsonArray = response.getJSONArray("data");
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        itemCode,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("whsecode");
                                itemName = jsonObject.getString("item_name");
                                itemCode = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("planned_qty");
                                toBranch = jsonObject.getString("whsecode");
                                uom = jsonObject.getString("uom");
                                int itemID = jsonObject.getInt("id");
                                int objtype = jsonObject.getInt("objtype");
                                double int_quantity = jsonObject.getDouble("planned_qty");
                                int isClosed_int = jsonObject.isNull("close") ? 0 : jsonObject.getBoolean("close") ? 1 : 0;
                                int int_received_quantity = jsonObject.isNull("received_qty") ? 0 : jsonObject.getInt("received_qty");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,int_received_quantity,itemID,objtype,isClosed_int,itemCode,response.toString(),"","");
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(APIReceived.this, "'" + id + "' not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }


    public void insertReceivedProduction(int id, String referenceNumber){
        String appendURL= "/api/production/order/details/" + id + "?mode=receive";
//        System.out.println("URL: " +  appendURL);
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPaddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray jsonArray;
                            jsonArray = response.getJSONArray("data");
//                            System.out.println("array: " + jsonArray);
                            int countError = 0;
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        itemCode,
                                        toBranch,
                                        uom;
                                Double quantity;

                                fromBranch = jsonObject.getString("whsecode");
                                itemName = jsonObject.getString("item_name");
                                itemCode = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("planned_qty");
                                toBranch = jsonObject.getString("whsecode");
                                uom = jsonObject.getString("uom");
                                double int_quantity = jsonObject.getDouble("planned_qty");
                                int int_received_quantity = jsonObject.isNull("received_qty") ? 0 : jsonObject.getInt("received_qty");
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,int_received_quantity,0,0,0,itemCode,response.toString(),"","");
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(APIReceived.this, "'" + id + "' not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void insertReceivedItemRequest(int id, String referenceNumber){
        String appendURL= "/api/inv/item_request/for_transfer/details/" + id;
//        System.out.println("URL: " +  appendURL);
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");
        String URL = IPAddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            int countError = 0;
                            JSONArray jsonArray = response.getJSONArray("data");
                            String selectedSapNumber = referenceNumber;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String fromBranch,
                                        itemName,
                                        itemCode,
                                        toBranch,
                                        uom;
                                Double quantity;
                                int item_id = jsonObject.isNull("id") ? 0 : jsonObject.getInt("id");
                                int obj_type = jsonObject.isNull("objtype") ? 0 : jsonObject.getInt("objtype");
                                fromBranch = jsonObject.getString("from_branch");
                                itemCode = itemName = jsonObject.getString("item_code");
                                quantity = jsonObject.getDouble("quantity");
                                toBranch = jsonObject.getString("to_branch");
                                uom = jsonObject.getString("uom");
                                double int_quantity = jsonObject.getDouble("quantity");
                                int int_received_quantity = jsonObject.isNull("deliverqty") ? 0 : jsonObject.getInt("deliverqty");
                                System.out.println("objtype: " + obj_type);
                                boolean isSuccess = myDb3.insertData(referenceNumber, fromBranch, itemName, quantity, int_quantity, 0, toBranch, id,hidden_title,0,uom,int_received_quantity,item_id,obj_type,0,itemCode,response.toString(),"","");
                                if (!isSuccess) {
                                    countError += 1;
                                }
                            }

                            if (countError <= 0) {
                                Toast.makeText(APIReceived.this, "'" + selectedSapNumber + "' added", Toast.LENGTH_SHORT).show();
                                loadData();
                            } else {
                                Toast.makeText(APIReceived.this, "'" + id + "' not added", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void insertIssueProduction(int id, String reference){
        String appendURL= (hidden_title.equals("API Issue For Production") ? "/api/production/item_to_issue/get_all/" : "/api/production/issue_for_prod/pending/details/") + id + (hidden_title.equals("API Issue For Production") ? "" : "?mode=confirm");
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPAddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isSuccess = myDb9.insertData(appendURL,"GET", hidden_title, response.toString(),currentDate,id);
                            if(isSuccess){
                                Toast.makeText(getBaseContext(), reference + " added" , Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public void insertPendingIssueProduction(int id, String reference){
        String appendURL= "/api/production/issue_for_prod/pending/details/" + id;
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences2.getString("IPAddress", "");

        String URL = IPAddress + appendURL;
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            try {
                final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null, response -> {
                    try {
                        if (response.getBoolean("success")) {
                            myDb9.truncateTable();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isSuccess = myDb9.insertData(appendURL,"GET", hidden_title, response.toString(),currentDate, id);
                            if(isSuccess){
                                Toast.makeText(getBaseContext(), reference + " added" , Toast.LENGTH_SHORT).show();
                                Intent intent;
                                intent = new Intent(getBaseContext(), API_IssueProductionItems.class);
                                intent.putExtra("id", id);
                                intent.putExtra("title", title);
                                intent.putExtra("hiddenTitle", hidden_title);
                                startActivity(intent);
                            }
                        } else {
                            String msg = response.getString("message");
                            if (msg.equals("Token is invalid")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(APIReceived.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(APIReceived.this);
                                    pc.removeToken(APIReceived.this);
                                    startActivity(uic.goTo(APIReceived.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            } else {
                                Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getBaseContext(), "Connection Timeout", Toast.LENGTH_SHORT).show()) {
                    @Override
                    public Map<String, String> getHeaders() {
                        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                        Map<String, String> params = new HashMap<>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);
                        return params;
                    }
                };
                mQueue.add(request);
            }catch (Exception ex){
                ex.printStackTrace();
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }
        },500);
    }

    public String cutWord(String value, int limit){
        String result;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void navigateDone() {
        utility_class utilityc = new utility_class();
        String[] prodList = utility_class.prodList;
        String[] packingList = utility_class.packingList;
        if(hidden_title.equals("API Issue For Production") && !utilityc.isAllowProdPacking(prodList, APIReceived.this)) {
            String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
            for(String list : prodList){
                appendMsg+= "- " + list + "\n";
            }
            String title = "Validation";
            String msg =  "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
            utilityc.customAlertDialog(title,msg, APIReceived.this);
        }else if(hidden_title.equals("API Issue For Packing") && !utilityc.isAllowProdPacking(packingList, APIReceived.this)) {
            String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
            for (String list : packingList) {
                appendMsg += "- " + list + "\n";
            }
            String title = "Validation";
            String msg = "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
            utilityc.customAlertDialog(title, msg, APIReceived.this);
        }
        else{
            Intent intent;
            intent = new Intent(getBaseContext(), (hidden_title.equals("API Menu Items") ? ShoppingCart.class : API_SelectedItems.class));
            intent.putExtra("title", title);
            intent.putExtra("selectedWarehouse", gSelectedBranch);
            intent.putExtra("hiddenTitle", hidden_title);
            startActivity(intent);
            finish();
        }
    }
}