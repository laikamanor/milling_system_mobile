package com.example.atlanticgrains;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class aaa extends AppCompatActivity {
    int i = 0;
    boolean isClickable = false;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aaa);

        btn = findViewById(R.id.button);
        String s1 = "Single Click!",
                s2 = "Double Click!";

        SharedPreferences sharedPreferences = getSharedPreferences("TOKEN",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("token","eyJhbGciOiJIUzUxMiIsImlhdCI6MTYyNzk1MjQzOSwiZXhwIjoxNjI4MTI1MjM5fQ.eyJ1c2VyX2lkIjoxNjR9.n9cmBKuMoxTe24rpJYnvoQ3ga0Ua_ukAcykm5eEDgl5zsk1_W-lRtfycQQu0qUnZGjYBOrwu9MZG6UdEPaxCzw").apply();
        //SOLUTION 1
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setEnabled(false);
                i++;
                Handler handler =new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(i == 1){
                            showResult showResult = new showResult();
                            showResult.execute("");
                        }else{
                            Toast.makeText(getBaseContext(), s2 + "/" + i,Toast.LENGTH_SHORT).show();
                            System.out.println(s2 + "/" + i);
                            btn.setEnabled(true);
                        }
                        i = 0;
                    }
                },500);
            }
        });


        //SOLUTION 2
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isClickable){
//                    isClickable = false;
//                }else{
//                    isClickable = true;
//                }
//            }
//        });
    }

    private class showResult extends AsyncTask<String, Void, String> {
        LoadingDialog loadingDialog = new LoadingDialog(aaa.this);

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            loadingDialog.dismissDialog();
            btn.setEnabled(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            String url = "/api/inv/recv/get_all?docstatus=&transnumber=&branch=&to_whse=&from_date=&to_date=&from_time=&to_time=";
            api_class apic = new api_class(url, "GET","","http://124.106.123.96:81",true,getBaseContext());
            String result =apic.getResponse();
            return result;
        }
    }
}