package com.avadna.luneblaze.helperClasses;


import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;

public class ConnectionErrorDialog extends Dialog implements View.OnClickListener {

    private Fragment fragment;
    private Activity activity;
    private TextView tv_retry;
    private ImageView iv_back;
    private TextView tv_title;

    private String title;

    private ConnectionErrorDialogListener listener;

    public ConnectionErrorDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public ConnectionErrorDialog(Fragment fragment) {
        super(fragment.getActivity());
        this.activity = fragment.getActivity();
    }


    public ConnectionErrorDialog(Activity activity, int appTheme, String title) {
        super(activity, appTheme);
        this.activity = activity;
        this.title = title;
    }

    public ConnectionErrorDialog(Fragment fragment, int appTheme, String title) {
        super(fragment.getActivity(), appTheme);
        this.activity = fragment.getActivity();
        this.activity = fragment.getActivity();
        this.title = title;
    }

    public void setTitle(String title){
        int a=5;
        if(tv_title!=null){
            tv_title.setText(title);
        }
    }


    public void setConnectionErrorDialogListener(ConnectionErrorDialogListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.connection_error_dialog);

        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_title.setText(title);
        tv_retry = (TextView) findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(activity, R.color.status_bar_color));
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            switch (view.getId()) {
                //Cancel Button
                case R.id.tv_retry:
                    listener.onRetry();
                    break;

                case R.id.iv_back:
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onBack();
                            dismiss();
                        }
                    }, 100);
                    break;

                default:
                    break;
            }
        }

    }


    public interface ConnectionErrorDialogListener {
        void onRetry();

        void onBack();
    }
}