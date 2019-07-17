package com.smartcart.www.smartcart.Fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smartcart.www.smartcart.Adapters.MyAdapter;
import com.smartcart.www.smartcart.Animator.MySwipe;
import com.smartcart.www.smartcart.Classes.Cart;
import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.Dialoges.EditItemDialog;
import com.smartcart.www.smartcart.Dialoges.MyCustomDialog;
import com.smartcart.www.smartcart.R;
import com.smartcart.www.smartcart.Screens.SecondStage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsListFragment extends Fragment implements MySwipe.MySwipeListener {
    private Button btnAddItemDialog,btnSave,btnGrocery;
    private TextView tvPrice,tvQuantity;
    private RecyclerView rvItemsList;
    private List<ItemsList> itemsLists;
    private MyAdapter adapter;
    private AlertDialog.Builder alertDialog;
    private DatabaseReference userDB,historyUserDB;
    private FirebaseUser cUser;
    private ChildEventListener childEventListener;
    private SharedPreferences sharedPreferences;
    private ImageView imageView;

    private String region,chain;

    public ItemsListFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflater.inflate(R.layout.fragment_items_list, container, false);

        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        region = sharedPreferences.getString("region", "random");
        chain = sharedPreferences.getString("chain", "random");

        userDB = FirebaseDatabase.getInstance().getReference();
        cUser = FirebaseAuth.getInstance().getCurrentUser();
        historyUserDB = FirebaseDatabase.getInstance().getReference("History").child(cUser.getUid());
        itemsLists = new ArrayList<>();


        return  inflater.inflate(R.layout.fragment_items_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);

        rvItemsList.setHasFixedSize(true);
        rvItemsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItemsList.setItemAnimator(new DefaultItemAnimator());
        rvItemsList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        adapter = new MyAdapter(getContext(),itemsLists);
        rvItemsList.setAdapter(adapter);


        getDataFirebase();

        btnAddItemDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCustomDialog dialog = new MyCustomDialog();
                dialog.show(getChildFragmentManager(), "MyCustomDialog");

            }
        });

        btnGrocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyGroceryList myGroceryList= new MyGroceryList();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragments_container, myGroceryList ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String key = historyUserDB.push().getKey();
                Calendar calendar = Calendar.getInstance();
                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                double price = Double.parseDouble(tvPrice.getText().toString());
                String location = region + " " + chain;
                Cart cart = new Cart(key, currentDate, location, price);

                historyUserDB.child(key).setValue(cart)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                userDB.child("Carts").child(cUser.getUid()).child(key)
                                        .setValue(itemsLists)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                toastMessage("הרשימה נשמרה בהצלחה!");
                                                userDB.child("Supers").child(cUser.getUid())
                                                        .child(region)
                                                        .child(chain).removeValue();
                                                adapter.clear();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toastMessage("מצטערים,לא הצלחנו לשמור את הרשימה");

                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage("לא הצלחנו לשמור את הרשימה");
                            }
                        });

            }
        });
        ItemTouchHelper.SimpleCallback simpleCallback = new MySwipe(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,getContext(),this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rvItemsList);
    }



    @Override
    public void onStart() {
        super.onStart();
        itemsLists.clear();
        userDB.child("Supers").child(cUser.getUid())
                .child(region)
                .child(chain)
                .addChildEventListener(childEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        userDB.child("Supers").child(cUser.getUid())
                .child(region)
                .child(chain)
                .removeEventListener(childEventListener);
    }

    private void findViewById(View rootview){
        btnAddItemDialog = rootview.findViewById(R.id.btnAddItemDialog);
        btnSave = rootview.findViewById(R.id.btnSave);
        tvPrice = rootview.findViewById(R.id.tvPrice);
        tvQuantity = rootview.findViewById(R.id.tvQuantity);
        rvItemsList = rootview.findViewById(R.id.rvItemsList);
        btnGrocery = rootview.findViewById(R.id.btnCreateGroceryList);
    }


    private void getDataFirebase() {
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ItemsList data = dataSnapshot.getValue(ItemsList.class);
                adapter.addItem(data);
                addToSum(Double.parseDouble(String.valueOf(data.getTableTotalPricePerItem())));
                addToQuantity(Integer.parseInt(data.getTableQuantity()));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                final ItemsList data = dataSnapshot.getValue(ItemsList.class);
                changeSum(data.getItemID());
                changeSumQuantity(data.getItemID());
                adapter.itemHasChanged(data);
                addToSum(Double.parseDouble(String.valueOf(data.getTableTotalPricePerItem())));
                addToQuantity(Integer.parseInt(data.getTableQuantity()));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                final ItemsList data = dataSnapshot.getValue(ItemsList.class);
                substractFromSum(Double.parseDouble(String.valueOf(data.getTableTotalPricePerItem())));
                substractFromSumQuantity(Integer.parseInt(data.getTableQuantity()));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    private void  addToQuantity(int quantity){
        int sum = Integer.parseInt(tvQuantity.getText().toString());
        sum += quantity;
        tvQuantity.setText(String.valueOf(sum));
    }

    private void changeSumQuantity(String id) {
        for (ItemsList itemsList : itemsLists) {
            if (itemsList.getItemID().equals(id)) {
                int sum = Integer.parseInt(tvQuantity.getText().toString());
                sum -= Double.parseDouble(itemsList.getTableQuantity());
                tvQuantity.setText(String.valueOf(sum));
            }
        }
    }

    private void substractFromSumQuantity(int quantity) {
        int sum = Integer.parseInt(tvQuantity.getText().toString());
        sum -= quantity;
        tvQuantity.setText(String.valueOf(sum));
    }

    @SuppressLint("DefaultLocale")
    private void addToSum(double price) {
        double sum = Double.parseDouble(tvPrice.getText().toString());
        sum += price;
        tvPrice.setText(String.format("%.2f",sum));
    }

    @SuppressLint("DefaultLocale")
    private void changeSum(String id) {
        for (ItemsList itemsList : itemsLists) {
            if (itemsList.getItemID().equals(id)) {
                double sum = Double.parseDouble(tvPrice.getText().toString());
                sum -= Double.parseDouble(String.valueOf(itemsList.getTableTotalPricePerItem()));
                tvPrice.setText(String.valueOf(sum));
                tvPrice.setText(String.format("%.2f",sum));
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private void substractFromSum(double price) {
        double sum = Double.parseDouble(tvPrice.getText().toString());
        sum -= price;
        tvPrice.setText(String.valueOf(sum));
        tvPrice.setText(String.format("%.2f",sum));

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        final ItemsList itemsList = adapter.getItemList(position);

        if (direction == ItemTouchHelper.LEFT) {
            String id = itemsList.getItemID();
            userDB.child("Supers").child(cUser.getUid()).child(region)
                    .child(chain).child(id).setValue(null)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //do whatever u want here
                            Toast.makeText(getContext(), "המוצר נמחק בהצלחה!", Toast.LENGTH_SHORT).show();
                        }
                    });

            adapter.removeItem(position);
        } else {

            itemsLists.get(position);
            EditItemDialog editItemDialog = EditItemDialog.newInstance(itemsList.getItemID(),region,chain);
            editItemDialog.show(getChildFragmentManager(),null);
            adapter.notifyDataSetChanged();


        }
    }

    private void SendUserToSecondStage() {
        Intent mainIntent = new Intent(getContext(), SecondStage.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }


    private void toastMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
