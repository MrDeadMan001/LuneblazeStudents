package com.avadna.luneblaze.fragments.interest;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.ArticleCommentListActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.adapters.InterestRelatedArticlesAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponseData;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestDetailsResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoRelatedArticles;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class InterestRelatedArticleListFragment extends Fragment implements
        InterestRelatedArticlesAdapter.InterestRelatedArticlesAdapterCallback {

    LinearLayout ll_parent;

    String user_id = "";
    String username;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_item_list;
    InterestRelatedArticlesAdapter interestRelatedArticlesAdapter;
    PojoInterestDetailsResponse pojoInterestDetailsResponse;
    List<PojoRelatedArticles> articlesList;
    LinearLayoutManager articleListLayoutManager;
    boolean isGetArticleApiCalled = false;

    TextView tv_filter;
    Dialog filterDialog;

    TextView tv_no_articles;

    static final String MY_ROLES_ANY = "any";
    static final String MY_ROLES_MY_ARTICLE = "My Articles";


    String newMyRole = MY_ROLES_ANY;
    String oldMyRole = MY_ROLES_ANY;
    int offset = 0;

    RelativeLayout rl_comment_bar;
    EditText et_comment;
    TextView bt_send;
    Dialog commentListDialog;
    EditText list_dialog_et_comment;
    List<PojoArticleCommentsResponseData> commentList;
    boolean isGetCommentApiCalled = false;
    LinearLayoutManager commentListLayoutManager;
    ProgressBar pb_loading_comments;
    TextView tv_no_more_questions;
    TextView tv_dialog_like_number, tv_dialog_comment_number;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_user_activity_article, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getContext());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        if (getArguments() != null)
            pojoInterestDetailsResponse = new Gson().fromJson(getArguments().getString("params"),
                    PojoInterestDetailsResponse.class);
        initViews(baseLayout);
        if (pojoInterestDetailsResponse != null) {
            hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                    pojoInterestDetailsResponse.data.interestData.interestId, "article", oldMyRole, offset);
        }
        return baseLayout;
    }


    private void initViews(View baseLayout) {

        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                        pojoInterestDetailsResponse.data.interestData.interestId, "article",
                        oldMyRole, offset);
            }
        });

        initArticleList(baseLayout);
        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        ll_parent.setVisibility(View.GONE);
        rl_comment_bar = (RelativeLayout) baseLayout.findViewById(R.id.rl_comment_bar);
        et_comment = (EditText) baseLayout.findViewById(R.id.et_comment);
        bt_send = (TextView) baseLayout.findViewById(R.id.bt_send);
        tv_no_more_questions = (TextView) baseLayout.findViewById(R.id.tv_no_more_questions);
        tv_filter = (TextView) baseLayout.findViewById(R.id.tv_filter);
        tv_filter.setVisibility(View.GONE);
        tv_no_articles = (TextView) baseLayout.findViewById(R.id.tv_no_articles);
        tv_no_articles.setVisibility(View.GONE);

        tv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilterDialog();
            }
        });
    }

    private void initArticleList(View baseLayout) {
        articlesList = new ArrayList<>();
        rv_item_list = (RecyclerView) baseLayout.findViewById(R.id.rv_item_list);
        interestRelatedArticlesAdapter = new InterestRelatedArticlesAdapter(this, articlesList);
        articleListLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_item_list.setLayoutManager(articleListLayoutManager);
        rv_item_list.setAdapter(interestRelatedArticlesAdapter);
        articlesList.clear();

        rv_item_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    ((InterestActivity) getActivity()).hideFilter();
                    rl_comment_bar.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager)
                            getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rv_item_list.getWindowToken(), 0);

                    if (!isGetArticleApiCalled && articleListLayoutManager
                            .findLastVisibleItemPosition() > articlesList.size() - 3) {
                        hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                                pojoInterestDetailsResponse.data.interestData.interestId,
                                "article", oldMyRole, ++offset);
                    }
                }

                if (dy < -5) {
                    ((InterestActivity) getActivity()).showFilter();
                }
            }
        });
    }

    private void hitGetInterestDetailsApi(String user_id, String interest_id, String type, String role, final int offset) {
        Call<PojoInterestDetailsResponse> call = apiService.getInterestArticles(user_id, interest_id,
                type, role, String.valueOf(offset));
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isGetArticleApiCalled) {
            isGetArticleApiCalled = true;
            interestRelatedArticlesAdapter.setShowProgressBar(true);

            call.enqueue(new Callback<PojoInterestDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoInterestDetailsResponse> call, Response<PojoInterestDetailsResponse> response) {
                    String message = "";
                    if (response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        if (response.body().data.articles.isEmpty()) {
                            interestRelatedArticlesAdapter.setMoreAvailable(false);
                        }
                        if (offset == 0) {
                            articlesList.clear();
                      /*      if(response.body().data.articles.isEmpty()){
                                tv_no_articles.setVisibility(View.VISIBLE);
                            }
                            else {
                                tv_no_articles.setVisibility(View.GONE);
                            }*/
                        }
                        articlesList.addAll(response.body().data.articles);
                        interestRelatedArticlesAdapter.notifyDataSetChanged();
                        ll_parent.setVisibility(View.VISIBLE);
                    }
                    interestRelatedArticlesAdapter.setShowProgressBar(false);
                    isGetArticleApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoInterestDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    //ll_parent.setVisibility(View.VISIBLE);
                    interestRelatedArticlesAdapter.setShowProgressBar(false);
                    isGetArticleApiCalled = false;
                    if (articlesList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    public void openFilterDialog() {
        filterDialog = new MyCustomThemeDialog(getActivity());
        filterDialog.setContentView(R.layout.interest_question_list_filter_dialog);
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        LinearLayout ll_relevance_wrapper = (LinearLayout) filterDialog.findViewById(R.id.ll_relevance_wrapper);
        ll_relevance_wrapper.setVisibility(View.GONE);

        LinearLayout ll_interest_data_wrapper = (LinearLayout) filterDialog.findViewById(R.id.ll_interest_data_wrapper);
        ll_interest_data_wrapper.setVisibility(View.GONE);

        final TextView tv_done, tv_cancel, tv_my_roles;
        tv_done = (TextView) filterDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) filterDialog.findViewById(R.id.tv_cancel);
        tv_my_roles = (TextView) filterDialog.findViewById(R.id.tv_my_roles);

        switch (oldMyRole) {
            case MY_ROLES_ANY:
                tv_my_roles.setText(R.string.any);
                break;

            case MY_ROLES_MY_ARTICLE:
                tv_my_roles.setText(R.string.my_articles);
                break;
        }


        final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {


                    case R.id.role_any:
                        newMyRole = MY_ROLES_ANY;
                        tv_my_roles.setText(R.string.any);
                        break;

                    case R.id.role_my_article:
                        newMyRole = MY_ROLES_MY_ARTICLE;
                        tv_my_roles.setText(R.string.my_articles);
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
                        oldMyRole = newMyRole;
                        offset = 0;
                        articlesList.clear();
                        interestRelatedArticlesAdapter.setMoreAvailable(true);
                        interestRelatedArticlesAdapter.setShowProgressBar(true);
                        hitGetInterestDetailsApi(preferenceUtils.get_user_id(),
                                pojoInterestDetailsResponse.data.interestData.interestId,
                                "article", oldMyRole, offset);
                        interestRelatedArticlesAdapter.notifyDataSetChanged();
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_cancel:
                        newMyRole = oldMyRole;
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_my_roles:
                        PopupMenu roleMenu = new PopupMenu(getActivity(), tv_my_roles);
                        MenuInflater rolesInflater = roleMenu.getMenuInflater();
                        rolesInflater.inflate(R.menu.interest_article_filter_role_menu, roleMenu.getMenu());
                        roleMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        roleMenu.show();
                        break;
                }
            }
        };

        tv_done.setOnClickListener(onClickListener);
        tv_cancel.setOnClickListener(onClickListener);
        tv_my_roles.setOnClickListener(onClickListener);
        filterDialog.show();
    }


   /* private void openCommentListDialog(final String articleId, final int position) {
        commentListDialog = new MyCustomThemeDialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        commentListDialog.setContentView(R.layout.add_comment_on_list_dialog);
        commentListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        pb_loading_comments = (ProgressBar) commentListDialog.findViewById(R.id.pb_loading_comments);

        list_dialog_et_comment = (EditText) commentListDialog.findViewById(R.id.et_comment);
        TextView bt_send = (TextView) commentListDialog.findViewById(R.id.bt_send);
        tv_dialog_like_number = (TextView) commentListDialog.findViewById(R.id.tv_dialog_like_number);
        tv_dialog_like_number.setText(articlesList.get(position).likes + " " + getString(R.string.likes));
        tv_dialog_comment_number = (TextView) commentListDialog.findViewById(R.id.tv_dialog_comment_number);
        commentList = new ArrayList<>();
        rv_comments_list = (RecyclerView) commentListDialog.findViewById(R.id.rv_comments_list);
        commentListLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rv_comments_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        articleCommentsAdapter = new ArticleCommentsAdapter(getActivity(), commentList);
        rv_comments_list.setAdapter(articleCommentsAdapter);

        rv_comments_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (commentListLayoutManager.findLastVisibleItemPosition() > commentList.size() - 3) {
                        hitGetArticleCommentsApi(user_id, articleId, "next", getLatestCommentId());
                    }
                } else if (dy < -4) {
                    if (commentListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetArticleCommentsApi(user_id, articleId, "prev", getOldestCommentId());
                    }
                }
            }
        });


        hitGetArticleCommentsApi(user_id, articleId, "next", "0");

        list_dialog_et_comment.postDelayed(new Runnable() {
            @Override
            public void run() {

                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rv_comments_list.getWindowToken(), 0);

            }
        }, 100);

        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!list_dialog_et_comment.getText().toString().trim().isEmpty()) {
                    hitAddCommentApi(user_id, articleId, list_dialog_et_comment.getText().toString(), "dialog", position);
                    PojoArticleCommentsResponseData tempCommment = new PojoArticleCommentsResponseData(list_dialog_et_comment.getText().toString(),
                            user_id, getString(R.string.just_now));
                    tempCommment.user = new PojoUserData();
                    tempCommment.user.userId = user_id;
                    tempCommment.user.userFullname = username;
                    tempCommment.user.userPicture = "";
                    tempCommment.modifiedOn = getString(R.string.just_now);
                    commentList.add(tempCommment);
                    articleCommentsAdapter.notifyDataSetChanged();
                    list_dialog_et_comment.getText().clear();
                }
            }
        });

        commentListDialog.show();

        rv_comments_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rv_comments_list.getWindowVisibleDisplayFrame(r);
                int screenHeight = rv_comments_list.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;


                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    if (!commentList.isEmpty()) {
                        rv_comments_list.smoothScrollToPosition(rv_comments_list.getAdapter().getItemCount() - 1);

                    }

                } else {
                    // keyboard is closed
                }
            }
        });
     *//*   Window window = commentListDialog.getWindow();
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);*//*
    }*/


  /*  private void openAddCommentDialog(final int position, final String articleId) {
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_comment.getText().toString().trim().isEmpty()) {
                    hitAddCommentApi(user_id, articleId, et_comment.getText().toString(), "home", position);
                    et_comment.getText().clear();
                }

            }
        });
        rl_comment_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }

    }*/


   /* private void hitGetArticleCommentsApi(String user_id, String article_id, final String order,
                                          final String comment_id) {
        Call<PojoArticleCommentsResponse> call = apiService.getArticleComments(user_id, article_id,
                order, comment_id);
        if (!isGetCommentApiCalled) {
            call.enqueue(new Callback<PojoArticleCommentsResponse>() {
                @Override
                public void onResponse(Call<PojoArticleCommentsResponse> call, Response<PojoArticleCommentsResponse> response) {
                    pb_loading_comments.setVisibility(View.GONE);
                    String message = "";
                    if (response != null) {
                        message = response.body().message;
                        if (response.body() != null) {

                            if (comment_id.equals("0")) {
                                commentList.clear();
                            }

                            if (order.equals("next")) {
                                if (!comment_id.equals("0")) {
                                    response.body().data.remove(0);
                                }
                                commentList.addAll(response.body().data);
                            } else if (order.equals("prev")) {
                                // Collections.reverse(response.body().data);
                        *//*    response.body().data.addAll(commentList);
                            commentList.clear();*//*
                                if (!comment_id.equals("0")) {
                                    response.body().data.remove(response.body().data.size() - 1);
                                }
                                commentList.addAll(0, response.body().data);
                            }
                            tv_dialog_comment_number.setText(commentList.size() + " " + getString(R.string.comments));
                            articleCommentsAdapter.notifyDataSetChanged();
                            rv_comments_list.scrollToPosition(commentList.size() - 1);
                        } else {
                            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        }

                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }
                    isGetCommentApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoArticleCommentsResponse> call, Throwable t) {
                    pb_loading_comments.setVisibility(View.GONE);

                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isGetCommentApiCalled = false;

                }
            });
        }


    }*/


    /*private void hitAddCommentApi(final String user_id, final String article_id, final String message, final String type, final int position) {
        Call<PojoNoDataResponse> call = apiService.addCommentOnArticle(user_id, article_id, message);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        if (type.equals("home")) {
                            openCommentListDialog(article_id, position);
                        } else {
                            hitGetArticleCommentsApi(user_id, article_id, "prev", "0");
                        }

                    } else {
                        if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                } else {
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.comment_not_added),
                        Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                if (type.equals("home")) {
                    et_comment.setText(message);
                } else {
                    list_dialog_et_comment.setText(message);
                }
            }
        });

    }*/

    private String getLatestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(commentList.size() - 1).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    private String getOldestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(0).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    @Override
    public void interestRelatedArticlesMethodClick(int position, String articleId, String type) {
        if (type.equals(AppKeys.ADD_COMMENT)) {
            Intent articleCommentIntent = new Intent(getActivity(), ArticleCommentListActivity.class);
            articleCommentIntent.putExtra("id", articleId);
            articleCommentIntent.putExtra(AppKeys.TARGET, AppKeys.COMMENT);
            startActivity(articleCommentIntent);
            //openAddCommentDialog(position, articleId);
        } else if (type.equals(AppKeys.SHARE_ARTICLE)) {
            openShareSessionOnProfileDialog(position);
        } else if (type.equals(AppKeys.LIKE)) {
            if (articlesList.get(position).likes.isEmpty()) {
                articlesList.get(position).likes = "0";
            }
            int numLikes = Integer.parseInt(articlesList.get(position).likes);
            if (articlesList.get(position).iLike) {
                hitArticleReactionApi(user_id, articlesList.get(position).articlesId, "unlike_article");
                numLikes--;
            } else {
                hitArticleReactionApi(user_id, articlesList.get(position).articlesId, "like_article");
                numLikes++;
            }
            articlesList.get(position).likes = "" + numLikes;
            articlesList.get(position).iLike = !articlesList.get(position).iLike;
            interestRelatedArticlesAdapter.notifyDataSetChanged();
        }


    }

    private void hitArticleReactionApi(String user_id, String article_id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.articleReaction(user_id, article_id, reaction);
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    // if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                //   if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });

    }


    private void openShareSessionOnProfileDialog(final int position) {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(getActivity());
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);
        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);
        rl_item_holder = (RelativeLayout) shareDialog.findViewById(R.id.rl_item_holder);

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);
        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.share_article_item, rl_item_holder, true);

        ImageView iv_writer_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
        TextView tv_writer_name = (TextView) view.findViewById(R.id.tv_writer_name);
        TextView tv_writer_designation = (TextView) view.findViewById(R.id.tv_writer_designation);
        TextView tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);

        ImageView iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
        TextView tv_article_name = (TextView) view.findViewById(R.id.tv_article_name);

        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .load(articlesList.get(position).createdBy.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_writer_dp);

        tv_writer_name.setText(articlesList.get(position).createdBy.userFullname);
        tv_writer_designation.setText(articlesList.get(position).createdBy.userWorkTitle);

        if (articlesList.get(position).imgDimensions != null && !articlesList.get(position).imgDimensions.isEmpty()) {
            String imgDimen = articlesList.get(position).imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                    .load(articlesList.get(position).coverPhoto)
                    .apply(new RequestOptions().override(width, height))
                    .into(iv_cover_photo);
        } else {
            if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                    .load(articlesList.get(position).coverPhoto)
                    .apply(new RequestOptions().override(400, 300))
                    .into(iv_cover_photo);
        }

        tv_article_name.setText(articlesList.get(position).title);
        tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(articlesList.get(position).addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, articlesList.get(position).articlesId, "share_article", et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
    }


    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    try {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                try {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
