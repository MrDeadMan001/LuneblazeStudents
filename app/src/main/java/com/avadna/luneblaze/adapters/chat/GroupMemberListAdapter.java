package com.avadna.luneblaze.adapters.chat;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatGroupMember;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class GroupMemberListAdapter extends RecyclerView.Adapter<GroupMemberListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Activity activity;
    ApiInterface apiService;
    boolean enableApiCall = true;
    List<PojoChatGroupMember> memberList;
    GroupMemberListAdapter.GroupMemberListAdapterCallback groupMemberListAdapterCallback;
    String group_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String groupOwner;

    public void updateOwnerId(String groupOwner) {
        this.groupOwner = groupOwner;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_username;
        TextView tv_status;
        ImageView iv_user_dp;
        LinearLayout ll_data_wrapper;
        ImageView iv_online_status;
        ImageView iv_more_options;
        PopupMenu popupMenu;
        MenuInflater inflater;


        MyViewHolder(View view) {
            super(view);
            ll_data_wrapper = (LinearLayout) view.findViewById(R.id.ll_data_wrapper);
            tv_username = (TextView) view.findViewById(R.id.tv_username);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            iv_user_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
            iv_online_status = (ImageView) view.findViewById(R.id.iv_online_status);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            popupMenu = new PopupMenu(activity, iv_more_options);
            inflater = popupMenu.getMenuInflater();

            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                }
            });
        }
    }

    public GroupMemberListAdapter(Activity activity, List<PojoChatGroupMember> memberList,
                                  String group_id) {
        try {
            this.groupMemberListAdapterCallback = ((GroupMemberListAdapter.GroupMemberListAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
        this.activity = activity;
        this.memberList = memberList;
        this.group_id = group_id;
        //  this.groupOwner=groupOwner;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();

    }

    @Override
    public GroupMemberListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_group_member_list_item, parent, false);

        return new GroupMemberListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GroupMemberListAdapter.MyViewHolder holder, final int position) {

        final PojoChatGroupMember currentItem = memberList.get(position);
        holder.tv_username.setText(currentItem.userFullName);

        holder.tv_username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(activity,
                        ProfileInfoActivity.class);
                profileIntent.putExtra("target_user_id",currentItem.userId);
                activity.startActivity(profileIntent);
            }
        });

        if(currentItem.isOnline.equals("1")){
            holder.iv_online_status.setVisibility(View.VISIBLE);
        }
        else {
            holder.iv_online_status.setVisibility(View.GONE);
        }

        if(activity!=null)      Glide.with(activity.getApplicationContext()).load(currentItem.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .placeholder(R.drawable.blank_profile_male)
                .into(holder.iv_user_dp);

        holder.ll_data_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupMemberListAdapterCallback.onGroupMemberListMethodCallback(position);
            }
        });

        holder.popupMenu.getMenu().clear();

        if (currentItem.userId.equals(groupOwner)) {
            holder.tv_status.setText(activity.getString(R.string.owner));
        } else {
            holder.tv_status.setText(currentItem.role);
            holder.inflater.inflate(R.menu.chat_group_member_options_menu, holder.popupMenu.getMenu());
            if (currentItem.role.equals("member")) {
                holder.popupMenu.getMenu().getItem(1).setTitle(activity.getString(R.string.promote_to_admin));
            } else {
                holder.popupMenu.getMenu().getItem(1).setTitle(activity.getString(R.string.revoke_admin_status));
            }
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


        //never show options if current item is me
        if (currentItem.userId.equals(user_id)) {
            holder.iv_more_options.setVisibility(View.GONE);
        } else {
            //never show options for owner
            if (currentItem.userId.equals(groupOwner)) {
                holder.iv_more_options.setVisibility(View.GONE);
            } else {
                //show if I am admin
                if (isUserAuthorised(user_id)) {
                    holder.iv_more_options.setVisibility(View.VISIBLE);
                } else {
                    holder.iv_more_options.setVisibility(View.GONE);
                }
            }
        }

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.remove:

                        if (user_id.equals(groupOwner)) {
                            hitRemoveGroupMemberApi(user_id, group_id, currentItem.userId, position);
                        } else {
                            for (int i = 0; i < memberList.size(); i++) {
                                if (memberList.get(i).role.equals("admin")
                                        && memberList.get(i).userId.equals(user_id)) {
                                    hitRemoveGroupMemberApi(user_id, group_id, currentItem.userId, position);
                                    break;
                                }
                            }
                        }
                        break;

                    case R.id.change_status:
                        if (isUserAuthorised(user_id)) {
                            if (currentItem.role.equals("member")) {
                                hitMakeAdminApi(user_id,group_id, currentItem.userId, position);
                            } else {
                                hitRevokeAdminApi(group_id, currentItem.userId, position);
                            }
                        }

                        break;

                }
                return false;
            }
        });
    }

    public boolean isUserAuthorised(String user_id) {
        boolean isAuthorised = false;
        if (user_id.equals(groupOwner)) {
            isAuthorised = true;
        } else {
            for (int i = 0; i < memberList.size(); i++) {
                if (memberList.get(i).userId.equals(user_id)
                        && memberList.get(i).role.equals("admin")) {
                    isAuthorised = true;
                    break;
                }
            }
        }
        return isAuthorised;
    }

    public static interface GroupMemberListAdapterCallback {
        void onGroupMemberListMethodCallback(int position);
    }

    @Override
    public int getItemCount() {
        return memberList.size();
        //  return hierarchyList.size();
    }

    private void hitRemoveGroupMemberApi(String user_id, String group_id, String memberId, final int position) {
        Call<PojoNoDataResponse> call = apiService.removeGroupMember(user_id, group_id, memberId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        memberList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                notifyDataSetChanged();
            }
        });
    }

    private void hitMakeAdminApi(String user_id,String group_id, String memberId, final int position) {
        Call<PojoNoDataResponse> call = apiService.makeUserAdmin(user_id,group_id, memberId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        memberList.get(position).role = activity.getString(R.string.admin);
                        notifyDataSetChanged();
                        groupMemberListAdapterCallback.onGroupMemberListMethodCallback(position);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                notifyDataSetChanged();
            }
        });
    }

    private void hitRevokeAdminApi(String group_id, String memberId, final int position) {
        Call<PojoNoDataResponse> call = apiService.revokeAdminStatus(memberId,group_id, memberId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        memberList.get(position).role = activity.getString(R.string.member);
                        notifyDataSetChanged();
                        groupMemberListAdapterCallback.onGroupMemberListMethodCallback(position);

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                notifyDataSetChanged();
            }
        });
    }

    private void hitLeaveGroupApi(String user_id, String cid, final int position) {
        Call<PojoNoDataResponse> call = apiService.leaveGroup(user_id, cid);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        memberList.remove(position);
                        notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
    }
}