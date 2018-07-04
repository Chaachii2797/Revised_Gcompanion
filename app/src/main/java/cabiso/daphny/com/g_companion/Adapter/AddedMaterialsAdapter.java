package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 4/19/2018.
 */

public class AddedMaterialsAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private String[] values;
    //DBMaterial[] values;

    public AddedMaterialsAdapter(Context context, String[] values){
        super(context, R.layout.materials_added_adapter, values);
        this.mContext = context;
        this.values = values;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.materials_added_adapter, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.tvAddedMat);

        textView.setText(values[position]);

        return rowView;
    }
}
