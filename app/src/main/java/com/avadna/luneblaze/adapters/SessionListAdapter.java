package com.avadna.luneblaze.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

/**
 * Created by Sunny on 19-11-2017.
 */

public class SessionListAdapter extends RecyclerView.Adapter<SessionListAdapter.MyViewHolder>{
  //  pivate List<String> hierarchyList;
    int listSize;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_interest_name;
        TextView tv_no_followers;
        TextView tv_follow_button;
        ImageView iv_interest_img;

        MyViewHolder(View view) {
            super(view);
            tv_interest_name= (TextView) view.findViewById(R.id.tv_interest_name);
            tv_no_followers = (TextView) view.findViewById(R.id.tv_no_followers);
            tv_follow_button = (TextView) view.findViewById(R.id.tv_follow_button);
            iv_interest_img = (ImageView) view.findViewById(R.id.iv_interest_img);


        }
    }


    public SessionListAdapter(int listSize) {
        this.listSize=listSize;
      //  this.hierarchyList = hierarchyList;
    }

    @Override
    public SessionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_session_list_item, parent, false);

        return new SessionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SessionListAdapter.MyViewHolder holder, int position) {
     //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

    }

    @Override
    public int getItemCount() {
        return listSize;
      //  return hierarchyList.size();
    }
}
