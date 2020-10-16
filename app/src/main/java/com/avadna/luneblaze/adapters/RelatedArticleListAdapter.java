package com.avadna.luneblaze.adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponseData;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Sunny on 23-01-2018.
 */
public class RelatedArticleListAdapter extends RecyclerView.Adapter<RelatedArticleListAdapter.MyViewHolder>{
    //  pivate List<String> hierarchyList;
    int listSize;
    List<PojoSuggestedArticlesResponseData> suggestedArticles;
    Activity activity;
    CommonFunctions commonFunctions;

    public RelatedArticleListAdapter(Activity activity, List<PojoSuggestedArticlesResponseData> suggestedArticles) {
        this.activity=activity;
        this.suggestedArticles=suggestedArticles;
        commonFunctions=new CommonFunctions(activity);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_article_name;
        TextView tv_article_writer;
        TextView tv_time_stamp;
        ImageView iv_cover_photo;

        MyViewHolder(View view) {
            super(view);
            tv_article_name= (TextView) view.findViewById(R.id.tv_article_name);
            tv_article_writer= (TextView) view.findViewById(R.id.tv_article_writer);
            tv_time_stamp= (TextView) view.findViewById(R.id.tv_time_stamp);
            iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
        }
    }

    @Override
    public RelatedArticleListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.related_article_list_item, parent, false);

        return new RelatedArticleListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RelatedArticleListAdapter.MyViewHolder holder, int position) {
        //   holder.tv_hierarchy_item.setText(hierarchyList.get(position));
        final PojoSuggestedArticlesResponseData currentItem=suggestedArticles.get(position);
        holder.tv_article_name.setText(Html.fromHtml(currentItem.title));
        holder.tv_article_writer.setText(activity.getString(R.string.written_by)+" : "+currentItem.createdBy.userFullname);
        holder.tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(currentItem.addedOn));
        if(activity!=null) Glide.with(activity.getApplicationContext()).load(currentItem.coverPhoto).into(holder.iv_cover_photo);

        holder.iv_cover_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent articleIntent=new Intent(activity,ArticleActivity.class);
                articleIntent.putExtra("id",currentItem.articlesId);
                activity.startActivity(articleIntent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return suggestedArticles.size();
        //  return hierarchyList.size();
    }
}

