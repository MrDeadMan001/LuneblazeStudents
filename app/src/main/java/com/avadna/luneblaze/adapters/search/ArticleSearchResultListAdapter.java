package com.avadna.luneblaze.adapters.search;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchArticle;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ArticleSearchResultListAdapter extends RecyclerView.Adapter<ArticleSearchResultListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    List<PojoSearchArticle> articleList;
    Activity activity;
    CommonFunctions commonFunctions;

    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;
    int maxArticleImageHeight;

    public ArticleSearchResultListAdapter(Activity activity, List<PojoSearchArticle> articleList) {
        this.activity = activity;
        this.articleList = articleList;
        commonFunctions=new CommonFunctions(activity);

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
    }

    private boolean showProgressBar = false;
    private boolean showNoMoreResults = false;

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setShowNoMoreResults(boolean status) {
        showNoMoreResults = status;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_article_name;
        TextView tv_article_writer;
        TextView tv_time_stamp;
        ImageView iv_cover_photo;
        RelativeLayout rl_parent;

        LinearLayout ll_content_wrapper;
        ProgressBar pb_loading_more;
        TextView tv_no_more_results;

        MyViewHolder(View view) {
            super(view);
            tv_article_name = (TextView) view.findViewById(R.id.tv_article_name);
            tv_article_writer = (TextView) view.findViewById(R.id.tv_article_writer);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);

            ll_content_wrapper = (LinearLayout) view.findViewById(R.id.ll_content_wrapper);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_results = (TextView) view.findViewById(R.id.tv_no_more_results);
        }
    }

    @Override
    public ArticleSearchResultListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_search_result_list_item, parent, false);

        return new ArticleSearchResultListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ArticleSearchResultListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == articleList.size()) {
            holder.ll_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_results.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (articleList.isEmpty()) {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_results_found));
                } else {
                    holder.tv_no_more_results.setText(activity.getString(R.string.no_more_results_found));
                }
                holder.tv_no_more_results.setVisibility(View.VISIBLE);
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_results.setVisibility(View.GONE);
            }
        } else {
            holder.ll_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_results.setVisibility(View.GONE);

            final PojoSearchArticle currentItem = articleList.get(position);
            holder.tv_article_name.setText(Html.fromHtml(currentItem.title));
            holder.tv_article_writer.setText(currentItem.createdBy.userFullname);

            holder.tv_article_writer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent=new Intent(activity,ProfileInfoActivity.class);
                    profileIntent.putExtra(AppKeys.TARGET_USER_ID,currentItem.createdBy.userId);
                    activity.startActivity(profileIntent);
                }
            });

            holder.tv_time_stamp.setText(commonFunctions.parseDateToName(currentItem.addedOn));

            if (!currentItem.coverPhoto.isEmpty()) {
                int height = imageFixedHeight;
                int width = imageFixedWidth;

                if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty()) {
                    ImageDimensions dimen = getScaledDimensions(currentItem.imgDimensions, AppKeys.ARTICLE);
                    height = dimen.height;
                    width = dimen.width;
                    holder.iv_cover_photo.getLayoutParams().height = height;
                    holder.iv_cover_photo.getLayoutParams().width = width;
                }

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

          /*  if (position == (articleList.size() - 1)) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            }*/

        }

    }

    public ImageDimensions getScaledDimensions(String imgDimen, String type) {
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

    @Override
    public int getItemCount() {
        return articleList.size() + 1;
        //  return hierarchyList.size();
    }
}

