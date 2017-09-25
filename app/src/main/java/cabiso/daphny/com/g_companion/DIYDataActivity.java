package cabiso.daphny.com.g_companion;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import cabiso.daphny.com.g_companion.Recommend.DIYrecommend;
import cabiso.daphny.com.g_companion.Recommend.RecommendDIYAdapter;

/**
 * Created by Lenovo on 9/23/2017.
 */

public class DIYDataActivity extends AppCompatActivity {
    private ArrayList<DIYrecommend> diyList = new ArrayList<>();
    private ListView lv;
    private RecommendDIYAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;

    private TextView diy_name, diy_materials, diy_procedures;
    private ImageView diy_image;
    String nameList;
    String materialsList;
    String proceduresList;
    String imageList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diy_data);

        progressDialog = new ProgressDialog(this);


        diy_name = (TextView) findViewById(R.id.diy_name);
        diy_image =(ImageView) findViewById(R.id.diy_image);
        diy_materials = (TextView) findViewById(R.id.diy_materials);
        diy_procedures = (TextView) findViewById(R.id.diy_procedures);


//        Intent i = getIntent();
//        String image = i.getStringExtra("image");
//        int imageID = i.getIntExtra("image",0);
//        String name = i.getStringExtra("name");
//        String materials = i.getStringExtra("materials");
//        String procedures = i.getStringExtra("procedures");

        getIntent().getStringExtra("image");
        int imageID =getIntent().getIntExtra("image",0);
        getIntent().getStringExtra("name");
        getIntent().getStringExtra("materials");
        getIntent().getStringExtra("procedures");





//        Bundle extras = getIntent().getExtras();
//        nameList = extras.getString("get");
//        materialsList = extras.getString("gets");
//        proceduresList = extras.getString("getss");
//        imageList = (String) extras.get("getsss");


      // diy_name.setText(name);
//        diy_materials.setText(materialsList);
//        diy_procedures.setText(proceduresList);
//        diy_image.setImageURI(Uri.parse(imageList));


  //      diy_image.setImageResource(imageID);

        diy_name.setText(getIntent().getStringExtra("name"));
        diy_materials.setText(getIntent().getStringExtra("materials"));
        diy_procedures.setText(getIntent().getStringExtra("procedures"));

        Bundle extras = getIntent().getExtras();
        byte[] byteArray = extras.getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        diy_image.setImageBitmap(bmp);


    }


}
