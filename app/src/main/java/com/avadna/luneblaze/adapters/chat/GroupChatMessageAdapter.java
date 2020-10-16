package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.RenderScript;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sunny on 27-03-2018.
 */

public class GroupChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int messageWidthThreshold;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    String user_id = "";
    PojoLoginResponseData userData;

    String creator_id = "";

    ApiInterface apiService;
    String cid = "";


    //height is in DP
    //int maxImageHeightDP = 400;
    int imageWidthDP = 240;
    int imageWidth;
    int maxImageHeightDP = 350;
    int maxImageHeight;
    int MSG_MAX_WIDTH = 240;

    List<PojoChatMessage> messageList;
    String last_user = "";
    HashMap<String, String> imageMap;


    String searchQuery = "";
    int searchResults = 0;
    // List<Integer> resultMessageIndexList = new ArrayList<>();

    int colorIds[] = {R.color.chat_pink, R.color.chat_brown, R.color.chat_orange,
            R.color.chat_purple, R.color.chat_violet, R.color.app_theme_dark,
            R.color.facebook_button, R.color.twitter_button, R.color.gmail_button, R.color.error_red};

    HashMap<String, Integer> userColorCode = new HashMap<>();

    GroupChatAdapterCallback groupChatAdapterCallback;

    boolean selectionEnabled = false;
    private boolean showProgressBar = false;
    private boolean showNoMoreResults = false;

    public void setShowProgressBar(boolean status) {
        showProgressBar = status;
        notifyDataSetChanged();
    }

    public void setCreatorId(String id) {
        creator_id = id;
    }

    public void setSelectionEnabled(boolean status) {
        selectionEnabled = status;
        // resultMessageIndexList.clear();
        notifyDataSetChanged();
    }

   /* public List<Integer> getResultIndexList() {
        return resultMessageIndexList;
    }*/


    public boolean getSelectionEnabled() {
        return selectionEnabled;
    }

    public GroupChatMessageAdapter(Activity activity, List<PojoChatMessage> messageList, String cid) {

        try {
            this.groupChatAdapterCallback = ((GroupChatAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("activity must implement GroupChatAdapterCallback.");
        }
        this.activity = activity;
        this.messageList = messageList;
        this.cid = cid;
        preferenceUtils = new PreferenceUtils(activity);
        userData = preferenceUtils.getUserLoginData();
        imageMap = preferenceUtils.getChatImagesMap();
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions = new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        messageWidthThreshold = (int) (displayMetrics.widthPixels * (3 / 4f));
        maxImageHeight = (int) activity.getResources().getDimension(R.dimen.chatImageMaxHeight);
        imageWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, imageWidthDP, activity.getResources().getDisplayMetrics());
        //  this.hierarchyList = hierarchyList;
    }

    public void updateSearchQuery(String s) {
        searchQuery = s;
        searchResults = 0;
        notifyDataSetChanged();
    }

    public int getSearchResultCount() {
        return searchResults;
    }

    public static interface GroupChatAdapterCallback {
        public void groupChatClickMethod(int position, PojoChatMessage pojoChatMessage, String type);
    }

    @Override
    public int getItemViewType(int position) {

        if (position == messageList.size()) {
            return 3;
        } else {
            if (messageList.get(position).messageType == AppKeys.MESSAGE_TYPE_USER) {
                if (messageList.get(position).userId.equals(user_id)) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 2;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_sent_message, parent, false);
                return new GroupChatMessageAdapter.SentMessageViewHolder(itemView);

            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_received_message, parent, false);
                return new GroupChatMessageAdapter.ReceivedMessageViewHolder(itemView);

            case 2:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_system_message, parent, false);
                return new GroupChatMessageAdapter.SystemMessageViewHolder(itemView);

            case 3:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.progress_bar_list_item, parent, false);
                return new GroupChatMessageAdapter.ProgressBarViewHolder(itemView);

            default:

                return null;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        switch (holder.getItemViewType()) {
            case 0:
                GroupChatMessageAdapter.SentMessageViewHolder sentMessageViewHolder
                        = (GroupChatMessageAdapter.SentMessageViewHolder) holder;
                configSentMessageViewHolder(sentMessageViewHolder, position);
                break;
            case 1:
                GroupChatMessageAdapter.ReceivedMessageViewHolder receivedMessageViewHolder
                        = (GroupChatMessageAdapter.ReceivedMessageViewHolder) holder;
                configReceivedMessageViewHolder(receivedMessageViewHolder, position);
                break;

            case 2:
                GroupChatMessageAdapter.SystemMessageViewHolder systemMessageViewHolder
                        = (GroupChatMessageAdapter.SystemMessageViewHolder) holder;
                configSystemMessageViewHolder(systemMessageViewHolder, position);
                break;

            case 3:
                GroupChatMessageAdapter.ProgressBarViewHolder progressBarViewHolder
                        = (GroupChatMessageAdapter.ProgressBarViewHolder) holder;
                configProgressBarViewHolder(progressBarViewHolder, position);
                break;
          /*  default:
                PostViewHolder postViewHolder1 = (PostViewHolder) holder;
                configPostViewHolder(postViewHolder1, position);
                break;*/
        }
    }

    private void configProgressBarViewHolder(ProgressBarViewHolder progressBarViewHolder, int position) {
        if (showProgressBar) {
            progressBarViewHolder.pb_loading_content.setVisibility(View.VISIBLE);
        } else {
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
        }

      /*  if (showNoMoreResults) {
            progressBarViewHolder.pb_loading_content.setVisibility(View.GONE);
            progressBarViewHolder.tv_no_more_results.setVisibility(View.VISIBLE);
           // progressBarViewHolder.tv_no_more_results.setText(noMoreResultsMessage);
        } else {
            progressBarViewHolder.tv_no_more_results.setVisibility(View.GONE);
        }*/

    }


    private void configSystemMessageViewHolder(SystemMessageViewHolder systemMessageViewHolder, int position) {
        final PojoChatMessage currentItem = messageList.get(position);

        if (currentItem.messageType == AppKeys.MESSAGE_TYPE_SYSTEM) {

            if (currentItem.message.contains("left")) {
                currentItem.message = currentItem.message.replace(userData.userFullname,
                        activity.getString(R.string.you));
                currentItem.message = currentItem.message.replace("has", "");
            } else {
                if (currentItem.message.contains("added") && user_id.equals(creator_id)) {
                    currentItem.message = currentItem.message.replace("were added to",
                            activity.getString(R.string.created_small));
                }
                String firstHalf = currentItem.message.substring(0, currentItem.message.length() / 2);
                String secondHalf = currentItem.message.substring(currentItem.message.length() / 2);

                if (currentItem.actorId.equals(user_id)) {
                    secondHalf = secondHalf.replace(userData.userFullname, activity.getString(R.string.you));

                }
                if (currentItem.acteeId.equals(user_id)) {
                    firstHalf = firstHalf.replace(userData.userFullname, activity.getString(R.string.you));
                    firstHalf = firstHalf.replace("has", "have");
                    firstHalf = firstHalf.replace("was", "were");

                }
                currentItem.message = firstHalf + secondHalf;
            }

        }

        systemMessageViewHolder.tv_message.setText(Html.fromHtml(currentItem.message));
    }

    private void configReceivedMessageViewHolder(final GroupChatMessageAdapter.ReceivedMessageViewHolder receivedMessageViewHolder, final int position) {
        receivedMessageViewHolder.setIsRecyclable(false);
        final PojoChatMessage currentItem = messageList.get(position);

        // if its from new user then show username

        if (selectionEnabled) {
            receivedMessageViewHolder.fl_highlighter.setVisibility(View.VISIBLE);
        } else {
            receivedMessageViewHolder.fl_highlighter.setVisibility(View.GONE);
        }

        if (currentItem.isHighLighted) {
            receivedMessageViewHolder.fl_highlighter.setBackgroundColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            receivedMessageViewHolder.fl_highlighter.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        }

        receivedMessageViewHolder.rl_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                groupChatAdapterCallback.groupChatClickMethod(position, currentItem, AppKeys.CHAT_MESSAGE_LONG_CLICK);

                return false;
            }
        });

        receivedMessageViewHolder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupChatAdapterCallback.groupChatClickMethod(position, currentItem, AppKeys.CHAT_MESSAGE_CLICK);

            }
        });

        if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty() &&
                !currentItem.imgDimensions.contains("quot")) {
            int imgHeight = 0;
            int imgWidth = 0;
            imgHeight = Integer.parseInt(currentItem.imgDimensions.substring(0, currentItem.imgDimensions.indexOf('X')));
            imgWidth = Integer.parseInt(currentItem.imgDimensions.substring(currentItem.imgDimensions.indexOf('X') + 1));
            double aspectRatio = (double) imgHeight / (double) imgWidth;
            int targetHeight = (int) (imageWidth * aspectRatio);
            if (targetHeight > maxImageHeight) {
                targetHeight = maxImageHeight;
            }
            receivedMessageViewHolder.iv_received_image.getLayoutParams().height = targetHeight;
            receivedMessageViewHolder.iv_received_image.getLayoutParams().width = imageWidth;
            receivedMessageViewHolder.iv_received_image.requestLayout();
            // Log.d("img dimensions","h="+imgHeight+" w="+imgWidth);
        }

        if (currentItem.isFromNewUser) {
            receivedMessageViewHolder.tv_user_name.setVisibility(View.VISIBLE);
            receivedMessageViewHolder.tv_user_name.setText(currentItem.userFullname);
            //if color code is not present then add it
            if (!userColorCode.containsKey(currentItem.userId)) {
                userColorCode.put(currentItem.userId, colorIds[(int) (Math.random() * 9)]);
            }
            receivedMessageViewHolder.tv_user_name.setTextColor(ContextCompat.getColor(activity, userColorCode.get(currentItem.userId)));
        }

        // if new msg is from last user too then no need to show user name
        else {
            receivedMessageViewHolder.tv_user_name.setVisibility(View.GONE);
        }

        if (currentItem.message.isEmpty()) {
            receivedMessageViewHolder.tv_received_message.setVisibility(View.GONE);
        } else {
            receivedMessageViewHolder.tv_received_message.setVisibility(View.VISIBLE);
        }
        //    receivedMessageViewHolder.tv_received_message.setText(Html.fromHtml(currentItem.message));

        currentItem.message = currentItem.message.replace("\n", "<br>");
        SpannableString spannableString = new SpannableString(Html.fromHtml(currentItem.message).toString());

        if (!searchQuery.isEmpty()) {
            String messageLower = currentItem.message.toLowerCase();
            String queryLower = searchQuery.toLowerCase();
            int index = messageLower.indexOf(queryLower);
            while (index >= 0) {
                int endIndex = index + queryLower.length();
                BackgroundColorSpan backgroundColorSpan =
                        new BackgroundColorSpan(activity.getResources().getColor(R.color.online_green));
                spannableString.setSpan(backgroundColorSpan, index, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                searchResults++;
                index = messageLower.indexOf(queryLower, index + 1);
            }
        }

        String text = Html.fromHtml(currentItem.message).toString();

        currentItem.clickableSpans = new ArrayList<>();
        int counter = 0;
        String[] words = text.split("\\s+");
        String emojiRegex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

        //adjustment is needed in case there are two same urls in the message
        int adjust = 0;
        for (final String word : words) {
            Matcher matchEmo = Pattern.compile(emojiRegex).matcher(word);
            boolean emojiPresent = false;
            while (matchEmo.find()) {
                emojiPresent = true;
                break;
            }
            if (!emojiPresent && Patterns.WEB_URL.matcher(word).matches()) {
                int spanStart = text.indexOf(word, adjust);
                int spanEnd = spanStart + word.length();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        commonFunctions.urlLoader(word);
                   /*     Intent webViewIntent = new Intent(activity, WebViewActivity.class);
                        webViewIntent.putExtra("url", word);
                        activity.startActivity(webViewIntent);*/
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                    }
                };
                currentItem.clickableSpans.add(clickableSpan);
                spannableString.setSpan(currentItem.clickableSpans.get(counter), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                counter++;
                receivedMessageViewHolder.tv_received_message.setMovementMethod(LinkMovementMethod.getInstance());
            }
            adjust = adjust + word.length();

        }


        receivedMessageViewHolder.tv_received_message.setText(spannableString);

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, MSG_MAX_WIDTH, activity.getResources().getDisplayMetrics());
        receivedMessageViewHolder.tv_received_message.setMaxWidth((int) px);

        if (!currentItem.image.isEmpty()) {
            receivedMessageViewHolder.tv_received_image_time_stamp.setVisibility(View.VISIBLE);
            receivedMessageViewHolder.tv_received_message_time_stamp.setVisibility(View.GONE);
            receivedMessageViewHolder.cv_image_wrapper.setVisibility(View.VISIBLE);

            String imgName = getImageName(currentItem.image);
            //if image is present in hashmap
            if (imageMap.containsKey(imgName)) {
                File f = new File(imageMap.get(imgName));
                //if image is present on local storage load form local
                if (f.exists()) {
                    Picasso.get()
                            .load(f)
                            .placeholder(R.drawable.placeholder)
                            .transform(new CustomBitMapTransform(activity))
                            .into(receivedMessageViewHolder.iv_received_image);
                }
                //else load from url
                else {
                    Picasso.get()
                            .load(currentItem.image)
                            .placeholder(R.drawable.placeholder)
                            .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                            .into(receivedMessageViewHolder.iv_received_image);
                }
            }
            // if image is not present on local storage load from url
            else {
                Picasso.get()
                        .load(currentItem.image)
                        .placeholder(R.drawable.placeholder)
                        .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                        .into(receivedMessageViewHolder.iv_received_image);
            }

        } else {
            receivedMessageViewHolder.tv_received_image_time_stamp.setVisibility(View.GONE);
            receivedMessageViewHolder.tv_received_message_time_stamp.setVisibility(View.VISIBLE);
            receivedMessageViewHolder.cv_image_wrapper.setVisibility(View.GONE);
        }
        receivedMessageViewHolder.tv_received_message_time_stamp
                .setText(commonFunctions.getChatActivityItemTime(currentItem.time));
        receivedMessageViewHolder.tv_received_image_time_stamp
                .setText(commonFunctions.getChatActivityItemTime(currentItem.time));

        receivedMessageViewHolder.iv_received_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionEnabled) {
                    final Dialog imageViewerDialog;
                    imageViewerDialog = new MyCustomThemeDialog(activity, android.R.style.Theme_Black_NoTitleBar);
                    //imageViewerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
                    ImageView iv_back, iv_edit;
                    PhotoView iv_photo;
                    iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);
                    iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
                    iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);
                    iv_edit.setVisibility(View.GONE);
                    iv_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            imageViewerDialog.dismiss();
                        }
                    });
                    String imgName = getImageName(currentItem.image);
                    if (imageMap.containsKey(imgName)) {
                        File f = new File(imageMap.get(imgName));
                        //if image is present on local storage load form local
                        if (f.exists()) {
                            Picasso.get()
                                    .load(f)
                                    .placeholder(R.drawable.placeholder)
                                    .into(iv_photo);
                        }
                        //else load from url
                        else {
                            Picasso.get()
                                    .load(currentItem.image)
                                    .placeholder(R.drawable.placeholder)
                                    .into(iv_photo);
                        }
                    }
                    // if image is not present on local storage load from url
                    else {
                        Picasso.get()
                                .load(currentItem.image)
                                .placeholder(R.drawable.placeholder)
                                .into(iv_photo);
                    }
                    imageViewerDialog.show();
                }
            }
        });

     /*   if (position == (messageList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            receivedMessageViewHolder.rl_parent.setPadding(0, DP64, 0, 0);
        } else {
            receivedMessageViewHolder.rl_parent.setPadding(0, 0, 0, 0);
        }*/
    }


    private void configSentMessageViewHolder(final GroupChatMessageAdapter.SentMessageViewHolder sentMessageViewHolder,
                                             final int position) {
        sentMessageViewHolder.setIsRecyclable(false);
        final PojoChatMessage currentItem = messageList.get(position);
        sentMessageViewHolder.tv_retry.setVisibility(View.GONE);

        if (selectionEnabled) {
            sentMessageViewHolder.fl_highlighter.setVisibility(View.VISIBLE);
        } else {
            sentMessageViewHolder.fl_highlighter.setVisibility(View.GONE);
        }

        if (currentItem.isHighLighted) {
            sentMessageViewHolder.fl_highlighter.setBackgroundColor(ContextCompat.getColor(activity, R.color.app_theme_medium));
        } else {
            sentMessageViewHolder.fl_highlighter.setBackgroundColor(ContextCompat.getColor(activity, R.color.transparent));
        }

        sentMessageViewHolder.rl_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                groupChatAdapterCallback.groupChatClickMethod(position, currentItem, AppKeys.CHAT_MESSAGE_LONG_CLICK);
                return false;
            }
        });

        sentMessageViewHolder.rl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groupChatAdapterCallback.groupChatClickMethod(position, currentItem, AppKeys.CHAT_MESSAGE_CLICK);
            }
        });

        if (currentItem.imgDimensions != null && !currentItem.imgDimensions.isEmpty() &&
                !currentItem.imgDimensions.contains("quot")) {
            int imgHeight = 0;
            int imgWidth = 0;
            imgHeight = Integer.parseInt(currentItem.imgDimensions.substring(0, currentItem.imgDimensions.indexOf('X')));
            imgWidth = Integer.parseInt(currentItem.imgDimensions.substring(currentItem.imgDimensions.indexOf('X') + 1));
            double aspectRatio = (double) imgHeight / (double) imgWidth;
            int targetHeight = (int) (imageWidth * aspectRatio);

            if (targetHeight > maxImageHeight) {
                targetHeight = maxImageHeight;
            }

            sentMessageViewHolder.iv_sent_image.getLayoutParams().height = targetHeight;
            sentMessageViewHolder.iv_sent_image.getLayoutParams().width = imageWidth;
            sentMessageViewHolder.iv_sent_image.requestLayout();

            // Log.d("img dimensions","h="+imgHeight+" w="+imgWidth);
        }


        last_user = currentItem.userId;
        if (currentItem.message.isEmpty()) {
            sentMessageViewHolder.tv_sent_message.setVisibility(View.GONE);
        } else {
            sentMessageViewHolder.tv_sent_message.setVisibility(View.VISIBLE);
        }
        // sentMessageViewHolder.tv_sent_message.setText(Html.fromHtml(currentItem.message));
        currentItem.message = currentItem.message.replace("\n", "<br>");
        SpannableString spannableString = new SpannableString(Html.fromHtml(currentItem.message).toString());

        if (!searchQuery.isEmpty()) {
            String messageLower = currentItem.message.toLowerCase();
            String queryLower = searchQuery.toLowerCase();
            int index = messageLower.indexOf(queryLower);
            while (index >= 0) {
                int endIndex = index + queryLower.length();
                BackgroundColorSpan backgroundColorSpan =
                        new BackgroundColorSpan(activity.getResources().getColor(R.color.online_green));
                spannableString.setSpan(backgroundColorSpan, index, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                searchResults++;
                index = messageLower.indexOf(queryLower, index + 1);
            }
        }

        String text = Html.fromHtml(currentItem.message).toString();

        currentItem.clickableSpans = new ArrayList<>();


        String[] words = text.split("\\s+");
        int counter = 0;
        String emojiRegex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";
        //adjustment is needed in case there are two same urls in the message
        int adjust = 0;
        for (final String word : words) {
            Matcher matchEmo = Pattern.compile(emojiRegex).matcher(word);
            boolean emojiPresent = false;
            while (matchEmo.find()) {
                emojiPresent = true;
                break;
            }
            if (!emojiPresent && Patterns.WEB_URL.matcher(word).matches()) {
                int spanStart = text.indexOf(word, adjust);
                int spanEnd = spanStart + word.length();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        commonFunctions.urlLoader(word);
                        /*Intent webViewIntent = new Intent(activity, WebViewActivity.class);
                        webViewIntent.putExtra("url", word);
                        activity.startActivity(webViewIntent);*/
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                    }
                };
                currentItem.clickableSpans.add(clickableSpan);
                spannableString.setSpan(currentItem.clickableSpans.get(counter), spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                counter++;
                sentMessageViewHolder.tv_sent_message.setMovementMethod(LinkMovementMethod.getInstance());
            }
            adjust = adjust + word.length();
        }
        sentMessageViewHolder.tv_sent_message.setText(spannableString);

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, MSG_MAX_WIDTH, activity.getResources().getDisplayMetrics());
        sentMessageViewHolder.tv_sent_message.setMaxWidth((int) px);

        File localImageFile = null;
        if (currentItem.localImagePath != null && !currentItem.localImagePath.isEmpty()) {
            sentMessageViewHolder.cv_image_wrapper.setVisibility(View.VISIBLE);
            sentMessageViewHolder.tv_sent_image_time_stamp.setVisibility(View.VISIBLE);
            sentMessageViewHolder.tv_sent_message_time_stamp.setVisibility(View.GONE);
            localImageFile = new File(currentItem.localImagePath);
            //if image is present on local storage load form local

        }

        if (localImageFile != null && localImageFile.exists()) {
            Picasso.get()
                    .load(localImageFile)
                    .placeholder(R.drawable.placeholder)
                    .transform(new CustomBitMapTransform(activity))
                    .into(sentMessageViewHolder.iv_sent_image);
        } else if (!currentItem.image.isEmpty()) {
            sentMessageViewHolder.cv_image_wrapper.setVisibility(View.VISIBLE);
            sentMessageViewHolder.tv_sent_image_time_stamp.setVisibility(View.VISIBLE);
            sentMessageViewHolder.tv_sent_message_time_stamp.setVisibility(View.GONE);

            String imgName = getImageName(currentItem.image);
            //if image is present in hashmap
            if (imageMap.containsKey(imgName)) {
                localImageFile = new File(imageMap.get(imgName));
                //if image is present on local storage load form local
                if (localImageFile.exists()) {
                    Picasso.get()
                            .load(localImageFile)
                            .placeholder(R.drawable.placeholder)
                            .transform(new CustomBitMapTransform(activity))
                            .into(sentMessageViewHolder.iv_sent_image);
                }
                //else load from url
                else {
                    if (currentItem.image.contains("http")) {
                        Picasso.get()
                                .load(currentItem.image)
                                .placeholder(R.drawable.placeholder)
                                .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                                .into(sentMessageViewHolder.iv_sent_image);
                    } else {
                        localImageFile = new File(currentItem.image);
                        Picasso.get()
                                .load(localImageFile)
                                .placeholder(R.drawable.placeholder)
                                .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                                .into(sentMessageViewHolder.iv_sent_image);
                    }

                }
            }

            // if image is not present on local storage load from url
            else {
                if (currentItem.image.contains("http")) {
                    Picasso.get()
                            .load(currentItem.image)
                            .placeholder(R.drawable.placeholder)
                            .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                            .into(sentMessageViewHolder.iv_sent_image);
                } else {
                    localImageFile = new File(currentItem.image);
                    Picasso.get()
                            .load(localImageFile)
                            .placeholder(R.drawable.placeholder)
                            .transform(new CustomBitMapTransform(activity, getImageName(currentItem.image)))
                            .into(sentMessageViewHolder.iv_sent_image);
                }

            }
        } else {
            sentMessageViewHolder.cv_image_wrapper.setVisibility(View.GONE);
            sentMessageViewHolder.tv_sent_image_time_stamp.setVisibility(View.GONE);
            sentMessageViewHolder.tv_sent_message_time_stamp.setVisibility(View.VISIBLE);
        }

        //show or hide progress bar
        if (currentItem.sentState == AppKeys.MESSAGE_STATE_UNSENT) {
            sentMessageViewHolder.pb_upload_status.setVisibility(View.VISIBLE);
        } else {
            sentMessageViewHolder.pb_upload_status.setVisibility(View.GONE);
        }

        if (currentItem.sentState == AppKeys.MESSAGE_STATE_SENT) {
            sentMessageViewHolder.tv_sent_message_time_stamp
                    .setText(commonFunctions.getChatActivityItemTime(currentItem.time));
            sentMessageViewHolder.tv_sent_message_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                    (0, 0, R.drawable.ic_message_sent, 0);
            sentMessageViewHolder.tv_sent_image_time_stamp
                    .setText(commonFunctions.getChatActivityItemTime(currentItem.time));
            sentMessageViewHolder.tv_sent_image_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                    (0, 0, R.drawable.ic_message_sent, 0);

            if (commonFunctions.isMessageSeen(currentItem)) {
                sentMessageViewHolder.tv_sent_message_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                        (0, 0, R.drawable.ic_message_seen, 0);
                sentMessageViewHolder.tv_sent_image_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                        (0, 0, R.drawable.ic_message_seen, 0);
            }

        } else {
            sentMessageViewHolder.tv_sent_message_time_stamp.setText(commonFunctions
                    .getChatActivityItemTime(currentItem.time));
            sentMessageViewHolder.tv_sent_message_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                    (0, 0, R.drawable.ic_hourglass_gray, 0);
            sentMessageViewHolder.tv_sent_image_time_stamp.setText(commonFunctions
                    .getChatActivityItemTime(currentItem.time));
            sentMessageViewHolder.tv_sent_image_time_stamp.setCompoundDrawablesWithIntrinsicBounds
                    (0, 0, R.drawable.ic_hourglass_gray, 0);
        }


   /*     if (currentItem.seen == null || currentItem.seen.equals("0")) {
            sentMessageViewHolder.iv_seen_status.setVisibility(View.GONE);
        } else {
            sentMessageViewHolder.iv_seen_status.setVisibility(View.VISIBLE);
        }*/

        if (currentItem.sentState == AppKeys.MESSAGE_STATE_FAILED) {
            if (!currentItem.image.isEmpty()) {
                sentMessageViewHolder.tv_retry.setVisibility(View.VISIBLE);
                sentMessageViewHolder.tv_retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sentMessageViewHolder.pb_upload_status.setVisibility(View.VISIBLE);
                        sentMessageViewHolder.tv_retry.setVisibility(View.GONE);
                        hitUploadImageApi(position, cid);
                    }
                });
            }
        }


        sentMessageViewHolder.iv_sent_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectionEnabled) {
                    final Dialog imageViewerDialog;
                    imageViewerDialog = new MyCustomThemeDialog(activity, android.R.style.Theme_Black_NoTitleBar);
                    //  imageViewerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
                    ImageView iv_back, iv_edit;
                    PhotoView iv_photo;
                    iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);
                    iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
                    iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);
                    iv_edit.setVisibility(View.GONE);
                    iv_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            imageViewerDialog.dismiss();
                        }
                    });
                    String imgName = getImageName(currentItem.image);
                    if (imageMap.containsKey(imgName)) {
                        File f = new File(imageMap.get(imgName));
                        //if image is present on local storage load form local
                        if (f.exists()) {
                            Picasso.get()
                                    .load(f)
                                    .placeholder(R.drawable.placeholder)
                                    .into(iv_photo);
                        }
                        //else load from url
                        else {
                            Picasso.get()
                                    .load(currentItem.image)
                                    .placeholder(R.drawable.placeholder)
                                    .into(iv_photo);
                        }
                    }
                    // if image is not present on local storage load from url
                    else {
                        Picasso.get()
                                .load(currentItem.image)
                                .placeholder(R.drawable.placeholder)
                                .into(iv_photo);
                    }
                    imageViewerDialog.show();
                }
            }
        });

      /*  if (position == (messageList.size() - 1)) {
            Resources r = activity.getResources();
            int DP64 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, r.getDisplayMetrics());
            int DP2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, r.getDisplayMetrics());
            sentMessageViewHolder.rl_parent.setPadding(0, DP64, 0, 0);
        } else {
            sentMessageViewHolder.rl_parent.setPadding(0, 0, 0, 0);
        }*/
        sentMessageViewHolder.popup.getMenu().clear();

        if (!commonFunctions.isMessageSeen(currentItem)) {
            sentMessageViewHolder.inflater.inflate(R.menu.sent_message_unseen_menu,
                    sentMessageViewHolder.popup.getMenu());
        } else {
            sentMessageViewHolder.inflater.inflate(R.menu.sent_message_seen_menu,
                    sentMessageViewHolder.popup.getMenu());
        }

        sentMessageViewHolder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.info:
                        //   openSeenInfoDialog(position);
                        break;
                    case R.id.copy:
                        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("message", currentItem.message);
                        clipboard.setPrimaryClip(clip);
                        break;
                }
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        if (messageList == null) {
            return 0;
        } else {
            return messageList.size() + 1;
        }
        //  return hierarchyList.size();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, int position) {

        float scaleX = 1f * newWidth / bm.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleX);

        //   Log.d("bitmap info ", "position= " + position + " bm w,h " + bm.getWidth() + "," + bm.getHeight() + "target w,h " + newWidth + "," + newHeight + "scale " + scaleX);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

        if (bm.getHeight() > maxImageHeight) {
            int difference = bm.getHeight() - maxImageHeight;
            bm = Bitmap.createBitmap(bm, 0, difference / 2, bm.getWidth(), maxImageHeight);
        }

        return bm;
    }

    public class SentMessageViewHolder extends RecyclerView.ViewHolder {
        CardView cv_image_wrapper;
        RelativeLayout rl_parent, rl_sent_message_parent;
        TextView tv_sent_message;
        TextView tv_sent_message_time_stamp, tv_sent_image_time_stamp;
        ImageView iv_sent_image;

        TextView tv_retry;
        ProgressBar pb_upload_status;
        TextView tv_seen_status;
        FrameLayout fl_highlighter;

        MenuInflater inflater;
        PopupMenu popup;


        public SentMessageViewHolder(View itemView) {
            super(itemView);
            tv_sent_message = (TextView) itemView.findViewById(R.id.tv_sent_message);
            tv_sent_message_time_stamp = (TextView) itemView.findViewById(R.id.tv_sent_message_time_stamp);
            tv_sent_image_time_stamp = (TextView) itemView.findViewById(R.id.tv_sent_image_time_stamp);
            tv_seen_status = (TextView) itemView.findViewById(R.id.tv_seen_status);
            tv_seen_status.setVisibility(View.GONE);
            tv_sent_message.setMaxWidth(messageWidthThreshold);

            rl_sent_message_parent = (RelativeLayout) itemView.findViewById(R.id.rl_sent_message_wrapper);
            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            fl_highlighter = (FrameLayout) itemView.findViewById(R.id.fl_highlighter);
            cv_image_wrapper = (CardView) itemView.findViewById(R.id.cv_image_wrapper);
            //rl_image_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_image_wrapper);
            iv_sent_image = (ImageView) itemView.findViewById(R.id.iv_sent_image);
            tv_retry = (TextView) itemView.findViewById(R.id.tv_retry);
            pb_upload_status = (ProgressBar) itemView.findViewById(R.id.pb_upload_status);


            popup = new PopupMenu(activity, rl_sent_message_parent);
            inflater = popup.getMenuInflater();

        }
    }

    public class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        CardView cv_image_wrapper;
        RelativeLayout rl_parent, rl_received_message_parent;
        TextView tv_received_message;
        TextView tv_user_name, tv_received_message_time_stamp, tv_received_image_time_stamp;
        ImageView iv_received_image;

        FrameLayout fl_highlighter;


        public ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_user_name.setVisibility(View.VISIBLE);
            tv_received_message = (TextView) itemView.findViewById(R.id.tv_received_message);
            tv_received_message_time_stamp = (TextView) itemView.findViewById(R.id.tv_received_message_time_stamp);
            tv_received_image_time_stamp = (TextView) itemView.findViewById(R.id.tv_received_image_time_stamp);
            fl_highlighter = (FrameLayout) itemView.findViewById(R.id.fl_highlighter);

            rl_parent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            rl_received_message_parent = (RelativeLayout) itemView.findViewById(R.id.rl_received_message_parent);
            tv_received_message.setMaxWidth(messageWidthThreshold);
            cv_image_wrapper = (CardView) itemView.findViewById(R.id.cv_image_wrapper);
            //   rl_image_wrapper = (RelativeLayout) itemView.findViewById(R.id.rl_image_wrapper);
            iv_received_image = (ImageView) itemView.findViewById(R.id.iv_received_image);

        }
    }

    public class SystemMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message;


        public SystemMessageViewHolder(View itemView) {
            super(itemView);
            tv_message = (TextView) itemView.findViewById(R.id.tv_message);

        }
    }

    public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        ProgressBar pb_loading_content;
        TextView tv_no_more_results;

        public ProgressBarViewHolder(View itemView) {
            super(itemView);
            pb_loading_content = (ProgressBar) itemView.findViewById(R.id.pb_loading_content);
            tv_no_more_results = (TextView) itemView.findViewById(R.id.tv_no_more_results);
            tv_no_more_results.setVisibility(View.GONE);
        }
    }

    public String getImageName(String path) {
        if (path.contains("cache")) {
            return path;
        } else {
            StringBuilder sb = new StringBuilder(path);
            String revStr = sb.reverse().toString();
            String imgName = revStr.substring(revStr.indexOf(".") + 1, revStr.indexOf("/"));
            sb = new StringBuilder(imgName);
            path = sb.reverse().toString();
        }
        return path;
    }

    private void saveBitmapToStorage(String imageName, Bitmap bitmap) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/sdcard/Luneblaze/Luneblaze_chat_images/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Luneblaze/Luneblaze_chat_images/");
            wallpaperDirectory.mkdirs();
        }

        File destination = new File(new File("/sdcard/Luneblaze/Luneblaze_chat_images/"), imageName + ".jpg");
        if (destination.exists()) {
            destination.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            MediaStore.Images.Media.insertImage(activity.getContentResolver(), destination.getAbsolutePath(),
                    imageName + ".jpg", "Chat image");
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (!imageMap.containsKey(imageName)) {
            imageMap.put(imageName, destination.getAbsolutePath());
            preferenceUtils.saveChatImagesMap(imageMap);
        }

    }

    class CustomBitMapTransform implements Transformation {

        RenderScript rs;
        String imageName;

        public CustomBitMapTransform(Context context) {
            super();
            rs = RenderScript.create(context);
        }


        public CustomBitMapTransform(Context context, String imageName) {
            super();
            this.imageName = imageName;
            rs = RenderScript.create(context);
        }

        @Override
        public Bitmap transform(Bitmap source) {
            if (imageName != null && !imageName.isEmpty()) {
                saveBitmapToStorage(imageName, source);
            }
            double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            int targetHeight = (int) (imageWidth * aspectRatio);

            Bitmap result = getResizedBitmap(source, imageWidth, targetHeight, 0);

            if (result != source) {
                // Same bitmap is returned if sizes are the same
                source.recycle();
            }


            return result;
        }


        @Override
        public String key() {
            return "transformation" + " desiredWidth";
        }
    }


    public void hitUploadImageApi(final int messageNum, final String cid) {

        final PojoChatMessage newMsg;
        newMsg = messageList.get(messageNum);
        File file = commonFunctions.getScaledDownImage(newMsg.image);
        // file = getScaledDownImage(file);
        Uri contentUri = commonFunctions.getImageContentUri(activity, file);
        String mimeType = "";
        mimeType = activity.getContentResolver().getType(contentUri);
        RequestBody mFile = RequestBody.create(MediaType.parse(mimeType), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), mFile);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), newMsg.userId);
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), newMsg.message);
        RequestBody temp_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(newMsg.tempMsgId));

        //  RequestBody recipient = RequestBody.create(MediaType.parse("text/plain"), newMsg.recipient);
        RequestBody conversation_id = RequestBody.create(MediaType.parse("text/plain"), cid);

        Call<PojoCreateMessageResponse> call = apiService.uploadChatImage(fileToUpload, user_id,
                message, temp_id, null, conversation_id);
        call.enqueue(new Callback<PojoCreateMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMessageResponse> call, Response<PojoCreateMessageResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    PojoCreateMessageResponseData item = response.body().data;
                    newMsg.setMessageId(item.lastMessageId);
                    newMsg.setImage(item.image);
                    newMsg.setTime(item.time);
                    newMsg.setUserName(item.name);
                    newMsg.setUserFullname(item.name);
                    newMsg.setUserGender("male");
                    newMsg.setUserPicture(item.picture);
                    newMsg.isImageUploading = false;
                    newMsg.setCid(cid);
                    newMsg.setSentState(AppKeys.MESSAGE_STATE_SENT);
                    newMsg.setTempMsgId(item.tempMsgId);

                    //  preferenceUtils.saveConversation(cid, messageList);


                    List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);
                  /*  for (int j = 0; j < savedList.size(); j++) {
                        //add any unsent messages
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT) {
                            messageList.add(savedList.get(j));
                        }
                    }*/

                    for (int j = 0; j < savedList.size(); j++) {
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                && savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                            break;
                        }
                    }

                 /*   for (int j = 0; j < savedList.size(); j++) {
                        //replace the first unsent msg that we find
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                &&savedList.get(j).message.equals(newMsg.message)) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                            break;
                            //  messageList.add(savedList.get(j));
                        }
                    }*/
                    preferenceUtils.saveConversation(cid, savedList);

                    for (int j = 0; j < messageList.size(); j++) {
                        if (messageList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                && messageList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {

                            messageList.get(j).setMessageId(item.lastMessageId);
                            messageList.get(j).setImage(item.image);
                            //   messageListdList.get(j)wMsg.setTime(item.time);
                            messageList.get(j).setUserName(item.name);
                            messageList.get(j).setUserFullname(item.name);
                            messageList.get(j).setUserGender("male");
                            messageList.get(j).setUserPicture(item.picture);
                            messageList.get(j).setCid(cid);
                            messageList.get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                            messageList.get(j).setTempMsgId(item.tempMsgId);

                            Log.d("messaging_service", messageList.get(j).message + " updated sent state");
                            break;
                        }
                    }
                    notifyDataSetChanged();
                    Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(pushNotification);
                }

            }

            @Override
            public void onFailure(Call<PojoCreateMessageResponse> call, Throwable t) {

                // Log error here since request failed
                newMsg.setSentState(AppKeys.MESSAGE_STATE_FAILED);

                List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);

                for (int j = 0; j < savedList.size(); j++) {
                    if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                            && savedList.get(j).tempMsgId == newMsg.tempMsgId) {
                        savedList.remove(j);
                        savedList.add(j, newMsg);
                        break;
                    }
                }

                preferenceUtils.saveConversation(cid, savedList);
                Intent msgFailed = new Intent(Config.MESSAGE_FAILED);
                LocalBroadcastManager.getInstance(activity).sendBroadcast(msgFailed);
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

}

