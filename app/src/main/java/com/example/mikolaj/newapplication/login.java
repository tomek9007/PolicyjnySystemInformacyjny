package com.example.mikolaj.newapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kosalgeek.asynctask.AsyncResponse;
import com.kosalgeek.asynctask.PostResponseAsyncTask;

import java.util.HashMap;

public class login extends AppCompatActivity implements AsyncResponse, View.OnClickListener {

    EditText etUsername, etPassword;
    Button btnLogin;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "username";
    public static final String Name = "nameKey";
    public static String sUsername;
    public myAccount account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public void processFinish(String result) {
        int res = Integer.parseInt(result);
        if (res>0) {
            sUsername = etUsername.getText().toString();
            account = new myAccount(sUsername,res);
            String temp = account.getMyID();
            Toast.makeText(this, "Zalogowano", Toast.LENGTH_LONG).show();
            Intent in = new Intent(this, userMenu.class);
            startActivity(in);
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onClick(View view) {
        HashMap postData = new HashMap();
        postData.put("mobile", "android");
        postData.put("txtUsername", etUsername.getText().toString());
        postData.put("txtPassword", etPassword.getText().toString());

        PostResponseAsyncTask task = new PostResponseAsyncTask(this, postData);
        task.execute("http://wilki.kylos.pl/PSI/_login.php");


    }

    public void login(View view) {
    }
}