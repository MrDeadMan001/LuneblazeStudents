package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;

import java.util.List;

/**
 * Created by Sunny on 06-01-2018.
 */

public class TaggedInterestAdapter extends RecyclerView.Adapter<TaggedInterestAdapter.MyViewHolder> {

    List<PojoGetInterestListResponseDataListItem> interestList;
    int listsize = 0;
    Activity activity;
    Fragment fragment;
    List<PojoInterestArr> pojoInterestArrs;
    TaggedInterestAdapterCallback taggedInterestAdapterCallback;
    int TYPE_REMOVE = 1;
    int TYPE_OPEN = 2;
    int type;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_interest_tag_name;

        MyViewHolder(View view) {
            super(view);

            tv_interest_tag_name = (TextView) view.findViewById(R.id.tv_interest_tag_name);

        }
    }

    public TaggedInterestAdapter(int listsize, Activity activity, List<PojoGetInterestListResponseDataListItem> interestList, int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedInterestAdapter.TaggedInterestAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement taggedInterestListAdapterCallback.");
        }
        this.activity = activity;
        this.interestList = interestList;
        this.type=type;
        //  this.hierarchyList = hierarchyList;
    }

    public TaggedInterestAdapter(int listsize, Fragment fragment, List<PojoGetInterestListResponseDataListItem> interestList, int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedInterestAdapter.TaggedInterestAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement taggedInterestListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.interestList = interestList;
        this.type=type;
        //  this.hierarchyList = hierarchyList;
    }



    public TaggedInterestAdapter(Fragment fragment, List<PojoInterestArr> pojoInterestArrs,int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedInterestAdapter.TaggedInterestAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement taggedInterestListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.pojoInterestArrs = pojoInterestArrs;
        this.type=type;
    }

    public TaggedInterestAdapter(Activity activity, List<PojoInterestArr> pojoInterestArrs,int type) {
        try {
            this.taggedInterestAdapterCallback = ((TaggedInterestAdapter.TaggedInterestAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement taggedInterestListAdapterCallback.");
        }
        this.activity = activity;
        this.pojoInterestArrs = pojoInterestArrs;
        this.type=type;
    }



    @Override
    public TaggedInterestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_tag_list_item, parent, false);

        return new TaggedInterestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaggedInterestAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (pojoInterestArrs != null) {
            //workaround used because api returns article name instead or interest name in case
            //of tagged interests of article
            if (pojoInterestArrs.get(position).interestName == null) {
                holder.tv_interest_tag_name.setText(pojoInterestArrs.get(position).articleName);
            } else {
                holder.tv_interest_tag_name.setText(pojoInterestArrs.get(position).interestName);
            }
            holder.tv_interest_tag_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent interestIntent = new Intent(activity, InterestActivity.class);
                    interestIntent.putExtra("id", pojoInterestArrs.get(position).interestId);
                    activity.startActivity(interestIntent);
                }
            });
        }
        else if (interestList != null) {
            holder.tv_interest_tag_name.setText(interestList.get(position).text);
            holder.tv_interest_tag_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type == TYPE_OPEN) {
                        Intent interestIntent = new Intent(activity, InterestActivity.class);
                        interestIntent.putExtra("id", interestList.get(position).interestId);
                        activity.startActivity(interestIntent);
                    }
                    else if (type==TYPE_REMOVE){
                        taggedInterestAdapterCallback.taggedInterestItemClickCallback(position,interestList.get(position));
                    }
                }
            });
        }

    }

    public static interface TaggedInterestAdapterCallback {
        void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem);
    }


    @Override
    public int getItemCount() {
        if (interestList != null) {
            return interestList.size();

        } else if (pojoInterestArrs != null) {
            return pojoInterestArrs.size();
        } else {
            return listsize;
        }
        //  return hierarchyList.size();
    }
}
