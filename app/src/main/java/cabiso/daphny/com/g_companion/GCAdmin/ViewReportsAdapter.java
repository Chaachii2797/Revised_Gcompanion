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

import cabiso.daphny.com.g_companion.Model.ReportsModel;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/14/2018.
 */

public class ViewReportsAdapter extends ArrayAdapter<ReportsModel> {

    private Activity context;
    private int resource;
    private List<ReportsModel> list_reports;

    public ViewReportsAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<ReportsModel> objects) {
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
        TextView tvCustomerName = (TextView) v.findViewById(R.id.get_customer_name);
        TextView tvDate = (TextView) v.findViewById(R.id.tv_report_date);

        get_complains.setText(list_reports.get(position).getComplaint());
        tvCustomerName.setText(list_reports.get(position).getReportedBy());
        tvDate.setText(list_reports.get(position).getReportDate());

        return v;
    }
}
