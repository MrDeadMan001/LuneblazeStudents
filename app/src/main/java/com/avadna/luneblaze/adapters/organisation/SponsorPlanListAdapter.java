package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.organisation.SponsorPlanPaymentActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoActiveSessions.PojoActiveSession;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanListData;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;


public class SponsorPlanListAdapter extends RecyclerView.Adapter<SponsorPlanListAdapter.MyViewHolder> {
    List<PojoSponsorPlanListData> dataSet;
    Activity activity;
    PreferenceUtils preferenceUtils;
    String user_id;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    PojoLoginResponseData pojoLoginResponseData;
    String sponsor_id;


    public SponsorPlanListAdapter(Activity activity, List<PojoSponsorPlanListData> dataSet, String sponsor_id) {

        this.activity = activity;
        this.dataSet = dataSet;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        this.sponsor_id = sponsor_id;


        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_plan_name;
        TextView tv_price;
        TextView tv_target_count;
        TextView tv_cost_per_student;
        TextView tv_buy;


        MyViewHolder(View view) {
            super(view);
            tv_plan_name = (TextView) view.findViewById(R.id.tv_plan_name);
            tv_price = (TextView) view.findViewById(R.id.tv_price);
            tv_target_count = (TextView) view.findViewById(R.id.tv_target_count);
            tv_cost_per_student = (TextView) view.findViewById(R.id.tv_cost_per_student);
            tv_buy = (TextView) view.findViewById(R.id.tv_buy);

        }
    }


    @Override
    public SponsorPlanListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sponsor_plan_list_item, parent, false);

        return new SponsorPlanListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SponsorPlanListAdapter.MyViewHolder holder, final int position) {
        final PojoSponsorPlanListData currentItem = dataSet.get(position);
        holder.tv_plan_name.setText(currentItem.name);
        holder.tv_price.setText("₹" + currentItem.price);
        holder.tv_target_count.setText(currentItem.nooftargetuser);
        float price = Float.parseFloat(currentItem.price);
        float target_count = Float.parseFloat(currentItem.nooftargetuser);
        int per = (int) (price / target_count);
        holder.tv_cost_per_student.setText("₹" + String.valueOf(per));

        holder.tv_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String dataStr = gson.toJson(
                        currentItem, new TypeToken<PojoSponsorPlanListData>() {
                        }.getType());
                Intent paymentIntent = new Intent(activity, SponsorPlanPaymentActivity.class);
                paymentIntent.putExtra(AppKeys.ORGANISATION_ID, sponsor_id);
                paymentIntent.putExtra("data", dataStr);
                activity.startActivityForResult(paymentIntent,AppKeys.SPONSOR_PLAN_SET);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}

