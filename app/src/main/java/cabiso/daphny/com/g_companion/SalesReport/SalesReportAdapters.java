package cabiso.daphny.com.g_companion.SalesReport;

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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 9/27/2018.
 */

public class SalesReportAdapters extends ArrayAdapter<DIYnames>{

    private Activity context;
    private int resource;
    private ArrayList<DIYnames> list_diynames;
    private DatabaseReference inventoryRef;

    public SalesReportAdapters(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        list_diynames = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        final View view = inflater.inflate(resource, null);
        TextView sales_name = (TextView) view.findViewById(R.id.tv_inventory_diyname);
        final TextView sales_price = (TextView) view.findViewById(R.id.tv_inventory_price);
        final TextView sales_qty = (TextView) view.findViewById(R.id.tv_inventory_qty);
        final TextView sales_total = (TextView) view.findViewById(R.id.tv_total_sales);
        final TextView sales_identity = (TextView) view.findViewById(R.id.tv_inventory_identity);
        final TextView sales_expense = (TextView) view.findViewById(R.id.tv_expense);
        final TextView sales_income = (TextView) view.findViewById(R.id.tv_income);

        sales_name.setText(list_diynames.get(position).getDiyName());
        final DecimalFormat df = new DecimalFormat("#,###.00");
        inventoryRef = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(list_diynames.get(position).getProductID());
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try{
                    DIYnames diYnames = dataSnapshot.getValue(DIYnames.class);
                    double fixed_price = 0;
                    int stock_qty = 0;
                    int total_qty = 0;
                    ArrayList<String> overall_sales = new ArrayList<String>();

                    sales_income.setText("Income Count : "+diYnames.getIncomeCount()+"");
                    sales_expense.setText("Expense Count : "+diYnames.getExpenseCount()+"");
                    Log.e("INCOMECOUNT", diYnames.getIncomeCount()+"");

                    Log.e("DIYNameData",dataSnapshot.getKey().toString());
                    if(diYnames.getIdentity().equalsIgnoreCase("selling")){
                        for (DataSnapshot sellingSnapshot : dataSnapshot.child("DIY Price").getChildren()) {
                            fixed_price += sellingSnapshot.child("selling_price").getValue(double.class);
                            stock_qty += sellingSnapshot.child("selling_qty").getValue(int.class);
                            total_qty += sellingSnapshot.child("total_qty").getValue(int.class);
                            sales_identity.setText("Selling");
                            sales_identity.bringToFront();
                        }
                    }else if(diYnames.getIdentity().equalsIgnoreCase("Done Bidding")){
                        for(DataSnapshot bidSnapshot :dataSnapshot.child("bidding").getChildren()){
                            fixed_price += bidSnapshot.child("intial_price").getValue(int.class);
                            sales_identity.setText("Bidding");
                            sales_identity.bringToFront();
                        }
                    }
                    else if(diYnames.getIdentity().equalsIgnoreCase("Promo")){
                        for(DataSnapshot promoSnapshot :dataSnapshot.child("DIY Price").getChildren()){
                            stock_qty += promoSnapshot.child("selling_qty").getValue(int.class);
                            fixed_price += promoSnapshot.child("selling_price").getValue(int.class);
                            total_qty += promoSnapshot.child("total_qty").getValue(int.class);
                            sales_identity.setText("Promo");
                            sales_identity.bringToFront();
                        }
                    }

                    int qty_sold = (total_qty-stock_qty);
                    double total_qty_sold = (fixed_price*qty_sold);
                    Log.e("TOTAL", total_qty+"");
                    Log.e("TOTAL22", (fixed_price*qty_sold)+"");

//                    NumberFormat.getNumberInstance(Locale.US).format(qty_sold);
                    String formatted_total_sales = NumberFormat.getNumberInstance(Locale.US).format(total_qty_sold);
                    String formatted_price = NumberFormat.getNumberInstance(Locale.US).format(fixed_price);
                    sales_price.setText("₱ "+formatted_price);
                    sales_qty.setText(qty_sold+" ");
                    sales_total.setText("₱ "+formatted_total_sales);

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
