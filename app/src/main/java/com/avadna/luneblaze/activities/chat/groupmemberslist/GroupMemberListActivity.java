package com.avadna.luneblaze.activities.chat.groupmemberslist;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.chat.AddChatMemberListAdapter;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberListActivity extends AppCompatActivity implements
        AddChatMemberListAdapter.AddChatMemberAdapterCallback {

    MemberListViewModel memberListViewModel;
    AddChatMemberListAdapter addChatMemberListAdapter;

    EditText et_search_bar;
    ImageButton ib_back_gray;
    ImageButton ib_clear;

    View.OnClickListener onClickListener;

    String group_id = "119";
    List<PojoUserData> userList;
    String type = "add_group_members";
    int offset = 0;

    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonFunctions = new CommonFunctions(this);
        setContentView(R.layout.activity_group_member_list);
        addObserver();
        initViews();
        initClickListeners();
        setClickListeners();
        setTextWatchers();
    }


    private void initViews() {
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        ib_clear = (ImageButton) findViewById(R.id.ib_clear);
        initMemberList();
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_back_gray:
                        finish();
                        break;

                    case R.id.ib_clear:
                        et_search_bar.getText().clear();
                        break;

                }
            }
        };
    }

    private void setTextWatchers() {
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                offset = 0;
                userList.clear();
                addChatMemberListAdapter.notifyDataSetChanged();
                memberListViewModel.getUsers(offset, group_id,et_search_bar.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setClickListeners() {
        ib_back_gray.setOnClickListener(onClickListener);
        ib_clear.setOnClickListener(onClickListener);
    }

    private void addObserver() {
        memberListViewModel = ViewModelProviders.of(this).get(MemberListViewModel.class);
        // memberListViewModel.setActivityContext(GroupMemberListActivity.this);

        final Observer<List<PojoUserData>> userListObserver = new Observer<List<PojoUserData>>() {
            @Override
            public void onChanged(@Nullable final List<PojoUserData> updatedList) {
                if (updatedList != null) {
                    userList.clear();
                    userList.addAll(updatedList);
                    //addChatMemberListAdapter.onNewUsers(userList);
                    addChatMemberListAdapter.notifyDataSetChanged();
                }
            }
        };
        memberListViewModel.getUserListLiveData(offset).observe(this, userListObserver);

        final Observer<Boolean> userListApiResponseObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean status) {
                if (status != null && addChatMemberListAdapter != null) {
                    addChatMemberListAdapter.setShowProgressBar(status);
                }
            }
        };
        memberListViewModel.getFetchUserApiLiveData().observe(this, userListApiResponseObserver);

        final Observer<Boolean> addMemberApiResponseObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean status) {
                if (status != null) {
                    if (status) {
                        commonFunctions.openProgressDialog();
                    } else {
                        commonFunctions.closeProgressDialog();
                    }
                }
            }
        };
        memberListViewModel.getAddMemberApiResponseLiveData().observe(this, addMemberApiResponseObserver);
    }

    private void initMemberList() {
        RecyclerView rv_member_list;
        userList = new ArrayList<>();
        LinearLayoutManager friendListLayoutManager;
        rv_member_list = (RecyclerView) findViewById(R.id.rv_member_list);
        friendListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_member_list.setLayoutManager(friendListLayoutManager);
        addChatMemberListAdapter = new AddChatMemberListAdapter(GroupMemberListActivity.this,
                userList, type);
        addChatMemberListAdapter.setShowProgressBar(true);
        rv_member_list.setAdapter(addChatMemberListAdapter);

        rv_member_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (friendListLayoutManager.findLastVisibleItemPosition() > userList.size() - 3) {
                        memberListViewModel.getUsers(++offset, group_id,et_search_bar.getText().toString().trim());
                    }
                }
            }
        });
        memberListViewModel.getUsers(offset, group_id,et_search_bar.getText().toString().trim());
    }

    @Override
    public void onAddChatMemberMethodCallback(int position, PojoUserData item, String type) {

        switch (type) {
            case "add_group_members":
                memberListViewModel.addMemberToGroup(position, group_id);
                break;
        }
    }

}
