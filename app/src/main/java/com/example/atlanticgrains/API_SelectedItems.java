 package com.example.atlanticgrains;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.atlanticgrains.Adapter.CustomExpandableListAdapter;
import com.example.atlanticgrains.Interface.NavigationManager;
import com.example.atlanticgrains.Helper.FragmentNavigationManager_API_SelectedItems;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.common.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

 public class API_SelectedItems extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    DatabaseHelper4 myDb4;
    DatabaseHelper3 myDb3;
    DatabaseHelper7 myDb7;
    DatabaseHelper8 myDb8;
    long mLastClickTime = 0;
    Button btnProceed,btnBack;
    DecimalFormat df = new DecimalFormat("#,##0.000");
    String title,hiddenTitle;
    private OkHttpClient client;
    private RequestQueue mQueue;

    prefs_class pc = new prefs_class();
    ui_class uic = new ui_class();
    navigation_class navc = new navigation_class();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
//    private String[] items;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> listTitle;
    private Map<String, List<String>> listChild;
    private NavigationManager navigationManager;
    View listReaderView = null, listReaderViewTemp = null;

    DatabaseHelper myDb;
    TextView lblGDate;

    Menu menu;

    String gBranch = "",gPlateNum = "",gBranchh = "",gMill = "",gFGItem = "";
    int gFGUOMID = 0;
    TextView lblFGUOM;
    View gView;
    int gI = 0;
    static Activity act;
    String gValue = "";
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"WrongConstant", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_i__selected_items);
        myDb = new DatabaseHelper(this);
        myDb8 = new DatabaseHelper8(this);
        client = new OkHttpClient();
        myDb4 = new DatabaseHelper4(this);
        myDb3 = new DatabaseHelper3(this);
        myDb7 = new DatabaseHelper7(this);
        btnProceed = findViewById(R.id.btnProceed);
        btnBack = findViewById(R.id.btnBack);
        mQueue = Volley.newRequestQueue(this);
        hiddenTitle = getIntent().getStringExtra("hiddenTitle");
        if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
            try {
                myWarehouse myWarehouse = new myWarehouse(false, true, "Warehouse", false, null);
                if (myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING) {
                    myWarehouse.execute("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item")) {
            try {
                myWarehouse myWarehouse = new myWarehouse(false, true, "Warehouse", false, null);
                if (myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING) {
                    myWarehouse.execute("");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (hiddenTitle.equals("API Item Request")) {
            try {
                //gBranchh = loadWarehouse(false, true,"Branch");
                myWarehouse myWarehouse = new myWarehouse(false, true, "Branch", false, null);
                if (myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING) {
                    myWarehouse.execute("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
            try {
                myWarehouse myWarehouse = new myWarehouse(false, true, "Mill", false, null);
                if (myWarehouse.getStatus() == AsyncTask.Status.FINISHED || myWarehouse.getStatus() == AsyncTask.Status.PENDING) {
                    myWarehouse.execute("");
                }
                myWarehouse myWarehouse2 = new myWarehouse(false, true, "Warehouse", false, null);
                if (myWarehouse2.getStatus() == AsyncTask.Status.FINISHED || myWarehouse2.getStatus() == AsyncTask.Status.PENDING) {
                    myWarehouse2.execute("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        expandableListView = (ExpandableListView) findViewById(R.id.navList);
        navigationManager = FragmentNavigationManager_API_SelectedItems.getmInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
        String fullName = Objects.requireNonNull(sharedPreferences.getString("fullname", ""));
        String currentShift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));

        title = getIntent().getStringExtra("title");

        listReaderView = getLayoutInflater().inflate(R.layout.nav_header, null, false);
        TextView txtName = listReaderView.findViewById(R.id.txtName);
        txtName.setText("Name: " + fullName + "\nDept: " + currentDepartment + "\nShift: " + currentShift + "\nVersion: v" + BuildConfig.VERSION_NAME);
        expandableListView.addHeaderView(listReaderView);

        this.getSupportActionBar().setDisplayShowCustomEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater inflator = LayoutInflater.from(getBaseContext());
        View v = inflator.inflate(R.layout.custom_action_bar, null);
        ((TextView)v.findViewById(R.id.title)).setText(title);
        ((TextView)v.findViewById(R.id.title2)).setText(currentDepartment + " - " + currentShift);
        getSupportActionBar().setCustomView(v);

        genData();
        addDrawersItem();
        setupDrawer();

        if (savedInstanceState == null) {
            selectFirstItemDefault();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        Objects.requireNonNull(getSupportActionBar()).setTitle(Html.fromHtml("<font color='#ffffff'>" + title + " </font>"));
        loadItems();
        btnProceed.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            String rndmString = randomString();
            System.out.println("hashed id " + rndmString);
            gI++;
            btnProceed.setEnabled(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (gI == 1) {
                        utility_class utilityc = new utility_class();
                        String[] prodList = utility_class.prodList;
                        String[] packingList = utility_class.packingList;
                        if (hiddenTitle.equals("API Issue For Production") && !utilityc.isAllowProdPacking(prodList, API_SelectedItems.this)) {
                            String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                            for (String list : prodList) {
                                appendMsg += "- " + list + "\n";
                            }
                            String title = "Validation";
                            String msg = "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                            utilityc.customAlertDialog(title, msg, API_SelectedItems.this);
                            return;
                        } else if (hiddenTitle.equals("API Issue For Packing") && !utilityc.isAllowProdPacking(packingList, API_SelectedItems.this)) {
                            String appendMsg = "Please Read\n\nThese are the list of departments that are allowed to transact: \n";
                            for (String list : packingList) {
                                appendMsg += "- " + list + "\n";
                            }
                            String title = "Validation";
                            String msg = "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                            utilityc.customAlertDialog(title, msg, API_SelectedItems.this);
                            return;
                        }
                        String shift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                        if (hiddenTitle.equals("API Issue For Production")) {
                            TextView txtMill = null;
                            TextInputLayout txtRemarks = null;
                            String sMill = "";
                            String sRemarks = "";
                            if (gView != null) {
                                txtMill = gView.findViewById(R.id.lblSelectedMill);

                                txtRemarks = gView.findViewById(R.id.txtRemarks);

                                sMill = txtMill.getText().toString();
                                sRemarks = txtRemarks.getEditText().getText().toString();
                            }
                            if (sMill.trim().isEmpty() || sMill.trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Mill field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (sRemarks.trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                String finalSMill = sMill;
                                String finalSRemarks = sRemarks;
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);
                                        Production production = new Production(finalSMill, finalSRemarks, shift, show, "", "", 0, rndmString);
                                        production.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else if (hiddenTitle.equals("API Received from Production")) {
                            TextView txtMill = null;
                            TextInputLayout txtRemarks = null;
                            String sMill = "";
                            String sRemarks = "";
                            if (gView != null) {
                                txtMill = gView.findViewById(R.id.lblSelectedMill);

                                txtRemarks = gView.findViewById(R.id.txtRemarks);

                                sMill = txtMill.getText().toString();
                                sRemarks = txtRemarks.getEditText().getText().toString();
                            }
                            if (sMill.trim().isEmpty() || sMill.trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Mill field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (sRemarks.trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                String finalSMill = sMill;
                                String finalSRemarks = sRemarks;
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);
                                        Production production = new Production(finalSMill, finalSRemarks, shift, show, "", "", 0, rndmString);
                                        production.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else if (hiddenTitle.equals("API Issue For Packing")) {
                            TextView txtMill = null;
                            TextInputLayout txtRemarks = null;
                            String sMill = "";
                            String sRemarks = "";
                            if (gView != null) {
                                txtMill = gView.findViewById(R.id.lblSelectedMill);

                                txtRemarks = gView.findViewById(R.id.txtRemarks);

                                sMill = txtMill.getText().toString();
                                sRemarks = txtRemarks.getEditText().getText().toString();
                            }
                            TextView txtFGQty = gView.findViewById(R.id.txtFGQuantity);
                            TextView lblFGItem = gView.findViewById(R.id.lblSelectedFGItem);
                            String sFGItem = lblFGItem.getText().toString();
                            String sFGUOM = lblFGUOM.getText().toString();
                            double fgQuantity = 0;
                            try {
                                fgQuantity = Double.parseDouble(txtFGQty.getText().toString());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (sMill.trim().isEmpty() || sMill.trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Mill Warehouse field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (sFGItem.trim().isEmpty() || sFGItem.trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "FG Item field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (fgQuantity <= 0) {
                                Toast.makeText(getBaseContext(), "FG Quantity field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (sRemarks.trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                double finalFgQuantity = fgQuantity;
                                String finalSMill1 = sMill;
                                String finalSRemarks1 = sRemarks;
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);
                                        Production production = new Production(finalSMill1, finalSRemarks1, shift, show, sFGItem, sFGUOM, finalFgQuantity, rndmString);
                                        production.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else if (hiddenTitle.equals("API Received Item")) {
                            TextView txtFromWhse = null, txtToWhse = null, txtTruck = null, txtVessel = null, txtDriver = null;
                            TextInputLayout txtRemarkss = null, txtCHTITS = null, txtAGITS = null;
                            String sRemarks = "";
                            if (gView != null) {
                                txtFromWhse = gView.findViewById(R.id.lblSelectedFromWhse);
                                txtToWhse = gView.findViewById(R.id.lblSelectedToWhse);
                                txtTruck = gView.findViewById(R.id.lblSelectedTruck);
                                txtVessel = gView.findViewById(R.id.lblSelectedVessel);
                                txtDriver = gView.findViewById(R.id.lblSelectedDriver);
                                txtRemarkss = gView.findViewById(R.id.txtRemarks);
                                txtCHTITS = gView.findViewById(R.id.txtCHTITS);
                                txtAGITS = gView.findViewById(R.id.txtAGITS);
                                if (txtFromWhse.getText().toString().trim().isEmpty() || txtFromWhse.getText().toString().trim().equals("N/A")) {
                                    Toast.makeText(getBaseContext(), "From Warehouse field is empty!", Toast.LENGTH_SHORT).show();
                                    btnProceed.setEnabled(true);
                                } else if (txtToWhse.getText().toString().trim().isEmpty() || txtToWhse.getText().toString().trim().equals("N/A")) {
                                    Toast.makeText(getBaseContext(), "To Warehouse field is empty!", Toast.LENGTH_SHORT).show();
                                    btnProceed.setEnabled(true);
                                } else if (txtRemarkss.getEditText().getText().toString().trim().isEmpty() || txtRemarkss.getEditText().getText().toString().trim().equals("N/A")) {
                                    Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                    btnProceed.setEnabled(true);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    builder.setMessage("Are you sure want to submit?")
                                            .setCancelable(false);
                                    builder.setPositiveButton("OK", null);
                                    builder.setNegativeButton("Cancel", null);
                                    AlertDialog show = builder.show();
                                    Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    TextView finalTxtFromWhse = txtFromWhse;
                                    TextView finalTxtToWhse = txtToWhse;
                                    TextInputLayout finalTxtRemarks = txtRemarkss;
                                    TextView finalTxtTruck = txtTruck;
                                    TextInputLayout finalTxtAGITS = txtAGITS;
                                    TextInputLayout finalTxtCHTITS = txtCHTITS;
                                    TextView finalTxtDriver = txtDriver;
                                    TextView finalTxtVessel = txtVessel;
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            btnProceed.setEnabled(false);
                                            String currentWhse = Objects.requireNonNull(sharedPreferences.getString("whse", ""));
                                            String whseCode = finalTxtFromWhse.getText().toString().trim().equals("N/A") || finalTxtFromWhse.getText().toString().trim().isEmpty() ? currentWhse : finalTxtFromWhse.getText().toString().trim();
                                            ManualReceived manualReceived = new ManualReceived(whseCode.trim(), finalTxtToWhse.getText().toString().trim(), finalTxtRemarks.getEditText().getText().toString(), "", finalTxtTruck.getText().toString().trim(), finalTxtVessel.getText().toString().trim(), finalTxtDriver.getText().toString().trim(), shift, finalTxtAGITS.getEditText().getText().toString().trim(), finalTxtCHTITS.getEditText().getText().toString().trim(), show, rndmString.trim());
                                            manualReceived.execute("");
                                        }
                                    });
                                    btn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            show.dismiss();
                                            btnProceed.setEnabled(true);
                                        }
                                    });
                                }

                            }
                        } else if (hiddenTitle.equals("API System Transfer Item")) {
                            TextInputLayout txtRemarkss = gView.findViewById(R.id.txtRemarks);
                            if (txtRemarkss.getEditText().getText().toString().trim().isEmpty()) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                String supplier = "";
                                Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
                                if (cursor != null) {
                                    if (cursor.moveToNext()) {
                                        supplier = cursor.getString(2);
                                    }
                                }
                                try {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    builder.setMessage("Are you sure want to submit?")
                                            .setCancelable(false);
                                    builder.setPositiveButton("OK", null);
                                    builder.setNegativeButton("Cancel", null);
                                    AlertDialog show = builder.show();
                                    Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    String finalSupplier = supplier;
                                    btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            show.dismiss();
                                            btnProceed.setEnabled(true);
                                            try {
                                                apiSaveDataRec(finalSupplier, txtRemarkss.getEditText().getText().toString(), show, rndmString);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    btn2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            show.dismiss();
                                            btnProceed.setEnabled(true);
                                        }
                                    });
                                } catch (Exception e) {
                                    btnProceed.setEnabled(true);
                                    e.printStackTrace();
                                }
                            }
                        } else if (hiddenTitle.equals("API Transfer Item")) {
                            TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
                            TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
                            TextView lblSelectedFGItem = gView.findViewById(R.id.lblSelectedFGItem);
                            TextView lblSelectedStartDate = gView.findViewById(R.id.lblSelectedStartDate);
                            TextView lblSelectedEndDate = gView.findViewById(R.id.lblSelectedEndDate);
                            TextView lblSelectedStartTime = gView.findViewById(R.id.lblSelectedStartTime);
                            TextView lblSelectedEndTime = gView.findViewById(R.id.lblSelectedEndTime);
                            TextInputLayout txtCHTITS = gView.findViewById(R.id.txtCHTITS);
                            TextInputLayout txtAGITS = gView.findViewById(R.id.txtAGITS);
                            TextInputLayout txtTemperingHour = gView.findViewById(R.id.txtTemperingHour);
                            TextInputLayout txtRemarkss = gView.findViewById(R.id.txtRemarks);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                            Date dd = null, dd1 = null;
                            String startDate = "", endDate = "";
                            try {
                                dd = format.parse(lblSelectedStartDate.getText().toString() + " " + lblSelectedStartTime.getText().toString());
                                dd1 = format.parse(lblSelectedEndDate.getText().toString() + " " + lblSelectedEndTime.getText().toString());
                                startDate = format.format(dd);
                                endDate = format.format(dd1);
                            } catch (Exception ex) {
                                btnProceed.setEnabled(true);
                                ex.printStackTrace();
                            }
                            if (lblSelectedStartDate.getText().toString().equals("---- -- --") && !lblSelectedStartTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer Start Date", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (!lblSelectedStartDate.getText().toString().equals("---- -- --") && lblSelectedStartTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer Start Time", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (lblSelectedEndDate.getText().toString().equals("---- -- --") && !lblSelectedEndTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer End Date", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (!lblSelectedEndDate.getText().toString().equals("---- -- --") && lblSelectedEndTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer End Time", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (lblSelectedStartDate.getText().toString().equals("---- -- --") && lblSelectedStartTime.getText().toString().equals("--:--") && !lblSelectedEndDate.getText().toString().equals("---- -- --") && !lblSelectedEndTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer Start Date", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (!lblSelectedStartDate.getText().toString().equals("---- -- --") && !lblSelectedStartTime.getText().toString().equals("--:--") && lblSelectedEndDate.getText().toString().equals("---- -- --") && lblSelectedEndTime.getText().toString().equals("--:--")) {
                                Toast.makeText(getBaseContext(), "Please select Transfer End Date", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (txtRemarkss.getEditText().getText().toString().trim().isEmpty() || txtRemarkss.getEditText().getText().toString().trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                String finalStartDate = startDate;
                                String finalEndDate = endDate;
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);

                                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                        String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                                        String fg_item = "", fg_uom  = "";
                                        final boolean flour_bins = currentDepartment.equals("FLOUR BINS") || currentDepartment.equals("CC-FLOUR BINS") || currentDepartment.equals("BRAN/POLLARD PACKING BINS") || currentDepartment.equals("CC-BRAN/POLLARD PACKING BINS") || currentDepartment.equals("BRAN/POLLARD BINS") || currentDepartment.equals("CC-BRAN/POLLARD BINS");
                                        if(lblSelectedFGItem.getVisibility() == VISIBLE && flour_bins){
                                            fg_item = lblSelectedFGItem.getText().toString();
                                        }
                                        if(lblFGUOM.getVisibility() == VISIBLE && flour_bins){
                                            fg_uom = lblFGUOM.getText().toString();
                                        }

                                        TransferItem transferItem = new TransferItem(txtRemarkss.getEditText().getText().toString(), txtTruck.getText().toString(), txtDriver.getText().toString(), shift, txtAGITS.getEditText().getText().toString(), txtCHTITS.getEditText().getText().toString(), txtTemperingHour.getEditText().getText().toString(), show, rndmString, finalStartDate, finalEndDate, fg_item,fg_uom);
                                        transferItem.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else if (hiddenTitle.equals("API Item Request")) {
                            TextView txtFromDept = gView.findViewById(R.id.lblSelectedFromDept);
                            TextView txtDueDate = gView.findViewById(R.id.lblSelectedDueDate);
                            TextInputLayout txtRemarkss = gView.findViewById(R.id.txtRemarks);
                            if (txtFromDept.getText().toString().trim().isEmpty() || txtFromDept.getText().toString().trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "From Department field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (txtDueDate.getText().toString().trim().isEmpty() || txtDueDate.getText().toString().trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Due Date field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else if (txtRemarkss.getEditText().getText().toString().trim().isEmpty() || txtRemarkss.getEditText().getText().toString().trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);
                                        ItemRequest itemRequest = new ItemRequest(txtRemarkss.getEditText().getText().toString(), txtFromDept.getText().toString(), txtDueDate.getText().toString(), show, rndmString);
                                        itemRequest.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else if (hiddenTitle.equals("API Item Request For Transfer")) {
                            TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
                            TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
                            TextInputLayout txtCHTITS = gView.findViewById(R.id.txtCHTITS);
                            TextInputLayout txtAGITS = gView.findViewById(R.id.txtAGITS);
                            TextInputLayout txtRemarkss = gView.findViewById(R.id.txtRemarks);
                            if (txtRemarkss.getEditText().getText().toString().trim().isEmpty() || txtRemarkss.getEditText().getText().toString().trim().equals("N/A")) {
                                Toast.makeText(getBaseContext(), "Remarks field is empty!", Toast.LENGTH_SHORT).show();
                                btnProceed.setEnabled(true);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setMessage("Are you sure want to submit?")
                                        .setCancelable(false);
                                builder.setPositiveButton("OK", null);
                                builder.setNegativeButton("Cancel", null);
                                AlertDialog show = builder.show();
                                Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        btnProceed.setEnabled(false);
                                        PendingItemRequest pendingItemRequest = new PendingItemRequest(txtRemarkss.getEditText().getText().toString().trim(), txtTruck.getText().toString(), txtDriver.getText().toString(), shift, txtAGITS.getEditText().getText().toString(), txtCHTITS.getEditText().getText().toString(), show, rndmString);
                                        pendingItemRequest.execute("");
                                    }
                                });
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        show.dismiss();
                                        btnProceed.setEnabled(true);
                                    }
                                });
                            }
                        } else {
                            AlertDialog.Builder myDialog = new AlertDialog.Builder(API_SelectedItems.this);
                            myDialog.setCancelable(false);
                            ScrollView scrollView = new ScrollView(getBaseContext());
                            LinearLayout layout = new LinearLayout(getBaseContext());
                            layout.setPadding(40, 40, 40, 40);
                            layout.setOrientation(LinearLayout.VERTICAL);
                            scrollView.addView(layout);

                            TextView lblFromSelectedBranch = new TextView(getBaseContext());
                            TextView lblSelectedFGItem = new TextView(getBaseContext());
                            TextView lblSelectedTruck = new TextView(getBaseContext());
                            TextView lblToSelectedBranch = new TextView(getBaseContext());
                            TextView lblFromSelectedDriver = new TextView(getBaseContext());
                            TextView lblSelectedVessel = new TextView(getBaseContext());
                            TextView lblSelectedStartDate = new TextView(getBaseContext());
                            TextView lblSelectedStartTime = new TextView(getBaseContext());
                            TextView lblSelectedEndDate = new TextView(getBaseContext());
                            TextView lblSelectedEndTime = new TextView(getBaseContext());
                            TextView lblSelectedDate = new TextView(getBaseContext());
                            EditText txtSupplier = new EditText(getBaseContext());
                            TextInputLayout textInputLayoutTempering = new TextInputLayout(API_SelectedItems.this);
                            TextInputLayout lblFGQuantity = new TextInputLayout(API_SelectedItems.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
//            TextInputLayout textInputLayoutVessel = new TextInputLayout(API_SelectedItems.this);
//            TextInputLayout textInputLayoutDriver = new TextInputLayout(API_SelectedItems.this);
                            TextInputLayout textInputLayoutAGIScale = new TextInputLayout(API_SelectedItems.this);
                            TextInputLayout textInputLayoutCHTIScale = new TextInputLayout(API_SelectedItems.this);
                            LinearLayout.LayoutParams layoutParamsBranch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams layoutParamsFromBranch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams layoutParamsBranch2 = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
                            LinearLayout.LayoutParams layoutParamsBranch3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
                                TextView lblFromBranch = new TextView(getBaseContext());
                                lblFromBranch.setText((hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production") ? "*Mill " : "From ") + (hiddenTitle.equals("API Item Request") ? "Department" : "Warehouse") + ":");
                                lblFromBranch.setTextColor(Color.rgb(0, 0, 0));
                                lblFromBranch.setTextSize(15);
                                lblFromBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layout.addView(lblFromBranch);

                                LinearLayout layoutFromBranch = new LinearLayout(getBaseContext());
                                layoutParamsFromBranch.setMargins(20, 0, 0, 20);
                                layoutFromBranch.setLayoutParams(layoutParamsFromBranch);
                                layoutFromBranch.setOrientation(LinearLayout.HORIZONTAL);
                                layoutParamsBranch3.setMargins(10, 0, 0, 0);

                                lblFromSelectedBranch.setText("N/A");
                                lblFromSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                                lblFromSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                                lblFromSelectedBranch.setTextSize(15);
                                lblFromSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblFromSelectedBranch.setLayoutParams(layoutParamsBranch2);
                                layoutFromBranch.addView(lblFromSelectedBranch);

                                TextView btnFromSelectBranch = new TextView(API_SelectedItems.this);
                                btnFromSelectBranch.setPadding(20, 10, 20, 10);
                                btnFromSelectBranch.setText("...");
                                btnFromSelectBranch.setBackgroundResource(R.color.colorPrimary);
                                btnFromSelectBranch.setTextColor(Color.WHITE);
                                btnFromSelectBranch.setTextSize(13);
                                btnFromSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                btnFromSelectBranch.setLayoutParams(layoutParamsBranch3);

                                btnFromSelectBranch.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        String title = hiddenTitle.equals("API Item Request") ? "Branch" : hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production") ? "Mill" : "Warehouse";
                                        myWarehouse myWarehouse = new myWarehouse(true, false, title, true, lblFromSelectedBranch);
                                        myWarehouse.execute("");
                                    }
                                });
                                layoutFromBranch.addView(btnFromSelectBranch);
                                layout.addView(layoutFromBranch);
                            }

                            if (hiddenTitle.equals("API Issue For Packing")) {
                                lblFGUOM = new TextView(API_SelectedItems.this);

                                TextView lblFGItem = new TextView(getBaseContext());
                                lblFGItem.setText("*FG Item: ");
                                lblFGItem.setTextColor(Color.rgb(0, 0, 0));
                                lblFGItem.setTextSize(15);
                                lblFGItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layout.addView(lblFGItem);

                                LinearLayout layoutFGItem = new LinearLayout(getBaseContext());
                                layoutParamsFromBranch.setMargins(20, 0, 0, 20);
                                layoutFGItem.setLayoutParams(layoutParamsFromBranch);
                                layoutFGItem.setOrientation(LinearLayout.HORIZONTAL);
                                layoutParamsBranch3.setMargins(10, 0, 0, 0);


                                lblSelectedFGItem.setText("N/A");
                                lblSelectedFGItem.setBackgroundColor(Color.parseColor("#ededed"));
                                lblSelectedFGItem.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedFGItem.setTextSize(15);
                                lblSelectedFGItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblSelectedFGItem.setLayoutParams(layoutParamsBranch2);
                                layoutFGItem.addView(lblSelectedFGItem);

                                TextView btnFGItem = new TextView(API_SelectedItems.this);
                                btnFGItem.setPadding(20, 10, 20, 10);
                                btnFGItem.setText("...");
                                btnFGItem.setBackgroundResource(R.color.colorPrimary);
                                btnFGItem.setTextColor(Color.WHITE);
                                btnFGItem.setTextSize(13);
                                btnFGItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                btnFGItem.setLayoutParams(layoutParamsBranch3);

                                btnFGItem.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        String title = "FG Item";
                                        myWarehouse myWarehouse = new myWarehouse(true, false, title, true, lblSelectedFGItem);
                                        myWarehouse.execute("");
                                    }
                                });
                                layoutFGItem.addView(btnFGItem);

                                TextView lblTextFGUOM = new TextView(API_SelectedItems.this);
                                lblTextFGUOM.setText("*FG UOM: ");
                                lblTextFGUOM.setTextColor(Color.rgb(0, 0, 0));
                                lblTextFGUOM.setTextSize(15);
                                lblTextFGUOM.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                lblFGUOM.setText("");
                                lblFGUOM.setTextColor(Color.rgb(0, 0, 0));
                                lblFGUOM.setBackgroundColor(Color.parseColor("#ededed"));
                                lblFGUOM.setTextSize(15);
                                lblFGUOM.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                layout.addView(layoutFGItem);
                                layout.addView(lblTextFGUOM);
                                layout.addView(lblFGUOM);

                                LinearLayout.LayoutParams layoutParamsLblQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParamsLblQuantity.setMargins(0, 10, 0, 10);
                                lblFGQuantity.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                                lblFGQuantity.setBoxCornerRadii(5, 5, 5, 5);
                                lblFGQuantity.setLayoutParams(layoutParamsLblQuantity);

                                TextInputEditText txtQuantity = new TextInputEditText(lblFGQuantity.getContext());
                                LinearLayout.LayoutParams layoutParamsTxtQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                txtQuantity.setLayoutParams(layoutParamsTxtQuantity);
                                txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                txtQuantity.setHint("*Enter FG Quantity");

                                lblFGQuantity.addView(txtQuantity);
                                lblFGQuantity.getEditText().setFocusable(true);

                                layout.addView(lblFGQuantity);

                            }

                            if (hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")) {
                                if (hiddenTitle.equals("API Received Item")) {
                                    TextView lblToBranch = new TextView(getBaseContext());
                                    lblToBranch.setText("*To Warehouse:");
                                    lblToBranch.setTextColor(Color.rgb(0, 0, 0));
                                    lblToBranch.setTextSize(15);
                                    lblToBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                    LinearLayout layoutToBranch = new LinearLayout(getBaseContext());
                                    LinearLayout.LayoutParams layoutParamsToBranch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParamsToBranch.setMargins(20, 0, 0, 20);
                                    layoutToBranch.setLayoutParams(layoutParamsFromBranch);
                                    layoutToBranch.setOrientation(LinearLayout.HORIZONTAL);

                                    lblToSelectedBranch.setText("N/A");
                                    lblToSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                                    lblToSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                                    lblToSelectedBranch.setTextSize(15);
                                    lblToSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    lblToSelectedBranch.setLayoutParams(layoutParamsBranch2);
                                    layoutToBranch.addView(lblToSelectedBranch);


                                    TextView btnToSelectBranch = new TextView(getBaseContext());
                                    btnToSelectBranch.setText("...");
                                    btnToSelectBranch.setPadding(20, 10, 20, 10);
                                    btnToSelectBranch.setBackgroundResource(R.color.colorPrimary);
                                    btnToSelectBranch.setTextColor(Color.WHITE);
                                    btnToSelectBranch.setTextSize(15);
                                    btnToSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    btnToSelectBranch.setLayoutParams(layoutParamsBranch3);

                                    btnToSelectBranch.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                return;
                                            }
                                            mLastClickTime = SystemClock.elapsedRealtime();
//                                toshowWarehouses(lblToSelectedBranch, false, "Warehouse");
                                            myWarehouse myWarehouse = new myWarehouse(false, false, "Warehouse", true, lblToSelectedBranch);
                                            myWarehouse.execute("");
                                        }
                                    });
                                    layoutToBranch.addView(btnToSelectBranch);
                                    layout.addView(lblToBranch);
                                    layout.addView(layoutToBranch);
                                }

                                if (hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request For Transfer")) {

                                    TextView lblTruck = new TextView(getBaseContext());
                                    lblTruck.setText("Truck:");
                                    lblTruck.setTextColor(Color.rgb(0, 0, 0));
                                    lblTruck.setTextSize(15);
                                    lblTruck.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                    LinearLayout layoutTruck = new LinearLayout(getBaseContext());
                                    LinearLayout.LayoutParams layoutParamsTruck = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParamsTruck.setMargins(20, 0, 0, 20);
                                    layoutTruck.setLayoutParams(layoutParamsTruck);
                                    layoutTruck.setOrientation(LinearLayout.HORIZONTAL);

                                    lblSelectedTruck.setText("N/A");
                                    lblSelectedTruck.setBackgroundColor(Color.parseColor("#ededed"));
                                    lblSelectedTruck.setTextColor(Color.rgb(0, 0, 0));
                                    lblSelectedTruck.setTextSize(15);
                                    lblSelectedTruck.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    lblSelectedTruck.setLayoutParams(layoutParamsBranch2);
                                    layoutTruck.addView(lblSelectedTruck);


                                    TextView btnTruck = new TextView(getBaseContext());
                                    btnTruck.setText("...");
                                    btnTruck.setPadding(20, 10, 20, 10);
                                    btnTruck.setBackgroundResource(R.color.colorPrimary);
                                    btnTruck.setTextColor(Color.WHITE);
                                    btnTruck.setTextSize(15);
                                    btnTruck.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    btnTruck.setLayoutParams(layoutParamsBranch3);

                                    btnTruck.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                return;
                                            }
                                            mLastClickTime = SystemClock.elapsedRealtime();
//                                showWarehouses(lblSelectedTruck, false, "Truck");
                                            myWarehouse myWarehouse = new myWarehouse(false, false, "Truck", true, lblSelectedTruck);
                                            myWarehouse.execute("");
                                        }
                                    });
                                    if (layoutTruck.getParent() != null) {
                                        ((ViewGroup) layoutTruck.getParent()).removeView(layoutTruck); // <- fix
                                    }
                                    layoutTruck.addView(btnTruck);
                                    layout.addView(lblTruck);
                                    layout.addView(layoutTruck);

                                    if (hiddenTitle.equals("API Received Item")) {
//                            textInputLayoutVessel.setHint("Vessel");
//                            textInputLayoutVessel.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
//                            textInputLayoutVessel.setBoxBackgroundColor(Color.WHITE);
//                            textInputLayoutVessel.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
//                            textInputLayoutVessel.setBoxStrokeColor(Color.parseColor("#1687a7"));
//
//                            TextInputEditText editTextVessel = new TextInputEditText(textInputLayoutVessel.getContext());
//                            textInputLayoutVessel.addView(editTextVessel);
//                            layout.addView(textInputLayoutVessel);

                                        TextView lblVessel = new TextView(getBaseContext());
                                        lblVessel.setText("Vessel:");
                                        lblVessel.setTextColor(Color.rgb(0, 0, 0));
                                        lblVessel.setTextSize(15);
                                        lblVessel.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                        LinearLayout layoutVessel = new LinearLayout(getBaseContext());
                                        LinearLayout.LayoutParams layoutParamsVessel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParamsVessel.setMargins(20, 0, 0, 20);
                                        layoutVessel.setLayoutParams(layoutParamsFromBranch);
                                        layoutVessel.setOrientation(LinearLayout.HORIZONTAL);

                                        lblSelectedVessel.setText("N/A");
                                        lblSelectedVessel.setBackgroundColor(Color.parseColor("#ededed"));
                                        lblSelectedVessel.setTextColor(Color.rgb(0, 0, 0));
                                        lblSelectedVessel.setTextSize(15);
                                        lblSelectedVessel.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                        lblSelectedVessel.setLayoutParams(layoutParamsBranch2);
                                        layoutVessel.addView(lblSelectedVessel);


                                        TextView btnSelectVessel = new TextView(getBaseContext());
                                        btnSelectVessel.setText("...");
                                        btnSelectVessel.setPadding(20, 10, 20, 10);
                                        btnSelectVessel.setBackgroundResource(R.color.colorPrimary);
                                        btnSelectVessel.setTextColor(Color.WHITE);
                                        btnSelectVessel.setTextSize(15);
                                        btnSelectVessel.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                        btnSelectVessel.setLayoutParams(layoutParamsBranch3);

                                        btnSelectVessel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                    return;
                                                }
                                                mLastClickTime = SystemClock.elapsedRealtime();
//                                toshowWarehouses(lblToSelectedBranch, false, "Warehouse");
                                                myWarehouse myWarehouse = new myWarehouse(false, false, "Vessel", true, lblSelectedVessel);
                                                myWarehouse.execute("");
                                            }
                                        });
                                        layoutVessel.addView(btnSelectVessel);
                                        layout.addView(lblVessel);
                                        layout.addView(layoutVessel);
                                    }

                                    TextView lblDriver = new TextView(getBaseContext());
                                    lblDriver.setText("Driver:");
                                    lblDriver.setTextColor(Color.rgb(0, 0, 0));
                                    lblDriver.setTextSize(15);
                                    lblDriver.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                    LinearLayout layoutDriver = new LinearLayout(getBaseContext());
                                    LinearLayout.LayoutParams layoutParamsDriver = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParamsDriver.setMargins(20, 0, 0, 20);
                                    layoutDriver.setLayoutParams(layoutParamsFromBranch);
                                    layoutDriver.setOrientation(LinearLayout.HORIZONTAL);

                                    lblFromSelectedDriver.setText("N/A");
                                    lblFromSelectedDriver.setBackgroundColor(Color.parseColor("#ededed"));
                                    lblFromSelectedDriver.setTextColor(Color.rgb(0, 0, 0));
                                    lblFromSelectedDriver.setTextSize(15);
                                    lblFromSelectedDriver.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    lblFromSelectedDriver.setLayoutParams(layoutParamsBranch2);
                                    layoutDriver.addView(lblFromSelectedDriver);


                                    TextView btnSelectDriver = new TextView(getBaseContext());
                                    btnSelectDriver.setText("...");
                                    btnSelectDriver.setPadding(20, 10, 20, 10);
                                    btnSelectDriver.setBackgroundResource(R.color.colorPrimary);
                                    btnSelectDriver.setTextColor(Color.WHITE);
                                    btnSelectDriver.setTextSize(15);
                                    btnSelectDriver.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    btnSelectDriver.setLayoutParams(layoutParamsBranch3);

                                    btnSelectDriver.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                return;
                                            }
                                            mLastClickTime = SystemClock.elapsedRealtime();
