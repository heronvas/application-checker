package com.example.phoneotp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Spinner spinner;
    EditText phonenumber;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.countryCode);
        phonenumber = findViewById(R.id.number);
        submit = findViewById(R.id.otp);

        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, CountryData.countryNames));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CountryData.countryAreaCodes[spinner.getSelectedItemPosition()];
                String p_number = phonenumber.getText().toString().trim();

                if(p_number.isEmpty() || p_number.length()!=10){
                    phonenumber.setError("Valid phone number is required");
                    phonenumber.requestFocus();
                    return;
                }

                String phoneNo = "+"+code+p_number;

                Intent intent = new Intent(MainActivity.this, verification.class);

                intent.putExtra("phoneNum", phoneNo);
                startActivity(intent);



            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this, prof.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}