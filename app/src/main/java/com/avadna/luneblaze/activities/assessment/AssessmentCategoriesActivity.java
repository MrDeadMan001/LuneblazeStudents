package com.avadna.luneblaze.activities.assessment;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.avadna.luneblaze.adapters.assessment.AssessmentCategoryHierarchyRecyclerAdapter;
import com.avadna.luneblaze.adapters.assessment.AssessmentCategoryListAdapter;
import com.avadna.luneblaze.adapters.assessment.AssessmentCategorySearchResultAdapter;
import com.avadna.luneblaze.adapters.assessment.TaggedAssessmentCategoriesAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.assessment.PojoGetAssessmentCategoriesResponse;
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

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessmentCategoriesActivity extends AppBaseActivity implements
        AssessmentCategorySearchResultAdapter.AssessmentCategorySearchResultCallback,
        AssessmentCategoryListAdapter.AssessmentCategoryListAdapterCallback,
        AssessmentCategoryHierarchyRecyclerAdapter.AssessmentCategoryHierarchyRecyclerAdapterCallback,
        TaggedAssessmentCategoriesAdapter.TaggedAssessmentCategoriesAdapterCallback {

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id = "";
    CommonFunctions commonFunctions;

    List<PojoAssessmentCategory> allCategoriesTree;
    PojoAssessmentCategory root;
    PojoAssessmentCategory[] allCategoriesArray;

    int totalCategoryCount = 0;
    int allCategoryCounter = 0;

    View.OnClickListener mOnClickListener;

    EditText et_search_bar;
    ImageButton ib_search, ib_back;

    RecyclerView rv_assessment_category_search_result;
    AssessmentCategorySearchResultAdapter categorySearchResultAdapter;
    List<PojoAssessmentCategory> categorySearchResultList;

    DisplayMetrics displayMetrics;
    RecyclerView rv_list1;
    AssessmentCategoryListAdapter currentCategoryListAdapter;
    List<PojoAssessmentCategory> currentCategoryList = new ArrayList<>();
    PojoAssessmentCategory currentListParent;

    TextView tv_added_categories_header;
    RecyclerView rv_categories_to_be_sent;
    TaggedAssessmentCategoriesAdapter shownCategoryListAdapter;
    List<PojoAssessmentCategory> shownCategoryList = new ArrayList<>();

    GridLayoutManager gridLayoutManager;
    TextView tv_length_getter;

    // List<PojoGetInterestListResponseDataListItem> checkedInterest = new ArrayList<>();

    int screenHeight, screenWidth;
    RecyclerView rv_hierarchy_indicator;
    AssessmentCategoryHierarchyRecyclerAdapter newHierarchyListAdapter;
    List<PojoAssessmentCategory> assessmentCategoryHierarchyList = new ArrayList<>();
    RecyclerView.LayoutManager hierarchyLayoutManager;

    boolean assessmentCategoryClickEnable = true;
    TextView tv_done_button;
    List<PojoAssessmentCategory> loopTestList;

    String type = "";
    private boolean isSearchApiCalled = false;

    ProgressBar pb_loading_content;
    RelativeLayout rl_content_wrapper;

    ImageView iv_outer_ring, iv_inner_ring;
    LinearLayout ll_loading_wrapper;

    Single<List<PojoAssessmentCategory>> resultsObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_categories);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(AssessmentCategoriesActivity.this, R.color.status_bar_color));
        }
        type = getIntent().getStringExtra("type");
        initDispMetrics();
        initViews();
        initClickListeners();
        setClickListeners();
        setTextWatcher();
        initIntentData();

        if (type == null) {
            // openMessageDialog();
            ib_back.setVisibility(View.GONE);
        } else {
            ib_back.setVisibility(View.VISIBLE);
        }
        if (!preferenceUtils.getAssessmentCategoriesList().isEmpty()) {
            handleApiResponse();
        } else {
            hitGetAssessmentCategoriesApi(user_id);
        }
    }


    private void openMessageDialog() {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(AssessmentCategoriesActivity.this);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();

            }
        });
        //  tv_dialog_description.setText(R.string.signup_assessmentCategory_selection_message);
        confirmationDialog.show();
    }

    private void initIntentData() {
        Type type = new TypeToken<ArrayList<PojoAssessmentCategory>>() {
        }.getType();
        Gson gson = new Gson();
        String dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            List<PojoAssessmentCategory> tempList = gson.fromJson(dataStr, type);
            shownCategoryList.clear();
            shownCategoryList.addAll(tempList);
            updateSelectedCategoryList(false);
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

        initCategorySearchResultList();
        initCategoryList();
        initAddedCategoryList();
        initCategoryHierarchyList();

    }

    private void initCategorySearchResultList() {
        categorySearchResultList = new ArrayList<>();
        rv_assessment_category_search_result = (RecyclerView) findViewById(R.id.rv_assessment_category_search_result);
        rv_assessment_category_search_result.setLayoutManager
                (new LinearLayoutManager(AssessmentCategoriesActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_assessment_category_search_result.setNestedScrollingEnabled(false);
        categorySearchResultAdapter = new AssessmentCategorySearchResultAdapter(AssessmentCategoriesActivity.this,
                categorySearchResultList);
        rv_assessment_category_search_result.setAdapter(categorySearchResultAdapter);
    }

    private void initCategoryHierarchyList() {
        rv_hierarchy_indicator = (RecyclerView) findViewById(R.id.rv_hierarchy_indicator);
        newHierarchyListAdapter = new AssessmentCategoryHierarchyRecyclerAdapter(AssessmentCategoriesActivity.this, assessmentCategoryHierarchyList);
        //using horizontal linearlayout as we want horizontal list
        hierarchyLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_hierarchy_indicator.setLayoutManager(hierarchyLayoutManager);
        rv_hierarchy_indicator.setAdapter(newHierarchyListAdapter);
    }

    private void initCategoryList() {
        rv_list1 = (RecyclerView) findViewById(R.id.rv_list1);
        rv_list1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        currentCategoryListAdapter = new AssessmentCategoryListAdapter(AssessmentCategoriesActivity.this,
                currentCategoryList,"");
        rv_list1.setAdapter(currentCategoryListAdapter);
    }

    private void initAddedCategoryList() {
        tv_added_categories_header = (TextView) findViewById(R.id.tv_added_categories_header);
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_categories_to_be_sent = (RecyclerView) findViewById(R.id.rv_categories_to_be_sent);
    }

    private void updateSelectedCategoryList(boolean clearList) {
        if (clearList) {
            shownCategoryList.clear();
        }
        chooseShownCategories(root);
        gridLayoutManager = (new GridLayoutManager(AssessmentCategoriesActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!shownCategoryList.isEmpty()) {
                    tv_length_getter.setText(shownCategoryList.get(position).text);
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

        rv_categories_to_be_sent.setLayoutManager(gridLayoutManager);

        shownCategoryListAdapter = new TaggedAssessmentCategoriesAdapter(
                AssessmentCategoriesActivity.this, shownCategoryList, 1);
        rv_categories_to_be_sent.setAdapter(shownCategoryListAdapter);
    }

    private void setClickListeners() {
        tv_added_categories_header.setOnClickListener(mOnClickListener);
        tv_done_button.setOnClickListener(mOnClickListener);
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
                    categorySearchResultList.clear();
                    categorySearchResultAdapter.notifyDataSetChanged();
                } else {
                    setupObservable();
                    subscribeToObservable();
                }
                int b = 6;
            }
        });
    }

    private void setupObservable() {
        resultsObservable = Single.fromCallable(new Callable<List<PojoAssessmentCategory>>() {
            @Override
            public List<PojoAssessmentCategory> call() throws Exception {
                return getResults(et_search_bar.getText().toString().trim());
            }
        });


    }

    private void subscribeToObservable() {
        resultsObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<PojoAssessmentCategory>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<PojoAssessmentCategory> results) {
                        categorySearchResultList.clear();
                        categorySearchResultList.addAll(results);
                        categorySearchResultAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    private List<PojoAssessmentCategory> getResults(String query) {
        List<PojoAssessmentCategory> resultList = new ArrayList<>();
        HashMap<String, Integer> tempHash = new HashMap<>();
        query = query.toLowerCase();

        for (int i = 0; i < allCategoriesArray.length; i++) {
            String text = "";
            if (allCategoriesArray[i] != null) {
                text = allCategoriesArray[i].text;
                text = text.toLowerCase();
                //  allCategoriesArray[i].matchScore = distance(text, query);

                if (text.startsWith(query)) {
                    if (!tempHash.containsKey(allCategoriesArray[i].categoryId)) {
                        tempHash.put(allCategoriesArray[i].categoryId, 1);
                        resultList.add(0, allCategoriesArray[i]);
                    }
                }
                if (text.contains(query)) {
                    if (!tempHash.containsKey(allCategoriesArray[i].categoryId)) {
                        tempHash.put(allCategoriesArray[i].categoryId, 1);
                        resultList.add(allCategoriesArray[i]);
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
                    case R.id.tv_added_categories_header:
                        if (rv_categories_to_be_sent.getVisibility() == View.VISIBLE) {
                            tv_added_categories_header
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            0, 0, R.drawable.ic_expand, 0);
                            rv_categories_to_be_sent.setVisibility(View.GONE);
                        } else {
                            rv_categories_to_be_sent.setVisibility(View.VISIBLE);
                            tv_added_categories_header
                                    .setCompoundDrawablesWithIntrinsicBounds(
                                            0, 0, R.drawable.ic_collapse, 0);
                        }
                        break;

                    case R.id.tv_done_button:
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                        if (et_search_bar.requestFocus()) {
                            imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                        }
                        Type type = new TypeToken<ArrayList<PojoAssessmentCategory>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String dataStr = gson.toJson(shownCategoryList, type);
                        Intent intent = new Intent();
                        intent.putExtra("data", dataStr);
                        setResult(Activity.RESULT_OK, intent);
                        finish();

                        break;

                    case R.id.ib_back:
                        finish();
                        break;

                }
            }
        };
    }




    private void hitGetAssessmentCategoriesApi(String user_id) {
        Call<PojoGetAssessmentCategoriesResponse> call = apiService.getAssessmentCategories(user_id);
        call.enqueue(new Callback<PojoGetAssessmentCategoriesResponse>() {
            @Override
            public void onResponse(Call<PojoGetAssessmentCategoriesResponse> call, Response<PojoGetAssessmentCategoriesResponse> response) {
                if (response.body() != null) {
                    preferenceUtils.saveAssessmentCategoriesList(response.body().data);
                    handleApiResponse();
                }
            }

            @Override
            public void onFailure(Call<PojoGetAssessmentCategoriesResponse> call, Throwable t) {
                // Log error here since request failed
                int a = 5;
                openNoConnectionDialog(getString(R.string.assessment_categories));
            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetAssessmentCategoriesApi(user_id);
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
        allCategoriesTree = new ArrayList<>();
        allCategoriesTree.clear();
        allCategoriesTree.addAll(preferenceUtils.getAssessmentCategoriesList());
        root = new PojoAssessmentCategory("0", "Home",
                "-1", "1", allCategoriesTree);
        root.setParent(null);
        totalCategoryCount = allCategoriesTree.size();
        setParentRefRecursively(root);
        allCategoriesArray = new PojoAssessmentCategory[totalCategoryCount];
        allCategoryCounter = 0;
        addAllCategoriesToArray(root);

        int a = 5;
        int b = 6;

        updateCategoryList(root.categoryId);

        Type type = new TypeToken<ArrayList<PojoAssessmentCategory>>() {
        }.getType();
        Gson gson = new Gson();
        String dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            List<PojoAssessmentCategory> tempList = gson.fromJson(dataStr, type);
            for (int i = 0; i < tempList.size(); i++) {
                PojoAssessmentCategory item = findItemUsingId(root,
                        tempList.get(i).categoryId);
                item.isCbChecked = true;
                setAllChildCheckedStatus(item, true);
            }
            updateSelectedCategoryList(true);
        }

        pb_loading_content.setVisibility(View.GONE);
        ll_loading_wrapper.setVisibility(View.GONE);
        rl_content_wrapper.setVisibility(View.VISIBLE);
    }


    private void addAllCategoriesToArray(PojoAssessmentCategory root) {
        if (root != null) {
            allCategoriesArray[allCategoryCounter++] = root;
        }

        for (int i = 0; i < root.child.size(); i++) {
            addAllCategoriesToArray(root.child.get(i));
        }
    }

    public PojoAssessmentCategory findItemUsingId(PojoAssessmentCategory root, String id) {

        if (root.categoryId.equals(id)) {
            return root;
        } else {
            PojoAssessmentCategory target = null;
            for (int i = 0; i < root.child.size(); i++) {
                PojoAssessmentCategory temp = findItemUsingId(root.child.get(i), id);
                if (temp != null) {
                    target = temp;
                }
            }
            return target;
        }
    }

    public void setParentRefRecursively(PojoAssessmentCategory root) {
        if (root.child.isEmpty()) {
            return;
        }
        for (int i = 0; i < root.child.size(); i++) {
            totalCategoryCount++;
            root.child.get(i).setParent(root);
            setParentRefRecursively(root.child.get(i));
        }
    }

    public void chooseShownCategories(PojoAssessmentCategory root) {
        if (root != null) {
            if (root.isCbChecked && !root.categoryId.equals("0")) {
                shownCategoryList.add(root);
            } else {
                for (int i = 0; i < root.child.size(); i++) {
                    chooseShownCategories(root.child.get(i));
                }
            }
        }
    }

    public void setAllChildCheckedStatus(PojoAssessmentCategory node, boolean status) {
        if (node != null && node.child != null) {
            for (int i = 0; i < node.child.size(); i++) {
                node.child.get(i).isCbChecked = status;
                setAllChildCheckedStatus(node.child.get(i), status);
            }
        }
    }

    @Override
    public void onAssessmentCategorySearchResultItemClick(int position, PojoAssessmentCategory
            PojoAssessmentCategory) {
        PojoAssessmentCategory currentItem = findItemUsingId(root, PojoAssessmentCategory.categoryId);
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
        updateCategoryAndHierarchyList(PojoAssessmentCategory.parentId);
        categorySearchResultList.clear();
        categorySearchResultAdapter.notifyDataSetChanged();
        commonFunctions.closeProgressDialog();
    }

    private void updateCategoryAndHierarchyList(String categoryId) {
        updateCategoryList(categoryId);
        updateHierarchyList(categoryId);
        updateSelectedCategoryList(true);
    }

    @Override
    public void onAssessmentCategoryListItemClick(int position, String type, Boolean cbChecked,
                                                  PojoAssessmentCategory PojoAssessmentCategory, String listType) {
        if (type.equals("iv")) {
            commonFunctions.openProgressDialog();
            updateCategoryAndHierarchyList(PojoAssessmentCategory.categoryId);
            commonFunctions.closeProgressDialog();
           /* assessmentCategoryHierarchyList.add(receivedInterestList.get(position));
            newHierarchyListAdapter.notifyDataSetChanged();*/
        }

        //if the click event is from checkbox
        if (type.equals("cb")) {
            PojoAssessmentCategory currentItem = findItemUsingId(root,
                    PojoAssessmentCategory.categoryId);
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
            updateSelectedCategoryList(true);
        }
    }


    @Override
    public void onHierarchyListMethodCallback(int position) {
        updateCategoryAndHierarchyList(assessmentCategoryHierarchyList.get(position).categoryId);

    }

    @Override
    public void onTaggedAssessmentCategoryItemClick(int position, PojoAssessmentCategory
            PojoAssessmentCategory) {

        shownCategoryList.remove(position);
        shownCategoryListAdapter.notifyDataSetChanged();

        PojoAssessmentCategory currentItem = findItemUsingId(root,
                PojoAssessmentCategory.categoryId);

        currentItem.isCbChecked = false;
        setAllChildCheckedStatus(currentItem, false);

        while (currentItem.parentRef != null && currentItem.parentRef.isCbChecked) {
            currentItem.parentRef.isCbChecked = false;
            currentItem = currentItem.parentRef;
        }

        currentCategoryListAdapter.notifyDataSetChanged();


    }


    public void updateCategoryList(String id) {
        currentListParent = findItemUsingId(root, id);
        if (currentListParent != null) {
            currentCategoryList.clear();
            currentCategoryList.addAll(currentListParent.child);
            currentCategoryListAdapter.notifyDataSetChanged();
        }
    }

    public void updateHierarchyList(String id) {
        commonFunctions.openProgressDialog();
        List<PojoAssessmentCategory> tempList = new ArrayList<>();
        PojoAssessmentCategory currentItem = findItemUsingId(root, id);

        tempList.add(currentItem);
        while (currentItem.parentRef != null) {
            tempList.add(currentItem.parentRef);
            currentItem = currentItem.parentRef;
        }

        assessmentCategoryHierarchyList.clear();
        Collections.reverse(tempList);
        assessmentCategoryHierarchyList.addAll(tempList);
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
        if (assessmentCategoryHierarchyList.size() > 1) {
            PojoAssessmentCategory currentItem = findItemUsingId(root,
                    assessmentCategoryHierarchyList.get(assessmentCategoryHierarchyList.size() - 1).categoryId);
            updateCategoryAndHierarchyList(currentItem.parentRef.categoryId);

        } else {
            super.onBackPressed();
        }
    }


}
