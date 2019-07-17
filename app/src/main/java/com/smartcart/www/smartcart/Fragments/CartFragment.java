package com.smartcart.www.smartcart.Fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartcart.www.smartcart.Adapters.MyCartAdapter;
import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {

    private DatabaseReference userDB;

    private RecyclerView rvItemsList;
    private List<ItemsList> list = new ArrayList<>();
    private MyCartAdapter adapter;
    private String current_user_id,region,chain;
    private SharedPreferences sharedPreferences;

    public CartFragment() {
        // Required empty public constructor
    }

   public static CartFragment newInstance(String key) {
        Bundle args = new Bundle();
        args.putString("key",key);
        CartFragment fragment = new CartFragment();
        fragment.setArguments(args);
        return fragment;
     }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        region = sharedPreferences.getString("region", "random");
        chain = sharedPreferences.getString("chain", "random");

        userDB = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = user.getUid();
        rvItemsList = view.findViewById(R.id.rclCartFragment);
        rvItemsList.setHasFixedSize(true);
        rvItemsList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItemsList.setItemAnimator(new DefaultItemAnimator());
        rvItemsList.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adapter = new MyCartAdapter(getContext(),list);
        rvItemsList.setAdapter(adapter);
        userDB.child("Carts").child(current_user_id).child(getArguments().getString("key")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("database",dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ItemsList data = snapshot.getValue(ItemsList.class);
                    adapter.addItem(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



    }

}
