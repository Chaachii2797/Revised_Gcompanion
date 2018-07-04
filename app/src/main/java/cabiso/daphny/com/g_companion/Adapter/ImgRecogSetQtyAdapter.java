package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import cabiso.daphny.com.g_companion.ImageRecognitionTags;
import cabiso.daphny.com.g_companion.Model.ImgRecogSetQty;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 4/4/2018.
 */

public class ImgRecogSetQtyAdapter extends RecyclerView.Adapter<ImgRecogSetQtyAdapter.ViewHolder>{
    private final Context mContext;
    private final List<ImgRecogSetQty> mItems;
    String[] quantity;
    String[] unitOfMeasurement;
    SpinnerAdapter1 qtyAdapter;


    public ImgRecogSetQtyAdapter(Context context, List<ImgRecogSetQty> items){
//        super(context,resource,items);
        this.mContext = context;
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.img_recog_set_qty, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        quantity = mContext.getResources().getStringArray(R.array.qty);
        qtyAdapter = new SpinnerAdapter1(mContext);

        unitOfMeasurement = mContext.getResources().getStringArray(R.array.UM);
        SpinnerAdapter nsAdapter = new SpinnerAdapter(mContext);

        final ImgRecogSetQty imgRecogSetQty = mItems.get(position);
        if(imgRecogSetQty != null){
            if(holder.tvTag != null){
                holder.tvTag.setText(imgRecogSetQty.getName());
            }

            if(holder.spinner_qty != null){
                holder.spinner_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        mItems.get(position).setQty(Integer.parseInt(quantity[position]));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
                holder.spinner_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        mItems.get(position).setUnit(unitOfMeasurement[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTag;
        Spinner spinner_qty;
        Spinner spinner_unit;
        ViewHolder(View view) {
            super(view);
            tvTag = (TextView) view.findViewById(R.id.tvTag);
            spinner_qty = (Spinner) view.findViewById(R.id.qtySpinner);
            spinner_unit = (Spinner) view.findViewById(R.id.unitspinner);
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
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.row_textview, null);
                holder = new ListContent();
                holder.text = (TextView) v.findViewById(R.id.textView1);

                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
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
    static class ListContent {
        TextView text;
    }

    public static class ContentQty {
        TextView text1;
    }
}
