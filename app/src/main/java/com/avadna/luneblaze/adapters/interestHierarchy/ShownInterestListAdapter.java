package com.avadna.luneblaze.adapters.interestHierarchy;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListItem;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ShownInterestListAdapter extends RecyclerView.Adapter<ShownInterestListAdapter.MyViewHolder> {

    Activity activity;
    List<PojoAllInterestListItem> interestList;
    ShownInterestListAdapterCallback shownInterestListAdapterCallback;
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


    public ShownInterestListAdapter(Activity activity, List<PojoAllInterestListItem> interestList, int type) {
        try {
            this.shownInterestListAdapterCallback = ((ShownInterestListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ShownInterestListAdapterCallback.");
        }
        this.activity = activity;
        this.interestList = interestList;
        this.type = type;
    }


    @Override
    public ShownInterestListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_tag_list_item, parent, false);

        return new ShownInterestListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ShownInterestListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

        if (interestList != null) {
            holder.tv_interest_tag_name.setText(interestList.get(position).text);
            holder.tv_interest_tag_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (type == TYPE_OPEN) {
                        Intent interestIntent = new Intent(activity, InterestActivity.class);
                        interestIntent.putExtra("id", interestList.get(position).interestId);
                        activity.startActivity(interestIntent);
                    } else if (type == TYPE_REMOVE) {
                        shownInterestListAdapterCallback.onShownInterestListAdapterCallback(position, interestList.get(position));
                    }
                }
            });
        }

    }

    public static interface ShownInterestListAdapterCallback {
        void onShownInterestListAdapterCallback(int position, PojoAllInterestListItem pojoAllInterestListItem);
    }


    @Override
    public int getItemCount() {
        return interestList.size();

        //  return hierarchyList.size();
    }
}
