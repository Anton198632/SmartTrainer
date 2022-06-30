package com.builderlinebr.smarttrainer.database;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercises")
public class Exercises implements Parcelable {
    // Колонки
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    private int id;

    @ColumnInfo(name = "Header")
    private String header;

    @ColumnInfo(name = "Properties")
    private String properties;

    @ColumnInfo(name = "Description")
    private String description;

    @ColumnInfo(name = "favorites")
    private int favorites;

    @Ignore
    public String image;

    @ColumnInfo(name = "ImageMale")
    private String imageMale;

    @ColumnInfo(name = "ImageFemale")
    private String imageFemale;

    protected Exercises(Parcel in) {
        id = in.readInt();
        header = in.readString();
        properties = in.readString();
        description = in.readString();
        favorites = in.readInt();
        image = in.readString();
        imageMale = in.readString();
        imageFemale = in.readString();
    }

    public static final Creator<Exercises> CREATOR = new Creator<Exercises>() {
        @Override
        public Exercises createFromParcel(Parcel in) {
            return new Exercises(in);
        }

        @Override
        public Exercises[] newArray(int size) {
            return new Exercises[size];
        }
    };

    public void setImageMale(String imageMale) {
        this.imageMale = imageMale;
    }

    public void setImageFemale(String imageFemale) {
        this.imageFemale = imageFemale;
    }

    public String getImageMale() {
        return imageMale;
    }

    public String getImageFemale() {
        return imageFemale;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getProperties() {
        return properties;
    }

    public String getDescription() {
        return description;
    }

    public Exercises(int id, String header, String properties, String description) {
        this.id = id;
        this.header = header;
        this.properties = properties;
        this.description = description;
    }

    public Exercises() {
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(header);
        dest.writeString(properties);
        dest.writeString(description);
        dest.writeInt(favorites);
        dest.writeString(image);
        dest.writeString(imageMale);
        dest.writeString(imageFemale);
    }


}
