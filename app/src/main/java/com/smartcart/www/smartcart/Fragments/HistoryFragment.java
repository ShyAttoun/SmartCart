package com.smartcart.www.smartcart.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;
import com.smartcart.www.smartcart.Adapters.MyHistoryAdapter;
import com.smartcart.www.smartcart.Classes.Cart;
import com.smartcart.www.smartcart.Animator.MyHistorySwiper;
import com.smartcart.www.smartcart.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment implements MyHistorySwiper.MySwipeListener {

    private DatabaseReference userDB;


    private TextView hDate,hLocation,hSum,hDetails;
    private RecyclerView hRecyclerView;
    private MyHistoryAdapter adapter;
    private List<Cart>hList;
    private Context mContext;

    private String current_user_id,itemId, region, chain;
    private ChildEventListener childEventListener;

    private SharedPreferences sharedPreferences;


    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mContext = getContext();


        sharedPreferences = getActivity().getApplicationContext().getSharedPreferences("db", MODE_PRIVATE);
        region = sharedPreferences.getString("region", "random");
        chain = sharedPreferences.getString("chain", "random");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user_id = user.getUid();
        userDB = FirebaseDatabase.getInstance().getReference();
        hList = new ArrayList<>();
        adapter = new MyHistoryAdapter(getContext(),hList);
      return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViewById(view);


        hRecyclerView = view.findViewById(R.id.rcvl);
        hRecyclerView.setHasFixedSize(true);
        hRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        hRecyclerView.setItemAnimator(new DefaultItemAnimator());
        hRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));



        hRecyclerView.setAdapter(adapter);

        userDB.child("History").child(current_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("database",dataSnapshot.toString());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart data = snapshot.getValue(Cart.class);
                    adapter.addItem(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ItemTouchHelper.SimpleCallback simpleCallback = new MyHistorySwiper(0,ItemTouchHelper.LEFT,getContext(),this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(hRecyclerView);
    }



    private void findViewById(View rootView){
        hDate = (TextView) rootView.findViewById(R.id.tvDate);
        hLocation = (TextView) rootView.findViewById(R.id.tvLocation);
        hSum = (TextView) rootView.findViewById(R.id.tvSum);
        hDetails = (TextView) rootView.findViewById(R.id.tvWatchDetails);

    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        final Cart cart = adapter.getCart(position);

        if (direction == ItemTouchHelper.LEFT) {
            String id = cart.getCartID();
            userDB.child("History").child(current_user_id).child(id).setValue(null)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(), "הרשומה נמחקה בהצלחה!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "מחיקת הרשומה נכשלה", Toast.LENGTH_SHORT).show();

                }
            });
            adapter.removeItem(position);

                        }

        }
    }


