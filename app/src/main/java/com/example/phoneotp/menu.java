package com.example.phoneotp;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

public class menu extends AppCompatActivity {
    int submittedApplication = 0;
    int Pending = 0;
    int ApprovedApplication = 0;
    int RejectedApplication = 0;
    int Leading = 0;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText phonenum, propertyname, city, locality, ownerName, preffLang;
    private Button submit, cancel, submitEntry;
    private TextView appSub, pendingApp, appApproved, appRejected, leadGenerated;
    DatabaseReference dbref, getRef, oref, checkRef;
    FirebaseUser user ;
    int cnt=0;
    int count=0;
    HashSet<String> numbers = new HashSet<String>();
    HashSet<String> nums = new HashSet<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        submitEntry = (Button) findViewById(R.id.submit);
        appSub = (TextView) findViewById(R.id.submitt);
        pendingApp = (TextView) findViewById(R.id.pending);
        appApproved = (TextView) findViewById(R.id.approved);
        appRejected = (TextView) findViewById(R.id.rejected);
        leadGenerated = (TextView) findViewById(R.id.lead);
        user = FirebaseAuth.getInstance().getCurrentUser();
        dbref = FirebaseDatabase.getInstance().getReference().child(user.getUid());
        getRef = FirebaseDatabase.getInstance().getReference();
        checkRef = FirebaseDatabase.getInstance().getReference().child("clients");
        getRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                     if(user.getUid()==snapshot.getKey()){
                        Log.d("mes", String.valueOf(snapshot.child("Pending Approval").getValue()));
                        pendingApp.setText("Pending Approval: "+snapshot.child("Pending Approval").getValue());
                        appSub.setText("Application Submitted: "+snapshot.child("Application Submitted").getValue());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        submitEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createpopup();
                Pending += 1;
                //dbref.child(String.valueOf(user.getUid()));
                dbref.child("Pending Approval").setValue(Pending);
                getRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                //Log.d("TAG", snapshot.getKey());
                            //Log.d("TAG", String.valueOf(user.getUid()==snapshot.getKey()));
                            if(user.getUid()==snapshot.getKey()){
                                Log.d("mes", String.valueOf(snapshot.child("Pending Approval").getValue()));
                                pendingApp.setText("Pending Approval: "+snapshot.child("Pending Approval").getValue());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }

    public void createpopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View popup = getLayoutInflater().inflate(R.layout.activity_popentry, null);
        phonenum = (EditText) popup.findViewById(R.id.pnumber);
        propertyname = (EditText) popup.findViewById(R.id.propertyname);
        city = (EditText) popup.findViewById(R.id.cityname);
        locality = (EditText) popup.findViewById(R.id.area);
        ownerName = (EditText) popup.findViewById(R.id.ownername);
        preffLang = (EditText) popup.findViewById(R.id.prefferedlanguage);
        submit = (Button) popup.findViewById(R.id.submitted);
        cancel = (Button) popup.findViewById(R.id.cancel);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String phoneNum = phonenum.getText().toString();
                final String property = propertyname.getText().toString();
                final String City = city.getText().toString();
                final String Area = locality.getText().toString();
                String Owner = ownerName.getText().toString();
                String Language = preffLang.getText().toString();
                final String ownerName, lang;
                cnt=1;
                nums=numbers;




                if(phoneNum.isEmpty()){
                    phonenum.setError("fill the details");
                    phonenum.requestFocus();
                    cnt=0;
                    return;
                }
                if (phoneNum.length()!=10){
                    phonenum.setError("Invalid Number");
                    phonenum.requestFocus();
                    cnt=0;
                    return;
                }
                if(property.isEmpty()){
                    propertyname.setError("fill the details");
                    propertyname.requestFocus();
                    cnt=0;
                    return;
                }
                if(City.isEmpty()){
                    city.setError("fill the details");
                    city.requestFocus();
                    cnt=0;
                    return;
                }
                if(Area.isEmpty()){
                    locality.setError("fill the details");
                    locality.requestFocus();
                    cnt=0;
                    return;
                }


                if(Owner.isEmpty()){
                    ownerName="NA";
                }
                else{
                    ownerName = Owner;
                }


                if(Language.isEmpty()){
                    lang="NA";
                }
                else{
                    lang = Language;
                }


                    if(cnt==1){
                        if(Pending>0){
                            Pending -= 1;
                        }


                        submittedApplication += 1;

                        dbref.child("Pending Approval").setValue(Pending);
                        dbref.child("Application Submitted").setValue(submittedApplication);
                        appSub.setText("Application Submitted: "+submittedApplication);
                        pendingApp.setText("Pending Approval: "+Pending);
                    }

                //oref = dbref.child("clients").child(phoneNum);
                dbref.child("clients").child(phoneNum).child("Pending Approval").setValue(property);
                dbref.child("clients").child(phoneNum).child("city name").setValue(City);
                dbref.child("clients").child(phoneNum).child("Area/Locality").setValue(Area);
                dbref.child("clients").child(phoneNum).child("Owner name").setValue(ownerName);
                dbref.child("clients").child(phoneNum).child("preffered language").setValue(lang);
                Toast.makeText(getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();



                getRef.child(user.getUid()).child("clients").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){



                            numbers.add(snapshot.getKey());
                            Log.d("counter", String.valueOf(numbers));
                            //dbref.child("Pending Approval").setValue(property);

                            //Log.d("TAG", String.valueOf(numbers));




                        }
                        //String[] arrays = numbers.toArray(new String[numbers.size()-1]);
                        //Log.d("counter", String.valueOf(arrays[numbers.size()-1]));



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                for(String i: nums){
                    Log.d("check", String.valueOf((i)));
                    Log.d("check", String.valueOf((phoneNum)));
                    Log.d("check", String.valueOf((i.equals(phoneNum))));
                    //Log.d("check", String.valueOf(("hello".equals("hello"))));
                    if(i.equals(phoneNum)){
                        Log.d("check", "hello");
                        Toast.makeText(getApplicationContext(),"already there",Toast.LENGTH_SHORT).show();

                        //Log.d("msss", "done");

                    }



                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(submittedApplication>0){
                    submittedApplication -= 1;
                    dbref.child("Application Submitted").setValue(submittedApplication);
                }



                dbref.child("Application Submitted").setValue(submittedApplication);
                appSub.setText("Application Submitted: "+submittedApplication);
            }
        });


    }
}