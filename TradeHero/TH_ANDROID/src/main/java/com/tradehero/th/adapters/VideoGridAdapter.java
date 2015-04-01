package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.VideoDTO;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;

//视频播放GridViewAdapter
public class VideoGridAdapter extends BaseAdapter
{

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<VideoDTO> listData;

    public VideoGridAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setListData(ArrayList<VideoDTO> data)
    {
        this.listData = data;
        notifyDataSetChanged();
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override public boolean isEnabled(int position)
    {
        return true;
    }

    @Override public VideoDTO getItem(int i)
    {
        return listData.get(i);
    }

    @Override public int getCount()
    {
        return listData == null ? 0 : listData.size();
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        VideoDTO data = getItem(position);

        if (data != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.video_class_gridview_item, viewGroup, false);
                holder = new ViewHolder();
                holder.imgVideoPlay = (ImageView) convertView.findViewById(R.id.videoImage);
                holder.tvVideoPlay = (TextView) convertView.findViewById(R.id.videoText);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            ImageLoader.getInstance().displayImage(data.thumbnail, holder.imgVideoPlay, UniversalImageLoader.getAvatarImageLoaderOptions());
            holder.tvVideoPlay.setText(data.name);
        }
        return convertView;
    }

    static class ViewHolder
    {
        public ImageView imgVideoPlay = null;
        public TextView tvVideoPlay = null;
    }
}
