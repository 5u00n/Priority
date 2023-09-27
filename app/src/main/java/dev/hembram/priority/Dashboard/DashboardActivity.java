package dev.hembram.priority.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import dev.hembram.priority.Login.LoginActivity;
import dev.hembram.priority.MainActivity;
import dev.hembram.priority.R;


public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    //Firebase
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;


    private static final int MY_LOCATION_REQUEST_CODE = 2;
    TabLayout tabLayout;
    ViewPager viewPager;

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;


    TextView today_text, attendance_status;
    Button manual_attendance;


    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "MyNotificationChannel";
    private static final String CHANNEL_NAME = "My Notification Channel";
    private static final String CHANNEL_DESC = "Channel for my daily notifications";
    String today;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);


        toolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(toolbar);


        drawerLayout = findViewById(R.id.dashboard_drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.dashboard_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = findViewById(R.id.home_viewpager);
        tabLayout = findViewById(R.id.home_tabs);

        tabLayout.addTab(tabLayout.newTab().setText("Important"));
        tabLayout.addTab(tabLayout.newTab().setText("Blank"));
        tabLayout.addTab(tabLayout.newTab().setText("Blank"));


        getPermission();
        updateUI();

    }

    void updateUI() {

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = simpleDateFormat.format(Calendar.getInstance().getTime());
        databaseReference = database.getReference("Attendance").child(auth.getUid()).child(today);

        today_text = findViewById(R.id.dashboard_text_date);
        attendance_status = findViewById(R.id.dashboard_text_attendance_status);
        //manual_attendance = findViewById(R.id.dashboard_button_manual_attendance);

        manual_attendance.setOnClickListener(v -> {
            /*Intent intent = new Intent(DashboardActivity.this, DetectFaceActivity.class);
            startActivityForResult(intent, 2);*/
        });


        today_text.setText(new SimpleDateFormat("E, dd MMMM").format(Calendar.getInstance().getTime()));


        // tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        /*viewPager.setAdapter(new DashboardAdapter(DashboardActivity.this, getSupportFragmentManager(), tabLayout.getTabCount()));
        viewPager.getCurrentItem();


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });*/


        databaseReference.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String state = snapshot.getValue().toString();
                    attendance_status.setText(state);
                    if (state.equals("Present")) {
                        attendance_status.setTextColor(Color.GREEN);
                    }
                    if (state.equals("Absent")) {
                        attendance_status.setTextColor(Color.RED);
                    }
                } else {
                    attendance_status.setText("NAN");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 2) {
            String message = data.getStringExtra("AUTH");
            if (message.equals("TRUE")) {
                Toast.makeText(this, "Attendance Marked!", Toast.LENGTH_SHORT).show();
                databaseReference.child("status").setValue("Present");
            }
            if (message.equals("FALSE")) {
                Toast.makeText(this, "Face Data didn't match Please try Again !", Toast.LENGTH_SHORT).show();
                databaseReference.child("status").setValue("Absent");
            }

        }
    }


    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST_CODE);

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        final int menu_id=item.getItemId();
        drawerLayout.closeDrawer(GravityCompat.START);

        if(menu_id==R.id.menu_logout){
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return true;
    }


    @Override
    protected void onResume() {
        updateUI();
        super.onResume();
    }

    @Override
    protected void onStart() {

        super.onStart();
        updateUI();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }
}