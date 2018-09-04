package cabiso.daphny.com.g_companion.Search;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.R;

public class SearchActivity extends Activity {

    private EditText search_item;
    private ListView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference searchReference;
    private DatabaseReference ownerReference;

    private ArrayList<DIYnames> item_list;
    private ArrayList<DBMaterial> dbMaterials;

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
        recyclerView = (ListView) findViewById(R.id.lv_search);

        searchReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        ownerReference = FirebaseDatabase.getInstance().getReference().child("userdata");

        item_list = new ArrayList<>();

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
                } else {
                    setAdapter(search.substring(1));
                }
            }
        });

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DIYnames selected_diy = (DIYnames) parent.getAdapter().getItem(position);
                String name = (String) selected_diy.diyName;

                Log.d("name:",selected_diy.diyName);
                Intent intent = new Intent(SearchActivity.this, SearchView.class);
                intent.putExtra("name",name);

//                Bundle extra = new Bundle();
//                extra.putSerializable("dbmaterials", dbMaterials);
//                intent.putExtra("dbmaterials", extra);
                startActivity(intent);

            }
        });
    }

    private void setAdapter(final String diYnames){

        searchReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                item_list.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    DIYnames diy_names = snapshot.getValue(DIYnames.class);
                    String name = diy_names.getDiyName();

                    if(name.contains(diYnames)){
                        item_list.add(diy_names);
                    }
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, R.layout.search_item, item_list);
                recyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
