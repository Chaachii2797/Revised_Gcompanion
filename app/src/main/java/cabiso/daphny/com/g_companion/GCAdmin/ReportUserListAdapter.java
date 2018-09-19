package cabiso.daphny.com.g_companion.GCAdmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.User_Profile;
import cabiso.daphny.com.g_companion.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Lenovo on 9/13/2018.
 */

public class ReportUserListAdapter extends ArrayAdapter<User_Profile> {

    private Activity context;
    private int resource;
    private List<User_Profile> list_reports;

    DatabaseReference reportsRef;

    public ReportUserListAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<User_Profile> objects) {
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

        reportsRef = FirebaseDatabase.getInstance().getReference().child("reportedSeller");

        final TextView tvSellerName = (TextView) v.findViewById(R.id.tv_sellerName);
        TextView tvUserID = (TextView) v.findViewById(R.id.tv_userID);
        CircleImageView pp = (CircleImageView) v.findViewById(R.id.seller_picture);
        TextView tvStatus = (TextView) v.findViewById(R.id.stausBlockorUnblock);

        tvSellerName.setText(list_reports.get(position).getF_name() + " " + list_reports.get(position).getL_name());
        tvUserID.setText(list_reports.get(position).getUserID());

        if(list_reports.get(position).getReport_status().equalsIgnoreCase("Unblock")){
            tvStatus.setVisibility(View.INVISIBLE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText(list_reports.get(position).getReport_status());
            tvStatus.setBackgroundColor(Color.RED);
        }

        Glide.with(context).load(list_reports.get(position).getUserProfileUrl()).diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().crossFade()
                .into(pp);


        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reports = (String) list_reports.get(position).getUserID();

                Intent intent = new Intent(v.getContext(), ViewComplaintsActivity.class);
                intent.putExtra("reportUserID", reports);
                Log.e("reportUserID", reports);


                v.getContext().startActivity(intent);
            }
        });
        return v;
    }





}
