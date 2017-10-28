package cabiso.daphny.com.g_companion.Recommend;

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

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 7/31/2017.
 */

public class RecommendDIYAdapter extends ArrayAdapter<DIYnames> {

    private Activity context;
    private int resource;
    private ArrayList<DIYnames> listDIY;

    public RecommendDIYAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
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

        tvName.setText(listDIY.get(position).getDiyName());

        //tvName.setText("DIY Name: "+listDIY.get(position).getDiyName());
        Glide.with(context).load(listDIY.get(position).getDiyUrl()).into(img);

        return v;
    }
}

