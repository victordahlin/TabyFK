package se.tabyfkappen.tabyfk.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Offer implements Comparable<Offer> {
    private int id, companyId;
    private String name;
    private String description;
    private String endDate;
    private boolean isSuperDeal;
    private String imageFilePath;

    public int getId() {
        return id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public boolean IsSuperDeal() {
        return isSuperDeal;
    }

    public String getName() {
        return name;
    }

    // Constructor to convert JSON object into a Java Class instance
    public Offer(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.companyId = object.getInt("company_id");
            this.name = object.getString("name");
            this.description = object.getString("description");
            this.endDate = object.getString("end_date");
            this.imageFilePath = object.getString("image_file_path");

            if (object.getInt("is_super_deal") > 0) {
                this.isSuperDeal = true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Offer another) {
        return getEndDate().compareTo(another.getEndDate());
    }
}
