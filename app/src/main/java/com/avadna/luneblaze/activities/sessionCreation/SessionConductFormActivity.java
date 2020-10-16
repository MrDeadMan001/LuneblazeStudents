package com.avadna.luneblaze.activities.sessionCreation;

import android.app.DatePickerDialog;
import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.adapters.VenueSearchResultAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoTopic;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionConductFormActivity extends AppBaseActivity implements
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        VenueSearchResultAdapter.VenueSearchResultAdapterCallback,
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback {

    ApiInterface apiService;
    ScrollView sv_parent_wrapper;
    CommonFunctions commonFunctions;

    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;

    int statusBarHeight = 0,actionBarHeight = 0;

    RecyclerView rv_discussion_topics_list;
    OrganisedSessionTopicsListAdapter discussionTopicListAdapter;
    List<PojoTopic> discussionTopicList;
    EditText et_discussion_topic;
    TextView tv_add_more_discuss;
    RelativeLayout rl_add_discussion_bar;


    RecyclerView rv_prerequisites_list;
    OrganisedSessionTopicsListAdapter preRequisitesListAdapter;
    List<PojoTopic> preRequisiteList;
    EditText et_requisite_topic;
    TextView tv_add_more_requisite;
    RelativeLayout rl_add_requisite_bar;


    RecyclerView rv_interest_search_result;
    InterestSearchResultAdapter interestSearchResultAdapter;
    List<PojoSearchInterestWithTextResponseData> interestSearchResultList;
    EditText et_interest_name;
    TextView tv_interest_search_button;
    LinearLayout ll_interest_search_wrapper;

    RecyclerView rv_venue_search_result;
    VenueSearchResultAdapter venueResultsListAdapter;
    List<PojoGetVenueListResponseData> venueSearchResultList;
    EditText et_venue_name;
    TextView tv_venue_search_button;
    TextView tv_venue_name;
    RelativeLayout rl_venue_wrapper;
    ImageView iv_remove;
    LinearLayout ll_venue_search_wrapper;


    int[] location = new int[2];
    RecyclerView rv_added_interests;
    TaggedInterestAdapter taggedInterestAdapter;
    GridLayoutManager gridLayoutManager;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics;
    List<PojoGetInterestListResponseDataListItem> selectedInterestList ;
    int screenHeight, screenWidth;


    TextView tv_date1, tv_date2, tv_date3, tv_date4;
    String[] monthNames = {"", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    int dateArr[][]=new int[4][3];
   // Spinner sp_purpose_list;
   // ArrayAdapter<String> purposeSpinnerAdapter;
  //  List<String> purpose_options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conduct_session);
        commonFunctions=new CommonFunctions(this);
        initBarSizes();
        initDispMetrics();
        initApis();
        initViews();
        initLists();
        initClickListener();
        setClickListener();
        setTextChangedListeners();
        initFocusChangeListener();
        setFocusChangeListener();
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void initFocusChangeListener() {
        onFocusChangeListener=new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()){
                    case R.id.et_interest_name:
                        if(hasFocus){
                            ll_interest_search_wrapper.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            sv_parent_wrapper.smoothScrollBy(0,y-statusBarHeight-actionBarHeight);
                        }
                        break;
                    case R.id.et_venue_name:
                        if(hasFocus){
                            interestSearchResultList.clear();
                            interestSearchResultAdapter.notifyDataSetChanged();
                            ll_venue_search_wrapper.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            sv_parent_wrapper.smoothScrollBy(0,y-statusBarHeight-actionBarHeight);
                        }
                        break;
                }
            }
        };
    }

    private void setFocusChangeListener() {
        et_interest_name.setOnFocusChangeListener(onFocusChangeListener);
        et_venue_name.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void initBarSizes() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }


        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

      /*  // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        // navigation bar height
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }*/
    }

    private void setTextChangedListeners() {
        et_interest_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_interest_name.getText().toString().trim().isEmpty()){
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                }
                else {
                    hitInterestSearchWithTextApi(et_interest_name.getText().toString().trim());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_venue_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_venue_name.getText().toString().trim().isEmpty()){
                    venueSearchResultList.clear();
                    venueResultsListAdapter.notifyDataSetChanged();
                }
                else {
                    hitVenueSearchWithTextApi(40.0f,74.0f,et_venue_name.getText().toString().trim(),"0");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_date1:
                        openDatePickerDialogBox(0);
                        break;

                    case R.id.tv_date2:
                        openDatePickerDialogBox(1);
                        break;

                    case R.id.tv_date3:
                        openDatePickerDialogBox(2);

                        break;

                    case R.id.tv_date4:
                        openDatePickerDialogBox(3);

                        break;

                    case R.id.tv_add_more_discuss:
                        addItemToDiscussionTopicList();
                        break;

                    case R.id.tv_add_more_requisite:
                        addItemToRequisiteList();
                        break;


                    case R.id.tv_interest_search_button:
                        hitInterestSearchWithTextApi(et_interest_name.getText().toString().trim());
                        break;

                    case R.id.tv_venue_search_button:
                        hitVenueSearchWithTextApi(40.0f,74.0f,et_venue_name.getText().toString().trim(),"0");
                        break;

                    case R.id.iv_remove:
                        rl_venue_wrapper.setVisibility(View.GONE);
                        break;

                }
            }
        };
    }

    private void setClickListener() {

        tv_add_more_discuss.setOnClickListener(onClickListener);
        tv_add_more_requisite.setOnClickListener(onClickListener);

        tv_interest_search_button.setOnClickListener(onClickListener);
        tv_venue_search_button.setOnClickListener(onClickListener);
        iv_remove.setOnClickListener(onClickListener);


        tv_date1.setOnClickListener(onClickListener);
        tv_date2.setOnClickListener(onClickListener);
        tv_date3.setOnClickListener(onClickListener);
        tv_date4.setOnClickListener(onClickListener);
    }





    private void initViews() {
        sv_parent_wrapper=(ScrollView)findViewById(R.id.sv_parent_wrapper);

        et_discussion_topic = (EditText) findViewById(R.id.et_discussion_topic);
        tv_add_more_discuss = (TextView) findViewById(R.id.tv_add_more_discuss);
        rl_add_discussion_bar = (RelativeLayout) findViewById(R.id.rl_add_discussion_bar);


        et_requisite_topic = (EditText) findViewById(R.id.et_requisite_topic);
        tv_add_more_requisite = (TextView) findViewById(R.id.tv_add_more_requisite);
        rl_add_requisite_bar = (RelativeLayout) findViewById(R.id.rl_add_requisite_bar);


        et_interest_name = (EditText) findViewById(R.id.et_interest_name);
        tv_interest_search_button = (TextView) findViewById(R.id.tv_interest_search_button);
        ll_interest_search_wrapper=(LinearLayout) findViewById(R.id.ll_interest_search_wrapper);

        et_venue_name = (EditText) findViewById(R.id.et_venue_name);
        tv_venue_name=(TextView)findViewById(R.id.tv_venue_name);
        rl_venue_wrapper=(RelativeLayout)findViewById(R.id.rl_venue_wrapper);
        tv_venue_search_button = (TextView) findViewById(R.id.tv_venue_search_button);
        ll_venue_search_wrapper=(LinearLayout) findViewById(R.id.ll_venue_search_wrapper);
        iv_remove=(ImageView)findViewById(R.id.iv_remove);


        tv_date1 = (TextView) findViewById(R.id.tv_date1);
        tv_date2 = (TextView) findViewById(R.id.tv_date2);
        tv_date3 = (TextView) findViewById(R.id.tv_date3);
        tv_date4 = (TextView) findViewById(R.id.tv_date4);
      //  sp_purpose_list = (Spinner) findViewById(R.id.sp_purpose_list);

    }

    private void initLists() {
        initDiscussionTopicList();
        initPreRequisiteList();
        initInterestResultList();
        initAddedInterestList();
        initVenueResultList();
     //   initPurposeListSpinner();
    }
    private void initAddedInterestList() {
        selectedInterestList = new ArrayList<>();
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_added_interests = (RecyclerView) findViewById(R.id.rv_added_interests);
        updateSelectedInterestList();
    }

    private void updateSelectedInterestList() {
        gridLayoutManager = (new GridLayoutManager(SessionConductFormActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!selectedInterestList.isEmpty()) {
                    tv_length_getter.setText(selectedInterestList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();


                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / screenWidth;

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests.setLayoutManager(gridLayoutManager);
        taggedInterestAdapter = new TaggedInterestAdapter(selectedInterestList.size(),SessionConductFormActivity.this, selectedInterestList,1);
        rv_added_interests.setAdapter(taggedInterestAdapter);
    }



    private void initDiscussionTopicList() {
        discussionTopicList = new ArrayList<>();
        rv_discussion_topics_list = (RecyclerView) findViewById(R.id.rv_discussion_topics_list);
        rv_discussion_topics_list.setLayoutManager
                (new LinearLayoutManager(SessionConductFormActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_discussion_topics_list.setNestedScrollingEnabled(false);
        discussionTopicListAdapter = new OrganisedSessionTopicsListAdapter(SessionConductFormActivity.this, discussionTopicList,"dis");
        rv_discussion_topics_list.setAdapter(discussionTopicListAdapter);

    }

    private void initPreRequisiteList() {
        preRequisiteList = new ArrayList<>();
        rv_prerequisites_list = (RecyclerView) findViewById(R.id.rv_prerequisites_list);
        rv_prerequisites_list.setLayoutManager
                (new LinearLayoutManager(SessionConductFormActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_prerequisites_list.setNestedScrollingEnabled(false);
        preRequisitesListAdapter = new OrganisedSessionTopicsListAdapter(SessionConductFormActivity.this, preRequisiteList,"preq");
        rv_prerequisites_list.setAdapter(preRequisitesListAdapter);

    }

    private void initInterestResultList() {
        interestSearchResultList = new ArrayList<>();
        rv_interest_search_result = (RecyclerView) findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(SessionConductFormActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(SessionConductFormActivity.this, interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);


    }

    private void initVenueResultList() {
        venueSearchResultList=new ArrayList<>();
        rv_venue_search_result = (RecyclerView) findViewById(R.id.rv_venue_search_result);
        rv_venue_search_result.setLayoutManager
                (new LinearLayoutManager(SessionConductFormActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_venue_search_result.setNestedScrollingEnabled(false);
        venueResultsListAdapter = new VenueSearchResultAdapter(SessionConductFormActivity.this,venueSearchResultList);
        rv_venue_search_result.setAdapter(venueResultsListAdapter);

    }

  /*  private void initPurposeListSpinner() {
        purpose_options = new ArrayList<String>();
        purpose_options.add("Exam");
        purpose_options.add("Out of Interest");
        purpose_options.add("Profession");
        purpose_options.add("Other");


        purposeSpinnerAdapter = new ArrayAdapter<String>(SessionConductFormActivity.this, R.layout.custom_spinner_dropdown_item, purpose_options);
        purposeSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_purpose_list.setAdapter(purposeSpinnerAdapter);
    }
*/
    private void openDatePickerDialogBox(final int i) {
        final Calendar cal = Calendar.getInstance();
        dateArr[i][0] = cal.get(Calendar.DAY_OF_MONTH);
        dateArr[i][1] = cal.get(Calendar.MONTH);
        dateArr[i][2] = cal.get(Calendar.YEAR);

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(SessionConductFormActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateArr[i][0]= day;
                dateArr[i][1] = month;
                dateArr[i][2] = year;
                switch (i){
                    case 0:
                        tv_date1.setText(day + " " + monthNames[month + 1] + " " + year);
                        break;
                    case 1:
                        tv_date2.setText(day + " " + monthNames[month + 1] + " " + year);
                        break;
                    case 2:
                        tv_date3.setText(day + " " + monthNames[month + 1] + " " + year);
                        break;
                    case 3:
                        tv_date4.setText(day + " " + monthNames[month + 1] + " " + year);
                        break;
                }

            }
        }, dateArr[i][2], dateArr[i][1], dateArr[i][0]);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        mDatePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
        mDatePickerDialog.show();
    }



    public void openAddDisscussionDialog() {
        TextView tv_dialog_description, tv_done_button;
        final EditText et_discussion_topic;

        final Dialog dialog = new MyCustomThemeDialog(SessionConductFormActivity.this);
        dialog.setContentView(R.layout.add_discussion_topic_dialog);

        tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);
        tv_done_button = (TextView) dialog.findViewById(R.id.tv_done_button);
        et_discussion_topic = (EditText) dialog.findViewById(R.id.et_discussion_topic);
        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_discussion_topic.getText().toString().trim().isEmpty())
                    discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                discussionTopicListAdapter.notifyDataSetChanged();
                rv_discussion_topics_list.scrollToPosition(discussionTopicList.size() - 1);
                if (discussionTopicList.size() == 5) {
                    ViewGroup.LayoutParams params = rv_discussion_topics_list.getLayoutParams();
                    //  Log.e("recycler height=",""+rv_prerequisites_list.getHeight());
                    params.height = rv_discussion_topics_list.getHeight();
                    rv_discussion_topics_list.setLayoutParams(params);
                }
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }


    private void hitInterestSearchWithTextApi(String text) {
        String offset="0";
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty();

        if (allFieldsOk) {
            Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text,offset);
            call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
                @Override
                public void onResponse(Call<PojoSearchInterestWithTextResponse> call, Response<PojoSearchInterestWithTextResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        interestSearchResultList.clear();
                        for(int i=0;i<response.body().data.size()&&i<4;i++){
                            interestSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        interestSearchResultAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(SessionConductFormActivity.this,
                                "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }
                @Override
                public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(SessionConductFormActivity.this, t.toString(),
                            Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }


    private void hitVenueSearchWithTextApi(float lat,float lon,  String text,String offset) {
        String latitude=Float.toString(lat);
        String longitude=Float.toString(lon);
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty()&&!latitude.isEmpty()&&!longitude.isEmpty();

        if (allFieldsOk) {
            Call<PojoGetVenueListResponse> call = apiService.searchVenueWithTextApi(latitude,longitude,text,offset);
            call.enqueue(new Callback<PojoGetVenueListResponse>() {
                @Override
                public void onResponse(Call<PojoGetVenueListResponse> call, Response<PojoGetVenueListResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        venueSearchResultList.clear();
                        //only show 4 results in list
                        for(int i=0;i<response.body().data.size()&&i<4;i++){
                            venueSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        venueResultsListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(SessionConductFormActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }
                @Override
                public void onFailure(Call<PojoGetVenueListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(SessionConductFormActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }


    private void addItemToRequisiteList() {


        //add max 8 items to list
        if (preRequisiteList.size() < 8) {
            if (!et_requisite_topic.getText().toString().trim().isEmpty()) {
                preRequisiteList.add(new PojoTopic(et_requisite_topic.getText().toString().trim()));
                preRequisitesListAdapter.notifyDataSetChanged();
                et_requisite_topic.getText().clear();
                et_requisite_topic.setHint("Enter topic here");
            }

        }

        //remove ability to add more items
        if (preRequisiteList.size() == 8) {
            rl_add_requisite_bar.setVisibility(View.GONE);
            et_interest_name.requestFocus();
        }

    }


    private void addItemToDiscussionTopicList() {
        //add max 8 items to list
        if (discussionTopicList.size() < 8) {
            if (!et_discussion_topic.getText().toString().trim().isEmpty()) {
                discussionTopicList.add(new PojoTopic(et_discussion_topic.getText().toString().trim()));
                discussionTopicListAdapter.notifyDataSetChanged();
                et_discussion_topic.getText().clear();
                et_discussion_topic.setHint("Enter topic here");

            }
        }

        if (discussionTopicList.size() == 8) {
            rl_add_discussion_bar.setVisibility(View.GONE);
            et_requisite_topic.requestFocus();
        }

    }



    @Override
    public void venueResultItemClickCallback(int position,PojoGetVenueListResponseData pojoGetVenueListResponseData) {
        tv_venue_name.setText(pojoGetVenueListResponseData.venueName);
        rl_venue_wrapper.setVisibility(View.VISIBLE);
    }

    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData item) {
        //todo change the parameters to pojo object
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                =new PojoGetInterestListResponseDataListItem(item.interestId, item.parentId, item.text, 0);

        for(int i=0;i<selectedInterestList.size();i++){
            //if item already present in list then return
            if(selectedInterestList.get(i).interestId
                    .equals(item.interestId)){
                return;
            }
        }

        //add 10 interests at max
        if(selectedInterestList.size()<10){
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            taggedInterestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void discussionTopicItemClickCallback(String action, String type, int position) {

    }
}
