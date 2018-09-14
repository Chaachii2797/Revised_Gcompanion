package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/14/2018.
 */

public class ViewReportsAdapter extends ArrayAdapter<String> {

    private Activity context;
    private int resource;
    private List<String> list_reports;

    public ViewReportsAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        list_reports = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView get_complains = (TextView) v.findViewById(R.id.get_complains);
        TextView tvUserID = (TextView) v.findViewById(R.id.tv_complain);

        get_complains.setText(list_reports.get(position));

        return v;
    }
}
