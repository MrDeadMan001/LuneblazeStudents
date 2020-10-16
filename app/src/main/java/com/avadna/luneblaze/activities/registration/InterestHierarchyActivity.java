package com.avadna.luneblaze.activities.registration;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.CreateInterestActivity;
import com.avadna.luneblaze.adapters.InterestHierarchyRecyclerAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.interestHierarchy.InterestCategoryListAdapter;
import com.avadna.luneblaze.adapters.interestHierarchy.InterestSearchResultAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class InterestHierarchyActivity extends AppBaseActivity implements
        InterestSearchResultAdapter.NewInterestResultAdapterCallback,
        InterestCategoryListAdapter.NewInterestCategoryListAdapterCallback,
        InterestHierarchyRecyclerAdapter.InterestHierarchyRecyclerAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id = "";
    CommonFunctions commonFunctions;

    List<PojoGetInterestListResponseDataListItem> allInterestTree;
    PojoGetInterestListResponseDataListItem root;
    PojoGetInterestListResponseDataListItem[] allInterestArray;

    int totalInterestCount = 0;
    int allInterestCounter = 0;

    View.OnClickListener mOnClickListener;

    EditText et_search_bar;
    ImageButton ib_search, ib_back;

    RecyclerView rv_interest_search_result;
    InterestSearchResultAdapter interestSearchResultAdapter;
    List<PojoGetInterestListResponseDataListItem> interestSearchResultList;
    LinearLayout ll_interest_not_found_wrapper;
    TextView tv_suggest_interest;

    DisplayMetrics displayMetrics;
    RecyclerView rv_list1;
    InterestCategoryListAdapter currentCategoryListAdapter;
    List<PojoGetInterestListResponseDataListItem> currentCategoryList = new ArrayList<>();
    PojoGetInterestListResponseDataListItem currentListParent;

    TextView tv_added_interests_header;
    RecyclerView rv_interest_to_be_sent;
    TaggedInterestAdapter shownInterestListAdapter;
    List<PojoGetInterestListResponseDataListItem> shownInterestList = new ArrayList<>();

    GridLayoutManager gridLayoutManager;
    TextView tv_length_getter;

    // List<PojoGetInterestListResponseDataListItem> checkedInterest = new ArrayList<>();

    int screenHeight, screenWidth;
    RecyclerView rv_hierarchy_indicator;
    InterestHierarchyRecyclerAdapter newHierarchyListAdapter;
    List<PojoGetInterestListResponseDataListItem> interestHierarchyList = new ArrayList<>();
    RecyclerView.LayoutManager hierarchyLayoutManager;

    boolean interestClickEnable = true;
    TextView tv_done_button;
    TextView tv_skip;
    List<PojoGetInterestListResponseDataListItem> loopTestList;

    String type = "";
    private boolean isSearchApiCalled = false;

    ProgressBar pb_loading_content;
    RelativeLayout rl_content_wrapper;

    ImageView iv_outer_ring, iv_inner_ring;
    LinearLayout ll_loading_wrapper;

    Single<List<PojoGetInterestListResponseDataListItem>> resultsObservable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest_hierarchy);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(InterestHierarchyActivity.this, R.color.status_bar_color));
        }
        type = getIntent().getStringExtra("type");
        initDispMetrics();
        initViews();
        initClickListeners();
        setClickListeners();
        setTextWatcher();
        initIntentData();

        if (type == null) {
            openMessageDialog();
            tv_skip.setVisibility(View.VISIBLE);
            ib_back.setVisibility(View.GONE);
        } else {
            tv_skip.setVisibility(View.GONE);
            ib_back.setVisibility(View.VISIBLE);
        }
        if (!preferenceUtils.getAllInterestHierarchyList().isEmpty()) {
            handleApiResponse();
        } else {
            hitGetInterestListApi();
        }
    }


    private void openMessageDialog() {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(InterestHierarchyActivity.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();

            }
        });
        tv_dialog_description.setText(R.string.signup_interest_selection_message);
        confirmationDialog.show();
    }

    private void initIntentData() {
        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
        }.getType();
        Gson gson = new Gson();
        String dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            List<PojoGetInterestListResponseDataListItem> tempList = gson.fromJson(dataStr, type);
            shownInterestList.clear();
            shownInterestList.addAll(tempList);
            updateSelectedInterestList(false);
        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void initViews() {
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        ib_search = (ImageButton) findViewById(R.id.ib_search);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        tv_done_button = (TextView) findViewById(R.id.tv_done_button);
        tv_skip = (TextView) findViewById(R.id.tv_skip);

        ll_interest_not_found_wrapper = (LinearLayout) findViewById(R.id.ll_interest_not_found_wrapper);
        ll_interest_not_found_wrapper.setVisibility(View.GONE);
        tv_suggest_interest = (TextView) findViewById(R.id.tv_suggest_interest);

        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);

        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        ll_loading_wrapper = (LinearLayout) findViewById(R.id.ll_loading_wrapper);

        iv_outer_ring = (ImageView) findViewById(R.id.iv_outer_ring);
        iv_inner_ring = (ImageView) findViewById(R.id.iv_inner_ring);

        ObjectAnimator clockwise = ObjectAnimator.ofFloat(iv_outer_ring, "rotation", 0f, -360f);
        clockwise.setRepeatCount(ValueAnimator.INFINITE);
        clockwise.setInterpolator(new LinearInterpolator());
        clockwise.setDuration(1000);

        ObjectAnimator countercClockwise = ObjectAnimator.ofFloat(iv_inner_ring, "rotation", 0f, 360f);
        countercClockwise.setRepeatCount(ValueAnimator.INFINITE);
        countercClockwise.setInterpolator(new LinearInterpolator());
        countercClockwise.setDuration(1000);

        countercClockwise.start();
        clockwise.start();

        initInterestSearchResultList();
        initCategoryList();
        initAddedInterestList();
        initInterestHierarchyList();

    }

    private void initInterestSearchResultList() {
        interestSearchResultList = new ArrayList<>();
        rv_interest_search_result = (RecyclerView) findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(InterestHierarchyActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(InterestHierarchyActivity.this,
                interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);
    }

    private void initInterestHierarchyList() {
        rv_hierarchy_indicator = (RecyclerView) findViewById(R.id.rv_hierarchy_indicator);
        newHierarchyListAdapter = new InterestHierarchyRecyclerAdapter(InterestHierarchyActivity.this, interestHierarchyList);
        //using horizontal linearlayout as we want horizontal list
        hierarchyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_hierarchy_indicator.setLayoutManager(hierarchyLayoutManager);
        rv_hierarchy_indicator.setAdapter(newHierarchyListAdapter);
    }

    private void initCategoryList() {
        rv_list1 = (RecyclerView) findViewById(R.id.rv_list1);
       // rv_list1.setLayoutManager(new GridLayoutManager(this, 4));
        rv_list1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        currentCategoryListAdapter = new InterestCategoryListAdapter(InterestHierarchyActivity.this,
                currentCategoryList);
        rv_list1.setAdapter(currentCategoryListAdapter);
    }

    private void initAddedInterestList() {
        tv_added_interests_header = (TextView) findViewById(R.id.tv_added_interests_header);
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_interest_to_be_sent = (RecyclerView) findViewById(R.id.rv_interest_to_be_sent);
    }

    private void updateSelectedInterestList(boolean clearList) {
        if (clearList) {
            shownInterestList.clear();
        }
        chooseShownInterests(root);
        gridLayoutManager = (new GridLayoutManager(InterestHierarchyActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!shownInterestList.isEmpty()) {
                    tv_length_getter.setText(shownInterestList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / screenWidth;

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_interest_to_be_sent.setLayoutManager(gridLayoutManager);

        shownInterestListAdapter = new TaggedInterestAdapter(shownInterestList.size(),
                InterestHierarchyActivity.this, shownInterestList, 1);
        rv_interest_to_be_sent.setAdapter(shownInterestListAdapter);
    }

    private void setClickListeners() {
        tv_added_interests_header.setOnClickListener(mOnClickListener);
        tv_suggest_interest.setOnClickListener(mOnClickListener);
        tv_done_button.setOnClickListener(mOnClickListener);
        tv_skip.setOnClickListener(mOnClickListener);
        ib_search.setOnClickListener(mOnClickListener);
        ib_back.setOnClickListener(mOnClickListener);
    }

    private void setTextWatcher() {
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search_bar.getText().toString().trim().isEmpty()) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    ll_interest_not_found_wrapper.setVisibility(View.GONE);
                    setupObservable();
                    subscribeToObservable();
                }
                int b = 6;
            }
        });
    }

    private void setupObservable() {
        resultsObservable = Single.fromCallable(new Callable<List<PojoGetInterestListResponseDataListItem>>() {
            @Override
            public List<PojoGetInterestListResponseDataListItem> call() throws Exception {
                return getResults(et_search_bar.getText().toString().trim());
            }
        });


    }

    private void subscribeToObservable() {
        resultsObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<PojoGetInterestListResponseDataListItem>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<PojoGetInterestListResponseDataListItem> results) {
                        interestSearchResultList.clear();
                        interestSearchResultList.addAll(results);
                        if (interestSearchResultList.isEmpty()) {
                            ll_interest_not_found_wrapper.setVisibility(View.VISIBLE);
                        }
                        interestSearchResultAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private List<PojoGetInterestListResponseDataListItem> getResults(String query) {
        List<PojoGetInterestListResponseDataListItem> resultList = new ArrayList<>();
        HashMap<String, Integer> tempHash = new HashMap<>();
        query = query.toLowerCase();

        for (int i = 0; i < allInterestArray.length; i++) {
            String text = "";
            if (allInterestArray[i] != null) {
                text = allInterestArray[i].text;
                text = text.toLowerCase();
                //  allInterestArray[i].matchScore = distance(text, query);

                if (text.startsWith(query)) {
                    if (!tempHash.containsKey(allInterestArray[i].interestId)) {
                        tempHash.put(allInterestArray[i].interestId, 1);
                        resultList.add(0, allInterestArray[i]);
                    }
                }
                if (text.contains(query)) {
                    if (!tempHash.containsKey(allInterestArray[i].interestId)) {
                        tempHash.put(allInterestArray[i].interestId, 1);
                        resultList.add(allInterestArray[i]);
                    }
                }
            }
        }
        return resultList;
    }


    private void initClickListeners() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_added_interests_header:
                        if (rv_interest_to_be_sent.getVisibility() == View.VISIBLE) {
                            tv_added_interests_header
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            0, 0, R.drawable.ic_expand, 0);
                            rv_interest_to_be_sent.setVisibility(View.GONE);
                        } else {
                            rv_interest_to_be_sent.setVisibility(View.VISIBLE);
                            tv_added_interests_header
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            0, 0, R.drawable.ic_collapse, 0);
                        }
                        break;

                    case R.id.tv_done_button:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                        if (et_search_bar.requestFocus()) {
                            imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                        }
                        if (type != null && type.equals(AppKeys.SELECT_INTEREST)) {
                            Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                            }.getType();
                            Gson gson = new Gson();
                            String dataStr = gson.toJson(shownInterestList, type);
                            Intent intent = new Intent();
                            intent.putExtra("data", dataStr);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            hitUpdateUserInterest(user_id, shownInterestList);
                        }
                        break;

                    case R.id.tv_skip:
                        Intent suggestedFriendsIntent = new Intent(InterestHierarchyActivity.this,
                                SuggestedFriendsActivity.class);
                        startActivity(suggestedFriendsIntent);
                        finish();
                        break;

                    case R.id.ib_back:
                        finish();
                        break;


                    case R.id.tv_suggest_interest:
                        Intent suggestIntent = new Intent(InterestHierarchyActivity.this, CreateInterestActivity.class);
                        startActivity(suggestIntent);
                        break;


                }
            }
        };
    }

    private void hitUpdateUserInterest(String user_id, List<PojoGetInterestListResponseDataListItem> shownInterestList) {
        HashMap<String, String> interestMap = new HashMap<>();
        for (int i = 0; i < shownInterestList.size(); i++) {
            interestMap.put("interest_ids[" + i + "]", shownInterestList.get(i).interestId);
        }
        Call<PojoNoDataResponse> call = apiService.updateUserInterest(user_id, interestMap);
        commonFunctions.openProgressDialog();
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                interestClickEnable = true;
                if (response.body() != null && response.body().status == 1) {
                    Intent suggestedFriendsIntent = new Intent(InterestHierarchyActivity.this,
                            SuggestedFriendsActivity.class);
                    startActivity(suggestedFriendsIntent);
                    finish();
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
            }
        });
    }


    private void hitGetInterestListApi() {
        Call<PojoAllInterestListResponse> call = apiService.getAllInterestsHierarchy("97");
        call.enqueue(new Callback<PojoAllInterestListResponse>() {
            @Override
            public void onResponse(Call<PojoAllInterestListResponse> call, Response<PojoAllInterestListResponse> response) {
                if (response.body() != null) {
                    preferenceUtils.saveAllInterestHierarchyList(response.body().data);
                    handleApiResponse();
                }
            }

            @Override
            public void onFailure(Call<PojoAllInterestListResponse> call, Throwable t) {
                // Log error here since request failed
                int a = 5;
                openNoConnectionDialog(getString(R.string.interests));

            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetInterestListApi();
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }


    }

    private void handleApiResponse() {
        allInterestTree = new ArrayList<>();
        allInterestTree.clear();
        allInterestTree.addAll(preferenceUtils.getAllInterestHierarchyList());
        root = new PojoGetInterestListResponseDataListItem("0", "Home",
                "-1", "1", allInterestTree);
        root.setParent(null);
        totalInterestCount = allInterestTree.size();
        setParentRefRecursively(root);
        allInterestArray = new PojoGetInterestListResponseDataListItem[totalInterestCount];
        allInterestCounter = 0;
        addAllInterestsToArray(root);

        int a = 5;
        int b = 6;

        updateCategoryList(root.interestId);

        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
        }.getType();
        Gson gson = new Gson();
        String dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            List<PojoGetInterestListResponseDataListItem> tempList = gson.fromJson(dataStr, type);
            for (int i = 0; i < tempList.size(); i++) {
                PojoGetInterestListResponseDataListItem item = findItemUsingId(root,
                        tempList.get(i).interestId);
                item.isCbChecked = true;
                setAllChildCheckedStatus(item, true);
            }
            updateSelectedInterestList(true);
        }

        pb_loading_content.setVisibility(View.GONE);
        ll_loading_wrapper.setVisibility(View.GONE);
        rl_content_wrapper.setVisibility(View.VISIBLE);
    }


    private void addAllInterestsToArray(PojoGetInterestListResponseDataListItem root) {
        if (root != null) {
            allInterestArray[allInterestCounter++] = root;
        }

        for (int i = 0; i < root.child.size(); i++) {
            addAllInterestsToArray(root.child.get(i));
        }
    }

    public PojoGetInterestListResponseDataListItem findItemUsingId(PojoGetInterestListResponseDataListItem root, String id) {

        if (root.interestId.equals(id)) {
            return root;
        } else {
            PojoGetInterestListResponseDataListItem target = null;
            for (int i = 0; i < root.child.size(); i++) {
                PojoGetInterestListResponseDataListItem temp = findItemUsingId(root.child.get(i), id);
                if (temp != null) {
                    target = temp;
                }
            }
            return target;
        }
    }

    public void setParentRefRecursively(PojoGetInterestListResponseDataListItem root) {
        if (root.child.isEmpty()) {
            return;
        }
        for (int i = 0; i < root.child.size(); i++) {
            totalInterestCount++;
            root.child.get(i).setParent(root);
            setParentRefRecursively(root.child.get(i));
        }
    }

    public void chooseShownInterests(PojoGetInterestListResponseDataListItem root) {
        if (root != null) {
            if (root.isCbChecked && !root.interestId.equals("0")) {
                shownInterestList.add(root);
            } else {
                for (int i = 0; i < root.child.size(); i++) {
                    chooseShownInterests(root.child.get(i));
                }
            }
        }
    }

    public void setAllChildCheckedStatus(PojoGetInterestListResponseDataListItem node, boolean status) {
        if (node != null && node.child != null) {
            for (int i = 0; i < node.child.size(); i++) {
                node.child.get(i).isCbChecked = status;
                setAllChildCheckedStatus(node.child.get(i), status);
            }
        }
    }

    @Override
    public void interestResultItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {
        PojoGetInterestListResponseDataListItem currentItem = findItemUsingId(root, pojoGetInterestListResponseDataListItem.interestId);
        if (!currentItem.isCbChecked) {
            currentItem.isCbChecked = true;
            setAllChildCheckedStatus(currentItem, true);
            while (currentItem.parentRef != null) {
                boolean allSiblingsChecked = true;
                for (int i = 0; i < currentItem.parentRef.child.size(); i++) {
                    if (!currentItem.parentRef.child.get(i).isCbChecked) {
                        allSiblingsChecked = false;
                        break;
                    }
                }
                if (allSiblingsChecked) {
                    currentItem.parentRef.isCbChecked = true;
                }
                currentItem = currentItem.parentRef;
            }
        }
        commonFunctions.openProgressDialog();
        updateCategoryAndHierarchyList(pojoGetInterestListResponseDataListItem.parentId);
        interestSearchResultList.clear();
        interestSearchResultAdapter.notifyDataSetChanged();
        commonFunctions.closeProgressDialog();
    }

    private void updateCategoryAndHierarchyList(String interestId) {
        updateCategoryList(interestId);
        updateHierarchyList(interestId);
        updateSelectedInterestList(true);
    }

    @Override
    public void onNewInterestCategoryListAdapterCallback(int position, String type, Boolean cbChecked,
                                                         PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {
        if (type.equals("iv")) {
            commonFunctions.openProgressDialog();
            updateCategoryAndHierarchyList(pojoGetInterestListResponseDataListItem.interestId);
            commonFunctions.closeProgressDialog();
           /* interestHierarchyList.add(receivedInterestList.get(position));
            newHierarchyListAdapter.notifyDataSetChanged();*/
        }

        //if the click event is from checkbox
        if (type.equals("cb")) {
            PojoGetInterestListResponseDataListItem currentItem = findItemUsingId(root,
                    pojoGetInterestListResponseDataListItem.interestId);
            if (currentItem.isCbChecked) {
                currentItem.isCbChecked = false;
                setAllChildCheckedStatus(currentItem, false);
                while (currentItem.parentRef != null && currentItem.parentRef.isCbChecked) {
                    currentItem.parentRef.isCbChecked = false;
                    currentItem = currentItem.parentRef;
                }
            } else {
                currentItem.isCbChecked = true;
                setAllChildCheckedStatus(currentItem, true);
                while (currentItem.parentRef != null) {
                    boolean allSiblingsChecked = true;
                    for (int i = 0; i < currentItem.parentRef.child.size(); i++) {
                        if (!currentItem.parentRef.child.get(i).isCbChecked) {
                            allSiblingsChecked = false;
                            break;
                        }
                    }
                    if (allSiblingsChecked) {
                        currentItem.parentRef.isCbChecked = true;
                    }
                    currentItem = currentItem.parentRef;
                }
            }
            updateSelectedInterestList(true);
        }
    }


    @Override
    public void onHierarchyListMethodCallback(int position) {
        updateCategoryAndHierarchyList(interestHierarchyList.get(position).interestId);

    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {

        shownInterestList.remove(position);
        shownInterestListAdapter.notifyDataSetChanged();

        PojoGetInterestListResponseDataListItem currentItem = findItemUsingId(root,
                pojoGetInterestListResponseDataListItem.interestId);

        currentItem.isCbChecked = false;
        setAllChildCheckedStatus(currentItem, false);

        while (currentItem.parentRef != null && currentItem.parentRef.isCbChecked) {
            currentItem.parentRef.isCbChecked = false;
            currentItem = currentItem.parentRef;
        }

        currentCategoryListAdapter.notifyDataSetChanged();


    }


    public void updateCategoryList(String interest_id) {
        currentListParent = findItemUsingId(root, interest_id);
        if (currentListParent != null) {
            currentCategoryList.clear();
            currentCategoryList.addAll(currentListParent.child);
            currentCategoryListAdapter.notifyDataSetChanged();
        }
    }

    public void updateHierarchyList(String id) {
        commonFunctions.openProgressDialog();
        List<PojoGetInterestListResponseDataListItem> tempList = new ArrayList<>();
        PojoGetInterestListResponseDataListItem currentItem = findItemUsingId(root, id);

        tempList.add(currentItem);
        while (currentItem.parentRef != null) {
            tempList.add(currentItem.parentRef);
            currentItem = currentItem.parentRef;
        }

        interestHierarchyList.clear();
        Collections.reverse(tempList);
        interestHierarchyList.addAll(tempList);
        newHierarchyListAdapter.notifyDataSetChanged();
        commonFunctions.closeProgressDialog();
    }


   /* private TreeNode addChildrenToNode(TreeNode rootNode, List<PojoGetInterestListResponseDataListItem> data) {
        if(data.isEmpty()){
            return null;
        }
        rootNode.addChildren(data);
        for(int i=0;i<data.size();i++){
            addChildrenToNode(rootNode.children.get(i),data.get(i).child);
        }
        return rootNode;
    }

    class TreeNode{
        public TreeNode parent;
        public List<TreeNode> children;
        public PojoGetInterestListResponseDataListItem data;

        public TreeNode (PojoGetInterestListResponseDataListItem data){
            this.data=data;
            children=new ArrayList<>();
        }

        public void addChild(TreeNode childNode){
            childNode.parent=this;
            this.children.add(childNode);
        }

        public void addChildren(List<PojoGetInterestListResponseDataListItem> childData){
            for(int i=0;i<childData.size();i++){
                TreeNode childNode=new TreeNode(childData.get(i));
                childNode.parent=this;
                this.children.add(new TreeNode(childData.get(i)));
            }
        }
    }*/

    @Override
    public void onBackPressed() {
        if (interestHierarchyList.size() > 1) {
            PojoGetInterestListResponseDataListItem currentItem = findItemUsingId(root,
                    interestHierarchyList.get(interestHierarchyList.size() - 1).interestId);
            updateCategoryAndHierarchyList(currentItem.parentRef.interestId);

        } else {
            super.onBackPressed();
        }
    }


}
