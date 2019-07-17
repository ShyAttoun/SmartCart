package com.smartcart.www.smartcart.Screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartcart.www.smartcart.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{
    private EditText UserEmail, UserPassword, UserConfirmPassword,FullName, Age;
    private Button SaveInformationbuttion;
    private RadioButton genderMale,genderFemale,radioButton;
    private RadioGroup radioGroup;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference userDB;
    public String current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();



        userDB = FirebaseDatabase.getInstance().getReference("User info");

        genderFemale = (RadioButton ) findViewById(R.id.radioFemale);
        genderMale = (RadioButton ) findViewById(R.id.radioMale);
        radioGroup = (RadioGroup ) findViewById(R.id.radio);
        FullName = (EditText) findViewById(R.id.etFullName);
        Age = (EditText)findViewById(R.id.etAge);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserConfirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        SaveInformationbuttion = (Button)findViewById(R.id.btnCreate) ;
        loadingBar = new ProgressDialog(this);


        SaveInformationbuttion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }



    @Override
    protected void onStart()
    {
        super.onStart();


    }

   



    private void CreateNewAccount()
    {
        final String fullname = FullName.getText().toString();
        final String age = Age.getText().toString();
        String male = genderMale.getText().toString();
        String female = genderFemale.getText().toString();
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "אנא הכנס/י את כתובת האי מייל שלך...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "אנא הכנס/י את סיסמתך...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(confirmPassword))
        {
            Toast.makeText(this, "אנא אמת/י את סיסמתך...", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "הסיסמה שלך לא תואמת...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname))
        {
            Toast.makeText(this, "אנא הכנס/י את שמך המלא...", Toast.LENGTH_SHORT).show();
        }

        else if(TextUtils.isEmpty(age))
        {
            Toast.makeText(this, "אנא הכנס/י את גילך...", Toast.LENGTH_SHORT).show();
        }

        else
        {
            loadingBar.setTitle("יוצר משתמש חדש");
            loadingBar.setMessage("אנא המתן בזמן שאנו יוצרים עבורך משתמש חדש...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            checkButton();
            final HashMap userMap = new HashMap();

            userMap.put("fullname", fullname);
            userMap.put("age", age);
            userMap.put("gender",radioButton.getText().toString());

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {

                                userDB.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                                SendUserToSecondStageActivity();

                                Toast.makeText(RegisterActivity.this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "התרחשה תקלה: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent =new Intent(RegisterActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        SendUserToLoginActivity();
    }

    private void SendUserToSecondStageActivity()
    {
        Intent mainIntent = new Intent(RegisterActivity.this, SecondStage.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void checkButton (){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton =findViewById(radioId);
    }
}