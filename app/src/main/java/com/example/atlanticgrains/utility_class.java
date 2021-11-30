package com.example.atlanticgrains;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

import java.util.Objects;

public class utility_class {

    public  static String[] prodList = {"CLEAN WHEAT", "CC-CLEAN WHEAT","FEEDBACK","CC-FEEDBACK","FLOUR BINS","CC-FLOUR BINS"};
    public static   String[] packingList = { "FLOUR PACKING BINS", "BRAN/POLLARD PACKING BINS", "CC-FLOUR PACKING BINS", "CC-BRAN/POLLARD PACKING BINS"};

    public String getIPAddress(Activity activity){
        SharedPreferences sharedPreferences2 = activity.getSharedPreferences("CONFIG", activity.MODE_PRIVATE);
        return sharedPreferences2.getString("IPAddress", "");
    }

    public boolean isAllowProdPacking(String[] lists, Activity activity){
        SharedPreferences sharedPreferences = activity.getSharedPreferences("LOGIN", activity.MODE_PRIVATE);
        String currentDepartment = Objects.requireNonNull(sharedPreferences.getString("branch", ""));
        int iResult = 0;
        for(String list : lists) {
            if (list.equals(currentDepartment)) {
                iResult++;
                break;
            }
        }
        return iResult > 0;
    }

    public void customAlertDialog(String title, String message, Activity activity){
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }
}
