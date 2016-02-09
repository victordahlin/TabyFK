package se.tabyfkappen.tabyfk.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.ArrayList;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.models.Company;

public class CompanyAdapter extends BaseAdapter {
    private ArrayList<Company> companies;
    private Activity activity;
    private TextView tvCompany;
    private ImageView ivCompany;

    public CompanyAdapter(Activity activity, ArrayList<Company> companies) {
        super();
        this.companies = companies;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return companies.size();
    }

    @Override
    public Object getItem(int position) {
        return companies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.content_list, parent, false);
            tvCompany = (TextView) convertView.findViewById(R.id.tvListOffer);
            ivCompany = (ImageView) convertView.findViewById(R.id.ivListOffer);
        }

        // Get data item for this position
        Company company = companies.get(position);
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + company.getImageFilePath();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ic_texture_black_24dp)
                .showImageOnFail(R.drawable.ic_texture_black_24dp)
                .showImageOnLoading(R.drawable.ic_texture_black_24dp).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //download and display image from url
        imageLoader.displayImage(url, ivCompany, options);
        // Populate the data into the template view using the data object
        tvCompany.setText(company.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
