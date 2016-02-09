package se.tabyfkappen.tabyfk.helpers;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class ImageHelper {

    public ImageHelper(Context c) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(c)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        // Init IL with conf
        ImageLoader.getInstance().init(config);
    }
}
