package com.avadna.luneblaze.update.activities.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.activities.PostCreationActivity;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.pojo.quiz.CreateQuestionItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

public class CreateQuizActivity extends AppBaseActivity {

    private EditText et_title, et_description;
    private Button bt_add_image, bt_select_duration,bt_add_question;
    private ImageView iv_quiz_image;
    private ListView lv_questions_list;
    private ArrayList<CreateQuestionItem> questions;
    private final int PICK_IMAGE_PERMISSION = 169;
    String quizImagePath;

    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        initViews();
        initClickListener();
        setClickListener();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_add_question:
                        Intent addAnswerIntent=new Intent(CreateQuizActivity.this,AddQuestionActivity.class);
                        startActivity(addAnswerIntent);
                        break;

                    case R.id.iv_quiz_image:
                    case R.id.bt_add_image:
                        requestStoragePermission(PICK_IMAGE_PERMISSION);
                        break;

                    case R.id.bt_select_duration:
                        break;
                }
            }
        };
    }

    private void setClickListener() {
        bt_add_question.setOnClickListener(onClickListener);
        bt_add_image.setOnClickListener(onClickListener);
        iv_quiz_image.setOnClickListener(onClickListener);
        bt_select_duration.setOnClickListener(onClickListener);
    }


    private void initViews() {
        initQuestionList();
        et_title = (EditText) findViewById(R.id.et_title);
        et_description = (EditText) findViewById(R.id.et_description);
        bt_add_image = (Button) findViewById(R.id.bt_add_image);
        bt_select_duration = (Button) findViewById(R.id.bt_select_duration);
        iv_quiz_image = (ImageView) findViewById(R.id.iv_quiz_image);
        bt_add_question = (Button) findViewById(R.id.bt_add_question);
    }


    private void initQuestionList() {
        questions = new ArrayList<>();
        lv_questions_list = (ListView) findViewById(R.id.lv_questions_list);
        lv_questions_list.setAdapter(new QuestionListAdapter(this));
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
                            .start(CreateQuizActivity.this);
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
                            .start(CreateQuizActivity.this);
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
                quizImagePath = Uri.fromFile(photo).getPath();
                Glide.with(getApplicationContext())
                        .load(quizImagePath)
                        .apply(new RequestOptions().override(iv_quiz_image.getWidth(), iv_quiz_image.getHeight()))
                        .into(iv_quiz_image);
                bt_add_image.setVisibility(View.GONE);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    class QuestionListAdapter extends BaseAdapter {
        Context context;
        LayoutInflater layoutInflater;

        public QuestionListAdapter(Context applicationContext) {
            this.context = applicationContext;
            layoutInflater = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return questions.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = layoutInflater.inflate(R.layout.added_question_list_item, null); // inflate the layout
            TextView tv_question = (TextView) view.findViewById(R.id.tv_question);
            ImageButton ib_more = (ImageButton) view.findViewById(R.id.ib_more);
            ImageView iv_question_image = (ImageView) view.findViewById(R.id.iv_question_image);


            CreateQuestionItem currentItem = questions.get(position);

            tv_question.setText(currentItem.text);

            Glide.with(context)
                    .load(currentItem.imagePath)
                    .apply(new RequestOptions().override(256, 192))
                    .into(iv_question_image);


            PopupMenu popup;
            MenuInflater menuInflater;
            popup = new PopupMenu(context, ib_more);
            menuInflater = popup.getMenuInflater();
            menuInflater.inflate(R.menu.edit_delete_menu, popup.getMenu());

            ib_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.edit:

                            break;

                        case R.id.delete:
                            questions.remove(position);
                            notifyDataSetChanged();
                            break;
                    }
                    return false;
                }
            });

            return view;
        }
    }


}