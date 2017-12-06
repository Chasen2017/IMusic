package com.example.imusic.bean;

public class Song {
    private int id;//编号
    private String songName;//歌名
    private String singer;//歌手名
    private int duration;//歌曲时间
    private String path;//歌曲存储路径

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", songName='" + songName + '\'' +
                ", singer='" + singer + '\'' +
                ", duration=" + duration +
                ", path='" + path + '\'' +
                '}';
    }
}
