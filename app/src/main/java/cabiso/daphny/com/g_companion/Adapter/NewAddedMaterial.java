package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cabiso.daphny.com.g_companion.ImageRecognitionForMaterials;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 4/23/2018.
 */

public class NewAddedMaterial extends ArrayAdapter<DBMaterial> {

    private final Context mContext;
    private final int mResource;
    private List<DBMaterial> dbMaterials;


    public NewAddedMaterial(Context context, int resource, List<DBMaterial> items){
        super(context,resource,items);
        this.mContext = context;
        mResource = resource;
        dbMaterials = items;


    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(mResource,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        DBMaterial item = dbMaterials.get(position);

        if(holder.tvTag!=null){
            holder.tvTag.setText(item.getName());
        }
        holder.tvQtyUnity.setText(item.getQuantity()+" "+item.getUnit());

//        Bitmap _mBitmap= ((Activity) mContext).getIntent().getParcelableExtra("Bitmap");
//        Drawable _mDrawable=new BitmapDrawable(Resources.getSystem(),_mBitmap);
//        Log.e("mat_drawable", String.valueOf(_mDrawable));
//        holder.mat_img.setImageDrawable(_mDrawable);

        Bitmap bm = ImageRecognitionForMaterials.constan.photoMap;
        holder.mat_img.setImageBitmap(item.getMat_image());

//        holder.mat_img.setImageDrawable(new BitmapDrawable(Resources.getSystem(),
//                ImageRecognitionForMaterials.getBitmap_transfer()));


        holder.btnDelete_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbMaterials.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    private static class ViewHolder{
        TextView tvTag;
        TextView tvQtyUnity;
        Button btnDelete_tag;
        ImageView mat_img;
        public ViewHolder(View view){
            tvQtyUnity = (TextView) view.findViewById(R.id.tvQtyUnity);
            tvTag = (TextView)view.findViewById(R.id.tvAddedMat);
            btnDelete_tag = (Button) view.findViewById(R.id.btn_delete_tag);
            mat_img = (ImageView) view.findViewById(R.id.addedMat_image);
        }
    }


}
