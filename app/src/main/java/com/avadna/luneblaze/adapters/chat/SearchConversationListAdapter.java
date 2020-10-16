package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.home.AllMessageListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class SearchConversationListAdapter extends RecyclerView.Adapter<SearchConversationListAdapter.MyViewHolder> {

    Activity activity;
    AllMessageListFragment fragment;
    SearchConversationAdapterCallback searchConversationAdapterCallback;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ApiInterface apiService;


    List<PojoConversationListItem> msgList;
    List<PojoUserData> userList;

    HashMap<String, String> seenList;

    int maxheight = 0;

    String type = "";

    boolean selectionEnabled = false;

    public void setType(String type) {
        this.type = type;
    }

    public SearchConversationListAdapter(AllMessageListFragment fragment,
                                         List<PojoConversationListItem> msgList,
                                         List<PojoUserData> userList) {

        try {
            this.searchConversationAdapterCallback = ((SearchConversationAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.msgList = msgList;
        this.userList = userList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        seenList = preferenceUtils.getSeenConversations();
        user_id = preferenceUtils.get_user_id();
        type = Config.NEW_MESSAGE_FCM;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public SearchConversationListAdapter(Activity activity,
                                         List<PojoConversationListItem> msgList,
                                         List<PojoUserData> userList, boolean selectionEnabled) {

        try {
            this.searchConversationAdapterCallback = ((SearchConversationAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.msgList = msgList;
        this.userList = userList;
        this.selectionEnabled = selectionEnabled;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        seenList = preferenceUtils.getSeenConversations();
        user_id = preferenceUtils.get_user_id();
        type = Config.NEW_MESSAGE_FCM;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
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

    public void updateSeenStatus() {
        seenList = preferenceUtils.getSeenConversations();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name, tv_time_stamp, tv_message;
        RelativeLayout rl_parent;
        ImageView iv_user_dp;
        Badge msgCountBadge;

        ProgressBar pb_loading_more;
        TextView tv_no_more_present;
        RelativeLayout rl_content_wrapper;

        ImageView iv_selected;


        MyViewHolder(View view) {
            super(view);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_selected = (ImageView) view.findViewById(R.id.iv_selected);

            if (selectionEnabled) {
                int DP42 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42,
                        activity.getResources().getDisplayMetrics());
                tv_time_stamp.setVisibility(View.GONE);
                iv_user_dp.getLayoutParams().height = DP42;
                iv_user_dp.getLayoutParams().width = DP42;
            }
            pb_loading_more = (ProgressBar) view.findViewById(R.id.pb_loading_more);
            tv_no_more_present = (TextView) view.findViewById(R.id.tv_no_more_present);
            rl_content_wrapper = (RelativeLayout) view.findViewById(R.id.rl_content_wrapper);

            rl_parent = (RelativeLayout) view.findViewById(R.id.rl_parent);
            msgCountBadge = new QBadgeView(activity)
                    .setBadgeNumber(0)
                    .setGravityOffset(0, -2, true)
                    .bindTarget(iv_user_dp);
        }
    }


    @Override
    public SearchConversationListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_conversation_list_item, parent, false);
        return new SearchConversationListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SearchConversationListAdapter.MyViewHolder holder, final int position) {

        if (position < msgList.size()) {
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_present.setVisibility(View.GONE);

            final PojoConversationListItem currentItem = msgList.get(position);



          /*  if (currentItem.userId.equals(user_id) && currentItem.seen.equals("1")) {
                holder.iv_seen_status.setVisibility(View.VISIBLE);
            } else {
                holder.iv_seen_status.setVisibility(View.GONE);
            }*/

            /*if (currentItem.isOnline != null) {
                if (currentItem.isOnline.equals("1")) {
                    holder.iv_online_status.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_online_status.setVisibility(View.GONE);
                }
            }*/

            if (currentItem.checked) {
                holder.iv_selected.setVisibility(View.VISIBLE);
            } else {
                holder.iv_selected.setVisibility(View.GONE);
            }

            if (currentItem.groupData.groupId != null) {
                if (!currentItem.groupData.groupPhoto.isEmpty()) {
                    if (activity != null) Glide.with(activity.getApplicationContext())
                            .load(currentItem.groupData.groupPhoto)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .apply(new RequestOptions().override(96, 96))
                            .into(holder.iv_user_dp);
                } else {
                    holder.iv_user_dp.setImageResource(R.drawable.placeholder);
                    if (activity != null) Glide.with(activity.getApplicationContext())
                            .load(R.drawable.placeholder)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .apply(new RequestOptions().override(96, 96))
                            .into(holder.iv_user_dp);
                    holder.tv_user_name.setText(currentItem.name);
                }
                holder.tv_user_name.setText(currentItem.groupData.groupName);
            } else if (currentItem.picture != null && !currentItem.picture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.picture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .apply(new RequestOptions().override(96, 96))
                        .placeholder(R.drawable.blank_profile_male)
                        .into(holder.iv_user_dp);
                holder.tv_user_name.setText(currentItem.name);

            } else {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(R.drawable.placeholder)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .apply(new RequestOptions().override(96, 96))
                        .placeholder(R.drawable.blank_profile_male)
                        .into(holder.iv_user_dp);
                holder.tv_user_name.setText(currentItem.name);
            }

            holder.tv_time_stamp.setText(commonFunctions.allMsgListTimeParser(currentItem.time, "chat_time"));

            if (seenList.get(currentItem.conversationId) != null) {
                int currentMsgId = Integer.parseInt(currentItem.lastMessageId);
                int seenMsgId = Integer.parseInt(seenList.get(currentItem.conversationId));
                if (currentMsgId <= seenMsgId) {

                    holder.tv_time_stamp.setText(commonFunctions.getChatDividerTime(currentItem.time) + " seen");
                }
            }

            if (selectionEnabled) {
                if (currentItem.groupData.groupId != null) {
                    holder.tv_message.setVisibility(View.VISIBLE);
                    holder.tv_message.setText(currentItem.groupData.noOfMembers + " " + activity.getString(R.string.members));
                } else {
                    holder.tv_message.setVisibility(View.GONE);
                }
            } else {
                List<PojoChatMessage> conv;
                conv = preferenceUtils.getConversation(currentItem.conversationId);

                //if the latest msg is our msg
                if (type.equals(Config.MESSAGE_SENT)) {
                    //if it has image
                    if (!conv.isEmpty()) {
                        if (!conv.get(0).image.isEmpty()) {
                            holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_sent
                                    , 0, 0, 0);
                            holder.tv_message.setText(Html.fromHtml(conv.get(0).message));
                            if (holder.tv_message.getText().toString().trim().isEmpty()) {
                                holder.tv_message.setText(activity.getString(R.string.image));
                            }

                        } else {
                            holder.tv_message.setText(Html.fromHtml(conv.get(0).message));
                            holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        }
                    } else {
                        if (!currentItem.image.isEmpty()) {
                            holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_sent,
                                    0, 0, 0);
                            holder.tv_message.setText(Html.fromHtml(currentItem.message));
                            if (holder.tv_message.getText().toString().trim().isEmpty()) {
                                holder.tv_message.setText(activity.getString(R.string.image));
                            }

                        } else {
                            holder.tv_message.setText(Html.fromHtml(currentItem.message));
                            holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                        }
                    }
                }

                //if it is FCM new MSg
                if (type.equals(Config.NEW_MESSAGE_FCM)) {
                    //if it has image
                    if (!currentItem.image.isEmpty()) {
                        holder.tv_message.setText(activity.getString(R.string.image_attach));

                    } else {
                        holder.tv_message.setText(Html.fromHtml(currentItem.message));
                    }
                }
            }

            holder.rl_content_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchConversationAdapterCallback.onSearchConversationItemClickMethod(position, "conv");
                    /*if (unreadConversations.get(currentItem.conversationId) != null
                            && unreadConversations.get(currentItem.conversationId) > 0) {
                        hitConversationSeenApi(user_id, currentItem.conversationId, currentItem.lastMessageId);
                    }*/
                }
            });
        } else if (position == msgList.size() && !userList.isEmpty()) {
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_present.setVisibility(View.VISIBLE);
            holder.tv_no_more_present.setText(Html.fromHtml("<b>" + activity.getString(R.string.fellows) + "</b>"));
            holder.tv_no_more_present.setGravity(Gravity.LEFT);
            //  holder.tv_no_more_present.setBackgroundColor(ContextCompat.getColor(activity,R.color.white));

            holder.rl_content_wrapper.setVisibility(View.GONE);
        } else if (position > msgList.size()) {
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_present.setVisibility(View.GONE);

            PojoUserData currentItem = userList.get(position - msgList.size() - 1);

            if (currentItem.checked) {
                holder.iv_selected.setVisibility(View.VISIBLE);
            } else {
                holder.iv_selected.setVisibility(View.GONE);
            }

            if (selectionEnabled) {
                holder.tv_message.setVisibility(View.GONE);
            } else {
                holder.tv_message.setVisibility(View.VISIBLE);
                holder.tv_message.setText(currentItem.userWorkTitle);
            }

            holder.rl_content_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchConversationAdapterCallback.onSearchConversationItemClickMethod(position - msgList.size() - 1, "user");
                }
            });

            if (activity != null) Glide.with(activity.getApplicationContext())
                    .load(currentItem.userPicture)
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .apply(new RequestOptions().override(96, 96))
                    .into(holder.iv_user_dp);
            holder.tv_user_name.setText(currentItem.userFullname);
            holder.tv_time_stamp.setVisibility(View.GONE);
        } else if (position == msgList.size() + userList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_present.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                if (msgList.isEmpty()) {
                    holder.tv_no_more_present.setText(activity.getString(R.string.no_results_found));
                } else {
                    holder.tv_no_more_present.setText(activity.getString(R.string.no_more_results_found));
                }
                holder.tv_no_more_present.setVisibility(View.VISIBLE);
                holder.tv_no_more_present.setGravity(Gravity.CENTER);

                holder.tv_no_more_present.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_present.setVisibility(View.GONE);
            }
        }

    }

    private void hitConversationSeenApi(String user_id, String cid, String lastMessageId) {
        Call<PojoNoDataResponse> call = apiService.conversationSeen(user_id, cid, lastMessageId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call,
                                   Response<PojoNoDataResponse> response) {
                int a;
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    public static interface SearchConversationAdapterCallback {
        void onSearchConversationItemClickMethod(int position, String type);
    }

    @Override
    public int getItemCount() {
        int size = msgList.size() + 1 + userList.size();
        return size;
        //  return hierarchyList.size();
    }
}
