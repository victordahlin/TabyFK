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

public class TemporaryDealsAdapter extends ArrayAdapter<Offer> {

    public static class ViewHolder {
        TextView mTvOfferTitle;
        TextView mTvOfferDate;
        SimpleDraweeView  mIvOffer;
    }

    public TemporaryDealsAdapter(Context context, ArrayList<Offer> offers) {
        super(context, R.layout.content_list_temporary, offers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Offer offer = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content_list_temporary, parent, false);
            viewHolder.mTvOfferTitle = (TextView) convertView.findViewById(R.id.tvOfferTitle);
            viewHolder.mTvOfferDate = (TextView) convertView.findViewById(R.id.tvOfferDate);
            viewHolder.mIvOffer = (SimpleDraweeView) convertView.findViewById(R.id.ivListOffer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.mTvOfferTitle.setText(offer.getName());

        String date = getContext().getText(R.string.deal_valid) + " " + offer.getEndDate();
        viewHolder.mTvOfferDate.setText(date);
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + offer.getImageFilePath();
        Uri imageUri = Uri.parse(url);
        viewHolder.mIvOffer.setImageURI(imageUri);

        return convertView;
    }
}
