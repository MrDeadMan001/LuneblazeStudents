package com.avadna.luneblaze.adapters.session;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.sessions.SessionCommentRepliesListActivity;
import com.avadna.luneblaze.activities.sessions.SessionDiscussionRepliesListActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoComment;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 06-01-2018.
 */

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.MyViewHolder> {
    int listSize;
    Activity activity;
    Fragment fragment;
    List<PojoComment> discussionList;

    PreferenceUtils preferenceUtils;
    String user_id;
    String creator_id;
    String userName;
    ApiInterface apiService;
    String type;

    boolean showProgressBar = false;
    boolean moreNextAvailable = true;
    boolean morePreviousAvailable = true;
    boolean showBottomMoreButton = true;
    boolean showTopMoreButton = true;

    DiscussionListAdapterCallback discussionListAdapterCallback;
    CommonFunctions commonFunctions;

    public DiscussionAdapter(Fragment fragment, List<PojoComment> discussionList, String type) {
        try {
            this.discussionListAdapterCallback = ((DiscussionListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DiscussionListAdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.discussionList = discussionList;
        this.type = type;
        preferenceUtils = new PreferenceUtils(activity);
        userName = preferenceUtils.get_user_name();
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);

    }

    public DiscussionAdapter(Activity activity, List<PojoComment> discussionList, String type) {
        try {
            this.discussionListAdapterCallback = ((DiscussionListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DiscussionListAdapterCallback.");
        }
        this.activity = activity;
        this.discussionList = discussionList;
        this.type = type;
        preferenceUtils = new PreferenceUtils(this.activity);
        userName = preferenceUtils.get_user_name();
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(activity);

    }

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setCreator_id(String creator) {
        creator_id = creator;
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
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_user_dp;
        TextView tv_user_name;
        TextView tv_time_stamp;
        TextView tv_comment;
        TextView tv_like_button;
        TextView tv_reply_button;
        TextView tv_num_of_likes;
        TextView tv_num_of_replies;

        RelativeLayout rl_web_preview_wrapper;
        ImageView iv_link_image;
        TextView tv_link_title;
        TextView tv_website_name;
        TextView tv_link_description;
        RelativeLayout rl_parent;

        PopupMenu popupMenu;
        ImageView iv_more_options;


        RelativeLayout rl_data_wrapper;
        TextView tv_load_more_button;
        ProgressBar pb_loading_more;


        MyViewHolder(View view) {
            super(view);
            iv_user_dp = (ImageView) itemView.findViewById(R.id.iv_user_dp);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_reply_button = (TextView) itemView.findViewById(R.id.tv_reply_button);
            tv_num_of_replies = (TextView) itemView.findViewById(R.id.tv_num_of_replies);
            tv_num_of_likes = (TextView) itemView.findViewById(R.id.tv_num_of_likes);
            tv_comment = (TextView) itemView.findViewById(R.id.tv_comment);
            rl_web_preview_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_web_preview_wrapper);
            rl_web_preview_wrapper.setVisibility(View.GONE);
            iv_link_image = (ImageView) itemView.findViewById(R.id.iv_link_image);
            tv_link_title = (TextView) itemView.findViewById(R.id.tv_link_title);
            tv_website_name = (TextView) itemView.findViewById(R.id.tv_website_name);
            tv_link_description = (TextView) itemView.findViewById(R.id.tv_link_description);
            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);

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
            pb_loading_more = (ProgressBar) itemView.findViewById(R.id.pb_loading_more);
            pb_loading_more.setVisibility(View.GONE);

        }
    }


    @Override
    public DiscussionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (type.equals("discussion")) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.discussion_list_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_list_item, parent, false);
        }

        return new DiscussionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final DiscussionAdapter.MyViewHolder holder, final int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.rl_parent.setPadding(0, 0, 0, 0);

        if (position == discussionList.size()) {
            holder.rl_data_wrapper.setVisibility(View.GONE);
            if (discussionList.size() == 0) {
                holder.pb_loading_more.setVisibility(View.GONE);
                holder.tv_load_more_button.setVisibility(View.VISIBLE);
                if (type.equals("comment")) {
                    holder.tv_load_more_button.setText(activity.getString(R.string.no_comments_present));
                } else {
                    holder.tv_load_more_button.setText(activity.getString(R.string.no_discusssions_present));
                }
            } else {
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
                            if (!discussionList.isEmpty()) {
                                discussionListAdapterCallback.discussionListItemClickCallback(position,
                                        AppKeys.LOAD_NEXT);
                            }
                        }
                    }
                });
            }
            if (type.equals("discussion")) {
                Resources r = activity.getResources();
                int DP48 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP48);
            }
        } else {
            holder.tv_load_more_button.setVisibility(View.GONE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.rl_data_wrapper.setVisibility(View.VISIBLE);
            final PojoComment currentItem = discussionList.get(position);
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
                    currentItem.sourceContent = sourceContent;
                    setPreviewData(holder, currentItem);
                }
            };

            currentItem.textCrawler = new TextCrawler();
            String text = Html.fromHtml(currentItem.post).toString();

            if (Patterns.WEB_URL.matcher(text).matches()) {
                holder.tv_comment.setText(text);
                if (currentItem.sourceContent == null) {
                    currentItem.textCrawler.makePreview(linkPreviewCallback, text);
                } else {
                    setPreviewData(holder, currentItem);
                }
            } else {
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

            holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));

            if (currentItem.totalLikes == 0) {
                holder.tv_num_of_likes.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_likes.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_likes.setText("" + currentItem.totalLikes + " " + activity.getString(R.string.likes));

            boolean hideButtons = false;
            if (type.equals("comment")) {
                if (currentItem.sessionsCommentId.isEmpty()) {
                    hideButtons = true;
                }
            } else {
                if (currentItem.sessionsDiscussionId.isEmpty()) {
                    hideButtons = true;
                }
            }

            if (hideButtons) {
                holder.tv_reply_button.setOnClickListener(null);
                holder.tv_like_button.setOnClickListener(null);
                holder.rl_data_wrapper.setOnClickListener(null);
                holder.iv_more_options.setVisibility(View.GONE);
                holder.rl_parent.setAlpha(0.6f);
            } else {
                holder.iv_more_options.setVisibility(View.VISIBLE);
                holder.rl_parent.setAlpha(1f);
                holder.tv_reply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent replyListIntent;
                        if (type.equals("comment")) {
                            replyListIntent = new Intent(activity, SessionCommentRepliesListActivity.class);
                            replyListIntent.putExtra(AppKeys.COMMENT_ID, currentItem.sessionsCommentId);
                            replyListIntent.putExtra("session_id", currentItem.sessionsId);
                        } else {
                            replyListIntent = new Intent(activity, SessionDiscussionRepliesListActivity.class);
                            replyListIntent.putExtra(AppKeys.DISCUSSION_ID, currentItem.sessionsDiscussionId);
                            replyListIntent.putExtra("session_id", currentItem.sessionsId);

                        }
                        replyListIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_REPLY);
                        activity.startActivity(replyListIntent);
                    }
                });

                holder.tv_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        discussionListAdapterCallback.discussionListItemClickCallback(position,
                                AppKeys.LIKE);
                    }
                });

                holder.rl_data_wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent replyListIntent;
                        if (type.equals("comment")) {
                            replyListIntent = new Intent(activity, SessionCommentRepliesListActivity.class);
                            replyListIntent.putExtra(AppKeys.COMMENT_ID, currentItem.sessionsCommentId);
                            replyListIntent.putExtra("session_id", currentItem.sessionsId);
                        } else {
                            replyListIntent = new Intent(activity, SessionDiscussionRepliesListActivity.class);
                            replyListIntent.putExtra(AppKeys.DISCUSSION_ID, currentItem.sessionsDiscussionId);
                            replyListIntent.putExtra("session_id", currentItem.sessionsId);
                        }
                        activity.startActivity(replyListIntent);
                    }
                });
            }


            holder.tv_num_of_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    discussionListAdapterCallback.discussionListItemClickCallback(position,AppKeys.LIKE_COUNT);
                }
            });

            if (currentItem.totalReplies == 0) {
                holder.tv_num_of_replies.setVisibility(View.GONE);
            } else {
                holder.tv_num_of_replies.setVisibility(View.VISIBLE);
            }
            holder.tv_num_of_replies.setText("" + currentItem.totalReplies + " " + activity.getString(R.string.replies));

            if (currentItem.iLike) {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                        0, 0, 0);
            } else {
                holder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
                holder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                        0, 0, 0);
            }

            MenuInflater inflater = holder.popupMenu.getMenuInflater();

            if (user_id.equals(currentItem.userData.userId)) {
                holder.popupMenu.getMenu().clear();
                inflater.inflate(R.menu.comment_options_poster, holder.popupMenu.getMenu());
                holder.popupMenu.getMenu().removeItem(2);
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
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:
                            discussionListAdapterCallback.discussionListItemClickCallback(position,
                                    AppKeys.EDIT);
                            break;

                        case R.id.delete:
                            discussionListAdapterCallback.discussionListItemClickCallback(position,
                                    AppKeys.DELETE);

                            break;

                        case R.id.share_to:
                            // shareTextUrl(answerList.get(position).sessionsQaId);
                            break;

                        case R.id.report:
                            discussionListAdapterCallback.discussionListItemClickCallback(position,
                                    AppKeys.REPORT_COMMENT);

                         /*   if (currentItem.sessionsCommentId != null) {
                                openReportDialog(user_id, "report_session_comment", currentItem.sessionsCommentId);
                            } else if (currentItem.sessionsDiscussionId != null) {
                                openReportDialog(user_id, "report_session_discussion", currentItem.sessionsDiscussionId);
                            }*/
                            break;

                    }
                    return false;
                }
            });


            holder.tv_num_of_replies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("comment")) {
                        Intent replyIntent = new Intent(activity, SessionCommentRepliesListActivity.class);
                        replyIntent.putExtra("comment_id", currentItem.sessionsCommentId);
                        replyIntent.putExtra("session_id", currentItem.sessionsId);
                        activity.startActivity(replyIntent);
                    } else if (type.equals("discussion")) {
                        Intent replyIntent = new Intent(activity, SessionDiscussionRepliesListActivity.class);
                        replyIntent.putExtra("discussion_id", currentItem.sessionsDiscussionId);
                        replyIntent.putExtra("session_id", currentItem.sessionsId);
                        activity.startActivity(replyIntent);
                    }
                }
            });
        }
    }

    private void setPreviewData(MyViewHolder holder, final PojoComment currentItem) {
        String title = currentItem.sourceContent.getTitle();
        String name = currentItem.sourceContent.getCannonicalUrl();
        String description = currentItem.sourceContent.getDescription();

        if (title.isEmpty() || name.isEmpty()) {
            holder.rl_web_preview_wrapper.setVisibility(View.GONE);
            holder.tv_comment.setVisibility(View.VISIBLE);
            holder.tv_comment.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            holder.tv_comment.setMovementMethod(LinkMovementMethod.getInstance());
            SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(currentItem.post,
                    currentItem.post, false, null);
            currentItem.clickableSpansList = spannableClickItem.clickableSpansList;
            holder.tv_comment
                    .setText(spannableClickItem.spannableString, TextView.BufferType.SPANNABLE);
        } else {
            holder.rl_web_preview_wrapper.setVisibility(View.VISIBLE);
            holder.tv_link_title.setText(title);
            holder.tv_website_name.setText(name);
            holder.tv_link_description.setText(description);
            holder.rl_web_preview_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    commonFunctions.urlLoader(Html.fromHtml(currentItem.post).toString());
                    /*Intent urlIntent = new Intent(activity, WebViewActivity.class);
                    urlIntent.putExtra("url", Html.fromHtml(currentItem.post).toString());
                    activity.startActivity(urlIntent);*/
                }
            });
            holder.tv_comment.setText(Html.fromHtml(currentItem.post).toString());
            holder.tv_comment.setVisibility(View.VISIBLE);
            if (!currentItem.sourceContent.getImages().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (!activity.isDestroyed()) {
                        for (int i = 0; i < currentItem.sourceContent.getImages().size(); i++) {
                            if (currentItem.sourceContent.getImages().get(i).contains("png")
                                    || currentItem.sourceContent.getImages().get(i).contains("jpg")) {
                                if (activity != null) Glide.with(activity.getApplicationContext())
                                        .load(currentItem.sourceContent.getImages().get(i))
                                        .apply(new RequestOptions().override(200, 140))
                                        .into(holder.iv_link_image);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return discussionList.size() + 1;
    }


    public static interface DiscussionListAdapterCallback {
        void discussionListItemClickCallback(int position, String type);
    }


}