//                                toshowWarehouses(lblToSelectedBranch, false, "Warehouse");
                                            myWarehouse myWarehouse = new myWarehouse(false, false, "Driver", true, lblFromSelectedDriver);
                                            myWarehouse.execute("");
                                        }
                                    });
                                    layoutDriver.addView(btnSelectDriver);
                                    layout.addView(lblDriver);
                                    layout.addView(layoutDriver);


//                        textInputLayoutDriver.setHint("Driver");
//                        textInputLayoutDriver.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
//                        textInputLayoutDriver.setBoxBackgroundColor(Color.WHITE);
//                        textInputLayoutDriver.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
//                        textInputLayoutDriver.setBoxStrokeColor(Color.parseColor("#1687a7"));
//
//                        TextInputEditText editDriver = new TextInputEditText(textInputLayoutDriver.getContext());
//                        textInputLayoutDriver.addView(editDriver);
//                        layout.addView(textInputLayoutDriver);

                                    if (!hiddenTitle.equals("API Item Request For Transfer")) {
                                        LinearLayout layoutShift = new LinearLayout(getBaseContext());
                                        LinearLayout.LayoutParams layoutParamsShift = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                        layoutParamsShift.setMargins(20, 0, 0, 20);
                                        layoutShift.setLayoutParams(layoutParamsShift);
                                        layoutShift.setOrientation(LinearLayout.HORIZONTAL);

                                        TextView lblShift = new TextView(getBaseContext());
                                        lblShift.setText("*Shift: ");
                                        lblShift.setTextColor(Color.rgb(0, 0, 0));
                                        lblShift.setTextSize(15);
                                        lblShift.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    }

                                    textInputLayoutAGIScale.setHint("AGI Truck Scale");
                                    textInputLayoutAGIScale.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                                    textInputLayoutAGIScale.setBoxBackgroundColor(Color.WHITE);
                                    textInputLayoutAGIScale.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                                    textInputLayoutAGIScale.setBoxStrokeColor(Color.parseColor("#1687a7"));

                                    //Must use context of textInputLayout
                                    TextInputEditText editTextAGIScale = new TextInputEditText(textInputLayoutAGIScale.getContext());
                                    textInputLayoutAGIScale.addView(editTextAGIScale);
//                    txt
                                    layout.addView(textInputLayoutAGIScale);

                                    textInputLayoutCHTIScale.setHint("CHTI Truck Scale");
                                    textInputLayoutCHTIScale.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                                    textInputLayoutCHTIScale.setBoxBackgroundColor(Color.WHITE);
                                    textInputLayoutCHTIScale.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                                    textInputLayoutCHTIScale.setBoxStrokeColor(Color.parseColor("#1687a7"));

                                    //Must use context of textInputLayout
                                    TextInputEditText editTextCHTIScale = new TextInputEditText(textInputLayoutCHTIScale.getContext());
                                    textInputLayoutCHTIScale.addView(editTextCHTIScale);
//                    txt
                                    layout.addView(textInputLayoutCHTIScale);
                                }
                            }
                            if (hiddenTitle.equals("API Item Request")) {
                                MaterialButton btnPickDate = new MaterialButton(API_SelectedItems.this);
                                LinearLayout.LayoutParams layoutParamsBtnDate = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsBtnDate.setMargins(0, 0, 0, 20);
                                btnPickDate.setText("*Pick Due Date");
                                btnPickDate.setCornerRadius(20);
                                btnPickDate.setLayoutParams(layoutParamsBtnDate);
                                btnPickDate.setBackgroundResource(R.color.colorPrimary);
                                btnPickDate.setTextColor(Color.WHITE);
                                btnPickDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                btnBack.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                btnPickDate.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onClick(View view) {
                                        showDatePickerDialog(lblSelectedDate);
                                    }
                                });
                                layout.addView(btnPickDate);

                                LinearLayout.LayoutParams layoutParamsLblSelectedDate = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsLblSelectedDate.setMargins(0, 0, 0, 20);
                                lblSelectedDate.setLayoutParams(layoutParamsLblSelectedDate);
                                lblSelectedDate.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedDate.setText("N/A");
                                lblSelectedDate.setTextSize(15);
                                lblSelectedDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layout.addView(lblSelectedDate);
                            }

                            if (hiddenTitle.equals("API Transfer Item")) {
                                textInputLayoutTempering.setHint("Tempering Hour");
                                textInputLayoutTempering.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                                textInputLayoutTempering.setBoxBackgroundColor(Color.WHITE);
                                textInputLayoutTempering.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                                textInputLayoutTempering.setBoxStrokeColor(Color.parseColor("#1687a7"));

                                TextInputEditText editTextTempering = new TextInputEditText(textInputLayoutTempering.getContext());
                                editTextTempering.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                textInputLayoutTempering.addView(editTextTempering);
                                layout.addView(textInputLayoutTempering);

                                TextView lblStartDate = new TextView(getBaseContext());
                                lblStartDate.setText("Transfer Start Date:");
                                lblStartDate.setTextColor(Color.rgb(0, 0, 0));
                                lblStartDate.setTextSize(15);
                                lblStartDate.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                layout.addView(lblStartDate);

                                LinearLayout layoutStartDate = new LinearLayout(getBaseContext());
                                LinearLayout.LayoutParams layoutParamsVessel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParamsVessel.setMargins(40, 0, 0, 40);
                                layoutStartDate.setLayoutParams(layoutParamsFromBranch);
                                layoutStartDate.setOrientation(LinearLayout.HORIZONTAL);

                                LinearLayout.LayoutParams layoutParamsStart = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                lblSelectedStartDate.setText("---- -- --");
                                lblSelectedStartDate.setBackgroundColor(Color.parseColor("#ededed"));
                                lblSelectedStartDate.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedStartDate.setTextSize(15);
                                lblSelectedStartDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layoutParamsStart.setMargins(20, 0, 0, 0);
                                lblSelectedStartDate.setLayoutParams(layoutParamsStart);
                                layoutStartDate.addView(lblSelectedStartDate);


                                TextView btnSelectStartDate = new TextView(getBaseContext());
                                btnSelectStartDate.setText("...");
                                btnSelectStartDate.setPadding(20, 10, 20, 10);
                                btnSelectStartDate.setBackgroundResource(R.color.colorPrimary);
                                btnSelectStartDate.setTextColor(Color.WHITE);
                                btnSelectStartDate.setTextSize(15);
                                btnSelectStartDate.setGravity(View.TEXT_ALIGNMENT_CENTER);

//                btnSelectStartDate.setLayoutParams(layoutParamsStart);

                                btnSelectStartDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        showDatePickerDialog(lblSelectedStartDate);
                                    }
                                });
                                layoutStartDate.addView(btnSelectStartDate);
                                LinearLayout.LayoutParams layoutParamsStartTime = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParamsStartTime.setMargins(20, 0, 10, 0);
                                lblSelectedStartTime.setText("--:--");
                                lblSelectedStartTime.setBackgroundColor(Color.parseColor("#ededed"));
                                lblSelectedStartTime.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedStartTime.setTextSize(15);
                                lblSelectedStartTime.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblSelectedStartTime.setLayoutParams(layoutParamsStartTime);
                                layoutStartDate.addView(lblSelectedStartTime);

                                LinearLayout.LayoutParams layoutParamsStartTime2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TextView btnSelectStartTime = new TextView(getBaseContext());
                                btnSelectStartTime.setText("...");
                                btnSelectStartTime.setPadding(20, 10, 20, 10);
                                btnSelectStartTime.setBackgroundResource(R.color.colorPrimary);
                                btnSelectStartTime.setTextColor(Color.WHITE);
                                btnSelectStartTime.setTextSize(15);
                                btnSelectStartTime.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblSelectedStartTime.setLayoutParams(layoutParamsStartTime);
                                layoutParamsStartTime2.setMargins(20, 0, 0, 0);
                                btnSelectStartTime.setLayoutParams(layoutParamsStartTime2);

                                btnSelectStartTime.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        showTime(lblSelectedStartTime);
                                    }
                                });
                                layoutParamsStart.setMargins(0, 0, 10, 0);
                                btnSelectStartTime.setLayoutParams(layoutParamsStart);
                                layoutStartDate.addView(btnSelectStartTime);
                                layout.addView(layoutStartDate);

                                TextView lblEndDate = new TextView(getBaseContext());
                                lblEndDate.setText("Transfer End Date:");
                                lblEndDate.setTextColor(Color.rgb(0, 0, 0));
                                lblEndDate.setTextSize(15);
                                lblEndDate.setGravity(View.TEXT_ALIGNMENT_CENTER);

                                layout.addView(lblEndDate);

                                LinearLayout layoutEndDate = new LinearLayout(getBaseContext());
                                layoutParamsVessel.setMargins(20, 0, 0, 20);
                                layoutEndDate.setLayoutParams(layoutParamsFromBranch);
                                layoutEndDate.setOrientation(LinearLayout.HORIZONTAL);

                                lblSelectedEndDate.setText("---- -- --");
                                lblSelectedEndDate.setBackgroundColor(Color.parseColor("#ededed"));
                                lblSelectedEndDate.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedEndDate.setTextSize(15);
                                lblSelectedEndDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layoutEndDate.addView(lblSelectedEndDate);

                                LinearLayout.LayoutParams layoutParamsEndDate = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TextView btnSelectEndDate = new TextView(getBaseContext());
                                btnSelectEndDate.setText("...");
                                btnSelectEndDate.setPadding(20, 10, 20, 10);
                                btnSelectEndDate.setBackgroundResource(R.color.colorPrimary);
                                btnSelectEndDate.setTextColor(Color.WHITE);
                                btnSelectEndDate.setTextSize(15);
                                btnSelectEndDate.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layoutParamsStart.setMargins(0, 0, 10, 0);
                                layoutParamsEndDate.setMargins(10, 0, 0, 0);
                                btnSelectEndDate.setLayoutParams(layoutParamsEndDate);

                                btnSelectEndDate.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        showDatePickerDialog(lblSelectedEndDate);
                                    }
                                });

                                layoutEndDate.addView(btnSelectEndDate);
                                LinearLayout.LayoutParams layoutParamsEndTime = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lblSelectedEndTime.setText("--:--");
                                lblSelectedEndTime.setBackgroundColor(Color.parseColor("#ededed"));
                                lblSelectedEndTime.setTextColor(Color.rgb(0, 0, 0));
                                lblSelectedEndTime.setTextSize(15);
                                lblSelectedEndTime.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layoutParamsEndTime.setMargins(20, 0, 10, 0);
                                lblSelectedEndTime.setLayoutParams(layoutParamsEndTime);
                                layoutEndDate.addView(lblSelectedEndTime);

                                LinearLayout.LayoutParams layoutParamsEndTime2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                TextView btnSelectEndTime = new TextView(getBaseContext());
                                btnSelectEndTime.setText("...");
                                btnSelectEndTime.setPadding(20, 10, 20, 10);
                                btnSelectEndTime.setBackgroundResource(R.color.colorPrimary);
                                btnSelectEndTime.setTextColor(Color.WHITE);
                                btnSelectEndTime.setTextSize(15);
                                btnSelectEndTime.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                layoutParamsEndTime2.setMargins(10, 0, 0, 0);
                                btnSelectEndTime.setLayoutParams(layoutParamsEndTime2);

                                btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();
                                        showTime(lblSelectedEndTime);
                                    }
                                });
                                layoutParamsStart.setMargins(0, 0, 10, 0);
                                btnSelectEndTime.setLayoutParams(layoutParamsStart);
                                layoutEndDate.addView(btnSelectEndTime);
                                layout.addView(layoutEndDate);
                            }

                            TextInputLayout textInputLayoutRemarks = new TextInputLayout(API_SelectedItems.this);
                            textInputLayoutRemarks.setHint("*Remarks");
                            textInputLayoutRemarks.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
                            textInputLayoutRemarks.setBoxBackgroundColor(Color.WHITE);
                            textInputLayoutRemarks.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                            textInputLayoutRemarks.setBoxStrokeColor(Color.parseColor("#1687a7"));

                            TextInputEditText editTextRemarks = new TextInputEditText(textInputLayoutRemarks.getContext());
                            textInputLayoutRemarks.addView(editTextRemarks);
                            layout.addView(textInputLayoutRemarks);

                            String supplier = "";
                            Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
                            if (cursor != null) {
                                if (cursor.moveToNext()) {
                                    supplier = cursor.getString(2);
                                }
                            }

                            String finalSupplier = supplier;
                            myDialog.setPositiveButton("Submit", null);

                            myDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

                            myDialog.setView(scrollView);
                            AlertDialog alertDialog = myDialog.show();
