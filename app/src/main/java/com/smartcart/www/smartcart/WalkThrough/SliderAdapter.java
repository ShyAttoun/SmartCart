package com.smartcart.www.smartcart.WalkThrough;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcart.www.smartcart.Animator.SquareImageView;
import com.smartcart.www.smartcart.R;

/**
 * Created by shyattoun on 21/02/2019.
 */

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }


    //Arrays
    public int [] slide_images = {

            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3,
            R.drawable.intro4,
            R.drawable.intro5,
            R.drawable.intro6

    };

    public String [] slide_heading = {

            "בחירת סניף",
            "מסך הבית",
            "הוספת מוצר",
            "מחיקת/עדכון פריט",
            "רשימת קניות",
            "שמירת עגלת קניות"
    };
    public String [] slide_descp = {

            "תוכלו לבחור את הרשת בה אתם מבצעים את הקניות שלכם.",
            "במסך הבית תוכלו להוסיף מוצר לעגלה,ליצור לכם רשימת קניות ואף לשמור את עגלת הקניות שלכם!",
            "הוספת מוצר לעגלה שלכם בצורה קלה ונוחה.",
            "למחיקת פריט משכו את שורת המוצר שמאלה,לעדכון הפריט משכו ימינה.",
            "חוששים לשכוח לשם מה הגעתם לסופר? אל דאגה יצרנו עבורכם את רשימת הקניות!",
            "הגעתם לקופה? בלחיצה אחת קטנה תשמרו את עגלת הקניות שלכם.",


    };
    @Override
    public int getCount() {
        return slide_heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout ) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater ) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        SquareImageView slideImageView = (SquareImageView) view.findViewById(R.id.slide_image);
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_descp);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_heading[position]);
        slideDescription.setText(slide_descp[position]);

        container.addView(view);
        return view;

    }

    //stop at the last page.
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);
    }
}
