package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Lenovo on 3/23/2018.
 */

public class YouItemsFragment extends Fragment {

    private ArrayList<DIYnames> myItems_diyList = new ArrayList<>();
    private ArrayList<CommunityItem> infoList = new ArrayList<>();
    private ListView lv;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private FirebaseUser mFirebaseUser;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private String userID;
    private ImageButton heart, star;

    public YouItemsFragment() {

    }

    public static YouItemsFragment newInstance() {

        YouItemsFragment fragment = new YouItemsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return new YouItemsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        database = FirebaseDatabase.getInstance();

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_items_layout, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        lv = (ListView) view.findViewById(R.id.myItems_lv);

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setMessage("Please Wait loading DIYs.....");
//        progressDialog.show();

        heart = (ImageButton) view.findViewById(R.id.heartu);
        star = (ImageButton) view.findViewById(R.id.staru);


        return view;
    }

    @Override
    public void onStart(){
       super.onStart();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("diy_by_tags");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //if (snapshot.hasChildren()) {
//
//                        Collections.sort(diyList);
//                        Collections.reverse(diyList);

                       // progressDialog.dismiss();

                        Toast.makeText(getContext(), "MY DIYS!", Toast.LENGTH_SHORT).show();

                        DIYnames img = dataSnapshot.getValue(DIYnames.class);
                        myItems_diyList.add(img);

//                        CommunityItem mp = dataSnapshot.getValue(CommunityItem.class);
//                        infoList.add(mp);

                        adapter = new RecommendDIYAdapter(getActivity(), R.layout.pending_layout, myItems_diyList);

                        lv.setAdapter(adapter);

//                        if(snapshot.getChildrenCount() == diyList.size()){
//                            for(int i=0; i<diyList.size();i++){
//                                Log.e("get "," "+diyList.get(i).getSold_items());
//                            }f
//                        }



//                        registerForContextMenu(lv);
                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), myItems_diyList.get(position).getDiyName(), Toast.LENGTH_SHORT).show();

                                DIYnames selectedItem = adapter.getItem(position);
                                if(selectedItem.getUser_id()!=null){
//                                    star.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Toast.makeText(getApplicationContext(),"CLIIIIIIIICK!", Toast.LENGTH_SHORT).show();
//                                            if(star.isPressed()){
//                                                count+=1;
//                                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("diy_by_tags").child(userID);
//                                                reference.addChildEventListener(new ChildEventListener() {
//                                                    @Override
//                                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                                                        for(DataSnapshot snapshot:dataSnapshot.getChildren()){
//                                                            String key = snapshot.getKey();
//                                                            String path = "/" + dataSnapshot.getKey() + "/" + key;
//                                                            HashMap<String, Object> result = new HashMap<>();
//                                                            result.put("bookmarks",count);
//                                                            reference.child(path).updateChildren(result);
                                    star.setColorFilter(ContextCompat.getColor(getActivity(), R.color.star_yello));
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(DatabaseError databaseError) {
//
//                                                    }
//                                                });
//                                            }
//                                        }
//                                    });


                                }
                                //To-DO get you data from the ItemDetails Getter
                                // selectedItem.getImage() or selectedItem.getName() .. etc
                                // the  send the data using intent when opening another activity
                                Intent intent = new Intent(getActivity(), DIYDetailViewActivity.class);
//                                String items = infoList.get(position).getVal();

                                CommunityItem mat = (CommunityItem) parent.getItemAtPosition(position);


                                adapter.notifyDataSetChanged();
//                                Toast toast = Toast.makeText(MyDiys.this, items, Toast.LENGTH_SHORT);
//                                toast.show();
                                intent.putExtra("image", selectedItem.getDiyUrl().toString());
                                intent.putExtra("name", selectedItem.getDiyName());
                                // intent.putExtra("procedures", infoList.get(position));
                                // intent.putExtra("materials", selectedItem.getDiymaterial());


                                view.buildDrawingCache();
                                Bitmap image = view.getDrawingCache();
                                Bundle extras = new Bundle();
                                extras.putParcelable("imagebitmap", image);

                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                intent.putExtra("image", byteArray);
                                startActivity(intent);
                            }
                        });
                  //  }
            //    }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
