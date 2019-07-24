package com.amap.map3d.demo.poisearch;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.SubPoiItem;
import com.amap.map3d.demo.R;

public class PoiListAdapter extends BaseAdapter{
    private Context ctx;
    private List<PoiItem> list;

    public PoiListAdapter(Context context, List<PoiItem> poiList) {
        this.ctx = context;
        this.list = poiList;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
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
            convertView = View.inflate(ctx, R.layout.listview_item, null);
            holder.poititle = (TextView) convertView
                   .findViewById(R.id.poititle);
            holder.subpois = (GridView) convertView.findViewById(R.id.listview_item_gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        PoiItem item = list.get(position);
        holder.poititle.setText(item.getTitle());
        if (item.getSubPois().size() > 0) {
            List<SubPoiItem> subPoiItems = item.getSubPois();
            SubPoiAdapter subpoiAdapter=new SubPoiAdapter(ctx, subPoiItems); 
            holder.subpois.setAdapter(subpoiAdapter); 
        }


        return convertView;
    }
    private class ViewHolder {
        TextView poititle;
        GridView subpois;
    }

}
