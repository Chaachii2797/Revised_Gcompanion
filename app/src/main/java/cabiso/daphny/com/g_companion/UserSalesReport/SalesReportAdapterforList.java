package cabiso.daphny.com.g_companion.UserSalesReport;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 9/7/2018.
 */

public class SalesReportAdapterforList extends ArrayAdapter<DIYnames>{

    private Activity context;
    private int resource;
    private ArrayList<DIYnames> list_diynames;
    private DatabaseReference priceRef;

    public SalesReportAdapterforList(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        list_diynames = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        final View view = inflater.inflate(resource, null);
        TextView report_diy_name = (TextView) view.findViewById(R.id.tv_report_diyName);
        final TextView report_diy_price = (TextView) view.findViewById(R.id.tv_report_price);
        final TextView report_diy_qty = (TextView) view.findViewById(R.id.tv_report_qty);
        final TextView report_qty_total = (TextView) view.findViewById(R.id.tv_report_qty_total);

        final TextView report_diy_qty_unsold = (TextView) view.findViewById(R.id.tv_report_qty_unsold);
        final TextView report_total_qty_unsold = (TextView) view.findViewById(R.id.tv_report_total_qty_unsold);

        final TextView report_diy_qty_sold = (TextView) view.findViewById(R.id.tv_report_qty_sold);
        final TextView report_total_qty_sold = (TextView) view.findViewById(R.id.tv_report_total_qty_sold);

        final TextView report_total_overall = (TextView) view.findViewById(R.id.report_total_overall);

        report_diy_name.setText(list_diynames.get(position).getDiyName());

        priceRef = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(list_diynames.get(position).getProductID());
        priceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                    int fixed_price = 0;
                    int stock_qty = 0;
                    int total_qty = 0;

                    Log.e("DIYNameData",dataSnapshot.getKey().toString());
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){
                        for (DataSnapshot sellingSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                            fixed_price += sellingSnapshot.child("selling_price").getValue(double.class);
                            stock_qty += sellingSnapshot.child("selling_qty").getValue(int.class);
                            total_qty += sellingSnapshot.child("total_qty").getValue(int.class);
                        }
                    }else if(diYnames.getIdentity().equalsIgnoreCase("on bid!")){
                        for(DataSnapshot bidSnapshot : dataSnapshot.child("bidding").getChildren()){
                            fixed_price += bidSnapshot.child("intial_price").getValue(int.class);
                        }
                    }else if(diYnames.getIdentity().equalsIgnoreCase("Promo")){
                        for(DataSnapshot promoSnapshot :dataSnapshot.child("DIY Price").getChildren()){
                            fixed_price += promoSnapshot.child("selling_price").getValue(int.class);
                            stock_qty += promoSnapshot.child("selling_qty").getValue(int.class);
                            total_qty += promoSnapshot.child("total_qty").getValue(int.class);
                        }
                    }

                    int qty_sold = (total_qty-stock_qty);
                    int total_qty_sold = (fixed_price*qty_sold);

                    int qty_unsold = (stock_qty);
                    int total_qty_unsold = (fixed_price*qty_unsold);

                    report_diy_price.setText(""+ fixed_price);
                    report_diy_qty.setText(""+total_qty);
                    report_qty_total.setText(""+(fixed_price*total_qty));

                    report_diy_qty_sold.setText(""+qty_sold);
                    report_diy_qty_unsold.setText(""+qty_unsold);

                    report_total_qty_sold.setText(""+total_qty_sold);
                    report_total_qty_unsold.setText(""+total_qty_unsold);

                    report_total_overall.setText(""+(total_qty_sold+total_qty_unsold));
                }catch (NullPointerException nPe){
                    nPe.getMessage();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }
}
