package com.avadna.luneblaze.adapters.assessment;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.assessment.AssessmentCategoriesActivity;
import com.avadna.luneblaze.activities.assessment.AssessmentStatusActivity;
import com.avadna.luneblaze.activities.organisation.FilterStudentsActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

public class AssessmentDomainCategoryAdapter extends RecyclerView.Adapter<AssessmentDomainCategoryAdapter.MyViewHolder> {
    List<PojoAssessmentCategory> dataSet;
    Activity activity;
    PreferenceUtils preferenceUtils;
    String user_id;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    PojoLoginResponseData pojoLoginResponseData;

    AssessmentDomainCategoryListCallback assessmentDomainCategoryListCallback;
    String listType;


    public AssessmentDomainCategoryAdapter(Activity activity, List<PojoAssessmentCategory> dataSet,String listType) {
        try {
            this.assessmentDomainCategoryListCallback = ((AssessmentDomainCategoryListCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AssessmentDomainCategoryListCallback.");
        }

        this.activity = activity;
        this.dataSet = dataSet;
        this.listType=listType;
        preferenceUtils = new PreferenceUtils(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        //  this.hierarchyList = hierarchyList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_content_wrapper;
        RadioButton rb_above;
        RadioButton rb_any;
        CheckBox cb_category;
        RecyclerView rv_sub_categories;
        TaggedAssessmentCategoriesAdapter taggedAssessmentCategoriesAdapter;

        MyViewHolder(View view) {
            super(view);
            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            rb_above = (RadioButton) view.findViewById(R.id.rb_above);
            rb_any = (RadioButton) view.findViewById(R.id.rb_any);
            cb_category = (CheckBox) view.findViewById(R.id.cb_category);
            rv_sub_categories=(RecyclerView)view.findViewById(R.id.rv_sub_categories);

        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assessment_category_checkbox_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PojoAssessmentCategory currentItem = dataSet.get(position);
        holder.setIsRecyclable(false);

        if(listType.equals(AppKeys.NON_DOMAIN)){
            holder.rb_any.setVisibility(View.INVISIBLE);
            holder.rb_any.setEnabled(false);
            holder.cb_category.setTextSize(16);
            holder.cb_category.setTextColor(ContextCompat.getColor(activity,R.color.med_grey));
        }

        holder.cb_category.setText(currentItem.text);
        holder.cb_category.setChecked(currentItem.isCbChecked);
        if(currentItem.cb_type==null||currentItem.cb_type.isEmpty()){
            holder.rb_above.setChecked(false);
            holder.rb_any.setChecked(false);
        }
        if(currentItem.cb_type!=null&&currentItem.cb_type.equals("any")){
            holder.rb_any.setChecked(true);
        }
        else if(currentItem.cb_type!=null&&currentItem.cb_type.equals("above")){
            holder.rb_above.setChecked(true);
        }
        boolean showList=false;
        for(int i=0;i<currentItem.child.size();i++){
            if(currentItem.child.get(i).isCbChecked){
                showList=true;
                break;
            }
        }

        if(showList){
            holder.rv_sub_categories.setLayoutManager
                    (new LinearLayoutManager(activity,
                            RecyclerView.HORIZONTAL, false));
            List<PojoAssessmentCategory> checked=new ArrayList<>();
            for(int i=0;i<currentItem.child.size();i++){
                if(currentItem.child.get(i).isCbChecked){
                    checked.add(currentItem.child.get(i));
                }
            }
            holder.taggedAssessmentCategoriesAdapter = new TaggedAssessmentCategoriesAdapter(
                    activity,checked, 1);
            holder.rv_sub_categories.setAdapter(holder.taggedAssessmentCategoriesAdapter);
        }

        holder.cb_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assessmentDomainCategoryListCallback.onAssessmentDomainCategoryItemClick(position,currentItem,"all",listType);
            }
        });

        holder.rb_any.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assessmentDomainCategoryListCallback.onAssessmentDomainCategoryItemClick(position,currentItem,"any",listType);
            }
        });

        holder.rb_above.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assessmentDomainCategoryListCallback.onAssessmentDomainCategoryItemClick(position,currentItem,"above",listType);
            }
        });

    }


    public static interface AssessmentDomainCategoryListCallback {
        void onAssessmentDomainCategoryItemClick(int position,PojoAssessmentCategory item, String clickType,String listType);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
        //  return hierarchyList.size();
    }
}
