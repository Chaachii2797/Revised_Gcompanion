package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 9/18/2018.
 */

public class TransactionListAdapter extends ArrayAdapter<String> {

    private Activity context;
    private int resource;
    private List<String> list_logs;

    DatabaseReference reportsRef;

    public TransactionListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        list_logs = objects;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);

        TextView tvSellerID = (TextView) v.findViewById(R.id.getSellerID);

        tvSellerID.setText(list_logs.get(position).toString());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userLogs = (String) list_logs.get(position).toString();

                Intent intent = new Intent(v.getContext(), ViewTransactionsActivity.class);
                intent.putExtra("transacUserID", userLogs);
                Log.e("transacUserID", userLogs);


                v.getContext().startActivity(intent);
            }
        });
        return v;
    }

}
