package com.avadna.luneblaze.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionPhotoGalleryAdapter;

public class SessionPhotoGalleryActivity extends AppBaseActivity {

    RecyclerView rv_session_photos;
    OrganisedSessionPhotoGalleryAdapter organisedSessionPhotoGalleryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_photo_gallery);
        initGallery();
    }

    private void initGallery() {
        /*final List<String> links;
        links=new ArrayList<>();
        links.add("https://www.compareprix.in/images/resizeimages/product/128/glide-bikes-go-glider-16-inch-bicycle-large.jpg?v=72");
        links.add("https://auto.ndtvimg.com/car-images/big/dc/avanti/dc-avanti.jpg?v=26");
        links.add("https://auto.ndtvimg.com/car-images/gallery/bmw/x6/exterior/bmw-x6-front-front-new.jpg?v=2016-09-26");
        links.add("https://hips.hearstapps.com/amv-prod-cad-assets.s3.amazonaws.com/wp-content/uploads/2016/12/2017-EC-Sedans-Mercedes-Benz-S-class.jpg?crop=1xw:1xh;center,center&resize=800:*");
        links.add("https://auto.ndtvimg.com/car-images/medium/audi/new-a8/audi-new-a8.jpg?v=2");

        rv_session_photos=(RecyclerView)findViewById(R.id.rv_session_photos);
        organisedSessionPhotoGalleryAdapter =new OrganisedSessionPhotoGalleryAdapter(20,this,links);

        RecyclerView.LayoutManager photoLayoutManager = new GridLayoutManager(this, 3);
        rv_session_photos.setLayoutManager(photoLayoutManager);
        rv_session_photos.setAdapter(organisedSessionPhotoGalleryAdapter);*/
    }
}
