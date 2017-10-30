package cabiso.daphny.com.g_companion.Recommend;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    public RecommendDIYAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull ArrayList<DIYnames> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        listDIY = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View v = inflater.inflate(resource, null);
        TextView tvName = (TextView) v.findViewById(R.id.get_diyName);
        ImageView img = (ImageView) v.findViewById(R.id.diy_item_icon);
        final ImageButton star = (ImageButton) v.findViewById(R.id.staru);
        ImageButton heart = (ImageButton) v.findViewById(R.id.heartu);

        tvName.setText(listDIY.get(position).getDiyName());
        star.setTag(listDIY.get(position));
        heart.setTag(listDIY.get(position));

        //tvName.setText("DIY Name: "+listDIY.get(position).getDiyName());
        Glide.with(context).load(listDIY.get(position).getDiyUrl()).into(img);

//        star.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count++;
//                if(count==1){
//                    star.setColorFilter(ContextCompat.getColor(context, R.color.star_yello));
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DIYs_By_Users").child("bottle");
//                    reference.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                String key = snapshot.getKey();
//                                String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                HashMap<String, Object> result = new HashMap<>();
//                                result.put("bookmarks",count);
//                                reference.child(path).updateChildren(result);
//                            }
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
//                }else if(count==2){
//                    count=0;
//                    star.setColorFilter(ContextCompat.getColor(context, R.color.for_star));
//                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DIYs_By_Users").child("bottle");
//                    reference.addChildEventListener(new ChildEventListener() {
//                        @Override
//                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                            for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                String key = snapshot.getKey();
//                                String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                HashMap<String, Object> result = new HashMap<>();
//                                result.put("bookmarks",count);
//                                reference.child(path).updateChildren(result);
//                            }
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
//                }
//            }
//        });

        return v;
    }
}

