package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.example.atlanticgrains.Helper.FragmentNavigationManager_API_SalesLogsItems;
import com.example.atlanticgrains.Interface.NavigationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class API_SalesLogsItems extends AppCompatActivity {
    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();
    DatabaseHelper myDb;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;

    DecimalFormat df = new DecimalFormat("#,##0.000");

    private OkHttpClient client;
    Menu menu;
    TableLayout tableLayout;
    Button btnBack;

    String title, hidden_title;
    TextView txtReference,txtHeader;
    ProgressBar progressBar;
    Long mLastClickTime;
    View listReaderView = null, listReaderViewTemp = null;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i_sales_logs_items);

        tableLayout = findViewById(R.id.table_main);
        btnBack = findViewById(R.id.btnBack);
        txtReference = findViewById(R.id.txtReference);
        txtHeader = findViewById(R.id.txtHeader);
        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE);

        myDb = new DatabaseHelper(this);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        expandableListView = (ExpandableListView)findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_API_SalesLogsItems.getmInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
        String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));

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

        txtReference = findViewById(R.id.txtReference);
        String reference = getIntent().getStringExtra("reference");
        txtReference.setText(reference);

        title = getIntent().getStringExtra("title");
        hidden_title = getIntent().getStringExtra("hiddenTitle");
