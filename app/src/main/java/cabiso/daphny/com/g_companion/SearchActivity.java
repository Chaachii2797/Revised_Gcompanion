package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Adapter.SearchAdapter;

public class SearchActivity extends Activity {

    private EditText search_item;
    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference searchReference;
    private DatabaseReference ownerReference;

    private ArrayList<String> item_list;
    private ArrayList<String> img_list;
    private ArrayList<String> identity_list;

    private SearchAdapter searchAdapter;
    private String searchString;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        userID = mFirebaseUser.getUid();

        // getIntent
        this.searchString = "";
        this.searchString = getIntent().getStringExtra("searchString");
        search_item = (EditText) findViewById(R.id.et_search_item);
        recyclerView = (RecyclerView) findViewById(R.id.search_recycler);

        searchReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        ownerReference = FirebaseDatabase.getInstance().getReference().child("userdata");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        item_list = new ArrayList<>();
        img_list = new ArrayList<>();
        identity_list = new ArrayList<>();

        search_item.setText(this.searchString);

        String search = this.searchString.toString();
        if(this.searchString.toString().isEmpty()){
            item_list.clear();
            recyclerView.removeAllViews();
        } else {
            setAdapter(search.substring(1));
        }

        search_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString();
                if(s.toString().isEmpty()){
                    item_list.clear();
                    recyclerView.removeAllViews();
                } else {
                    setAdapter(search.substring(1));
                }
            }
        });

    }

    private void setAdapter(final String diYnames){

        searchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item_list.clear();
                recyclerView.removeAllViews();

                int counter = 0;
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String name = snapshot.child("diyName").getValue(String.class);
                    String img = snapshot.child("diyUrl").getValue(String.class);
                    String identity = snapshot.child("identity").getValue(String.class);


                    if(name.contains(diYnames)){
                        item_list.add(name);
                        img_list.add(img);
                        identity_list.add(identity);
                        counter++;
                    }
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, item_list, img_list, identity_list);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
