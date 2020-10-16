package com.avadna.luneblaze.activities.sessions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.MarkAttendeesAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkSessionAttendeesActivity extends AppBaseActivity implements
        MarkAttendeesAdapter.MarkAttendeesAdapterCallback {

    RecyclerView rv_attendee_list;
    LinearLayoutManager attendeeLayoutManager;
    List<PojoUserData> attendeeList;
    MarkAttendeesAdapter markAttendeesAdapter;
    boolean isGetUpvotersApiCalled = false;
    int attendeeListOffset = 0;
    private ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String sessionId = "";

    ImageButton ib_clear_search;
    ImageButton ib_back_gray;
    EditText et_search_bar;

    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_session_attendees);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        sessionId=getIntent().getStringExtra(AppKeys.SESSION_ID);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        initViews();
        initClickListener();
        setClickListener();
    }


    private void initViews() {
        initUserList();
        ib_clear_search = (ImageButton) findViewById(R.id.ib_clear_search);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
    }

    private void initUserList() {
        rv_attendee_list = (RecyclerView) findViewById(R.id.rv_attendee_list);
        //rv_list.setPadding(16,32,16,32);
        attendeeLayoutManager = new LinearLayoutManager(MarkSessionAttendeesActivity.this,
                RecyclerView.VERTICAL, false);
        attendeeList = new ArrayList<>();
        rv_attendee_list.setLayoutManager(attendeeLayoutManager);
        markAttendeesAdapter = new MarkAttendeesAdapter(this, attendeeList);

        rv_attendee_list.setAdapter(markAttendeesAdapter);
        rv_attendee_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    if (!isGetUpvotersApiCalled
                            && attendeeLayoutManager.findLastVisibleItemPosition() > attendeeList.size() - 2) {
                        hitGetUpvotersListApi(user_id, sessionId, "totalattendees", ++attendeeListOffset);
                    }
                }
            }
        });
        attendeeListOffset = 0;
        hitGetUpvotersListApi(user_id, sessionId, "totalattendees", attendeeListOffset);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_clear_search:
                        et_search_bar.getText().clear();
                        break;

                    case R.id.ib_back_gray:
                        finish();
                        break;
                }
            }
        };
    }

    private void setClickListener() {
        ib_back_gray.setOnClickListener(onClickListener);
        ib_clear_search.setOnClickListener(onClickListener);
    }

    private void hitGetUpvotersListApi(String user_id, final String id, String reaction, int offset) {
        Call<PojoUpvotersAndAttendeesListResponse> call = apiService.getUpvotersAndAttendees(user_id,
                id, reaction, String.valueOf(offset));
        if (!isGetUpvotersApiCalled) {
            markAttendeesAdapter.setShowProgressBar(true);
            isGetUpvotersApiCalled = true;
            call.enqueue(new Callback<PojoUpvotersAndAttendeesListResponse>() {
                @Override
                public void onResponse(Call<PojoUpvotersAndAttendeesListResponse> call,
                                       Response<PojoUpvotersAndAttendeesListResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        if (offset == 0) {
                            attendeeList.clear();
                        }
                        attendeeList.addAll(response.body().data);
                        markAttendeesAdapter.notifyDataSetChanged();
                    }
                    isGetUpvotersApiCalled = false;
                    markAttendeesAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoUpvotersAndAttendeesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (MarkSessionAttendeesActivity.this != null)
                        commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    markAttendeesAdapter.setShowProgressBar(false);
                    isGetUpvotersApiCalled = false;
                }
            });
        }
    }


    @Override
    public void onMarkAttendeesMethod(int position, String type) {
        switch (type) {
            case "present":
                markAttendStatus(attendeeList.get(position).userId, sessionId, "1");
         /*       attendeeList.remove(position);
                markAttendeesAdapter.notifyDataSetChanged();*/
                break;

            case "absent":
                markAttendStatus(attendeeList.get(position).userId, sessionId, "0");
                attendeeList.remove(position);
                markAttendeesAdapter.notifyDataSetChanged();
                break;
        }
    }


    private void markAttendStatus(String user_id, String sessionId, String iAttend) {
        Call<PojoNoDataResponse> call = apiService.markAttendStatus(user_id, sessionId, iAttend);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
