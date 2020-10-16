package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PollPostAdapter extends RecyclerView.Adapter<PollPostAdapter.MyViewHolder> {
    Activity activity;
    boolean voteCasted = false;
    boolean canSeeVotes = false;

    List<PojoNewsFeedPollOption> optionList;
    ApiInterface apiService;
    String user_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    int totalVotes;
    String type="";
    PollPostAdapterCallback pollPostAdapterCallback;
    int  newsFeedItemPosition=0;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, graph_bar, tv_black_button, tv_vote_percent, tv_checked_bar;
        RelativeLayout rl_venue_wrapper;
        TextView tv_option_name;


        MyViewHolder(View view) {
            super(view);
            tv_option_name = (TextView) view.findViewById(R.id.tv_option_name);
            tv_black_button = (TextView) view.findViewById(R.id.tv_black_button);
            tv_vote_percent = (TextView) view.findViewById(R.id.tv_vote_percent);
            tv_checked_bar = (TextView) view.findViewById(R.id.tv_checked_bar);
            graph_bar = (TextView) view.findViewById(R.id.graph_bar);
            rl_venue_wrapper = (RelativeLayout) view.findViewById(R.id.rl_venue_wrapper);
        }
    }


    public PollPostAdapter(Activity activity, List<PojoNewsFeedPollOption> optionList,String type) {
        try {
            this.pollPostAdapterCallback = ((PollPostAdapterCallback) activity);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement PollPostAdapterCallback.");
        }
        this.activity = activity;
        this.optionList = optionList;
        this.type=type;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public PollPostAdapter(Fragment fragment, List<PojoNewsFeedPollOption> optionList, String type) {
        try {
            this.pollPostAdapterCallback = ((PollPostAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement PollPostAdapterCallback.");
        }
        this.activity = fragment.getActivity();
        this.optionList = optionList;
        this.type=type;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(activity);
        commonFunctions=new CommonFunctions(activity);
        user_id = preferenceUtils.get_user_id();
    }

    public void setNewsFeedItemPosition(int newsFeedItemPosition){
        this.newsFeedItemPosition=newsFeedItemPosition;
    }

    public void setTotalVotes(String total) {
        this.totalVotes = Integer.parseInt(total);
        notifyDataSetChanged();
    }

    public String getTotalVotes() {
       return String.valueOf(totalVotes);
    }

    public void setVoteCasted(boolean casted) {
        voteCasted = casted;
        notifyDataSetChanged();
    }

    public void setCanSeeVotes(boolean canSee) {
        canSeeVotes = canSee;
        notifyDataSetChanged();
    }

    @Override
    public PollPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poll_post_options_list_item, parent, false);

        return new PollPostAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PollPostAdapter.MyViewHolder holder, final int position) {
        final PojoNewsFeedPollOption currentItem = optionList.get(position);

      /*  holder.rl_venue_wrapper.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                holder.rl_venue_wrapper.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            }
        });
*/

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        holder.tv_black_button.measure(size.x, size.y);
        int maxWidth = size.x;

        Resources r = activity.getResources();
        int gap=0;
        if(type.equals("normal")){
            gap = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
        }
        else {
            gap = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, r.getDisplayMetrics());
        }
        maxWidth=maxWidth-(2*gap);
        //holder.rl_venue_wrapper.getWidth();
        holder.tv_option_name.setText(Html.fromHtml(currentItem.text));
        if (canSeeVotes) {
            holder.tv_vote_percent.setVisibility(View.VISIBLE);
            //holder.tv_black_button.setVisibility(View.GONE);
            holder.graph_bar.setVisibility(View.VISIBLE);

            int currWidth;
            float currPercent;
            if(totalVotes==0){
                currWidth = 0;
                currPercent=0;
            }
            else {
                currWidth = (int) ((1f * currentItem.votes / totalVotes) * maxWidth);
                currPercent=(100f*currentItem.votes/totalVotes);
            }

          //  Log.d("vote_percent",currPercent+"");
            DecimalFormat form = new DecimalFormat("0.00");
            holder.tv_vote_percent.setText("(" + form.format(currPercent)+ "%)");
            ViewGroup.LayoutParams layoutParams = holder.graph_bar.getLayoutParams();
            layoutParams.width = currWidth;
            holder.graph_bar.setLayoutParams(layoutParams);
        } else {
            holder.tv_vote_percent.setVisibility(View.GONE);
            holder.tv_black_button.setVisibility(View.VISIBLE);
            holder.graph_bar.setVisibility(View.GONE);
        }

        if (currentItem.checked) {
            holder.tv_checked_bar.setVisibility(View.VISIBLE);
        } else {
            holder.tv_checked_bar.setVisibility(View.GONE);

        }

        holder.rl_venue_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!voteCasted) {
                    totalVotes++;
                    hitCastVoteApi(user_id, "add_vote", currentItem.optionId);
                    notifyDataSetChanged();
                    pollPostAdapterCallback.pollPostAdapterMethod(position,totalVotes,currentItem,newsFeedItemPosition);

                }
            }
        });

    }

    public static interface PollPostAdapterCallback {
        void pollPostAdapterMethod(int position,int totalVotes, PojoNewsFeedPollOption pojoNewsFeedPollOption,int newsFeedItemPosition);
    }


    private void hitCastVoteApi(String user_id, String reaction, String id) {
        Call<PojoNoDataResponse> call = apiService.castVoteOnPollPost(user_id, reaction, id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(activity, message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(activity, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public int getItemCount() {
        return optionList.size();
    }


}