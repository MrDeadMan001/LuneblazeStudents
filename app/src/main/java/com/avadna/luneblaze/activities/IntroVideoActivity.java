package com.avadna.luneblaze.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

public class IntroVideoActivity extends AppCompatActivity {

    PlayerView playerView;
    SimpleExoPlayer player;
    Player.EventListener eventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_video);

        initVideoPlayer();

        TextView tv_skip = (TextView) findViewById(R.id.tv_skip);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
    }

    private void initVideoPlayer() {
        //String path = "android.resource://" + getPackageName() + "/" + R.raw.intro;
        playerView = (PlayerView) findViewById(R.id.playerView);
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);

        String videoPath = RawResourceDataSource.buildRawResourceUri(R.raw.intro).toString();
        Uri uri = RawResourceDataSource.buildRawResourceUri(R.raw.intro);

        ExtractorMediaSource audioSource = new ExtractorMediaSource(
                uri,
                new DefaultDataSourceFactory(this, "Luneblaze"),
                new DefaultExtractorsFactory(),
                null,
                null);
        player.prepare(audioSource);
        playerView.setPlayer(player);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setPlayWhenReady(true);
// Prepare the player with the source.
        eventListener = new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    goNext();
                }
            }


            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        };
        player.addListener(eventListener);

        playerView.setUseController(false);

    }

    public void goNext() {
        Intent loginIntent = new Intent(IntroVideoActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
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


}
