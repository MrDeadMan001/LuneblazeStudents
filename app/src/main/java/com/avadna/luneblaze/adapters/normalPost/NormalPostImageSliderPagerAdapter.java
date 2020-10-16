package com.avadna.luneblaze.adapters.normalPost;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPhoto;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class NormalPostImageSliderPagerAdapter extends PagerAdapter {

    private Activity _activity;
    List<PojoNewsFeedPhoto> dataSet;
    private LayoutInflater inflater;

    // constructor
    public NormalPostImageSliderPagerAdapter(Activity activity,
                                          List<PojoNewsFeedPhoto> dataSet) {
        this._activity = activity;
        this.dataSet = dataSet;
    }

    @Override
    public int getCount() {
        return this.dataSet.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView imageView;

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_slider_item, container, false);

        imageView = (PhotoView) viewLayout.findViewById(R.id.imageView);

        Glide.with(_activity.getApplicationContext())
                .load(dataSet.get(position).source)
                .into(imageView);

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}