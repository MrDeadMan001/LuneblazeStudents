package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.organisation.CampusDriveDetailsActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.assessment.TaggedAssessmentCategoriesAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoJobListItem;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CampusDriveListAdapter extends RecyclerView.Adapter<CampusDriveListAdapter.MyViewHolder> {
    Activity activity;
    Fragment fragment;
    List<PojoCampusDriveListResponseData> driveList;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    boolean showProgress = true;
    String organisationId;
    CampusDriveListCallback campusDriveListCallback;

    public CampusDriveListAdapter(Activity activity, List<PojoCampusDriveListResponseData> driveList,
                                  String organisationId) {
        try {
            this.campusDriveListCallback = ((CampusDriveListCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("activity must implement CampusDriveListCallback.");
        }
        this.driveList = driveList;
        this.activity = activity;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        this.organisationId = organisationId;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public CampusDriveListAdapter(Fragment fragment, List<PojoCampusDriveListResponseData> driveList,
                                  String organisationId) {
        try {
            this.campusDriveListCallback = ((CampusDriveListCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement CampusDriveListCallback.");
        }
        this.driveList = driveList;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        this.organisationId = organisationId;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void showProgressBar(boolean status) {
        showProgress = status;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cv_parent;
        ProgressBar pb_loading_more;
        TextView tv_venue_name;
        TextView tv_date;
        TextView tv_criteria;
        RecyclerView rv_vacancy_list;
        VacancyListAdapter vacancyListAdapter;
        List<PojoJobListItem> vacancyList;
        TextView tv_apply;
        RelativeLayout rv_apply_wrapper;
        RecyclerView rv_category_list;
        TaggedInterestAdapter taggedInterestAdapter;

        MyViewHolder(View view) {
            super(view);
            tv_venue_name = (TextView) view.findViewById(R.id.tv_venue_name);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_apply = (TextView) view.findViewById(R.id.tv_apply);
            rv_apply_wrapper = (RelativeLayout) view.findViewById(R.id.rv_apply_wrapper);
            rv_apply_wrapper.setVisibility(View.GONE);
            tv_criteria = (TextView) view.findViewById(R.id.tv_criteria);
            cv_parent = (CardView) view.findViewById(R.id.cv_parent);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            rv_vacancy_list = (RecyclerView) view.findViewById(R.id.rv_vacancy_list);
            rv_vacancy_list.setLayoutManager
                    (new LinearLayoutManager(activity,
                            RecyclerView.VERTICAL, false));
            rv_vacancy_list.setNestedScrollingEnabled(false);

            rv_category_list = (RecyclerView) view.findViewById(R.id.rv_category_list);
            rv_category_list.setLayoutManager
                    (new LinearLayoutManager(activity,
                            RecyclerView.HORIZONTAL, false));
            rv_category_list.setNestedScrollingEnabled(false);

        }
    }

    @Override
    public CampusDriveListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.campus_drive_list_item, parent, false);

        return new CampusDriveListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CampusDriveListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == driveList.size()) {
            holder.cv_parent.setVisibility(View.GONE);
            if (showProgress) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }
        } else {
            holder.cv_parent.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_criteria.setVisibility(View.GONE);

            final PojoCampusDriveListResponseData currentItem = driveList.get(position);

            if (this.fragment != null) {
                holder.vacancyListAdapter = new VacancyListAdapter(fragment,
                        currentItem.jobs, false);
            } else {
                holder.vacancyListAdapter = new VacancyListAdapter(activity,
                        currentItem.jobs, false);
            }

            holder.rv_vacancy_list.setAdapter(holder.vacancyListAdapter);
            if (currentItem.categories.isEmpty() || currentItem.categories.get(0).text == null) {
                holder.rv_category_list.setVisibility(View.GONE);
            } else {
                holder.rv_category_list.setVisibility(View.VISIBLE);
                holder.rv_category_list.setAdapter(new TaggedAssessmentCategoriesAdapter(fragment, currentItem.categories, 1));
            }
            String criteria = "";
            if (currentItem.noofsessions != null && !currentItem.noofsessions.isEmpty()) {
                criteria = currentItem.noofsessions + " " + activity.getString(R.string.sessions) + " ";
                if (currentItem.sessionroles != null && !currentItem.sessionroles.isEmpty()) {
                    if (currentItem.sessionroles.equalsIgnoreCase("conducted")) {
                        criteria = criteria + activity.getString(R.string.conducted);
                    }
                    if (currentItem.sessionroles.equalsIgnoreCase("attended")) {
                        criteria = criteria + activity.getString(R.string.attended);
                    }
                    if (currentItem.sessionroles.equalsIgnoreCase("both")) {
                        criteria = criteria + activity.getString(R.string.attended_or_conducted);
                    }
                }
                holder.tv_criteria.setVisibility(View.VISIBLE);
                if (!criteria.isEmpty()) {
                    criteria = "<B>" + activity.getString(R.string.eligibility_criteria) + "</B><BR>" + criteria;
                }
                holder.tv_criteria.setText(Html.fromHtml(criteria));
            }

            if (currentItem.eligible == null || currentItem.eligible == 0) {
                holder.rv_apply_wrapper.setVisibility(View.GONE);

            } else if (currentItem.eligible == 1) {
                holder.rv_apply_wrapper.setVisibility(View.VISIBLE);
                //  holder.tv_apply.setBackgroundResource(R.drawable.app_theme_border);
                holder.tv_apply.setText(activity.getString(R.string.apply));
                // holder.tv_apply.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                holder.tv_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        campusDriveListCallback.onDriveItemClick(position, AppKeys.APPLY_DRIVE);
                    }
                });
            } else if (currentItem.eligible == 2) {
                holder.rv_apply_wrapper.setVisibility(View.VISIBLE);
                //   holder.tv_apply.setBackgroundResource(R.drawable.light_blue_button);
                holder.tv_apply.setText(activity.getString(R.string.apply));
                //holder.tv_apply.setTextColor(ContextCompat.getColor(activity, R.color.white));
                holder.tv_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        campusDriveListCallback.onDriveItemClick(position, AppKeys.CANCEL_DRIVE_APPLY);
                    }
                });
            }

            if (currentItem.collegename != null && !currentItem.collegename.isEmpty()) {
                holder.tv_venue_name.setText(currentItem.collegename);
                holder.tv_venue_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent venueIntent = new Intent(activity, VenueActivity.class);
                        venueIntent.putExtra(AppKeys.ID, currentItem.college);
                        activity.startActivity(venueIntent);
                    }
                });
            } else if (currentItem.companyaddress != null && !currentItem.companyaddress.isEmpty()) {
                holder.tv_venue_name.setText(currentItem.companyaddress);
                holder.tv_venue_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse(currentItem.companyaddress);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                            activity.startActivity(mapIntent);
                        }
                    }
                });
            }

            holder.tv_date.setText(commonFunctions.parseDateToName(currentItem.date));
            holder.cv_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent campusDriveIntent = new Intent(activity, CampusDriveDetailsActivity.class);
                    Type type = new TypeToken<PojoCampusDriveListResponseData>() {
                    }.getType();
                    String dataStr = new Gson().toJson(currentItem, type);
                    campusDriveIntent.putExtra(AppKeys.CAMPUS_DRIVE_DATA, dataStr);
                    campusDriveIntent.putExtra(AppKeys.ORGANISATION_ID, organisationId);
                    activity.startActivity(campusDriveIntent);
                }
            });
        }
    }

    public static interface CampusDriveListCallback {
        void onDriveItemClick(int position, String action);
    }

    @Override
    public int getItemCount() {
        return driveList.size() + 1;
    }


}