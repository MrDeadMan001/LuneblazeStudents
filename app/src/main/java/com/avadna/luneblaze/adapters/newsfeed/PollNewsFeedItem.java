package com.avadna.luneblaze.adapters.newsfeed;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
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

public class PollNewsFeedItem extends NewsFeedItemBase{

    public PollNewsFeedItem(Fragment fragment, String userFixedname) {
       super(fragment,userFixedname);
    }

    public PollPostViewHolder getViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_post_item, parent, false);
        return new PollPostViewHolder(itemView);
    }

    public class PollPostViewHolder extends RecyclerView.ViewHolder {

        CardView cv_post_wrapper;
        RecyclerView rv_poll_list;
        PollPostAdapter pollPostAdapter;
        List<PojoNewsFeedPollOption> optionList;

        RelativeLayout rl_top_bar_wrapper;

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

        PopupMenu popup;
        MenuInflater inflater;

        public PollPostViewHolder(View itemView) {
            super(itemView);
            cv_post_wrapper = (CardView) itemView.findViewById(R.id.cv_post_wrapper);
            rl_top_bar_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_top_bar_wrapper);
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
            pollPostAdapter = new PollPostAdapter(fragment, optionList, "normal");
            //using horizontal linearlayout as we want horizontal list
            rv_poll_list.setNestedScrollingEnabled(false);
            rv_poll_list.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.VERTICAL, false));
            rv_poll_list.setAdapter(pollPostAdapter);

            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                iv_more_options.setVisibility(View.GONE);
            }
            iv_more_options = (ImageView) itemView.findViewById(R.id.iv_more_options);
            if (isGuestUser) {
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

    public void configPollPostViewHolder(PollPostViewHolder pollPostViewHolder,final int position,
                                         final PojoGetNewsFeedResponseData currentItem) {
        pollPostViewHolder.pollPostAdapter.setVoteCasted(false);
        pollPostViewHolder.pollPostAdapter.setNewsFeedItemPosition(position);
        String userToShow = "";

        if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")
                || currentItem.postType.equals("poll_vote")) {
            if (userFixedname == null) {
                userToShow = currentItem.liked_by.userFullname;
            } else {
                userToShow = userFixedname;
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

            if (currentItem.postType.equals("post_like")) {
                pollPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + likeOrLikes));
            } else if (currentItem.postType.equals("post_comment")) {
                pollPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.commented_on)));

            } else if (currentItem.postType.equals("poll_vote")) {
                pollPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.voted_on)));
            }

        } else {
            if (userFixedname == null) {
                userToShow = currentItem.userFullname;
            } else {
                userToShow = userFixedname;
            }

            userToShow = "<b>" + userToShow + "</b>";
            if (userFixedname == null) {
                int total = Integer.parseInt(currentItem.total);
                if (total > 0) {
                    userToShow = userToShow + " " + activity.getString(R.string.and) + " " + currentItem.total +
                            " " + activity.getString(R.string.others);
                }
            }
            if (currentItem.postType.equals("shared")) {
                pollPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.shared)));

            } else {
                pollPostViewHolder.tv_activity_performed.setText(Html.fromHtml(userToShow + " "
                        + activity.getString(R.string.created_poll)));
            }
        }

        pollPostViewHolder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.updatedAt));
        if (currentItem.iLike == 1) {
            pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
            pollPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                    0, 0, 0);
        } else {
            pollPostViewHolder.tv_like_button.setTextColor(ContextCompat.getColor(activity, R.color.med_grey));
            pollPostViewHolder.tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                    0, 0, 0);
        }

        pollPostViewHolder.tv_poll_title.setText(Html.fromHtml(currentItem.textPlain));

        if (currentItem.postType.equals("shared")) {

            if (currentItem.origin.poll.votes.equals("0")) {
                pollPostViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_like_number.setText(currentItem.origin.poll.votes + " " + activity.getString(R.string.votes));

            if (currentItem.origin.comments.equals("0")) {
                pollPostViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_comment_number.setText(currentItem.origin.comments + " " + activity.getString(R.string.comments));
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.origin.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.origin.poll.votes);

            pollPostViewHolder.tv_poll_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.originId, "");

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.originId);
                    activity.startActivity(pollIntent);*/
                }
            });
            pollPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isGuestUser) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.ADD_POST_COMMENT, currentItem.originId, AppKeys.COMMENT);
                    }

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.originId);
                    pollIntent.putExtra(AppKeys.TARGET, AppKeys.COMMENT);
                    activity.startActivity(pollIntent);*/
                }
            });

            pollPostViewHolder.tv_comment_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.originId, "");

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.originId);
                    activity.startActivity(pollIntent);*/
                }
            });
            pollPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.originId, "");

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.origin.postId);
                    activity.startActivity(pollIntent);*/
                }
            });
            if (currentItem.origin != null && currentItem.origin.authorId.equals(user_id)) {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);
            } else {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(false);
            }
        } else {
            if (currentItem.authorId.equals(user_id)) {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);
            } else {
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(false);
            }
            if (currentItem.poll.votes.equals("0")) {
                pollPostViewHolder.tv_like_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_like_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_like_number.setText(currentItem.poll.votes + " " + activity.getString(R.string.votes));

            if (currentItem.comments.equals("0")) {
                pollPostViewHolder.tv_comment_number.setVisibility(View.GONE);
            } else {
                pollPostViewHolder.tv_comment_number.setVisibility(View.VISIBLE);
            }
            pollPostViewHolder.tv_comment_number.setText(currentItem.comments + " " + activity.getString(R.string.comments));
            pollPostViewHolder.optionList.clear();
            pollPostViewHolder.optionList.addAll(currentItem.poll.options);
            pollPostViewHolder.pollPostAdapter.setTotalVotes(currentItem.poll.votes);


            pollPostViewHolder.cv_post_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.postId, "");

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    activity.startActivity(pollIntent);*/
                }
            });

            pollPostViewHolder.tv_comment_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isGuestUser) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.ADD_POST_COMMENT, currentItem.postId, AppKeys.COMMENT);
                    }

                   /* Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    pollIntent.putExtra(AppKeys.TARGET, AppKeys.COMMENT);
                    activity.startActivity(pollIntent);*/
                }
            });

            pollPostViewHolder.tv_comment_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST, currentItem.postId, "");

                    /*Intent pollIntent = new Intent(activity, PollActivity.class);
                    pollIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    //  pollIntent.putExtra(AppKeys.TARGET,AppKeys.COMMENT);
                    activity.startActivity(pollIntent);*/
                }
            });
        }


        for (int i = 0; i < pollPostViewHolder.optionList.size(); i++) {
            if (pollPostViewHolder.optionList.get(i).checked) {
                pollPostViewHolder.pollPostAdapter.setVoteCasted(true);
                pollPostViewHolder.pollPostAdapter.setCanSeeVotes(true);
                break;
            }
        }

        pollPostViewHolder.tv_like_button.setOnClickListener(new View.OnClickListener() {
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

        pollPostViewHolder.tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isGuestUser) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.POST_SHARE, "", "");
                }
            }
        });

        pollPostViewHolder.pollPostAdapter.notifyDataSetChanged();

        if (currentItem.postType.equals("post_comment") && currentItem.commentsArray != null
                && currentItem.commentsArray.size() > 0) {
            pollPostViewHolder.rl_user_comment_wrapper.setVisibility(View.VISIBLE);
            if (activity != null)
                Glide.with(activity.getApplicationContext()).load(currentItem.commentsArray.get(0).createdBy.userPicture)
                        .apply(new RequestOptions().override(96, 96))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(pollPostViewHolder.iv_comment_dp);

            pollPostViewHolder.tv_comment_user_name.setText(currentItem.commentsArray.get(0)
                    .createdBy.userFullname);
            pollPostViewHolder.tv_comment_user_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                    profileIntent.putExtra("target_user_id", currentItem.commentsArray.get(0)
                            .userId);
                    activity.startActivity(profileIntent);
                }
            });

            pollPostViewHolder.rl_user_comment_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                            AppKeys.OPEN_POST_COMMENT, currentItem.postId,
                            currentItem.commentsArray.get(0).commentId);

                    /*Intent normalPostIntent = new Intent(activity, PollActivity.class);
                    normalPostIntent.putExtra(AppKeys.POST_ID, currentItem.postId);
                    normalPostIntent.putExtra(AppKeys.COMMENT_ID, currentItem.commentsArray.get(0).commentId);
                    activity.startActivity(normalPostIntent);*/
                }
            });
            pollPostViewHolder.tv_user_comment.setText(Html.fromHtml(currentItem.commentsArray.get(0).text));
        } else {
            pollPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);
        }

        pollPostViewHolder.popup.getMenu().clear();

        if (user_id.equals(currentItem.authorId)) {
            pollPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_creator_menu,
                    pollPostViewHolder.popup.getMenu());
        } else {
            pollPostViewHolder.inflater.inflate(R.menu.news_feed_normal_post_other_menu,
                    pollPostViewHolder.popup.getMenu());

            if (userFixedname == null) {
                String firstName = "";
                if (currentItem.postType.equals("poll_vote") || currentItem.postType.equals("post_comment")) {
                    firstName = currentItem.liked_by.userFullname;
                } else {
                    firstName = currentItem.postAuthorName;
                }
                if (firstName.contains(" ")) {
                    firstName = firstName.substring(0, firstName.indexOf(" "));
                }
                pollPostViewHolder.popup.getMenu().getItem(1)
                        .setTitle(activity.getString(R.string.unFollow) + " " + firstName);
            } else {
                pollPostViewHolder.popup.getMenu().removeItem(R.id.unfollow);
                pollPostViewHolder.popup.getMenu().removeItem(R.id.hide_post);

            }

        }

        try {
            Field[] fields = pollPostViewHolder.popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(pollPostViewHolder.popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        pollPostViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                   /* case R.id.pin:
                        if (newsFeedList.get(position).pinned) {
                            pollPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.pin));
                        } else {
                            pollPostViewHolder.popup.getMenu().getItem(0)
                                    .setTitle(activity.getString(R.string.unpin));
                        }
                        newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                        currentItem.pinned = !currentItem.pinned;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.PIN_ARTICLE);
                        break;

                    case R.id.save:
                        if (newsFeedList.get(position).iSave) {
                            pollPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.save));
                        } else {
                            pollPostViewHolder.popup.getMenu().getItem(1)
                                    .setTitle(activity.getString(R.string.unsave));
                        }
                        newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                        currentItem.iSave = !currentItem.iSave;
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.SAVE_ARTICLE);
                        break;*/

                    case R.id.edit:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.POLL_EDIT, "", "");
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
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.REPORT_POST, "", "");
                        break;

                    case R.id.unfollow:
                        newsFeedAdapterCallback.newsFeedItemClickCallback(position, currentItem,
                                AppKeys.UNFOLLOW_USER, "", "");
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
            pollPostViewHolder.rl_top_bar_wrapper.setVisibility(View.GONE);
        }
        if (hideFooter) {
            pollPostViewHolder.rl_user_comment_wrapper.setVisibility(View.GONE);

        }
    }
}
