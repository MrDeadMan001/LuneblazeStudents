package com.avadna.luneblaze.adapters.organisation;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponseData;

import java.util.List;

public class OrganisationTypeListAdapter extends BaseAdapter {
    List<PojoGetOrganisationTypeResponseData> typeList;
    Activity activity;


    public OrganisationTypeListAdapter(Activity activity, List<PojoGetOrganisationTypeResponseData> typeList) {
        this.activity = activity;
        this.typeList = typeList;
    }

    @Override
    public int getCount() {
        return typeList.size();
    }

    @Override
    public PojoGetOrganisationTypeResponseData getItem(int i) {
        return typeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrganisationTypeListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_list_textview, null);
            holder = new OrganisationTypeListAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (OrganisationTypeListAdapter.ViewHolder) convertView.getTag();
        }

        holder.tv_item.setText(getItem(position).name);

        return convertView;
    }

    static class ViewHolder {
        TextView tv_item;

        ViewHolder(View view) {
            tv_item = view.findViewById(R.id.tv_item);

        }
    }
}


