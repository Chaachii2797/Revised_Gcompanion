package cabiso.daphny.com.g_companion.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/15/2018.
 */

public class DBMaterialsAdapter extends BaseAdapter {

    Context context;
    ArrayList<DBMaterial> itemList;
    ArrayList<QuantityItem> itemQty;

    public DBMaterialsAdapter(Context context, ArrayList<DBMaterial> modelList) {
        this.context = context;
        this.itemList = modelList;
    }



    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        convertView = null;

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.materials_adapter, null);

            TextView tvMaterials = (TextView) convertView.findViewById(R.id.etMaterials);
            ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteMaterial);

            int pos = position+1;

            DBMaterial m = itemList.get(position);
            tvMaterials.setText(pos + ".) " + " " + m.getQuantity() +" " + m.getUnit() + " "+ m.getName());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DBMaterial materials = itemList.get(position);
                    String mat = materials.getName();
                    int qty = materials.getQuantity();
                    String unit = materials.getUnit();

                    Toast.makeText(context, "Clicked: " + " " + qty + " " + unit + " " + mat, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("INTENT_NAME").putExtra("materialName", mat);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                }
            });

            // click listener for remove button
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.remove(position);
                    notifyDataSetChanged();
                }
            });


        }

        return convertView;
    }
}
