package com.avadna.luneblaze.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {

    PlayerView pv_media;
    SimpleExoPlayer player;
    Player.EventListener eventListener;
    View controlView;
    ImageButton ib_exo_fullscreen;
    boolean fullscreen = false;

    float x1, x2;

    ProgressBar pb_loading;

    TextView tv_time_skip;

    String contentPath ;
    String currentText = "";
    String newText = "";
    int seconds, minutes, hours;
    long currentPos = 0;
    String hourText;
    String minText;
    String secText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        initViews();

        contentPath=getIntent().getStringExtra(AppKeys.PATH);
        if(contentPath==null||contentPath.isEmpty()){
            contentPath = AppKeys.ALTERNATE_WEBSITE_URL + "content/uploads/videos/test-video.mp4";
        }

        initMediaPlayer();
    }

    private void initViews() {
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        tv_time_skip = (TextView) findViewById(R.id.tv_time_skip);
        tv_time_skip.setVisibility(View.GONE);
        pv_media = (PlayerView) findViewById(R.id.pv_media);
        controlView = pv_media.findViewById(R.id.exo_controller);
        ib_exo_fullscreen = (ImageButton) controlView.findViewById(R.id.ib_exo_fullscreen);
    }

    private void initMediaPlayer() {

        player = ExoPlayerFactory.newSimpleInstance(this);
        pv_media.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Luneblaze"));
// This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(contentPath));

// Prepare the player with the source.
        player.prepare(videoSource);
        eventListener = new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    pb_loading.setVisibility(View.VISIBLE);

                } else {
                    pb_loading.setVisibility(View.GONE);

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {


            }
        };
        player.addListener(eventListener);

        ib_exo_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullscreen) {
                    showSystemUI();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                } else {
                    hideSystemUI();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                fullscreen = !fullscreen;
            }
        });

        final int MIN_DISTANCE = 50;
        final float SCALE_FACTOR = 80;


        pv_media.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = event.getX();

                        currentPos = player.getCurrentPosition();
                        seconds = (int) (currentPos / 1000) % 60;
                        minutes = (int) ((currentPos / (1000 * 60)) % 60);
                        hours = (int) ((currentPos / (1000 * 60 * 60)) % 24);
                        hourText = String.valueOf(hours) + ":";
                        minText = String.valueOf(minutes) + ":";
                        secText = String.valueOf(seconds);

                        if (hours == 0) {
                            hourText = "";
                        }
                        if (minutes < 10) {
                            minText = "0" + minText;
                        }
                        if (seconds < 10) {
                            secText = "0" + secText;
                        }
                        currentText = hourText + minText + secText;
                        break;


                    case MotionEvent.ACTION_UP:
                        player.setPlayWhenReady(true);
                        tv_time_skip.setVisibility(View.GONE);
                        x2 = event.getX();
                        float deltaX = x2 - x1;
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            long target = (long) (deltaX * SCALE_FACTOR);
                            long newPos = player.getCurrentPosition() + target;
                            if (newPos > player.getContentDuration()) {
                                newPos = player.getContentDuration();
                            }
                            player.seekTo(newPos);
                            Log.d("seek data", "deltaX = " + deltaX);
                            Log.d("seek data", "Target = " + target);
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getX();
                        float diff = currentX - x1;

                        if (Math.abs(diff) > MIN_DISTANCE) {
                            player.setPlayWhenReady(false);
                            tv_time_skip.setVisibility(View.VISIBLE);
                            long offsetPos = (long) (diff * SCALE_FACTOR);
                            long newPos = currentPos + offsetPos;

                            if (newPos > player.getContentDuration()) {
                                newPos = player.getContentDuration();
                            }

                            if (newPos < 0) {
                                newPos = 0;
                            }

                            seconds = (int) (newPos / 1000) % 60;
                            minutes = (int) ((newPos / (1000 * 60)) % 60);
                            hours = (int) ((newPos / (1000 * 60 * 60)) % 24);
                            hourText = String.valueOf(hours) + ":";
                            minText = String.valueOf(minutes) + ":";
                            secText = String.valueOf(seconds);
                            if (hours == 0) {
                                hourText = "";
                            }
                            if (minutes < 10) {
                                minText = "0" + minText;
                            }
                            if (seconds < 10) {
                                secText = "0" + secText;
                            }
                            newText = hourText + minText + secText;
                            tv_time_skip.setText(currentText + " -> " + newText);
                        }
                        break;

                }
                return false;
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);

    }

    @Override
    public void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        player.removeListener(eventListener);
        player.release();
    }


    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }


}
