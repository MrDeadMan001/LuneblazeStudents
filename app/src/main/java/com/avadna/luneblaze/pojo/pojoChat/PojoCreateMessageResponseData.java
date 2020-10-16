package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 26-03-2018.
 */

public class PojoCreateMessageResponseData {
    @SerializedName("conversation_id")
    @Expose
    public String conversationId;
    @SerializedName("last_message_id")
    @Expose
    public String lastMessageId;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("time")
    @Expose
    public String time;
    @SerializedName("seen")
    @Expose
    public String seen;
    @SerializedName("recipients")
    @Expose
    public List<PojoCreateMessageResponseDataRecipient> recipients = null;
    @SerializedName("name_list")
    @Expose
    public String nameList;
    @SerializedName("ids")
    @Expose
    public String ids;
    @SerializedName("multiple_recipients")
    @Expose
    public Boolean multipleRecipients;
    @SerializedName("picture")
    @Expose
    public String picture;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("total_messages")
    @Expose
    public Integer totalMessages;

    @SerializedName("temp_msg_id")
    @Expose
    public String tempMsgId;

}
