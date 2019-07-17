package com.smartcart.www.smartcart.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcart.www.smartcart.Classes.Cart;
import com.smartcart.www.smartcart.Fragments.CartFragment;
import com.smartcart.www.smartcart.R;
import com.smartcart.www.smartcart.Screens.MainActivity;

import java.util.List;

/**
 * Created by shyattoun on 19.12.2018.
 */

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.HistoryViewHolder> {

    private static final String TAG = "MyHistoryAdapter";
    private List<Cart> hList;
    private Context mContext;

    public MyHistoryAdapter(Context mContext, List<Cart> hList) {
        this.hList = hList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_location,parent,false);
        return new MyHistoryAdapter.HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        final Cart cart = hList.get(holder.getAdapterPosition());
        holder.date.setText(cart.getDate());
        holder.price.setText(String.valueOf(cart.getPrice()));
        holder.location.setText(cart.getLocation());
        holder.btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity )mContext)
                        .getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fragments_container, CartFragment.newInstance(cart.getCartID()))
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


    @Override
    public int getItemCount () {
        return hList.size();
    }

    public  Cart getCart(int pos){
        return hList.get(pos);
    }
    public void addItem(Cart cart){
        hList.add(cart);
        notifyItemInserted(getItemCount()-1);
    }
    public void clear (){
        hList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        hList.remove(position);
        notifyItemRemoved(position);
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView location;
        private TextView price;
        private TextView btnShow;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tvDate);
            location = itemView.findViewById(R.id.tvLocation);
            price = itemView.findViewById(R.id.tvSum);
            btnShow = itemView.findViewById(R.id.tvWatchDetails);

        }
    }



}