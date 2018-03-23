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

import cabiso.daphny.com.g_companion.Recommend.Bottle_Recommend;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiImage;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.ConceptModel;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

/**
 * Created by Lenovo on 7/30/2017.
 */

public class ImageRecognitionTags extends AppCompatActivity{

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private ImageView imageView;
    private Button diyBtn;
    private TextView tvTag, tv_category;
    private EditText addMaterial;
    private ImageButton btnMaterial;
    private ProgressDialog progressDialog;

    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();
    final ClarifaiClient client;
    String CURRENT_MODEL;

    String[] unitOfMeasurement;
    String[] quantity;
    String spinner_item_um;
    String spinner_item_q;
    SpinnerAdapter nsAdapter;
    SpinnerAdapter1 qtyAdapter;

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

        addMaterial = (EditText) findViewById(R.id.addMaterials);
        btnMaterial = (ImageButton) findViewById(R.id.btnAddMaterial);

        unitOfMeasurement = getResources().getStringArray(R.array.UM);
        nsAdapter= new SpinnerAdapter(getApplicationContext());

        imageView.setClickable(true);
        addMaterial.setText(" ");
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                printTags();
            }
        });
        quantity = getResources().getStringArray(R.array.qty);
        qtyAdapter=new SpinnerAdapter1(getApplicationContext());

        btnMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ImageRecognitionTags.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.row_spinner);
                dialog.setCancelable(true);

                final Spinner spinnerUM = (Spinner) dialog.findViewById(R.id.spinner1);
                final Spinner spinnerQty = (Spinner) dialog.findViewById(R.id.qtySpinner);
                Button okButton = (Button) dialog.findViewById(R.id.okaybtn);

                spinnerQty.setAdapter(qtyAdapter);
                spinnerUM.setAdapter(nsAdapter);

                spinnerUM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        spinner_item_um = unitOfMeasurement[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                spinnerQty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        spinner_item_q = quantity[position];
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                String inputMaterials = addMaterial.getText().toString();
                if (inputMaterials.isEmpty()) {
                    Toast.makeText(ImageRecognitionTags.this, "Please enter materials", Toast.LENGTH_SHORT).show();
                } else {


                    Toast.makeText(ImageRecognitionTags.this, spinner_item_q + spinner_item_um, Toast.LENGTH_SHORT).show();
                }

                dialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String quantityMaterial = spinner_item_q + " " + spinner_item_um + " " + addMaterial.getText().toString();

                        tvTag.append("\n" + quantityMaterial);

                        Toast.makeText(ImageRecognitionTags.this, spinner_item_q + " " +spinner_item_um + " " +
                                addMaterial.getText().toString(), Toast.LENGTH_SHORT).show();
                        addMaterial.setText("");
                        dialog.dismiss();

                    }
                });

            }
        });

        diyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String results = " ";
                String priority = " ";

                for(int i = 0; i < 10; i++) {
                    results += " "+tags.get(i);
                    priority +=" "+extras.get(i);
                    Log.e("RESULT: ",""+results);
                    Intent intent = new Intent(ImageRecognitionTags.this,Bottle_Recommend.class);

                    String quantity_unit = spinner_item_q + " " + spinner_item_um;
                    intent.putExtra("result_priority", priority);
                    intent.putExtra("qty_unit", quantity_unit);
                    intent.putExtra("result_tag", results);

                    Log.e("PRIORITY: ",""+priority);
                    Log.e("RESULTS: ",""+results);
                    Log.e("qty_unit: ",""+quantity_unit);

                    startActivity(intent);

                    progressDialog = new ProgressDialog(ImageRecognitionTags.this);
                    progressDialog.setTitle("Processing...");
                    progressDialog.setMessage("Please wait loading DIYs...");
                    progressDialog.show();
                }
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

    static class ContentQty {
        TextView text1;
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
        for(int i = 0; i < 10; i++) {
            for(int c = 0; c < validWords.size(); c++){
                if(tags.get(i).contains(validWords.get(c))){
                    results += "\n" + tags.get(i);
                    Log.e("TAGS:", " " +results);

                    Log.e("value_here",extras.get(i));
                    //tvTag.setText(results);
                    addMaterial.setText(results);


                }else{
                    //invalid words
                }
            }
            Log.e("tags: ", " "+results);
            Log.e("value",extras.get(i));

        }
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
                        }
                        printTags();
                    }
                }.execute(bitmap);
            }
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture or selection.
            Toast.makeText(getApplicationContext(), "User Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            // capture failed or did not find file.
            Toast.makeText(getApplicationContext(), "Unknown Failure. Please notify app owner.", Toast.LENGTH_SHORT).show();
        }

    }

}
