package com.avadna.luneblaze.adapters.poll;

import android.app.Dialog;

import com.avadna.luneblaze.adapters.OldUpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.graphics.Point;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.sessions.SessionPollFragment;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoVoteDate;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoVoting;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionDatePollAdapter extends RecyclerView.Adapter<SessionDatePollAdapter.MyViewHolder> {
    PojoVoting voteOption;
    //  Activity activity;
    String user_id;
    String session_id;
    String venue;
    ApiInterface apiService;
    //  int currDay, currMonth, currYear, chosenDay = 0, chosenMonth = 0, chosenYear = 0;

    int venueNumber;
    SessionPollFragment fragment;
    SessionDatePollListAdapterCallback sessionDatePollListAdapterCallback;

    List<PojoUserData> upvotersList;
    OldUpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;

    ProgressBar pb_loading_users;


    boolean allowSuggestion;

    CommonFunctions commonFunctions;

    String finalDate;

    public void setAllowSuggestion(boolean status) {
        allowSuggestion = status;
    }

    public SessionDatePollAdapter(SessionPollFragment fragment, PojoVoting voteOption, String user_id,
                                  String sessionId, int position, boolean allowSuggestion) {

        try {
            this.sessionDatePollListAdapterCallback = ((SessionDatePollListAdapterCallback) fragment);
        } catch (ClassCastException e) {
            throw new ClassCastException("fragment must implement sessionDatePollListAdapterCallback.");
        }

        this.voteOption = voteOption;
        this.fragment = fragment;
        this.user_id = user_id;
        this.session_id = sessionId;
        this.venue = venue;
        commonFunctions = new CommonFunctions(fragment.getActivity());
        this.allowSuggestion = allowSuggestion;
        venueNumber = position;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public void setFinalDate(String finalisedDate) {
        this.finalDate = finalisedDate;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_date, tv_date_bar, tv_bg_bar, tv_votes;
        TextView tv_checked_bar;
        ImageView iv_is_final;

        MyViewHolder(View view) {
            super(view);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_votes = (TextView) view.findViewById(R.id.tv_votes);
            tv_date_bar = (TextView) view.findViewById(R.id.tv_date_bar);
            tv_bg_bar = (TextView) view.findViewById(R.id.tv_bg_bar);
            tv_checked_bar = (TextView) view.findViewById(R.id.tv_checked_bar);
            iv_is_final = (ImageView) view.findViewById(R.id.iv_is_final);
        }
    }


    @Override
    public SessionDatePollAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_poll_list_date_item, parent, false);

        return new SessionDatePollAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SessionDatePollAdapter.MyViewHolder holder, final int position) {

        if (position < voteOption.dates.size()) {
            final PojoVoteDate currentItem = voteOption.dates.get(position);

            if (finalDate != null && finalDate.equals(currentItem.date)) {
                holder.iv_is_final.setVisibility(View.VISIBLE);
            } else {
                holder.iv_is_final.setVisibility(View.GONE);
            }

            if (currentItem.checked) {
                holder.tv_checked_bar.setVisibility(View.VISIBLE);
            } else {
                holder.tv_checked_bar.setVisibility(View.GONE);
            }
            holder.tv_date.setText(commonFunctions.parseDateToName(currentItem.date));
            holder.tv_votes.setText("(" + currentItem.votes + ")");

            int totalVotes = 0;
            for (int i = 0; i < voteOption.dates.size(); i++) {
                totalVotes += voteOption.dates.get(i).votes;
            }

            Display display = fragment.getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            holder.tv_date_bar.measure(size.x, size.y);
            int maxWidth = size.x / 2;
            ViewGroup.LayoutParams layoutParams = holder.tv_date_bar.getLayoutParams();
            if (totalVotes > 0) {
                layoutParams.width = (int) (1f * maxWidth * currentItem.votes / totalVotes);
            } else {
                layoutParams.width = 0;
            }
            holder.tv_date_bar.setLayoutParams(layoutParams);
            holder.tv_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commonFunctions.isGuestUser()) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        sessionDatePollListAdapterCallback.onSessionDatePollListMethodCallback(position,
                                venueNumber, "date_clicked");
                    }
                 /*   if(!voteOption.dates.get(position).checked){
                        voteOption.dates.get(position).votes++;
                        voteOption.dates.get(position).checked=true;

                        //   upvoteDateApi(user_id, session_id, currentItem.optionId,position);
                    }
                    else {
                        voteOption.dates.get(position).votes--;
                        voteOption.dates.get(position).checked=false;
                        Intent newVenueAdded = new Intent(Config.POLL_DATE_UNSELECTED);
                        newVenueAdded.putExtra("position",venueNumber);
                        LocalBroadcastManager.getInstance(fragment.getActivity()).sendBroadcast(newVenueAdded);
                    }
                    notifyDataSetChanged();*/
                }
            });

            holder.tv_votes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tv_title;
                    ImageView iv_back;
                    final Dialog upvoterListDialog;
                    upvoterListDialog = new MyCustomThemeDialog(fragment.getActivity());
                    upvoterListDialog.setContentView(R.layout.contact_list_dialog);
                    upvoterListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    //   upvoterListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    pb_loading_users = (ProgressBar) upvoterListDialog.findViewById(R.id.pb_loading_users);
                    tv_title = upvoterListDialog.findViewById(R.id.tv_title);
                    tv_title.setText(fragment.getString(R.string.voters));

                    iv_back = (ImageView) upvoterListDialog.findViewById(R.id.iv_back);
                    iv_back.setVisibility(View.GONE);
                    iv_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upvoterListDialog.dismiss();
                        }
                    });

                    upvoterListDialog.setTitle(fragment.getString(R.string.voters));
                    RecyclerView rv_list;
                    rv_list = (RecyclerView) upvoterListDialog.findViewById(R.id.rv_list);
                    //rv_list.setPadding(16,32,16,32);
                    rv_list.setLayoutManager(new LinearLayoutManager(fragment.getActivity(),
                            RecyclerView.VERTICAL, false));
                    upvotersList = new ArrayList<>();
                    upvotersAndAttendeesListAdapter = new OldUpvotersAndAttendeesListAdapter(fragment,
                            upvotersList);
                    rv_list.setAdapter(upvotersAndAttendeesListAdapter);
                    hitGetPollUpvoterListApi(user_id, currentItem.optionId, 0);
                    upvoterListDialog.show();
                }
            });

        } else if (position == voteOption.dates.size()) {
            ViewGroup.LayoutParams layoutParams = holder.tv_date_bar.getLayoutParams();
            layoutParams.width = 0;
            holder.tv_date_bar.setLayoutParams(layoutParams);

         /*   RelativeLayout.LayoutParams layoutParams2 =(RelativeLayout.LayoutParams) holder.tv_date.getLayoutParams();
            layoutParams2.addRule(Gravity.CENTER);
            holder.tv_date.setLayoutParams(layoutParams2);*/
            holder.tv_date.setText("+ " + fragment.getActivity().getString(R.string.add_new_date));
            holder.tv_votes.setVisibility(View.GONE);
            holder.tv_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commonFunctions.isGuestUser()) {
                        commonFunctions.showLoginRequestDialog();
                    } else {
                        sessionDatePollListAdapterCallback.onSessionDatePollListMethodCallback(position,
                                venueNumber, "add_new_date");
                    }
                    //  openDatePickerDialogBox();
                }
            });
            holder.iv_is_final.setVisibility(View.GONE);


        } else {
            ViewGroup.LayoutParams layoutParams = holder.tv_date_bar.getLayoutParams();
            holder.iv_is_final.setVisibility(View.GONE);

            layoutParams.width = 0;
            holder.tv_date_bar.setLayoutParams(layoutParams);
            holder.tv_date.setText(fragment.getActivity().getString(R.string.select_all));
            holder.tv_votes.setVisibility(View.GONE);
            holder.tv_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < voteOption.dates.size(); i++) {
                        if (!voteOption.dates.get(i).checked) {
                            voteOption.dates.get(i).votes++;
                            voteOption.dates.get(i).checked = true;
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

    }


    private void upvoteDateApi(String user_id, String session_id, String optionId, final int position) {
        Call<PojoNoDataResponse> call = apiService.votePoll(user_id, session_id, optionId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                commonFunctions.setToastMessage(fragment.getActivity(), "" + message,
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(fragment.getActivity(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (allowSuggestion) {
            return voteOption.dates.size() + 1;
        } else {
            return voteOption.dates.size();
        }
    }

  /*  private void openDatePickerDialogBox() {
        final Calendar cal = Calendar.getInstance();
        currYear = cal.get(Calendar.YEAR);
        currMonth = cal.get(Calendar.MONTH);
        currDay = cal.get(Calendar.DAY_OF_MONTH);

        chosenYear = currYear;
        chosenDay = currDay;



        DatePickerDialog mDatePickerDialog = new DatePickerDialog(fragment.getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                chosenDay = day;
                chosenMonth = month;
                chosenYear = year;
                String date="" + chosenYear + "-" + (chosenMonth + 1) + "-" + chosenDay;
               // hitSuggestDateApi(user_id,session_id,voteOption.venueName,date);
            }
        }, currYear, currDay, currMonth);
        mDatePickerDialog.show();
    }*/

    /*private void hitSuggestDateApi(String user_id, String session_id,String venue, String date) {
        Call<PojoNoDataResponse> call = apiService.suggestDate(user_id, session_id,venue, date);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                commonFunctions.setToastMessage(fragment.getActivity(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(fragment.getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
    }*/

    private void hitGetPollUpvoterListApi(String user_id, String option_id, int offSet) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.getPollUpvoterList(user_id, option_id, String.valueOf(offSet));
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {
                pb_loading_users.setVisibility(View.GONE);
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        upvotersList.clear();
                        upvotersList.addAll(response.body().data);
                        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(fragment.getActivity(), "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                } else {
                    commonFunctions.setToastMessage(fragment.getActivity(), "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                pb_loading_users.setVisibility(View.GONE);

                // Log error here since request failed
                commonFunctions.setToastMessage(fragment.getActivity(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    public static interface SessionDatePollListAdapterCallback {
        void onSessionDatePollListMethodCallback(int datePosition, int venuePosition, String type);
    }


}