//            alertDialog.getWindow().setLayout(720,hiddenTitle.equals("API System Transfer Item") ? 420 : 1300);
                            Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String hashedID = randomString();
                                    double fgQuantity;
                                    try {
                                        fgQuantity = Double.parseDouble(lblFGQuantity.getEditText().getText().toString());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        ;
                                        fgQuantity = 0.00;
                                    }
                                    utility_class utilityc = new utility_class();
                                    String[] prodList = utility_class.prodList;
                                    String[] packingList = utility_class.packingList;
                                    if (hiddenTitle.equals("API Issue For Production") && !utilityc.isAllowProdPacking(prodList, API_SelectedItems.this)) {
                                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                                        for (String list : prodList) {
                                            appendMsg += "- " + list + "\n";
                                        }
                                        String title = "Validation";
                                        String msg = "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                                        utilityc.customAlertDialog(title, msg, API_SelectedItems.this);
                                        btnProceed.setEnabled(true);
                                    } else if (hiddenTitle.equals("API Issue For Packing") && !utilityc.isAllowProdPacking(packingList, API_SelectedItems.this)) {
                                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                                        for (String list : packingList) {
                                            appendMsg += "- " + list + "\n";
                                        }
                                        String title = "Validation";
                                        String msg = "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                                        utilityc.customAlertDialog(title, msg, API_SelectedItems.this);
                                        btnProceed.setEnabled(true);
                                    } else if (lblFromSelectedBranch.getText().toString().equals("N/A") && (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing"))) {
                                        Toast.makeText(getBaseContext(), "Please select Mill Warehouse", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (lblSelectedFGItem.getText().toString().equals("N/A") && hiddenTitle.equals("API Issue For Packing")) {
                                        Toast.makeText(getBaseContext(), "Please select FG Item", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (fgQuantity <= 0 && hiddenTitle.equals("API Issue For Packing")) {
                                        Toast.makeText(getBaseContext(), "Please FG Quantity", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (lblFromSelectedBranch.getText().toString().equals("N/A") && hiddenTitle.equals("API Received Item")) {
                                        Toast.makeText(getBaseContext(), "Please select From Warehouse", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (lblToSelectedBranch.getText().toString().equals("N/A") && hiddenTitle.equals("API Received Item")) {
                                        Toast.makeText(getBaseContext(), "Please select To Warehouse", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (lblFromSelectedBranch.getText().toString().equals("N/A") && hiddenTitle.equals("API Item Request")) {
                                        Toast.makeText(getBaseContext(), "Please select From Department", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && lblSelectedStartDate.getText().toString().equals("---- -- --") && !lblSelectedStartTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer Start Date", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && !lblSelectedStartDate.getText().toString().equals("---- -- --") && lblSelectedStartTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer Start Time", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && lblSelectedEndDate.getText().toString().equals("---- -- --") && !lblSelectedEndTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer End Date", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && !lblSelectedEndDate.getText().toString().equals("---- -- --") && lblSelectedEndTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer End Time", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && lblSelectedStartDate.getText().toString().equals("---- -- --") && lblSelectedStartTime.getText().toString().equals("--:--") && !lblSelectedEndDate.getText().toString().equals("---- -- --") && !lblSelectedEndTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer Start Date", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Transfer Item") && !lblSelectedStartDate.getText().toString().equals("---- -- --") && !lblSelectedStartTime.getText().toString().equals("--:--") && lblSelectedEndDate.getText().toString().equals("---- -- --") && lblSelectedEndTime.getText().toString().equals("--:--")) {
                                        Toast.makeText(getBaseContext(), "Please select Transfer End Date", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (textInputLayoutRemarks.getEditText().getText().toString().isEmpty()) {
                                        Toast.makeText(getBaseContext(), "Remarks field is empty", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    } else if (hiddenTitle.equals("API Item Request") && lblSelectedDate.getText().toString() == "N/A") {
                                        Toast.makeText(getBaseContext(), "Please select Due Date", Toast.LENGTH_SHORT).show();
                                        btnProceed.setEnabled(true);
                                        return;
                                    }
                                    AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    double finalFgQuantity = fgQuantity;
                                    builder.setMessage("Are you sure want to submit?")
                                            .setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String whseCode = "", whseToCode = "";
                                                    if (!lblFromSelectedBranch.getText().toString().isEmpty()) {
                                                        String globalResult = "", keyName = "", keyFind = "";
                                                        if (hiddenTitle.equals("API Item Request")) {
                                                            globalResult = gBranchh;
                                                            keyName = "name";
                                                            keyFind = "code";
                                                        } else if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
                                                            globalResult = gMill;
                                                            System.out.println("otso mill: " + globalResult);
                                                            keyName = "name";
                                                            keyFind = "code";
                                                        } else {
                                                            globalResult = gBranch;
                                                            keyName = "whsename";
                                                            keyFind = "whsecode";
                                                        }
                                                        if (lblFromSelectedBranch != null && lblFromSelectedBranch.getText() != null) {
                                                            whseCode = findWarehouseCode(globalResult, lblFromSelectedBranch.getText().toString(), keyName, keyFind);
                                                        }
                                                        if (!(hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production"))) {
                                                            whseToCode = hiddenTitle.equals("API Item Request") ? "" : findWarehouseCode(globalResult, lblToSelectedBranch.getText().toString(), "whsename", "whsecode");
                                                        }
                                                    }

                                                    if (hiddenTitle.equals("API Received Item")) {
                                                        btnProceed.setEnabled(false);
                                                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                        String currentWhse = Objects.requireNonNull(sharedPreferences.getString("whse", ""));
                                                        whseCode = whseCode.equals("N/A") || whseCode.isEmpty() ? currentWhse : whseCode;
                                                        String shift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));

                                                        ManualReceived manualReceived = new ManualReceived(whseCode, whseToCode, textInputLayoutRemarks.getEditText().getText().toString(), txtSupplier.getText().toString(), lblSelectedTruck.getText().toString(), lblSelectedVessel.getText().toString(), lblFromSelectedDriver.getText().toString(), shift, textInputLayoutAGIScale.getEditText().getText().toString(), textInputLayoutCHTIScale.getEditText().getText().toString(), alertDialog, hashedID);
                                                        manualReceived.execute("");
//                                            apiSaveManualReceived(whseCode,whseToCode, textInputLayoutRemarks.getEditText().getText().toString(),txtSupplier.getText().toString(),lblSelectedTruck.getText().toString(), textInputLayoutVessel.getEditText().getText().toString(), textInputLayoutDriver.getEditText().getText().toString(), lblSelectedProductionShift.getText().toString(), textInputLayoutAGIScale.getEditText().getText().toString(), textInputLayoutCHTIScale.getEditText().getText().toString(),alertDialog);
                                                    } else if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
                                                        String shift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                                                        btnProceed.setEnabled(false);
                                                        String sFgItem = "", sFgUom = "";
                                                        if (hiddenTitle.equals("API Issue For Packing")) {
                                                            sFgItem = lblSelectedFGItem.getText().toString();
                                                            sFgUom = lblFGUOM.getText().toString();
                                                        }
                                                        Production production = new Production(whseCode, textInputLayoutRemarks.getEditText().getText().toString(), shift, alertDialog, sFgItem, sFgUom, finalFgQuantity, hashedID);
                                                        production.execute("");
//                                        apiSaveIssueForProduction(whseCode, textInputLayoutRemarks.getEditText().getText().toString(), shift, alertDialog);
                                                    } else if (hiddenTitle.equals("API Transfer Item")) {
                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                                                        Date dd = null, dd1 = null;
                                                        String startDate = "", endDate = "";
                                                        try {
                                                            dd = format.parse(lblSelectedStartDate.getText().toString() + " " + lblSelectedStartTime.getText().toString());
                                                            dd1 = format.parse(lblSelectedEndDate.getText().toString() + " " + lblSelectedEndTime.getText().toString());
                                                            startDate = format.format(dd);
                                                            endDate = format.format(dd1);
                                                        } catch (Exception ex) {
                                                            ex.printStackTrace();
                                                        }
//                                            sFgItem = lblSelectedFGItem.getText().toString();
                                                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                        String shift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                                                        TransferItem transferItem = new TransferItem(textInputLayoutRemarks.getEditText().getText().toString(), lblSelectedTruck.getText().toString(), lblFromSelectedDriver.getText().toString(), shift, textInputLayoutAGIScale.getEditText().getText().toString(), textInputLayoutCHTIScale.getEditText().getText().toString(), textInputLayoutTempering.getEditText().getText().toString(), alertDialog, hashedID, startDate, endDate, "","");
                                                        transferItem.execute("");
//                                            apiSaveTransferItem(textInputLayoutRemarks.getEditText().getText().toString(),lblSelectedTruck.getText().toString(), textInputLayoutVessel.getEditText().getText().toString(), textInputLayoutDriver.getEditText().getText().toString(), lblSelectedProductionShift.getText().toString(), textInputLayoutAGIScale.getEditText().getText().toString(), textInputLayoutCHTIScale.getEditText().getText().toString(),alertDialog);
                                                    } else if (hiddenTitle.equals("API Item Request")) {
                                                        ItemRequest itemRequest = new ItemRequest(textInputLayoutRemarks.getEditText().getText().toString(), whseCode, lblSelectedDate.getText().toString(), alertDialog, hashedID);
                                                        itemRequest.execute("");
                                                    }
//                                        else if(hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count")){
//                                            apiSaveInventoryCount(textInputLayoutRemarks.getEditText().getText().toString(),alertDialog);
//                                        }
                                                    else if (hiddenTitle.equals("API Item Request For Transfer")) {
                                                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                                        String shift = Objects.requireNonNull(sharedPreferences.getString("shift", ""));
                                                        try {
//                                                apiSaveItemRequestForTransfer(textInputLayoutRemarks.getEditText().getText().toString().trim(),whseCode, whseToCode,alertDialog);
                                                            PendingItemRequest pendingItemRequest = new PendingItemRequest(textInputLayoutRemarks.getEditText().getText().toString().trim(), lblSelectedTruck.getText().toString(), lblFromSelectedDriver.getText().toString(), shift, textInputLayoutAGIScale.getEditText().getText().toString(), textInputLayoutCHTIScale.getEditText().getText().toString(), alertDialog, hashedID);
                                                            pendingItemRequest.execute("");
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    } else {
                                                        try {
                                                            apiSaveDataRec(finalSupplier, textInputLayoutRemarks.getEditText().getText().toString(), alertDialog, hashedID);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
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
                            });
                        }
                    } else {
                        btnBack.setEnabled(true);
                        Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Back once only", Toast.LENGTH_SHORT).show();
                    }
                    gI = 0;
                }
            }, 500);
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gI++;
                btnBack.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(gI == 1){
                            btnBack.setEnabled(true);
                            Intent intent;
                            intent = new Intent(getBaseContext(), APIReceived.class);
                            intent.putExtra("title", title);
                            intent.putExtra("hiddenTitle", hiddenTitle);
                            startActivity(intent);
                            finish();
                        }else{
                            btnBack.setEnabled(true);
                            Toast.makeText(getBaseContext(), "You pressed too many times! Please click the button Back once only", Toast.LENGTH_SHORT).show();
                        }
                        gI = 0;
                    }
                },500);

            }
        });
    }

    public String randomString(){
        int n = 20;
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnñopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private class showAssignedDepartment extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
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
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SelectedItems.this);
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
                ArrayAdapter adapter = new ArrayAdapter(API_SelectedItems.this,android.R.layout.simple_list_item_1, arrayList){
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
                        androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SelectedItems.this);
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
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
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
                        ((TextView)v.findViewById(R.id.title)).setText(title);
                        ((TextView)v.findViewById(R.id.title2)).setText(branch + " - " + currentShift);
                        getSupportActionBar().setCustomView(v);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                        builder.setCancelable(false);
                        builder.setTitle("Message");
                        builder.setMessage(msg);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (gView != null) {
                                    TextView lblFGItem = gView.findViewById(R.id.labelFGItem);
                                    TextView labelFGUOM = gView.findViewById(R.id.labelFGUOM);
                                    TextView txtFGItem = gView.findViewById(R.id.lblSelectedFGItem);
                                    TextView btnSelectFGItem = gView.findViewById(R.id.btnSelectFGItem);

                                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                                    String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                                    if (currentDepartment.equals("FLOUR BINS") || currentDepartment.equals("CC-FLOUR BINS") || currentDepartment.equals("BRAN/POLLARD PACKING BINS") || currentDepartment.equals("CC-BRAN/POLLARD PACKING BINS") || currentDepartment.equals("BRAN/POLLARD BINS") || currentDepartment.equals("CC-BRAN/POLLARD BINS")) {
                                        lblFGItem.setVisibility(VISIBLE);
                                        btnSelectFGItem.setVisibility(VISIBLE);
                                        txtFGItem.setVisibility(VISIBLE);
                                        lblFGUOM.setVisibility(VISIBLE);
                                        labelFGUOM.setVisibility(VISIBLE);
                                        btnSelectFGItem.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                                    return;
                                                }
                                                mLastClickTime = SystemClock.elapsedRealtime();
                                                getDataFromDownload downloadd = new getDataFromDownload("FG Item",txtFGItem, "FG Item","","",false);
                                                downloadd.execute("");
                                            }
                                        });
                                    } else {
                                        lblFGItem.setVisibility(GONE);
                                        btnSelectFGItem.setVisibility(GONE);
                                        txtFGItem.setVisibility(GONE);
                                        lblFGUOM.setVisibility(GONE);
                                        labelFGUOM.setVisibility(GONE);
                                    }
                                }
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
            navigationManager.showFragment(title);
            getSupportActionBar().setTitle(title);
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
                    if(utilityc.isAllowProdPacking(prodList, API_SelectedItems.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Production");
                        intent.putExtra("hiddenTitle", "API Issue For Production");
                        startActivity(intent);
                        finish();
                    }else{
                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                        for(String list : prodList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Production!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, API_SelectedItems.this);
                    }
                }
                else if(selectedItem.equals("Issue For Packing")) {
                    utility_class utilityc = new utility_class();
                    String[] packingList = utility_class.packingList;
                    if(utilityc.isAllowProdPacking(packingList, API_SelectedItems.this)){
                        intent = new Intent(getBaseContext(), APIReceived.class);
                        intent.putExtra("title", "Issue For Packing");
                        intent.putExtra("hiddenTitle", "API Issue For Packing");
                        startActivity(intent);
                        finish();
                    }else{
                        String appendMsg = "Please Read\n\n These are the list of departments that are allowed to transact: \n";
                        for(String list : packingList){
                            appendMsg+= "- " + list + "\n";
                        }
                        String title = "Validation";
                        String msg =  "Your current department is not allowed to transact Issue For Packing!\n\n" + appendMsg + "\n\nYou can change your current department under Settings > Change Department";
                        utilityc.customAlertDialog(title,msg, API_SelectedItems.this);
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
                else if(selectedItem.equals("Received from Production")) {
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
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
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
                            androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SelectedItems.this);
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
                            ArrayAdapter adapter = new ArrayAdapter(API_SelectedItems.this,android.R.layout.simple_list_item_1, arrayList);
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
                                    androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(API_SelectedItems.this);
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

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
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
                                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
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
                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
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

    private class Production extends AsyncTask<String, Void, String> {
        String gRemarks, gFromBranch, gShift, gFGItem, gFGUOM,gHashedID;
        double gFGQuantity = 0.00;
        AlertDialog gDialog;
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);

        public Production(String fromBranch, String remarks, String shift, AlertDialog dialog, String fgItem, String fgUom, double fgQuantity, String hashedID) {
            gRemarks = remarks;
            gDialog = dialog;
            gFromBranch = fromBranch;
            gShift = shift;
            gFGItem = fgItem;
            gFGUOM = fgUom;
            gFGQuantity = fgQuantity;
            gHashedID = hashedID;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            JSONObject jsonObject = new JSONObject();
            try {
                // create your json here
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                JSONObject objectHeaders = new JSONObject();
                objectHeaders.put("sap_number", JSONObject.NULL);
                objectHeaders.put("transdate", currentDateandTime);
                objectHeaders.put("shift", gShift);
                objectHeaders.put("mill", gFromBranch);
                objectHeaders.put("hashed_id", gHashedID);
                objectHeaders.put("remarks", gRemarks);
                if (hiddenTitle.equals("API Received from Production")) {
                    DatabaseHelper9 myDb9 = new DatabaseHelper9(getBaseContext());
                    String sIssueResult = "";
                    Cursor cursor9 = myDb9.getAllData();
                    if (cursor9.moveToNext()) {
                        sIssueResult = cursor9.getString(3);
                    }
                    if (!sIssueResult.trim().isEmpty()) {
                        if (sIssueResult.startsWith("{")) {
                            JSONObject jsonObjectResult = new JSONObject(sIssueResult);
                            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                            objectHeaders.put("issue_id", jsonObjectData.has("id") ? !jsonObjectData.isNull("id") ? jsonObjectData.getInt("id") : 0 : 0);
                        }
                    }
                } else if (hiddenTitle.equals("API Issue For Packing")) {
                    objectHeaders.put("fg_item", gFGItem);
                    objectHeaders.put("fg_uom", gFGUOM);
                    objectHeaders.put("fg_quantity", gFGQuantity);
                }
                objectHeaders.put("remarks", gRemarks);
                jsonObject.put("header", objectHeaders);
                JSONArray arrayDetails = new JSONArray();
                Cursor cursor = myDb4.getAllData(title);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        JSONObject objectDetails = new JSONObject();
                        objectDetails.put("item_code", cursor.getString(6));
                        objectDetails.put("whsecode", cursor.getString(hiddenTitle.equals("API Received from Production") ? 8 : 7));
                        objectDetails.put("quantity", cursor.getDouble(2));
                        objectDetails.put("uom", cursor.getString(5));
                        arrayDetails.put(objectDetails);
                    }
                    jsonObject.put("rows", arrayDetails);
                }
            } catch (JSONException e) {
                btnProceed.setEnabled(true);
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPaddress = sharedPreferences2.getString("IPAddress", "");
            String appendURL = hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") ? "/api/production/issue_for_prod/new" : "/api/production/rec_from_prod/new";
            String sURL = IPaddress + appendURL;
            String method = "POST";
            String bodyy = jsonObject.toString();
            String fromModule = title;
            String hiddenFromModule = hiddenTitle;
            System.out.println("url " + sURL);
            System.out.println("body " + bodyy);
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(sURL)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isInserted = myDb7.insertData(sURL, method, bodyy, fromModule, hiddenFromModule, currentDate);
                            btnProceed.setEnabled(true);
                            String msg = "The data is " + (isInserted ? "inserted to" : "failed to insert in") + " local database";
                            if (isInserted) {
                                DatabaseHelper9 myDb9 = new DatabaseHelper9(getBaseContext());
                                myDb9.truncateTable();
                                myDb4.truncateTable();
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                            builder.setCancelable(false);
                            builder.setTitle("Message");
                            builder.setMessage(msg);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isInserted) {
                                        btnProceed.setEnabled(true);
                                        gDialog.dismiss();
                                        loadItems();
                                    } else {
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    String answer = response.body().string();
                    formatResponse(answer, gDialog);
                }
            });
            return null;
        }
    }

    public void apiSaveReceivedProduction(String remarks, AlertDialog dialogg){
        Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                JSONObject jsonObject = new JSONObject();
                try {
                    // create your json here
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    JSONArray arrayDetails = new JSONArray();
                    String whseCode = "";
                    int baseID = 0;
                    Cursor cursor2 = myDb3.getAllSelectedData(hiddenTitle);
                    while (cursor2.moveToNext()) {
                        if(cursor2.getInt(6) == 1) {
                            JSONObject objectDetails = new JSONObject();
                            String itemName = cursor2.getString(16);
                            whseCode = cursor2.getString(8);
                            baseID = cursor2.getInt(9);
                            Double actualQty = cursor2.getDouble(5);
                            objectDetails.put("item_code", itemName);
                            objectDetails.put("quantity", actualQty);
                            objectDetails.put("whsecode", whseCode);
                            objectDetails.put("uom", cursor2.getString(11));
                            arrayDetails.put(objectDetails);
                        }
                    }
                    cursor2.close();
                    JSONObject objectHeaders = new JSONObject();
                    objectHeaders.put("prod_order_id", baseID);
                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("sap_number", JSONObject.NULL);
                    objectHeaders.put("remarks", remarks);
                    objectHeaders.put("whsecode", whseCode);
                    jsonObject.put("header", objectHeaders);
                    jsonObject.put("rows", arrayDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                System.out.println("HOY: " + jsonObject);
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPaddress = sharedPreferences2.getString("IPAddress", "");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPaddress + "/api/production/rec_from_prod/new")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setCancelable(false);
                                builder.setTitle("Validation");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    dialogg.dismiss();
                                    dialog.dismiss();
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) {
                        String result = "";
                        try {
                            result = response.body().string();
//                            System.out.println(result);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String finalResult = result;
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                JSONObject jj = new JSONObject(finalResult);
                                String msg = jj.has("message") ? jj.getString("message") : "";
                                boolean isSuccess = jj.has("success") && jj.getBoolean("success");
                                if (isSuccess) {
                                    myDb3.truncateTable();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("Message");
                                    builder.setMessage(msg);
                                    builder.setPositiveButton("OK", (dialog, which) -> {
                                        dialogg.dismiss();
                                        dialog.dismiss();
                                        loadItems();
                                    });
                                    builder.show();
                                } else {
                                    if (msg.equals("Token is invalid")) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Your session is expired. Please login again.");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            pc.loggedOut(API_SelectedItems.this);
                                            pc.removeToken(API_SelectedItems.this);
                                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                                            finish();
                                            dialog.dismiss();
                                        });
                                        builder.show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Error \n" + msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setCancelable(false);
                                builder.setTitle("Validation");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                });
                                builder.show();
                            }
                        });
                    }
                });
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void showWarehouses(TextView lblSelectedBranch, String title, String vResult,String ifValue,String ifKey) {
        AlertDialog _dialog = null;
        AlertDialog.Builder dialogSelectWarehouse = new AlertDialog.Builder(API_SelectedItems.this);
        dialogSelectWarehouse.setTitle("Select " + (title.equals("Branch") ? "Department" : title));
        dialogSelectWarehouse.setCancelable(false);
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(40, 40, 40, 40);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout textInputLayout = new TextInputLayout(API_SelectedItems.this);
        textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CLEAR_TEXT);
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setBoxStrokeColor(Color.parseColor("#1687a7"));
        textInputLayout.setHint("Search " + (title.equals("Branch") ? "Department" : title));

        AutoCompleteTextView txtSearchBranch = new AutoCompleteTextView(getBaseContext());
        txtSearchBranch.setTextSize(15);
        textInputLayout.addView(txtSearchBranch);
        layout.addView(textInputLayout);


        LinearLayout.LayoutParams layoutParamsLa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout la = new LinearLayout(API_SelectedItems.this);
        la.setWeightSum(2f);
        la.setOrientation(LinearLayout.HORIZONTAL);
        la.setLayoutParams(layoutParamsLa);

        final List<String>[] warehouses = new List[]{returnBranches(title, vResult, ifKey, ifValue)};
        final ArrayList<String>[] myReference = new ArrayList[]{getReference(warehouses[0], txtSearchBranch.getText().toString().trim())};
        final ArrayList<String>[] myID = new ArrayList[]{getID(warehouses[0], txtSearchBranch.getText().toString().trim())};
        final List<String>[] listItems = new List[]{getListItems(warehouses[0])};


        MaterialButton btnSearchBranch = new MaterialButton(API_SelectedItems.this);
        btnSearchBranch.setCornerRadius(20);
        LinearLayout.LayoutParams layoutParamsBtn = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        btnSearchBranch.setLayoutParams(layoutParamsBtn);
        btnSearchBranch.setBackgroundResource(R.color.colorPrimary);
