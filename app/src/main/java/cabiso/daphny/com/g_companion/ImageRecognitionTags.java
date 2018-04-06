package cabiso.daphny.com.g_companion;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cabiso.daphny.com.g_companion.Adapter.ImgRecogSetQtyAdapter;
import cabiso.daphny.com.g_companion.Adapter.ImgRecogTagAdapter;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.ImgRecogSetQty;
import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import clarifai2.exception.ClarifaiException;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class ImageRecognitionTags extends AppCompatActivity{

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private ImageView imageView;
    private Button diyBtn, btnAdd_tag;
    private TextView tvTag, tv_category;
//    private EditText addMaterial;
//    private ImageButton btnMaterial;
    private ProgressDialog progressDialog;

    //added changes start
    private ImageButton btn_imagerecog;
    private ListView imagerecog_listview;
    private EditText add_tag;
    //end

    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();
    private ArrayList<ImgRecogSetQty> imgRecogSetQties;
    private ImgRecogSetQtyAdapter imgRecogSetQtyAdapter;
    private ArrayList<DBMaterial> dbMaterials;
    private ImgRecogTagAdapter imgRecogTagAdapter;
    final ClarifaiClient client;

    String[] unitOfMeasurement;
    String[] quantity;
    SpinnerAdapter nsAdapter;
    SpinnerAdapter1 qtyAdapter;

    private ListView mLvTags;

    public ImageRecognitionTags() {
        client = new ClarifaiBuilder("cb169e9d3f9e4ec5a7769cc0422f3162").buildSync();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recog_tag);

        getWordBank();

        diyBtn = (Button)findViewById(R.id.btnDIY);
        imageView = (ImageView)findViewById(R.id.imgPhotoSaver);
        tvTag = (TextView) findViewById(R.id.tvTag);
        mLvTags = (ListView) findViewById(R.id.lvTags);
        add_tag = (EditText) findViewById(R.id.tv_add_tag);
        btnAdd_tag = (Button) findViewById(R.id.btn_add_tag);

        imgRecogSetQties  = new ArrayList<>();
        ImgRecogSetQty item1 = new ImgRecogSetQty().setName("test11");
        ImgRecogSetQty item2 = new ImgRecogSetQty().setName("test22");
        imgRecogSetQties.add(item1);
        imgRecogSetQties.add(item2);

        //added changess star
        imagerecog_listview = (ListView) findViewById(R.id.image_recog_lv);

        Toolbar toolbar = (Toolbar) findViewById(R.id.imToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent im = new Intent(ImageRecognitionTags.this,MainActivity.class);
                startActivity(im);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        unitOfMeasurement = getResources().getStringArray(R.array.UM);
        nsAdapter= new SpinnerAdapter(getApplicationContext());

        dbMaterials = new ArrayList<>();
        imgRecogTagAdapter = new ImgRecogTagAdapter(ImageRecognitionTags.this,R.layout.img_recog_set_qty,dbMaterials);
        mLvTags.setAdapter(imgRecogTagAdapter);


        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                printTags();
            }
        });

        btnAdd_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String added_tag = add_tag.getText().toString();
                if(!added_tag.isEmpty()){
                    dbMaterials.add(new DBMaterial().setName(added_tag));
                    imgRecogTagAdapter.notifyDataSetChanged();
                    add_tag.setText("");
                }else{
                    Toast.makeText(ImageRecognitionTags.this, "Add tag cannot be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        quantity = getResources().getStringArray(R.array.qty);
        qtyAdapter=new SpinnerAdapter1(getApplicationContext());

        diyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageRecognitionTags.this,Bottle_Recommend.class);
                Bundle extra = new Bundle();
                extra.putSerializable("dbmaterials", dbMaterials);
                intent.putExtra("dbmaterials", extra);

                startActivity(intent);

                progressDialog = new ProgressDialog(ImageRecognitionTags.this);
                progressDialog.setTitle("Processing...");
                progressDialog.setMessage("Please wait loading DIYs...");
                progressDialog.show();

            }
        });
        dispatchTakePictureIntent();

    }

    public class SpinnerAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;

        public SpinnerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return unitOfMeasurement.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ListContent holder;
            View v = convertView;
            if (v == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.row_textview, null);
                holder = new ListContent();
                holder.text = (TextView) v.findViewById(R.id.textView1);

                v.setTag(holder);
            } else {
                holder = (ListContent) v.getTag();
            }
            holder.text.setText(unitOfMeasurement[position]);
            return v;
        }
    }

    public class SpinnerAdapter1 extends BaseAdapter {
        Context context;
        private LayoutInflater mInflater;

        public SpinnerAdapter1(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return quantity.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ContentQty holder1;
            View vi = convertView;
            if (vi == null) {
                mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                vi = mInflater.inflate(R.layout.row_textview, null);
                holder1 = new ContentQty();
                holder1.text1 = (TextView) vi.findViewById(R.id.textView1);

                vi.setTag(holder1);
            } else {
                holder1 = (ContentQty) vi.getTag();
            }
            holder1.text1.setText(quantity[position]);
            return vi;
        }
    }


    static class ListContent {
        TextView text;
    }

    public static class ContentQty {
        public TextView text1;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public void clearFields() {
        tags.clear();
        tvTag.setText("");
        ((ImageView)findViewById(R.id.imgPhotoSaver)).setImageResource(android.R.color.transparent);
    }

    public void printTags() {
        String results = "";

        dbMaterials.clear();
        for(int i = 0; i < 2; i++) {
            for(int c = 0; c < validWords.size(); c++){
                if(tags.get(i).contains(validWords.get(c))) {
                    results += "\n" + tags.get(i);
//                    tvTag.setText(results);
                    imgRecogSetQties.add(new ImgRecogSetQty().setName(results));
//                    addMaterial.setText(results);
                    Log.e("imageRecogItem",results);
                    dbMaterials.add(new DBMaterial().setName(tags.get(i)));
                }else{
                    //invalid words
                }
            }
        }

        imgRecogTagAdapter.notifyDataSetChanged();
        Log.e("imageRecogItemCount",imgRecogSetQties.size()+" ");
//        imgRecogSetQtyAdapter.notifyDataSetChanged();

    }

    public void getWordBank(){
        final DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("word_bank");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                validWords.add(dataSnapshot.getValue().toString());
                Log.d("fsdfs", dataSnapshot.getValue().toString());
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == MainActivity.RESULT_OK){
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                        byteArray.length);
                imageView.setImageBitmap(bitmap);

                new AsyncTask<Bitmap, Void, ClarifaiResponse<List<ClarifaiOutput<Concept>>>>() {
                    // Model prediction
                    @Override
                    protected ClarifaiResponse<List<ClarifaiOutput<Concept>>> doInBackground(Bitmap... bitmaps) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);

                        byte[] byteArray = stream.toByteArray();

                        final ConceptModel generalModel = client.getDefaultModels().generalModel();
//                        final Concept diy = client.getConceptByID("trash_DIY").executeSync().get().asConcept();

//                        ClarifaiResponse<List<Concept>> card = client.searchConcepts(String.valueOf(SearchClause.matchConcept(Concept
//                                .forName("cardboard")))).getPage(0).executeSync();

                        return generalModel.predict().withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray))).executeSync();
