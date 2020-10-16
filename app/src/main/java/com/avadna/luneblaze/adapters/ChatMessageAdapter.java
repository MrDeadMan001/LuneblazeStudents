package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Sunny on 23-01-2018.
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int messageWidthThreshold;
    List<PojoChatMessage> messageList;
    String user_id;


    public ChatMessageAdapter(Activity activity, List<PojoChatMessage> messageList, String user_id) {
        this.activity = activity;
        this.messageList = messageList;
        this.user_id = user_id;
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        messageWidthThreshold = (int) (displayMetrics.widthPixels * (3 / 4f));
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public int getItemViewType(int position) {
        // return position%2;
        if (messageList.get(position).userId.equals(user_id)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {

            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_sent_message, parent, false);
                return new SentMessageViewHolder(itemView);

            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_received_message, parent, false);
                return new ReceivedMessageViewHolder(itemView);


            default:

                return null;

        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        switch (holder.getItemViewType()) {
            case 0:
                SentMessageViewHolder sentMessageViewHolder = (SentMessageViewHolder) holder;
                configSentMessageViewHolder(sentMessageViewHolder, position);
                break;
            case 1:
                ReceivedMessageViewHolder receivedMessageViewHolder = (ReceivedMessageViewHolder) holder;
                configReceivedMessageViewHolder(receivedMessageViewHolder, position);
                break;
          /*  default:
                PostViewHolder postViewHolder1 = (PostViewHolder) holder;
                configPostViewHolder(postViewHolder1, position);
                break;*/
        }
    }

    /*private void configPostViewHolder(PostViewHolder postViewHolder, int position) {
    }
*/

    private void configReceivedMessageViewHolder(final ReceivedMessageViewHolder receivedMessageViewHolder, int position) {
        PojoChatMessage listItem = messageList.get(position);
        receivedMessageViewHolder.tv_received_message.setText(listItem.message);
        if (!listItem.image.isEmpty()) {
            receivedMessageViewHolder.rl_image_wrapper.setVisibility(View.VISIBLE);
            int wrapperWidth = receivedMessageViewHolder.rl_received_message_parent.getMeasuredWidth();
            ViewGroup.LayoutParams layoutParams = receivedMessageViewHolder.iv_received_image.getLayoutParams();
            layoutParams.width = (int) (wrapperWidth * (6 / 7f));
            receivedMessageViewHolder.iv_received_image.setLayoutParams(layoutParams);
            if(activity!=null) Glide.with(activity.getApplicationContext()).load(listItem.image).into(receivedMessageViewHolder.iv_received_image);
        } else {
            receivedMessageViewHolder.rl_image_wrapper.setVisibility(View.GONE);
        }
        receivedMessageViewHolder.tv_received_message_time_stamp.setText(listItem.time);
    }

    private void configSentMessageViewHolder(final SentMessageViewHolder sentMessageViewHolder, int position) {
        PojoChatMessage listItem = messageList.get(position);
        sentMessageViewHolder.tv_sent_message.setText(listItem.message);
        if (!listItem.image.isEmpty()) {
            sentMessageViewHolder.rl_image_wrapper.setVisibility(View.VISIBLE);
            int wrapperWidth = sentMessageViewHolder.rl_sent_message_parent.getMeasuredWidth();
            ViewGroup.LayoutParams layoutParams = sentMessageViewHolder.iv_sent_image.getLayoutParams();
            layoutParams.width = (int) (wrapperWidth * (6 / 7f));
            sentMessageViewHolder.iv_sent_image.setLayoutParams(layoutParams);
            if(activity!=null) Glide.with(activity.getApplicationContext()).load(listItem.image).into(sentMessageViewHolder.iv_sent_image);
        } else {
            sentMessageViewHolder.rl_image_wrapper.setVisibility(View.GONE);
        }
        sentMessageViewHolder.tv_sent_message_time_stamp.setText(listItem.time);


    }

    @Override
    public int getItemCount() {
        return messageList.size();
        //  return hierarchyList.size();
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_sent_message_parent, rl_image_wrapper;
        TextView tv_sent_message, tv_sent_message_time_stamp;
        ImageView iv_sent_image;


        public SentMessageViewHolder(View itemView) {
            super(itemView);
            tv_sent_message = (TextView) itemView.findViewById(R.id.tv_sent_message);
            tv_sent_message_time_stamp = (TextView) itemView.findViewById(R.id.tv_sent_message_time_stamp);
            tv_sent_message.setMaxWidth(messageWidthThreshold);
            rl_sent_message_parent = (RelativeLayout) itemView.findViewById(R.id.rl_sent_message_wrapper);
            rl_image_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_image_wrapper);
            iv_sent_image = (ImageView) itemView.findViewById(R.id.iv_sent_image);

        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rl_received_message_parent, rl_image_wrapper;
        TextView tv_received_message, tv_received_message_time_stamp;
        ImageView iv_received_image;


        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            tv_received_message = (TextView) itemView.findViewById(R.id.tv_received_message);
            tv_received_message_time_stamp = (TextView) itemView.findViewById(R.id.tv_received_message_time_stamp);
            rl_received_message_parent = (RelativeLayout) itemView.findViewById(R.id.rl_received_message_parent);
            tv_received_message.setMaxWidth(messageWidthThreshold);
            rl_image_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_image_wrapper);
            iv_received_image = (ImageView) itemView.findViewById(R.id.iv_received_image);
        }
    }

}
