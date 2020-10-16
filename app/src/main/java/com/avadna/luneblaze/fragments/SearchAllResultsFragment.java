package com.avadna.luneblaze.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.RelatedInterestAdapter;
import com.avadna.luneblaze.adapters.SessionListAdapter;
import com.avadna.luneblaze.adapters.InterestRelatedArticlesAdapter;
import com.avadna.luneblaze.adapters.UserActivityPostAdapter;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.pojoInterest.PojoRelatedArticles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SearchAllResultsFragment extends Fragment implements
        InterestRelatedArticlesAdapter.InterestRelatedArticlesAdapterCallback{

    RecyclerView rv_post_list;
    UserActivityPostAdapter postListAdapter;

    RecyclerView rv_user_list;


    RecyclerView rv_session_list;
    SessionListAdapter sessionListAdapter;

    RecyclerView rv_article_list;
    InterestRelatedArticlesAdapter articleListAdapter;


    RecyclerView rv_interest_list;
    RelatedInterestAdapter interestlistAdapter;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics = new DisplayMetrics();
    int screenWidth;



    List<PojoGetInterestListResponseDataListItem> interestList=new ArrayList<>();
    List<String> listDataHeader;
    HashMap<String, List<String>> interestListHashMap;
    String[] intList={"sddd","ndjd"};

    RecyclerView rv_qna_list;




    public SearchAllResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout=inflater.inflate(R.layout.fragment_search_all_results, container, false);
        initLists(baseLayout);
        return baseLayout;
    }

    private void initLists(View baseLayout) {

        //post List
        rv_post_list=(RecyclerView)baseLayout.findViewById(R.id.rv_post_list);
        rv_post_list.setNestedScrollingEnabled(false);
        postListAdapter =new UserActivityPostAdapter(3,2);
        rv_post_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_post_list.setAdapter(postListAdapter);

        //session List
        rv_session_list=(RecyclerView)baseLayout.findViewById(R.id.rv_session_list);
        rv_session_list.setNestedScrollingEnabled(false);
        sessionListAdapter =new SessionListAdapter(3);
        rv_session_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_session_list.setAdapter(sessionListAdapter);

        //article List
        rv_article_list=(RecyclerView)baseLayout.findViewById(R.id.rv_article_list);
        rv_article_list.setNestedScrollingEnabled(false);
        articleListAdapter =new InterestRelatedArticlesAdapter(this,new ArrayList<PojoRelatedArticles>());
        rv_article_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_article_list.setAdapter(articleListAdapter);

        //interest list
        rv_interest_list=(RecyclerView)baseLayout.findViewById(R.id.rv_interest_list);
        rv_interest_list.setNestedScrollingEnabled(false);
        interestlistAdapter=new RelatedInterestAdapter(3);
        rv_interest_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        rv_interest_list.setAdapter(interestlistAdapter);
        // initInterestList(baseLayout);

       // initAddedInterestList(baseLayout);


    }

    @Override
    public void interestRelatedArticlesMethodClick(int position, String articleId,String type) {

    }

    /*private void initInterestList(View baseLayout) {
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth=displayMetrics.widthPixels;
        interestList=new ArrayList<>();
        interestList.add("dnsakjsanddnkjndadb");
        interestList.add("dnsnkjndadb");

        interestList.add("dndadb");

        interestList.add("dnsakjsadb");

        interestList.add("dnsakjsanddnkjndb");

        interestList.add("dadb");
        rv_interest_list=(RecyclerView)baseLayout.findViewById(R.id.rv_interest_list);
        tv_length_getter=(TextView)baseLayout.findViewById(R.id.tv_length_getter);

        GridLayoutManager manager=(new GridLayoutManager(getApplicationContext(),200));


        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = getActivity().getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                tv_length_getter.setText(interestList.get(position));
                tv_length_getter.measure(size.x, size.y);
                int textWidth=tv_length_getter.getMeasuredWidth();
                Log.e("text",""+tv_length_getter.getText().toString());
                Log.e("position=",""+position);
                Log.e("textwidth=",""+textWidth);

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, displayMetrics);

                float spanCount=(200*(textWidth+adjustMarginPx))/screenWidth;
                //  Log.e("textwidthpx=",""+textWidthPx);
                Log.e("Scrnwidth=",""+screenWidth);
                Log.e("spancount=",""+spanCount);
                if(spanCount>200){
                    spanCount=200;
                }
                return (int)spanCount;

             *//*   vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        tv_length_getter.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        int width  = tv_length_getter.getMeasuredWidth();
                        Log.e("text",""+tv_length_getter.getText().toString());
                        Log.e("width=",""+width);

                    }
                });*//*



            }
        });
        rv_interest_list.setLayoutManager(manager);
        rv_interest_list.setAdapter(new TaggedInterestAdapter(6,interestList));
    }*/

   /* private void initAddedInterestList(View baseLayout) {

        //used Expandable list view for showing selected interests
        el_selected_interests = (ExpandableListView) baseLayout.findViewById(R.id.el_selected_interests);

        listDataHeader = new ArrayList<String>();
        interestListHashMap = new HashMap<String, List<String>>();

        listDataHeader.add("Selected Interest");

        interestList= new ArrayList<String>();
        interestList.add("Interest is added here");

        interestListHashMap.put(listDataHeader.get(0), interestList); // Header, Child data
        expandableListAdapter = new CustomExpandableListAdapter(getActivity(), listDataHeader, interestListHashMap);

        // setting list adapter
        el_selected_interests.setAdapter(expandableListAdapter);

        // Listview Group click listener
        el_selected_interests.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // commonFunctions.setToastMessage(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                return false;
            }
        });

        // Listview Group expanded listener
        el_selected_interests.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                commonFunctions.setToastMessage(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });

        // Listview Group collasped listener
        el_selected_interests.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                commonFunctions.setToastMessage(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });

        // Listview on child click listener
        el_selected_interests.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                commonFunctions.setToastMessage(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + interestListHashMap.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

    }*/
}
