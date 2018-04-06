package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cabiso.daphny.com.g_companion.CaptureDIY;
import cabiso.daphny.com.g_companion.ImageRecognitionTags;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 4/6/2018.
 */

public class ImgRecogTagAdapter extends ArrayAdapter<DBMaterial> {
    private final Context mContext;
    private final int mResource;
    private String[] quantity;
    private SpinnerAdapter umAdapter;
    private SpinnerAdapter1 qAdapter;
    private String[] unitOfMeasurement;
    private final List<DBMaterial> dbMaterials;

    public ImgRecogTagAdapter(Context context, int resource, List<DBMaterial> items){
        super(context,resource,items);
        this.mContext = context;
        mResource = resource;
        dbMaterials = items;

        unitOfMeasurement = mContext.getResources().getStringArray(R.array.UM);
        umAdapter = new SpinnerAdapter(mContext);

        quantity = mContext.getResources().getStringArray(R.array.qty);
        qAdapter = new SpinnerAdapter1(mContext);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(mResource,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DBMaterial item = dbMaterials.get(position);
        if(item != null){
            holder.qtySpinner.setAdapter(qAdapter);
            holder.unitspinner.setAdapter(umAdapter);
            if(holder.tvTag!=null){
                holder.tvTag.setText(item.getName());
            }
            if(holder.qtySpinner != null){
                holder.qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position2, long id) {
                        // TODO Auto-generated method stub
                        try{
//                            Toast.makeText(mContext, "Quantity "+quantity[position2], Toast.LENGTH_SHORT).show();
                            dbMaterials.get(position).setQuantity(Integer.parseInt(quantity[position2]));
                        }catch(NumberFormatException e){

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
                holder.unitspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position3, long id) {
                        // TODO Auto-generated method stub
//                        Toast.makeText(mContext, "Unit "+unitOfMeasurement[position3], Toast.LENGTH_SHORT).show();
                        dbMaterials.get(position).setUnit(unitOfMeasurement[position3]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
            }
            holder.btnDelete_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbMaterials.remove(position);
                    notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }
    private static class ViewHolder{
        TextView tvTag;
        Spinner unitspinner;
        Spinner qtySpinner;
        Button btnDelete_tag;
        public ViewHolder(View view){
            tvTag = (TextView)view.findViewById(R.id.tvTag);
            unitspinner = (Spinner) view.findViewById(R.id.unitspinner);
            qtySpinner = (Spinner) view.findViewById(R.id.qtySpinner);
            btnDelete_tag = (Button) view.findViewById(R.id.btn_delete_tag);
        }
    }

    public class SpinnerAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;

        public SpinnerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return unitOfMeasurement.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImgRecogSetQtyAdapter.ListContent holder;
            View v = convertView;
            if (v == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.row_textview, null);
                holder = new ImgRecogSetQtyAdapter.ListContent();
                holder.text = (TextView) v.findViewById(R.id.textView1);

                v.setTag(holder);
            } else {
                holder = (ImgRecogSetQtyAdapter.ListContent) v.getTag();
            }
            holder.text.setText(unitOfMeasurement[position]);
            return v;
        }
    }

    public class SpinnerAdapter1 extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;

        public SpinnerAdapter1(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return quantity.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageRecognitionTags.ContentQty holder1;
            View vi = convertView;
            if (vi == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                vi = mInflater.inflate(R.layout.row_textview, null);
                holder1 = new ImageRecognitionTags.ContentQty();
                holder1.text1 = (TextView) vi.findViewById(R.id.textView1);

                vi.setTag(holder1);
            } else {
                holder1 = (ImageRecognitionTags.ContentQty) vi.getTag();
            }
            holder1.text1.setText(quantity[position]);
            return vi;
        }
    }
}
