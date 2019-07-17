package com.smartcart.www.smartcart.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartcart.www.smartcart.Classes.ItemsList;
import com.smartcart.www.smartcart.GlideApp;
import com.smartcart.www.smartcart.R;

import java.util.List;
import java.util.Locale;

/**
 * Created by shyattoun on 12.5.2018.
 */



public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {



    private static final String TAG = "MyAdapter";
    private static final int NUM_GRID_COLUMNS = 4;
    private List<ItemsList> mList;
    private Context mContext;


    public MyAdapter(Context mContext, List<ItemsList> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.table_list_items,parent,false);
        return new MyAdapter.ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ItemsList itemsList = mList.get(holder.getAdapterPosition());
        Float f = itemsList.getTableTotalPricePerItem();
        holder.description.setText(itemsList.getTableDescription());
        holder.price.setText(itemsList.getTablePrice());
        holder.quantity.setText(itemsList.getTableQuantity());
        holder.totalPricePerItem.setText(String.format(Locale.CANADA,"%.2f",f));


        GlideApp.with(mContext)
                .load(itemsList.getProductImage()).centerCrop()
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

    public void itemHasChanged(ItemsList newItemList){

        for (ItemsList itemsList : mList) {
            if (itemsList.getItemID().equals(newItemList.getItemID())){
                final int position = mList.indexOf(itemsList);
                mList.set(position,newItemList);
                notifyItemChanged(position);
                return;
            }
        }

    }


    public void clear (){
        mList.clear();
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(ItemsList item, int position) {
        mList.add(position, item);

        notifyItemInserted(position);
    }

    public  ItemsList getItemList(int pos){
        return mList.get(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private TextView quantity;
        private TextView price;
        private TextView totalPricePerItem;
        private ImageView productPic;

        public ViewHolder(View itemView) {
            super(itemView);
            description = ( TextView ) itemView.findViewById(R.id.tvDescription);
            quantity = ( TextView ) itemView.findViewById(R.id.tvQuantity);
            price = ( TextView ) itemView.findViewById(R.id.tv);
            productPic = ( ImageView ) itemView.findViewById(R.id.ivProduct);
            totalPricePerItem = (TextView) itemView.findViewById(R.id.tvTotal);


            int gridWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth/NUM_GRID_COLUMNS;
            productPic.setMaxHeight(imageWidth);
            productPic.setMaxWidth(imageWidth);
        }
    }



}














