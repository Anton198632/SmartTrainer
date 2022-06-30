package com.builderlinebr.smarttrainer.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "food")
public class Food {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pk")
    private int pk;

    @ColumnInfo(name = "symbols")
    private String symbols;

    @ColumnInfo(name = "foodName")
    private String foodName;

    @ColumnInfo(name = "mas")
    private String mas;

    @ColumnInfo(name = "proteins")
    private String proteins;

    @ColumnInfo(name = "fats")
    private String fats;

    @ColumnInfo(name = "carbohydrates")
    private String carbohydrates;

    @ColumnInfo(name = "calories")
    private String calories;

    @ColumnInfo(name = "image")
    private String image;

    @Ignore
    public int count = 100;

    public Food() {
    }

    public Food(int pk, String symbols, String foodName, String mas, String proteins, String fats, String carbohydrates, String calories, String image) {
        this.pk = pk;
        this.symbols = symbols;
        this.foodName = foodName;
        this.mas = mas;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.calories = calories;
        this.image = image;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setMas(String mas) {
        this.mas = mas;
    }

    public void setProteins(String proteins) {
        this.proteins = proteins;
    }

    public void setFats(String fats) {
        this.fats = fats;
    }

    public void setCarbohydrates(String carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPk() {
        return pk;
    }

    public String getSymbols() {
        return symbols;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getMas() {
        return mas;
    }

    public String getProteins() {
        return proteins;
    }

    public String getFats() {
        return fats;
    }

    public String getCarbohydrates() {
        return carbohydrates;
    }

    public String getCalories() {
        return calories;
    }

    public String getImage() {
        return image;
    }
}
