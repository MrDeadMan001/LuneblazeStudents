package com.avadna.luneblaze.adapters;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.question.AnswerReplyListActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoAnswerReply;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetAnswerRepliesResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AnswerReplyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String creator_id;
    String USER = "user";
    String OTHER = "other";
    String type = USER;
    List<PojoAnswerReply> replyList;
    ApiInterface apiService;
    TextCrawler textCrawler;
    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;
    AnswerReplyListAdapterCallback answerReplyListAdapterCallback;

    PojoGetAnswerRepliesResponseData answerItem;


    public AnswerReplyListAdapter(Activity activity, List<PojoAnswerReply> replyList) {
        try {
            this.answerReplyListAdapterCallback = ((AnswerReplyListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AnswerReplyListAdapterCallback.");
        }

        this.activity = activity;
        this.replyList = replyList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        textCrawler = new TextCrawler();

    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        // notifyDataSetChanged();
    }

    public void setMoreNextAvailable(boolean available) {
        moreNextAvailable = available;
    }

    public void setMorePreviousAvailable(boolean available) {
        morePreviousAvailable = available;
    }

    public void setShowBottomMoreButton(boolean show) {
        showBottomMoreButton = show;
    }

    public void setShowTopMoreButton(boolean show) {
        showTopMoreButton = show;
    }

    public void setCreator_id(String user_id) {
        this.creator_id = user_id;
    }

    public void setAnswerItem(PojoGetAnswerRepliesResponseData answerItem) {
        this.answerItem = answerItem;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.answer_activity_top_item, parent, false);
                return new AnswerViewHolder(itemView);

            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply_list_item, parent, false);
                return new ReplyViewHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.reply_list_item, parent, false);
                return new ReplyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 1:
                AnswerViewHolder replyViewHolder = (AnswerViewHolder) holder;
                configAnswerViewHolder(replyViewHolder, position);
                break;

            case 2:
                ReplyViewHolder questionViewHolder = (ReplyViewHolder) holder;
                configReplyViewHolder(questionViewHolder, position - 1);
                break;
        }

    }

    private void configReplyViewHolder(ReplyViewHolder holder, int position) {
        if (position == replyList.size()) {
            holder.rl_data_wrapper.setVisibility(View.GONE);
            holder.tv_load_more_button.setText(activity.getString(R.string.load_more));

            if (!moreNextAvailable) {
                holder.tv_load_more_button.setText(activity.getString(R.string.no_more_available));
            }

            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_load_more_button.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
                if (showBottomMoreButton) {
                    holder.tv_load_more_button.setVisibility(View.VISIBLE);
                } else {
                    holder.tv_load_more_button.setVisibility(View.GONE);
                }
            }

            holder.tv_load_more_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (moreNextAvailable) {
                        answerReplyListAdapterCallback.answerReplyListItemClick(position,
                                AppKeys.LOAD_NEXT);
                    }
                }
            });
        } else {
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoAnswerReply currentItem = replyList.get(position);

            LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                @Override
                public void onPre() {
                    // Any work that needs to be done before generating the preview. Usually inflate
                    // your custom preview layout here.
                    //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    holder.rl_web_preview_wrapper.setVisibility(View.GONE);

                }

                @Override
                public void onPos(SourceContent sourceContent, boolean b) {
                    // Populate your preview layout with the results of sourceContent.
                    holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                    holder.tv_link_title.setText(sourceContent.getTitle());
                    holder.tv_website_name.setText(sourceContent.getCannonicalUrl());
                    holder.tv_link_description.setText(sourceContent.getDescription());
                    if (!sourceContent.getImages().isEmpty()) {
                        if (activity != null)
                            Glide.with(activity.getApplicationContext()).load(sourceContent.getImages().get(0)).into(holder.iv_link_image);
                    }
                }
            };

          /*  if (Patterns.WEB_URL.matcher(currentItem.post).matches()) {
                textCrawler.makePreview(linkPreviewCallback, currentItem.post);
                holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                holder.tv_comment.setVisibility(View.GONE);
            } else */
            {
                holder.rl_web_preview_wrapper.setVisibility(View.GONE);
                holder.tv_comment.setVisibility(View.VISIBLE);
                holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
                SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                        currentItem.post, false, null);
                currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
                holder.tv_comment.setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
            }

            if (!currentItem.userData.userPicture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.userData.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
            }

            holder.tv_user_name.setText(currentItem.userData.userFullname);
            holder.tv_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity,
                            ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.userId);
                    activity.startActivity(profileIntent);
                }
            });
            holder.iv_user_dp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.tv_user_name.callOnClick();
                }
            });

            if (currentItem.totalLikes.equals("0")) {
                holder.tv_num_likes.setVisibility(View.GONE);
            } else {
                holder.tv_num_likes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_likes.setText(currentItem.totalLikes + " " + activity.getString(R.string.likes));
            holder.tv_num_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    answerReplyListAdapterCallback.answerReplyListItemClick(position,
                            AppKeys.LIKE_COUNT);
                }
            });

            holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.modifiedOn));

            if (currentItem.iLike != null && currentItem.iLike == 1) {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                        0, 0, 0);
            } else {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                        0, 0, 0);
            }

            if (currentItem.sessionsQaId.isEmpty()) {
                holder.tv_like_button.setOnClickListener(null);
                holder.iv_more_options.setVisibility(View.GONE);
                holder.rl_parent.setAlpha(0.6f);
            } else {
                holder.iv_more_options.setVisibility(View.VISIBLE);
                holder.rl_parent.setAlpha(1f);

                holder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerReplyListAdapterCallback.answerReplyListItemClick(position,
                                AppKeys.LIKE_REPLY);
                    }
                });
            }

            MenuInflater inflater = holder.popupMenu.getMenuInflater();

            if (user_id.equals(currentItem.userId)) {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_poster, holder.popupMenu.getMenu());
            } else if (user_id.equals(creator_id)) {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_creator, holder.popupMenu.getMenu());
            } else {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_other, holder.popupMenu.getMenu());
            }

            try {
                Field[] fields = holder.popupMenu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(holder.popupMenu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.edit:
                            answerReplyListAdapterCallback.answerReplyListItemClick(position,
                                    AppKeys.EDIT);
                            break;

                        case R.id.delete:
                            answerReplyListAdapterCallback.answerReplyListItemClick(position,
                                    AppKeys.DELETE);
                            break;

                        case R.id.share_to:
                            shareTextUrl(replyList.get(position).sessionsQaId);
                            break;

                        case R.id.report:
                            answerReplyListAdapterCallback.answerReplyListItemClick(position,
                                    AppKeys.REPORT_REPLY);
                          /*  commonFunctions.hitReportContentApi(user_id, "report_answer_reply",
                                    currentItem.sessionsQaId, currentItem.post);*/
                            // commonFunctions.openReportDialog(user_id, "report_answer_reply", currentItem.sessionsQaId);
                            break;

                    }
                    return false;
                }
            });


        /*    if (position == (replyList.size() - 1) && bottomGapAllowed) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            }*/
        }
    }

    private void configAnswerViewHolder(AnswerViewHolder holder, int position) {

        PojoGetAnswerRepliesResponseData currentItem = answerItem;

        holder.tv_answer.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
        holder.tv_answer.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                currentItem.post, false, null);
        currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
        holder.tv_answer
                .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);

        holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));
        holder.tv_user_name.setText(currentItem.user.userFullname);

        holder.rl_user_data_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        if (!currentItem.user.userPicture.isEmpty()) {
            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.user.userPicture)
                    .apply(new RequestOptions().override(96, 96))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(holder.iv_user_dp);
        }
        if (currentItem.replies.size() == 0) {
            holder.tv_num_comments.setVisibility(View.GONE);
        } else {
            holder.tv_num_comments.setVisibility(View.VISIBLE);
        }
        holder.tv_num_comments.setText(currentItem.replies.size() + " " + activity.getString(R.string.replies));

        if (currentItem.totalLikes == 0) {
            holder.tv_num_upvotes.setVisibility(View.GONE);
        } else {
            holder.tv_num_upvotes.setVisibility(View.VISIBLE);
        }
        holder.tv_num_upvotes.setText(currentItem.totalLikes + " " + activity.getString(R.string.upvotes));


        if (currentItem.iLike == 1) {
            holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_filled,
                    0, 0, 0);
            holder.tv_upvote_button.setText(R.string.upvoted);
        } else {
            holder.tv_upvote_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            holder.tv_upvote_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_upvote_unfilled,
                    0, 0, 0);
            holder.tv_upvote_button.setText(R.string.upvote);
        }

        holder.tv_upvote_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerReplyListAdapterCallback.answerClickActionMethod(position,
                        AppKeys.LIKE);
            }
        });

        holder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerReplyListAdapterCallback.answerClickActionMethod(position,
                        AppKeys.ANSWER_SHARED);
            }
        });

        holder.tv_num_upvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerReplyListAdapterCallback.answerClickActionMethod(position,
                        AppKeys.UPVOTER);
            }
        });

        holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerReplyListAdapterCallback.answerClickActionMethod(position,
                        AppKeys.REPLY);
            }
        });

    }


    @Override
    public int getItemCount() {

        if (replyList.isEmpty()) {
            if (answerItem == null) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return replyList.size() + 2;
        }
    }

    public class ReplyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_comment;
        TextView tv_like_button;
        TextView tv_num_likes;
        RelativeLayout rl_web_preview_wrapper;
        RelativeLayout rl_parent;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;

        ImageView iv_more_options;
        PopupMenu popupMenu;

        RelativeLayout rl_data_wrapper;
        TextView tv_load_more_button;
        //   TextView tv_load_previous;
        ProgressBar pb_loading_more;

        ReplyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_num_likes = (TextView) itemView.findViewById(R.id.tv_num_likes);
            rl_web_preview_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_web_preview_wrapper);
            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            iv_link_image = (ImageView) itemView.findViewById(R.id.iv_link_image);
            tv_link_title = (TextView) itemView.findViewById(R.id.tv_link_title);
            tv_website_name = (TextView) itemView.findViewById(R.id.tv_website_name);
            tv_link_description = (TextView) itemView.findViewById(R.id.tv_link_description);

            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity, iv_more_options);


            rl_data_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_data_wrapper);
            tv_load_more_button = (TextView) itemView.findViewById(R.id.tv_load_more_button);
            //   tv_load_previous = (TextView) itemView.findViewById(R.id.tv_load_previous);
            pb_loading_more = (ProgressBar) itemView.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);
        }
    }


    public class AnswerViewHolder extends RecyclerView.ViewHolder {
        //   Spinner sp_more_options;
        RelativeLayout rl_user_data_wrapper;
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_answer;
        TextView tv_num_upvotes;
        TextView tv_num_comments;
        TextView tv_upvote_button;
        TextView tv_reply_button;

        TextView tv_share_button;

        AnswerViewHolder(View view) {
            super(view);
            rl_user_data_wrapper = (RelativeLayout) view.findViewById(R.id.rl_user_data_wrapper);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            //    sp_more_options = (Spinner) view.findViewById(R.id.sp_more_options);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_answer = (TextView) view.findViewById(R.id.tv_answer);
            tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
            tv_num_comments = (TextView) view.findViewById(R.id.tv_num_comments);
            tv_upvote_button = (TextView) view.findViewById(R.id.tv_upvote_button);
            tv_reply_button = (TextView) view.findViewById(R.id.tv_reply_button);
            tv_share_button = (TextView) view.findViewById(R.id.tv_share_button);

        }
    }


    private void shareTextUrl(String sessionsQaId) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        String url = AppKeys.WEBSITE_URL + "question/19/#ans" + sessionsQaId;
        share.putExtra(Intent.EXTRA_TEXT, url);
        activity.startActivity(Intent.createChooser(share, "Share link!"));
    }


    public static interface AnswerReplyListAdapterCallback {
        void answerReplyListItemClick(int position, String type);

        void answerClickActionMethod(int position, String type);
    }


}
