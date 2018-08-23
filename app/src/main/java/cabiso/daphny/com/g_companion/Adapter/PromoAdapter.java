package cabiso.daphny.com.g_companion.Adapter;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/6/2018.
 */

public class PromoAdapter extends ArrayAdapter<DIYnames> {

    private Activity context;
    private int resource;
    private List<DIYnames> listDIY;
    ArrayList<String> selectedStrings = new ArrayList<String>();

    public PromoAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDIY = objects;
    }

    ArrayList<String> getSelectedString(){
        return selectedStrings;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView tvName = (TextView) v.findViewById(R.id.promo_item_name);
        ImageView img = (ImageView) v.findViewById(R.id.img_promo_img);
        TextView tvcategory = (TextView) v.findViewById(R.id.promo_identity);
        final CheckBox checkbox = (CheckBox) v.findViewById(R.id.cb_for_promo_checked);

        tvName.setText(listDIY.get(position).getDiyName());
        tvcategory.setText(listDIY.get(position).getIdentity());
        Glide.with(context).load(listDIY.get(position).getDiyUrl()).into(img);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    selectedStrings.add(checkbox.getText().toString());
                    Toast.makeText(context, "Promo DIY name" + " = " + listDIY.get(position).getDiyName() , Toast.LENGTH_SHORT).show();
                }else{
//                    selectedStrings.remove(checkbox.getText().toString());
                }

            }
        });

        return v;
    }
}
