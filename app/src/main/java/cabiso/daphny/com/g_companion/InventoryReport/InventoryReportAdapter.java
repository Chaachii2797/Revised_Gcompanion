package cabiso.daphny.com.g_companion.InventoryReport;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 9/7/2018.
 */

public class InventoryReportAdapter extends ArrayAdapter<DIYnames>{

    private Activity context;
    private int resource;
    private ArrayList<DIYnames> list_diynames;
    private DatabaseReference priceRef;

    public InventoryReportAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
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
        TextView inventory_diy_name = (TextView) view.findViewById(R.id.tv_report_diyName);
        final TextView inventory_diy_price = (TextView) view.findViewById(R.id.tv_report_price);
        final TextView inventory_diy_qty = (TextView) view.findViewById(R.id.tv_report_qty);
        final TextView inventory_qty_total = (TextView) view.findViewById(R.id.tv_report_qty_total);

        final TextView inventory_diy_qty_unsold = (TextView) view.findViewById(R.id.tv_report_qty_unsold);
        final TextView inventory_total_qty_unsold = (TextView) view.findViewById(R.id.tv_report_total_qty_unsold);

        final TextView inventory_diy_qty_sold = (TextView) view.findViewById(R.id.tv_report_qty_sold);
        final TextView report_total_qty_sold = (TextView) view.findViewById(R.id.tv_report_total_qty_sold);

        final TextView inventory_total_overall = (TextView) view.findViewById(R.id.report_total_overall);

        inventory_diy_name.setText(list_diynames.get(position).getDiyName());

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

                    int qty_total = (fixed_price*total_qty);
                    int total_overall = (total_qty_sold+total_qty_unsold);

                    String formatted_fixed_price = NumberFormat.getNumberInstance(Locale.US).format(fixed_price);
                    String formatted_qty_total = NumberFormat.getNumberInstance(Locale.US).format(qty_total);
                    String formatted_total_qty_sold = NumberFormat.getNumberInstance(Locale.US).format(total_qty_sold);
                    String formatted_total_qty_unsold = NumberFormat.getNumberInstance(Locale.US).format(total_qty_unsold);
                    String formatted_total_overall = NumberFormat.getNumberInstance(Locale.US).format(total_overall);

                    inventory_diy_price.setText("₱ "+formatted_fixed_price);
                    inventory_diy_qty.setText(total_qty+"");
                    inventory_qty_total.setText(formatted_qty_total+"");

                    inventory_diy_qty_sold.setText(qty_sold+"");
                    inventory_diy_qty_unsold.setText(qty_unsold+"");

                    report_total_qty_sold.setText(formatted_total_qty_sold+"");
                    inventory_total_qty_unsold.setText(formatted_total_qty_unsold+"");

                    inventory_total_overall.setText("₱ "+formatted_total_overall);
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
