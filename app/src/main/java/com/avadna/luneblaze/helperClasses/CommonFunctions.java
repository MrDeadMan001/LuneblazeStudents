package com.avadna.luneblaze.helperClasses;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.activities.WebViewActivity;
import com.avadna.luneblaze.activities.normalpost.NormalPostActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostComment;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.avadna.luneblaze.helperClasses.FileUtils.getPath;

/**
 * Created by Sunny on 16-03-2018.
 */

public class CommonFunctions {
    Activity activity;
    ProgressDialog progressDialog;
    Context context;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    FirebaseAnalytics firebaseAnalytics;


    public CommonFunctions(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(context);
        user_id = preferenceUtils.get_user_id();
        firebaseAnalytics= FirebaseAnalytics.getInstance(activity);
    }

    public CommonFunctions(Context context) {
        this.context = context;
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(context);
        user_id = preferenceUtils.get_user_id();
        firebaseAnalytics= FirebaseAnalytics.getInstance(context);
    }

    public void logFirebaseEvent(String eventType,ArrayList<KeyValuePair> pairs){
        Bundle bundle = new Bundle();
        for(int i=0;i<pairs.size();i++){
            KeyValuePair pair=pairs.get(i);
            bundle.putString(pair.key,pair.value);
        }
        firebaseAnalytics.logEvent(eventType, bundle);
    }


    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                String[] ids = AppKeys.channel_ids;
                String[] names = context.getResources().getStringArray(R.array.channel_names);
                String[] descriptions = context.getResources().getStringArray(R.array.channel_descriptions);
                for (int i = 0; i < ids.length; i++) {
                    NotificationChannel channel = new NotificationChannel(ids[i], names[i],
                            NotificationManager.IMPORTANCE_HIGH);
                    channel.setDescription(descriptions[i]);
                    Uri soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
                    channel.setSound(soundUri, null);
                    channel.enableVibration(true);
                    notificationManager.createNotificationChannel(channel);
                }
            }
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
        }
    }



