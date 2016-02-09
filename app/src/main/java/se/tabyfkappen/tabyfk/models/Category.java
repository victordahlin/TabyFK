package se.tabyfkappen.tabyfk.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Victor on 2016-01-21.
 */
public class Category {
    private int id;
    private String name, imageFilePath;

    public int getId() {
        return id;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public String getName() {
        return name;
    }

    public Category(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            this.imageFilePath = object.getString("image_file_path");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
