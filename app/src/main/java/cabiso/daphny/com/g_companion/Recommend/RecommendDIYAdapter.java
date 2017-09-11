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

import java.util.List;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 7/31/2017.
 */

public class RecommendDIYAdapter<S> extends ArrayAdapter<DIYrecommend> {

    private Activity context;
    private int resource;
    private List<DIYrecommend> listDIY;

    public RecommendDIYAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYrecommend> objects) {
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
        TextView tvName = (TextView) v.findViewById(R.id.fetch_item_name);
       // TextView tvProcedure = (TextView) v.findViewById(R.id.fetch_item_procedure);
       // TextView tvMaterial = (TextView) v.findViewById(R.id.fetch_item_material);
        ImageView img = (ImageView) v.findViewById(R.id.fetch_image);

     //   tvProcedure.setText(listDIY.get(position).getDiyprocedure());
       // tvMaterial.setText(listDIY.get(position).getDiymaterial());
        tvName.setText(listDIY.get(position).getDiyName());
        Glide.with(context).load(listDIY.get(position).getImage_URL()).into(img);

        return v;
    }
}
