package com.example.recordapplicationcoderbyte.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "records")

public class RecordEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    String title;
    String duration;
    String imgUrl;

    public RecordEntity(String title, String duration, String imgUrl) {
        this.title = title;
        this.duration = duration;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
