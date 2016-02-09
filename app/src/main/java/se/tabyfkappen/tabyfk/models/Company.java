package se.tabyfkappen.tabyfk.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 2016-01-21.
 */
public class Company {
    private int id, categoryId;
    private String name;
    private String email;
    private String address;
    private String mobile;
    private String openingHours;

    public String getUrl() {
        return url;
    }

    private String url;
    private String imageFilePath;
    private String longTermDeal;

    public int getId() {
        return id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public String getLongTermDeal() {
        return longTermDeal;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public String getName() {
        return name;
    }

    public Company(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.categoryId = object.getInt("category_id");
            this.name = object.getString("name");
            this.url = object.getString("url");
            this.address = object.getString("address");
            this.mobile = object.getString("mobile");
            this.openingHours = object.getString("opening_hours");
            this.email = object.getString("email");
            this.longTermDeal = object.getString("long_term_deals");
            this.imageFilePath = object.getString("image_file_path");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
