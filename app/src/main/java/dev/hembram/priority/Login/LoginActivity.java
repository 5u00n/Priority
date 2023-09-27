package dev.hembram.priority.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import dev.hembram.priority.Dashboard.DashboardActivity;
import dev.hembram.priority.Models.UsersModel;
import dev.hembram.priority.R;

public class LoginActivity extends AppCompatActivity {



    TextInputEditText text_c_code, text_phone,text_otp;

    MaterialButton button_send_otp, button_login;

    ProgressBar progressBar;


    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;


    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String codesent;
    private String phonenumber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initElements();

        buttonOperation();

    }

    void initElements(){

        auth=FirebaseAuth.getInstance();
        text_otp= findViewById(R.id.login_text_otp);
        text_c_code= findViewById(R.id.login_text_code);
        text_phone= findViewById(R.id.login_text_phone);

        button_login= findViewById(R.id.login_button_login);
        button_send_otp= findViewById(R.id.login_button_send_otp);

        progressBar= findViewById(R.id.progressBar);
    }

    void buttonOperation(){
        button_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number,countrycode;
                number = text_phone.getText().toString();
                if (number.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please Enter Your number", Toast.LENGTH_SHORT).show();
                } else if (number.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Please Enter correct number", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    countrycode= text_c_code.getText().toString();
                    if(countrycode.equals("")){
                        countrycode="+91";
                    }
                    phonenumber = countrycode + number;

                    Log.d("PHONE","--"+countrycode+"--  "+phonenumber);

                    //otpAcceptance();

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phonenumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginActivity.this)
                            .setCallbacks(mCallbacks)
                            .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string_otp=text_otp.getText().toString();
                if(string_otp==null){
                    Toast.makeText(LoginActivity.this,"Please Enter OTP to proceed !",Toast.LENGTH_SHORT).show();
                }else{

                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codesent,string_otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        text_otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==6){

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    hideKeyboard(text_otp);
                }

            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                //how to automatically fetch code here
                String code = phoneAuthCredential.getSmsCode();
                if (code != null) {
                    // Automatically fill the OTP in the text field
                    text_otp.setText(code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                    otpAcceptance();
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
               progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this,"Sending OTP failed, try again!",Toast.LENGTH_SHORT).show();
                button_send_otp.setText("Try Again");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(getApplicationContext(), "OTP Sent", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
                codesent = s;
                //Intent intent = new Intent(LoginActivity.this, OTPAuthenticationActivity.class);
                //intent.putExtra("otp", codesent);
                //intent.putExtra("phone", phonenumber);

                //startActivity(intent);
                otpAcceptance();
            }
        };
    }


    void otpAcceptance(){
        findViewById(R.id.textInputLayout2).setVisibility(View.VISIBLE);
        text_otp.requestFocus();
        button_login.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);



    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Login success", Toast.LENGTH_SHORT).show();
                    checkUsersAndUpdate();

                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    void checkUsersAndUpdate(){
        database= FirebaseDatabase.getInstance();
        reference= database.getReference("users").child(auth.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    UsersModel model = new UsersModel(auth.getUid(), "User_"+auth.getUid(),String.valueOf((System.currentTimeMillis() / 1000)),"--");
                    reference.setValue(model);
                    goToNextActivity();
                }else {
                    goToNextActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    void goToNextActivity() {
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

}