package com.avadna.luneblaze.fragments.sessions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionNotesGalleryAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionPhotoGalleryAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionVideosGalleryAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionFile;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionVideo;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrganisedSessionGalleryFragment extends Fragment {

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    LinearLayout ll_parent;
    ProgressBar pb_loading_content;

    TextView tv_no_photos;
    RecyclerView rv_session_photos;
    OrganisedSessionPhotoGalleryAdapter organisedSessionPhotoGalleryAdapter;

    LinearLayout ll_session_notes_wrapper;
    RecyclerView rv_session_notes;
    List<PojoSessionFile> notesList;
    OrganisedSessionNotesGalleryAdapter organisedSessionNotesGalleryAdapter;

    LinearLayout ll_session_videos_wrapper;
    RecyclerView rv_session_videos;
    List<PojoSessionVideo> videoList;
    OrganisedSessionVideosGalleryAdapter organisedSessionVideosGalleryAdapter;

    AdapterView.OnItemClickListener onItemClickListener;

    List<PojoVenuePhoto> links;

    String sessionId;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    ApiInterface apiService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View baseLayout = inflater.inflate(R.layout.fragment_session_gallery, container, false);
        // Inflate the layout for this fragment
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        sessionId = getArguments().getString("id");
        initViews(baseLayout);
        hitGetSessionDetailsApi(user_id, sessionId, "photos");

        return baseLayout;
    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetSessionDetailsApi(user_id, sessionId, "photos");
            }
        });

        ll_parent = (LinearLayout) baseLayout.findViewById(R.id.ll_parent);
        ll_parent.setVisibility(View.GONE);

        tv_no_photos = (TextView) baseLayout.findViewById(R.id.tv_no_photos);
        tv_no_photos.setVisibility(View.GONE);

        ll_session_notes_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_session_notes_wrapper);
        ll_session_notes_wrapper.setVisibility(View.GONE);

        ll_session_videos_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_session_videos_wrapper);
        ll_session_videos_wrapper.setVisibility(View.GONE);

        pb_loading_content = (ProgressBar) baseLayout.findViewById(R.id.pb_loading_content);
        initGallery(baseLayout);
    }


    private void initGallery(View baseLayout) {

        //Photos List
        links = new ArrayList<>();

        rv_session_photos = (RecyclerView) baseLayout.findViewById(R.id.rv_session_photos);
        rv_session_photos.setNestedScrollingEnabled(false);
        organisedSessionPhotoGalleryAdapter = new OrganisedSessionPhotoGalleryAdapter(getActivity(),
                sessionId, links, "grid");

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_session_photos.setLayoutManager(gridLayoutManager);

        /*RecyclerView.LayoutManager photoLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_session_photos.setLayoutManager(photoLayoutManager);*/
        rv_session_photos.setAdapter(organisedSessionPhotoGalleryAdapter);

        //Notes List
        rv_session_notes = (RecyclerView) baseLayout.findViewById(R.id.rv_session_notes);
        rv_session_notes.setNestedScrollingEnabled(false);
        notesList = new ArrayList<>();
        organisedSessionNotesGalleryAdapter = new OrganisedSessionNotesGalleryAdapter(notesList, getContext());

        RecyclerView.LayoutManager notesLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_session_notes.setLayoutManager(notesLayoutManager);
        rv_session_notes.setAdapter(organisedSessionNotesGalleryAdapter);


        //Videos List
        rv_session_videos = (RecyclerView) baseLayout.findViewById(R.id.rv_session_videos);
        rv_session_videos.setNestedScrollingEnabled(false);
        videoList = new ArrayList<>();
        organisedSessionVideosGalleryAdapter = new OrganisedSessionVideosGalleryAdapter(videoList, getContext());

        RecyclerView.LayoutManager videoLayoutManager = new GridLayoutManager(getActivity(), 2);
        rv_session_videos.setLayoutManager(videoLayoutManager);
        rv_session_videos.setAdapter(organisedSessionVideosGalleryAdapter);
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        ll_no_connection_wrapper.setVisibility(View.GONE);
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (response.body().data.eventPhotos != null && !response.body().data.eventPhotos.isEmpty()) {
                        if (view.equalsIgnoreCase("photos")) {
                            links.clear();
                            links.addAll(response.body().data.eventPhotos);
                            organisedSessionVideosGalleryAdapter.setThumbnail(response.body().data.coverPhoto);
                            organisedSessionPhotoGalleryAdapter.notifyDataSetChanged();
                            videoList.clear();
                            if (response.body().data.videos != null) {
                                videoList.addAll(response.body().data.videos);
                                if(!videoList.isEmpty()){
                                    ll_session_videos_wrapper.setVisibility(View.VISIBLE);
                                }
                            }
                            organisedSessionVideosGalleryAdapter.notifyDataSetChanged();

                            notesList.clear();
                            if (response.body().data.files != null) {
                                notesList.addAll(response.body().data.files);
                                if(!notesList.isEmpty()){
                                    ll_session_notes_wrapper.setVisibility(View.VISIBLE);
                                }
                            }
                            organisedSessionNotesGalleryAdapter.setSession_id(response.body().data.sessionsId);
                            organisedSessionNotesGalleryAdapter.notifyDataSetChanged();
                        }

                        ll_parent.setVisibility(View.VISIBLE);
                        pb_loading_content.setVisibility(View.GONE);
                    } else {
                        pb_loading_content.setVisibility(View.GONE);
                        tv_no_photos.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //  ll_parent.setVisibility(View.VISIBLE);
                pb_loading_content.setVisibility(View.GONE);
                ll_no_connection_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

}
