package com.example.imusic.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.imusic.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * 查找音乐
 */

public class MusicUtil {

    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        // 媒体库查询语句（写一个工具类MusicUtils）
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        int id=0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                id++;
                Song song = new Song();
                song.setId(id);
                String name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                name = name.replace(singer, "").replace("-", "").trim();
                song.setSongName(name);
                song.setSinger(singer);
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                list.add(song);
            }
            // 释放资源
            cursor.close();
        }
        return list;
    }

    //时间转换
    public static String getTime(int duration) {
        duration /= 1000;
        int hour = duration/3600;       //时
        int minute = (duration-hour*3600)/60;   //分
        int seconds = duration-hour*3600-minute*60;  //秒

        if(hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, seconds);
        }
        return String.format("%02d:%02d", minute, seconds);
    }
}
