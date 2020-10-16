package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.avadna.luneblaze.pojo.pojoSettings.PojoExtraInfo;

import java.util.List;

/**
 * Created by Sunny on 03-02-2018.
 */

public class EmailListAdapter extends RecyclerView.Adapter<EmailListAdapter.MyViewHolder> {
    int type;
    Activity activity;
    List<PojoExtraInfo> phoneEmailList;
    EmailListAdapterCallback emailListAdapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_email;
        TextView tv_primary_or_other;
        TextView tv_verified_status;
        PopupMenu popupMenu;
        ImageView iv_more_options;


        MyViewHolder(View view) {
            super(view);
            tv_email = (TextView) view.findViewById(R.id.tv_email);
            tv_primary_or_other = (TextView) view.findViewById(R.id.tv_primary_or_other);
            tv_verified_status = (TextView) view.findViewById(R.id.tv_verified_status);

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


    public EmailListAdapter(Activity activity, List<PojoExtraInfo> phoneEmailList, int type) {
        try {
            this.emailListAdapterCallback = ((EmailListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement  EmailListAdapterCallback.");
        }
        this.activity = activity;
        this.phoneEmailList = phoneEmailList;
        this.type = type;


    }

    @Override
    public EmailListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_list_item, parent, false);

        return new EmailListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmailListAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoExtraInfo currentItem = phoneEmailList.get(position);
        //if email list item

        if (currentItem.primary.equals("1")) {
            holder.tv_primary_or_other.setText(R.string.primary);
        } else if (currentItem.primary.equals("0")) {
            holder.tv_primary_or_other.setText(R.string.other);
        }

        holder.tv_email.setText(currentItem.data);
        MenuInflater inflater = holder.popupMenu.getMenuInflater();
        holder.popupMenu.getMenu().clear();

        if (currentItem.status.equals("1")||currentItem.primary.equals("1")) {
            holder.tv_verified_status.setText(activity.getString(R.string.verified));
            holder.tv_verified_status.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            if (currentItem.primary.equals("0")) {
                inflater.inflate(R.menu.email_verified_non_primary, holder.popupMenu.getMenu());
            } else {
                inflater.inflate(R.menu.email_verified_primary, holder.popupMenu.getMenu());
            }
        } else if (currentItem.status.equals("0")) {
            holder.tv_verified_status.setText(activity.getString(R.string.verification_pending));
            holder.tv_verified_status.setTextColor(ContextCompat.getColor(activity, R.color.error_red));
            inflater.inflate(R.menu.email_unverified, holder.popupMenu.getMenu());
        }

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.make_primary:
                        emailListAdapterCallback.emailListItemClickCallback(position, currentItem,
                                AppKeys.MAKE_PRIMARY);
                        break;

                    case R.id.verify:
                        emailListAdapterCallback.emailListItemClickCallback(position, currentItem,
                                AppKeys.VERIFY);
                        break;

                    case R.id.remove:
                        emailListAdapterCallback.emailListItemClickCallback(position, currentItem,
                                AppKeys.REMOVE);

                        break;

                }
                return false;
            }
        });
    }

    public static interface EmailListAdapterCallback {
        void emailListItemClickCallback(int position, PojoExtraInfo currentItem, String type);
    }


    @Override
    public int getItemCount() {
        return phoneEmailList.size();
        //  return hierarchyList.size();
    }
}

