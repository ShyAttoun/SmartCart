package com.smartcart.www.smartcart.Screens;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.smartcart.www.smartcart.R;
import com.smartcart.www.smartcart.WalkThrough.OnBoardActivity;

/**
 * Created by shyattoun on 4.4.2018.
 */

public class LogoActivity extends AppCompatActivity {

    private ImageView ivLogo;
    private TextView tvLogo;
    private TextView tvSlogan;

    private FirebaseAuth mAuth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        animateToMain ();

        ivLogo=(ImageView)findViewById(R.id.ivLogo);
        tvLogo=(TextView)findViewById(R.id.tvLogo);
        tvSlogan=(TextView)findViewById(R.id.tvSlogan);

        ivLogo.animate().setDuration(3000);

    }

    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(LogoActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void animateToMain() {
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent= new Intent(LogoActivity.this,OnBoardActivity.class);


                startActivity(intent);
            }
        },3000);
    }


}
