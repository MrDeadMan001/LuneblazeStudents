package com.avadna.luneblaze.adapters.registration;

import android.app.Activity;
import android.content.Context;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.bumptech.glide.Glide;

public class AppTourImageSliderPagerAdapter extends PagerAdapter {

    private Activity _activity;
    int[] caricatures;
    private LayoutInflater inflater;

    // constructor
    public AppTourImageSliderPagerAdapter(Activity activity, int[] caricatures) {
        this._activity = activity;
        this.caricatures = caricatures;
    }

    @Override
    public int getCount() {
        return caricatures.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView;
        TextView tv_title;
        RelativeLayout rl_background_holder;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.simple_image_view, container, false);

        imageView = (ImageView) viewLayout.findViewById(R.id.imageView);
        rl_background_holder = (RelativeLayout) viewLayout.findViewById(R.id.rl_background_holder);
        rl_background_holder.setBackgroundResource(R.drawable.app_theme_round_gradient);

        Glide.with(_activity.getApplicationContext())
                .load(caricatures[position])
                .into(imageView);


        tv_title = (TextView) viewLayout.findViewById(R.id.tv_title);
        if (position == 0) {
            tv_title.setText(_activity.getString(R.string.attend_sessions_of_your_interests));
        } else if (position == 1) {
            tv_title.setText(_activity.getString(R.string.interactive_and_conducive_environment));
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}