//        btnSearchBranch.setPadding(20,20,20,20);
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
                MyAdapter adapter = new MyAdapter(API_SelectedItems.this, myReference[0], myID[0]);
                listView.setAdapter(adapter);
            }
        });

        AppCompatButton btnRefresh = new AppCompatButton(API_SelectedItems.this);
//        btnRefresh.setCornerRadius(20);
        LinearLayout.LayoutParams layoutParamsRefresh = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        layoutParamsRefresh.setMargins(10, 0, 0, 0);
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

        LinearLayout.LayoutParams layoutParamsWarehouses = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 300);
        layoutParamsWarehouses.setMargins(10, 10, 10, 10);
        listView.setLayoutParams(layoutParamsWarehouses);

        txtSearchBranch.setAdapter(fillItems(listItems[0]));
        API_SelectedItems.MyAdapter adapter = new API_SelectedItems.MyAdapter(API_SelectedItems.this, myReference[0], myID[0]);

//        if(isShowAvailableQty) {
//            Button btn = new Button(getBaseContext());
//            btn.setText("View Available Qty Per Whse");
//            btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode);
//                    if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
//                        showAvailabelQuantity.execute("");
//                    }
//                }
//            });
//
//            btn.setBackgroundResource(R.color.colorAccent);
//            LinearLayout.LayoutParams lay = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            lay.setMargins(0, 5, 0, 0);
//            btn.setLayoutParams(lay);
//            btn.setTextColor(Color.WHITE);
//            layout.addView(btn);
//        }

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
                if (fullName.equals("Offline Mode")) {
                    Toast.makeText(getBaseContext(), "You can't refresh " + title + " because you are in offline mode!", Toast.LENGTH_SHORT).show();
                } else {
                    final_dialog2.dismiss();
                    getDataFromDownload downloadd = new getDataFromDownload(title, lblSelectedBranch, title, ifValue, ifKey, true);
                    downloadd.execute("");
                }
            }
        });

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
                        lblSelectedBranch.setText(textView1.getText().toString());
                        lblSelectedBranch.setTag(textView1.getText().toString());
                        if (title.equals("FG Item") && (hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Transfer Item"))) {
                            if (!gFGItem.trim().isEmpty()) {
                                if (gFGItem.startsWith("{")) {
                                    if (lblFGUOM != null) {
                                        lblFGUOM.setText(findWarehouseCode(gFGItem, textView1.getText().toString(), "item_code", "uom").trim());
                                    }
                                }
                            }
                        }
                        final_dialog.dismiss();
                    }
                });
            }
        });
        layout.addView(listView);
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
                    if (temp.trim().toLowerCase().equals(value.trim().toLowerCase())) {
                        result.add(temp);
                    }
                }else{
                    result.add(temp);
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
                    if (temp.trim().toLowerCase().equals(value.trim().toLowerCase())) {
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

    private class showAvailabelQuantity extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gItemCode = "",gItemName = "";
        Object[] gO = null;
        public showAvailabelQuantity(String itemCode,String itemName, Object[] o){
            gItemCode = itemCode;
            gItemName = itemName;
            gO = o;
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
                    if (module.trim().toLowerCase().contains("per warehouse")) {
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
                        for (int i = 0; i < jaData.length(); i++) {
                            JSONObject joData = jaData.getJSONObject(i);
                            itemCode = joData.has("item_code") ? joData.isNull("item_code") ? "" : joData.getString("item_code") : "";
                            if (itemCode.trim().toLowerCase().contains(gItemCode.trim().toLowerCase())) {
                                double qty = joData.has("quantity") ? joData.isNull("quantity") ? 0.00 : joData.getDouble("quantity") : 0.00;
                                double stockAge = joData.has("stock_age") ? joData.isNull("stock_age") ? 0.00 : joData.getDouble("stock_age") : 0.00;

                                String sWhse = joData.has("warehouse") ? joData.isNull("warehouse") ? "" : joData.getString("warehouse") : "";
                                String sVessel = joData.has("vessel") ? joData.isNull("vessel") ? "" : joData.getString("vessel") : "";
                                arrayList.add("Warehouse: " + sWhse + "\nQty: " + df.format(qty) + "\nVessel: " + sVessel + "\nStock Age: " + df.format(stockAge));
                            }
                        }

                        AlertDialog.Builder dialog = new AlertDialog.Builder(API_SelectedItems.this);
                        dialog.setTitle(gItemName);
                        dialog.setCancelable(false);

                        LinearLayout layout = new LinearLayout(getBaseContext());
                        layout.setPadding(20, 20, 20, 10);
                        layout.setOrientation(LinearLayout.VERTICAL);

                        ListView listView = new ListView(getBaseContext());
                        layout.addView(listView);
                        ArrayAdapter adapter = new ArrayAdapter(API_SelectedItems.this, android.R.layout.simple_list_item_1, arrayList);
                        listView.setAdapter(adapter);

                        dialog.setView(layout);

                        String positiveText = !(hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")) ? "Ok" : "Cancel";
                        dialog.setPositiveButton(positiveText, null);

                        AlertDialog alertDialog = dialog.show();

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")) {
//                                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


                                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                                    String a111 = text.getText().toString();
                                    int firstWordIndex = a111.indexOf("Warehouse: ");
                                    int twoWordIndex = a111.indexOf("\nVessel");
                                    String getWhse = a111.substring(firstWordIndex, twoWordIndex);
                                    getWhse = getWhse.replace("Warehouse: ", "").trim();

                                    String fromWhse = getWhse.replace(getWhse.substring(getWhse.indexOf("\n")), "");
                                    double quantity = 0;
                                    try {
                                        quantity = Double.parseDouble(getWhse.substring(getWhse.indexOf("\n"), getWhse.length()).replace("\nQty: ", "").replace(",", ""));
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

//                                        ClipData clip = ClipData.newPlainText("Quantity copied!",df.format(quantity));
//                                        clipboard.setPrimaryClip(clip);
                                    String selectedWhse = "";
                                    alertDialog.dismiss();
                                    editDialog2((int) gO[0], (String) gO[1], (String) gO[2], quantity, (double) gO[4], fromWhse);
//                                        String finalItem, double finalPrice, Integer finalDocEntry, String finalSupplier, double quantity, int store_quantity, int auditor_quantity, int variance_quantity, String uom, String uomGroup, LinearLayout linearLayout, String itemCode
//                                        if(gO != null){
//                                            anotherFunction(String.valueOf(gO[0]),(double) gO[1],(int) gO[2],String.valueOf( gO[3]),quantity,(int) gO[5],(int)gO[6], (int) gO[7],String.valueOf(gO[8]),String.valueOf(gO[9]),(LinearLayout) gO[10],String.valueOf(gO[11]),fromWhse);
//                                        }else{
//                                            System.out.println("quantity " + quantity + "/" + fromWhse);
//                                            anotherFunction2((int) gO2[0],(String) gO2[1], (String) gO2[2],(double)gO2[3],(String) gO2[4],(boolean)gO2[5],(int)gO2[6],(String)gO2[7],(int)gO2[8],(boolean)gO2[9],(int)gO2[10],(String)gO2[11],(LinearLayout)gO2[12], fromWhse,quantity);
//                                        }
                                }
                            }
                        });

                        Button btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        AlertDialog finalAlertDialog1 = alertDialog;
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finalAlertDialog1.dismiss();
//                                    public void editDialog2(int id, String itemName,String itemCode, double actualQty, double finalQuantity){
                                editDialog2((int) gO[0], (String) gO[1], (String) gO[2], (double) gO[3], (double) gO[4], (String) gO[5]);
//                                    if(gO != null && (hidden_title.equals("API Issue For Production") || hidden_title.equals("API Issue For Packing") || hidden_title.equals("API Transfer Item"))) {
//                                        anotherFunction(String.valueOf(gO[0]), (double) gO[1], (int) gO[2], String.valueOf(gO[3]), (double)gO[4], (int) gO[5], (int) gO[6], (int) gO[7], String.valueOf(gO[8]), String.valueOf(gO[9]), (LinearLayout) gO[10], String.valueOf(gO[11]), String.valueOf(gO[12]));
//                                    }else{
//                                        anotherFunction2((int) gO2[0],(String) gO2[1], (String) gO2[2],(double)gO2[3],(String) gO2[4],(boolean)gO2[5],(int)gO2[6],(String)gO2[7],(int)gO2[8],(boolean)gO2[9],(int)gO2[10],(String)gO2[11],(LinearLayout)gO2[12], (String)gO2[13],(double)gO2[14]);
//                                    }
                            }
                        });


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

    public ArrayAdapter<String> fillItems(List<String> items){
        return new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, items);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context rContext;
        ArrayList<String> myReference;
        ArrayList<String> myIds;

        MyAdapter(Context c, ArrayList<String> reference, ArrayList<String> id) {
            super(c, R.layout.custom_list_view_sales_logs, R.id.txtReference, reference);
            this.rContext = c;
            this.myReference = reference;
            this.myIds = id;
            this.myIds = id;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.custom_list_view_sales_logs, parent, false);
            TextView textView1 = row.findViewById(R.id.txtReference);
            TextView textView2 = row.findViewById(R.id.txtIDs);
            TextView textView3 = row.findViewById(R.id.txtAmount);
            textView1.setText(myReference.get(position));
            textView2.setText(myIds.get(position));
            textView2.setVisibility(GONE);
            textView3.setVisibility(GONE);
            return row;
        }
    }

    public void changePassword(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(API_SelectedItems.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                    builder.setMessage("Are you sure want to submit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    API_SelectedItems.myChangePassword myChangePassword = new API_SelectedItems.myChangePassword(txtPassword.getText().toString().trim());
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
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                        builder.setMessage("We redirect you to Login Page")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pc.loggedOut(API_SelectedItems.this);
                                        pc.removeToken(API_SelectedItems.this);
                                        startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(getBaseContext(), APIReceived.class);
        intent.putExtra("title", title);
        intent.putExtra("hiddenTitle", hiddenTitle);
        startActivity(intent);
    }

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
                .setPositiveButton("Yes", (dialog, which) -> {
                    pc.loggedOut(API_SelectedItems.this);
                    pc.removeToken(API_SelectedItems.this);
                    startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    public void apiSaveTransferItem(String remarks, String plateNum, String vessel, String driver, String shift, String agiScale, String chtiScale, AlertDialog dialogg){

    }



    private class TransferItem extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gRemarks = "",gPlateNum = "", gDriver = "", gShift = "", gAgiScale = "", gChtiScale = "",gTempering = "",gHashedID= "", gStartDate = "",gEndDate="",gFGItem = "",gFGUom = "";
        AlertDialog gDialog;
        public TransferItem(String remarks, String plateNum, String driver, String shift, String agiScale, String chtiScale,String tempering, AlertDialog dialogg, String hashedID, String startDate, String endDate,String fgItem,String fgUom){
            gRemarks = remarks;
            gPlateNum = plateNum;
            gDriver = driver;
            gShift = shift;
            gAgiScale = agiScale;
            gChtiScale = chtiScale;
            gTempering = tempering;
            gDialog = dialogg;
            gHashedID= hashedID;
            gStartDate = startDate;
            gEndDate = endDate;
            gFGItem = fgItem;
            gFGUom = fgUom;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            JSONObject jsonObject = new JSONObject();
            try {

                // create your json here
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());

                JSONObject objectHeaders = new JSONObject();
//            objectHeaders.put("transtype", "TRFR");
                objectHeaders.put("sap_number", JSONObject.NULL);
                objectHeaders.put("transdate", currentDateandTime);
                objectHeaders.put("remarks", gRemarks);
                objectHeaders.put("reference2", null);
                if(gFGItem.trim().isEmpty() || gFGItem.trim().equals("N/A")){
                    objectHeaders.put("fg_item", JSONObject.NULL);
                }else{
                    objectHeaders.put("fg_item", gFGItem);
                }
                if(gFGUom.trim().isEmpty() || gFGUom.trim().equals("N/A")){
                    objectHeaders.put("fg_uom", JSONObject.NULL);
                }else{
                    objectHeaders.put("fg_uom", gFGUom);
                }
                objectHeaders.put("hashed_id", gHashedID);
                if(!gStartDate.trim().isEmpty()){
                    objectHeaders.put("start_transfer_date", gStartDate);
                }
                if(!gEndDate.trim().isEmpty()){
                    objectHeaders.put("end_transfer_date", gEndDate);
                }
                objectHeaders.put("hashed_id", gHashedID);
                 if (!gPlateNum.equals("N/A") && !gPlateNum.trim().equals("")) {
                     objectHeaders.put("plate_num", gPlateNum);
                }else{
                     objectHeaders.put("plate_num", JSONObject.NULL);
                 }
                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String shift = Objects.requireNonNull(sharedPreferences2.getString("shift", ""));
                if (gShift.equals("N/A") || gShift.trim().equals("")) {
                    objectHeaders.put("shift", JSONObject.NULL);
                }
                else{
                    objectHeaders.put("shift", shift);
                }
                if (gDriver.equals("N/A") || gDriver.trim().equals("")) {
                    objectHeaders.put("driver", JSONObject.NULL);
                }else{
                    objectHeaders.put("driver", gDriver);
                }
                if (!gAgiScale.equals("N/A") || !gAgiScale.trim().equals("")) {
                    objectHeaders.put("agi_truck_scale", gAgiScale);
                }
                if (!gChtiScale.equals("N/A") && !gChtiScale.trim().equals("")) {
                    objectHeaders.put("chti_truck_scale", gChtiScale);
                }
                 if (!gTempering.equals("N/A") && !gTempering.trim().equals("")) {
                     objectHeaders.put("tempering_time", Double.parseDouble(gTempering));
                }
                jsonObject.put("header", objectHeaders);

                JSONArray arrayDetails = new JSONArray();

                Cursor cursor = myDb4.getAllData(title);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        JSONObject objectDetails = new JSONObject();
                        objectDetails.put("item_code", cursor.getString(6));
                        objectDetails.put("from_whse", cursor.getString(7));
                        objectDetails.put("to_whse", cursor.getString(8));
                        objectDetails.put("quantity", cursor.getDouble(2));
                        objectDetails.put("uom", cursor.getString(5));
                        arrayDetails.put(objectDetails);
                    }
                    jsonObject.put("details", arrayDetails);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("transfer " + jsonObject);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPaddress = sharedPreferences2.getString("IPAddress", "");

            String sURL = IPaddress + "/api/inv/trfr/new";
            String method = "POST";
            String bodyy = jsonObject.toString();
            String fromModule = title;
            String hiddenFromModule = hiddenTitle;

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(sURL)
                    .method(method, body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isInserted = myDb7.insertData(sURL,method, bodyy, fromModule, hiddenFromModule,currentDate);
                            String msg = "The data is " + (isInserted ? "inserted to" : "failed to insert in") + " local database";
                            if(isInserted){
                                gDialog.dismiss();
                                loadingDialog.dismissDialog();
                                myDb4.truncateTable();
                                loadItems();
                            }
                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                            builder.setCancelable(false);
                            builder.setTitle("Message");
                            builder.setMessage(msg);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loadItems();
                                }
                            });
                            builder.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    formatResponse(response.body().string(),gDialog);
                    loadingDialog.dismissDialog();
                }
            });
            return null;
        }

                @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
            super.onPostExecute(s);
        }
    }


    public void apiItemRequest(String remarks,String fromBranch,AlertDialog dialogg){

    }

    private class ItemRequest extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gRemarks = "",gFromBranch = "", gDueDate = "",gHashedID= "";
        AlertDialog gDialog;
        public ItemRequest(String remarks,String fromBranch,String dueDate, AlertDialog dialogg, String hashedID){
            gRemarks = remarks;
            gFromBranch = fromBranch;
            gDueDate = dueDate;
            gDialog = dialogg;
            gHashedID= hashedID;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            JSONObject jsonObject = new JSONObject();
            try {
                // create your json here
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdf.format(new Date());
                JSONObject objectHeaders = new JSONObject();
                objectHeaders.put("transdate", currentDateandTime);

                jsonObject.put("header", objectHeaders);
                objectHeaders.put("reference2", null);
                objectHeaders.put("remarks", gRemarks);
                objectHeaders.put("duedate", gDueDate);
                objectHeaders.put("hashed_id", gHashedID);
//                objectHeaders.put("from_branch", gFromBranch);
                jsonObject.put("header", objectHeaders);

                JSONArray arrayDetails = new JSONArray();

                Cursor cursor = myDb4.getAllData(title);

//            SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
//            String branch = Objects.requireNonNull(sharedPreferences2.getString("whse", ""));
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        JSONObject objectDetails = new JSONObject();
                        objectDetails.put("item_code", cursor.getString(6));
                        objectDetails.put("quantity", cursor.getDouble(2));
                        objectDetails.put("uom", cursor.getString(5));
                        objectDetails.put("from_branch", gFromBranch);
                        arrayDetails.put(objectDetails);
                    }
                    jsonObject.put("rows", arrayDetails);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("body " + jsonObject);
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPAddress = sharedPreferences2.getString("IPAddress", "");

            String sURL = IPAddress + "/api/inv/item_request/new";
            String method = "POST";
            String bodyy = jsonObject.toString();
            String fromModule = title;
            String hiddenFromModule = hiddenTitle;

            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(sURL)
                    .method(method, body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isInserted = myDb7.insertData(sURL,method, bodyy, fromModule, hiddenFromModule,currentDate);
                            if(isInserted){
                                Toast.makeText(getBaseContext(),  "The data is inserted to local database", Toast.LENGTH_SHORT).show();
                                myDb4.truncateTable();
                                gDialog.dismiss();
                                loadingDialog.dismissDialog();
                                loadItems();
                            }else{
                                Toast.makeText(getBaseContext(),  "Your data is failed to insert in local database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    formatResponse(response.body().string(), gDialog);
                    loadingDialog.dismissDialog();
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
        }

    }


    public int returnDate(String value){
        int result = 0;
        SimpleDateFormat y = new SimpleDateFormat(value, Locale.getDefault());
        result = Integer.parseInt(y.format(new Date()));
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDatePickerDialog(TextView lbl) {
        Date d = null;
        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
            d = format.parse(lbl.getText().toString().trim());
        }catch (Exception ex){
            ex.printStackTrace();
        }
        DatePickerDialog datePickerDialog;
        if(d == null){
            datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (DatePickerDialog.OnDateSetListener) this, Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }else{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(d);
            int year = calendar.get(Calendar.YEAR);
//Add one to month {0 - 11}
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            System.out.println("eee: " + year + "/" + month + "/" + day);
            datePickerDialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, (DatePickerDialog.OnDateSetListener) this, year, month, day);
        }

        if(hiddenTitle.equals("API Transfer Item")) {

            final Calendar calendar = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            //Min date setting part
            cal.set(Calendar.MONTH, mm);
            cal.set(Calendar.DAY_OF_MONTH, dd-1);
            cal.set(Calendar.YEAR, yy);
            datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());

            SimpleDateFormat sdff = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            String currentDateandTime = sdff.format(new Date());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar iCalendar = Calendar.getInstance();
            try {
                iCalendar.setTime(sdf.parse(currentDateandTime));// all done
            } catch (ParseException e) {
                e.printStackTrace();
            }
            iCalendar.add(Calendar.DAY_OF_MONTH, 1);
            datePickerDialog.getDatePicker().setMaxDate(iCalendar.getTimeInMillis());




        }

        datePickerDialog.show();

        lblGDate = lbl;
    }

    public void showTime(TextView lbl) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = 0, minute = 0;
        if(lbl.getText().toString().substring(0,2).trim().equals("--")){
             hour =mcurrentTime.get(Calendar.HOUR_OF_DAY);
        }else{
            System.out.println("hour: " + lbl.getText().toString().substring(0,2).trim());
            hour = Integer.parseInt(lbl.getText().toString().substring(0,2).trim());
        }
        if(lbl.getText().toString().substring(3,5).trim().equals("--")){
            minute =mcurrentTime.get(Calendar.MINUTE);
        }else{
            System.out.println("hour: " + lbl.getText().toString().substring(3,5).trim());
            minute = Integer.parseInt(lbl.getText().toString().substring(3,5).trim());
        }
        TimePickerDialog mTimePicker;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        mTimePicker = new TimePickerDialog(API_SelectedItems.this,TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                lbl.setText(selectedHour + ":" + selectedMinute);

                Calendar calendar = Calendar.getInstance(Locale.getDefault());
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                calendar.set(Calendar.MINUTE, selectedMinute);
                lbl.setText(sdf.format(calendar.getTime()));
            }

        }, hour, minute,true);
        mTimePicker.setTitle("Select 24-hour format");
        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        mTimePicker.show();

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        DecimalFormat dff = new DecimalFormat("#,#00.###");
        lblGDate.setText(dff.format(month + 1) + "-" + dff.format(dayOfMonth) + "-" + year);
    }


    private class ManualReceived extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gFromBranch = "",gToBranch = "", gRemarks = "", gSupplier = "", gPlateNum = "", gVessel = "", gDriver = "", gShift = "", gAgiScale = "", gChtiScale = "",gHashedID = "";
        AlertDialog gDialog;
        public ManualReceived(String fromBranch,String toBranch, String remarks,String supplier,String plateNum,String vessel, String driver,String shift, String agiScale, String chtiScale,AlertDialog dialogg, String hashedID){
            gFromBranch = fromBranch;
            gToBranch = toBranch;
            gRemarks = remarks;
            gSupplier = supplier;
            gPlateNum = plateNum;
            gVessel = vessel;
            gDriver = driver;
            gShift = shift;
            gAgiScale = agiScale;
            gChtiScale = chtiScale;
            gDialog = dialogg;
            gHashedID= hashedID;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
            String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
            JSONObject jsonObject = new JSONObject();
            try {
                // create your json here
                SimpleDateFormat sdff = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String currentDateandTime = sdff.format(new Date());

                JSONObject objectHeaders = new JSONObject();
                objectHeaders.put("transtype", "MNL");
                objectHeaders.put("transfer_id", null);
                objectHeaders.put("sap_number", JSONObject.NULL);
                objectHeaders.put("transdate", currentDateandTime);
                objectHeaders.put("remarks", gRemarks);
                objectHeaders.put("reference2", null);
                objectHeaders.put("supplier", gSupplier.isEmpty() ? JSONObject.NULL : gSupplier);
                objectHeaders.put("type2", "N/A");
                if (!gPlateNum.equals("N/A") && !gPlateNum.trim().equals("")) {
                    objectHeaders.put("plate_num", gPlateNum);
                }
                if (!gShift.equals("N/A") && !gShift.trim().equals("")) {
                    objectHeaders.put("shift", gShift);
                }
                if (!gVessel.equals("N/A") && !gVessel.trim().equals("")) {
                    objectHeaders.put("vessel", gVessel);
                }
                if (!gDriver.equals("N/A") && !gDriver.trim().equals("")) {
                    objectHeaders.put("driver", gDriver);
                }
                if (!gAgiScale.equals("N/A") && !gAgiScale.trim().equals("")) {
                    objectHeaders.put("agi_truck_scale", gAgiScale);
                }
                if (!gChtiScale.equals("N/A") && !gChtiScale.trim().equals("")) {
                    objectHeaders.put("chti_truck_scale", gChtiScale);
                }
                objectHeaders.put("hashed_id", gHashedID);
                jsonObject.put("header", objectHeaders);
                JSONArray arrayDetails = new JSONArray();
                Cursor cursor = myDb4.getAllData(title);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        JSONObject objectDetails = new JSONObject();
                        objectDetails.put("item_code", cursor.getString(6));
                        objectDetails.put("from_whse", gFromBranch.equals("N/A") ? JSONObject.NULL : gFromBranch);
                        objectDetails.put("to_whse", gToBranch.equals("N/A") ? JSONObject.NULL : gToBranch);
//                    objectDetails.put("to_whse", null);
                        objectDetails.put("quantity", cursor.getDouble(2));
                        objectDetails.put("actualrec", cursor.getDouble(2));
                        objectDetails.put("uom", cursor.getString(5));
                        arrayDetails.put(objectDetails);
                    }
                    jsonObject.put("details", arrayDetails);
                }
            } catch (JSONException e) {
                btnProceed.setEnabled(true);
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");


            System.out.println("manual receive " + jsonObject);

            SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
            String IPaddress = sharedPreferences2.getString("IPAddress", "");

            String sURL = IPaddress + "/api/inv/recv/new";
            String method = "POST";
            String bodyy = jsonObject.toString();
            String fromModule = title;
            String hiddenFromModule = hiddenTitle;
//        System.out.println("body: " + jsonObject);
            RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(sURL)
                    .method("POST", body)
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String currentDate = sdf.format(new Date());
                            boolean isInserted = myDb7.insertData(sURL, method, bodyy, fromModule, hiddenFromModule, currentDate);
                            String msg = "The data is " + (isInserted ? "inserted to" : "failed to insert in") + "local database";

                            if (isInserted) {
                                myDb4.truncateTable();
                                gDialog.dismiss();
                            }

                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                            builder.setCancelable(false);
                            builder.setTitle(isInserted ? "Message" : "Validation");
                            builder.setMessage(msg);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            if(isInserted){
                                loadItems();
                            }

                            btnProceed.setEnabled(true);
                            loadingDialog.dismissDialog();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    String answer = response.body().string();
                    formatResponse(answer, gDialog);
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
        }
    }

    public void apiSaveIssueForProduction(String fromBranch, String remarks,String shift, AlertDialog dialogg){
        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
        String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
        JSONObject jsonObject = new JSONObject();
        try {
            // create your json here
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());

            JSONObject objectHeaders = new JSONObject();
            objectHeaders.put("sap_number", JSONObject.NULL);
            objectHeaders.put("transdate", currentDateandTime);
            objectHeaders.put("shift", shift);
            objectHeaders.put("mill", fromBranch);
            objectHeaders.put("remarks", remarks);
            if(hiddenTitle.equals("API Received from Production")){
                DatabaseHelper9 myDb9 = new DatabaseHelper9(getBaseContext());
                String sIssueResult = "";
                Cursor cursor9 = myDb9.getAllData();
                if(cursor9.moveToNext()){
                    sIssueResult = cursor9.getString(3);
                }
                if(!sIssueResult.trim().isEmpty()){
                    if(sIssueResult.startsWith("{")){
                        JSONObject jsonObjectResult = new JSONObject(sIssueResult);
                        JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                        objectHeaders.put("issue_id", jsonObjectData.has("id") ? !jsonObjectData.isNull("id") ? jsonObjectData.getInt("id") : 0 : 0);
                    }
                }
            }
            objectHeaders.put("remarks", remarks);
            jsonObject.put("header", objectHeaders);
            JSONArray arrayDetails = new JSONArray();
            Cursor cursor = myDb4.getAllData(title);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSONObject objectDetails = new JSONObject();
                    objectDetails.put("item_code", cursor.getString(6));
                    objectDetails.put("whsecode", cursor.getString(hiddenTitle.equals("API Received from Production") ? 8 : 7));
                    objectDetails.put("quantity", cursor.getDouble(2));
                    objectDetails.put("uom", cursor.getString(5));
                    arrayDetails.put(objectDetails);
                }
                jsonObject.put("rows", arrayDetails);
            }
        } catch (JSONException e) {
            btnProceed.setEnabled(true);
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
        String IPaddress = sharedPreferences2.getString("IPAddress", "");
        String appendURL = hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") ? "/api/production/issue_for_prod/new" : "/api/production/rec_from_prod/new";
        String sURL = IPaddress +  appendURL;
        String method = "POST";
        String bodyy = jsonObject.toString();
        String fromModule = title;
        String hiddenFromModule = hiddenTitle;
        System.out.println("url " + sURL);
        System.out.println("body " + bodyy);
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(sURL)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String currentDate = sdf.format(new Date());
                        boolean isInserted = myDb7.insertData(sURL,method, bodyy, fromModule, hiddenFromModule,currentDate);
                        btnProceed.setEnabled(true);
                        String msg = "The data is " + (isInserted ? "inserted to" : "failed to insert in") + " local database";
                        if(isInserted){
                            DatabaseHelper9 myDb9 = new DatabaseHelper9(getBaseContext());
                            myDb9.truncateTable();
                            myDb4.truncateTable();
                        }

                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                        builder.setCancelable(false);
                        builder.setTitle("Message");
                        builder.setMessage(msg);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(isInserted){
                                    dialogg.dismiss();
                                    loadItems();
                                }else {
                                    dialog.dismiss();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                String answer = response.body().string();
                formatResponse(answer,dialogg);
            }
        });
    }

    public void formatResponse(String temp,AlertDialog dialogg){
        if(!temp.isEmpty() && temp.startsWith("{")){
            try{
                JSONObject jj = new JSONObject(temp);
                String msg = jj.isNull("message") ? "" : jj.getString("message");
                boolean isSuccess = !jj.isNull("success") && jj.getBoolean("success");
                if (isSuccess) {
                    if( hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")){
                        myDb3.truncateTable();
                    }else {
                        myDb4.truncateTable();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                            builder.setCancelable(false);
                            builder.setTitle("Message");
                            builder.setMessage(msg);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    btnProceed.setEnabled(true);
                                    dialogg.dismiss();
                                    loadItems();
                                }
                            });
                            builder.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnProceed.setEnabled(true);
                            dialogg.dismiss();
                            if(msg.equals("Token is invalid")){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setCancelable(false);
                                builder.setMessage("Your session is expired. Please login again.");
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    pc.loggedOut(API_SelectedItems.this);
                                    pc.removeToken(API_SelectedItems.this);
                                    startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                                    finish();
                                    dialog.dismiss();
                                });
                                builder.show();
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        btnProceed.setEnabled(true);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setTitle("Validation");
                                        builder.setMessage("Error\n" + msg);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                               dialog.dismiss();
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }
                        }
                    });
                }
            }catch (Exception ex){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnProceed.setEnabled(true);
                        ex.printStackTrace();
                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                        builder.setCancelable(false);
                        builder.setTitle("Validation");
                        builder.setMessage("Error\n" + ex.getMessage());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }
        }else{
            System.out.println(temp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnProceed.setEnabled(true);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                    builder.setCancelable(false);
                    builder.setTitle("Validation");
                    builder.setMessage("Error\n" + temp);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    public String getFromSharedPref(String name, String key){
        SharedPreferences sharedPreferences2 = getSharedPreferences(name, MODE_PRIVATE);
        return sharedPreferences2.getString(key, "");
    }

//    public String loadWarehouse(boolean isFromWhse,boolean isGlobal,String title) throws IOException {
//        try{
//            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
//            String currentBranch = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
//            String sParams = isGlobal ? "" : !isFromWhse && hiddenTitle.equals("API Received Item") ? "?branch=" + currentBranch : isFromWhse && hiddenTitle.equals("API Transfer Item") ? "?branch=" +currentBranch : "";
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            String IPAddress = getFromSharedPref("CONFIG", "IPAddress");
//            String token = getFromSharedPref("TOKEN", "token");
//            okhttp3.Request request = new okhttp3.Request.Builder()
//                    .url(IPAddress + (title.equals("Warehouse") ? "/api/whse/get_all" + sParams : title.equals("Branch") ? "/api/branch/get_all" : title.equals("Shift") ? "/api/production/shift/get_all" : "/api/trucks/get_all"))
//                    .method("GET", null)
//                    .addHeader("Authorization", "Bearer " + token)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//            Response response = null;
//            response = client.newCall(request).execute();
//            System.out.println("response: "+ response);
//            return response.body().string();
//        }catch (Exception ex){
//            ex.printStackTrace();
//            Cursor cursor = myDb8.getAllData();
//            String res = "";
//            while (cursor.moveToNext()) {
//                String module = cursor.getString(3);
//                if (module.contains(title)) {
//                    res = cursor.getString(4);
//                }
//            }
//            System.out.println("RESPONSE: " + res);
//            return res;
//        }
//    }

    private class myEditWarehouse extends AsyncTask<String, Void, String> {
        String gTitle = "",gURL = "",gParams = "",gItemCode = "";
        boolean gNeedDialog = false,gIsShowAvailableQty = false;
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        TextView gLbl;
        public myEditWarehouse(String URL, String paramss, String title, boolean needDialog, TextView lbl,String itemCode, boolean isShowAvailableQty ){
            gTitle = title;
            gURL = URL;
            gParams = paramss;
            gNeedDialog = needDialog;
            gLbl = lbl;
            gItemCode = itemCode;
            gIsShowAvailableQty = isShowAvailableQty;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                String sURL = gURL + gParams;
                utility_class utilityc = new utility_class();

                SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String bearerToken = Objects.requireNonNull(sharedPreferences2.getString("token", ""));

                OkHttpClient client;
                client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                okhttp3.Request request = null;
                request = new okhttp3.Request.Builder()
                        .url(utilityc.getIPAddress(API_SelectedItems.this) + sURL)
                        .method("GET", null)
                        .addHeader("Authorization", "Bearer " + bearerToken)
                        .build();
                Response response;
                response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception ex){
                ex.printStackTrace();
                Cursor cursor = myDb8.getAllData();
                while (cursor.moveToNext()) {
                    String module = cursor.getString(4);
                    if (module.trim().toLowerCase().contains(gTitle.trim().toLowerCase())) {
                        ex.printStackTrace();
                        return cursor.getString(3);
                    }
                }
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
            if(gNeedDialog){
                showWarehouses(gLbl,gTitle,s,"","");
            }
        }
    }

    private class getDataFromDownload extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gFromModule = "", gTitle = "", gIfValue = "", gIfKey = "";
        TextView gLblSelected;
        boolean gIsRefresh = false;
        int gID = 0;

        public getDataFromDownload(String fromModule, TextView lblSelected, String title, String ifValue, String ifKey, boolean isRefresh) {
            gFromModule = fromModule;
            gTitle = title;
            gIfValue = ifValue;
            gIfKey = ifKey;
            gLblSelected = lblSelected;
            gIsRefresh = isRefresh;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            Cursor cursor = myDb8.getAllData();
            String cURL = "", cMethod = "",cResponse = "";
            while (cursor.moveToNext()) {
                String module = cursor.getString(4);
                System.out.println("moduleeeed " + module + "/" + gFromModule + "/" + gIsRefresh);
                if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase()) && !gIsRefresh) {
                    System.out.println("first last");
                    cResponse = cursor.getString(3);
                    return cursor.getString(3);
                } else if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase()) && gIsRefresh) {
                    cURL = cursor.getString(1);
                    cMethod = cursor.getString(2);
                    cResponse = cursor.getString(3);
                    gID = cursor.getInt(0);
                }
            }
            if (gIsRefresh && !cURL.trim().isEmpty() && !cMethod.trim().isEmpty()) {
                try {
                    utility_class utilityc = new utility_class();
                    SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                    String bearerToken = Objects.requireNonNull(sharedPreferences2.getString("token", ""));

                    OkHttpClient client;
                    client = new OkHttpClient();
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    okhttp3.Request request = null;
                    request = new okhttp3.Request.Builder()
                            .url(utilityc.getIPAddress(API_SelectedItems.this) + cURL)
                            .method(cMethod, null)
                            .addHeader("Authorization", "Bearer " + bearerToken)
                            .build();
                    Response response;
                    response = client.newCall(request).execute();
                    System.out.println("second last");
                    return response.body().string();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    API_SelectedItems.this.runOnUiThread(() -> {
                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    });

                    while (cursor.moveToNext()) {
                        String module = cursor.getString(4);
                        if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase())) {
                            ex.printStackTrace();
                            System.out.println("third last");
                            return cursor.getString(3);
                        }
                    }
                    return "{}";
                }
            }else{
//                while (cursor.moveToNext()) {
//                    String module = cursor.getString(3);
//                    if (module.trim().toLowerCase().equals(gFromModule.trim().toLowerCase())) {
//                        System.out.println("second last");
//                        return cursor.getString(4);
//                    }
//                }
                return cResponse;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.trim().equals("{}") && gIsRefresh && gID > 0) {
                int i = myDb8.updateResponse(String.valueOf(gID), s);
                System.out.println("update response: " + i);
            }
            if((hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Transfer Item")) && gTitle.equals("FG Item")){
                System.out.println("walang fg item :(");
                gFGItem = s;
            }
            showWarehouses(gLblSelected, gTitle, s, gIfValue, gIfKey);
            loadingDialog.dismissDialog();
        }
    }

    private class myWarehouse extends AsyncTask<String, Void, String> {
        String gitle = "";
        boolean isGlobal = false, isFromWhse = false, gNeedDialog = false;
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        TextView gLbl;

        public myWarehouse(boolean pIsFromWhse, boolean pIsGlobal, String pTitle, boolean needDialog, TextView lbl) {
            gitle = pTitle;
            isGlobal = pIsGlobal;
            isFromWhse = pIsFromWhse;
            gNeedDialog = needDialog;
            gLbl = lbl;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String currentBranch = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                String currentPlant = Objects.requireNonNull(sharedPreferences.getString("plant", ""));
                String sParams = isGlobal ? "" : !isFromWhse && hiddenTitle.equals("API Received Item") ? "?branch=" + currentBranch : isFromWhse && hiddenTitle.equals("API Transfer Item") ? "?branch=" + currentBranch : !hiddenTitle.equals("API Received Item") ? "?plant=" + currentPlant : "";

                String sURL = (gitle.equals("Mill") ? "/api/mill/get_all" : gitle.equals("Warehouse") ? "/api/whse/get_all" + sParams : gitle.equals("Branch") ? "/api/branch/get_all?plant=" + currentPlant : gitle.equals("Shift") ? "/api/production/shift/get_all" : gitle.equals("Driver") ? "/api/driver/get_all" : gitle.equals("Vessel") ? "/api/vessel/get_all" : gitle.equals("FG Item") ? "/api/item/getall?in_item_group=FG - Soft Flour&in_item_group=FG - Hard Flour&in_item_group=By-Products&in_item_group=Specialty Flour" : gitle.equals("Truck") ? "/api/trucks/get_all" : "");
                utility_class utilityc = new utility_class();

                System.out.println("sURL " + sURL);

                SharedPreferences sharedPreferences2 = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String bearerToken = Objects.requireNonNull(sharedPreferences2.getString("token", ""));

                OkHttpClient client;
                client = new OkHttpClient();
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                okhttp3.Request request = null;
                request = new okhttp3.Request.Builder()
                        .url(utilityc.getIPAddress(API_SelectedItems.this) + sURL)
                        .method("GET", null)
                        .addHeader("Authorization", "Bearer " + bearerToken)
                        .build();
                Response response;
                response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();
                Cursor cursor = myDb8.getAllData();
                while (cursor.moveToNext()) {
                    String module = cursor.getString(4);
                    if (module.trim().toLowerCase().contains(gitle.trim().toLowerCase())) {
                        ex.printStackTrace();
                        return cursor.getString(3);
                    }
                }
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (gNeedDialog) {
                if((hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Transfer Item")) && gitle.equals("FG Item")){
                    System.out.println("walang fg item :(");
                    gFGItem = s;
                }
                showWarehouses(gLbl, gitle, s,"","");
            }
            else if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
                gBranch = s;
            } else if (hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item")) {
                gBranch = s;
            } else if (hiddenTitle.equals("API Item Request")) {
                gBranchh = s;
            }
            if ((hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) && gitle.equals("Mill")) {
                gMill = s;
            }
            loadingDialog.dismissDialog();
        }
    }

    public String findWarehouseCode(String globalResult, String value,String keyName,String keyFind){
        String result = "";
        try{
            if(globalResult != null || !globalResult.isEmpty()){
                System.out.println("globee " + globalResult);
                if(globalResult.startsWith("{")){
                    JSONObject jsonObjectResponse = new JSONObject(globalResult);
                    JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if(jsonObject.getString(keyName).equals(value)){
                            result = jsonObject.getString(keyFind);
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

    public String findKey(String value, String conditionName,String findName, String vResult){
        String result = "";
        try{
            if(vResult != null || !vResult.isEmpty()){
                if(vResult.startsWith("{")){
                    JSONObject jsonObjectResponse = new JSONObject(vResult);
                    JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String conName = jsonObject.getString(conditionName).toLowerCase().trim();
                        if(conName.equals(value.toLowerCase().trim())){
                            result = jsonObject.getString(findName);
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

    public List<String> returnBranches(String title, String value,String ifValue,String ifKey) {
        System.out.println("value " + value);
        List<String> result = new ArrayList<>();
//        System.out.println(gBranch);
        try {
            if (value.startsWith("{")) {
                JSONObject jsonObjectResponse = new JSONObject(value);
                if (jsonObjectResponse.has("success")) {
                    if (jsonObjectResponse.getBoolean("success")) {
                        JSONArray jsonArray = jsonObjectResponse.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            String branch = jsonObject.getString("whsecode") + "," + jsonObject.getString("whsename");
                            if (!ifValue.trim().isEmpty()) {
                                if (jsonObject.getString(ifKey).toLowerCase().trim().equals(ifValue.toLowerCase().trim())) {
                                    String branch = jsonObject.getString(title.equals("Mill") ? "name" : title.equals("Truck") ? "plate_num" : title.equals("Branch") ? "name" : title.equals("Shift") ? "code" : title.equals("Driver") ? "fullname" : title.equals("Vessel") ? "name" : title.equals("FG Item") ? "item_code" : "whsename");
                                    result.add(branch);
                                }
                            } else {
                                String branch = jsonObject.getString(title.equals("Mill") ? "name" : title.equals("Truck") ? "plate_num" : title.equals("Branch") ? "name" : title.equals("Shift") ? "code" : title.equals("Driver") ? "fullname" : title.equals("Vessel") ? "name" : title.equals("FG Item") ? "item_code" : "whsename");
                                result.add(branch);
                            }

                        }
                    } else {
                        Toast.makeText(getBaseContext(), "Error fetching " + title + "\n" + jsonObjectResponse.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getBaseContext(), "Error fetching " + title + "\n" + value, Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getBaseContext(), "Error fetching " + title + "\n" + value, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        if (result.isEmpty()) {
            Toast.makeText(getBaseContext(), "No data found for " + title, Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    @SuppressLint({"SetTextI18n", "RtlHardcoded"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void loadItems() {
        Cursor cursor;
        int count = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API System Transfer Item") ? myDb3.countItems(hiddenTitle) : myDb4.countItems(title));
//                int count = myDb4.countItems(title);

        if (hiddenTitle.equals("API Received from SAP")) {
            count = myDb3.countSAPItems(hiddenTitle);
        } else if (hiddenTitle.equals("API Inventory Count")) {
            count = myDb3.countItems(hiddenTitle);
        } else if (hiddenTitle.equals("API Pull Out Count")) {
            count = myDb3.countItems(hiddenTitle);
        } else if (hiddenTitle.equals("API System Transfer Item")) {
            count = myDb3.countItemsSelected(hiddenTitle);
        } else if (hiddenTitle.equals("API Received from Production")) {
            count = myDb4.countItems(title);
        } else if (hiddenTitle.equals("API Item Request For Transfer")) {
            count = myDb3.countItemsSelected(hiddenTitle);
        } else {
            count = myDb4.countItems(title);
        }

        LinearLayout layout = findViewById(R.id.layoutNoItems);
        final TableLayout tableLayout = findViewById(R.id.table_main);
        System.out.println(count);
        if (count == 0) {
            tableLayout.removeAllViews();
            layout.setVisibility(VISIBLE);
            Button btnGoto = findViewById(R.id.btnGoto);
            btnGoto.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                finish();
            });
            btnProceed.setVisibility(GONE);

            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            layout.setVisibility(VISIBLE);
        } else {
            layout.setVisibility(GONE);

            btnProceed.setVisibility(VISIBLE);
            loadDialog();
            TableRow tableColumn = new TableRow(API_SelectedItems.this);
            tableColumn.setBackgroundColor(Color.rgb(245, 245, 245));
            LinearLayout.LayoutParams layoutParamsTableColumn = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1.0f);
            tableColumn.setLayoutParams(layoutParamsTableColumn);
            tableLayout.removeAllViews();
            System.out.println("TITLE: " + hiddenTitle);
            String[] columns = null;

            if(hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")){
                columns =  new String[]{"Item", "Del. Qty.", "Act. Qty.", "Var.", "Action","Action"};
            }
            else if( hiddenTitle.equals("API Item Request")){
                columns = new String[]{"Item", "Qty.","Action"};
            }
            else if(hiddenTitle.equals("API Transfer Item")){
                columns = new String[]{"Item", "Qty.", "Uom","From Whse","To Whse","Action","Action"};
            }
            else if(hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")){
                columns = new String[]{"Item", "Qty.", "Uom","From Whse", "Action"};
            }
            else if(hiddenTitle.equals("API Received from Production")){
                columns = new String[]{"Item", "Qty.", "Uom","To Whse", "Action"};
            }
            else {
                columns = new String[]{"Item", "Qty.", "Uom", "Action","Action"};
            }

            for (String s : columns) {
                TextView lblColumn1 = new TextView(API_SelectedItems.this);
                lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblColumn1.setText(s);
                lblColumn1.setPadding(10, 0, 10, 0);
                tableColumn.addView(lblColumn1);
            }
            tableLayout.addView(tableColumn);
            cursor = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Item Request For Transfer") ? myDb3.getAllSelectedData(hiddenTitle) : myDb4.getAllData(title));

//                    cursor = myDb4.getAllData(title);

            if (cursor != null) {
                while (cursor.moveToNext()) {

                    if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")) {
                        if (cursor.getInt(6) == 1) {
                            final TableRow tableRow = new TableRow(API_SelectedItems.this);
                            tableRow.setBackgroundColor(Color.WHITE);
                            String itemName = cursor.getString((hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Item Request For Transfer") ? 3 : 1));
                            String v = cutWord(itemName);
                            double quantity = 0.00;

                            if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Received from Production")|| hiddenTitle.equals("API Item Request For Transfer")) {
                                quantity = cursor.getDouble(4);
                            } else if (hiddenTitle.equals("API Inventory Count") || hiddenTitle.equals("API Pull Out Count")) {
                                quantity = cursor.getDouble(5);
                            } else {
                                quantity = cursor.getDouble(2);
                            }

                            final int id = cursor.getInt(0);

                            LinearLayout linearLayoutItem = new LinearLayout(this);
                            linearLayoutItem.setPadding(10, 10, 10, 10);
                            linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                            linearLayoutItem.setBackgroundColor(Color.WHITE);
                            linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            tableRow.addView(linearLayoutItem);

                            //item
                            LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            TextView lblColumn1 = new TextView(this);
                            lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn1.setLayoutParams(layoutParamsItem);
//                       String v = cutWord(item);
                            lblColumn1.setText(itemName);
                            lblColumn1.setTextSize(13);
                            lblColumn1.setBackgroundColor(Color.WHITE);

                            lblColumn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getBaseContext(), itemName, Toast.LENGTH_SHORT).show();
                                }
                            });

                            linearLayoutItem.addView(lblColumn1);

                            //actual quantity
                            TextView lblColumn2 = new TextView(API_SelectedItems.this);
                            lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn2.setText(df.format(quantity));
                            lblColumn2.setTextSize(13);
                            lblColumn2.setBackgroundColor(Color.WHITE);
                            lblColumn2.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn2);

                            if (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Item Request For Transfer")) {
                                TextView lblColumn4 = new TextView(API_SelectedItems.this);
                                lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                lblColumn4.setText(df.format(cursor.getDouble(5)));
                                lblColumn4.setBackgroundColor(Color.WHITE);
                                lblColumn4.setPadding(10, 10, 10, 10);
                                lblColumn4.setTextSize(13);
                                tableRow.addView(lblColumn4);

                                if(hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Item Request For Transfer")){
                                    TextView lblColumn5 = new TextView(API_SelectedItems.this);
                                    lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    double variance = cursor.getDouble(5) - quantity;
                                    lblColumn5.setText(df.format(variance));
                                    lblColumn5.setBackgroundColor(Color.WHITE);
                                    lblColumn5.setTextSize(13);
                                    lblColumn5.setPadding(10, 10, 10, 10);
                                    tableRow.addView(lblColumn5);
                                }
                            }

                            TextView lblColumn3 = new TextView(API_SelectedItems.this);
                            lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn3.setTag(id);
                            lblColumn3.setBackgroundColor(Color.WHITE);
                            lblColumn3.setText("Remove");
                            lblColumn3.setTextSize(13);
                            lblColumn3.setPadding(10, 10, 10, 10);
                            lblColumn3.setTextColor(Color.RED);

                            lblColumn3.setOnClickListener(view -> {
                                boolean deletedItem;
                                deletedItem = (hiddenTitle.equals("API Received from SAP") || hiddenTitle.equals("API System Transfer Item") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Item Request For Transfer")  ? myDb3.removeData(Integer.toString(id)) : myDb3.deleteData(Integer.toString(id)));
                                if (!deletedItem) {
                                    Toast.makeText(API_SelectedItems.this, "Item not remove", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(API_SelectedItems.this, "Item removed", Toast.LENGTH_SHORT).show();
                                    loadItems();
                                }


//                                if (myDb4.countItems(title).equals(0)) {
//                                    tableLayout.removeAllViews();
//                                    btnProceed.setVisibility(View.GONE);
//                                }
                            });

                            tableRow.addView(lblColumn3);

                            TextView lblColumn4 = new TextView(API_SelectedItems.this);
                            lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn4.setTag(id);
                            lblColumn4.setBackgroundColor(Color.WHITE);
                            lblColumn4.setText("Edit");
                            lblColumn4.setTextSize(13);
                            lblColumn4.setPadding(10, 10, 10, 10);
                            lblColumn4.setTextColor(Color.BLUE);
                            double actualQty = cursor.getDouble(5);
                            double finalQuantity = quantity;
                            lblColumn4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    editDialog1(id,itemName,actualQty,finalQuantity);
                                }
                            });
                            tableRow.addView(lblColumn4);
                            tableLayout.addView(tableRow);
                        }
                        View viewLine = new View(this);
                        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        viewLine.setLayoutParams(layoutParamsLine);
                        viewLine.setBackgroundColor(Color.GRAY);
                        tableLayout.addView(viewLine);
                    } else {
                        final TableRow tableRow = new TableRow(API_SelectedItems.this);
                        tableRow.setBackgroundColor(Color.WHITE);
                        String itemName = hiddenTitle.equals("API Item Request For Transfer")  ? cursor.getString(3) : cursor.getString(1);
                        String itemCode= hiddenTitle.equals("API Item Request For Transfer") ? cursor.getString(16) : cursor.getString(6);
                        String v = cutWord(itemName);
                        double quantity = cursor.getDouble(hiddenTitle.equals("API Item Request For Transfer")  ? 5 : 2);
                        final int id = cursor.getInt(0);

                        LinearLayout linearLayoutItem = new LinearLayout(this);
                        linearLayoutItem.setPadding(10, 10, 10, 10);
                        linearLayoutItem.setOrientation(LinearLayout.VERTICAL);
                        linearLayoutItem.setBackgroundColor(Color.WHITE);
                        linearLayoutItem.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        tableRow.addView(linearLayoutItem);

                        LinearLayout.LayoutParams layoutParamsItem = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //item
                        TextView lblColumn1 = new TextView(this);
                        lblColumn1.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn1.setLayoutParams(layoutParamsItem);
                        lblColumn1.setBackgroundColor(Color.WHITE);
                        lblColumn1.setText(itemName);
                        lblColumn1.setTextSize(15);
                        lblColumn1.setBackgroundColor(Color.WHITE);

                        lblColumn1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getBaseContext(), itemName, Toast.LENGTH_SHORT).show();
                            }
                        });

                        linearLayoutItem.addView(lblColumn1);

                        //qty
                        TextView lblColumn2 = new TextView(API_SelectedItems.this);
                        lblColumn2.setBackgroundColor(Color.WHITE);
                        lblColumn2.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn2.setText(df.format(quantity));
                        lblColumn2.setTextSize(15);
                        lblColumn2.setPadding(10, 10, 10, 10);
                        tableRow.addView(lblColumn2);

                        if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Item Request For Transfer")){
                            //uom
                            TextView lblColumn4 = new TextView(API_SelectedItems.this);
                            lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                            lblColumn4.setTag(id);
                            lblColumn4.setBackgroundColor(Color.WHITE);
                            lblColumn4.setText(cursor.getString(hiddenTitle.equals("API Item Request For Transfer")  ? 11 : 5));
                            lblColumn4.setTextSize(13);
                            lblColumn4.setPadding(10, 10, 10, 10);
                            tableRow.addView(lblColumn4);
                            if(!hiddenTitle.equals("API Received Item")){
                                if(hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Item Request For Transfer")){
                                    //from whse
                                    TextView lblColumn5 = new TextView(API_SelectedItems.this);
                                    lblColumn5.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    lblColumn5.setTag(id);
                                    lblColumn5.setBackgroundColor(Color.WHITE);
                                    lblColumn5.setText(cursor.getString(hiddenTitle.equals("API Item Request For Transfer") ? 18 : 7));
                                    lblColumn5.setTextSize(13);
                                    lblColumn5.setPadding(10, 10, 10, 10);
                                    tableRow.addView(lblColumn5);
                                }

                                if(hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Item Request For Transfer")){
                                    //to whse
                                    TextView lblColumn6 = new TextView(API_SelectedItems.this);
                                    lblColumn6.setGravity(View.TEXT_ALIGNMENT_CENTER);
                                    lblColumn6.setTag(id);
                                    lblColumn6.setBackgroundColor(Color.WHITE);
                                    lblColumn6.setText(cursor.getString(hiddenTitle.equals("API Item Request For Transfer") ? 19 : 8));
                                    lblColumn6.setTextSize(13);
                                    lblColumn6.setPadding(10, 10, 10, 10);
                                    tableRow.addView(lblColumn6);
                                }
                            }
                        }
                        //remove
                        TextView lblColumn3 = new TextView(API_SelectedItems.this);
                        lblColumn3.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn3.setBackgroundColor(Color.WHITE);
                        lblColumn3.setTag(id);
                        lblColumn3.setText("Remove");
                        lblColumn3.setTextSize(13);
                        lblColumn3.setPadding(10, 10, 10, 10);
                        lblColumn3.setTextColor(Color.RED);

                        lblColumn3.setOnClickListener(view -> {
                            int deletedItem = 0;
                            if(hiddenTitle.equals("API Item Request For Transfer") ){
                                boolean isSuccess = myDb3.deleteData(Integer.toString(id));
                                deletedItem = isSuccess ?  1 : 0;
                            }else{
//                                if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Transfer Item")){
//                                    myDb4.removeItem(String.valueOf(id),title);
//                                    deletedItem = 1;
//                                }else{
//                                    deletedItem = myDb4.deleteData(Integer.toString(id),title);
//                                }
                                deletedItem = myDb4.deleteData(Integer.toString(id),title);
                            }
                            if (deletedItem <= 0) {
                                Toast.makeText(API_SelectedItems.this, "Item not remove", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(API_SelectedItems.this, "Item removed", Toast.LENGTH_SHORT).show();
//                                Intent intent;
//                                intent = new Intent(getBaseContext(), API_SelectedItems.class);
//                                intent.putExtra("title", title);
//                                intent.putExtra("hiddenTitle", hiddenTitle);
//                                startActivity(intent);
//                                finish();
                                loadItems();
                            }

                            if (myDb4.countItems(title).equals(0)) {
                                tableLayout.removeAllViews();
                                btnProceed.setVisibility(GONE);
                            }
                        });
                        tableRow.addView(lblColumn3);

                        TextView lblColumn4 = new TextView(API_SelectedItems.this);
                        lblColumn4.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        lblColumn4.setTag(id);
                        lblColumn4.setBackgroundColor(Color.WHITE);
                        lblColumn4.setText("Edit");
                        lblColumn4.setTextSize(13);
                        lblColumn4.setPadding(10, 10, 10, 10);
                        lblColumn4.setTextColor(Color.BLUE);
                        double actualQty = cursor.getDouble( hiddenTitle.equals("API Item Request For Transfer") ? 5 : 2);
                        double finalQuantity = quantity;
                        lblColumn4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editDialog2(id,itemName,itemCode,actualQty,finalQuantity,"N/A");
                            }
                        });
                        tableRow.addView(lblColumn4);

                        tableLayout.addView(tableRow);

                        View viewLine = new View(this);
                        LinearLayout.LayoutParams layoutParamsLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                        viewLine.setLayoutParams(layoutParamsLine);
                        viewLine.setBackgroundColor(Color.GRAY);
                        tableLayout.addView(viewLine);
                    }
                }
            }
        }
    }

    public void editDialog2(int id, String itemName,String itemCode, double actualQty, double finalQuantity,String fromWhse){
        AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
        builder.setCancelable(false);
        builder.setTitle("Edit");
        builder.setMessage(itemName);

        ScrollView scrollView = new ScrollView(getBaseContext());
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(20, 20, 20, 20);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout lblQuantity = new TextInputLayout(API_SelectedItems.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        LinearLayout.LayoutParams layoutParamsLblQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsLblQuantity.setMargins(0, 5, 0, 5);
        lblQuantity.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        lblQuantity.setBoxCornerRadii(5, 5, 5, 5);
        lblQuantity.setLayoutParams(layoutParamsLblQuantity);

        TextInputEditText txtQuantity = new TextInputEditText(lblQuantity.getContext());
        LinearLayout.LayoutParams layoutParamsTxtQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtQuantity.setLayoutParams(layoutParamsTxtQuantity);
        txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        DecimalFormat df2 = new DecimalFormat(".000");
        txtQuantity.setText(df2.format(actualQty));
        txtQuantity.setHint("*Enter Quantity");

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
                    double deliveredQty = finalQuantity;
                    double inputQty = lblQuantity.getEditText().getText().toString().trim().isEmpty() || lblQuantity.getEditText().getText().toString().trim().equals(".") ? 0.00 : Double.parseDouble(lblQuantity.getEditText().getText().toString().trim());
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
        Objects.requireNonNull(lblQuantity.getEditText()).setFocusable(true);
        layout.addView(lblQuantity);

        if(hiddenTitle.equals("API Item Request For Transfer")) {
            double variance = actualQty - finalQuantity;


            lblVariance.setText("Variance: " + df.format(variance));
            if (variance == 0) {
                lblVariance.setTextColor(Color.BLACK);
            } else if (variance > 0) {
                lblVariance.setTextColor(Color.BLUE);
            } else if (variance < 0) {
                lblVariance.setTextColor(Color.RED);
            }
            layout.addView(lblVariance);
        }

        if(hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")){

        }

        TextView lblFromSelectedBranch = new TextView(getBaseContext());
        TextView lblToSelectedBranch = new TextView(getBaseContext());

        String cToWhse = "", cFromWhse = "", sSelectedFromWhse = "", sSelectedToWhse = "";
        if (hiddenTitle.equals("API Item Request For Transfer")) {
            Cursor cursor =  myDb3.getAllSelectedData(hiddenTitle);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    cFromWhse = cursor.getString(2);
                    cToWhse = cursor.getString(8);
                    sSelectedFromWhse = cursor.getString(18);
                    sSelectedToWhse = cursor.getString(19);
                }
            }
        }
        else if (hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Received from Production") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")) {
            Cursor cursor =  myDb4.getAllData(title);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    sSelectedFromWhse = cursor.getString(7);
                    sSelectedToWhse = cursor.getString(8);
                }
            }
        }
        Button btn = null;
        AlertDialog show = null;
        if (hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")) {
            LinearLayout.LayoutParams layoutParamsFromBranch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsBranch2 = new LinearLayout.LayoutParams(350, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsBranch3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")) {
                TextView lblFromBranch = new TextView(getBaseContext());
                lblFromBranch.setText("*From Warehouse");
                lblFromBranch.setTextColor(Color.rgb(0, 0, 0));
                lblFromBranch.setTextSize(15);
                lblFromBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblFromBranch);

                LinearLayout layoutFromBranch = new LinearLayout(getBaseContext());
                layoutParamsFromBranch.setMargins(20, 0, 0, 20);
                layoutFromBranch.setLayoutParams(layoutParamsFromBranch);
                layoutFromBranch.setOrientation(LinearLayout.HORIZONTAL);
                layoutParamsBranch3.setMargins(10, 0, 0, 0);

                lblFromSelectedBranch.setText("N/A");
                lblFromSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                lblFromSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                lblFromSelectedBranch.setTextSize(15);
                lblFromSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblFromSelectedBranch.setLayoutParams(layoutParamsBranch2);
                layoutFromBranch.addView(lblFromSelectedBranch);

                TextView btnFromSelectBranch = new TextView(API_SelectedItems.this);
                btnFromSelectBranch.setPadding(20, 10, 20, 10);
                btnFromSelectBranch.setText("...");
                btnFromSelectBranch.setBackgroundResource(R.color.colorPrimary);
                btnFromSelectBranch.setTextColor(Color.WHITE);
                btnFromSelectBranch.setTextSize(13);
                btnFromSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                btnFromSelectBranch.setLayoutParams(layoutParamsBranch3);

                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String currentBranch = Objects.requireNonNull(sharedPreferences2.getString("branch", ""));

                String finalCFromWhse = cFromWhse;
                String sFromParams = hiddenTitle.equals("API Item Request For Transfer") ? "?branch=" + finalCFromWhse : "?branch=" + currentBranch;
                String whseName = findKey(sSelectedFromWhse, "whsecode", "whsename", gBranch);

                lblFromSelectedBranch.setText(fromWhse.trim().isEmpty() || fromWhse.trim().equals("N/A") ? whseName : fromWhse);
                layoutFromBranch.addView(btnFromSelectBranch);
                layout.addView(layoutFromBranch);

                scrollView.addView(layout);

                builder.setPositiveButton("Update", null);
                builder.setNegativeButton("Cancel", null);
                builder.setView(scrollView);
                show = builder.show();
                btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                AlertDialog finalShow2 = show;
                btnFromSelectBranch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        double quantityTemp =0.00;
                        try{
                            quantityTemp =Double.parseDouble(txtQuantity.getText().toString());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        finalShow2.dismiss();
                        Object[] o = {id,itemName,itemCode, quantityTemp,finalQuantity,fromWhse};
//                        public void editDialog2(int id, String itemName,String itemCode, double actualQty, double finalQuantity,String fromWhse){
                        showAvailabelQuantity showAvailabelQuantity = new showAvailabelQuantity(itemCode,itemName,o);
                        if (showAvailabelQuantity.getStatus() == AsyncTask.Status.FINISHED || showAvailabelQuantity.getStatus() == AsyncTask.Status.PENDING) {
                            showAvailabelQuantity.execute("");
                        }
//                        myEditWarehouse myEditWarehouse = new myEditWarehouse("/api/whse/get_all", sFromParams, "Warehouse", true, lblFromSelectedBranch, itemName,true);
//                        myEditWarehouse.execute("");
                    }
                });
            }
            if (hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Received from Production")) {
                TextView lblToBranch = new TextView(getBaseContext());
                lblToBranch.setText("*To Warehouse");
                lblToBranch.setTextColor(Color.rgb(0, 0, 0));
                lblToBranch.setTextSize(15);
                lblToBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                layout.addView(lblToBranch);

                LinearLayout layoutToBranch = new LinearLayout(getBaseContext());

                layoutToBranch.setLayoutParams(layoutParamsFromBranch);
                layoutToBranch.setOrientation(LinearLayout.HORIZONTAL);

                lblToSelectedBranch.setText("N/A");
                lblToSelectedBranch.setBackgroundColor(Color.parseColor("#ededed"));
                lblToSelectedBranch.setTextColor(Color.rgb(0, 0, 0));
                lblToSelectedBranch.setTextSize(15);
                lblToSelectedBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                lblToSelectedBranch.setLayoutParams(layoutParamsBranch2);
                layoutToBranch.addView(lblToSelectedBranch);

                TextView btnToSelectBranch = new TextView(API_SelectedItems.this);
                btnToSelectBranch.setPadding(20, 10, 20, 10);
                btnToSelectBranch.setText("...");
                btnToSelectBranch.setBackgroundResource(R.color.colorPrimary);
                btnToSelectBranch.setTextColor(Color.WHITE);
                btnToSelectBranch.setTextSize(13);
                btnToSelectBranch.setGravity(View.TEXT_ALIGNMENT_CENTER);
                btnToSelectBranch.setLayoutParams(layoutParamsBranch3);

                String finalCToWhse = cToWhse;
                SharedPreferences sharedPreferences2 = getSharedPreferences("LOGIN", MODE_PRIVATE);
                String currentBranch = Objects.requireNonNull(sharedPreferences2.getString("branch", ""));
                String sToParams = hiddenTitle.equals("API Item Request For Transfer") ? "?branch=" + finalCToWhse : hiddenTitle.equals("API Received from Production") ? "?branch=" + currentBranch : "";
                btnToSelectBranch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        boolean isShowAvailableQty = !hiddenTitle.equals("API Received from Production");
                        myEditWarehouse myEditWarehouse = new myEditWarehouse("/api/whse/get_all", sToParams, "Warehouse", true, lblToSelectedBranch, itemName,isShowAvailableQty);
                        myEditWarehouse.execute("");
                    }
                });
                String whseToName = findKey(sSelectedToWhse, "whsecode", "whsename", gBranch);
                lblToSelectedBranch.setText(whseToName);
                layoutToBranch.addView(btnToSelectBranch);
                builder.setPositiveButton("Update", null);
                builder.setNegativeButton("Cancel", null);
                layout.addView(layoutToBranch);
                if(hiddenTitle.equals("API Received from Production")){
                    scrollView.addView(layout);
                    builder.setView(scrollView);
                    show = builder.show();
                    btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
                }
            }
        }
        if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request")){
            builder.setPositiveButton("Update", null);
            builder.setNegativeButton("Cancel", null);
            scrollView.addView(layout);
            builder.setView(scrollView);
            show = builder.show();
            btn = show.getButton(DialogInterface.BUTTON_POSITIVE);
        }

        AlertDialog finalShow = show;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = 0.00;
                try {
                    qty = Double.parseDouble(lblQuantity.getEditText().getText().toString());
                } catch (NumberFormatException ex) {
                    qty = 0.00;
                }
                if (qty <= 0) {
                    Toast.makeText(getBaseContext(), "Input atleast 1 quantity!", Toast.LENGTH_SHORT).show();
                } else if (lblFromSelectedBranch.getText().toString().equals("N/A") && (hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item"))) {
                    Toast.makeText(getBaseContext(), "From Warehouse field is required!", Toast.LENGTH_SHORT).show();
                } else if (lblToSelectedBranch.getText().toString().equals("N/A") && (hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item"))) {
                    Toast.makeText(getBaseContext(), "To Warehouse field is required!", Toast.LENGTH_SHORT).show();
                }   else if(lblFromSelectedBranch.getText().toString().equals("N/A") && hiddenTitle.equals("API Received from Production")){
                    Toast.makeText(getBaseContext(), "From Warehouse field is required!", Toast.LENGTH_SHORT).show();
                }
//                                        else if(lblToSelectedBranch.getText().toString().equals("N/A") && hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")){
//                                            Toast.makeText(getBaseContext(), "To Warehouse field is required!", Toast.LENGTH_SHORT).show();
//                                        }
                else {
                    String whseCode = "",whseToCode = "";
                    if(hiddenTitle.equals("API Item Request For Transfer") || hiddenTitle.equals("API Transfer Item") || hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing") || hiddenTitle.equals("API Received from Production")){
                        whseCode = findKey(lblFromSelectedBranch.getText().toString(), "whsename","whsecode", gBranch);
                        whseToCode = findKey(lblToSelectedBranch.getText().toString(), "whsename","whsecode", gBranch);
                    }
                    boolean isSuccess = false;
                    if(hiddenTitle.equals("API Item Request For Transfer")) {
                        isSuccess = myDb3.updatePendingItemRequest(String.valueOf(id), qty, whseCode, whseToCode);
                    }
                    else if(hiddenTitle.equals("API Received Item") || hiddenTitle.equals("API Item Request")){
//                        if(hiddenTitle.equals("API Received Item")){
//                            myDb4.updateQuantity2(String.valueOf(id), qty,getHeader());
//                            isSuccess = true;
//                        }else{
//                            isSuccess = myDb4.updateQuantity(String.valueOf(id), qty);
//                        }
                        myDb4.updateQuantity(String.valueOf(id), qty,false);
                        isSuccess = true;
                    }
                    else if(hiddenTitle.equals("API Transfer Item")){
                        isSuccess = myDb4.updateTransferItem(String.valueOf(id),qty,whseCode,whseToCode);
                    }
                    else if(hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")){
                        isSuccess = myDb4.updateQuantityWhse(String.valueOf(id),qty,"fromWhse",whseCode,false);
                    }
                    else if(hiddenTitle.equals("API Received from Production")){
                        isSuccess = myDb4.updateQuantityWhse(String.valueOf(id),qty,"toWhse",whseToCode,false);
                    }
                    if (isSuccess) {
                        Toast.makeText(getBaseContext(), "Item Updated!", Toast.LENGTH_SHORT).show();
                        finalShow.dismiss();
                        loadItems();
                    } else {
                        Toast.makeText(getBaseContext(), "Item Not Updated!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
        AlertDialog finalShow1 = show;
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalShow1.dismiss();
            }
        });
    }

    public void editDialog1(int id ,String itemName,double actualRec, double quantity){
        AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
        builder.setCancelable(false);
        builder.setTitle("Edit");
        builder.setMessage(itemName);

//                                    double variance = cursor.getDouble(5) - finalQuantity;

        ScrollView scrollView = new ScrollView(getBaseContext());
        LinearLayout layout = new LinearLayout(getBaseContext());
        layout.setPadding(20, 20, 20, 20);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextInputLayout lblQuantity = new TextInputLayout(API_SelectedItems.this, null, R.style.Widget_MaterialComponents_TextInputLayout_OutlinedBox);
        LinearLayout.LayoutParams layoutParamsLblQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsLblQuantity.setMargins(0, 5, 0, 5);
        lblQuantity.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        lblQuantity.setBoxCornerRadii(5, 5, 5, 5);
        lblQuantity.setLayoutParams(layoutParamsLblQuantity);

        TextInputEditText txtQuantity = new TextInputEditText(lblQuantity.getContext());
        LinearLayout.LayoutParams layoutParamsTxtQuantity = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtQuantity.setLayoutParams(layoutParamsTxtQuantity);
        txtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        txtQuantity.setText(String.valueOf(actualRec));
        txtQuantity.setHint("*Enter Quantity");

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
                    double deliveredQty = quantity;
                    double inputQty = lblQuantity.getEditText().getText().toString().trim().isEmpty() || lblQuantity.getEditText().getText().toString().trim().equals(".") ? 0.00 : Double.parseDouble(lblQuantity.getEditText().getText().toString().trim());
                    double variance = inputQty - deliveredQty;
                    lblVariance.setText("Variance: " + df.format(variance));
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
        Objects.requireNonNull(lblQuantity.getEditText()).setFocusable(true);
        layout.addView(lblQuantity);

        double variance = actualRec - quantity;


        lblVariance.setText("Variance: " + df.format(variance));
        if (variance == 0) {
            lblVariance.setTextColor(Color.BLACK);
        } else if (variance > 0) {
            lblVariance.setTextColor(Color.BLUE);
        } else if (variance < 0) {
            lblVariance.setTextColor(Color.RED);
        }
        layout.addView(lblVariance);

        scrollView.addView(layout);

        builder.setPositiveButton("Update", null);
        builder.setNegativeButton("Cancel", null);
        builder.setView(scrollView);
        AlertDialog show = builder.show();
        Button btn = show.getButton(DialogInterface.BUTTON_POSITIVE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double qty = 0.00;
                try{
                    qty = Double.parseDouble(lblQuantity.getEditText().getText().toString());
                }catch (NumberFormatException ex){
                    qty = 0.00;
                }
                if(qty <=0){
                    Toast.makeText(getBaseContext(), "Input atleast 1 quantity!", Toast.LENGTH_SHORT).show();
                }else{
                    boolean isSuccess = myDb3.updateActualQuantity(String.valueOf(id),qty);
                    if(isSuccess){
                        Toast.makeText(getBaseContext(), "Item Updated!", Toast.LENGTH_SHORT).show();
                        show.dismiss();
                        loadItems();
                    }else{
                        Toast.makeText(getBaseContext(), "Item Not Updated!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        Button btn2 = show.getButton(DialogInterface.BUTTON_NEGATIVE);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    public void loadDialog() {
        if (hiddenTitle.equals("API Issue For Production") || hiddenTitle.equals("API Issue For Packing")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_issue_for_prod_dialog, null);
            if (hiddenTitle.equals("API Issue For Packing")) {
                LinearLayout linearLayout123 = gView.findViewById(R.id.layoutPacking);
                linearLayout123.setVisibility(VISIBLE);

                lblFGUOM = gView.findViewById(R.id.lblSelectedFGUOM);
                TextView btnSelectFGItem = gView.findViewById(R.id.btnSelectFGItem);
                TextView txtFGItem = gView.findViewById(R.id.lblSelectedFGItem);
                TextView txtFGUOM = gView.findViewById(R.id.lblSelectedFGUOM);


                btnSelectFGItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getDataFromDownload downloadd = new getDataFromDownload("FG Item",txtFGItem, "FG Item","","",false);
                        downloadd.execute("");
//                        myWarehouse myWarehouse = new myWarehouse(true, false, "FG Item", true, txtFGItem);
//                        myWarehouse.execute("");
                    }
                });

                EditText txtFGQuantity = gView.findViewById(R.id.txtFGQuantity);
                TextView btnMinus = gView.findViewById(R.id.btnMinus);
                TextView btnPlus = gView.findViewById(R.id.btnPlus);

                btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double fgQuantity = 0.00;
                        try {
                            fgQuantity = Double.parseDouble(txtFGQuantity.getText().toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (fgQuantity <= 0) {
                            fgQuantity = 0;
                        } else {
                            fgQuantity--;
                        }
                        txtFGQuantity.setText(df.format(fgQuantity));
                    }
                });
                btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        double fgQuantity = 0.00;
                        try {
                            fgQuantity = Double.parseDouble(txtFGQuantity.getText().toString());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        fgQuantity++;
                        txtFGQuantity.setText(df.format(fgQuantity));
                    }
                });
            }
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);
            TextView txtMill = gView.findViewById(R.id.lblSelectedMill);
            TextView btnSelectMill = gView.findViewById(R.id.btnSelectMill);

            btnSelectMill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Mill",txtMill, "Mill","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(true, false, "Mill", true, txtMill);
