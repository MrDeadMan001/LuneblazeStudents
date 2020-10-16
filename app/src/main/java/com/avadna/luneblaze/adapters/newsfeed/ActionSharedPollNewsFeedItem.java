package com.avadna.luneblaze.adapters.newsfeed;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.PollPostAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ActionSharedPollNewsFeedItem extends NewsFeedItemBase{

    public ActionSharedPollNewsFeedItem(Fragment fragment, String userFixedname) {
        super(fragment,userFixedname);
    }


    public ActionSharedPollPostViewHolder getViewHolder(ViewGroup parent) {
       View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.action_poll_post_shared_item, parent, false);
        return new ActionSharedPollPostViewHolder(itemView);
    }

    public class ActionSharedPollPostViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_top_bar_wrapper;
        LinearLayout ll_poll_wrapper;
        TextView tv_share_title;
        ImageView iv_post_sharer_user_dp;
        TextView tv_post_sharer_name;
        TextView tv_post_sharer_designation;
        TextView tv_post_sharer_stamp;
        CardView cv_origin_wrapper;

        RecyclerView rv_poll_list;
        PollPostAdapter pollPostAdapter;
        ImageView iv_more_options;
        TextView tv_activity_performed;
        TextView tv_poll_title;
        TextView tv_time_stamp;
        TextView tv_like_number;
        TextView tv_comment_number;
        TextView tv_share_number;
        TextView tv_like_button;
        TextView tv_comment_button;
        TextView tv_share_button;
        ImageView iv_comment_dp;
        TextView tv_comment_user_name, tv_user_comment;
        RelativeLayout rl_user_comment_wrapper;
        List<PojoNewsFeedPollOption> optionList;

        PopupMenu popup;
        MenuInflater inflater;

        public ActionSharedPollPostViewHolder(View itemView) {
            super(itemView);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
            ll_poll_wrapper = (LinearLayout) itemView.findViewById(R.id.ll_poll_wrapper);
            tv_share_title = (TextView) itemView.findViewById(R.id.tv_share_title);
            iv_post_sharer_user_dp = (ImageView) itemView.findViewById(R.id.iv_post_sharer_user_dp);
            tv_post_sharer_name = (TextView) itemView.findViewById(R.id.tv_post_sharer_name);
            tv_post_sharer_designation = (TextView) itemView.findViewById(R.id.tv_post_sharer_designation);
            tv_post_sharer_stamp = (TextView) itemView.findViewById(R.id.tv_post_sharer_stamp);
            cv_origin_wrapper = (CardView) itemView.findViewById(R.id.cv_origin_wrapper);

            tv_activity_performed = (TextView) itemView.findViewById(R.id.tv_activity_performed);
            tv_poll_title = (TextView) itemView.findViewById(R.id.tv_poll_title);
            tv_time_stamp = (TextView) itemView.findViewById(R.id.tv_time_stamp);
            tv_like_number = (TextView) itemView.findViewById(R.id.tv_like_number);
            tv_comment_number = (TextView) itemView.findViewById(R.id.tv_comment_number);
            tv_share_number = (TextView) itemView.findViewById(R.id.tv_share_number);
            tv_like_button = (TextView) itemView.findViewById(R.id.tv_like_button);
            tv_comment_button = (TextView) itemView.findViewById(R.id.tv_comment_button);
            tv_share_button = (TextView) itemView.findViewById(R.id.tv_share_button);


            optionList = new ArrayList<>();
            rv_poll_list = (RecyclerView) itemView.findViewById(R.id.rv_poll_list);
            pollPostAdapter = new PollPostAdapter(fragment, optionList, "shared");
            //using horizontal linearlayout as we want horizontal list
            rv_poll_list.setNestedScrollingEnabled(false);
            rv_poll_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            rv_poll_list.setAdapter(pollPostAdapter);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (isGuestUser) {
                iv_more_options.setVisibility(View.GONE);
            }
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                iv_more_options.setVisibility(View.GONE);
            }
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });
            popup = new PopupMenu(activity, iv_more_options);
            inflater = popup.getMenuInflater();


            rl_user_comment_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_user_comment_wrapper);
            iv_comment_dp = (ImageView) itemView.findViewById(R.id.iv_comment_dp);
            tv_comment_user_name = (TextView) itemView.findViewById(R.id.tv_comment_user_name);
            tv_user_comment = (TextView) itemView.findViewById(R.id.tv_user_comment);
        }
    }

    //this one is used to config the news feed related to action performed on a shared poll
    public void configActionSharedPollViewHolder(ActionSharedPollPostViewHolder actionSharedPollViewHolder,
                                                 final int position, final PojoGetNewsFeedResponseData currentItem) {
        actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        actionSharedPollViewHolder.pollPostAdapter.setVoteCasted(false);
        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            actionSharedPollViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedPollViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedPollViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        actionSharedPollViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedPollViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedPollViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_PROFILE, currentItem.authorId, "");

                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);*/
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedPollViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedPollViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedPollViewHolder.tv_post_sharer_name.callOnClick();
            }
        });


        actionSharedPollViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.time));
        if (currentItem.iLike == 1) {
            actionSharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            actionSharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        actionSharedPollViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_LIKE, "", "");
                }
            }
        });

        actionSharedPollViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                pollIntent.putExtra(AppKeys.ACTION, AppKeys.COMMENT);
                activity.startActivity(pollIntent);*/
            }
        });

        actionSharedPollViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_SHARE, "", "");
            }
        });

        actionSharedPollViewHolder.tv_share_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(pollIntent);*/
            }
        });

        actionSharedPollViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(pollIntent)*/
                ;
            }
        });


        if (currentItem.origin.poll.votes.equals("0")) {
            actionSharedPollViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedPollViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " "
                + activity.getString(R.string.votes));

        if (currentItem.comments.equals("0")) {
            actionSharedPollViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedPollViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));

        actionSharedPollViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.textPlain));
        actionSharedPollViewHolder.optionList.clear();
        actionSharedPollViewHolder.optionList.addAll(currentItem.origin.poll.options);
        actionSharedPollViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);

        if (currentItem.origin != null && currentItem.origin.authorId.equals(user_id)) {
            actionSharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
        }
        for (int i = 0; i < actionSharedPollViewHolder.optionList.size(); i++) {
            if (actionSharedPollViewHolder.optionList.get(i).checked) {
                actionSharedPollViewHolder.pollPostAdapter.setVoteCasted(true);
                actionSharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
                break;
            }
        }


        String userToShow = "";
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }

        if (currentItem.postType.equals("post_comment") || currentItem.postType.equals("post_like")) {
            if (userFixedname == null) {
                userToShow = currentItem.liked_by.userFullname;
            } else {
                userToShow = userFixedname;
            }
        }

        userToShow = "<b>" + userToShow + "</b>";

        String likeOrLikes = activity.getString(R.string.likes_small);
        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
                likeOrLikes = activity.getString(R.string.like_small);

            }
        }
        if (currentItem.postType.equals("post_comment")) {
            actionSharedPollViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.commented_on)));
        } else if (currentItem.postType.equals("post_like")) {
            actionSharedPollViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + likeOrLikes));
        }

        actionSharedPollViewHolder.pollPostAdapter.notifyDataSetChanged();

        if (currentItem.postType.equals("post_comment") && currentItem.commentsArray != null
                && currentItem.commentsArray.size() > 0) {
            actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);

            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.commentsArray.get(0).createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedPollViewHolder.iv_comment_dp);

            actionSharedPollViewHolder.tv_comment_user_name.setText(currentItem
                    .commentsArray.get(0).createdBy.userFullname);
            actionSharedPollViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_PROFILE, currentItem.commentsArray.get(0).userId, "");

                    /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.commentsArray.get(0).userId);
                    activity.startActivity(profileIntent);*/
                }
            });

            actionSharedPollViewHolder.rl_user_comment_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST_COMMENT, currentItem.postId, currentItem.commentsArray.get(0).commentId);

                    /*Intent normalPostIntent = new Intent(activity, SharedPollActivity.class);
                    normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    normalPostIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentsArray.get(0).commentId);
                    activity.startActivity(normalPostIntent);*/
                }
            });
            actionSharedPollViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.commentsArray.get(0).text));
        }

        if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
            //case added because of incorrect data
            if (userFixedname != null) {
                if (!currentItem.liked_by.userFullname.equals(userFixedname)) {
                    actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
                    actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
                } else {
                    actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.VISIBLE);
                }
            }
        }

        actionSharedPollViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            actionSharedPollViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedPollViewHolder.popup.getMenu());
        } else {
            actionSharedPollViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedPollViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName;
                if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;

                } else {
                    firstName = currentItem.postAuthorName;
                }
                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedPollViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedPollViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedPollViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }

        try {
            Field[] fields = actionSharedPollViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedPollViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedPollViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                 /*   case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            actionSharedPollViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            actionSharedPollViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            actionSharedPollViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            actionSharedPollViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SAVE_ARTICLE);
                        break;*/

                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST_TITLE, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POLL_DELETE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_POST, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.origin.postId);

                    /*    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_POST);*/
                        break;


                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });

        if (hideHeader) {
            actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }


    //this one is used to config the shared poll item
    public void configSharedPollViewHolder(ActionSharedPollPostViewHolder actionSharedPollViewHolder,
                                           final int position, final PojoGetNewsFeedResponseData currentItem) {
        actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        actionSharedPollViewHolder.pollPostAdapter.setVoteCasted(false);
        actionSharedPollViewHolder.pollPostAdapter.setNewsFeedItemPosition(position);

        //sharer data
        if (currentItem.postTitle.isEmpty()) {
            actionSharedPollViewHolder.tv_share_title.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_share_title.setVisibility(View.VISIBLE);
            actionSharedPollViewHolder.tv_share_title.setText(Html.fromHtml(currentItem.postTitle));
        }
        actionSharedPollViewHolder.tv_post_sharer_stamp.setText(commonFunctions
                .parseNewsFeedItemTime(currentItem.updatedAt));
        actionSharedPollViewHolder.tv_post_sharer_name.setText(currentItem.userFullname);
        actionSharedPollViewHolder.tv_post_sharer_designation.setText(currentItem.userWorkTitle);
        actionSharedPollViewHolder.tv_post_sharer_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_PROFILE, currentItem.authorId, "");

                /*Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id", currentItem.authorId);
                activity.startActivity(profileIntent);*/
            }
        });

        if (!currentItem.postAuthorPicture.isEmpty()) {
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.postAuthorPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(actionSharedPollViewHolder.iv_post_sharer_user_dp);
        }
        actionSharedPollViewHolder.iv_post_sharer_user_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionSharedPollViewHolder.tv_post_sharer_name.callOnClick();
            }
        });

        String userToShow = "";
        if (userFixedname == null) {
            userToShow = currentItem.userFullname;
        } else {
            userToShow = userFixedname;
        }

        String likeOrLikes = activity.getString(R.string.likes_small);
        if (userFixedname == null) {
            int total = Integer.parseInt(currentItem.total);
            if (total > 0) {
                userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                        " " + activity.getString(R.string.others);
                likeOrLikes = activity.getString(R.string.like_small);

            }
        }
        actionSharedPollViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.origin.time));
        if (currentItem.iLike == 1) {
            actionSharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            actionSharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled, 0, 0, 0);
        } else {
            actionSharedPollViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            actionSharedPollViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled, 0, 0, 0);
        }

        actionSharedPollViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_LIKE, "", "");
            }
        });

        actionSharedPollViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem, AppKeys.POST_SHARE, "", "");
            }
        });


        actionSharedPollViewHolder.cv_origin_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(pollIntent);*/
            }
        });


        actionSharedPollViewHolder.tv_share_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(pollIntent);*/
            }
        });

        actionSharedPollViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                }

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                pollIntent.putExtra(AppKeys.TARGET, AppKeys.COMMENT);
                activity.startActivity(pollIntent);*/
            }
        });

        actionSharedPollViewHolder.tv_comment_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                        AppKeys.OPEN_POST, currentItem.postId, "");

                /*Intent pollIntent = new Intent(activity, SharedPollActivity.class);
                pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                activity.startActivity(pollIntent);*/
            }
        });

        if (currentItem.origin.poll.votes.equals("0")) {
            actionSharedPollViewHolder.tv_like_number.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_like_number.setVisibility(View.VISIBLE);
        }
        actionSharedPollViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " " + activity.getString(R.string.votes));

        if (currentItem.comments.equals("0")) {
            actionSharedPollViewHolder.tv_comment_number.setVisibility(View.GONE);
        } else {
            actionSharedPollViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
        }
        actionSharedPollViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
        actionSharedPollViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.origin.textPlain));
        actionSharedPollViewHolder.optionList.clear();
        actionSharedPollViewHolder.optionList.addAll(currentItem.origin.poll.options);
        actionSharedPollViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);
        if (currentItem.origin != null && currentItem.origin.authorId.equals(user_id)) {
            actionSharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
        }

        for (int i = 0; i < actionSharedPollViewHolder.optionList.size(); i++) {
            if (actionSharedPollViewHolder.optionList.get(i).checked) {
                actionSharedPollViewHolder.pollPostAdapter.setVoteCasted(true);
                actionSharedPollViewHolder.pollPostAdapter.setCanSeeVotes(true);
                break;
            }
        }

        if (currentItem.postType.equals("post_comment")) {
            actionSharedPollViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + activity.getString(R.string.commented_on)));
        } else if (currentItem.postType.equals("post_like")) {
            actionSharedPollViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                    + likeOrLikes));
        } else {
            actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }

        actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        actionSharedPollViewHolder.pollPostAdapter.notifyDataSetChanged();

        actionSharedPollViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            actionSharedPollViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    actionSharedPollViewHolder.popup.getMenu());
        } else {
            actionSharedPollViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    actionSharedPollViewHolder.popup.getMenu());
            if (userFixedname == null) {
                String firstName = currentItem.postAuthorName;
                if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;
                }

                if (currentItem.postAuthorName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                actionSharedPollViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                actionSharedPollViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                actionSharedPollViewHolder.popup.getMenu().removeItem(R.id.hide_post);
            }
        }

        try {
            Field[] fields = actionSharedPollViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(actionSharedPollViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        actionSharedPollViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            actionSharedPollViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            actionSharedPollViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            actionSharedPollViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            actionSharedPollViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SAVE_ARTICLE);
                        break;*/

                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.EDIT_POST_TITLE, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
                        break;

                    case R.id.delete:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POLL_DELETE, "", "");
                        break;

                    case R.id.share_to:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SHARE_TO_POST, "", "");
                        break;

                    case R.id.report:
                        commonFunctions.openReportDialog(user_id, "report_post", currentItem.postId);
                   /*     newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_POST);*/
                        break;

                    case R.id.hide_post:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.HIDE, "", "");
                        break;
                }
                return false;
            }
        });

        if (hideHeader) {
            actionSharedPollViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            actionSharedPollViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }

}
