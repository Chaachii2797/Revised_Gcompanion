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

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityAdapter extends BaseAdapter {

    Context context;
    ArrayList<CommunityItem> itemList;
    ArrayList<QuantityItem> itemQty;

    public CommunityAdapter(Context context, ArrayList<CommunityItem> modelList) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = null;

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.materials_adapter, null);

            TextView tvMaterials = (TextView) convertView.findViewById(R.id.etMaterials);
            ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteMaterial);

            int pos = position+1;

            CommunityItem m = itemList.get(position);
            tvMaterials.setText(pos + ".) " +m.getVal());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CommunityItem materials = itemList.get(position);
                    String prod = materials.getVal();

                    Toast.makeText(context, "Clicked: " + " " + prod, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent("INTENT_NAME").putExtra("procedureName", prod);
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