//                    myWarehouse.execute("");
                }
            });
        } else if (hiddenTitle.equals("API Received from Production")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_issue_for_prod_dialog, null);

//            LinearLayout linearLayout123 = gView.findViewById(R.id.layoutPacking);
//            linearLayout123.setVisibility(View.VISIBLE);

            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);
            TextView txtMill = gView.findViewById(R.id.lblSelectedMill);
            TextView btnSelectMill = gView.findViewById(R.id.btnSelectMill);

            btnSelectMill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Mill",txtMill, "Mill","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(true, false, "Mill", true, txtMill);
//                    myWarehouse.execute("");
                }
            });
        } else if (hiddenTitle.equals("API Received Item")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_manual_receive_dialog, null);
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextView btnSelectFromWhse = gView.findViewById(R.id.btnSelectFromWhse);
            TextView txtFromWhse = gView.findViewById(R.id.lblSelectedFromWhse);
            btnSelectFromWhse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Warehouse",txtFromWhse, "Warehouse","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(true, false, "Warehouse", true, txtFromWhse);
//                    myWarehouse.execute("");
                }
            });

            TextView btnSelectToWhse = gView.findViewById(R.id.btnSelectToWhse);
            TextView txtToWhse = gView.findViewById(R.id.lblSelectedToWhse);
            btnSelectToWhse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                    String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
                    getDataFromDownload downloadd = new getDataFromDownload("Warehouse",txtToWhse, "Warehouse","branch",currentDepartment,false);
                    downloadd.execute("");
