package com.smartcart.www.smartcart.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by shyattoun on 24.12.2018.
 */

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.CartViewHolder> {

    private static final String TAG = "MyAdapter";
    private static final int NUM_GRID_COLUMNS = 4;
    private List<ItemsList> mList;
    private Context mContext;

    public MyCartAdapter(Context mContext, List<ItemsList> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyCartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_table_list_history,parent,false);
        return new MyCartAdapter.CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int i) {
        ItemsList itemsList = mList.get(holder.getAdapterPosition());
        Float f = itemsList.getTableTotalPricePerItem();
        holder.description.setText(itemsList.getTableDescription());
        holder.price.setText(itemsList.getTablePrice());
        holder.quantity.setText(itemsList.getTableQuantity());
        holder.totalPricePerItem.setText(String.format(Locale.CANADA,"%.2f",f));

        Glide.with(mContext)
                .load(itemsList.getProductImage())
                .into(holder.productPic);
    }

    @Override
    public int getItemCount () {
        return mList.size();
    }

    public void addItem(ItemsList itemsList){
        mList.add(itemsList);
        notifyItemInserted(getItemCount()-1);
    }

    public void restoreItem(ItemsList item, int position) {
        mList.add(position, item);
        notifyItemInserted(position);
    }

    public void clear (){
        mList.clear();
        notifyDataSetChanged();
    }

    public  ItemsList getItemList(int pos){
        return mList.get(pos);
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView quantity;
        private TextView price;
        private TextView totalPricePerItem;
        private ImageView productPic;

        public CartViewHolder(View itemView) {
            super(itemView);
            description = ( TextView ) itemView.findViewById(R.id.tvDescriptionHistory);
            quantity = ( TextView ) itemView.findViewById(R.id.tvQuantityHistory);
            price = ( TextView ) itemView.findViewById(R.id.tvHistory);
            productPic = ( ImageView ) itemView.findViewById(R.id.ivProductHistory);
            totalPricePerItem = (TextView) itemView.findViewById(R.id.tvTotalHistory);

            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
            productPic.setMaxHeight(imageWidth);
            productPic.setMaxWidth(imageWidth);
        }
    }



}