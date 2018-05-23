package com.example.mikolaj.newapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class addOffense extends AppCompatActivity implements AsyncResponse, View.OnClickListener {
    EditText etID, etName, etSurname, etAddress, etDescription, etCount,etLatitude, etLongitude,sDistrict;
    Button btnAdd, btnLoc;
    Spinner sType,etNrD;
    String temp1,temp2;
    Double dTemp1, dTemp2;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "offense";
    public static final String Name = "nameKey";

    public static String DistrictName;

    public static String findDistrict(double latitude, double longitude, List<Districts> listOfDistricts){

        LatLng point = new LatLng(latitude,longitude);

        for (Districts x : listOfDistricts)
        {
            if (Districts.isPointInPolygon(point,x.getList())){
                addOffense.DistrictName= x.districtName;
                return x.districtName;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_offense);
        //etID = (EditText) findViewById(R.id.input_ID);
        etAddress = (EditText) findViewById(R.id.address);
        etLatitude = (EditText) findViewById(R.id.poz_x);
        etLongitude = (EditText) findViewById(R.id.poz_y);
        etDescription = (EditText) findViewById(R.id.input_description);
        etCount = (EditText) findViewById(R.id.signup_input_count);
        etNrD = (Spinner) findViewById(R.id.signup_input_dyspozytor);
        btnAdd = (Button) findViewById(R.id.btn_signup);
        sDistrict = (EditText) findViewById(R.id.dzielnica);
        sType = (Spinner) findViewById(R.id.spinner4);
        btnLoc = (Button) findViewById(R.id.btn_loc);
        btnAdd.setOnClickListener(this);
        btnLoc.setOnClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_loc:
                String lokalizacja = null;
                lokalizacja = etAddress.getText().toString();
                new Coordinates().execute(lokalizacja.replace(" ","+"));
                break;
            case R.id.btn_signup:
                Calendar c = Calendar.getInstance();
                System.out.println("Current time =&gt; "+c.getTime());
                HashMap postData = new HashMap();
//                postData.put("txtID", etID.getText().toString());
                postData.put("txtID", myAccount.getMyID());

                postData.put("txtLatitude", etLatitude.getText().toString());
                postData.put("txtLongitute", etLongitude.getText().toString());
                postData.put("txtAddress", etAddress.getText().toString());
                postData.put("txtDescription", etDescription.getText().toString());
                postData.put("txtCount", etCount.getText().toString());
                postData.put("txtNrD", etNrD.getSelectedItem().toString());
                postData.put("txtDistrict", sDistrict.getText().toString());
                postData.put("txtType", sType.getSelectedItem().toString());


                PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
                task.execute("http://wilki.kylos.pl/PSI/_addOffense.php");
                break;
        }


    }


    public void logout(View view) {
        SharedPreferences sharedpreferences = getSharedPreferences(login.MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void close(View view) {
        finish();
    }

    @Override
    public void processFinish(String result) {
        if (result.equals("success")) {
            StrictMode.setThreadPolicy((new StrictMode.ThreadPolicy.Builder().permitNetwork().build()));
            Toast.makeText(this, "Zgłoszenie zostało dodane", Toast.LENGTH_LONG).show();
            DownloadDataBase.getData1(DownloadDataBase.address);
            DownloadDataBase.splitOffenseData(DownloadDataBase.offenses);
            Intent in = new Intent(this, userMenu.class);
            finish();
            startActivity(in);
        } else {
            Toast.makeText(this, "Register Failed", Toast.LENGTH_LONG).show();

        }
    }


    private class Coordinates extends AsyncTask<String,Void,String>{
        ProgressDialog dialog = new ProgressDialog(addOffense.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%S", address);
                response = http.getHTTPData(url);
                return response;
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);
                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                String lon = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                etLatitude.setText(String.format("%s",lat));
                etLongitude.setText(String.format("%s",lon));
                temp1 = lat;
                dTemp1 = Double.parseDouble(temp1);
                temp2 = lon;
                dTemp2 = Double.parseDouble(temp2);
                String foundDistrict = addOffense.findDistrict(dTemp1,dTemp2, DownloadDataBase.districts);
                sDistrict.setText( foundDistrict );
                if(dialog.isShowing()){
                    dialog.dismiss();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}