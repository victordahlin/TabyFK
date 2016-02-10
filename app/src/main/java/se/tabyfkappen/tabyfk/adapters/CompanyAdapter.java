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
import se.tabyfkappen.tabyfk.models.Company;

public class CompanyAdapter extends ArrayAdapter<Company> {

    public static class ViewHolder {
        TextView tvCompany;
        SimpleDraweeView  ivCompany;
    }

    public CompanyAdapter(Context context, ArrayList<Company> companies) {
        super(context, R.layout.content_list, companies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Company company = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.content_list, parent, false);
            viewHolder.tvCompany = (TextView) convertView.findViewById(R.id.tvListOffer);
            viewHolder.ivCompany = (SimpleDraweeView) convertView.findViewById(R.id.ivListOffer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvCompany.setText(company.getName());
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + company.getImageFilePath();
        Uri imageUri = Uri.parse(url);
        viewHolder.ivCompany.setImageURI(imageUri);

        return convertView;
    }
}
