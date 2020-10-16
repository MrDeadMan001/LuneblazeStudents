package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;

import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.home.AllMessageListFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
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

public class AllMessageListAdapter extends RecyclerView.Adapter<AllMessageListAdapter.MyViewHolder> {

    Activity activity;
    AllMessageListFragment fragment;
    private MsgListAdapterCallback msgListAdapterCallback;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ApiInterface apiService;

    HashMap<String, Integer> unreadConversations;

    List<PojoConversationListItem> msgList;

    HashMap<String, String> seenList;

    int maxheight = 0;


    public AllMessageListAdapter(AllMessageListFragment fragment,
                                 List<PojoConversationListItem> msgList) {

        try {
            this.msgListAdapterCallback = ((MsgListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.msgList = msgList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        seenList = preferenceUtils.getSeenConversations();
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public AllMessageListAdapter(Activity activity,
                                 List<PojoConversationListItem> msgList) {

        try {
            this.msgListAdapterCallback = ((MsgListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement AdapterCallback.");
        }
        this.activity = activity;
        this.msgList = msgList;
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        seenList = preferenceUtils.getSeenConversations();
        user_id = preferenceUtils.get_user_id();
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
        ImageView iv_seen_status;
        RelativeLayout rl_parent;
        ImageView iv_online_status;
        ImageView iv_user_dp;
        Badge msgCountBadge;

        ProgressBar pb_loading_more;
        TextView tv_no_more_present;
        RelativeLayout rl_content_wrapper;


        MyViewHolder(View view) {
            super(view);
            tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
            tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);
            tv_message = (TextView) view.findViewById(R.id.tv_message);
            iv_seen_status = (ImageView) view.findViewById(R.id.iv_seen_status);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_online_status = (ImageView) view.findViewById(R.id.iv_online_status);
            iv_online_status.setVisibility(View.GONE);

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
    public AllMessageListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_msg_list_item, parent, false);
        return new AllMessageListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AllMessageListAdapter.MyViewHolder holder, final int position) {

        if (position == msgList.size()) {
            holder.rl_content_wrapper.setVisibility(View.GONE);
            if (showProgressBar) {
                holder.pb_loading_more.setVisibility(View.VISIBLE);
                holder.tv_no_more_present.setVisibility(View.GONE);
            } else {
                holder.pb_loading_more.setVisibility(View.GONE);
            }

            if (showNoMoreResults) {
                holder.tv_no_more_present.setText(activity.getString(R.string.no_more_results_found));
                holder.tv_no_more_present.setVisibility(View.VISIBLE);
                holder.pb_loading_more.setVisibility(View.GONE);
            } else {
                holder.tv_no_more_present.setVisibility(View.GONE);
            }
        } else {
            holder.rl_content_wrapper.setVisibility(View.VISIBLE);
            holder.pb_loading_more.setVisibility(View.GONE);
            holder.tv_no_more_present.setVisibility(View.GONE);
            final PojoConversationListItem currentItem = msgList.get(position);

            if (currentItem.isOnline != null) {
                if (currentItem.isOnline.equals("1")) {
                    holder.iv_online_status.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_online_status.setVisibility(View.GONE);
                }
            }

            if (currentItem.groupData.groupId != null) {
                if (currentItem.message.contains("added") && user_id.equals(currentItem.groupData.groupOwner)) {
                    currentItem.message = currentItem.message.replace("were added to",
                            activity.getString(R.string.created_small));
                }
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
                            .into(holder.iv_user_dp);
                    holder.tv_user_name.setText(currentItem.name);
                }
                holder.tv_user_name.setText(currentItem.groupData.groupName);
            } else if (currentItem.picture != null && !currentItem.picture.isEmpty()) {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(currentItem.picture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
                holder.tv_user_name.setText(currentItem.name);

            } else {
                if (activity != null) Glide.with(activity.getApplicationContext())
                        .load(R.drawable.placeholder)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(holder.iv_user_dp);
                holder.tv_user_name.setText(currentItem.name);
            }


            //if the latest msg is our msg
            List<PojoChatMessage> conv;
            conv = preferenceUtils.getConversation(currentItem.conversationId);

            //if the conv is not present on pref
            //or if the conv is empty
            //or if the latest message in conv is in sent state
            //then use the data from the api

            PojoChatMessage msgToUse = null;

            for (int i = 0; i < conv.size(); i++) {
                if (conv.get(i).messageType == AppKeys.MESSAGE_TYPE_USER) {
                    msgToUse = conv.get(i);
                    break;
                }
            }

            if (conv.isEmpty() || msgToUse == null
                    || (msgToUse.sentState == AppKeys.MESSAGE_STATE_SENT && !msgToUse.isAppLocal)) {

                holder.tv_time_stamp.setText(commonFunctions.allMsgListTimeParser(currentItem.time, "chat_time"));
                //if the message has image
                if (!currentItem.image.isEmpty()) {
                    holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_image_sent
                            , 0, 0, 0);
                    holder.tv_message.setText(Html.fromHtml(currentItem.message));

                    //if the message only has image but no text
                    if (holder.tv_message.getText().toString().trim().isEmpty()) {
                        holder.tv_message.setText(activity.getString(R.string.image));
                    }
                }
                // if the message does not have image then show only text
                else {
                    holder.tv_message.setText(Html.fromHtml(currentItem.message));
                    holder.tv_message.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                //if the latest is my message
                if (currentItem.userId.equals(user_id)) {
                    holder.iv_seen_status.setVisibility(View.VISIBLE);
                    //if the message seen status is 1 show seen icon
                    if (commonFunctions.isMessageSeen(currentItem)) {
                        holder.iv_seen_status.setImageResource(R.drawable.ic_message_seen);
                    }
                    //else show sent icon
                    else {
                        holder.iv_seen_status.setImageResource(R.drawable.ic_message_sent);
                    }
                } else {
                    holder.iv_seen_status.setVisibility(View.GONE);
                }

            }

            //if the conv is present in prefs and the last message in pref list is not in sent state or is not mine
            else {
                //if message has image


                holder.tv_time_stamp.setText(commonFunctions.allMsgListTimeParser(msgToUse.time, "chat_time"));

                if (!msgToUse.image.isEmpty()) {
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

                //show hourglass icon as the message is still unsent
                holder.iv_seen_status.setVisibility(View.VISIBLE);

                if (msgToUse.sentState == AppKeys.MESSAGE_STATE_SENT) {
                    if (commonFunctions.isMessageSeen(currentItem)) {
                        holder.iv_seen_status.setImageResource(R.drawable.ic_message_seen);

                    } else {
                        holder.iv_seen_status.setImageResource(R.drawable.ic_message_sent);
                    }
                } else {
                    holder.iv_seen_status.setImageResource(R.drawable.ic_hourglass_gray);
                }

                if (seenList.get(currentItem.conversationId) != null) {

                    int currentMsgId = 0;
                    int seenMsgId = -1;

                    if (seenList.get(currentItem.conversationId) != null
                            && !seenList.get(currentItem.conversationId).isEmpty()) {
                        seenMsgId = Integer.parseInt(seenList.get(currentItem.conversationId));
                    }

                    if (!msgToUse.messageId.isEmpty()) {
                        currentMsgId = Integer.parseInt(msgToUse.messageId);
                    }

                    if (currentMsgId != 0 && currentMsgId <= seenMsgId) {
                        holder.iv_seen_status.setVisibility(View.VISIBLE);
                        holder.iv_seen_status.setImageResource(R.drawable.ic_message_seen);
                    } else {
                        if (msgToUse.sentState == AppKeys.MESSAGE_STATE_SENT) {
                            holder.iv_seen_status.setImageResource(R.drawable.ic_message_sent);
                        } else {
                            holder.iv_seen_status.setImageResource(R.drawable.ic_hourglass_gray);
                        }
                    }
                }
            }


          /*  if (position == msgList.size() - 1&&!showProgressBar) {
                Resources r = activity.getResources();
                int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, r.getDisplayMetrics());
                int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
                holder.rl_parent.setPadding(0, 0, 0, DP64);
            } else {
                holder.rl_parent.setPadding(0, 0, 0, 0);
            }*/

            unreadConversations = preferenceUtils.getUnreadConversations();
            if (unreadConversations.get(currentItem.conversationId) != null) {
                holder.msgCountBadge.setBadgeNumber(unreadConversations.get(currentItem.conversationId));
               /* if (!currentItem.image.isEmpty()) {
                    holder.tv_message.setText(activity.getString(R.string.image_attach));

                } else {
                    holder.tv_message.setText(Html.fromHtml(currentItem.message));
                }*/
            } else {
                if (currentItem.unreadCount != null) {
                    holder.msgCountBadge.setBadgeNumber(currentItem.unreadCount);
                } else {
                    holder.msgCountBadge.setBadgeNumber(0);
                }
            }

            holder.rl_content_wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    msgListAdapterCallback.onMsgListMethodCallback(position);
                    if (unreadConversations.get(currentItem.conversationId) != null
                            && unreadConversations.get(currentItem.conversationId) > 0) {
                        hitConversationSeenApi(user_id, currentItem.conversationId, currentItem.lastMessageId);
                        notifyDataSetChanged();
                    }
                }
            });
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


    public static interface MsgListAdapterCallback {
        void onMsgListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return msgList.size() + 1;
        //  return hierarchyList.size();
    }
}

