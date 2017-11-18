package cabiso.daphny.com.g_companion.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 10/19/2017.
 */

public class CommunityAdapter extends BaseAdapter {

    Context context;
    ArrayList<CommunityItem> itemList;

    public CommunityAdapter(Context context, ArrayList<CommunityItem> modelList) {
        this.context = context;
        this.itemList = modelList;
    }

//    public CommunityAdapter(CommunityFragment communityFragment, int recommend_ui, ArrayList<DIYMethods> diyList) {
//    }

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
//            tvMaterials.setText(itemList.get(position).getMaterial());

            // click listener for remove button
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemList.remove(position);
                    notifyDataSetChanged();
                }
            });

            //TextView tvName = (TextView) convertView.findViewById(R.id.get_diyName);
            //ImageView img = (ImageView) v.findViewById(R.id.diy_item_icon);

//            tvName.setText(itemList.get(position).getValue());
            //Glide.with(context).load(listDIY.get(position).getProductPictureURLs().get(0)).into(img);
        }
        return convertView;
    }
}
