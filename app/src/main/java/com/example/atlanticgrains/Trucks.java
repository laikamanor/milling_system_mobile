package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.atlanticgrains.Adapter.CustomExpandableListAdapter;
import com.example.atlanticgrains.Helper.FragmentNavigationManager;
import com.example.atlanticgrains.Interface.NavigationManager;
import com.example.atlanticgrains.Helper.FragmentNavigationManager_Trucks;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
public class Trucks extends AppCompatActivity {
    private String mActivityTitle;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();

    ListView listView;
    View listReaderView = null, listReaderViewTemp = null;
    Button btnAdd;
    private OkHttpClient client;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trucks);

        client = new OkHttpClient();

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        expandableListView = (ExpandableListView)findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_Trucks.getmInstance(this);

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        listView = findViewById(R.id.listview1);
        btnAdd = findViewById(R.id.btnAdd);
        requestData();
    }


    public String getFromSharedPref(String name, String key){
        SharedPreferences sharedPreferences2 = getSharedPreferences(name, MODE_PRIVATE);
        return sharedPreferences2.getString(key, "");
    }

    private class showAssignedDepartment extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(Trucks.this);
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
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Trucks.this);
                dialog.setTitle("Select Department");
                dialog.setCancelable(false);

                LinearLayout layout = new LinearLayout(getBaseContext());
                layout.setPadding(20, 20, 20, 10);
                layout.setOrientation(LinearLayout.VERTICAL);

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
                ArrayAdapter adapter = new ArrayAdapter(Trucks.this,android.R.layout.simple_list_item_1, arrayList);
                listView.setAdapter(adapter);

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
                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Trucks.this);
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
        LoadingDialog loadingDialog = new LoadingDialog(Trucks.this);
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
//                        boolean isSuccess = joResult.has("success") && joResult.getBoolean("success");
                        String msg = joResult.has("message") ? !joResult.isNull("message") ? joResult.getString("message") : "" : "";
                        String branch = !joData.has("branch") ? "" : joData.isNull("branch") ? "" : joData.getString("branch");
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN",MODE_PRIVATE);
                        String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("branch",branch).apply();

                        expandableListView.removeHeaderView(listReaderView);
                        expandableListView.removeHeaderView(listReaderViewTemp);
                        listReaderViewTemp   = getLayoutInflater().inflate(R.layout.nav_header, null,false);
                        expandableListView.removeHeaderView(listReaderView);
                        TextView txtName = listReaderViewTemp.findViewById(R.id.txtName);
                        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));
                        txtName.setText("Name: " +fullName + "\nDept: " + branch + "\nShift: " + currentShift + "\nVersion: v" + BuildConfig.VERSION_NAME);
                        expandableListView.addHeaderView(listReaderViewTemp);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
                        builder.setCancelable(false);
                        builder.setTitle("Message");
                        builder.setMessage(msg);
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
            navigationManager.showFragment(getString(R.string.app_name));
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    public void addDrawersItem(){
        adapter = new CustomExpandableListAdapter(this, listTitle, listChild);
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
                else if(selectedItem.equals("Issue For Production/Packing")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Issue For Production/Packing");
                    intent.putExtra("hiddenTitle", "API Issue For Production");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Pending Issue For Production/Packing")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pending Issue For Production/Packing");
                    intent.putExtra("hiddenTitle", "API Pending Issue For Production");
                    startActivity(intent);
                    finish();
                }
                else if(selectedItem.equals("Confirm Issue For Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Confirm Issue For Production");
                    intent.putExtra("hiddenTitle", "API Confirm Issue For Production");
                    startActivity(intent);
                }
                else if(selectedItem.equals("Received from Production")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Received from Production");
                    intent.putExtra("hiddenTitle", "API Received from Production");
                    startActivity(intent);
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
                    startActivity(intent);
                }
                else if(selectedItem.equals("Pull out Request")) {
                    intent = new Intent(getBaseContext(), APIReceived.class);
                    intent.putExtra("title", "Pull Out Request");
                    intent.putExtra("hiddenTitle", "API Pull Out Count");
                    startActivity(intent);
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
                else if(selectedItem.equals("Trucks")){
                    intent = new Intent(getBaseContext(), Trucks.class);
                    intent.putExtra("title", "Trucks");
                    intent.putExtra("hiddenTitle", "API Trucks");
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

    private class showShift extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(Trucks.this);
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
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Trucks.this);
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
                            ArrayAdapter adapter = new ArrayAdapter(Trucks.this,android.R.layout.simple_list_item_1, arrayList);
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
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(Trucks.this);
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

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
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
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
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

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(Trucks.this);
                        pc.removeToken(Trucks.this);
                        startActivity(uic.goTo(Trucks.this, MainActivity.class));
                        finish();
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
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
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

    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(Trucks.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Trucks.myChangePassword myChangePassword = new Trucks.myChangePassword(txtPassword.getText().toString().trim());
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
        String password = "";
        LoadingDialog loadingDialog = new LoadingDialog(Trucks.this);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(Trucks.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(Trucks.this);
                                        pc.removeToken(Trucks.this);
                                        startActivity(uic.goTo(Trucks.this, MainActivity.class));
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                });
            }
        }
    }

    public void getRequestData(List<String> resultData){
        ArrayAdapter adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                resultData
        );
        listView.setAdapter(adapter);
    }

    public void requestData(){
        SharedPreferences sharedPreferences0 = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = sharedPreferences0.getString("token", "");

        SharedPreferences sharedPreferences3 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPAddress = sharedPreferences3.getString("IPAddress", "");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(IPAddress + "/api/trucks/get_all")
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .method("GET", null)
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
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                Trucks.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Handler handler = new Handler();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                synchronized (this) {
                                    try {
                                        wait(10);
                                    } catch (InterruptedException ignored) {

                                    }
                                    handler.post(() -> {
                                        try {
                                            assert response.body() != null;
                                            String result = response.body().string();
                                            System.out.println("fuck : " + result);
                                            JSONObject jsonObject1 = new JSONObject(result);
                                            if (response.isSuccessful()) {
                                                if (jsonObject1.getBoolean("success")) {
                                                    List<String> resultData = new ArrayList<String>();
                                                    JSONArray jsonArrayData = jsonObject1.getJSONArray("data");
                                                    for (int i = 0; i < jsonArrayData.length(); i++){
                                                        JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                                                        resultData.add("Plate Number: \t\t\t" + jsonObjectData.getString("plate_num") + "\n"
                                                                + "Truck Model: \t\t\t\t" + jsonObjectData.getString("truck_model") + "\n" +
                                                                "Truck Type: \t\t\t\t\t" + jsonObjectData.getString("truck_type")
                                                        );
                                                    }
                                                    getRequestData(resultData);
                                                }else {
                                                    String msg = jsonObject1.getString("message");
                                                    Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getBaseContext(), "Error \n" + jsonObject1.getString("message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception ex) {
                                            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                                            ex.printStackTrace();
                                        }
                                    });
                                }
                            }
                        };
                        Thread thread = new Thread(runnable);
                        thread.start();
                    }
                });
            }
        });
    }


}