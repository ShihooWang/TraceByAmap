package com.amap.map3d.demo.poisearch;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.services.poisearch.SubPoiItem;
import com.amap.map3d.demo.R;

public class SubPoiAdapter extends BaseAdapter {

    private Context ctx;
    private List<SubPoiItem> list; 
    
    public SubPoiAdapter(Context context, List<SubPoiItem> poiList) {
        this.ctx = context;
        this.list = poiList;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(ctx, R.layout.gridview_item, null);
            holder.poititle = (TextView) convertView
                    .findViewById(R.id.gridview_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        
        SubPoiItem item = list.get(position);
        holder.poititle.setText(item.getSubName());
        
        return convertView;
    }
    private class ViewHolder {
        TextView poititle;
    }


}
