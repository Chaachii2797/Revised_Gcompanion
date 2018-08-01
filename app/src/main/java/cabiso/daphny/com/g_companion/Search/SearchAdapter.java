package cabiso.daphny.com.g_companion.Search;

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
 * Created by cicctuser on 7/26/2018.
 */

public class SearchAdapter extends ArrayAdapter<DIYnames>{

    private Activity context;
    private int resource;
    ArrayList<DIYnames> item_list;

    public SearchAdapter(@NonNull Activity context, @LayoutRes int resource,@NonNull ArrayList<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        item_list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View itemview = inflater.inflate(resource, null);
        ImageView item_picture = (ImageView) itemview.findViewById(R.id.img_item_picture);
        TextView item_name = (TextView) itemview.findViewById(R.id.tv_item_name);
        TextView identity_name = (TextView) itemview.findViewById(R.id.tv_search_identity);

        item_name.setText(item_list.get(position).getDiyName());
        identity_name.setText(item_list.get(position).getIdentity());

        Glide.with(context).load(item_list.get(position).getDiyUrl()).into(item_picture);

        return itemview;
    }
}
