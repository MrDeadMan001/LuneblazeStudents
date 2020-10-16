package com.avadna.luneblaze.activities.chat.groupmemberslist;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MemberListViewModel extends AndroidViewModel {
    private MutableLiveData<List<PojoUserData>> userListLiveData;
    private MutableLiveData<Boolean> fetchUserApiLiveData;
    private MutableLiveData<Boolean> addMemberApiResponseLiveData;


    private Context context;
    private PreferenceUtils preferenceUtils;
    private String user_id;
    private CommonFunctions commonFunctions;
    private ApiInterface apiService;
    private boolean isGetFriendApiCalled = false;
    private String currentQuery = "";

    public MemberListViewModel(Application application) {
        super(application);
        this.context = application.getApplicationContext();
        preferenceUtils = new PreferenceUtils(context);
        commonFunctions = new CommonFunctions(context);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public MutableLiveData<List<PojoUserData>> getUserListLiveData(int offset) {
        if (userListLiveData == null) {
            userListLiveData = new MutableLiveData<>();

        }
        return userListLiveData;
    }


    public MutableLiveData<Boolean> getFetchUserApiLiveData() {
        if (fetchUserApiLiveData == null) {
            fetchUserApiLiveData = new MutableLiveData<>();
        }
        return fetchUserApiLiveData;
    }

    public MutableLiveData<Boolean> getAddMemberApiResponseLiveData() {
        if (addMemberApiResponseLiveData == null) {
            addMemberApiResponseLiveData = new MutableLiveData<>();
        }
        return addMemberApiResponseLiveData;
    }


    public void getUsers(int offset,String group_id, String query) {
        currentQuery = query;

        hitGetUserListApi(user_id, group_id,offset, currentQuery);
    }

    private void hitGetUserListApi(String user_id,String group_id, int offset, String query) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendsNotInGroup(user_id, user_id,
                group_id, query, "0", "0", String.valueOf(offset));
        fetchUserApiLiveData.setValue(true);

        if (!isGetFriendApiCalled) {
            isGetFriendApiCalled = true;
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {

                    isGetFriendApiCalled = false;

                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (query.equals(currentQuery)) {
                                List<PojoUserData> userList = userListLiveData.getValue();
                                if (userList == null || offset == 0) {
                                    userList = new ArrayList<>();
                                }
                                userList.addAll(response.body().data.friends);
                                userListLiveData.setValue(userList);
                                fetchUserApiLiveData.setValue(false);

                            } else {
                                hitGetUserListApi(user_id,group_id, offset, currentQuery);
                            }
                        } else {
                            commonFunctions.setToastMessage(context,
                                    "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(context, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetFriendApiCalled = false;

                }
            });
        }
    }

    public void addMemberToGroup(int position, String group_id) {
        hitAddGroupMemberApi(user_id, group_id, userListLiveData.getValue().get(position).userId, position);

    }

    private void hitAddGroupMemberApi(String user_id, final String group_id, String memberId, final int position) {
        addMemberApiResponseLiveData.setValue(true);
        Call<PojoNoDataResponse> call = apiService.addMemberToGroup(user_id, group_id, memberId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    try {
                        List<PojoUserData> userList = userListLiveData.getValue();
                        userList.remove(position);
                        userListLiveData.setValue(userList);
                    } catch (Exception e) {

                    }
                }
                addMemberApiResponseLiveData.setValue(false);
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(context, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                addMemberApiResponseLiveData.setValue(false);
            }
        });
    }


}
