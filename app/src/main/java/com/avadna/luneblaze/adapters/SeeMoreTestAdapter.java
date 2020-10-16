package com.avadna.luneblaze.adapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.fragments.home.NotificationsListFragment;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SeeMoreTestAdapter extends RecyclerView.Adapter<SeeMoreTestAdapter.MyViewHolder> {

    private ArrayList<HashMap<String, String>> tipsArray;
    Activity activity;
    NotificationsListFragment fragment;

    List <String> texts=new ArrayList<>();

    final int SIZE=20;

    int[] indexes=new int[SIZE];

    CommonFunctions commonFunctions;



    public SeeMoreTestAdapter(Activity activity) {
        this.activity = activity;
        texts.add("Le Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards.[1] Feminist critiques of her writing were particularly influential upon Le Guin's later work.[3]");
        texts.add("Le Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards. Le Guin came to critical attention with the publication of A Wizard of Earthsea in 1968, and The Left Hand of Darkness in 1969. The Earthsea books, of which A Wizard of Earthsea was the first, have been described as Le Guin's best work by several commentators, while scholar Charlotte Spivack described The Left Hand of Darkness as having established Le Guin's reputation as a writer of science fiction.[2] Literary critic Harold Bloom referred to the books as Le Guin's masterpieces. Several scholars have called the Earthsea books Le Guin's best work.[3] Her work has received intense critical attention. As of 1999, ten volumes of literary criticism and forty dissertations had been written about her work: she was referred to by scholar Donna White as a \"major figure in American letters\".[1] Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards.[1] Feminist critiques of her writing were particularly influential upon Le Guin's later work.[3]");
        SecureRandom secureRandom = new SecureRandom();

        for(int i=0;i<SIZE;i++){
            int number = secureRandom.nextInt(2);
            indexes[i]=number;
        }
        commonFunctions=new CommonFunctions(activity);
    }

    public SeeMoreTestAdapter(NotificationsListFragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        texts.add("Le Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards.[1] Feminist critiques of her writing were particularly influential upon Le Guin's later work.[3]");
        texts.add("Le Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards. Le Guin came to critical attention with the publication of A Wizard of Earthsea in 1968, and The Left Hand of Darkness in 1969. The Earthsea books, of which A Wizard of Earthsea was the first, have been described as Le Guin's best work by several commentators, while scholar Charlotte Spivack described The Left Hand of Darkness as having established Le Guin's reputation as a writer of science fiction.[2] Literary critic Harold Bloom referred to the books as Le Guin's masterpieces. Several scholars have called the Earthsea books Le Guin's best work.[3] Her work has received intense critical attention. As of 1999, ten volumes of literary criticism and forty dissertations had been written about her work: she was referred to by scholar Donna White as a \"major figure in American letters\".[1] Her awards include the National Book Award, the Newbery Medal, and multiple Hugo and Nebula Awards.[1] Feminist critiques of her writing were particularly influential upon Le Guin's later work.[3]");
        SecureRandom secureRandom = new SecureRandom();

        for(int i=0;i<SIZE;i++){
            int number = secureRandom.nextInt(2);
            indexes[i]=number;

        }

        commonFunctions=new CommonFunctions(activity);
    }

    @Override
    public SeeMoreTestAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.see_more_layout, parent, false);

        return new SeeMoreTestAdapter.MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_textview;

        MyViewHolder(View view) {
            super(view);
            tv_textview = (TextView) view.findViewById(R.id.tv_post_content);
        }
    }

    @Override
    public void onBindViewHolder(final SeeMoreTestAdapter.MyViewHolder holder, final int position) {

        SpannableClickItem spannableClickItem=commonFunctions
                .setClickSpans(texts.get(indexes[position]),texts.get(indexes[position]),false,null);
        holder.tv_textview.setMovementMethod(null);

        holder.tv_textview.setText(spannableClickItem.spannableString,TextView.BufferType.SPANNABLE);
    }

    @Override
    public int getItemCount() {
        return  SIZE;
        //  return hierarchyList.size();
    }
}

