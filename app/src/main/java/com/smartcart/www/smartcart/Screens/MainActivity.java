package com.smartcart.www.smartcart.Screens;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartcart.www.smartcart.Fragments.EmailFragment;
import com.smartcart.www.smartcart.Fragments.HistoryFragment;
import com.smartcart.www.smartcart.Fragments.ItemsListFragment;
import com.smartcart.www.smartcart.Fragments.MyGroceryList;
import com.smartcart.www.smartcart.R;


public  class MainActivity extends AppCompatActivity  {

    private static final String TAG = "myTag";

    private DatabaseReference userDB;
    private FirebaseAuth mAuth;

    private String region, chain,cUserId;

    private AlertDialog.Builder alertDialog;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private Toolbar mToolBar;

    private Context mContext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        region = sharedPreferences.getString("region", "random");
        chain = sharedPreferences.getString("chain", "random");


        mContext = MainActivity.this;

        userDB = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        cUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mToolBar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolBar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //תפריט הצד במסך
        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

//        מסך הצד בפרויקט
        navigationView = findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,new ItemsListFragment()).addToBackStack(null).commit();
        //הכפתור שפותח את הדיאלוג על גבי המסך הראשי

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        } else if (item.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.nav_contactUs:
                EmailFragment emailFragment = new EmailFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,emailFragment).addToBackStack(null).commit();
                Toast.makeText(this, "דיווח על תקלה", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_history:
                // TODO: 18.12.2018 need to check if we already in nav_history
                HistoryFragment historyFragment = new HistoryFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,historyFragment).addToBackStack(null).commit();
                Toast.makeText(this, "היסטוריה", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_pricesMatch:
               toastMessage("השוואת מחירים-בעדכון גירסה הבא");
                break;
            case R.id.nav_myGroceryList:
                MyGroceryList myGroceryList = new MyGroceryList();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragments_container,myGroceryList).addToBackStack(null).commit();
                Toast.makeText(this, "רשימת הקניות שלי", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                mAuth.signOut();
                LoginManager.getInstance().logOut();
                SendUserToLoginActivity();
                break;
        }
        drawerLayout = ( DrawerLayout ) findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }

    private void SendUserToMainActivity() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void toastMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
       if (getSupportFragmentManager().getBackStackEntryCount()>1){
           getSupportFragmentManager().popBackStack();
           return;
       }

        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("אזהרה");
        alertDialog.setCancelable(true);
        alertDialog.setMessage("הינך עומד לחזור למסך הקודם,במידה ותעשה זאת הנתונים שלך יימחקו,לביטול לחץ 'לא'");
        alertDialog.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                userDB.child("Supers").child(cUserId)
                        .child(region)
                        .child(chain).removeValue();

                SendUserToSecondStage();
            }
        });
        alertDialog.setNegativeButton("לא", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.create();
        alertDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds cartList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }


    private void SendUserToSecondStage() {
        Intent mainIntent = new Intent(MainActivity.this, SecondStage.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}









