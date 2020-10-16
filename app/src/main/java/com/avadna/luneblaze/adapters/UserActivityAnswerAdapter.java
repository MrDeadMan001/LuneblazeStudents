package com.avadna.luneblaze.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

/**
 * Created by Sunny on 27-12-2017.
 */

public class UserActivityAnswerAdapter extends RecyclerView.Adapter<UserActivityAnswerAdapter.MyViewHolder>{
    //  pivate List<String> hierarchyList;
    int listSize;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_activity_performer;
        TextView tv_post_title;
        TextView tv_post_description;
        TextView tv_post_contents;
        TextView tv_like_num;
        TextView tv_comment_num;
        TextView tv_share_num;
        FrameLayout fl_like_button;
        FrameLayout fl_comment_button;
        FrameLayout fl_Share_button;

        ImageView iv_post_image;

        MyViewHolder(View view) {
            super(view);
            /*
            tv_activity_performer= (TextView) view.findViewById(R.id.tv_activity_performer);
            tv_post_title= (TextView) view.findViewById(R.id.tv_post_title);
            tv_post_description= (TextView) view.findViewById(R.id.tv_post_description);
            tv_post_contents= (TextView) view.findViewById(R.id.tv_post_contents);
            tv_like_num= (TextView) view.findViewById(R.id.tv_like_num);
            tv_comment_num= (TextView) view.findViewById(R.id.tv_comment_num);
            tv_share_num= (TextView) view.findViewById(R.id.tv_share_num);
            fl_like_button= (FrameLayout) view.findViewById(R.id.fl_like_button);
            fl_comment_button= (FrameLayout) view.findViewById(R.id.fl_comment_button);
            fl_Share_button= (FrameLayout) view.findViewById(R.id.fl_Share_button);
            iv_post_image = (ImageView) view.findViewById(R.id.iv_post_image);
            */

        }
    }


    public UserActivityAnswerAdapter(int listSize) {
        this.listSize=listSize;
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public UserActivityAnswerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.verified_session_post, parent, false);

        return new UserActivityAnswerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserActivityAnswerAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));

    }

    @Override
    public int getItemCount() {
        return listSize;
        //  return hierarchyList.size();
    }
}
