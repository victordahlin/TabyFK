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
import se.tabyfkappen.tabyfk.models.Offer;

public class OfferAdapter extends BaseAdapter {
    private ArrayList<Offer> offers;
    private Activity activity;
    private TextView tvOffer;
    private ImageView ivOffer;

    public OfferAdapter(Activity activity, ArrayList<Offer> offers) {
        super();
        this.offers = offers;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return offers.size();
    }

    @Override
    public Object getItem(int position) {
        return offers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.content_list, parent, false);
            tvOffer = (TextView) convertView.findViewById(R.id.tvListOffer);
            ivOffer = (ImageView) convertView.findViewById(R.id.ivListOffer);
        }
        // Get data item for this position
        Offer offer = offers.get(position);
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + offer.getImageFilePath();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.ic_texture_black_24dp)
                .showImageOnFail(R.drawable.ic_texture_black_24dp)
                .showImageOnLoading(R.drawable.ic_texture_black_24dp).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        //download and display image from url
        imageLoader.displayImage(url, ivOffer, options);
        // Populate the data into the template view using the data object
        tvOffer.setText(offer.getName());
        // Return the completed view to render on screen
        return convertView;
    }
}
