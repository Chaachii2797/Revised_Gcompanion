package cabiso.daphny.com.g_companion.Adapter;

import android.app.Activity;
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

import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 9/29/2017.
 */

public class Items_Adapter extends ArrayAdapter<DIYSell> {

    private Activity context;
    private int resource;
//    private List<DIYnames> listDIY;
    private List<DIYSell> listDIY;

    public Items_Adapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYSell> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDIY = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView tvName = (TextView) v.findViewById(R.id.get_diyName);
        ImageView img = (ImageView) v.findViewById(R.id.diy_item_icons);
        TextView tvcategory = (TextView) v.findViewById(R.id.tv_category);
        ImageView imgIcon = (ImageView) v.findViewById(R.id.promoIcon);

        if(listDIY.get(position).getIdentity().equalsIgnoreCase("Pending Item")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else if (listDIY.get(position).getIdentity().equalsIgnoreCase("For Confirmation Item")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else if(listDIY.get(position).getIdentity().equalsIgnoreCase("For Buyer Meet-up")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else if(listDIY.get(position).getIdentity().equalsIgnoreCase("For Seller Meet-up")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else if(listDIY.get(position).getIdentity().equalsIgnoreCase("DELIVERED")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else if(listDIY.get(position).getIdentity().equalsIgnoreCase("PURCHASED")){
            imgIcon.setVisibility(View.INVISIBLE);
        }
        else{
            imgIcon.setVisibility(View.VISIBLE);
        }

        tvcategory.setText(listDIY.get(position).getIdentity());
        tvName.setText(listDIY.get(position).getDiyName());
        Glide.with(context).load(listDIY.get(position).getDiyUrl()).into(img);

        return v;
    }
}