/*    public String getNotificationChannelId() {
        if (preferenceUtils.getSoundSettings()) {
            if (preferenceUtils.getVibrationSettings()) {
                return AppKeys.CHANNEL_ID_SOUND_ON_VIBRATION_ON;
            } else {
                return AppKeys.CHANNEL_ID_SOUND_ON_VIBRATION_OFF;
            }
        } else {
            if (preferenceUtils.getVibrationSettings()) {
                return AppKeys.CHANNEL_ID_SOUND_OFF_VIBRATION_ON;
            } else {
                return AppKeys.CHANNEL_ID_SOUND_OFF_VIBRATION_OFF;
            }
        }
    }*/

    public boolean isGuestUser() {
       return user_id==null||user_id.equals(AppKeys.GUEST_USER_ID);
    }

    public boolean is_internet_connected() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public void openProgressDialog(String message) {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(activity, "", message, true);
            }
        } else {
            progressDialog = ProgressDialog.show(activity, "", message, true);
        }
    }

    public int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public void openProgressDialog() {
        if (progressDialog != null) {
            if (!progressDialog.isShowing()) {
                progressDialog = ProgressDialog.show(activity, "",
                        context.getString(R.string.please_wait), true);
            }
        } else {
            progressDialog = ProgressDialog.show(activity, "",
                    context.getString(R.string.please_wait), true);
        }

    }

    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public void showNotFoundDialog(final Activity activity) {
        Button bt_ok;
        final Dialog confirmationDialog;
        TextView tv_not_found;
        confirmationDialog = new MyCustomThemeDialog(activity, R.style.AppTheme);
        confirmationDialog.setContentView(R.layout.not_found_dialog);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
        tv_not_found = (TextView) confirmationDialog.findViewById(R.id.tv_not_found);
        tv_not_found.setText(activity.getString(R.string.post_deleted_or_no_permission));
        confirmationDialog.getWindow().setDimAmount(0);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }

    public void showLoginRequestDialog() {
        String message = activity.getString(R.string.please_login_to_perform_this_action);

        TextView tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(activity);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);

        TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_dialog_description.setText(Html.fromHtml(message));

        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_no.setText(activity.getString(R.string.cancel));

        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_yes.setText(activity.getString(R.string.login));

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(activity, LoginActivity.class);
                activity.startActivity(loginActivityIntent);
                confirmationDialog.dismiss();
            }
        });

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        confirmationDialog.setCancelable(true);
        confirmationDialog.show();
    }

    public ImageDimensions getScaledDimensions(DisplayMetrics displayMetrics,String imgDimen, String type) {

        int maxArticleImageHeight;
        int maxSessionImageHeight;
        int maxNormalPostImageHeight;

        maxArticleImageHeight = (int) (displayMetrics.widthPixels * (3f / 5));
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * (2f / 3));
        maxNormalPostImageHeight = (int) (displayMetrics.heightPixels * (5f / 6));

        ImageDimensions dimen = new ImageDimensions();

        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));

        if (type.equals(AppKeys.NORMAL)) {
            width = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            height = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        }

        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);


        switch (type) {
            case AppKeys.ARTICLE:
                if (targetHeight > maxArticleImageHeight) {
                    targetHeight = maxArticleImageHeight;
                }
                break;

            case AppKeys.SESSION:
                if (targetHeight > maxSessionImageHeight) {
                    targetHeight = maxSessionImageHeight;
                }
                break;

            case AppKeys.VERIFIED_SESSION:
                float px = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        16,
                        activity.getResources().getDisplayMetrics()
                );
                targetWidth = (int) ((displayMetrics.widthPixels) - px);
                targetHeight = (int) (1f * targetWidth / imgRatio);
                if (targetHeight > maxSessionImageHeight) {
                    targetHeight = maxSessionImageHeight;
                }
                break;

            case AppKeys.NORMAL:
                if (targetHeight > maxSessionImageHeight) {
                    targetHeight = maxSessionImageHeight;
                }
                break;

        }

        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }


    public void openImageViewerDialog(String path) {
        final Dialog imageDialog;
        imageDialog = new MyCustomThemeDialog(activity, R.style.AppTheme);
        imageDialog.setContentView(R.layout.image_viewer_dialog);
        ImageView iv_photo, iv_back, iv_edit;

        iv_photo = (ImageView) imageDialog.findViewById(R.id.iv_photo);
        iv_back = (ImageView) imageDialog.findViewById(R.id.iv_back);
        iv_edit = (ImageView) imageDialog.findViewById(R.id.iv_edit);
        iv_edit.setVisibility(View.GONE);

        Glide.with(context).load(path).into(iv_photo);

        imageDialog.getWindow().setDimAmount(0);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDialog.dismiss();
            }
        });
        imageDialog.show();

    }

    public void showSessionDeactivated(final Activity activity) {
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(activity, R.style.AppTheme);
        confirmationDialog.setContentView(R.layout.session_deactivated_dialog);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
        confirmationDialog.getWindow().setDimAmount(0);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                confirmationDialog.dismiss();
            }
        });
        confirmationDialog.show();
    }


    public void showMessageDialog(String message) {
        if (activity != null) {
            Button bt_ok;
            final Dialog confirmationDialog;
            confirmationDialog = new MyCustomThemeDialog(activity);
            confirmationDialog.setContentView(R.layout.message_dialog);

            TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
            tv_dialog_description.setText(message);

            bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationDialog.dismiss();
                }
            });
            confirmationDialog.show();
        }
    }

    public void showMessageDialogAndExit(String message) {
        if (activity != null) {
            Button bt_ok;
            final Dialog confirmationDialog;
            confirmationDialog = new MyCustomThemeDialog(activity);
            confirmationDialog.setContentView(R.layout.message_dialog);

            TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
            tv_dialog_description.setText(message);
            bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmationDialog.dismiss();
                    activity.finish();
                }
            });
            confirmationDialog.setCancelable(false);
            confirmationDialog.show();
        }
    }

    public void urlLoader(String url) {
        Intent intent = null;
        Intent chooser = null;
        if (!url.contains("http")) {
            url = "http://" + url;
        }
        //if its luneblaze based url
        if (url.contains("www.luneblaze.com")) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            PackageManager packageManager = context.getPackageManager();
            chooser = Intent.createChooser(intent, context.getString(R.string.open_with));
            //if no app can handle the url
            if (intent.resolveActivity(packageManager) == null) {
                //if its valid url then open in webviewer
                if (URLUtil.isNetworkUrl(url)) {
                    intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra(AppKeys.URL, url);
                } else {
                    intent = null;
                }
            }
        }

        if (chooser != null && intent != null) {
            if (activity != null) {
                activity.startActivity(chooser);
            } else {
                chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(chooser);
            }
        } else if (intent != null) {
            if (activity != null) {
                activity.startActivity(intent);
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }

    public List<PojoNormalPostComment> removeReportedPostComments(List<PojoNormalPostComment> comments) {
        List<PojoNormalPostComment> moddedComments = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).iReported == 0) {
                moddedComments.add(comments.get(i));
            }
        }
        return moddedComments;
    }

    public void setToastMessage(Context context, String message, int duration, int type) {
        try {
            if (type == AppKeys.TOAST_DEBUG) {
                // Toast.makeText(context, message, duration).show();
            } else if (type == AppKeys.TOAST_USER) {
                Toast.makeText(context.getApplicationContext(), message, duration).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean areDatesSame(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        if (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) {
            return true;
        } else {
            return false;
        }
    }


    public File getScaledDownImage(String path) {
// we'll start with the original picture already open to a file
        File imgFileOrig = new File(path); //change "getPic()" for whatever you need to open the image file.
        String absPath = imgFileOrig.getAbsolutePath();
        Bitmap b = BitmapFactory.decodeFile(absPath);
// original measurements
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        final int destWidth = 720;//or the width you need

        if (origWidth > destWidth) {
            // picture is wider than we want it, we calculate its target height
            int destHeight = (int) ((1f * destWidth * origHeight) / origWidth);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
            // we save the file, at least until we have made use of it

            Calendar cal = Calendar.getInstance();
            File f = new File(context.getCacheDir(), cal.getTime() + "temp_image_scale.jpg");

            try {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //write the bytes in file
            FileOutputStream fo = null;
            try {
                fo = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                fo.write(outStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            // remember close de FileOutput
            try {
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return f;
        }
        return imgFileOrig;
    }


    public Uri getImageContentUri(Context context, File file) {
        String absPath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                , new String[]{MediaStore.Images.Media._ID}
                , MediaStore.Images.Media.DATA + "=? "
                , new String[]{absPath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id));

        } else if (!absPath.isEmpty()) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, absPath);
            return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            return null;
        }
    }

   /* public Uri getImageContentUri(Context context, File file) {
        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri
        ContentResolver cr = context.getContentResolver();
        String imagePath = file.getAbsolutePath();
        String imageName = null;
        String imageDescription = null;
        String uriString = null;
        try {
            uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return Uri.parse(uriString);
    }*/

    public String getMimeType(Uri contentUri) {
        if (contentUri == null) {
            return "image/jpeg";
        }
        String mimeType = context.getContentResolver().getType(contentUri);
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }
        return mimeType;
    }

    public String getMimeType(File file) {

        String mimeType = null;
        try {
            mimeType = context.getContentResolver().getType(getImageContentUri(context, file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }
        return mimeType;
    }

    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public String getPathFromUri(final Uri uri) {
        MyFileUtils myFileUtils=new MyFileUtils(context);
        return  myFileUtils.getPath(uri);
        //return getPath(context,uri);

        /*final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
                *//*else {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }*//*
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);

                //////
                *//*String paths[]=new String[]{"content://downloads/public_downloads"
                        ,"content://downloads/my_downloads"};
                for(int i=0;i<paths.length;i++){
                    long longId=-1;
                    try{
                        longId=Long.parseLong(id);
                    }
                    catch (Exception e){

                    }
                    if(longId!=-1){
                        final Uri contentUri = ContentUris.withAppendedId(
                                Uri.parse(paths[i]), Long.parseLong(id));
                        String path=getDataColumn(context, contentUri, null, null);
                        if(path!=null){
                            return path;
                        }
                    }
                }


                // Path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                val fileName = getFileName(context, uri)
                val cacheDir = getDocumentCacheDirectory(context)
                val file = generateFileName(fileName, cacheDir)
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }

                return destinationPath*//*
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosUri(uri)) {
                 return  uri.getLastPathSegment();
            }
            else {
                return getDataColumn(context, uri, null, null);
            }
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;*/
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static String convertMinToHours(String mins) {
        int minutes = Integer.parseInt(mins);
        int hrs = minutes / 60;
        minutes = minutes % 60;

        return "" + hrs + "hrs " + minutes + "mins";
    }

    public Date convertToLocalTime(String time) {
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        source.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date = null;
        try {
            date = source.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        TimeZone toTimeZone = calendar.getTimeZone();

        source.setTimeZone(toTimeZone);
        String newDateStr = "";
        try {
            newDateStr = source.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            date = source.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public String parseDateToName(String time) {
        if (time == null || time.isEmpty()) {
            return time;
        }
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = source.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null) {
            return time;
        }

        SimpleDateFormat target = new SimpleDateFormat("dd MMM yyyy");
        String newDateStr = target.format(date);

        return newDateStr;

    }

    public String parseDateAndTimeToName(String time) {
        if (time == null || time.isEmpty()) {
            return time;
        }
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        try {
            date = source.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat target = new SimpleDateFormat("hh:mm a dd MMM yyyy");
        String newDateStr = target.format(date);

        return newDateStr;

    }


    public String parseMessageSeenTime(String time) {
        if (time == null) {
            return "";
        }
        if (time.isEmpty()) {
            return time;
        }
        Date date = convertToLocalTime(time);
        SimpleDateFormat target = new SimpleDateFormat("hh:mm:ss a dd MMM yyyy");

        return target.format(date);
    }


    public String parseNewsFeedItemTime(String time) {

        if (time != null) {
            if (time.equals(activity.getString(R.string.just_now))) {
                return activity.getString(R.string.just_now);
            }
            if (time.isEmpty()) {
                return "";
            }
            Date date = convertToLocalTime(time);

            Date currentDate = Calendar.getInstance().getTime();
            int secondDifference = ((int) ((currentDate.getTime() / (1000))
                    - (int) (date.getTime() / (1000))));
            if (secondDifference <= 0) {
                secondDifference = 1;
            }
            int minuteDifference = secondDifference / 60;
            int hrDifference = minuteDifference / 60;
            int dayDiff = hrDifference / 24;


            int currentMins = currentDate.getHours() * 60 + currentDate.getMinutes();


            if (hrDifference == 0) {
                if (minuteDifference == 0) {
                    time = secondDifference + " " + activity.getString(R.string.sec);
                } else {
                    time = minuteDifference + " " + activity.getString(R.string.min);
                }
            } else if (hrDifference < 24) {
                if (currentMins - minuteDifference < 0) {
                    time = activity.getString(R.string.yesterday);
                    SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                    try {
                        time = time + " " + target.format(date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (hrDifference <= 1) {
                        time = hrDifference + " " + activity.getString(R.string.hr);
                    } else {
                        time = hrDifference + " " + activity.getString(R.string.hrs);
                    }
                }

            } else if (hrDifference >= 24 && hrDifference < 48) {
                if (currentMins + (60 * 24) - minuteDifference < 0) {
                    SimpleDateFormat target = new SimpleDateFormat("hh:mm a dd-MMM");
                    try {
                        time = target.format(date);
                        time = time.replace("-", " ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    time = activity.getString(R.string.yesterday);
                    SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                    try {
                        time = time + " " + target.format(date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SimpleDateFormat target;
                if (currentDate.getYear() != date.getYear()) {
                    target = new SimpleDateFormat("hh:mm a dd-MMM-yyyy");

                } else {
                    target = new SimpleDateFormat("hh:mm a dd-MMM");
                }
                try {
                    time = target.format(date);
                    time = time.replace("-", " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        }
        return time;

    }


    public String parseNewsFeedItemTimeWithoutLocalConversion(String time) {

        if (time != null) {
            if (time.equals(activity.getString(R.string.just_now))) {
                return activity.getString(R.string.just_now);
            }

            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = null;
            try {
                date = source.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date currentDate = Calendar.getInstance().getTime();
            int minuteDifference =
                    ((int) ((currentDate.getTime() / (60 * 1000))
                            - (int) (date.getTime() / (60 * 1000))));
            int hrDifference = minuteDifference / 60;
            int dayDiff = hrDifference / 24;


            int currentMins = currentDate.getHours() * 60 + currentDate.getMinutes();


            if (hrDifference == 0) {
                time = minuteDifference + " " + activity.getString(R.string.min);
            } else if (hrDifference < 24) {
                if (currentMins - minuteDifference < 0) {
                    time = activity.getString(R.string.yesterday);
                    SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                    try {
                        time = time + " " + target.format(date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (hrDifference <= 1) {
                        time = hrDifference + " " + activity.getString(R.string.hr);
                    } else {
                        time = hrDifference + " " + activity.getString(R.string.hrs);
                    }
                }

            } else if (hrDifference >= 24 && hrDifference < 48) {
                if (currentMins + (60 * 24) - minuteDifference < 0) {
                    SimpleDateFormat target = new SimpleDateFormat("dd-MMM hh:mm a");
                    try {
                        time = target.format(date);
                        time = time.replace("-", " ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    time = activity.getString(R.string.yesterday);
                    SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                    try {
                        time = time + " " + target.format(date);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SimpleDateFormat target = new SimpleDateFormat("dd-MMM hh:mm a");
                try {
                    time = target.format(date);
                    time = time.replace("-", " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return time;

    }

    public String allMsgListTimeParser(String time, String type) {
        if (time == null || time.isEmpty()) {
            return time;
        }
        Date date = convertToLocalTime(time);

        Date currentDate = Calendar.getInstance().getTime();

        int difference =
                ((int) ((currentDate.getTime() / (24 * 60 * 60 * 1000))
                        - (int) (date.getTime() / (24 * 60 * 60 * 1000))));


        if (difference < 1) {
            SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
            try {
                time = target.format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (difference == 1) {
            time = activity.getString(R.string.yesterday);
        } else {
            SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
            try {
                time = target.format(date);
                time = time.replace("-", " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return time;

    }


    /*public String getChatDividerTime(String time) {
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        source.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date date = null;
        try {
            date = source.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        TimeZone toTimeZone = calendar.getTimeZone();


        source.setTimeZone(toTimeZone);
        String newDateStr = source.format(date);

        try {
            date = source.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date currentDate = Calendar.getInstance().getTime();
        int minuteDifference =
                ((int) ((currentDate.getTime() / ( 60 * 1000))
                        - (int) (date.getTime() / ( 60 * 1000))));
        int hrDifference=minuteDifference/60;
        int dayDiff=hrDifference/24;

        int currentMins=currentDate.getHours()*60+currentDate.getMinutes();


        Log.d("time", "local=" + newDateStr);


        if (hrDifference < 24) {
            if(currentMins-minuteDifference<0){
                time= "Yesterday";
            }
            else {
                SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                try {
                    time= target.format(date);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else if (hrDifference >= 24&&hrDifference<48) {
            if(currentMins+(60*24)-minuteDifference<0){
                SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
                try {
                    time= target.format(date);
                    time=time.replace("-"," ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                time= "Yesterday";
            }
        }
        else {
            SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
            try {
                time= target.format(date);
                time=time.replace("-"," ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;

    }*/

    public String parseLastSeenTime(String time) {

        if (time == null || time.isEmpty()) {
            return time;
        }
        Date date = convertToLocalTime(time);

        Date currentDate = Calendar.getInstance().getTime();
        int minuteDifference =
                ((int) ((currentDate.getTime() / (60 * 1000))
                        - (int) (date.getTime() / (60 * 1000))));
        int hrDifference = minuteDifference / 60;
        int dayDiff = hrDifference / 24;

        int currentMins = currentDate.getHours() * 60 + currentDate.getMinutes();


        if (hrDifference < 24) {
            if (currentMins - minuteDifference < 0) {
                time = activity.getString(R.string.yesterday);
            }
            SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
            try {
                time = target.format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (hrDifference >= 24 && hrDifference < 48) {
            if (currentMins + (60 * 24) - minuteDifference < 0) {
                SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
                try {
                    time = target.format(date);
                    time = time.replace("-", " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                time = activity.getString(R.string.yesterday);
                SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
                try {
                    time = time + " " + target.format(date);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            SimpleDateFormat target = new SimpleDateFormat("dd-MMM hh:mm a");
            try {
                time = target.format(date);
                time = time.replace("-", " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;

    }

    public String getChatDividerTime(String time) {
        if (time == null) {
            time = "";
        }

        if (!time.isEmpty() && !time.equals(context.getString(R.string.just_now))) {

            Date date = convertToLocalTime(time);

            Date currentDate = Calendar.getInstance().getTime();
            int minuteDifference =
                    ((int) ((currentDate.getTime() / (60 * 1000))
                            - (int) (date.getTime() / (60 * 1000))));
            int hrDifference = minuteDifference / 60;
            int dayDiff = hrDifference / 24;

            int currentMins = currentDate.getHours() * 60 + currentDate.getMinutes();


            if (hrDifference < 24) {
                if (currentMins - minuteDifference < 0) {
                    time = activity.getString(R.string.yesterday);
                } else {
                    time = activity.getString(R.string.today);
                }
            } else if (hrDifference >= 24 && hrDifference < 48) {
                if (currentMins + (60 * 24) - minuteDifference < 0) {
                    SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
                    try {
                        time = target.format(date);
                        time = time.replace("-", " ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    time = activity.getString(R.string.yesterday);
                }
            } else {
                SimpleDateFormat target = new SimpleDateFormat("dd-MMM");
                try {
                    time = target.format(date);
                    time = time.replace("-", " ");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return time;
        } else {
            return time;
        }

    }

    public String getChatActivityItemTime(String time) {

        if (!time.equals(context.getString(R.string.just_now))) {
            Date date = convertToLocalTime(time);

            SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
            try {
                time = target.format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return time;

        } else {
            return time;
        }

    }

    public String getChatActivityItemTimeWithoutLocalConversion(String time) {

        if (!time.equals(context.getString(R.string.just_now))) {
            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = null;
            try {
                date = source.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat target = new SimpleDateFormat("hh:mm a");
            try {
                time = target.format(date);

            } catch (Exception e) {
                e.printStackTrace();
            }


            return time;

        } else {
            return time;
        }

    }


    public void openReportDialog(final String user_id, final String type, final String id) {
        final Dialog dialog = new MyCustomThemeDialog(activity);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(activity.getString(R.string.report_reason_description));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void hitReportContentApi(String user_id, String report_type, String content_id, String reason) {
        Call<PojoNoDataResponse> call = apiService.reportContent(user_id, report_type,
                content_id, reason);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    setToastMessage(context, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                setToastMessage(context, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void hitPostReactionApi(String user_id, String id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.postReaction(user_id, id, reaction);
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                if (response != null && response.body() != null) {
                    String message = "";
                    if (reaction.contains("delete")) {
                        message = context.getString(R.string.deleted_successfully);
                        if (response.body().data != null) {
                            if (context != null)
                                setToastMessage(context,
                                        "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                        } else {
                            if (context != null)
                                setToastMessage(context,
                                        "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        }
                    } else {
                        message = response.body().message;
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                if (context != null)
                    setToastMessage(context, context.getString(R.string.something_went_wrong_try_again),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            }
        });
    }


    public String parseTagString(String sendPostStr, HashMap<String, PojoUserData> taggedUserList) {
        String result = sendPostStr;
        for (Map.Entry<String, PojoUserData> entry : taggedUserList.entrySet()) {
            result = result.replace("[" + entry.getKey() + "]", entry.getValue().userFullname);
        }
        return result;
    }

    public SpannableClickItem setClickSpans(String text, String text_plain, boolean tagged, final String post_id) {

        String delimiter = "[";
        String htmlDelimiter = "<strong>";
        SpannableClickItem spannableClickItem = new SpannableClickItem();

        //to replace all anchor(<a>) tags
        text = text.replaceAll("</?a[^>]*>", "");

        SpannableString spannableString = new SpannableString(Html.fromHtml(text));
        List<ClickableSpan> clickableSpansList = new ArrayList<>();
        List<String> userIdList = new ArrayList<>();
        final List<IndexToIdListItem> indexToIdList = new ArrayList<>();
        ClickableSpan textClickableSpan;

        // text_plain=Html.fromHtml(text_plain).toString();
        if (!text.equals(text_plain) && tagged) {
            Log.d("span info", "span for post " + post_id);
            //i for plaintext
            //j for text
            text = text.replace("<br>", "");
            text = text.replace("<br />", "");

            for (int i = 0, j = 0; i < text_plain.length() && j < text.length(); i++, j++) {
                char a = text_plain.charAt(i);
                char b = text.charAt(j);
                if (text_plain.charAt(i) != text.charAt(j)) {
                    int indexOfBracket = text_plain.indexOf("]", i);
                    if (indexOfBracket > 0 && indexOfBracket < text_plain.length()) {
                        String id = text_plain.substring(i + 1, text_plain.indexOf("]", i));
                        i = i + id.length() + 2;

                        int spannedIndexStart = j - 2 * htmlDelimiter.length() * (indexToIdList.size());
                        int spannedIndexEnd = text.indexOf(htmlDelimiter, j + 1) - htmlDelimiter.length()
                                - 2 * htmlDelimiter.length() * (indexToIdList.size());

                        IndexToIdListItem indexToIdListItem = new IndexToIdListItem(j, spannedIndexStart, spannedIndexEnd, id);
                        indexToIdList.add(indexToIdListItem);
                        userIdList.add(id);

                        j = text.indexOf(htmlDelimiter, j + 1) + htmlDelimiter.length();
                    }
                }
            }

            StringBuilder str = new StringBuilder(text);

            for (int i = 0; i < indexToIdList.size(); i++) {
                str = str.replace(indexToIdList.get(i).spannedIndexStart, indexToIdList.get(i).spannedIndexStart
                        + htmlDelimiter.length(), "");
                str = str.replace(indexToIdList.get(i).spannedIndexEnd, indexToIdList.get(i).spannedIndexEnd
                        + htmlDelimiter.length(), "");

            }
            spannableString = new SpannableString(str);

            for (int i = 0; i < indexToIdList.size(); i++) {
                final int finalI = i;
                ClickableSpan userClickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent profileIntent = new Intent(activity, ProfileInfoActivity.class);
                        profileIntent.putExtra("target_user_id", indexToIdList.get(finalI).userId);
                        activity.startActivity(profileIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                    }
                };
                clickableSpansList.add(userClickableSpan);
                spannableString.setSpan(userClickableSpan, indexToIdList.get(i).spannedIndexStart
                        , indexToIdList.get(i).spannedIndexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        text = spannableString.toString();
        //  text = text.replace("\n", "");
        String[] words = text.split("\\s+");
        String emojiRegex = "([\\u20a0-\\u32ff\\ud83c\\udc00-\\ud83d\\udeff\\udbb9\\udce5-\\udbb9\\udcee])";

        spannableClickItem.clickableUrls = new ArrayList<>();
        int adjust = 0;
        for (final String word : words) {
            Matcher matchEmo = Pattern.compile(emojiRegex).matcher(word);

            boolean emojiPresent = false;
            while (matchEmo.find()) {
                emojiPresent = true;
                break;
            }

            if (!emojiPresent && Patterns.WEB_URL.matcher(word).matches()) {

                spannableClickItem.clickableUrls.add(word);
                int spanStart = text.indexOf(word, adjust);
                int spanEnd = spanStart + word.length();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        urlLoader(word);
                     /*   Intent webViewIntent = new Intent(activity, WebViewActivity.class);
                        webViewIntent.putExtra("url", word);
                        activity.startActivity(webViewIntent);*/
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                    }
                };
                clickableSpansList.add(clickableSpan);
                spannableString.setSpan(clickableSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            if (!word.isEmpty() && word.charAt(0) == '#') {
                int spanStart = text.indexOf(word);
                int spanEnd = spanStart + word.length();
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Intent searchIntent = new Intent(activity, SearchActivity.class);
                        searchIntent.putExtra("tab_type", AppKeys.POSTS);
                        searchIntent.putExtra("query", word);
                        activity.startActivity(searchIntent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                        ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                    }
                };
                clickableSpansList.add(clickableSpan);
                spannableString.setSpan(clickableSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            adjust = adjust + word.length();

        }


        //setClickSpan on rest of the text
        //this workaround is added because the movement method consumes click events on text
        if (post_id != null) {
            textClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Intent normalPostIntent = new Intent(activity, NormalPostActivity.class);
                    normalPostIntent.putExtra(AppKeys.POST_ID, post_id);
                    activity.startActivity(normalPostIntent);

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(activity, R.color.dark_grey));
                }
            };
            clickableSpansList.add(textClickableSpan);
            spannableString.setSpan(textClickableSpan, 0
                    , spannableString.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }


        //again we need to put colors on text as we just over wrote the color in above code
        for (int i = 0; i < indexToIdList.size(); i++) {
            final int finalI = i;
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat
                    .getColor(activity, R.color.app_theme_dark));
            spannableString.setSpan(foregroundColorSpan, indexToIdList.get(i).spannedIndexStart
                    , indexToIdList.get(i).spannedIndexEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (final String word : words) {

            Matcher matchEmo = Pattern.compile(emojiRegex).matcher(word);
            boolean emojiPresent = false;
            while (matchEmo.find()) {
                emojiPresent = true;
                break;
            }
            if (!emojiPresent && Patterns.WEB_URL.matcher(word).matches()) {
                int spanStart = text.indexOf(word);
                int spanEnd = spanStart + word.length();
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat
                        .getColor(activity, R.color.app_theme_dark));

                spannableString.setSpan(foregroundColorSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
            if (!word.isEmpty() && word.charAt(0) == '#') {
                int spanStart = text.indexOf(word);
                int spanEnd = spanStart + word.length();
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat
                        .getColor(activity, R.color.app_theme_dark));

                spannableString.setSpan(foregroundColorSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

        }

        spannableClickItem.clickableSpansList = clickableSpansList;
        spannableClickItem.spannableString = spannableString;

        spannableClickItem.taggedUserList = userIdList;
        spannableClickItem.indexToIdList = indexToIdList;
        return spannableClickItem;

    }


    public void hitNotificationSeenApi(String user_id, String notificationId) {

        Intent notifSeen = new Intent(Config.NOTIFICATION_SEEN);
        notifSeen.putExtra(AppKeys.NOTIFICATION_ID, notificationId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(notifSeen);

        Call<PojoNoDataResponse> call = apiService.seenNotification(user_id, notificationId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                //    commonFunctions.setToastMessage(context, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
            }
        });
    }


    public SpannableClickItem scanTextForLinks(String text) {
        List<ClickableSpan> clickableSpansList = new ArrayList<>();
        SpannableString spannableString = new SpannableString(text);

        String[] words = text.split(" ");
        for (final String word : words) {
            if (word.contains(".")) {
                int dotIndex = word.indexOf('.');
                if (dotIndex > 0 || dotIndex < word.length() - 1) {
                    int spanStart = text.indexOf(word);
                    int spanEnd = spanStart + word.length();
                    ClickableSpan clickableSpan = new ClickableSpan() {
                        @Override
                        public void onClick(View view) {
                            urlLoader(word);
                            /*Intent webViewIntent = new Intent(activity, WebViewActivity.class);
                            webViewIntent.putExtra("url", word);
                            activity.startActivity(webViewIntent);*/
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            super.updateDrawState(ds);
                            ds.setUnderlineText(false);
                            ds.setColor(ContextCompat.getColor(activity, R.color.app_theme_dark));
                        }
                    };

                    spannableString.setSpan(clickableSpan, spanStart, spanEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    clickableSpansList.add(clickableSpan);
                }
            }
        }

        SpannableClickItem spannableClickItem = new SpannableClickItem();
        spannableClickItem.clickableSpansList = clickableSpansList;
        spannableClickItem.spannableString = spannableString;
        spannableClickItem.taggedUserList = new ArrayList<>();
        return spannableClickItem;
    }


    public int splitWordsIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> currentLine = new ArrayList<>();
        String[] sources = source.split("\\s+");
        for (String chunk : sources) {
            if (paint.measureText(chunk) < maxWidthPx) {
                processFitChunk(maxWidthPx, paint, result, currentLine, chunk);
            } else {
                //the chunk is too big, split it.
                List<String> splitChunk = splitIntoStringsThatFit(chunk, maxWidthPx, paint);
                for (String chunkChunk : splitChunk) {
                    processFitChunk(maxWidthPx, paint, result, currentLine, chunkChunk);
                }
            }
        }
        if (!currentLine.isEmpty()) {
            result.add(TextUtils.join(" ", currentLine));
        }

        String findStr = "\n";
        int lastIndex = 0;
        int count = 0;

        while (lastIndex != -1) {
            lastIndex = source.indexOf(findStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += findStr.length();
            }
        }
        return result.size() + count;
    }

    /**
     * Splits a string to multiple strings each of which does not exceed the width
     * of maxWidthPx.
     */

    private List<String> splitIntoStringsThatFit(String source, float maxWidthPx, Paint paint) {
        if (TextUtils.isEmpty(source) || paint.measureText(source) <= maxWidthPx) {
            return Arrays.asList(source);
        }

        ArrayList<String> result = new ArrayList<>();
        int start = 0;
        for (int i = 1; i <= source.length(); i++) {
            String substr = source.substring(start, i);
            if (paint.measureText(substr) >= maxWidthPx) {
                //this one doesn't fit, take the previous one which fits
                String fits = source.substring(start, i - 1);
                result.add(fits);
                start = i - 1;
            }
            if (i == source.length()) {
                String fits = source.substring(start, i);
                result.add(fits);
            }
        }
        return result;
    }

    /**
     * Processes the chunk which does not exceed maxWidth.
     */
    private void processFitChunk(float maxWidth, Paint paint, ArrayList<String> result, ArrayList<String> currentLine, String chunk) {
        currentLine.add(chunk);
        String currentLineStr = TextUtils.join(" ", currentLine);
        if (paint.measureText(currentLineStr) >= maxWidth) {
            //remove chunk
            currentLine.remove(currentLine.size() - 1);
            result.add(TextUtils.join(" ", currentLine));
            currentLine.clear();
            //ok because chunk fits
            currentLine.add(chunk);
        }
    }


    public boolean isMessageSeen(PojoChatMessage pojoChatMessage) {
        if (pojoChatMessage.seendetail == null) {
            if (pojoChatMessage.seen == null || pojoChatMessage.seen.equals("0")) {
                return false;
            } else {
                return true;
            }
        } else {
            //if all have seen then the seen status is true
            if (pojoChatMessage.seendetail.get(0).notseenusers.isEmpty()) {
                return true;
            } else if (pojoChatMessage.seendetail.get(0).notseenusers.size() == 1) {
                //if its my message and only I have not seen it then seen state is true
                if (pojoChatMessage.userId.equals(user_id)
                        && pojoChatMessage.seendetail.get(0).notseenusers.get(0).equals(user_id)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isMessageSeen(PojoConversationListItem currentItem) {
        if (currentItem.seendetail == null) {
            if (currentItem.seen == null || currentItem.seen.equals("0")) {
                return false;
            } else {
                return true;
            }
        } else {
            //if all have seen then the seen status is true
            if (currentItem.seendetail.get(0).notseenusers.isEmpty()) {
                return true;
            } else if (currentItem.seendetail.get(0).notseenusers.size() == 1) {
                //if its my message and only I have not seen it then seen state is true
                if (currentItem.userId.equals(user_id)
                        && currentItem.seendetail.get(0).notseenusers.get(0).equals(user_id)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getScoreMultiplier(String type) {
        switch (type) {
            case "article":
            case "article_like":
            case "article_comment":
                return 2;

            case "article_shared":
                return 5;

            case "session_attend":
            case "session":
                return 1;

            case "session_shared":
                return 3;

            case "question":
                return 2;
            case "question_shared":
                return 5;

            case "answer_shared":
                return 5;

            case "photos":
            case "":
            case "post_like":
            case "post_comment":
            case "poll":
            case "poll_vote":
                return 1;

            case "interest_shared":
            case "venue_shared":
                return 5;

            case "shared":
                return 1;

            default:
                return 5;

        }
    }


}
