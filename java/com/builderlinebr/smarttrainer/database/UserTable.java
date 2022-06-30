package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserTable")
public class UserTable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @ColumnInfo(name = "userName")
    private String userName;

    @ColumnInfo(name = "userPhoto")
    private String userPhoto;

    public UserTable() {
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getPk() {
        return pk;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public UserTable(int pk, String userName, String userPhoto) {
        this.pk = pk;
        this.userName = userName;
        this.userPhoto = userPhoto;
    }
}
