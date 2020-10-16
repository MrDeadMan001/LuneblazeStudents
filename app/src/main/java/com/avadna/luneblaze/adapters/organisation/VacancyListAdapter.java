package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoJobListItem;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class VacancyListAdapter extends RecyclerView.Adapter<VacancyListAdapter.MyViewHolder> {
    List<PojoJobListItem> dataSet;
    Activity activity;
    Fragment fragment;
    VacancyListAdapterCallback vacancyListAdapterCallback;
    boolean editable;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cv_parent;
        RelativeLayout rl_content_wrapper;
        TextView tv_item_name;
        TextView tv_description;
        // Spinner sp_more_options;
        ImageView iv_more_options;
        PopupMenu popupMenu;
        EditText et_count;

        MyViewHolder(View view) {
            super(view);
            cv_parent = (CardView) view.findViewById(R.id.cv_parent);
            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);
            et_count = (EditText) view.findViewById(R.id.et_count);
            tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
            tv_description = (TextView) view.findViewById(R.id.tv_description);
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

    public VacancyListAdapter(Activity activity, List<PojoJobListItem> dataSet, boolean editable) {
        try {
            this.vacancyListAdapterCallback = ((VacancyListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement VacancyListAdapter.");
        }
        this.dataSet = dataSet;
        this.activity = activity;
        this.editable = editable;
        //  this.hierarchyList = hierarchyList;
    }


    public VacancyListAdapter(Fragment fragment, List<PojoJobListItem> dataSet, boolean editable) {
        try {
            this.vacancyListAdapterCallback = ((VacancyListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement VacancyListAdapter.");
        }
        this.dataSet = dataSet;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.editable = editable;
        //  this.hierarchyList = hierarchyList;
    }


    @Override
    public VacancyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vacancy_list_item, parent, false);

        return new VacancyListAdapter.MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final VacancyListAdapter.MyViewHolder holder, final int position) {
        // this orientation discussion not editable
        PojoJobListItem currentItem = dataSet.get(position);
        if (!editable) {
            holder.iv_more_options.setVisibility(View.GONE);
            // holder.tv_description.setVisibility(View.GONE);
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
                holder.cv_parent.setElevation(0);
            } else{
                // do something for phones running an SDK before lollipop
            }
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 2, activity.getResources().getDisplayMetrics()
            );
            holder.rl_content_wrapper.setPadding(px, px, px, px);
        }

        holder.tv_item_name.setText(Html.fromHtml(currentItem.jobname + " <b>(" + currentItem.noofvacancies + ")</b>"));
        holder.tv_description.setText(currentItem.description);
        holder.et_count.setText(String.valueOf(currentItem.noofvacancies));

        MenuInflater inflater = holder.popupMenu.getMenuInflater();
        holder.popupMenu.getMenu().clear();
        inflater.inflate(R.menu.session_topic_edit_menu, holder.popupMenu.getMenu());
        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        vacancyListAdapterCallback.vacancyListAdapterClick(AppKeys.EDIT,
                                position);
                        break;

                    case R.id.delete:

                        vacancyListAdapterCallback.vacancyListAdapterClick(AppKeys.DELETE,
                                position);
                        break;
                }
                return false;
            }
        });

        holder.et_count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    vacancyListAdapterCallback.vacancyListAdapterClick(AppKeys.COUNT_CHANGED,
                            position);
                }
            }
        });

    /*    holder.tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacancyListAdapterCallback.vacancyListAdapterClick(AppKeys.ADD,
                        position);
            }
        });

        holder.tv_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vacancyListAdapterCallback.vacancyListAdapterClick(AppKeys.SUBTRACT,
                        position);
            }
        });*/

    }

    public static interface VacancyListAdapterCallback {
        void vacancyListAdapterClick(String action, int position);
    }

    public List<PojoJobListItem> getTopicList() {
        return dataSet;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();

        //  return hierarchyList.size();
    }
}