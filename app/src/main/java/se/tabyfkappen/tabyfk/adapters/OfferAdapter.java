package se.tabyfkappen.tabyfk.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.models.Offer;

public class OfferAdapter extends ArrayAdapter<Offer> {

    private static class ViewHolder {
        TextView tvOffer;
        SimpleDraweeView  ivOffer;
    }

    public OfferAdapter(Context context, ArrayList<Offer> offers) {
        super(context, R.layout.content_list, offers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Offer offer = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content_list, parent, false);
            viewHolder.tvOffer = (TextView) convertView.findViewById(R.id.tvListOffer);
            viewHolder.ivOffer = (SimpleDraweeView) convertView.findViewById(R.id.ivListOffer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvOffer.setText(offer.getName());
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + offer.getImageFilePath();
        Uri imageUri = Uri.parse(url);
        viewHolder.ivOffer.setImageURI(imageUri);

        return convertView;
    }
}
