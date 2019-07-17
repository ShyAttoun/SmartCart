package com.smartcart.www.smartcart.Screens;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartcart.www.smartcart.R;

import java.util.ArrayList;
import java.util.List;

public class SecondStage extends AppCompatActivity {

    private Button btnSaveDataOfSpinner;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;

    private String id;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference SuperRef;
    private String current_user_id, chain, region;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_stage);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String signedInUser;
        if (bundle != null) {
            signedInUser = bundle.getString("user");
        }

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            current_user_id = user.getUid();
        }


        btnSaveDataOfSpinner = ( Button ) findViewById(R.id.btnSaveData);


        final Spinner spinnerRegion = findViewById(R.id.spinnerRegion);
        List<String> list1 = new ArrayList<String>();
        list1.add("צפון");
        list1.add("מרכז");
        list1.add("דרום");
        ArrayAdapter<String> spinnerRegionAdapter = new ArrayAdapter<String>(SecondStage.this, android.R.layout.simple_spinner_item, list1);
        spinnerRegionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegion.setAdapter(spinnerRegionAdapter);

        final Spinner spinnerChain = findViewById(R.id.spinnerChain);
        List<String> list2 = new ArrayList<String>();
        list2.add("רמי לוי");
        list2.add("חצי חינם");
        list2.add("שופרסל");
        list2.add("טיב טעם");
        list2.add("יינות ביתן");
        list2.add("AM:PM");
        list2.add("סופר שוק יוחננוף");
        list2.add("ויקטורי");
        list2.add("מחסני השוק");
        list2.add("אלון דור");
        list2.add("מזון מרב כל");
        list2.add("אחר");


        ArrayAdapter<String> spinnerChainAdapter = new ArrayAdapter<String>(SecondStage.this, android.R.layout.simple_spinner_item, list2);
        spinnerChainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChain.setAdapter(spinnerChainAdapter);


        btnSaveDataOfSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chain = spinnerChain.getSelectedItem().toString();
                region = spinnerRegion.getSelectedItem().toString();

                SuperRef = mFirebaseDatabase.getReference("Supers").child(current_user_id);

                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait, while we are creating your new Account...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("region", region);
                editor.putString("chain", chain);
                editor.commit();
                Intent intent = new Intent(SecondStage.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(SecondStage.this, " בחרת ב: " + " אזור " + String.valueOf(spinnerRegion.getSelectedItem()) +
                                " בסניף " + String.valueOf(spinnerChain.getSelectedItem()),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            updateUI();
        }

    }

    private void updateUI() {

        Toast.makeText(SecondStage.this, "התנתקת מהמשתמש", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(SecondStage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
            return;
        }
    }
}



