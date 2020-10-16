package com.avadna.luneblaze.adapters.session.organised;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;

import java.util.List;

/**
 * Created by Sunny on 06-01-2018.
 */

public class OrganisedSessionTopicsListAdapter extends RecyclerView.Adapter<OrganisedSessionTopicsListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    List<PojoTopic> dataSet;
    Activity activity;
    DiscussionTopicItemClickCallback discussionTopicItemClickCallback;
    String type;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_name;
        // Spinner sp_more_options;
        ImageView iv_more_options;
        PopupMenu popupMenu;


        MyViewHolder(View view) {
            super(view);
            tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
            //sp_more_options = (Spinner) view.findViewById(R.id.sp_more_options);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity, iv_more_options);

        }
    }

    public OrganisedSessionTopicsListAdapter(Activity activity, List<PojoTopic> dataSet, String type) {
        try {
            this.discussionTopicItemClickCallback = ((DiscussionTopicItemClickCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DiscussionTopicItemClickCallback.");
        }
        this.type = type;
        this.dataSet = dataSet;
        this.activity = activity;
        //  this.hierarchyList = hierarchyList;
    }

    public OrganisedSessionTopicsListAdapter(Fragment fragment, List<PojoTopic> dataSet, String type) {
        try {
            this.discussionTopicItemClickCallback = ((DiscussionTopicItemClickCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DiscussionTopicItemClickCallback.");
        }
        this.type = type;
        this.dataSet = dataSet;
        this.activity = fragment.getActivity();
        //  this.hierarchyList = hierarchyList;
    }


    @Override
    public OrganisedSessionTopicsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_topic_list_item, parent, false);

        return new OrganisedSessionTopicsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OrganisedSessionTopicsListAdapter.MyViewHolder holder, final int position) {
        // this orientation discussion not editable
        holder.tv_item_name.setText(Html.fromHtml(dataSet.get(position).value));

        if (dataSet.get(position).editable == null || dataSet.get(position).editable.isEmpty()
                || dataSet.get(position).editable.equals("1")) {
            holder.iv_more_options.setVisibility(View.VISIBLE);
        } else if (dataSet.get(position).editable.equals("0")) {
            holder.iv_more_options.setVisibility(View.GONE);
        }
        MenuInflater inflater = holder.popupMenu.getMenuInflater();
        holder.popupMenu.getMenu().clear();
        inflater.inflate(R.menu.session_topic_edit_menu, holder.popupMenu.getMenu());
        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:

                        discussionTopicItemClickCallback.discussionTopicItemClickCallback(AppKeys.EDIT,
                                type, position);
                        break;

                    case R.id.delete:

                        discussionTopicItemClickCallback.discussionTopicItemClickCallback(AppKeys.DELETE,
                                type, position);
                        break;
                }
                return false;
            }
        });

    }

    public static interface DiscussionTopicItemClickCallback {
        void discussionTopicItemClickCallback(String action, String type, int position);
    }

    public List<PojoTopic> getTopicList() {
        return dataSet;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();

        //  return hierarchyList.size();
    }
}

