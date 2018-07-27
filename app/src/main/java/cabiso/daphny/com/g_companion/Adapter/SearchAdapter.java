package cabiso.daphny.com.g_companion.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.R;
/**
 * Created by cicctuser on 7/26/2018.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder>{

    Context context;
    ArrayList<String> item_list;
    ArrayList<String> img_list;
    ArrayList<String> identity_list;

    class SearchViewHolder extends RecyclerView.ViewHolder{
        ImageView item_picture;
        TextView item_name;
        TextView owner_name;
        TextView identity_name;

        public SearchViewHolder(View itemview){
            super(itemview);
            item_picture = (ImageView) itemview.findViewById(R.id.img_item_picture);
            item_name = (TextView) itemview.findViewById(R.id.tv_item_name);
            identity_name = (TextView) itemview.findViewById(R.id.tv_search_identity);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> item_list, ArrayList<String> img_list ,ArrayList<String> identity_list) {
        this.context = context;
        this.item_list = item_list;
        this.img_list = img_list;
        this.identity_list = identity_list;
    }

    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_item,parent,false);
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Recycle Click" + position, Toast.LENGTH_SHORT).show();

            }
        });
        holder.item_name.setText(item_list.get(position));
        holder.identity_name.setText(identity_list.get(position));

        Glide.with(context).load(img_list.get(position)).asBitmap()
                .placeholder(R.mipmap.ic_launcher_round).into(holder.item_picture);
    }


    @Override
    public int getItemCount() {
        return item_list.size();
    }
}
