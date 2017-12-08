package com.example.imusic.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imusic.R;
import com.example.imusic.activity.MainActivity;
import com.example.imusic.bean.Song;
import com.example.imusic.util.MusicUtil;

import java.util.List;

/**
 * 音乐ListView适配器
 */

public class MusicAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater inflater;

    private List<Song> lSongs;

    public MusicAdapter() {
    }

    public MusicAdapter(Context context, List<Song> lSongs) {
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.lSongs = lSongs;
    }

    @Override
    public int getCount() {
        return lSongs.size();
    }

    @Override
    public Song getItem(int i) {
        return lSongs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder.tv_id = convertView.findViewById(R.id.txt_id);
            viewHolder.tv_song = convertView.findViewById(R.id.txt_title);
            viewHolder.tv_singer = convertView.findViewById(R.id.txt_artist);
            viewHolder.im_details=convertView.findViewById(R.id.im_details);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Song song = getItem(i);
        if (null != song) {
            viewHolder.tv_id.setText(song.getId()+"");
            viewHolder.tv_song.setText(song.getSongName());
            viewHolder.tv_singer.setText(song.getSinger());
        }
        ShowDetails sd=new ShowDetails(song);
        viewHolder.im_details.setOnClickListener(sd);
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_id;
        TextView tv_song;
        TextView tv_singer;
        ImageView im_details;
    }

    private class ShowDetails implements View.OnClickListener{

        Song song;

        public ShowDetails(Song song) {
            this.song = song;
        }

        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder =new AlertDialog.Builder(context);
            builder.setTitle("详细信息");
            String s="歌曲名称："+song.getSongName();
            s+="\n歌手："+song.getSinger();
            s+="\n歌曲时间："+ MusicUtil.getTime(song.getDuration());
            s+="\n歌曲路径："+song.getPath();
            builder.setMessage(s);
            builder.setIcon(R.drawable.ic_details);
            builder.setCancelable(false);
            builder.setPositiveButton("知道了！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
    }

}
