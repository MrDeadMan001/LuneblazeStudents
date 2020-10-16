package com.avadna.luneblaze.update.activities.quiz;

import androidx.cardview.widget.CardView;
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
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.pojo.quiz.CreateQuestionItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlternateAddQuestionActivity extends AppBaseActivity {

    private RelativeLayout rl_question_wrapper;
    private EditText et_question_text;
    private TextView tv_text_count;
    private CardView cv_image_wrapper;
    private ImageView iv_question_image;
    private TextView tv_add_question_image;

    private TextView tv_answer1;
    private TextView tv_answer2;
    private TextView tv_answer3;
    private TextView tv_answer4;

    private RelativeLayout rl_answer1_wrapper;
    private RelativeLayout rl_answer2_wrapper;
    private RelativeLayout rl_answer3_wrapper;
    private RelativeLayout rl_answer4_wrapper;

    private ImageView iv_ans1_correct;
    private ImageView iv_ans2_correct;
    private ImageView iv_ans3_correct;
    private ImageView iv_ans4_correct;


    private RelativeLayout rl_added_questions_wrapper;
    private ImageButton bt_add_more;
    List<CreateQuestionItem> questions;
    private RecyclerView rv_added_question_list;
    private AddedQuestionsListAdapter addedQuestionsListAdapter;
    private View.OnClickListener onClickListener;


    private DisplayMetrics displayMetrics;
    private int screenHeight;
    private int screenWidth;
    private int maxQuestionItemHeight;
    private int imgMaxHeight;
    private int answerItemHeight;
    private float imgRatio = 4f / 3;

    private int addedQuestionItemHeight;
    private int addedQuestionItemWidth;

    List<String> addedAnswers = new ArrayList<>();

    String imagePath;
    int correctAnswer = 1;

    private final int PICK_IMAGE_PERMISSION = 169;
    private Dialog addAnswerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternate_add_question);
        initViews();
        initClickListener();
        setClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDimensions();
    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_add_more:
                        addQuestionItem();
                        clearFields();
                        break;

                    case R.id.tv_answer1:
                    case R.id.tv_answer2:
                    case R.id.tv_answer3:
                    case R.id.tv_answer4:
                        openAddAnswerDialog();
                        break;

                    case R.id.iv_question_image:
                        requestStoragePermission(PICK_IMAGE_PERMISSION);
                        break;
                }
            }
        };
    }


    private void updateAnswersText() {
        tv_answer1.setText(R.string.add_answer);
        tv_answer2.setText(R.string.add_answer);
        tv_answer3.setText(R.string.add_answer_optional);
        tv_answer4.setText(R.string.add_answer_optional);

        for (int i = 0; i < addedAnswers.size(); i++) {
            if (i == 0) {
                tv_answer1.setText(addedAnswers.get(i));
            } else if (i == 1) {
                tv_answer2.setText(addedAnswers.get(i));
            } else if (i == 2) {
                tv_answer3.setText(addedAnswers.get(i));
            } else if (i == 3) {
                tv_answer4.setText(addedAnswers.get(i));
            }
        }
    }


    private void addQuestionItem() {
        CreateQuestionItem createQuestionItem = new CreateQuestionItem();
        createQuestionItem.text = et_question_text.getText().toString();
        createQuestionItem.imagePath = imagePath;
        createQuestionItem.answers.addAll(addedAnswers);
        createQuestionItem.correctAns = correctAnswer;
        createQuestionItem.duration = 25;
        questions.add(createQuestionItem);
        addedQuestionsListAdapter.notifyItemInserted(questions.size() - 1);
    }

    private void clearFields() {
        et_question_text.getText().clear();
        iv_question_image.setImageBitmap(null);
        iv_question_image.invalidate();
        tv_add_question_image.setVisibility(View.VISIBLE);
        addedAnswers.clear();

        tv_answer1.setText(R.string.add_answer);
        tv_answer2.setText(R.string.add_answer);
        tv_answer3.setText(R.string.add_answer_optional);
        tv_answer4.setText(R.string.add_answer_optional);

        iv_ans1_correct.setVisibility(View.GONE);
        iv_ans2_correct.setVisibility(View.GONE);
        iv_ans3_correct.setVisibility(View.GONE);
        iv_ans4_correct.setVisibility(View.GONE);
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
                if (et_ans.requestFocus()) {
                    imm.hideSoftInputFromWindow(et_ans.getWindowToken(), 0);
                    addedAnswers.add(et_ans.getText().toString());
                    updateAnswersText();
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


    private void initAddedQuestionList() {
        questions = new ArrayList<>();
        rv_added_question_list = (RecyclerView) findViewById(R.id.rv_added_question_list);
        addedQuestionsListAdapter = new AddedQuestionsListAdapter(this);
        rv_added_question_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        rv_added_question_list.setAdapter(addedQuestionsListAdapter);
        rv_added_question_list.getLayoutParams().height = (addedQuestionItemHeight + 20);
    }

    private void setEditTextProperties() {
        et_question_text = (EditText) findViewById(R.id.et_question_text);
        et_question_text.setRawInputType(InputType.TYPE_CLASS_TEXT);
        et_question_text.setImeActionLabel(getResources().getString(R.string.done), EditorInfo.IME_ACTION_DONE);
        et_question_text.setImeOptions(EditorInfo.IME_ACTION_DONE);

        et_question_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event == null) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Capture soft enters in a singleLine EditText that is the last EditText
                        // This one is useful for the new list case, when there are no existing ListItems
                        et_question_text.clearFocus();
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    } else if (actionId == EditorInfo.IME_ACTION_NEXT) {
                        // Capture soft enters in other singleLine EditTexts
                    } else if (actionId == EditorInfo.IME_ACTION_GO) {
                    } else {
                        // Let the system handle all other null KeyEvents
                        return false;
                    }
                } else if (actionId == EditorInfo.IME_NULL) {
                    // Capture most soft enters in multi-line EditTexts and all hard enters;
                    // They supply a zero actionId and a valid keyEvent rather than
                    // a non-zero actionId and a null event like the previous cases.
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        // We capture the event when the key is first pressed.
                    } else {
                        // We consume the event when the key is released.
                        return true;
                    }
                } else {
                    // We let the system handle it when the listener is triggered by something that
                    // wasn't an enter.
                    return false;
                }
                return true;
            }
        });
    }


    private void setTextChangeListener() {
        et_question_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int len = s.length();
                tv_text_count.setText(len + "/" + AppKeys.QUIZ_QUESTION_CHARACTER_LIMIT);

                if (s.length() > AppKeys.QUIZ_QUESTION_CHARACTER_LIMIT) {
                    et_question_text.setText(s.subSequence(0, AppKeys.QUIZ_QUESTION_CHARACTER_LIMIT));
                    et_question_text.setSelection(et_question_text.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                            .start(AlternateAddQuestionActivity.this);
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
                            .start(AlternateAddQuestionActivity.this);
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
                imagePath = Uri.fromFile(photo).getPath();
                Glide.with(getApplicationContext())
                        .load(imagePath)
                        .apply(new RequestOptions().override(iv_question_image.getWidth(), iv_question_image.getHeight()))
                        .into(iv_question_image);
                tv_add_question_image.setVisibility(View.GONE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setClickListeners() {
        tv_answer1.setOnClickListener(onClickListener);
        tv_answer2.setOnClickListener(onClickListener);
        tv_answer3.setOnClickListener(onClickListener);
        tv_answer4.setOnClickListener(onClickListener);
        bt_add_more.setOnClickListener(onClickListener);
        iv_question_image.setOnClickListener(onClickListener);
    }

    private void initViews() {
        initDimensions();
        initAddedQuestionList();

        rl_added_questions_wrapper = (RelativeLayout) findViewById(R.id.rl_added_questions_wrapper);
        rl_question_wrapper = (RelativeLayout) findViewById(R.id.rl_question_wrapper);
        et_question_text = (EditText) findViewById(R.id.et_question_text);
        tv_text_count = (TextView) findViewById(R.id.tv_text_count);
        cv_image_wrapper = (CardView) findViewById(R.id.cv_image_wrapper);
        iv_question_image = (ImageView) findViewById(R.id.iv_question_image);
        tv_add_question_image = (TextView) findViewById(R.id.tv_add_question_image);

        rl_answer1_wrapper = (RelativeLayout) findViewById(R.id.rl_answer1_wrapper);
        rl_answer2_wrapper = (RelativeLayout) findViewById(R.id.rl_answer2_wrapper);
        rl_answer3_wrapper = (RelativeLayout) findViewById(R.id.rl_answer3_wrapper);
        rl_answer4_wrapper = (RelativeLayout) findViewById(R.id.rl_answer4_wrapper);

        tv_answer1 = (TextView) findViewById(R.id.tv_answer1);
        tv_answer2 = (TextView) findViewById(R.id.tv_answer2);
        tv_answer3 = (TextView) findViewById(R.id.tv_answer3);
        tv_answer4 = (TextView) findViewById(R.id.tv_answer4);
        iv_ans1_correct = (ImageView) findViewById(R.id.iv_ans1_correct);
        iv_ans2_correct = (ImageView) findViewById(R.id.iv_ans2_correct);
        iv_ans3_correct = (ImageView) findViewById(R.id.iv_ans3_correct);
        iv_ans4_correct = (ImageView) findViewById(R.id.iv_ans4_correct);

        bt_add_more = (ImageButton) findViewById(R.id.bt_add_more);

        iv_ans1_correct.setVisibility(View.GONE);
        iv_ans2_correct.setVisibility(View.GONE);
        iv_ans3_correct.setVisibility(View.GONE);
        iv_ans4_correct.setVisibility(View.GONE);


        setTextChangeListener();
        setEditTextProperties();
    }

    private void setDimensions() {
        ViewTreeObserver vto = iv_question_image.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                iv_question_image.getViewTreeObserver().removeOnPreDrawListener(this);
                int imgViewWidth = iv_question_image.getMeasuredWidth();
                int scaledImgHeight = (int) (1f * imgViewWidth / imgRatio);
                if (scaledImgHeight > imgMaxHeight) {
                    scaledImgHeight = imgMaxHeight;
                }

                iv_question_image.getLayoutParams().height = scaledImgHeight;
                et_question_text.setHeight(maxQuestionItemHeight - scaledImgHeight);
                return true;
            }
        });

        rl_answer1_wrapper.getLayoutParams().height = answerItemHeight;
        rl_answer2_wrapper.getLayoutParams().height = answerItemHeight;
        rl_answer3_wrapper.getLayoutParams().height = answerItemHeight;
        rl_answer4_wrapper.getLayoutParams().height = answerItemHeight;
    }

    private void initDimensions() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        //42% of screen height
        maxQuestionItemHeight = ((int) (screenHeight * 0.40f));
        //max image height would be 30% of the screen
        imgMaxHeight = ((int) (screenHeight * 0.25f));
        answerItemHeight = ((int) (screenHeight * 0.15f));
        addedQuestionItemHeight = (int) (screenHeight * 0.09f);
        addedQuestionItemWidth = (int) (screenHeight * 0.13f);
    }

    class AddedQuestionsListAdapter extends RecyclerView.Adapter<AddedQuestionsListAdapter.MyViewHolder> {
        Activity activity;

        public AddedQuestionsListAdapter(Activity activity) {
            this.activity = activity;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CardView cv_content_wrapper;
            TextView tv_text;
            ImageView iv_image;

            MyViewHolder(View view) {
                super(view);
                cv_content_wrapper = (CardView) view.findViewById(R.id.cv_content_wrapper);
                tv_text = (TextView) view.findViewById(R.id.tv_text);
                iv_image = (ImageView) view.findViewById(R.id.iv_image);
                cv_content_wrapper.getLayoutParams().height = addedQuestionItemHeight;
                cv_content_wrapper.getLayoutParams().width = addedQuestionItemWidth;
            }
        }

        @Override
        public AddedQuestionsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.added_question_list_item2, parent, false);

            return new AddedQuestionsListAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(AddedQuestionsListAdapter.MyViewHolder holder, int position) {
            //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
            final CreateQuestionItem currentItem = questions.get(position);
            holder.cv_content_wrapper.getLayoutParams().height = addedQuestionItemHeight;
            holder.cv_content_wrapper.getLayoutParams().width = addedQuestionItemWidth;
            holder.iv_image.setMaxHeight(addedQuestionItemHeight);

            Glide.with(getApplicationContext())
                    .load(currentItem.imagePath)
                    .apply(new RequestOptions().override(addedQuestionItemWidth, addedQuestionItemHeight))
                    .into(holder.iv_image);
            holder.tv_text.setText(currentItem.text);
        }

        @Override
        public int getItemCount() {
            return questions.size();
            //  return hierarchyList.size();
        }
    }


}