//                    if(downloadd.i)


//                    showWarehouses(txtToWhse, "Warehouse", gValue,"branch",currentDepartment);

//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Warehouse", true, txtToWhse);
//                    myWarehouse.execute("");
                }
            });
            TextView btnSelectTruck = gView.findViewById(R.id.btnSelectTruck);
            TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
            btnSelectTruck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Truck",txtTruck, "Truck","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Truck", true, txtTruck);
//                    myWarehouse.execute("");
                }
            });

            TextView btnSelectVessel = gView.findViewById(R.id.btnSelectVessel);
            TextView txtVessel = gView.findViewById(R.id.lblSelectedVessel);
            btnSelectVessel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Vessel",txtVessel, "Vessel","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Vessel", true, txtVessel);
//                    myWarehouse.execute("");
                }
            });
            TextView btnSelectDriver = gView.findViewById(R.id.btnSelectDriver);
            TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
            btnSelectDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Driver",txtDriver, "Driver","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Driver", true, txtDriver);
//                    myWarehouse.execute("");
                }
            });

            TextInputLayout textInputLayoutAGIScale = gView.findViewById(R.id.txtAGITS);
            TextInputLayout textInputLayoutCHTIScale = gView.findViewById(R.id.txtCHTITS);
            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);
//            btnSaveDraft.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
//                        builder.setCancelable(false);
//                        builder.setTitle("Validation");
//                        builder.setTitle("Are you sure you want so save as draft?");
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                saveDraft(getHeader(),true);
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        } else if (hiddenTitle.equals("API System Transfer Item")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_system_receive_dialog, null);
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);
        } else if (hiddenTitle.equals("API Transfer Item")) {

            gView = LayoutInflater.from(this).inflate(R.layout.layout_system_transfer_dialog, null);
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextView lblFGItem = gView.findViewById(R.id.labelFGItem);
            TextView txtFGItem = gView.findViewById(R.id.lblSelectedFGItem);
            TextView btnSelectFGItem = gView.findViewById(R.id.btnSelectFGItem);

            TextView labelFGUOM = gView.findViewById(R.id.labelFGUOM);
            lblFGUOM = gView.findViewById(R.id.lblSelectedFGUOM);

            SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
            String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
            if(currentDepartment.equals("FLOUR BINS") || currentDepartment.equals("CC-FLOUR BINS") || currentDepartment.equals("BRAN/POLLARD PACKING BINS") || currentDepartment.equals("CC-BRAN/POLLARD PACKING BINS") || currentDepartment.equals("BRAN/POLLARD BINS") || currentDepartment.equals("CC-BRAN/POLLARD BINS")) {
                lblFGItem.setVisibility(VISIBLE);
                btnSelectFGItem.setVisibility(VISIBLE);
                txtFGItem.setVisibility(VISIBLE);
                lblFGUOM.setVisibility(VISIBLE);
                labelFGUOM.setVisibility(VISIBLE);
                btnSelectFGItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getDataFromDownload downloadd = new getDataFromDownload("FG Item", txtFGItem, "FG Item", "", "",false);
                        downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "FG Item", true, txtFGItem);
//                    myWarehouse.execute("");
                    }
                });
            }else{
                lblFGItem.setVisibility(GONE);
                txtFGItem.setVisibility(GONE);
                btnSelectFGItem.setVisibility(GONE);
                lblFGUOM.setVisibility(GONE);
                labelFGUOM.setVisibility(GONE);
            }

            TextView btnSelectTruck = gView.findViewById(R.id.btnSelectTruck);
            TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
            TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
            TextView lblSelectedStartDate = gView.findViewById(R.id.lblSelectedStartDate);
            TextView lblSelectedEndDate = gView.findViewById(R.id.lblSelectedEndDate);
            TextView lblSelectedStartTime = gView.findViewById(R.id.lblSelectedStartTime);
            TextView lblSelectedEndTime = gView.findViewById(R.id.lblSelectedEndTime);
            btnSelectTruck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Truck",txtTruck, "Truck","","",false);
                    downloadd.execute("");

