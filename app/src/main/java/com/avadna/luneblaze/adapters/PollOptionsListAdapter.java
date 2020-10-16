package com.avadna.luneblaze.adapters;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;

import java.util.List;

public class PollOptionsListAdapter extends RecyclerView.Adapter<PollOptionsListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    int listMaxSize = 6;
    List<PojoNewsFeedPollOption> dataSet;
    PollItemOptionClickCallback pollItemOptionClickCallback;

    Activity activity;

    private Boolean spinnerTouched = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        EditText et_list_item;
        ImageView iv_poll_image;

        ImageView iv_more_options;
        PopupMenu popupMenu;
        MenuInflater inflater;

        MyViewHolder(View view) {
            super(view);
            et_list_item = (EditText) view.findViewById(R.id.et_list_item);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_poll_image = (ImageView) view.findViewById(R.id.iv_poll_image);
            popupMenu = new PopupMenu(activity, iv_more_options);
            inflater = popupMenu.getMenuInflater();
        }
    }


    public PollOptionsListAdapter(Activity activity, List<PojoNewsFeedPollOption> dataSet) {
        try {
            this.pollItemOptionClickCallback = ((PollItemOptionClickCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement pollItemOptionClickCallback.");
        }

        this.dataSet = dataSet;
        this.activity = activity;


        //  this.hierarchyList = hierarchyList;
    }

    public List<PojoNewsFeedPollOption> getList() {
        return dataSet;
    }

    @Override
    public PollOptionsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_option_item, parent, false);

        return new PollOptionsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PollOptionsListAdapter.MyViewHolder holder, final int position) {
        if (dataSet != null) {
            holder.et_list_item.setText(dataSet.get(position).text);
            holder.et_list_item.setEnabled(false);
            holder.popupMenu.getMenu().clear();

            holder.iv_more_options.setVisibility(View.VISIBLE);
            holder.inflater.inflate(R.menu.edit_delete_menu, holder.popupMenu.getMenu());
            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            pollItemOptionClickCallback.pollOptionItemMethodCallback(position, AppKeys.EDIT);
                            break;
                        case R.id.delete:
                            pollItemOptionClickCallback.pollOptionItemMethodCallback(position, AppKeys.DELETE);
                            break;
                    }
                    return false;
                }
            });

            holder.iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.popupMenu.show();
                }
            });


        }
    }

    public static interface PollItemOptionClickCallback {
        void pollOptionItemMethodCallback(int position, String type);
    }


    @Override
    public int getItemCount() {
        if (dataSet != null) {
            return dataSet.size();
        } else {
            return (listMaxSize > listSize) ? listSize : listMaxSize;
        }
        //  return hierarchyList.size();
    }
}
