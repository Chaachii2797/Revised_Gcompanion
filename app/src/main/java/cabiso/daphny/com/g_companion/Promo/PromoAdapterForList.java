package cabiso.daphny.com.g_companion.Promo;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 8/7/2018.
 */

public class PromoAdapterForList extends ArrayAdapter<DIYnames>{
    private Activity context;
    private int resource;
    private DatabaseReference addPromoReference;
    private FirebaseUser mFirebaseUser;
    private String userID;
    List<DIYnames> promo_list;
    ArrayList<SellingDIY> selling_list;
    PromoModel promoItem;


    public PromoAdapterForList(@NonNull Activity context, @LayoutRes int resource, @NonNull List<DIYnames> promo, PromoModel promoItem) {
        super(context, resource, promo);
        this.context = context;
        this.resource = resource;
        this.promoItem = promoItem;
        addPromoReference = FirebaseDatabase.getInstance().getReference();
        promo_list = promo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View promo_view = inflater.inflate(resource, null);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();
        ImageView promo_picture = (ImageView) promo_view.findViewById(R.id.img_promo_img);
        TextView promo_name = (TextView) promo_view.findViewById(R.id.promo_item_name);
        final TextView promo_price = (TextView) promo_view.findViewById(R.id.promo_price);
        TextView promo_identity_name = (TextView) promo_view.findViewById(R.id.promo_identity);
        final EditText mEtPromoItemQuantity = (EditText) promo_view.findViewById(R.id.etQuantity);
//        ImageView promo_for_checked = (ImageView) promo_view.findViewById(R.id.img_for_checked);
//        promo_for_checked.setVisibility(View.INVISIBLE);
        final CheckBox promo_check = (CheckBox) promo_view.findViewById(R.id.cb_for_promo_checked);

        promo_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Quantity: "+mEtPromoItemQuantity.getText(), Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                if(promo_check.isChecked()){
//                    mListSelectedItems.add(promo_list.get(position));
                    promoItem.addPromoItem(position,promo_list.get(position),mEtPromoItemQuantity.getText()+"");
                }else{
//                    mListSelectedItems.remove(promo_list.get(position));
                    promoItem.removePromoItem(position);
                }
            }
        });

        if(promo_list.get(position).getIdentity().equals("selling")){
            promo_identity_name.setText(promo_list.get(position).getIdentity());

        }

        if(userID.equals(promo_list.get(position).getUser_id())){
            promo_name.setText(promo_list.get(position).getDiyName());
        }

        promo_price.setText(promo_list.get(position).getSelling_price()+"");
        Glide.with(context).load(promo_list.get(position).getDiyUrl()).into(promo_picture);

        return promo_view;
    }
}
