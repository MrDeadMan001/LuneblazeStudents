package com.avadna.luneblaze.fragments.home.sessionslist;

import android.app.DatePickerDialog;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.SelectedVenuesListAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.VenueSearchResultAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoGetAllSessionsResponse;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoUpcomingSession;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.avadna.luneblaze.adapters.AssignedSessionAdapter;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionListAdapter;
import com.avadna.luneblaze.adapters.UpcomingSessionAdapter;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoAssignedSessions;
import com.avadna.luneblaze.pojo.pojoAllSessions.PojoOrganisedSession;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

//todo change bg color of tabs
public class AllSessionsListFragmentOld extends Fragment implements
        VenueSearchResultAdapter.VenueSearchResultAdapterCallback,
        SelectedVenuesListAdapter.SelectedVenuesListAdapterCallback,
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {
    PojoSessions sessions;
    SwipeRefreshLayout srl_refresh;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    String mParam1;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ProgressBar pb_loading_content;

    RecyclerView rv_assigned_session_list;
    AssignedSessionAdapter assignedSessionAdapter;
    List<PojoAssignedSessions> assignedSessions = new ArrayList<>();

    RecyclerView rv_organised_session_list;
    OrganisedSessionListAdapter organisedSessionListAdapter;
    List<PojoOrganisedSession> organisedSessions = new ArrayList<>();

    RecyclerView rv_upcoming_session_list;
    UpcomingSessionAdapter upcomingSessionAdapter;
    List<PojoUpcomingSession> upcomingSessions = new ArrayList<>();

    int offset;

    String interest_id;
    TextView tv_upcoming_sessions, tv_assigned_sessions, tv_organised_sessions;
    View.OnClickListener mOnClickListener;

    TextView tv_filter;
    Dialog filterDialog;
    TextView tv_location, tv_my_role, tv_time, tv_venue, tv_interest_option;
    TextView tv_done, tv_cancel;


    static final String SESSION_ROLES_ANY = "Any";
    static final String SESSION_ROLES_INITIATED = "Initiated";
    static final String SESSION_ROLES_CONDUCTED = "Conducted";
    static final String SESSION_ROLES_ATTENDED = "Attended";
    static final String SESSION_ROLES_JOINED = "Joined";
    String newSessionRole = SESSION_ROLES_ANY;
    String oldSessionRole = SESSION_ROLES_ANY;


    static final String LOCATION_ANY = "Any";
    static final String LOCATION_10KM = "Within_10KM";
    static final String LOCATION_GIVEN_COORDS = "10KM_From_lat_long_provided";
    static final String LOCATION_VENUE = "location_venue";
    static final String LOCATION_MY_VENUES = "my_venues";

    String newIsMyVenuesSelected = "0";
    String oldIsMyVenuesSelected = "0";


    static final String INTEREST_OPTION_ANY = "Any";
    static final String INTEREST_OPTION_MY_INTEREST = "my_interest";
    static final String INTEREST_OPTION_SEARCH = "search_interest";
    String newInterestOption = INTEREST_OPTION_ANY;
    String oldInterestOption = INTEREST_OPTION_ANY;


    String newIsMyInterestSelected = "0";
    String oldIsMyInterestSelected = "0";


    String newLocation = LOCATION_ANY;
    String oldLocation = LOCATION_ANY;

    static final String VENUE_ANY = "";
    // static final String VENUE_PICKED = "venue_picked";

    String oldVenueType = VENUE_ANY;
    String newVenueType = VENUE_ANY;

    String oldVenueNameToShow = "Any";
    String newVenueNameToShow = "";

    String oldVenueId = "";
    String newVenueId = "";

    static final String TIME_ANY = "";
    static final String TIME_10_DAYS = "Within_10_days";
    static final String TIME_DATE_RANGE = "date_range";

    String oldTime = "";
    String newTime = "";


    static final String DATE_ANY = "";
    static final String START_DATE = "start_date";
    static final String END_DATE = "end_date";


    boolean startDateSet = false;
    String startDateToShow = "";
    String oldStartDate = DATE_ANY;
    String newStartDate = DATE_ANY;

    boolean endDateSet = false;
    String endDateToShow = "";
    String oldEndDate = DATE_ANY;
    String newEndDate = DATE_ANY;

    TextView tv_start_date, tv_end_date;
    String[] monthNames = {"", "Jan", "Feb", "March", "April", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    int dateArr[][] = new int[2][3];


    Dialog venueSearchDialog;
    RecyclerView rv_venue_search_result;
    VenueSearchResultAdapter venueResultsListAdapter;
    List<PojoGetVenueListResponseData> venueSearchResultList = new ArrayList<>();

    RecyclerView rv_selected_venue_list_in_search, rv_selected_venue_list_in_filter;
    SelectedVenuesListAdapter selectedVenuesListAdapter_in_search, selectedVenuesListAdapter_in_filter;
    List<PojoGetVenueListResponseData> selectedVenuesList = new ArrayList<>();


    List<String> selectedVenueIdList = new ArrayList<>();


    int PLACE_PICKER_REQUEST = 1;
    double latitude = 28;
    double longitude = 77;

    Dialog interestSearchDialog;
    RecyclerView rv_interest_search_result;
    InterestSearchResultAdapter interestSearchResultAdapter;
    List<PojoSearchInterestWithTextResponseData> interestSearchResultList = new ArrayList<>();
    EditText et_interest_name;


    int[] location = new int[2];
    RecyclerView rv_added_interests_in_filter, rv_added_interests_in_search;
    TaggedInterestAdapter taggedInterestAdapter_in_filter, taggedInterestAdapter_in_search;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics;
    List<PojoGetInterestListResponseDataListItem> selectedInterestList = new ArrayList<>();
    List<String> selectedInterestIdList = new ArrayList<>();

    int screenHeight, screenWidth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_sessions_list, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions=new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initDispMetrics();
        initViews(baseLayout);
        initSessionList(baseLayout);
        initClickListener();
        setClickListeners();
        if (getArguments() != null) {
            mParam1 = getArguments().getString("sessions");
            if (!mParam1.equals("null")) {
                Gson gson = new Gson();
                sessions = gson.fromJson(mParam1, PojoSessions.class);

                assignedSessions.addAll(sessions.assigned);
                assignedSessionAdapter.notifyDataSetChanged();

                organisedSessions.addAll(sessions.organized);
                organisedSessionListAdapter.notifyDataSetChanged();

                //workaround used as sometimes in api the array name is different
                if (upcomingSessions.size() == 0 && sessions.upcoming != null) {
                    upcomingSessions.addAll(sessions.upcoming);
                }
                if (upcomingSessions.size() == 0 && sessions.verified != null) {
                    upcomingSessions.addAll(sessions.verified);
                }
                pb_loading_content.setVisibility(View.GONE);
                upcomingSessionAdapter.notifyDataSetChanged();
            }
        } else {
            hitGetAllSessionsApi(user_id, "distance", "ASC", offset, latitude, longitude,
                    oldSessionRole, oldLocation, selectedVenueIdList, oldIsMyVenuesSelected,
                    selectedInterestIdList, oldIsMyInterestSelected, oldTime, oldStartDate, oldEndDate);
        }
        return baseLayout;
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        screenWidth = (int) (0.8f * screenWidth);
    }

    private void hitGetAllSessionsApi(String user_id, String sort, String order, int offset,
                                      double latitude, double longitude, String session_role,
                                      String location_based, List<String> venues,
                                      String isMyVenuesSelected, List<String> interests,
                                      String isMyInterestSelected, String time_based,
                                      String start_date, String end_date) {

        if (start_date.equals(DATE_ANY)) {
            start_date = null;
        }
        if (end_date.equals(DATE_ANY)) {
            end_date = null;
        }
        HashMap<String, String> interestMap = new HashMap<>();
        for (int i = 0; i < interests.size(); i++) {
            interestMap.put("interest_search[" + i + "]", interests.get(i));
        }

        HashMap<String, String> venueMap = new HashMap<>();
        for (int i = 0; i < venues.size(); i++) {
            venueMap.put("venue_id_search[" + i + "]", venues.get(i));
        }

        Call<PojoGetAllSessionsResponse> call = apiService.getAllSessionsApiOld(user_id, sort, order,
                String.valueOf(offset), String.valueOf(latitude), String.valueOf(longitude),
                session_role, location_based, venueMap, isMyVenuesSelected, interestMap,
                isMyInterestSelected, time_based, start_date, end_date);

        call.enqueue(new Callback<PojoGetAllSessionsResponse>() {
            @Override
            public void onResponse(Call<PojoGetAllSessionsResponse> call, Response<PojoGetAllSessionsResponse> response) {
                String message = "";
                if (response.body() != null && response.body().data != null) {
                    assignedSessions.clear();
                    assignedSessions.addAll(response.body().data.sessions.assigned);
                    assignedSessionAdapter.notifyDataSetChanged();

                    organisedSessions.clear();
                    organisedSessions.addAll(response.body().data.sessions.organized);
                    organisedSessionListAdapter.notifyDataSetChanged();

                    upcomingSessions.clear();
                    upcomingSessions.addAll(response.body().data.sessions.verified);
                    upcomingSessionAdapter.notifyDataSetChanged();

                    /*verifiedSessions.clear();
                    verifiedSessions.addAll(response.body().data.sessions.verified);
                    verifiedSessionAdapter.notifyDataSetChanged();*/

                }
                srl_refresh.setRefreshing(false);
                pb_loading_content.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<PojoGetAllSessionsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(AllSessionsListFragmentOld.this.getActivity(),
                        t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                srl_refresh.setRefreshing(false);

            }
        });
    }


    private void setClickListeners() {
        tv_upcoming_sessions.setOnClickListener(mOnClickListener);
        tv_assigned_sessions.setOnClickListener(mOnClickListener);
        tv_organised_sessions.setOnClickListener(mOnClickListener);
        tv_filter.setOnClickListener(mOnClickListener);
    }

    private void initViews(View baseLayout) {
        tv_upcoming_sessions = (TextView) baseLayout.findViewById(R.id.tv_upcoming_sessions);
        tv_assigned_sessions = (TextView) baseLayout.findViewById(R.id.tv_assigned_sessions);
        tv_organised_sessions = (TextView) baseLayout.findViewById(R.id.tv_organised_sessions);
        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);

        tv_filter = (TextView) baseLayout.findViewById(R.id.tv_filter);

        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);

        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                offset=0;
                hitGetAllSessionsApi(user_id, "distance", "ASC", offset, latitude,
                        longitude, oldSessionRole, oldLocation, selectedVenueIdList,
                        oldIsMyVenuesSelected, selectedInterestIdList, oldIsMyInterestSelected,
                        oldTime, oldStartDate, oldEndDate);
            }
        });

    }

    private void initSessionList(View baseLayout) {

        upcomingSessions = new ArrayList<>();
        rv_upcoming_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_upcoming_session_list);
        rv_upcoming_session_list.setNestedScrollingEnabled(true);
        upcomingSessionAdapter = new UpcomingSessionAdapter(getActivity(), upcomingSessions, true);
        rv_upcoming_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false));
        rv_upcoming_session_list.setAdapter(upcomingSessionAdapter);

        assignedSessions = new ArrayList<>();
        rv_assigned_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_assigned_session_list);
        rv_assigned_session_list.setNestedScrollingEnabled(true);
        assignedSessionAdapter = new AssignedSessionAdapter(getActivity(), assignedSessions, true);
        rv_assigned_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false));
        rv_assigned_session_list.setAdapter(assignedSessionAdapter);

        organisedSessions = new ArrayList<>();
        rv_organised_session_list = (RecyclerView) baseLayout.findViewById(R.id.rv_organised_session_list);
        rv_organised_session_list.setNestedScrollingEnabled(true);
        organisedSessionListAdapter = new OrganisedSessionListAdapter(getActivity(), organisedSessions, true);
        rv_organised_session_list.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,
                false));
        rv_organised_session_list.setAdapter(organisedSessionListAdapter);
    }


    private void initClickListener() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_upcoming_sessions:
                        tv_upcoming_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_upcoming_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


                        tv_assigned_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_assigned_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_organised_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_organised_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_upcoming_session_list.setVisibility(View.VISIBLE);
                        rv_assigned_session_list.setVisibility(View.GONE);
                        rv_organised_session_list.setVisibility(View.GONE);

                        break;

                    case R.id.tv_assigned_sessions:
                        tv_assigned_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_assigned_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));


                        tv_upcoming_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_upcoming_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_organised_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_organised_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_upcoming_session_list.setVisibility(View.GONE);
                        rv_assigned_session_list.setVisibility(View.VISIBLE);
                        rv_organised_session_list.setVisibility(View.GONE);


                        break;

                    case R.id.tv_organised_sessions:
                        tv_organised_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_theme_medium));
                        tv_organised_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                        tv_upcoming_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_upcoming_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));
                        tv_assigned_sessions.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        tv_assigned_sessions.setTextColor(ContextCompat.getColor(getContext(), R.color.med_grey));

                        rv_upcoming_session_list.setVisibility(View.GONE);
                        rv_assigned_session_list.setVisibility(View.GONE);
                        rv_organised_session_list.setVisibility(View.VISIBLE);

                        break;

                    case R.id.tv_filter:
                        openFilterDilaog();
                        break;
                }
            }
        };
    }

    private void updateSelectedInterestListSearch() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;

                display = interestSearchDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });
        rv_added_interests_in_search.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_search = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_search.setAdapter(taggedInterestAdapter_in_search);
    }


    private void updateSelectedInterestListFilter() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;
                display = filterDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests_in_filter.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_filter = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_filter.setAdapter(taggedInterestAdapter_in_filter);
    }


    private void updateSelectedVenueList(RecyclerView recyclerView,
                                         SelectedVenuesListAdapter selectedVenuesListAdapter,
                                         final Dialog dialog) {
        GridLayoutManager selectedVenueGridLayoutManager;

        selectedVenueGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        selectedVenueGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = dialog.getWindow().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                String text = selectedVenuesList.get(position).venueName;
                if (!selectedVenuesList.isEmpty()) {
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        recyclerView.setLayoutManager(selectedVenueGridLayoutManager);
        selectedVenuesListAdapter = new SelectedVenuesListAdapter(this, selectedVenuesList);
        recyclerView.setAdapter(selectedVenuesListAdapter);
    }

    private void openFilterDilaog() {
        filterDialog = new MyCustomThemeDialog(getActivity());
        filterDialog.setContentView(R.layout.all_session_list_filter_dialog);
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        final LinearLayout ll_date_range_wrapper = (LinearLayout) filterDialog.findViewById(R.id.ll_date_range_wrapper);
        tv_done = (TextView) filterDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) filterDialog.findViewById(R.id.tv_cancel);
        tv_location = (TextView) filterDialog.findViewById(R.id.tv_location);
        tv_interest_option = (TextView) filterDialog.findViewById(R.id.tv_interest_option);
        tv_my_role = (TextView) filterDialog.findViewById(R.id.tv_my_role);
        tv_time = (TextView) filterDialog.findViewById(R.id.tv_time);
        tv_venue = (TextView) filterDialog.findViewById(R.id.tv_venue);
        tv_start_date = (TextView) filterDialog.findViewById(R.id.tv_start_date);
        tv_end_date = (TextView) filterDialog.findViewById(R.id.tv_end_date);

        tv_length_getter = (TextView) filterDialog.findViewById(R.id.tv_length_getter);
        rv_added_interests_in_filter = filterDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListFilter();

        rv_selected_venue_list_in_filter = filterDialog.findViewById(R.id.rv_selected_venue_list);
        updateSelectedVenueList(rv_selected_venue_list_in_filter, selectedVenuesListAdapter_in_filter, filterDialog);

        if (oldIsMyInterestSelected.equals("1")) {
            tv_interest_option.setText(R.string.my_interests);
        } else {
            switch (oldInterestOption) {
                case INTEREST_OPTION_ANY:
                    tv_interest_option.setText(R.string.any);
                    break;

                case INTEREST_OPTION_SEARCH:
                    tv_interest_option.setText(R.string.choose_interest);
                    break;

            }
        }


        switch (oldSessionRole) {
            case SESSION_ROLES_ANY:
                tv_my_role.setText(R.string.any);
                break;
            case SESSION_ROLES_INITIATED:
                tv_my_role.setText(R.string.initiator);
                break;
            case SESSION_ROLES_ATTENDED:
                tv_my_role.setText(R.string.attendee);
                break;
            case SESSION_ROLES_CONDUCTED:
                tv_my_role.setText(R.string.sharer);
                break;
            case SESSION_ROLES_JOINED:
                tv_my_role.setText(R.string.joined);
                break;
        }

        if (oldIsMyVenuesSelected.equals("1")) {
            tv_location.setText(R.string.my_venues);
        } else {
            switch (oldLocation) {
                case LOCATION_ANY:
                    tv_location.setText(R.string.any);
                    break;

                case LOCATION_10KM:
                    tv_location.setText(R.string.near_me);
                    break;

                case LOCATION_GIVEN_COORDS:
                    tv_location.setText(oldLocation);
                    break;

                case LOCATION_VENUE:
                    tv_location.setText(oldVenueNameToShow);
                    break;
            }
        }

        switch (oldTime) {
            case TIME_10_DAYS:
                tv_time.setText(R.string.any);
                break;

            case TIME_ANY:
                tv_time.setText(R.string.any);
                break;

            case TIME_DATE_RANGE:
                tv_time.setText(R.string.date_range);
                ll_date_range_wrapper.setVisibility(View.VISIBLE);
                break;

        }

        switch (oldStartDate) {
            case DATE_ANY:
                tv_start_date.setText(R.string.any);
                break;

            default:
                tv_start_date.setText(startDateToShow);
                break;
        }

        switch (oldEndDate) {
            case DATE_ANY:
                tv_end_date.setText(R.string.any);
                break;

            default:
                tv_end_date.setText(endDateToShow);
                break;
        }


        final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {


                    case R.id.interest_any:
                        newInterestOption = INTEREST_OPTION_ANY;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.any));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.my_interest:
                        newInterestOption = INTEREST_OPTION_MY_INTEREST;
                        newIsMyInterestSelected = "1";
                        tv_interest_option.setText(getString(R.string.my_interests));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.choose_interest:
                        newInterestOption = INTEREST_OPTION_SEARCH;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.choose_interest));
                        openInterestSearchDialog();
                        rv_added_interests_in_filter.setVisibility(View.VISIBLE);
                        break;


                    case R.id.role_any:
                        newSessionRole = SESSION_ROLES_ANY;
                        tv_my_role.setText(getString(R.string.any));
                        break;

                    case R.id.role_sharer:
                        newSessionRole = SESSION_ROLES_CONDUCTED;
                        tv_my_role.setText(getString(R.string.sharer));
                        break;

                    case R.id.role_initiator:
                        newSessionRole = SESSION_ROLES_INITIATED;
                        tv_my_role.setText(getString(R.string.initiator));
                        break;

                    case R.id.role_attendee:
                        newSessionRole = SESSION_ROLES_ATTENDED;
                        tv_my_role.setText(getString(R.string.attendee));
                        break;

                    case R.id.role_joined:
                        newSessionRole = SESSION_ROLES_JOINED;
                        tv_my_role.setText(getString(R.string.joined));
                        break;


                    case R.id.location_any:
                        newIsMyVenuesSelected = "0";
                        newLocation = LOCATION_ANY;
                        tv_location.setText(getString(R.string.any));
                        newVenueType = VENUE_ANY;
                        oldVenueId = "";
                        oldVenueNameToShow = "Any";
                        tv_venue.setText(getString(R.string.any));
                        rv_selected_venue_list_in_filter.setVisibility(View.GONE);
                        break;
                    case R.id.location_near_me:
                        newIsMyVenuesSelected = "0";
                        newLocation = LOCATION_10KM;
                        tv_location.setText(getString(R.string.near_me));
                        rv_selected_venue_list_in_filter.setVisibility(View.GONE);

                        break;
                    case R.id.location_near_custom:
                        if (getActivity() != null) {
                            newIsMyVenuesSelected = "0";
                            newLocation = LOCATION_GIVEN_COORDS;
                            if (!Places.isInitialized()) {
                                Places.initialize(getActivity().getApplicationContext(),
                                        AES.decrypt(AppKeys.PLACES_API_KEY,AppKeys.enKey));
                            }
                            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                            Intent intent = new Autocomplete.IntentBuilder(
                                    AutocompleteActivityMode.FULLSCREEN, fields)
                                    .build(getActivity());
                            startActivityForResult(intent, PLACE_PICKER_REQUEST);
                            tv_location.setText(getString(R.string.pick_location));
                            rv_selected_venue_list_in_filter.setVisibility(View.GONE);
                        }

                        break;
                    case R.id.venue_select:
                        newIsMyVenuesSelected = "0";
                        newLocation = LOCATION_VENUE;
                        tv_location.setText(getString(R.string.select_venue));
                        openVenueSearchDialog();
                        rv_selected_venue_list_in_filter.setVisibility(View.VISIBLE);

                        break;
                    case R.id.my_venues:
                        newIsMyVenuesSelected = "1";
                        newLocation = LOCATION_ANY;
                        tv_location.setText(getString(R.string.my_venues));
                        rv_selected_venue_list_in_filter.setVisibility(View.GONE);

                        break;


                    case R.id.time_any:
                        newTime = TIME_ANY;
                        tv_time.setText(getString(R.string.any));
                        ll_date_range_wrapper.setVisibility(View.GONE);
                        newEndDate = DATE_ANY;
                        newStartDate = DATE_ANY;
                        break;
                    case R.id.time_10_days:
                        newTime = TIME_10_DAYS;
                        tv_time.setText(getString(R.string.within_ten_days));
                        ll_date_range_wrapper.setVisibility(View.GONE);
                        newEndDate = DATE_ANY;
                        newStartDate = DATE_ANY;
                        break;
                    case R.id.date_range:
                        newTime = TIME_DATE_RANGE;
                        tv_time.setText(getString(R.string.pick_date_range));
                        ll_date_range_wrapper.setVisibility(View.VISIBLE);
                        break;


                    case R.id.start_date_any:
                        newStartDate = DATE_ANY;
                        startDateSet = false;
                        tv_start_date.setText(getString(R.string.any));
                        break;
                    case R.id.start_date_select:
                        openDatePickerDialogBox(0);
                        break;


                    case R.id.end_date_any:
                        newEndDate = DATE_ANY;
                        endDateSet = false;
                        tv_end_date.setText(getString(R.string.any));
                        break;
                    case R.id.end_date_select:
                        openDatePickerDialogBox(1);
                        break;
                }
                return false;
            }
        };


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_done:
                        oldSessionRole = newSessionRole;
                        oldStartDate = newStartDate;
                        oldEndDate = newEndDate;
                        oldLocation = newLocation;
                        oldTime = newTime;
                        oldVenueId = newVenueId;
                        oldVenueType = newVenueType;
                        oldIsMyVenuesSelected = newIsMyVenuesSelected;
                        oldIsMyInterestSelected = newIsMyInterestSelected;
                        oldVenueNameToShow = newVenueNameToShow;

                        if (oldTime.equals(TIME_DATE_RANGE)) {
                            oldTime = TIME_ANY;
                        }
                        offset=0;
                        hitGetAllSessionsApi(user_id, "distance", "ASC", offset, latitude, longitude,
                                oldSessionRole, oldLocation, selectedVenueIdList, oldIsMyVenuesSelected,
                                selectedInterestIdList, oldIsMyInterestSelected, oldTime, oldStartDate, oldEndDate);
                        oldTime = newTime;
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_cancel:
                        newSessionRole = oldSessionRole;
                        newStartDate = oldStartDate;
                        newEndDate = oldEndDate;
                        newLocation = oldLocation;
                        newTime = oldTime;
                        newVenueId = oldVenueId;
                        newIsMyVenuesSelected = oldIsMyVenuesSelected;
                        newIsMyInterestSelected = oldIsMyInterestSelected;
                        newVenueType = oldVenueType;
                        newVenueNameToShow = oldVenueNameToShow;
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_interest_option:
                        PopupMenu interestOptionMenu = new PopupMenu(getActivity(), tv_interest_option);
                        MenuInflater interestOptionMenuInflater = interestOptionMenu.getMenuInflater();
                        interestOptionMenuInflater.inflate(R.menu.interest_session_filter_interest_option_menu,
                                interestOptionMenu.getMenu());
                        interestOptionMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        interestOptionMenu.show();
                        break;

                    case R.id.tv_my_role:
                        PopupMenu sessionRoleMenu = new PopupMenu(getActivity(), tv_my_role);
                        MenuInflater inflater = sessionRoleMenu.getMenuInflater();
                        inflater.inflate(R.menu.interest_session_filter_role_menu, sessionRoleMenu.getMenu());
                        sessionRoleMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        sessionRoleMenu.show();
                        break;

                    case R.id.tv_location:
                        PopupMenu locationMenu = new PopupMenu(getActivity(), tv_location);
                        MenuInflater rolesInflater = locationMenu.getMenuInflater();
                        rolesInflater.inflate(R.menu.interest_session_filter_location_menu, locationMenu.getMenu());
                        locationMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        locationMenu.show();
                        break;

                    case R.id.tv_time:
                        PopupMenu timeMenu = new PopupMenu(getActivity(), tv_time);
                        MenuInflater timeInflater = timeMenu.getMenuInflater();
                        timeInflater.inflate(R.menu.interest_session_filter_time_menu, timeMenu.getMenu());
                        timeMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        timeMenu.show();
                        break;

                    case R.id.tv_start_date:
                        PopupMenu startDateMenu = new PopupMenu(getActivity(), tv_start_date);
                        MenuInflater startDateInflater = startDateMenu.getMenuInflater();
                        startDateInflater.inflate(R.menu.interest_session_filter_start_date_menu, startDateMenu.getMenu());
                        startDateMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        startDateMenu.show();
                        break;

                    case R.id.tv_end_date:
                        PopupMenu endDateMenu = new PopupMenu(getActivity(), tv_end_date);
                        MenuInflater endDateInflater = endDateMenu.getMenuInflater();
                        endDateInflater.inflate(R.menu.interest_session_filter_end_date_menu, endDateMenu.getMenu());
                        endDateMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        endDateMenu.show();
                        break;


                }
            }
        };

        tv_done.setOnClickListener(onClickListener);
        tv_cancel.setOnClickListener(onClickListener);
        tv_my_role.setOnClickListener(onClickListener);
        tv_location.setOnClickListener(onClickListener);
        tv_time.setOnClickListener(onClickListener);
        tv_venue.setOnClickListener(onClickListener);
        tv_start_date.setOnClickListener(onClickListener);
        tv_end_date.setOnClickListener(onClickListener);
        tv_interest_option.setOnClickListener(onClickListener);


        filterDialog.show();
    }

    private void openDatePickerDialogBox(final int i) {
        final Calendar cal = Calendar.getInstance();
        dateArr[i][0] = cal.get(Calendar.DAY_OF_MONTH);
        dateArr[i][1] = cal.get(Calendar.MONTH);
        dateArr[i][2] = cal.get(Calendar.YEAR);

        DatePickerDialog mDatePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                dateArr[i][0] = day;
                dateArr[i][1] = month;
                dateArr[i][2] = year;

                Calendar startdate = Calendar.getInstance();
                startdate.set(dateArr[0][2], dateArr[0][1], dateArr[0][0]);

                Calendar endDate = Calendar.getInstance();
                endDate.set(dateArr[1][2], dateArr[1][1], dateArr[1][0]);
                /*Date oldDate=new Date(dateArr[0][2],dateArr[0][1],dateArr[0][0]);
                Date newDate=new Date(dateArr[1][2],dateArr[1][1],dateArr[1][0]);*/
                if (endDateSet && startDateSet && startdate.after(endDate)) {
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.new_date_after_old_msg), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                } else {
                    switch (i) {
                        case 0:
                            newStartDate = "" + year + "-" + (month + 1) + "-" + day;
                            startDateToShow = day + " " + monthNames[month + 1] + " " + year;
                            startDateSet = true;
                            tv_start_date.setText(startDateToShow);
                            break;
                        case 1:
                            newEndDate = "" + year + "-" + (month + 1) + "-" + day;
                            endDateToShow = day + " " + monthNames[month + 1] + " " + year;
                            endDateSet = true;
                            tv_end_date.setText(endDateToShow);
                            break;
                    }
                }
            }
        }, dateArr[i][2], dateArr[i][0], dateArr[i][1]);
        mDatePickerDialog.show();
    }


    public void openInterestSearchDialog() {

        interestSearchDialog = new MyCustomThemeDialog(getActivity());
        interestSearchDialog.setContentView(R.layout.interest_search_dialog);
        interestSearchDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        interestSearchDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        rv_interest_search_result = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(getActivity(),
                        RecyclerView.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(this, interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);

        tv_length_getter = interestSearchDialog.findViewById(R.id.tv_length_getter);
        rv_added_interests_in_search = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListSearch();


        et_interest_name = (EditText) interestSearchDialog.findViewById(R.id.et_interest_name);
        et_interest_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_interest_name.getText().toString().trim().isEmpty()) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    hitInterestSearchWithTextApi(et_interest_name.getText().toString().trim());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        TextView tv_done_button= (TextView) interestSearchDialog.findViewById(R.id.tv_done_button);
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestSearchDialog.dismiss();
            }
        });

        interestSearchDialog.show();

    }

    public void openVenueSearchDialog() {
        venueSearchDialog = new MyCustomThemeDialog(getActivity());
        venueSearchDialog.setContentView(R.layout.venue_search_dialog);
        venueSearchDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        venueSearchDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        rv_selected_venue_list_in_search = (RecyclerView) venueSearchDialog.findViewById(R.id.rv_selected_venue_list);
        tv_length_getter = venueSearchDialog.findViewById(R.id.tv_length_getter);

        updateSelectedVenueList(rv_selected_venue_list_in_search, selectedVenuesListAdapter_in_search, venueSearchDialog);

        final EditText et_venue_name = (EditText) venueSearchDialog.findViewById(R.id.et_venue_name);
        final ImageButton ib_send;

        et_venue_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hitVenueSearchWithTextApi(40.0f, 74.0f, charSequence.toString(), "0");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ib_send = (ImageButton) venueSearchDialog.findViewById(R.id.ib_send);
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitVenueSearchWithTextApi(40.0f, 74.0f, et_venue_name.getText().toString().trim(), "0");
            }
        });

        venueSearchResultList = new ArrayList<>();
        rv_venue_search_result = (RecyclerView) venueSearchDialog.findViewById(R.id.rv_venue_search_result);
        rv_venue_search_result.setLayoutManager
                (new LinearLayoutManager(getActivity(),
                        RecyclerView.VERTICAL, false));
        rv_venue_search_result.setNestedScrollingEnabled(false);
        venueResultsListAdapter = new VenueSearchResultAdapter(this, venueSearchResultList);
        rv_venue_search_result.setAdapter(venueResultsListAdapter);

        venueSearchDialog.show();
    }


    private void hitInterestSearchWithTextApi(String text) {
        String offset = "0";
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty();

        if (allFieldsOk) {
            Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text, offset);
            call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
                @Override
                public void onResponse(Call<PojoSearchInterestWithTextResponse> call, Response<PojoSearchInterestWithTextResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        interestSearchResultList.clear();
                        for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                            interestSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        interestSearchResultAdapter.notifyDataSetChanged();
                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }


    private void hitVenueSearchWithTextApi(float lat, float lon, String text, String offset) {
        String latitude = Float.toString(lat);
        String longitude = Float.toString(lon);
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty();

        if (allFieldsOk) {
            Call<PojoGetVenueListResponse> call = apiService.searchVenueWithTextApi(latitude, longitude, text, offset);
            call.enqueue(new Callback<PojoGetVenueListResponse>() {
                @Override
                public void onResponse(Call<PojoGetVenueListResponse> call, Response<PojoGetVenueListResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        venueSearchResultList.clear();
                        //only show 4 results in list
                        for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                            venueSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        venueResultsListAdapter.notifyDataSetChanged();
                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoGetVenueListResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                LatLng latLng;
                latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                oldLocation = getString(R.string.near) + " " + place.getName();
                tv_location.setText(getString(R.string.near) + " " + place.getName());
                String toastMsg = String.format("Place: %s", place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

            }
        }
    }


    @Override
    public void venueResultItemClickCallback(int position, PojoGetVenueListResponseData item) {
        for (int i = 0; i < selectedVenuesList.size(); i++) {
            if (selectedVenuesList.get(i).venueId.equals(item.venueId)) {
                return;
            }
        }
        if (selectedVenuesList.size() < 10) {
            selectedVenuesList.add(item);
            selectedVenueIdList.clear();
            for (int i = 0; i < selectedVenuesList.size(); i++) {
                selectedVenueIdList.add(selectedVenuesList.get(i).venueId);
            }
            updateSelectedVenueList(rv_selected_venue_list_in_search,
                    selectedVenuesListAdapter_in_search, venueSearchDialog);
            updateSelectedVenueList(rv_selected_venue_list_in_filter,
                    selectedVenuesListAdapter_in_filter, filterDialog);

        }

  /*      newVenueNameToShow = pojoGetVenueListResponseData.venueName;
        tv_location.setText(newVenueNameToShow);
        newVenueId = pojoGetVenueListResponseData.venueId;
        selectedVenueIdList = new ArrayList<>();
        selectedVenueIdList.add(newVenueId);
        venueSearchDialog.dismiss();*/
    }

    @Override
    public void selectedVenuesItemClickCallback(int position, PojoGetVenueListResponseData item) {
       /* for (int i = 0; i < selectedVenuesList.size(); i++) {
            if (selectedVenuesList.get(i).venueId.equals(item.venueId)) {
                return;
            }
        }
        if (selectedVenuesList.size() < 10) {
            selectedVenuesList.add(item);
            selectedVenueIdList.clear();
            for (int i = 0; i < selectedVenuesList.size(); i++) {
                selectedVenueIdList.add(selectedVenuesList.get(i).venueId);
            }
            updateSelectedVenueList(rv_selected_venue_list_in_search);
        }*/
        selectedVenuesList.remove(position);
        updateSelectedVenueList(rv_selected_venue_list_in_search,
                selectedVenuesListAdapter_in_search, venueSearchDialog);
        updateSelectedVenueList(rv_selected_venue_list_in_filter,
                selectedVenuesListAdapter_in_filter, filterDialog);
    }

    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData data) {
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                = new PojoGetInterestListResponseDataListItem(data.interestId, data.parentId, data.text, 0);
        for (int i = 0; i < selectedInterestList.size(); i++) {
            //if item already present in list then return
            if (selectedInterestList.get(i).interestId
                    .equals(data.interestId)) {
                return;
            }
        }

        //add 10 interests at max
        if (selectedInterestList.size() < 10) {
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            selectedInterestIdList.clear();
            for (int i = 0; i < selectedInterestList.size(); i++) {
                selectedInterestIdList.add(selectedInterestList.get(i).interestId);
            }
            updateSelectedInterestListFilter();
            updateSelectedInterestListSearch();

        }
    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {
        selectedInterestList.remove(position);
       /* selectedInterestIdList.clear();
        for(int i=0;i<selectedInterestList.size();i++){
            selectedInterestIdList.add(selectedInterestList.get(i).interestId);
        }*/
        updateSelectedInterestListFilter();
        updateSelectedInterestListSearch();
    }


}
