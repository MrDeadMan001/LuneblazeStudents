package com.avadna.luneblaze.fragments.venue;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoGetVenueDetailsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenueDetailsFragment extends Fragment implements OnMapReadyCallback {

    MapView mv_venue_location;
    PojoGetVenueDetailsResponse pojoGetVenueDetailsResponse;
    TextView tv_venue_address;
    int ellipsizedTextLength;

    EditText et_web_link, et_venue_description;

    RelativeLayout rl_edit_description, rl_edit_website;

    TextView tv_web_link, tv_venue_description;

    TextView tv_show_more;
    ProgressBar pb_loading_content;

    ImageButton ib_edit_description, ib_edit_website;

    ImageButton ib_done_description, ib_done_website;


    View.OnClickListener onClickListener;
    ApiInterface apiService;
    String user_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;


    //   MapViewInScrollViewTouchListener mapViewInScrollViewTouchListener;

    public VenueDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("params");
            if (!mParam1.equals("null")) {
                Gson gson = new Gson();
                pojoGetVenueDetailsResponse = gson.fromJson(mParam1, PojoGetVenueDetailsResponse.class);
                pojoGetVenueDetailsResponse.data.venueData.description
                        = pojoGetVenueDetailsResponse.data.venueData.description.replace("\n", "<br>");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_venue_details, container, false);
        setUpMapView(baseLayout, savedInstanceState);
        initViews(baseLayout);
        setDataOnViews();
        initClickListener();
        setClickListeners();

        return baseLayout;
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                switch (view.getId()) {
                    case R.id.ib_edit_description:
                        rl_edit_description.setVisibility(View.VISIBLE);
                        tv_venue_description.setVisibility(View.GONE);
                        ib_edit_description.setVisibility(View.GONE);
                        et_venue_description.setText(Html.fromHtml(pojoGetVenueDetailsResponse.data.venueData.description));
                        et_venue_description.requestFocus();
                        et_venue_description.setSelection(et_venue_description.getText().length());
                        imm.showSoftInput(et_venue_description, InputMethodManager.SHOW_FORCED);

                        break;

                    case R.id.ib_done_description:
                        rl_edit_description.setVisibility(View.GONE);
                        tv_venue_description.setVisibility(View.VISIBLE);
                        ib_edit_description.setVisibility(View.VISIBLE);

                        hitEditDescriptionApi(user_id, pojoGetVenueDetailsResponse.data.venueData.venueId,
                                et_venue_description.getText().toString(), et_web_link.getText().toString()
                                , pojoGetVenueDetailsResponse.data.venueData.venueName);
                        pojoGetVenueDetailsResponse.data.venueData.description = et_venue_description.getText().toString();
                        addShowMoreButton();
                        imm.hideSoftInputFromWindow(et_venue_description.getWindowToken(), 0);
                        ib_edit_description.setImageResource(R.drawable.ic_edit_grey);
                        break;

                    case R.id.ib_edit_website:
                        rl_edit_website.setVisibility(View.VISIBLE);
                        tv_web_link.setVisibility(View.GONE);
                        ib_edit_website.setVisibility(View.GONE);
                        et_web_link.setText(pojoGetVenueDetailsResponse.data.venueData.websiteLink);
                        et_web_link.requestFocus();
                        et_web_link.setSelection(et_web_link.getText().length());
                        imm.showSoftInput(et_web_link, InputMethodManager.SHOW_FORCED);
                        break;


                    case R.id.ib_done_website:
                        rl_edit_website.setVisibility(View.GONE);
                        tv_web_link.setVisibility(View.VISIBLE);
                        ib_edit_website.setVisibility(View.VISIBLE);
                        hitEditDescriptionApi(user_id, pojoGetVenueDetailsResponse.data.venueData.venueId,
                                et_venue_description.getText().toString(), et_web_link.getText().toString()
                                , pojoGetVenueDetailsResponse.data.venueData.venueName);
                        tv_web_link.setText(et_web_link.getText());
                        pojoGetVenueDetailsResponse.data.venueData.websiteLink = et_web_link.getText().toString();
                        imm.hideSoftInputFromWindow(et_web_link.getWindowToken(), 0);
                        ib_edit_website.setImageResource(R.drawable.ic_edit_grey);
                        break;


                    case R.id.tv_web_link:
                        commonFunctions.urlLoader(tv_web_link.getText().toString());
                        /*Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);
                        webViewIntent.putExtra("url", tv_web_link.getText().toString());
                        startActivity(webViewIntent);*/
                        break;


                }
            }
        };
    }

    private void hitEditDescriptionApi(String user_id, String venue_id, String description, String website,
                                       String name) {
        {
            Call<PojoNoDataResponse> call = apiService.updateVenueDetails(user_id, venue_id,
                    description, website, name);
            call.enqueue(new Callback<PojoNoDataResponse>() {
                @Override
                public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        /*if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), getString(R.string.done),
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);*/
                    }
                }

                @Override
                public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                    // Log error here since request failed
                }
            });
        }
    }

    private void setClickListeners() {
        ib_edit_website.setOnClickListener(onClickListener);
        ib_edit_description.setOnClickListener(onClickListener);
        ib_done_website.setOnClickListener(onClickListener);
        ib_done_description.setOnClickListener(onClickListener);
        tv_web_link.setOnClickListener(onClickListener);
    }

    private void initViews(View baseLayout) {
        tv_venue_address = (TextView) baseLayout.findViewById(R.id.tv_venue_address);
        et_venue_description = (EditText) baseLayout.findViewById(R.id.et_venue_description);
        tv_web_link = (TextView) baseLayout.findViewById(R.id.tv_web_link);
        tv_venue_description = (TextView) baseLayout.findViewById(R.id.tv_venue_description);

        rl_edit_description = (RelativeLayout) baseLayout.findViewById(R.id.rl_edit_description);
        rl_edit_description.setVisibility(View.GONE);
        rl_edit_website = (RelativeLayout) baseLayout.findViewById(R.id.rl_edit_website);
        rl_edit_website.setVisibility(View.GONE);

        ib_done_description = (ImageButton) baseLayout.findViewById(R.id.ib_done_description);
        ib_done_website = (ImageButton) baseLayout.findViewById(R.id.ib_done_website);

        ib_edit_description = (ImageButton) baseLayout.findViewById(R.id.ib_edit_description);
        ib_edit_description.setVisibility(View.GONE);
        ib_edit_website = (ImageButton) baseLayout.findViewById(R.id.ib_edit_website);
        ib_edit_website.setVisibility(View.GONE);
        et_web_link = (EditText) baseLayout.findViewById(R.id.et_web_link);
        tv_show_more = (TextView) baseLayout.findViewById(R.id.tv_show_more);

        tv_show_more.setVisibility(View.GONE);
    }

    private void setDataOnViews() {

        if (canUpdateData()) {
            ib_edit_website.setVisibility(View.VISIBLE);
            ib_edit_description.setVisibility(View.VISIBLE);
        }

        tv_venue_address.setText(pojoGetVenueDetailsResponse.data.venueData.location);
        tv_web_link.setText(pojoGetVenueDetailsResponse.data.venueData.websiteLink);
        et_web_link.setText(pojoGetVenueDetailsResponse.data.venueData.websiteLink);
        et_web_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_web_link.isEnabled()) {
                    commonFunctions.urlLoader(et_web_link.getText().toString());
                    /*Intent webIntent = new Intent(getActivity(), WebViewActivity.class);
                    webIntent.putExtra("url", et_web_link.getText().toString());
                    startActivity(webIntent);*/
                }

            }
        });
        addShowMoreButton();
    }

    private void addShowMoreButton() {
        tv_venue_description.setMaxLines(10);
        tv_venue_description.invalidate();
        tv_show_more.setText(getString(R.string.show_more));
        pojoGetVenueDetailsResponse.data.venueData.description
                = pojoGetVenueDetailsResponse.data.venueData.description.replace("<br />", "");
        pojoGetVenueDetailsResponse.data.venueData.description
                = pojoGetVenueDetailsResponse.data.venueData.description.replace("\n", "<br>");
        tv_venue_description.setText(Html.fromHtml(pojoGetVenueDetailsResponse.data.venueData.description));
        tv_venue_description.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                try {
                    tv_venue_description.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int lines = tv_venue_description.getLineCount();
                    int ellCount = tv_venue_description.getLayout().getEllipsisCount(lines - 1);
                    if (ellCount > 0 || ellipsizedTextLength > 0) {
                        String visibleText = tv_venue_description.getText().toString();

                        if (ellipsizedTextLength == 0) {
                            ellipsizedTextLength = visibleText.length() - ellCount - 9;
                        }

                        tv_show_more.setVisibility(View.VISIBLE);
                        tv_show_more.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                addShowLessButton();
                            }
                        });
                    } else {
                        tv_show_more.setVisibility(View.GONE);
                    }



                    /*visibleText = visibleText.substring(0, ellipsizedTextLength);
                    int spanStart = visibleText.length() + 3;
                    visibleText = visibleText + "..." + getString(R.string.more);
                    tv_venue_description.setText(visibleText);
                    SpannableString spannableString = new SpannableString(tv_venue_description.getText());
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View view) {

                            addShowLessButton();
                        }
                    };
                    spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_venue_description.setMovementMethod(LinkMovementMethod.getInstance());
                    tv_venue_description.setText(spannableString, TextView.BufferType.SPANNABLE);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void addShowLessButton() {
        tv_venue_description.setMaxLines(10000);
        tv_venue_description.setText(Html.fromHtml(pojoGetVenueDetailsResponse.data.venueData.description));
        tv_show_more.setVisibility(View.VISIBLE);
        tv_show_more.setText(getString(R.string.show_less));
        tv_show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShowMoreButton();
            }
        });
       /* tv_venue_description.invalidate();
        int spanStart = visibleText.length() ;
        visibleText = visibleText +  getString(R.string.show_less);
        tv_venue_description.setText(visibleText);
        SpannableString spannableString = new SpannableString(visibleText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                addShowMoreButton();
            }
        };
        spannableString.setSpan(clickableSpan, spanStart, visibleText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_venue_description.setMovementMethod(LinkMovementMethod.getInstance());
        tv_venue_description.setText(spannableString, TextView.BufferType.SPANNABLE);*/
    }


    private void setUpMapView(View baseLayout, Bundle savedInstanceState) {

        mv_venue_location = (MapView) baseLayout.findViewById(R.id.mv_venue_location);
        mv_venue_location.onCreate(savedInstanceState);

        mv_venue_location.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mv_venue_location.getMapAsync(this);


        /*mapViewInScrollViewTouchListener = new MapViewInScrollViewTouchListener(this, mv_venue_location);
        mv_venue_location.setOnTouchListener(mapViewInScrollViewTouchListener);*/

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // latitude and longitude
        double latitude = Double.parseDouble(pojoGetVenueDetailsResponse.data.venueData.latitude);
        double longitude = Double.parseDouble(pojoGetVenueDetailsResponse.data.venueData.longitude);

        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title(pojoGetVenueDetailsResponse.data.venueData.venueName);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker).showInfoWindow();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }


    private boolean canUpdateData() {
        for (int i = 0; i < pojoGetVenueDetailsResponse.data.teams.size(); i++) {
            if (pojoGetVenueDetailsResponse.data.teams.get(i).userId.equals(user_id)
                    &&pojoGetVenueDetailsResponse.data.teams.get(i).role.equals("Ambassadors")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mv_venue_location.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mv_venue_location.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mv_venue_location.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mv_venue_location.onLowMemory();
    }


}
