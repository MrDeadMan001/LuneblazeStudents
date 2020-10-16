package com.avadna.luneblaze.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.StrictMode;
import androidx.core.content.FileProvider;
import android.text.Html;
import android.util.Log;

import com.avadna.luneblaze.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ShareContentOnMediaApps {

    public static void shareContent(Context context, String type, String url,String title, Bitmap bitmap) {
        PreferenceUtils preferenceUtils=new PreferenceUtils(context);
        switch (type) {
            case AppKeys.SHARE_TO_POST: {
                url = "posts/" + url;
                url = AES.encrypt(url, AppKeys.enKey);
                String urlToShare = AppKeys.WEBSITE_URL + url;
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, Html.fromHtml(title));
                urlToShare = context.getString(R.string.check_post_on_luneblaze) + urlToShare;
                share.putExtra(Intent.EXTRA_TEXT, urlToShare);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share_post)));
            }
            break;

            case AppKeys.SHARE_SESSION: {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                url = "session/" + url;
                url = AppKeys.WEBSITE_URL + AES.encrypt(url, AppKeys.enKey);
                String textToShare = context.getString(R.string.you_might_interest_session)+ " \"" + Html.fromHtml(title) +
                        "\" "+context.getString(R.string.on_luneblaze_dot)+" \n" + url;

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Uri bmpUri = saveImage(bitmap,context);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_this_on_luneblaze));
               // share.putExtra(Intent.EXTRA_Html.fromHtml(title), context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_session)));
            }

            break;

            case AppKeys.SHARE_SESSION_GALLERY_IMAGE: {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                url = "session_photo/" + url;
                url = AppKeys.WEBSITE_URL + AES.encrypt(url, AppKeys.enKey);
            /*    String textToShare = context.getString(R.string.you_might_session_photo)+ " \"" + Html.fromHtml(title) +
                        "\" "+context.getString(R.string.on_luneblaze_dot)+" \n" + url;*/

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Uri bmpUri = saveImage(bitmap,context);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
               // share.putExtra(Intent.EXTRA_TEXT, textToShare);
                // share.putExtra(Intent.EXTRA_Html.fromHtml(title), context.getString(R.string.check_this_on_luneblaze));
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_image)));
            }

            break;

            case AppKeys.SHARE_USER: {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                url = "user/" + url;
                url = AppKeys.WEBSITE_URL + AES.encrypt(url, AppKeys.enKey);
                String textToShare = context.getString(R.string.check_the_profile_of)+ " \"" + Html.fromHtml(title) +
                        " \""+context.getString(R.string.on_luneblaze_dot)+" \n" + url;

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Uri bmpUri = saveImage(bitmap,context);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_this_on_luneblaze));
                // share.putExtra(Intent.EXTRA_Html.fromHtml(title), context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_profile)));
            }
            break;

            case AppKeys.SHARE_ARTICLE: {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                url = "article/" + url;
                url = AppKeys.WEBSITE_URL + AES.encrypt(url, AppKeys.enKey);
                String textToShare = context.getString(R.string.you_might_like_article)+ " \"" + Html.fromHtml(title) +
                        " \""+context.getString(R.string.on_luneblaze_dot)+" \n" + url;

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Uri bmpUri = saveImage(bitmap,context);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_this_on_luneblaze));
                // share.putExtra(Intent.EXTRA_Html.fromHtml(title), context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_article)));
            }
            break;

            case AppKeys.SHARE_INTEREST: {
                url = "interest/" + url;
                url = AES.encrypt(url, AppKeys.enKey);

                String urlToShare = AppKeys.WEBSITE_URL + url;

                String textToShare = context.getString(R.string.you_might_like_interest)+ " \"" + Html.fromHtml(title) +
                        " \""+context.getString(R.string.on_luneblaze_dot)+" \n" + urlToShare;

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_interest)));
            }
            break;

            case AppKeys.SHARE_QUESTION: {
                url = "question/" + url;
                url = AES.encrypt(url, AppKeys.enKey);
                String urlToShare = AppKeys.WEBSITE_URL + url;
                String textToShare=context.getString(R.string.would_you_like_to_answer)+" \""+Html.fromHtml(title)+
                        " \" "+context.getString(R.string.on_luneblaze_dot)+"? \n"+urlToShare;

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT,  context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share_question)));
            }
            break;

            case AppKeys.SHARE_ANSWER: {
                url = "question/" + url;
                url = AES.encrypt(url, AppKeys.enKey);
                String urlToShare = AppKeys.WEBSITE_URL + url;

                String textToShare=context.getString(R.string.you_might_like_this_answer)+" \""+Html.fromHtml(title)+
                        " \" "+context.getString(R.string.on_luneblaze_dot)+" \n"+urlToShare;

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT,  context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share_answer)));
            }
            break;

            case AppKeys.SHARE_VENUE: {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                url = "venue/" + url;
                url = AppKeys.WEBSITE_URL + AES.encrypt(url, AppKeys.enKey);
                String textToShare=context.getString(R.string.attend_session_at_this_venue)+" \""+Html.fromHtml(title)+
                        " \" . \n"+url;

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Uri bmpUri = saveImage(bitmap,context);
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                share.putExtra(Intent.EXTRA_STREAM, bmpUri);
                share.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.check_this_on_luneblaze));
                // share.putExtra(Intent.EXTRA_Html.fromHtml(title), context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, textToShare);
                context.startActivity(Intent.createChooser(share,  context.getString(R.string.share_venue)));
            }

            break;

            case AppKeys.SHARE_TEXT: {
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                share.putExtra(Intent.EXTRA_SUBJECT,  context.getString(R.string.check_this_on_luneblaze));
                share.putExtra(Intent.EXTRA_TEXT, url);
                context.startActivity(Intent.createChooser(share, context.getString(R.string.share_answer)));
            }
            break;
        }
    }



    private static Uri saveImage(Bitmap image,Context context) {
        //TODO - Should be processed in another thread
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.jpg");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.avadna.luneblaze.fileprovider", file);

        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }
}
