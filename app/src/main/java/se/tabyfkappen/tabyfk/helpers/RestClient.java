package se.tabyfkappen.tabyfk.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import se.tabyfkappen.tabyfk.Tasks.GetResponseTask;
import se.tabyfkappen.tabyfk.models.Category;
import se.tabyfkappen.tabyfk.models.Company;
import se.tabyfkappen.tabyfk.models.Offer;

/**
 * Created by Victor on 2016-01-21.
 * Updated: 2017-01-21
 */
public class RestClient {
    private String token;
    private final String url;
    private ArrayList<Company> mCompanies;
    private ArrayList<Category> mCategories;
    private ArrayList<Offer> mSuperDeals;
    private ArrayList<Offer> mTemporaryDeals;
    private String aboutApp;
    private String aboutTabyFK;
    private static RestClient mInstance = null;

    public String getAboutApp() {
        return aboutApp;
    }

    public String getAboutTabyFK() {
        return aboutTabyFK;
    }

    public ArrayList<Company> getCompanies() {
        return mCompanies;
    }

    public ArrayList<Category> getCategories() {
        return mCategories;
    }

    public ArrayList<Offer> getSuperDeals() {
        return mSuperDeals;
    }

    public ArrayList<Offer> getTemporaryDeals() {
        return mTemporaryDeals;
    }

    private RestClient(String token) {
        this.url = "https://www.tabyfkappen.se/api/v1/";
        this.token = "?token=" + token;

        this.mCompanies = new ArrayList<>();
        this.mCategories = new ArrayList<>();
        this.mSuperDeals = new ArrayList<>();
        this.mTemporaryDeals = new ArrayList<>();

        setOffers("getOffers");
        Collections.sort(mSuperDeals);
        Collections.sort(mTemporaryDeals);

        setInformation("getInfo");
        setCategories("getCategories");
        sortCategories(mCategories);
        setCompanies("getCompanies");
        sortCompanies(mCompanies);
    }

    /**
     * Static instance of this class
     * @param token provided by Taby FK
     * @return instance of this class
     */
    public static RestClient getInstance(String token) {
        if(mInstance == null) {
            mInstance = new RestClient(token);
        }
        return mInstance;
    }

    /**
     * Receive all offers from JSON and split into super
     * and temporary deals and create Offer objects
     * @param subDomain name of sub domain
     */
    private void setOffers(String subDomain) {
        try {
            String jsonString = "";
            try {
                jsonString = new GetResponseTask().execute(url+subDomain+token).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray(jsonString);

            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Offer offer = new Offer(jsonObject);
                if (offer.IsSuperDeal()) {
                    mSuperDeals.add(offer);
                } else {
                    mTemporaryDeals.add(offer);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive all companies JSON and create
     * Company objects
     * @param subDomain name of sub domain
     */
    private void setCompanies(String subDomain) {
        try {
            String jsonString = "";
            try {
                jsonString = new GetResponseTask().execute(url+subDomain+token).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++) {
                mCompanies.add(new Company(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive JSON Object from server and apply with model
     * to mCategories list
     * @param subDomain name of the sub domain
     */
    private void setCategories(String subDomain) {
        try {
            String jsonString = "";
            try {
                jsonString = new GetResponseTask().execute(url+subDomain+token).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++) {
                mCategories.add(new Category(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Receive JSON Object with company information
     * @param subDomain name of sub domain
     */
    private void setInformation(String subDomain) {
        try {
            String jsonString = "";
            try {
                jsonString = new GetResponseTask().execute(url+subDomain+token).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject(jsonString);
            aboutApp = jsonObject.getString("app");
            aboutTabyFK = jsonObject.getString("tabyfk");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sort companies for names
     * @param list
     */
    private void sortCompanies(List<Company> list) {
        Collections.sort(list, new Comparator<Company>() {
            @Override
            public int compare(Company lhs, Company rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

    /**
     * Sort categories objects with their names
     * @param list of sorted categories
     */
    private void sortCategories(List<Category> list) {
        Collections.sort(list, new Comparator<Category>() {
            @Override
            public int compare(Category lhs, Category rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
    }

    /**
     * @param companyID
     * @return filtered super deals of type offer
     */
    public ArrayList<Offer> getSelectedOffers(int companyID) {
        ArrayList<Offer> filtered = new ArrayList<>();
        for(Offer offer : mSuperDeals) {
            if(offer.getCompanyId() == companyID) {
                filtered.add(offer);
            }
        }

        for(Offer offer : mTemporaryDeals) {
            if(offer.getCompanyId() == companyID) {
                filtered.add(offer);
            }
        }
        return filtered;
    }

    /**
     * Filter list of categories after id
     * @param categoryID the group name
     * @return filtered list of companies
     */
    public ArrayList<Company> getSelectedCompanies(int categoryID) {
        ArrayList<Company> filtered = new ArrayList<>();
        for(Company company : mCompanies) {
            if(company.getCategoryId() == categoryID) {
                filtered.add(company);
            }
        }
        return filtered;
    }

    /**
     * Remove offer from list by id
     * @param id of the offer
     */
    public void removeOffer(int id) {
        Iterator<Offer> superIterator = mSuperDeals.iterator();

        while(superIterator.hasNext()) {
            Offer offer = superIterator.next();

            if(offer.getId() == id) {
                superIterator.remove();
            }
        }

        Iterator<Offer> temporaryIterator = mTemporaryDeals.iterator();
        while(temporaryIterator.hasNext()) {
            Offer offer = temporaryIterator.next();

            if(offer.getId() == id) {
                temporaryIterator.remove();
            }
        }
    }

}
