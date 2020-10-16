package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;

/**
 * Created by Sunny on 14-01-2018.
 */

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    int listSize;
    Activity activity;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        String USER = "user";
        String OTHER = "other";
        String type = USER;
       /* List<String> user_options_list;
        List<String> other_options_list;
        ArrayAdapter<String> spinnerAdapter;*/
        ImageView iv_more_options;
        //Spinner sp_more_options;
        TextView tv_activity_performed;
        RelativeLayout rl_top_bar_wrapper;
        TextView tv_follow_button, tv_answer_button, tv_upvote_button;
        FrameLayout fl_follow_button_wrapper, fl_answer_button_wrapper, fl_upvote_button_wrapper;
        PopupMenu popupMenu;



        MyViewHolder(View view) {
            super(view);
            tv_activity_performed = (TextView) view.findViewById(R.id.tv_activity_performed);
          //  sp_more_options = (Spinner) view.findViewById(R.id.sp_more_options);
            rl_top_bar_wrapper = (RelativeLayout) view.findViewById(R.id.rl_top_bar_wrapper);
            tv_follow_button = (TextView) view.findViewById(R.id.tv_follow_button);
            tv_answer_button = (TextView) view.findViewById(R.id.tv_answer_button);
            tv_upvote_button = (TextView) view.findViewById(R.id.tv_upvote_button);
            fl_follow_button_wrapper = (FrameLayout) view.findViewById(R.id.fl_follow_button_wrapper);
            fl_answer_button_wrapper = (FrameLayout) view.findViewById(R.id.fl_answer_button_wrapper);
            fl_upvote_button_wrapper = (FrameLayout) view.findViewById(R.id.fl_upvote_button_wrapper);
            iv_more_options = (ImageView) view.findViewById(R.id.iv_more_options);
            iv_more_options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                }
            });
            popupMenu = new PopupMenu(activity,iv_more_options);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.question_list_item_side_options, popupMenu.getMenu());
         /*   user_options_list = new ArrayList<String>();
            user_options_list.add("Edit");
            user_options_list.add("Delete");
            user_options_list.add("Share");
            user_options_list.add("Request answer");

            other_options_list = new ArrayList<>();
            other_options_list.add("Report");
            other_options_list.add("Follow");
            other_options_list.add("Share");
            other_options_list.add("Request answer");*/

        }
    }


    public QuestionListAdapter(int listSize, Activity activity) {
        this.listSize = listSize;
        this.activity = activity;

        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public QuestionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_item, parent, false);

        return new QuestionListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final QuestionListAdapter.MyViewHolder holder, final int position) {
        holder.rl_top_bar_wrapper.setVisibility(View.GONE);

        holder.tv_activity_performed.setText(Html.fromHtml("<font color=\"#0b6c73\">" +
                " <b>Username</b> </font> <font color=\"#4e4e4e\"> answered a question in</font>" +
                "<font color=\"#0b6c73\"> <b>Session Name</b></font>"));

        holder.popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){

                }
                return false;
            }
        });


      /*  if (holder.orientation.equals(holder.USER)) {
            holder.spinnerAdapter = new ArrayAdapter<String>(activity, R.layout.invisible_spinner, holder.user_options_list);

        } else if (holder.orientation.equals(holder.OTHER)) {
            holder.spinnerAdapter = new ArrayAdapter<String>(activity, R.layout.invisible_spinner, holder.other_options_list);

        }
        holder.spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        holder.sp_more_options.setAdapter(holder.spinnerAdapter);

        holder.sp_more_options.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        holder.iv_more_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.sp_more_options.performClick();

                  *//* final Dialog dialog = new MyCustomThemeDialog(activity);
                   dialog.setContentView(R.layout.qna_more_option_dialog);
                   ImageView iv_close=(ImageView)dialog.findViewById(R.id.iv_close);
                   iv_close.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           dialog.dismiss();
                       }
                   });
                   dialog.show();*//*
            }
        });*/




     /*   mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.tv_follow_button:
                        holder.fl_follow_button_wrapper.callOnClick();
                        break;
                    case R.id.tv_answer_button:
                        holder.fl_answer_button_wrapper.callOnClick();
                        break;
                    case R.id.tv_follow_button:
                        holder.fl_upvote_button_wrapper.callOnClick();
                        break;
                    case R.id.fl_follow_button_wrapper:
                        commonFunctions.setToastMessage(activity,"clicked",Toast.LENGTH_SHORT);
                        break;
                    case R.id.fl_answer_button_wrapper:

                        break;
                    case R.id.fl_upvote_button_wrapper:

                        break;
                }
            }
        };

        holder.tv_follow_button.setOnClickListener(mOnClickListener);
        holder.tv_answer_button.setOnClickListener(mOnClickListener);
        holder.tv_follow_button.setOnClickListener(mOnClickListener);
        holder.fl_follow_button_wrapper.setOnClickListener(mOnClickListener);
        holder.fl_answer_button_wrapper.setOnClickListener(mOnClickListener);
        holder.fl_upvote_button_wrapper.setOnClickListener(mOnClickListener);*/
/*
        holder.tv_follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click info", "clicked tv pos " + position);
                commonFunctions.setToastMessage(activity, "clicked", Toast.LENGTH_SHORT);
                holder.fl_follow_button_wrapper.callOnClick();
            }

        });

        holder.fl_follow_button_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("click info", "clicked fl pos " + position);
                commonFunctions.setToastMessage(activity, "clicked", Toast.LENGTH_SHORT);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return listSize;
        //  return hierarchyList.size();
    }
}

