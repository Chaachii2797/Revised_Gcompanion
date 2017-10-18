package cabiso.daphny.com.g_companion.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cabiso.daphny.com.g_companion.ProductInfo;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 9/29/2017.
 */

public class Items_Adapter extends ArrayAdapter<ProductInfo> {

    private Activity context;
    private int resource;
    private List<ProductInfo> listDIY;

    public Items_Adapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<ProductInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDIY = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView tvName = (TextView) v.findViewById(R.id.get_diyName);
        ImageView img = (ImageView) v.findViewById(R.id.diy_item_icon);

        tvName.setText(listDIY.get(position).title);
        Glide.with(context).load(listDIY.get(position).getProductPictureURLs().get(0)).into(img);

        return v;
    }
}
