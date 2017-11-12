package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by cicctuser on 7/31/2017.
 */

public class RecommendDIYAdapter extends ArrayAdapter<DIYnames> {

    private Activity context;
    private int resource;
    private ArrayList<DIYnames> listDIY;
    private int count=0;
    private ListView lv;

    public int heartCount=0;
    public int starCount=0;


    private FirebaseUser mFirebaseUser;
    private String userID;

    HashMap<String, Object> starResult = new HashMap<>();
    HashMap<String, Object> likeResult = new HashMap<>();


    public RecommendDIYAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDIY = objects;
    }



    @NonNull

    @Override
    public View getView(final int position, @Nullable final View convertView, @NonNull final ViewGroup parent) {
        final LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);
        TextView tvName = (TextView) v.findViewById(R.id.get_diyName);
        TextView tvcategory = (TextView) v.findViewById(R.id.tv_category);
        ImageView img = (ImageView) v.findViewById(R.id.diy_item_icon);
//        final ImageButton star = (ImageButton) v.findViewById(R.id.staru);
//        ImageButton heart = (ImageButton) v.findViewById(R.id.heartu);

        tvName.setText(listDIY.get(position).getDiyName());
//        tvcategory.setText(listDIY.get(position).getTag());
//        star.setTag(getItem(position).getBookmarks());
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags");


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        //tvName.setText("DIY Name: "+listDIY.get(position).getDiyName());
        Glide.with(context).load(listDIY.get(position).getDiyUrl()).into(img);

//        star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View view) {
//                Float position = (Float) view.getTag();
//                Toast.makeText(getContext(), "Bookmark DIY!", Toast.LENGTH_SHORT).show();
//                if (star.isPressed()) {
//                    count += 1;
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags");
//                    reference.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                             //   String key = dataSnapshot.getKey();
//
//                            String path = "/" + dataSnapshot.getKey();
//                            HashMap<String, Object> result = new HashMap<>();
//                            result.put("bookmarks", count);
//                            reference.child(path).updateChildren(result);
//                            star.setColorFilter(ContextCompat.getColor(getContext(), R.color.star_yello));
//
//                        }
//
//                        @Override
//                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                        }
//
//                        @Override
//                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//
//                }
//            }
//        });


//        heart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getContext(), "Liked DIY!", Toast.LENGTH_SHORT).show();
//                    }
//        });


                return v;
            }
    }
