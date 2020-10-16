package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.avadna.luneblaze.adapters.article.ArticleCommentsAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponseData;
import com.avadna.luneblaze.pojo.pojoInterest.PojoRelatedArticles;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 27-12-2017.
 */

public class InterestRelatedArticlesAdapter extends RecyclerView.Adapter<InterestRelatedArticlesAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    List<PojoRelatedArticles> articlesList;
    Activity activity;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    RecyclerView rv_comments_list;
    List<PojoArticleCommentsResponseData> commentList;
    boolean isGetCommentApiCalled = false;
    ArticleCommentsAdapter articleCommentsAdapter;
    LinearLayoutManager commentListLayoutManager;
    ProgressBar pb_loading_comments;
    boolean showProgressBar = true;
    boolean moreAvailable = true;

    DisplayMetrics displayMetrics;
    int imageFixedWidth;
    int imageFixedHeight;
    int maxArticleImageHeight;

    InterestRelatedArticlesAdapterCallback interestRelatedArticlesAdapterCallback;

    public InterestRelatedArticlesAdapter(Fragment fragment, List<PojoRelatedArticles> articlesList) {
        try {
            this.interestRelatedArticlesAdapterCallback = ((InterestRelatedArticlesAdapter.InterestRelatedArticlesAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement taggedInterestListAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.articlesList = articlesList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.6f);
        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
    }


    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setMoreAvailable(boolean more) {
        moreAvailable = more;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_parent;
        LinearLayout ll_data_wrapper;
        ImageView iv_writer_dp;
        TextView tv_writer_name;
        TextView tv_writer_designation;
        TextView tv_time_stamp;

        TextView tv_article_name;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like;
        TextView tv_comment;
        TextView tv_share;
        ImageView iv_cover_photo;

        ProgressBar pb_loading_more;
        TextView tv_no_more_present;

        MyViewHolder(View view) {
            super(view);
            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            iv_writer_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            tv_writer_name = (TextView) view.findViewById(R.id.tv_writer_name);
            tv_writer_designation = (TextView) view.findViewById(R.id.tv_writer_designation);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_article_name = (TextView) view.findViewById(R.id.tv_article_name);
            tv_like_number = (TextView) view.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) view.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) view.findViewById(R.id.tv_share_number);
            tv_like = (TextView) view.findViewById(R.id.tv_like);
            tv_comment = (TextView) view.findViewById(R.id.tv_comment);
            tv_share = (TextView) view.findViewById(R.id.tv_share);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_present = (TextView) view.findViewById(R.id.tv_no_more_present);
            pb_loading_more.setVisibility(View.GONE);

        }
    }


    @Override
    public InterestRelatedArticlesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_related_article_list_item, parent, false);

        return new InterestRelatedArticlesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final InterestRelatedArticlesAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        if (position == articlesList.size()) {
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.ll_data_wrapper.setVisibility(View.GONE);
                holder.tv_no_more_present.setVisibility(View.GONE);

            } else if (!moreAvailable) {
                holder.ll_data_wrapper.setVisibility(View.GONE);
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.tv_no_more_present.setVisibility(View.VISIBLE);
                if(articlesList.isEmpty()){
                    holder.tv_no_more_present.setText(activity.getString(R.string.no_articles_present));
                }
                else {
                    holder.tv_no_more_present.setText(activity.getString(R.string.no_more_articles));
                }
            }
        } else {
            holder.tv_no_more_present.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.ll_data_wrapper.setVisibility(View.VISIBLE);
            final PojoRelatedArticles currentItem = articlesList.get(position);

            if (!currentItem.createdBy.userPicture.isEmpty()) {
                if(activity!=null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.createdBy.userPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_writer_dp);
            }

            holder.tv_writer_name.setText(currentItem.createdBy.userFullname);

            String placeAndDesignation = "";
            //if both fields are in data list then show both
            if ((!currentItem.createdBy.userWorkTitle.isEmpty()) && (!currentItem.createdBy.userWorkPlace.isEmpty())) {
                placeAndDesignation = currentItem.createdBy.userWorkTitle + " (" + currentItem.createdBy.userWorkPlace + ")";
            }
            //if work title not available show only work place
            else if (currentItem.createdBy.userWorkTitle.isEmpty()) {
                placeAndDesignation = currentItem.createdBy.userWorkPlace;
            }
            //if work place not available show only work title
            else if (currentItem.createdBy.userWorkPlace.isEmpty()) {
                placeAndDesignation = currentItem.createdBy.userWorkTitle;
            }
            //if none is present then remove visibility of the field
            else if (currentItem.createdBy.userWorkTitle.isEmpty() && currentItem.createdBy.userWorkPlace.isEmpty()) {
                holder.tv_writer_designation.setVisibility(View.GONE);
            }
            holder.tv_writer_designation.setText(placeAndDesignation);
            holder.tv_time_stamp.setText(commonFunctions.parseDateToName(currentItem.addedOn));

            holder.tv_article_name.setText(currentItem.title);

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

            if (currentItem.likes.equals("0") || currentItem.likes.isEmpty()) {
                holder.tv_like_number.setVisibility(View.GONE);
            } else {
                holder.tv_like_number.setVisibility(View.VISIBLE);
            }
            holder.tv_like_number.setText(currentItem.likes + " " + activity.getString(R.string.likes));

            if (currentItem.discussionsCnt.equals("0") || currentItem.discussionsCnt.isEmpty()) {
                holder.tv_comment_number.setVisibility(View.GONE);
            } else {
                holder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            holder.tv_comment_number.setText(currentItem.discussionsCnt + " " + activity.getString(R.string.comments));

            if (currentItem.shares.equals("0") || currentItem.shares.isEmpty()) {
                holder.tv_share_number.setVisibility(View.GONE);
            } else {
                holder.tv_share_number.setVisibility(View.VISIBLE);
            }
            holder.tv_share_number.setText(currentItem.shares + " " + activity.getString(R.string.shares));

            holder.iv_cover_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent articleIntent = new Intent(activity, ArticleActivity.class);
                    articleIntent.putExtra("id", currentItem.articlesId);
                    activity.startActivity(articleIntent);
                }
            });

            if (currentItem.iLike) {
                holder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                        0,0,0);
            } else {
                holder.tv_like.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                        0,0,0);
            }

            holder.tv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedArticlesAdapterCallback.interestRelatedArticlesMethodClick(position, currentItem.articlesId, AppKeys.LIKE);

                }
            });

            holder.tv_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedArticlesAdapterCallback.interestRelatedArticlesMethodClick(position, currentItem.articlesId, AppKeys.ADD_COMMENT);
                    //openCommentDialog(currentItem.articlesId);
                }
            });

            holder.tv_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRelatedArticlesAdapterCallback.interestRelatedArticlesMethodClick(position, currentItem.articlesId, AppKeys.SHARE_ARTICLE);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return articlesList.size() + 1;
        //  return hierarchyList.size();
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


    public static interface InterestRelatedArticlesAdapterCallback {
        void interestRelatedArticlesMethodClick(int position, String articleId, String type);
    }
}

