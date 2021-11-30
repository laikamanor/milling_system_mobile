package com.example.atlanticgrains;

import java.util.Arrays;
import java.util.List;

public class navigation_class {
    public List<String> getTitles(String appName){
        return Arrays.asList("Settings","Production","Inventory", "Logs","Count","Master Data");
    }

    public List<String> getItem(String parentItem){
        if(parentItem.equals("Settings")){
            return Arrays.asList("Cut Off","Offline Pending Transactions","Change Department","Change Shift", "Change Password", "Logout");
        }else if(parentItem.equals("Inventory")){
            return Arrays.asList("Manual Receive Item","System Receive Item", "System Transfer Item","Item Request","Pending Item Request");
        }else if(parentItem.equals("Production")){
//            return Arrays.asList("Item Request","Issue For Production", "Confirm Issue For Production", "Received from Production");
            return Arrays.asList("Issue For Production","Pending Issue For Production/Packing", "Received from Production");
        }else if(parentItem.equals("Logs")){
            return Arrays.asList("Logs");
        }
        else if(parentItem.equals("Count")){
            return Arrays.asList("Inventory Count", "Inventory Count Confirmation");
        }
        else if(parentItem.equals("Master Data")){
            return Arrays.asList("Trucks","Production Shift");
        }
        else {
            return null;
        }
    }
}
