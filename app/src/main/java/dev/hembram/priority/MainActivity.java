package dev.hembram.priority;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import dev.hembram.priority.Dashboard.DashboardActivity;
import dev.hembram.priority.Login.LoginActivity;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getBaseContext());

        auth= FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(this, DashboardActivity.class));
        }else {

            startActivity(new Intent(this, LoginActivity.class));

        }
        finish();
    }
}