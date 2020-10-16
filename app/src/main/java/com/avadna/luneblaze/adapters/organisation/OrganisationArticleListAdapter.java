package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoArticlesWritten;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class OrganisationArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<PojoArticlesWritten> articlesWritten;
    Activity activity;
    boolean showProgress = false;
    int imageFixedWidth;
    int imageFixedHeight;
    DisplayMetrics displayMetrics;
    int maxArticleImageHeight;
    CommonFunctions commonFunctions;
    boolean showNoMoreResults=false;


    public void showProgress(boolean status) {
        showProgress = status;
        notifyDataSetChanged();
    }

    public void showNoMoreResults(boolean status){
        showNoMoreResults=status;
    }

    public OrganisationArticleListAdapter(Activity activity, List<PojoArticlesWritten> articlesWritten) {
        this.activity = activity;
        this.articlesWritten = articlesWritten;
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (7f / 10));
        commonFunctions = new CommonFunctions(activity);

    }

    @Override
    public int getItemViewType(int position) {
        if (position == articlesWritten.size()) {
            return 1;
        } else {
            return 0;
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_article_name;
        TextView tv_article_writer;
        TextView tv_time_stamp;
        ImageView iv_cover_photo;
        RelativeLayout rl_parent;
        RelativeLayout rl_written_by_wrapper;


        ItemViewHolder(View view) {
            super(view);
            tv_article_name = (TextView) view.findViewById(R.id.tv_article_name);
            tv_article_writer = (TextView) view.findViewById(R.id.tv_article_writer);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            rl_written_by_wrapper=(RelativeLayout) view.findViewById(R.id.rl_written_by_wrapper);

        }
    }

    public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb_loading_content;
        TextView tv_no_more_results;

        ProgressBarViewHolder(View itemView) {
            super(itemView);
            pb_loading_content = (ProgressBar) itemView.findViewById(R.id.pb_loading_content);
            tv_no_more_results= (TextView) itemView.findViewById(R.id.tv_no_more_results);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.profile_article_list_item, parent, false);
                return new OrganisationArticleListAdapter.ItemViewHolder(itemView);

            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_bar_list_item, parent, false);
                return new OrganisationArticleListAdapter.ProgressBarViewHolder(itemView);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        switch (holder.getItemViewType()) {
            case 0:
                OrganisationArticleListAdapter.ItemViewHolder itemViewHolder = (OrganisationArticleListAdapter.ItemViewHolder) holder;
                configItemViewHolder(itemViewHolder, position);
                break;
            case 1:
                OrganisationArticleListAdapter.ProgressBarViewHolder progressBarViewHolder = (OrganisationArticleListAdapter.ProgressBarViewHolder) holder;
                configProgressViewHolder(progressBarViewHolder, position);
                break;
        }
    }

    private void configProgressViewHolder(OrganisationArticleListAdapter.ProgressBarViewHolder progressBarViewHolder, int position) {
        if (showProgress) {
            progressBarViewHolder.pb_loading_content.setVisibility(View.VISIBLE);
            progressBarViewHolder.tv_no_more_results.setVisibility(View.GONE);
        } else {
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
        }

        if(showNoMoreResults){
            progressBarViewHolder.tv_no_more_results.setVisibility(View.VISIBLE);
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
        }
        else {
            progressBarViewHolder.tv_no_more_results.setVisibility(View.GONE);
        }
    }

    private void configItemViewHolder(OrganisationArticleListAdapter.ItemViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoArticlesWritten currentItem = articlesWritten.get(position);
        holder.tv_article_name.setText(Html.fromHtml(currentItem.title));
        holder.tv_article_writer.setVisibility(View.GONE);
        holder.tv_time_stamp.setText(commonFunctions.parseDateAndTimeToName(currentItem.addedOn));

        holder.rl_written_by_wrapper.setVisibility(View.GONE);

        if (!currentItem.coverPhoto.isEmpty()) {
            int height = imageFixedHeight/2;
            int width = imageFixedWidth/2;

            if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(currentItem.imgDimensions);
                height = dimen.height;
                width = dimen.width;
                holder.iv_cover_photo.getLayoutParams().height = height/2;
                holder.iv_cover_photo.getLayoutParams().width = width/2;
            }

           /* CardView.LayoutParams layoutParams=(CardView.LayoutParams)articleViewHolder.iv_cover_photo.getLayoutParams();
            layoutParams.height=height;
            layoutParams.width=width;
            articleViewHolder.iv_cover_photo.setLayoutParams(layoutParams);*/

            if(activity!=null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(holder.iv_cover_photo);

        }

        holder.iv_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent articleIntent = new Intent(activity, ArticleActivity.class);
                articleIntent.putExtra("id", currentItem.articlesId);
                activity.startActivity(articleIntent);
            }
        });

     /*   if (position == (articlesWritten.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            holder.rl_parent.setPadding(0, 0, 0, DP64);
        } else {
            holder.rl_parent.setPadding(0, 0, 0, 0);
        }*/

    }

    @Override
    public int getItemCount() {
        return articlesWritten.size() + 1;
        //  return hierarchyList.size();
    }

    public ImageDimensions getScaledDimensions(String imgDimen) {
        ImageDimensions dimen = new ImageDimensions();
        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);
        if (targetHeight > maxArticleImageHeight) {
            targetHeight = maxArticleImageHeight;
        }

        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }
}

