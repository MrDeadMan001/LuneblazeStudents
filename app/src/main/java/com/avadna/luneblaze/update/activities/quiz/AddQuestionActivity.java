package com.avadna.luneblaze.update.activities.quiz;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.adapters.RelatedArticleListAdapter;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponseData;
import com.avadna.luneblaze.pojo.quiz.CreateQuestionItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddQuestionActivity extends AppBaseActivity {

    String questionImagePath;
    private Button bt_add_question_image;
    private ImageView iv_question_image;
    private EditText et_question;
    private Button bt_add_answer;
    private Button bt_select_question_duration;
    private ImageButton ib_more;
    private  TextView tv_done;

    private RecyclerView rv_answer_list;
    private AnswerListAdapter answerListAdapter;
    private ArrayList<String> answers = new ArrayList<>();
    private final int PICK_IMAGE_PERMISSION = 169;

    private Dialog addAnswerDialog;
    View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        initViews();
        initClickListener();
        setClickListeners();
    }


    private void initViews() {
        initAnswerList();
        tv_done= (TextView) findViewById(R.id.tv_done);
        bt_add_question_image = (Button) findViewById(R.id.bt_add_question_image);
        iv_question_image = (ImageView) findViewById(R.id.iv_question_image);
        ib_more = (ImageButton) findViewById(R.id.ib_more);
        et_question = (EditText) findViewById(R.id.et_question);
        bt_add_answer = (Button) findViewById(R.id.bt_add_answer);
        bt_select_question_duration = (Button) findViewById(R.id.bt_select_question_duration);

        bt_add_question_image.setOnClickListener(onClickListener);
        bt_add_answer.setOnClickListener(onClickListener);
        bt_select_question_duration.setOnClickListener(onClickListener);
    }

    private void initAnswerList() {
        answerListAdapter = new AnswerListAdapter(this);
        rv_answer_list = (RecyclerView) findViewById(R.id.rv_answer_list);
        rv_answer_list.setLayoutManager(new LinearLayoutManager(this));
        rv_answer_list.setAdapter(answerListAdapter);
        rv_answer_list.setNestedScrollingEnabled(false);
        tv_done.setOnClickListener(onClickListener);
    }


    private void setClickListeners() {
        bt_add_answer.setOnClickListener(onClickListener);
        bt_add_question_image.setOnClickListener(onClickListener);
        bt_select_question_duration.setOnClickListener(onClickListener);
        iv_question_image.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_question_image:
                    case R.id.bt_add_question_image:
                        requestStoragePermission(PICK_IMAGE_PERMISSION);
                        break;

                    case R.id.bt_add_answer:
                        openAddAnswerDialog();
                        break;

                    case R.id.bt_select_duration:

                        break;

                    case R.id.tv_done:
                      //  String str=new Gson().toJson()

                        CreateQuestionItem questionItem=new CreateQuestionItem();
                        questionItem.answers=answers;
                        questionItem.imagePath=questionImagePath;
                        questionItem.correctAns=1;
                        questionItem.duration=20;
                        questionItem.text=et_question.getText().toString();

                        Type type = new TypeToken<CreateQuizActivity>() {
                        }.getType();

                        break;
                }
            }
        };
    }

    private void openAddAnswerDialog() {
        addAnswerDialog = new MyCustomThemeDialog(this, R.style.fullWidthDialogTheme);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Window window = addAnswerDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addAnswerDialog.setTitle(null);
        addAnswerDialog.setContentView(R.layout.quiz_answer_list_item);

        EditText et_ans = (EditText) addAnswerDialog.findViewById(R.id.et_ans);
        et_ans.setImeOptions(EditorInfo.IME_ACTION_DONE);
        et_ans.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                answers.add(et_ans.getText().toString());
                answerListAdapter.notifyItemInserted(answers.size()-1);
                if (et_ans.requestFocus()) {
                    imm.hideSoftInputFromWindow(et_ans.getWindowToken(), 0);
                    addAnswerDialog.dismiss();
                }
                return false;
            }
        });

        addAnswerDialog.show();

        et_ans.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (et_ans.requestFocus()) {
                    imm.showSoftInput(et_ans, InputMethodManager.SHOW_FORCED);
                }
            }
        }, 150);

    }


    class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.MyViewHolder> {
        Activity activity;

        public AnswerListAdapter(Activity activity) {
            this.activity = activity;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_ans_char_acount;
            TextView tv_ans;
            EditText et_ans;
            ImageButton ib_more;
            PopupMenu popup;
            MenuInflater menuInflater;

            MyViewHolder(View view) {
                super(view);
                tv_ans = (TextView) view.findViewById(R.id.tv_ans);
                tv_ans_char_acount = (TextView) view.findViewById(R.id.tv_ans_char_acount);
                et_ans = (EditText) view.findViewById(R.id.et_ans);
                ib_more = (ImageButton) view.findViewById(R.id.ib_more);
                popup = new PopupMenu(activity, ib_more);
                menuInflater = popup.getMenuInflater();
                menuInflater.inflate(R.menu.edit_delete_menu, popup.getMenu());

                ib_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popup.show();
                    }
                });
            }
        }

        @Override
        public AnswerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quiz_answer_list_item, parent, false);

            return new AnswerListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AnswerListAdapter.MyViewHolder holder, int position) {
            //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
            holder.et_ans.setText(answers.get(position));
            holder.tv_ans.setText(getString(R.string.answer)+" "+(position+1));
            holder.et_ans.setEnabled(false);

            holder.popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:

                            break;

                        case R.id.delete:
                            answers.remove(position);
                            notifyDataSetChanged();
                            break;
                    }
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return answers.size();
            //  return hierarchyList.size();
        }
    }


    private void requestStoragePermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            switch (requestCode) {
                case PICK_IMAGE_PERMISSION:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AddQuestionActivity.this);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PICK_IMAGE_PERMISSION:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(AddQuestionActivity.this);
                    break;
            }
            return;
            // other 'case' lines to check for other
            // permissions this app might request.

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File photo = commonFunctions.getScaledDownImage(result.getUri().getPath());
                questionImagePath = Uri.fromFile(photo).getPath();
                Glide.with(getApplicationContext())
                        .load(questionImagePath)
                        .apply(new RequestOptions().override(iv_question_image.getWidth(), iv_question_image.getHeight()))
                        .into(iv_question_image);
                bt_add_question_image.setVisibility(View.GONE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}