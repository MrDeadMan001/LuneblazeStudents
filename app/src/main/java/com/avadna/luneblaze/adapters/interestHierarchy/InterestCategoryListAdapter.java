package com.avadna.luneblaze.adapters.interestHierarchy;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class InterestCategoryListAdapter extends RecyclerView.Adapter<InterestCategoryListAdapter.MyViewHolder> {
    List<PojoGetInterestListResponseDataListItem> dataSet;
    private NewInterestCategoryListAdapterCallback newInterestCategoryListAdapterCallback;
    Activity activity;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_interest_name;
        CheckBox cb_selected;
        ImageView iv_expand;
        ImageView iv_interest_img;

        MyViewHolder(View view) {
            super(view);
            tv_interest_name = (TextView) view.findViewById(R.id.tv_interest_name);
            cb_selected = (CheckBox) view.findViewById(R.id.cb_selected);
            iv_expand = (ImageView) view.findViewById(R.id.iv_expand);
            iv_interest_img = (ImageView) view.findViewById(R.id.iv_interest_img);
        }
    }

    public InterestCategoryListAdapter(Activity activity, List<PojoGetInterestListResponseDataListItem> dataSet) {
        try {
            this.newInterestCategoryListAdapterCallback = ((NewInterestCategoryListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement InterestCategoryListAdapter.");
        }

        this.activity = activity;
        this.dataSet = dataSet;

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public InterestCategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_list_item, parent, false);
        /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.square_interest_list_item, parent, false);*/
         /*View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.square_interest_list_item2, parent, false);*/

        return new InterestCategoryListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestCategoryListAdapter.MyViewHolder holder, final int position) {
        PojoGetInterestListResponseDataListItem currentItem = dataSet.get(position);
        holder.tv_interest_name.setText(currentItem.text);

        //check if the interest is present in selected interest list


      /*  if (checkedInterestMap.containsKey(currentItem.interestId)) {
            currentItem.isCbChecked = true;
        } else {
            currentItem.isCbChecked = false;
        }*/


        if (currentItem.isCbChecked == null) {
            currentItem.isCbChecked = false;
        }
        holder.cb_selected.setChecked(currentItem.isCbChecked);

        if (currentItem.child == null || currentItem.child.isEmpty()) {
            holder.iv_expand.setVisibility(View.GONE);
        } else {
            holder.iv_expand.setVisibility(View.VISIBLE);
        }

        Glide.with(activity)
                .load(currentItem.image)
                .apply(new RequestOptions().override(64, 64))
                .placeholder(R.drawable.ic_interest_follow_unfilled)
                .into(holder.iv_interest_img);

        holder.cb_selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.cb_selected.isPressed()) {
                    newInterestCategoryListAdapterCallback
                            .onNewInterestCategoryListAdapterCallback(position, "cb", true, currentItem);
                }
             /*   if (holder.cb_selected.isChecked()) {
                    newInterestCategoryListAdapterCallback
                            .onNewInterestCategoryListAdapterCallback(position, "cb", true, currentItem);
                } else {
                    newInterestCategoryListAdapterCallback
                            .onNewInterestCategoryListAdapterCallback(position, "cb", false, currentItem);
                }*/
            }
        });


        holder.iv_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentItem.child.isEmpty()) {
                    newInterestCategoryListAdapterCallback.onNewInterestCategoryListAdapterCallback(position,
                            "iv", false, currentItem);
                }
            }
        });

        holder.tv_interest_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.iv_expand.callOnClick();
            }
        });
    }

    public static interface NewInterestCategoryListAdapterCallback {
        void onNewInterestCategoryListAdapterCallback(int position, String type, Boolean cbChecked,
                                                      PojoGetInterestListResponseDataListItem pojoAllInterestListItem);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}
