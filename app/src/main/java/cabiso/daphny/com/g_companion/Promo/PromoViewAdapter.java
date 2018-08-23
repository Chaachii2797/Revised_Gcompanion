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

import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 8/15/2018.
 */

public class PromoViewAdapter extends ArrayAdapter<DIYnames>{
    private Activity context;
    private int resource;
    List<DIYnames> promo_list;

    public PromoViewAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.promo_list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View promo_view = inflater.inflate(resource, null);
        ImageView img_buy_view_promo = (ImageView) promo_view.findViewById(R.id.img_buy_view_promo);
        ImageView img_get_view_promo = (ImageView) promo_view.findViewById(R.id.img_get_view_promo);
        TextView tv_view_promo_diyname = (TextView) promo_view.findViewById(R.id.view_promo_item_diyName);



        return promo_view;
    }
}
