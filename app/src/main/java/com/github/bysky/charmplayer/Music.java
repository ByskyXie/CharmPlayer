package com.github.bysky.charmplayer;

import java.io.Serializable;

/**
 * Created by asus on 2017/11/26.
 */

public class Music implements Serializable{
    private String musicName;
    private String filePath;
    private String fileFolder;
    private String fileName;
    private String artist;
    Music(String filePath,String fileName,String fileFolder,String musicName,String artist){
        this.fileFolder = fileFolder;
        this.filePath = filePath;
        this.fileName = fileName;
        this.musicName = musicName;
        this.artist = artist;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileFolder() {
        return fileFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public String getArtist() {
        return artist;
    }
}