//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Truck", true, txtTruck);
//                    myWarehouse.execute("");
                }
            });

            TextView btnSelectDriver = gView.findViewById(R.id.btnSelectDriver);

            btnSelectDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Driver",txtDriver, "Driver","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Driver", true, txtDriver);
//                    myWarehouse.execute("");
                }
            });
            TextView btnSelectStartDate = gView.findViewById(R.id.btnSelectStartDate);

            btnSelectStartDate.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showDatePickerDialog(lblSelectedStartDate);
                }
            });
            TextView btnSelectEndDate = gView.findViewById(R.id.btnSelectEndDate);

            btnSelectEndDate.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showDatePickerDialog(lblSelectedEndDate);
                }
            });
            TextView btnSelectStartTime = gView.findViewById(R.id.btnSelectStartTime);

            btnSelectStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showTime(lblSelectedStartTime);
                }
            });
            TextView btnSelectEndTime = gView.findViewById(R.id.btnSelectEndTime);

            btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showTime(lblSelectedEndTime);
                }
            });

            TextInputLayout textInputLayoutTemperingHour = gView.findViewById(R.id.txtTemperingHour);
            TextInputLayout textInputLayoutAGIScale = gView.findViewById(R.id.txtAGITS);
            TextInputLayout textInputLayoutCHTIScale = gView.findViewById(R.id.txtCHTITS);
            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);

