package com.smartcart.www.smartcart.Animator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.smartcart.www.smartcart.Adapters.MyHistoryAdapter;
import com.smartcart.www.smartcart.R;

/**
 * Created by shyattoun on 21/02/2019.
 */

public class MyHistorySwiper extends ItemTouchHelper.SimpleCallback {
    private Context context;
    private MyHistorySwiper.MySwipeListener listener;
    private Paint p = new Paint();

    public MyHistorySwiper(int dragDirs, int swipeDirs, Context context, MyHistorySwiper.MySwipeListener listener) {
        super(dragDirs, swipeDirs);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        final View foregroundView = ((MyHistoryAdapter.HistoryViewHolder ) viewHolder).itemView;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);

        Bitmap icon;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;
            float height = ( float ) itemView.getBottom() - ( float ) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                p.setColor(Color.parseColor("#388E3C"));
                RectF background = new RectF(( float ) itemView.getLeft(), ( float ) itemView.getTop(), dX, ( float ) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.edit);
                RectF icon_dest = new RectF(( float ) itemView.getLeft() + width, ( float ) itemView.getTop() + width, ( float ) itemView.getLeft() + 2 * width, ( float ) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF(( float ) itemView.getRight() + dX, ( float ) itemView.getTop(), ( float ) itemView.getRight(), ( float ) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.delete);
                RectF icon_dest = new RectF(( float ) itemView.getRight() - 2 * width, ( float ) itemView.getTop() + width, ( float ) itemView.getRight() - width, ( float ) itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        listener.onSwiped(viewHolder, i, viewHolder.getAdapterPosition());
    }

    public interface MySwipeListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}