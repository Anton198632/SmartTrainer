package com.builderlinebr.smarttrainer.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "imagesfemale")
public class ImagesFemale {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @NonNull
    @ColumnInfo(name = "Id")
    private int id;

    @ColumnInfo(name = "Link")
    private String link;

    public ImagesFemale() {
    }

    public ImagesFemale(int pk, int id, String link) {
        this.pk = pk;
        this.id = id;
        this.link = link;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getPk() {
        return pk;
    }
}
