package com.example.imusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.imusic.R;
import com.example.imusic.bean.Song;

import java.util.List;

/**
 * Created by ASUS on 2017/12/6.
 *
 */

public class MusicAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<Song> lSongs;

    public MusicAdapter() {
    }

    public MusicAdapter(Context context, List<Song> lSongs) {
        this.inflater = LayoutInflater.from(context);
        this.lSongs = lSongs;
    }

    @Override
    public int getCount() {
        return lSongs.size();
    }

    @Override
    public Object getItem(int i) {
        return lSongs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        Song song=lSongs.get(i);
        View view = inflater.inflate(R.layout.list_item, null);
        TextView tv_id=view.findViewById(R.id.txt_id);
        TextView tv_song=view.findViewById(R.id.txt_title);
        TextView tv_singer=view.findViewById(R.id.txt_artist);
        ImageView iv_details=view.findViewById(R.id.im_details);
        tv_id.setText(song.getId()+"");
        tv_song.setText(song.getSongName());
        tv_singer.setText(song.getSinger());
        //iv_details
        return view;
    }

}