//                        return client.getDefaultModels().generalModel().predict()
//                                .withInputs(ClarifaiInput.forImage(ClarifaiImage.of(byteArray)))
//                                .executeSync();

                    }

                    // Handling API response and then collecting and printing tags
                    @Override
                    protected void onPostExecute(ClarifaiResponse<List<ClarifaiOutput<Concept>>> response) {
                        try{
                            if (!response.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "API contact error", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            final List<ClarifaiOutput<Concept>> predictions = response.get();
                            if (predictions.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "No results from API", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            final List<Concept> predictedTags = predictions.get(0).data();
                            for(int i = 0; i < predictedTags.size(); i++) {
                                tags.add(predictedTags.get(i).name());
                                extras.add(String.valueOf(predictedTags.get(i).value()));
                                ImgRecogSetQty imgRecogSetQty = new ImgRecogSetQty();
                                imgRecogSetQty.setName(predictedTags.get(i).name());
                                imgRecogSetQties.add(imgRecogSetQty);
                                Log.e("imgRecogSetQtiesSizeLp",imgRecogSetQties.size()+"");
                            }
                            printTags();
                            Log.e("imgRecogSetQtiesSize",imgRecogSetQties.size()+"");
//                            imgRecogSetQtyAdapter.notifyDataSetChanged();
                        }catch (ClarifaiException ex){
                                ex.getMessage();
                            }
                        }
                }.execute(bitmap);
            }
//            imgRecogSetQtyAdapter.notifyDataSetChanged();
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture or selection.
            Toast.makeText(getApplicationContext(), "User Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            // capture failed or did not find file.
            Toast.makeText(getApplicationContext(), "Unknown Failure. Please notify app owner.", Toast.LENGTH_SHORT).show();
        }
        imgRecogSetQties.add(new ImgRecogSetQty().setName("RODRIGO"));
        Log.e("imgRecogSetQties",imgRecogSetQties+"");
        Log.e("imgRecogSetQties222",imgRecogSetQties+"");
//        imgRecogSetQtyAdapter.notifyDataSetChanged();


    }

}
