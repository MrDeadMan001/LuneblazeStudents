package com.avadna.luneblaze.adapters.session.organised;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Sunny on 06-01-2018.
 */

public class OrganisedSessionNotesGalleryAdapter extends RecyclerView.Adapter<OrganisedSessionNotesGalleryAdapter.MyViewHolder> {
    //  pivate List<String> hierarchyList;
    Context context;
    List<PojoSessionFile> notesList = new ArrayList<>();
    String session_id;

    int STORAGE_PERMISSION_REQUEST = 3456;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_file_type;
        TextView tv_file_name;
        LinearLayout ll_parent_wrapper;

        MyViewHolder(View view) {
            super(view);
            tv_file_type = (TextView) view.findViewById(R.id.tv_file_type);
            tv_file_name = (TextView) view.findViewById(R.id.tv_file_name);
            ll_parent_wrapper= (LinearLayout) view.findViewById(R.id.ll_parent_wrapper);
        }
    }

    public void setSession_id(String session_id){
        this.session_id=session_id;
    }

    public OrganisedSessionNotesGalleryAdapter(List<PojoSessionFile> notesList, Context context) {
        this.notesList = notesList;
        this.context = context;
        this.session_id = session_id;
        //  this.hierarchyList = hierarchyList;
    }

    @Override
    public OrganisedSessionNotesGalleryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_gallery_notes_item, parent, false);

        return new OrganisedSessionNotesGalleryAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OrganisedSessionNotesGalleryAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        holder.tv_file_name.setText("Notes " + (position + 1));
        holder.tv_file_type.setText("Notes " + (position + 1));

        holder.ll_parent_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission(STORAGE_PERMISSION_REQUEST, position);
            }
        });

    }

    private void requestStoragePermission(int requestType, int position) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestType);
        } else {

            if (requestType == STORAGE_PERMISSION_REQUEST) {
                downloadFile(position);
            }
        }
    }

    public void downloadFile(int position) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Luneblaze/Luneblaze_session_notes/");
        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()
                    +"/Luneblaze/Luneblaze_session_notes/");
            wallpaperDirectory.mkdirs();
        }
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(notesList.get(position).file);
        String filename="Luneblaze_session_notes" + session_id +position+ "."+fileExtension;
        File file = new File(direct, filename);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.setDataAndType(fileUri, "application/"+fileExtension);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(notesList.get(position).file))
                    .setTitle("Session Notes")// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            long downloadID = downloadManager.enqueue(request);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
        //  return hierarchyList.size();
    }
}