//            textInputLayoutTemperingHour.getEditText().setText(sTemperingHour);
//            textInputLayoutAGIScale.getEditText().setText(sAGITS);
//            textInputLayoutCHTIScale.getEditText().setText(sCHTITS);
//            textInputLayoutRemarks.getEditText().setText(sRemarks);
//            btnSaveDraft.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
//                        builder.setCancelable(false);
//                        builder.setTitle("Validation");
//                        builder.setTitle("Are you sure you want so save as draft?");
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                saveDraft(getHeader(),true);
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        } else if (hiddenTitle.equals("API Item Request")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_item_request_dialog, null);
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextView btnSelectFromDept = gView.findViewById(R.id.btnSelectFromDept);
            TextView txtFromDept = gView.findViewById(R.id.lblSelectedFromDept);
            btnSelectFromDept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Branch",txtFromDept, "Branch","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Branch", true, txtFromDept);
//                    myWarehouse.execute("");
                }
            });

            TextView btnSelectDueDate = gView.findViewById(R.id.btnSelectDueDate);
            TextView txtDueDate = gView.findViewById(R.id.lblSelectedDueDate);
            btnSelectDueDate.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    showDatePickerDialog(txtDueDate);
                }
            });
            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);
        } else if (hiddenTitle.equals("API Item Request For Transfer")) {
            gView = LayoutInflater.from(this).inflate(R.layout.layout_pending_item_request, null);
            LinearLayout linearLayout111 = findViewById(R.id.linearLayout1);
            linearLayout111.removeAllViews();
            linearLayout111.addView(gView);

            TextView btnSelectTruck = gView.findViewById(R.id.btnSelectTruck);
            TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
            TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
            TextInputLayout textInputLayoutAGIScale = gView.findViewById(R.id.txtAGITS);
            TextInputLayout textInputLayoutCHTIScale = gView.findViewById(R.id.txtCHTITS);
            TextInputLayout textInputLayoutRemarks = gView.findViewById(R.id.txtRemarks);
            btnSelectTruck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Truck",txtTruck, "Truck","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Truck", true, txtTruck);
//                    myWarehouse.execute("");
                }
            });

            TextView btnSelectDriver = gView.findViewById(R.id.btnSelectDriver);

            btnSelectDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    getDataFromDownload downloadd = new getDataFromDownload("Driver",txtDriver, "Driver","","",false);
                    downloadd.execute("");
//                    myWarehouse myWarehouse = new myWarehouse(false, false, "Driver", true, txtDriver);
//                    myWarehouse.execute("");
                }
            });


//            btnSaveDraft.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
//                        builder.setCancelable(false);
//                        builder.setTitle("Validation");
//                        builder.setTitle("Are you sure you want so save as draft?");
//                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                saveDraft(getHeader(),false);
//                                dialog.dismiss();
//                            }
//                        });
//                        builder.show();
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }

//    public String getHeader(){
//        JSONObject joHeader = new JSONObject();
//        try {
//            if(hiddenTitle.equals("API Received Item")){
//                TextView txtFromWhse = gView.findViewById(R.id.lblSelectedFromWhse);
//                TextView txtToWhse = gView.findViewById(R.id.lblSelectedToWhse);
//                TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
//                TextView txtVessel = gView.findViewById(R.id.lblSelectedVessel);
//                TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
//                TextInputLayout txtAGITS = gView.findViewById(R.id.txtAGITS);
//                TextInputLayout txtCHTITS = gView.findViewById(R.id.txtCHTITS);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//
//                joHeader.put("from_whse", txtFromWhse.getText().toString());
//                joHeader.put("to_whse", txtToWhse.getText().toString());
//                joHeader.put("plate_num", txtTruck.getText().toString());
//                joHeader.put("vessel", txtVessel.getText().toString());
//                joHeader.put("driver", txtDriver.getText().toString());
//                joHeader.put("agi_truck_scale", txtAGITS.getEditText().getText().toString());
//                joHeader.put("chti_truck_scale", txtCHTITS.getEditText().getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//            }else if(hiddenTitle.equals("API Transfer Item")){
//                TextView txtFGItem = gView.findViewById(R.id.lblSelectedFGItem);
//                TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
//                TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
//                TextView txtStartDate = gView.findViewById(R.id.lblSelectedStartDate);
//                TextView txtStartTime = gView.findViewById(R.id.lblSelectedStartTime);
//                TextView txtEndDate = gView.findViewById(R.id.lblSelectedEndDate);
//                TextView txtEndTime = gView.findViewById(R.id.lblSelectedEndTime);
//                TextInputLayout txtTemperingHour = gView.findViewById(R.id.txtTemperingHour);
//                TextInputLayout txtAGITS = gView.findViewById(R.id.txtAGITS);
//                TextInputLayout txtCHTITS = gView.findViewById(R.id.txtCHTITS);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//
//                joHeader.put("fg_item", txtFGItem.getText().toString());
//                joHeader.put("plate_num", txtTruck.getText().toString());
//                joHeader.put("driver", txtDriver.getText().toString());
//
//                joHeader.put("transfer_start_date", txtStartDate.getText().toString());
//                joHeader.put("transfer_start_time", txtStartTime.getText().toString());
//                joHeader.put("transfer_end_date", txtEndDate.getText().toString());
//                joHeader.put("transfer_end_time", txtEndTime.getText().toString());
//
//                joHeader.put("tempering_hour", txtTemperingHour.getEditText().getText().toString());
//                joHeader.put("agi_truck_scale", txtAGITS.getEditText().getText().toString());
//                joHeader.put("chti_truck_scale", txtCHTITS.getEditText().getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//            }
//            else if(hiddenTitle.equals("API Item Request")){
//                TextView txtFromDept = gView.findViewById(R.id.lblSelectedFromDept);
//                TextView txtDueDate = gView.findViewById(R.id.lblSelectedDueDate);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//
//                joHeader.put("from_dept", txtFromDept.getText().toString());
//                joHeader.put("due_date", txtDueDate.getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//            }
//            else if(hiddenTitle.equals("API Issue For Production")){
//                TextView txtMill = gView.findViewById(R.id.lblSelectedMill);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//
//                joHeader.put("mill", txtMill.getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//            }
//            else if(hiddenTitle.equals("API Received from Production")){
//                TextView txtMill = gView.findViewById(R.id.lblSelectedMill);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//                joHeader.put("mill", txtMill.getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//            }
//            else if(hiddenTitle.equals("API Issue For Packing")){
//                TextView txtMill = gView.findViewById(R.id.lblSelectedMill);
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//
//                TextView txtFGItem = gView.findViewById(R.id.lblSelectedFGItem);
//                TextView txtFGUOM = gView.findViewById(R.id.lblSelectedFGUOM);
//                TextView txtFGQuantity = gView.findViewById(R.id.txtFGQuantity);
//
//                joHeader.put("mill", txtMill.getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//                joHeader.put("fg_item", txtFGItem.getText().toString());
//                joHeader.put("fg_uom", txtFGUOM.getText().toString());
//                joHeader.put("fg_quantity", txtFGQuantity.getText().toString());
//            }
//            else if(hiddenTitle.equals("API Item Request For Transfer")){
//
//                TextView txtTruck = gView.findViewById(R.id.lblSelectedTruck);
//                TextView txtDriver = gView.findViewById(R.id.lblSelectedDriver);
//
//                TextInputLayout txtRemarks = gView.findViewById(R.id.txtRemarks);
//                TextInputLayout txtAGITS = gView.findViewById(R.id.txtAGITS);
//                TextInputLayout txtCHTITS = gView.findViewById(R.id.txtCHTITS);
//
//                joHeader.put("plate_num", txtTruck.getText().toString());
//                joHeader.put("driver", txtDriver.getText().toString());
//                joHeader.put("remarks", txtRemarks.getEditText().getText().toString());
//                joHeader.put("agi_truck_scale", txtAGITS.getEditText().getText().toString());
//                joHeader.put("chti_truck_scale", txtCHTITS.getEditText().getText().toString());
//            }
//        }
//        catch (Exception ex) {
//            ex.printStackTrace();
//            Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
//        }
//        System.out.println("bakit ganun? " + joHeader);
//        return joHeader.toString();
//    }

//    public void saveDraft(String header, boolean isDB4){
//        boolean result = myDb4.insertDataDrafts(header, title);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
//        builder.setCancelable(false);
//        if(result){
//            builder.setTitle("Information");
//            builder.setMessage("Saved Draft! We redirect you to Home Page");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(API_SelectedItems.this, API_Nav2.class);
//                    startActivity(intent);
//                    finish();
//                    dialog.dismiss();
//                }
//            });
//        }else{
//            builder.setTitle("Validation");
//            builder.setMessage("Failed to Save! No data found to save as draft");
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//        }
//        builder.show();
//    }

    private class showVessel extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gItemCode = "",gWhseCode = "";
        Spinner gCmbVessel = null;
        public showVessel(String itemCode, String whseCode,Spinner cmbVessel){
            gItemCode = itemCode;
            gWhseCode = whseCode;
            gCmbVessel = cmbVessel;
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
                String sURL = IPAddress + "/api/inv/vessel/get_all?item_code=" + gItemCode + "&whsecode=" + gWhseCode;
                System.out.println("show vessel " + sURL);
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
            try{
                if(s != null || !s.isEmpty()){
                    if(s.startsWith("{")){
                        List<String>listItems = new ArrayList<>();
                        JSONObject joResult = new JSONObject(s);
                        JSONArray jaData = joResult.getJSONArray("data");
                        if(jaData.length() > 0){
                            listItems.add("--Select--");
                        }
                        for (int i = 0; i < jaData.length(); i++) {
                            JSONObject joData = jaData.getJSONObject(i);
                            String vessel = joData.has("vessel") ? !joData.isNull("vessel") ? joData.getString("vessel") : "" : "";
                            if(!vessel.trim().isEmpty()){
                                listItems.add(vessel);
                            }
                        }
                        if(gCmbVessel != null){
                            gCmbVessel.setAdapter(fillItems(listItems));
                        }
                    }
                }
                loadingDialog.dismissDialog();
            }catch (Exception ex){
                Toast.makeText(getBaseContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        }
    }

    public void apiSaveItemRequestForTransfer(String remarks,String fromWhse, String toWhse,AlertDialog dialogg) throws JSONException {

    }

    private class PendingItemRequest extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(API_SelectedItems.this);
        String gRemarks = "",gFromBranch = "", gPlateNum = "",gDriver = "", gShift = "", gAgiScale = "", gCHTIScale = "",gHashedID="";
        AlertDialog gDialog;
        public PendingItemRequest(String remarks, String plateNum, String driver, String shift, String agiScale, String chtiScale,AlertDialog dialogg, String hashedID){
            gRemarks = remarks;
            gPlateNum = plateNum;
            gDriver = driver;
            gShift = shift;
            gAgiScale = agiScale;
            gCHTIScale = chtiScale;
            gDialog = dialogg;
            gHashedID = hashedID;
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected String doInBackground(String... strings) {
            Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                    String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                    JSONObject jsonObject = new JSONObject();
                    try {
                        // create your json here
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                        String currentDateTime = sdf.format(new Date());

                        JSONObject objectHeaders = new JSONObject();

                        objectHeaders.put("sap_number", JSONObject.NULL);
                        objectHeaders.put("transdate", currentDateTime);
                        objectHeaders.put("remarks", gRemarks);
                        objectHeaders.put("reference2", null);
                        objectHeaders.put("base_id", cursor.getInt(9));
                        objectHeaders.put("base_objtype", cursor.getInt(14));
                        if (!gPlateNum.equals("N/A") && !gPlateNum.trim().equals("")) {
                            objectHeaders.put("plate_num",gPlateNum);
                        }
                        objectHeaders.put("remarks", gRemarks);
                        if (!gShift.equals("N/A") && !gShift.trim().equals("")) {
                            objectHeaders.put("shift",gShift);
                        }
                        objectHeaders.put("hashed_id", gHashedID);
                        if (!gDriver.equals("N/A") && !gDriver.trim().equals("")) {
                            objectHeaders.put("driver", gDriver);
                        }
                        if (!gAgiScale.equals("N/A") && !gAgiScale.trim().equals("")) {
                            objectHeaders.put("agi_truck_scale", gAgiScale);
                        }
                        if (!gCHTIScale.equals("N/A") && !gCHTIScale.trim().equals("")) {
                            objectHeaders.put("chti_truck_scale", gCHTIScale);
                        }
                        jsonObject.put("header", objectHeaders);

                        JSONArray arrayDetails = new JSONArray();

                        Cursor cursor2 = myDb3.getAllSelectedData(hiddenTitle);
                        while (cursor2.moveToNext()) {
                            if(cursor2.getInt(6) == 1) {
                                JSONObject objectDetails = new JSONObject();
                                String itemName = cursor2.getString(16);
                                Double actualQty = cursor2.getDouble(5);

                                objectDetails.put("base_id", cursor2.getInt(13));
                                objectDetails.put("item_code", itemName);
                                objectDetails.put("from_whse",cursor2.getString(18));
                                objectDetails.put("to_whse",cursor2.getString(19));
                                objectDetails.put("quantity", actualQty);
                                objectDetails.put("uom", cursor2.getString(11));
                                arrayDetails.put(objectDetails);
                            }
                        }
                        jsonObject.put("details", arrayDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(jsonObject);
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                    SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                    String IPAddress = sharedPreferences2.getString("IPAddress", "");

                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(IPAddress + "/api/inv/trfr/new")
                            .method("POST", body)
                            .addHeader("Authorization", "Bearer " + token)
                            .addHeader("Content-Type", "application/json")
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    builder.setCancelable(false);
                                    builder.setMessage(e.getMessage());
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            gDialog.dismiss();
                                            loadingDialog.dismissDialog();
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            loadingDialog.dismissDialog();
                            formatResponse(response.body().string(), gDialog);
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            loadingDialog.dismissDialog();
        }
    }


    public void apiSaveDataRec(String supplier, String remarks, AlertDialog dialogg, String hashedID) throws JSONException {
        Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String sap_number = cursor.getString(1);
                String fromBranch = cursor.getString(2);

                String data = cursor.getString(17);
                JSONObject jsonObjectResult = new JSONObject(data);
                JSONArray jsonArrayData = jsonObjectResult.getJSONArray("data");
                System.out.println("array " + jsonArrayData);
                JSONObject objectHeaders = new JSONObject();

                for (int i = 0; i < jsonArrayData.length(); i++) {
                    JSONObject jsonObjectData = jsonArrayData.getJSONObject(i);
                    if(!jsonObjectData.isNull("vessel")){
                        objectHeaders.put("vessel", jsonObjectData.getString("vessel"));
                    }
                    objectHeaders.put("plate_num", jsonObjectData.has("plate_num") ? jsonObjectData.isNull("plate_num") ? JSONObject.NULL : jsonObjectData.getString("plate_num") : JSONObject.NULL);
                    if(!jsonObjectData.getString("shift").trim().equals("") && !jsonObjectData.isNull("shift")){
                        objectHeaders.put("shift", jsonObjectData.getString("shift"));
                    }
                    if(!jsonObjectData.isNull("driver")){
                        objectHeaders.put("driver", jsonObjectData.getString("driver"));
                    }
                    if(!jsonObjectData.isNull("agi_truck_scale")){
                        objectHeaders.put("agi_truck_scale", jsonObjectData.getString("agi_truck_scale"));
                    }

                }
                objectHeaders.put("hashed_id", hashedID);

                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                JSONObject jsonObject = new JSONObject();
                try {
                    // create your json here
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    String transType;
                    if(cursor.getInt(7) == 0 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPPO";
                    }else if(cursor.getInt(7) == 1 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPIT";
                    }else if(cursor.getInt(7) == 2 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPDN";
                    }else if(cursor.getInt(7) == 3 && hiddenTitle.equals("API Received from SAP")){
                        transType = "SAPAR";
                    }
                    else {
                        transType = "TRFR";
                    }

                    objectHeaders.put("transtype", transType);
                  if(hiddenTitle.equals("API Received from SAP")){
                      objectHeaders.put("transfer_id", null);
                  }
                  else {
                      objectHeaders.put("base_id", (cursor.getInt(9) <= 0 ? null : cursor.getInt(9)));
                  }
                    objectHeaders.put("sap_number", (hiddenTitle.equals("API Received from SAP") ? (sap_number.isEmpty() ? null : sap_number) : null));
                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("remarks", remarks);
                    objectHeaders.put((hiddenTitle.equals("API Received from SAP") ? "reference2" : "ref2"), null);
                    objectHeaders.put("supplier", (cursor.getInt(7) == 0) ? supplier : null);
                    jsonObject.put("header", objectHeaders);

                    JSONArray arrayDetails = new JSONArray();

                    Cursor cursor2 = myDb3.getAllSelectedData(hiddenTitle);
                    while (cursor2.moveToNext()) {
                        if(cursor2.getInt(6) == 1) {
                            JSONObject objectDetails = new JSONObject();
                            String itemName = cursor2.getString(16);
                            Double deliveredQty = cursor2.getDouble(4);
                            Double actualQty = cursor2.getDouble(5);

                            objectDetails.put("item_code", itemName);
                            objectDetails.put("from_whse", fromBranch);
                            objectDetails.put("to_whse", cursor2.getString(8));
                            objectDetails.put("quantity", deliveredQty);
                            objectDetails.put("actualrec", actualQty);
                            objectDetails.put("uom", cursor2.getString(11));
                            arrayDetails.put(objectDetails);
                        }
                    }
                    jsonObject.put("details", arrayDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("whaaat " + jsonObject);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPaddress = sharedPreferences2.getString("IPAddress", "");

                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPaddress + "/api/inv/recv/new")
                        .method("POST", body)
                        .addHeader("Authorization", "Bearer " + token)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setCancelable(false);
                                builder.setTitle("Validation");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, okhttp3.Response response) {
                        String result = "";
                        try {
                            result = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String finalResult = result;
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                JSONObject jj = new JSONObject(finalResult);
                                String msg = jj.has("message") ? jj.getString("message") : "No Message found";
                                boolean isSuccess = jj.has("success") && jj.getBoolean("success");
                                if (isSuccess) {
                                    myDb3.truncateTable();
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                    builder.setCancelable(false);
                                    builder.setTitle("Message");
                                    builder.setMessage(msg);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialogg.dismiss();
                                            loadItems();
                                        }
                                    });
                                    builder.show();
                                } else {
                                    if (msg.equals("Token is invalid")) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Your session is expired. Please login again.");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            pc.loggedOut(API_SelectedItems.this);
                                            pc.removeToken(API_SelectedItems.this);
                                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
                                            finish();
                                            dialog.dismiss();
                                        });
                                        builder.show();
                                    } else {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setTitle("Validation");
                                        builder.setMessage(msg);
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                        });
                                        builder.show();
                                    }
                                }
                            } catch (Exception e) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                builder.setCancelable(false);
                                builder.setTitle("Validation");
                                builder.setMessage(e.getMessage());
                                builder.setPositiveButton("OK", (dialog, which) -> {
                                    dialog.dismiss();
                                });
                                builder.show();
                            }
                        });
                    }
                });
            }
        }
    }

    public void apiSaveInventoryCount(String remarks,AlertDialog dialogg) {
        Cursor cursor = myDb3.getAllSelectedData(hiddenTitle);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                SharedPreferences sharedPreferences = getSharedPreferences("TOKEN", MODE_PRIVATE);
                String token = Objects.requireNonNull(sharedPreferences.getString("token", ""));
                JSONObject jsonObject = new JSONObject();
                try {
                    // create your json here
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date());

                    JSONObject objectHeaders = new JSONObject();

                    objectHeaders.put("transdate", currentDateandTime);
                    objectHeaders.put("remarks", remarks);
                    jsonObject.put("header", objectHeaders);

                    JSONArray arrayDetails = new JSONArray();

                    Cursor cursor2 = myDb3.getAllSelectedData(hiddenTitle);
                    while (cursor2.moveToNext()) {
                        if(cursor2.getInt(6) == 1) {
                            JSONObject objectDetails = new JSONObject();
                            String itemName = cursor2.getString(16);
                            Double deliveredQty = cursor2.getDouble(4);
                            Double actualQty = cursor2.getDouble(5);

                            objectDetails.put("item_code", itemName);

                            if(hiddenTitle.equals("API Inventory Count")){
                                objectDetails.put("quantity", deliveredQty);
                            }

                            objectDetails.put((hiddenTitle.equals("API Inventory Count") ? "actual_count" : "quantity"), actualQty);
                            objectDetails.put("uom", cursor2.getString(11));
                            arrayDetails.put(objectDetails);
                        }
                    }
                    jsonObject.put("rows", arrayDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                SharedPreferences sharedPreferences2 = getSharedPreferences("CONFIG", MODE_PRIVATE);
                String IPaddress = sharedPreferences2.getString("IPAddress", "");

                String isInvCount = (hiddenTitle.equals("API Inventory Count") ? "inv/count" : "pulloutreq");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(IPaddress + "/api/" + isInvCount + "/create")
                        .method("POST", body)
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String finalResult = result;
                        API_SelectedItems.this.runOnUiThread(() -> {
                            try {
                                JSONObject jj = new JSONObject(finalResult);
                                boolean isSuccess = jj.getBoolean("success");
                                if (isSuccess) {
                                    myDb3.truncateTable();
                                    dialogg.dismiss();
                                    Toast.makeText(getBaseContext(), jj.getString("message"), Toast.LENGTH_SHORT).show();
                                    loadItems();
                                } else {
                                    String msg = jj.getString("message");
                                    if (msg.equals("Token is invalid")) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(API_SelectedItems.this);
                                        builder.setCancelable(false);
                                        builder.setMessage("Your session is expired. Please login again.");
                                        builder.setPositiveButton("OK", (dialog, which) -> {
                                            pc.loggedOut(API_SelectedItems.this);
                                            pc.removeToken(API_SelectedItems.this);
                                            startActivity(uic.goTo(API_SelectedItems.this, MainActivity.class));
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
        }
    }

    public String cutWord(String value){
        String result;
        int limit = 10;
        int limitTo = limit - 3;
        result = (value.length() > limit ? value.substring(0, limitTo) + "..." : value);
        return result;
    }
}