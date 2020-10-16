package com.avadna.luneblaze.listdiffcallbacks;

import android.os.Bundle;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

public class UserListDiffCallback extends DiffUtil.Callback {

    private List<PojoUserData> mOldList;
    private List<PojoUserData> mNewList;


    public UserListDiffCallback(List<PojoUserData> oldList, List<PojoUserData> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }


    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).userId.equals(mOldList.get(oldItemPosition).userId);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).equals(mOldList.get(oldItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        PojoUserData newUser = mNewList.get(newItemPosition);
        PojoUserData oldProduct = mOldList.get(oldItemPosition);
        Bundle diffBundle = new Bundle();
        if (!newUser.userId.equals(oldProduct.userId)) {
            diffBundle.putString(AppKeys.USER_ID, newUser.userId);
        }
        if (!newUser.userName.equals(oldProduct.userName)) {
            diffBundle.putString(AppKeys.USER_NAME, newUser.userName);
        }
        if (!newUser.userFullname.equals(oldProduct.userFullname)) {
            diffBundle.putString(AppKeys.USER_FULL_NAME, newUser.userFullname);
        }
        if (!newUser.userPicture.equals(oldProduct.userPicture)) {
            diffBundle.putString(AppKeys.USER_PICTURE, newUser.userPicture);
        }
        if (!newUser.userWork.equals(oldProduct.userWork)) {
            diffBundle.putString(AppKeys.USER_WORK, newUser.userWork);
        }
        if (!newUser.userWorkTitle.equals(oldProduct.userWorkTitle)) {
            diffBundle.putString(AppKeys.USER_WORK_TITLE, newUser.userWorkTitle);
        }
        if (!newUser.userWorkPlace.equals(oldProduct.userWorkPlace)) {
            diffBundle.putString(AppKeys.USER_WORK_PLACE, newUser.userWorkPlace);
        }
        if (!newUser.mutualFriendsCount.equals(oldProduct.mutualFriendsCount)) {
            diffBundle.putString(AppKeys.USER_MUTUAL_FRIEND_COUNT, newUser.mutualFriendsCount);
        }
        if (!newUser.connection.equals(oldProduct.connection)) {
            diffBundle.putString(AppKeys.USER_CONNECTION, newUser.connection);
        }
        if (diffBundle.size() == 0) return null;
        return diffBundle;
    }
}
