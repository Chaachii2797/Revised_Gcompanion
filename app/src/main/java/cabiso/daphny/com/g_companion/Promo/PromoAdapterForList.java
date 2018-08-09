package cabiso.daphny.com.g_companion.Promo;

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

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 8/7/2018.
 */

public class PromoAdapterForList extends ArrayAdapter<DIYnames>{
    private Activity context;
    private int resource;
    List<DIYnames> promo_list;
    ArrayList<SellingDIY> selling_list;

    public PromoAdapterForList(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYnames> promo) {
        super(context, resource, promo);
        this.context = context;
        this.resource = resource;
        promo_list = promo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View promo_view = inflater.inflate(resource, null);
        ImageView promo_picture = (ImageView) promo_view.findViewById(R.id.img_promo_img);
        TextView promo_name = (TextView) promo_view.findViewById(R.id.promo_item_name);
        TextView promo_price = (TextView) promo_view.findViewById(R.id.promo_price);
        TextView promo_identity_name = (TextView) promo_view.findViewById(R.id.promo_identity);

        if(promo_list.get(position).getIdentity().equals("selling")){
            promo_identity_name.setText(promo_list.get(position).getIdentity());
        }

        promo_name.setText(promo_list.get(position).getDiyName());
        promo_price.setText(promo_list.get(position).getSelling_price()+"");
        Glide.with(context).load(promo_list.get(position).getDiyUrl()).into(promo_picture);

        return promo_view;
    }
}