//        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        ((TextView)v.findViewById(R.id.title)).setText(R.string.app_name);
        ((TextView)v.findViewById(R.id.title2)).setText(currentDepartment + " - " + currentShift);
        this.getSupportActionBar().setCustomView(v);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String typeTrans = getIntent().getStringExtra("type");
        if(typeTrans.equals("Sales Transactions") || typeTrans.equals("Received Transactions") || typeTrans.equals("Transfer Transactions") || typeTrans.equals("Item Request Transactions")){
            txtHeader.setVisibility(View.VISIBLE);
        }else{
            txtHeader.setVisibility(View.GONE);
        }

        loadData();
    }

    public String getFromSharedPref(String name, String key){
        SharedPreferences sharedPreferences2 = getSharedPreferences(name, MODE_PRIVATE);
        return sharedPreferences2.getString(key, "");
    }

    private class showAssignedDepartment extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SalesLogsItems.this);
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
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SalesLogsItems.this);
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
                ArrayAdapter adapter = new ArrayAdapter(API_SalesLogsItems.this,android.R.layout.simple_list_item_1, arrayList){
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
                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SalesLogsItems.this);
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
        LoadingDialog loadingDialog = new LoadingDialog(API_SalesLogsItems.this);
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

                        LayoutInflater inflator = LayoutInflater.from(getBaseContext());
                        View v = inflator.inflate(R.layout.custom_action_bar, null);
                        ((TextView)v.findViewById(R.id.title)).setText(R.string.app_name);
                        ((TextView)v.findViewById(R.id.title2)).setText(branch + " - " + currentShift);
                        getSupportActionBar().setCustomView(v);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
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
            String firstItem = listTitle.get(0);
            navigationManager.showFragment(firstItem);
            getSupportActionBar().setTitle(firstItem);
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
                else if(selectedItem.equals("Issue For Production")) {
                    utility_class utilityc = new utility_class();
                    String[] prodList = utility_class.prodList;
                    if(utilityc.isAllowProdPacking(prodList, API_SalesLogsItems.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Production");
                        intent.putExtra("hiddenTitle", "API Issue For Production");
                        startActivity(intent);
                    }else{
                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                        for(String list : prodList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, API_SalesLogsItems.this);
                    }
                }
                else if(selectedItem.equals("Issue For Packing")) {
                    utility_class utilityc = new utility_class();
                    String[] packingList = utility_class.packingList;
                    if(utilityc.isAllowProdPacking(packingList, API_SalesLogsItems.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Packing");
                        intent.putExtra("hiddenTitle", "API Issue For Packing");
                        startActivity(intent);
                    }else{
                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                        for(String list : packingList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, API_SalesLogsItems.this);
                    }
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

    private class showShift extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SalesLogsItems.this);
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
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SalesLogsItems.this);
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
                            ArrayAdapter adapter = new ArrayAdapter(API_SalesLogsItems.this,android.R.layout.simple_list_item_1, arrayList);
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
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SalesLogsItems.this);
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
                                                ((TextView)vv.findViewById(R.id.title)).setText(R.string.app_name);
                                                ((TextView)vv.findViewById(R.id.title2)).setText(currentDepartment + " - " + currentShift);
                                                getSupportActionBar().setCustomView(vv);

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
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
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
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
        int id = item.getItemId();
        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(API_SalesLogsItems.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    API_SalesLogsItems.myChangePassword myChangePassword = new API_SalesLogsItems.myChangePassword(txtPassword.getText().toString().trim());
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
        LoadingDialog loadingDialog = new LoadingDialog(API_SalesLogsItems.this);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(API_SalesLogsItems.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(API_SalesLogsItems.this);
                                        pc.removeToken(API_SalesLogsItems.this);
                                        startActivity(uic.goTo(API_SalesLogsItems.this, MainActivity.class));
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


    public void loadData() {
        tableLayout.removeAllViews();
        TableRow tableColumn = new TableRow(API_SalesLogsItems.this);
        String[] columns;
        String typeTrans = getIntent().getStringExtra("type");
        if(typeTrans.equals("Received Transactions")) {
            columns = new String[]{"Item", "Qty.", "Actual Qty.", "Variance", "Uom"};
        }else if(typeTrans.equals("Transfer Transactions") || typeTrans.equals("Item Request Transactions")){
            columns = new String[]{"Item", "Qty.", "Uom"};
        }
        else{
            columns = new String[]{"Item", "Qty.", "Price", "Gross", "Disc. %", "Disc. Amt.", "Total"};
        }
        for (String s : columns) {
            TextView lblColumn1 = new TextView(API_SalesLogsItems.this);
            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
            lblColumn1.setText(s);
            lblColumn1.setPadding(10, 0, 10, 0);
            lblColumn1.setTextSize(20);
            lblColumn1.setTextColor(Color.BLACK);
            tableColumn.addView(lblColumn1);
        }
        tableLayout.addView(tableColumn);

        MyData myData = new MyData();
        myData.execute();

    }

    private class MyData extends AsyncTask<String, Void, String> {

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

                SharedPreferences sharedPreferences0 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = sharedPreferences0.getString("token", "");

                String URL = getIntent().getStringExtra("URL");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPAddress +  URL)
                        .method("GET", null)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = null;
                System.out.println("URL Details: " + URL);
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                if(s != null) {
                    progressBar.setVisibility(View.GONE);
                    String answer = s;
                    formatResponse(answer);
                }
            } catch (Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    public void formatResponse(String temp){
        System.out.println("temp: " + temp);
        if(!temp.isEmpty() && temp.substring(0,1).equals("{")){
            try{
                String typeTrans = getIntent().getStringExtra("type");
                JSONObject jsonObject1 = new JSONObject(temp);
                if(jsonObject1.getBoolean("success")){
                    JSONObject jsonObject = jsonObject1.getJSONObject("data");

                    String headerFormat = "";
                    if(typeTrans.equals("Sales Transactions")) {
                        headerFormat = "Customer Code: " + jsonObject.getString("cust_code") + "\n" +
                                "Remarks: " + (jsonObject.isNull("remarks") ? "" : jsonObject.getString("remarks")) + "\n" +
                                "Trans. Type: " + jsonObject.getString("transtype") + "\n" +
                                "Gross: " + jsonObject.getDouble("gross") + "\n" +
                                "Discount Amount: " + jsonObject.getDouble("disc_amount") + "\n" +
                                "Document Total: " + jsonObject.getDouble("doctotal") + "\n" +
                                "Applied Amount: " + jsonObject.getDouble("appliedamt") + "\n" +
                                "Tender Amount: " + jsonObject.getDouble("tenderamt") + "\n" +
                                "Amount Due: " + jsonObject.getDouble("amount_due") + "\n";
                    }else if(typeTrans.equals("Received Transactions") || typeTrans.equals("Transfer Transactions")) {
                        String docStatus = jsonObject.getString("docstatus"),
                                docStatusDecode = docStatus.equals("O") ? "Open" : docStatus.equals("C") ? "Closed" : "Cancelled";
                        headerFormat = "Transaction Date: " + jsonObject.getString("transdate").replace("T", " ") + "\n" +
                                "Remarks: " + jsonObject.getString("remarks") + "\n" +
                                "Document Status: " + docStatusDecode+ "\n";
                    }else{
                        System.out.println("are you there?");
                        String docStatus = jsonObject.getString("docstatus"),
                                docStatusDecode = docStatus.equals("O") ? "Open" : docStatus.equals("C") ? "Closed" : "Cancelled";
                        headerFormat = "Transaction Date: " + jsonObject.getString("transdate").replace("T", " ") + "\n" +
                                "Remarks: " + jsonObject.getString("remarks") + "\n" +
                                "Document Status: " + docStatusDecode+ "\n";
                    }
                    String finalHeaderFormat1 = headerFormat;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtHeader.setText(finalHeaderFormat1);
                        }
                    });

                    String srow;
                    if(typeTrans.equals("Received Transactions")){
                        srow = "recrow";
                    }
                    else if(typeTrans.equals("Transfer Transactions")){
                        srow = "transrow";
                    }
                    else if(typeTrans.equals("Item Request Transactions")){
                        srow = "request_rows";
                    }
                    else{
                        srow = "salesrow";
                    }
                    JSONArray jsonArrayRecRow = jsonObject.getJSONArray(srow);
                    for (int i = 0; i < jsonArrayRecRow.length(); i++) {
                        JSONObject jsonObjectRecRow = jsonArrayRecRow.getJSONObject(i);

                        final TableRow tableRow = new TableRow(getBaseContext());
                        tableRow.setBackgroundColor(Color.WHITE);
                        LinearLayout linearLayoutItem = new LinearLayout(this);
                        linearLayoutItem.setPadding(10, 10, 10, 10);
                        linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                        linearLayoutItem.setBackgroundColor(Color.WHITE);
                        linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        tableRow.addView(linearLayoutItem);

                        LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                        TextView lblColumn1 = new TextView(this);
                        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn1.setLayoutParams(layoutParamsItem);
//                       String v = cutWord(item);
                        lblColumn1.setText(jsonObjectRecRow.getString("item_code"));
                        lblColumn1.setTextSize(15);
                        lblColumn1.setTextColor(Color.BLACK);
                        lblColumn1.setBackgroundColor(Color.WHITE);
                        linearLayoutItem.addView(lblColumn1);

                        TextView lblColumn2 = new TextView(getBaseContext());
                        lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn2.setText(df.format(jsonObjectRecRow.getDouble("quantity")));
                        lblColumn2.setTextSize(15);
                        lblColumn2.setTextColor(Color.BLACK);
                        lblColumn2.setBackgroundColor(Color.WHITE);
                        lblColumn2.setPadding(10, 10, 10, 10);
                        tableRow.addView(lblColumn2);

                        TextView lblColumn3 = new TextView(getBaseContext());
                        lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);

                        double col3 = 0.00, col4 = 0.00,col5 = 0.00, col6 = 0.00, col7 = 0.00;
                        if(typeTrans.equals("Received Transactions")) {
                            col3 = jsonObjectRecRow.getDouble("actualrec");
                            col4 = jsonObjectRecRow.getDouble("quantity") - col3;
                        }else if(typeTrans.equals("Sales Transactions")){
                            col3 = jsonObjectRecRow.getDouble("unit_price");
                            col4 = jsonObjectRecRow.getDouble("gross");
                            col5 = jsonObjectRecRow.getDouble("disc_amount");
                            col6 = jsonObjectRecRow.getDouble("discprcnt");
                            col7 = jsonObjectRecRow.getDouble("linetotal");
                        }

                        if(typeTrans.equals("Received Transactions") || typeTrans.equals("Sales Transactions")){
                            lblColumn3.setText(df.format(col3));
                            lblColumn3.setBackgroundColor(Color.WHITE);
                            lblColumn3.setPadding(10, 10, 10, 10);
                            lblColumn3.setTextSize(15);
                            lblColumn3.setTextColor(Color.BLACK);
                            tableRow.addView(lblColumn3);

                            TextView lblColumn4 = new TextView(getBaseContext());
                            lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn4.setText(df.format(col4));
                            lblColumn4.setTextSize(15);
                            lblColumn4.setTextColor(Color.BLACK);
                            lblColumn4.setBackgroundColor(Color.WHITE);
                            lblColumn4.setPadding(10, 10, 10, 10);

                            if(typeTrans.equals("Received Transactions")) {
                                if(col4 == 0.00){
//                                    lblColumn4.setTextColor(Color.BLACK);
                                }
                                if (col4 > 0) {
                                    lblColumn4.setTextColor(Color.BLUE);
                                } else if (col4 < 0) {
                                    lblColumn4.setTextColor(Color.RED);
                                }
                            }
                            tableRow.addView(lblColumn4);

                            if(typeTrans.equals("Received Transactions")){
                                TextView lblColumn8 = new TextView(getBaseContext());
                                lblColumn8.setText(jsonObjectRecRow.getString("inv_uom"));
                                lblColumn8.setBackgroundColor(Color.WHITE);
                                lblColumn8.setPadding(10, 10, 10, 10);
                                lblColumn8.setTextSize(15);
                                lblColumn8.setTextColor(Color.BLACK);
                                tableRow.addView(lblColumn8);
                            }
                        }

                        if(typeTrans.equals("Sales Transactions")){
                            TextView lblColumn4 = new TextView(getBaseContext());
                            lblColumn4.setText(String.valueOf(col5));
                            lblColumn4.setTextSize(15);
                            lblColumn4.setTextColor(Color.BLACK);
                            lblColumn4.setBackgroundColor(Color.WHITE);
                            lblColumn4.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn4);

                            TextView lblColumn5 = new TextView(getBaseContext());
                            lblColumn5.setText(String.valueOf(col6));
                            lblColumn5.setTextSize(15);
                            lblColumn5.setTextColor(Color.BLACK);
                            lblColumn5.setBackgroundColor(Color.WHITE);
                            lblColumn5.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn5);

                            TextView lblColumn6 = new TextView(getBaseContext());
                            lblColumn6.setText(String.valueOf(col7));
                            lblColumn6.setTextSize(15);
                            lblColumn6.setTextColor(Color.BLACK);
                            lblColumn6.setBackgroundColor(Color.WHITE);
                            lblColumn6.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn6);
                        }

                        if(typeTrans.equals("Transfer Transactions")){
                            lblColumn3.setText(jsonObjectRecRow.getString("inv_uom"));
                            lblColumn3.setBackgroundColor(Color.WHITE);
                            lblColumn3.setPadding(10, 10, 10, 10);
                            lblColumn3.setTextSize(15);
                            lblColumn3.setTextColor(Color.BLACK);
                            tableRow.addView(lblColumn3);
                        }

                        if(typeTrans.equals("Item Request Transactions")){
                            TextView lblColumn8 = new TextView(getBaseContext());
                            lblColumn8.setText(jsonObjectRecRow.getString("uom"));
                            lblColumn8.setBackgroundColor(Color.WHITE);
                            lblColumn8.setPadding(10, 10, 10, 10);
                            lblColumn8.setTextSize(15);
                            lblColumn8.setTextColor(Color.BLACK);
                            tableRow.addView(lblColumn8);
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tableLayout.addView(tableRow);
                                View viewLine = new View(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                                viewLine.setLayoutParams(layoutParamsLine);
                                viewLine.setBackgroundColor(Color.GRAY);
                                tableLayout.addView(viewLine);
                            }
                        });

                    }
                }else{
                    String msg = jsonObject1.getString("message");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Error: \n" + msg, Toast.LENGTH_SHORT).show();
                        }
                    });
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
        }else{
            Runnable r = () -> {
                Toast.makeText(getBaseContext(), temp, Toast.LENGTH_SHORT).show();
            };
        }
    }

//    @Override
//    public void onBackPressed() {
//        Intent a = new Intent(Intent.ACTION_MAIN);
//        a.addCategory(Intent.CATEGORY_HOME);
//        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(a);
//    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    public  void onBtnLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pc.loggedOut(API_SalesLogsItems.this);
                        pc.removeToken(API_SalesLogsItems.this);
                        startActivity(uic.goTo(API_SalesLogsItems.this, MainActivity.class));
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

}
