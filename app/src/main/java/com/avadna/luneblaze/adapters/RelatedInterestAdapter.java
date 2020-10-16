package com.avadna.luneblaze.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;

/**
 * Created by Sunny on 20-11-2017.
 */

public class RelatedInterestAdapter extends RecyclerView.Adapter<RelatedInterestAdapter.MyViewHolder>{
    //  pivate List<String> hierarchyList;
    int listSize;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_session_name;
        TextView tv_initiator_name;
        TextView tv_sharer_name;
        TextView tv_venue;
        TextView tv_session_note;

        MyViewHolder(View view) {
            super(view);
            tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
            tv_initiator_name = (TextView) view.findViewById(R.id.tv_initiator_name);
            tv_sharer_name = (TextView) view.findViewById(R.id.tv_sharer_name);
            tv_venue = (TextView) view.findViewById(R.id.tv_venue);
          //  tv_session_note = (TextView) view.findViewById(R.id.tv_session_note);

        }
    }


    public RelatedInterestAdapter(int listSize) {
        this.listSize=listSize;
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public RelatedInterestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.related_interest_list_item, parent, false);

        return new RelatedInterestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelatedInterestAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

    }

    @Override
    public int getItemCount() {
        return listSize;
        //  return hierarchyList.size();
    }
}
