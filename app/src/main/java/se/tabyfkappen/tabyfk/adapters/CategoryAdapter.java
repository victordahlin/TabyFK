package se.tabyfkappen.tabyfk.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import java.util.ArrayList;
import se.tabyfkappen.tabyfk.R;
import se.tabyfkappen.tabyfk.models.Category;

public class CategoryAdapter extends BaseAdapter {
    private ArrayList<Category> categories;
    private Context context;

    public CategoryAdapter(Context c, ArrayList<Category> categories) {
        super();
        this.categories = categories;
        this.context= c;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_layout, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.tvGridName);
        SimpleDraweeView ivIcon = (SimpleDraweeView) convertView.findViewById(R.id.ivGridIcon);
        // Get data item for this position
        Category category = categories.get(position);
        // Apply URL for current offer
        String url = "https://www.tabyfkappen.se/api/v1/image/" + category.getImageFilePath();
        Uri imageUri = Uri.parse(url);
        ivIcon.setImageURI(imageUri);
        // Populate the data into the template view using the data object
        tvName.setText(category.getName());

        // Return the completed view to render on screen
        return convertView;
    }
}
