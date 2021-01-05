package com.example.phoneotp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class verification extends AppCompatActivity {

    EditText verCode;
    Button send;
    ProgressBar prog;
    private String verifId;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.num_verif);
        auth = FirebaseAuth.getInstance();
        String phoneNumber = getIntent().getStringExtra("phoneNum");
        verCode = findViewById(R.id.Code);
        send = findViewById(R.id.submit);
        prog = findViewById(R.id.progress);

        sendVerif(phoneNumber);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = verCode.getText().toString().trim();

                if (code.isEmpty() || code.length()<6){
                    verCode.setError("Invalid Code");
                    verCode.requestFocus();
                    return;
                }


                verifyCode(code);
            }
        });

    }
    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifId, code);
        signInwithCredentials(credential);
    }

    private void signInwithCredentials(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(verification.this, prof.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(verification.this, task.getException().getMessage(), Toast.LENGTH_LONG);
                }
            }
        });
    }


    private void sendVerif(String number){
        prog.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallback);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verifId =s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();

            if (code != null){
                verCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}
