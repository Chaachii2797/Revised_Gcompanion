package cabiso.daphny.com.g_companion.EditData;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Adapter.DBMaterialsAdapter;
import cabiso.daphny.com.g_companion.MainActivity;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.Constants;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Bidding.DIYBidding;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Model.TagClass;
import cabiso.daphny.com.g_companion.R;

/**
 * Created by Lenovo on 8/13/2018.
 */

public class EditDIYDetailsActivity extends AppCompatActivity implements View.OnClickListener{


    private DatabaseReference databaseReference;
    private DatabaseReference byuser_Reference;
    private DatabaseReference biddingReference;
    private DatabaseReference donebiddingReference;

    private FirebaseStorage mStorage;
    private FirebaseDatabase database;
    private StorageReference storageReference, imageRef;
    private Uri diyPictureUri;

    private StorageReference storageRef, imgRef;

    private FirebaseUser mFirebaseUser;
    private String userID;
    private String imageFileName;
    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();

    private static final int SELECT_PHOTO = 100;
    private static final int MAX_LENGTH = 100;
    private Button diy_Button, sellButton, bidButton;
    private ImageButton btnAddMaterial, btnAddProcedure;
    private TagView tagGroup;
    private EditText name, material, procedure;
    private ImageView imgView;
    private ListView materialsList;
    private ListView proceduresList;
    private CommunityAdapter pAdapter;
    private CommunityAdapter mAdapter;
    private Spinner categorySpinner;
    private DBMaterialsAdapter dbMaterialsAdapter;


    //for bidding
    private EditText mEtPriceMin;
    private EditText mEtPriceMessage;
    private EditText mEtExpiryDate;
    private TextView mTvDateToday;
    private String diyName, diyOwner, diyKey, diyPrice, diyQty, diyDsc, diyBidInitialPrice, diyBidExpiry,
            diyBidComment, diyKeyInside, diyImage;
    private Button mBtnAddBid, mDeleteDIYBtn;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    Calendar myCalendar = Calendar.getInstance();

    ArrayList<CommunityItem> itemMaterial;
    ArrayList<CommunityItem> itemMat;
    ArrayList<CommunityItem> itemForMaterials;
    ArrayList<CommunityItem> itemProcedure;
    ArrayList<QuantityItem> itemQuantity;
    ArrayList<DBMaterial> dbMaterials;
    ArrayList<SellingDIY> dbSelling;
    ArrayList<DIYBidding> dbBidding;

    ArrayList<QuantityItem> itemUnit;
    private List<String> diys = new ArrayList<String>();

    private ProgressDialog progressDialog;
    UploadTask uploadTask;

    private ArrayList<TagClass> tagList;
    String[] unitOfMeasurement;
    String[] quantity;
    String spinner_item_um;
    String spinner_item_q;
    SpinnerAdapter umAdapter;
    SpinnerAdapter1 qAdapter;

    public String photoFileName = "diy_by_tags";
    File photoFile;
    public final String APP_TAG = "diy_by_tags";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_diy_details_activity);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        //get Intent from DIYDetailViewActivity for Edit DIY
        final Intent intent = getIntent();
        diyKey = intent.getExtras().getString("diyKey");
        diyName = intent.getExtras().getString("diyName");
        diyOwner = intent.getExtras().getString("diyOwner");
        int diyCategory = intent.getIntExtra("diyCategory", 0);
        diyPrice = intent.getExtras().getString("diyPrice");
        diyQty = intent.getExtras().getString("diyQuantity");
        diyDsc =intent.getExtras().getString("diyDescription");
        diyBidInitialPrice = intent.getExtras().getString("diyBidInitialPrice");
        diyBidExpiry = intent.getExtras().getString("diyBidExpiry");
        diyBidComment = intent.getExtras().getString("diyBidComment");
        diyKeyInside = intent.getExtras().getString("diyKeyInside");
        diyImage = intent.getExtras().getString("diyImage");


        Log.e("diyImageee", "" + diyImage);
        Log.e("diyBidInitialPriceee", "" + diyBidInitialPrice);
        Log.e("diyBidCommentttt","" + diyBidComment);
        Log.e("diyBidExpiryyyy","" + diyBidExpiry);

        Log.e("diyPriceeee","" + diyPrice);
        Log.e("diyDscsss", "" +diyDsc);
        Log.e("diyQtyyyy","" + diyQty);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.diyKey);
        byuser_Reference = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID).child(this.diyKey);
        biddingReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.diyKey)
                .child("bidding");

        donebiddingReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags").child(this.diyKey);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/").child("diy_by_tags");

        storageRef = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/").child("diy_by_tags");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddDIY);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent im = new Intent(EditDIYDetailsActivity.this,MainActivity.class);
                startActivity(im);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        name = (EditText) findViewById(R.id.add_diy_name);
        name.setText(diyName);

        material = (EditText) findViewById(R.id.etMaterials);

        tagGroup = (TagView) findViewById(R.id.tag_group);

        //for DIY catagory spinner
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        categorySpinner.setSelection(diyCategory);

        unitOfMeasurement = getResources().getStringArray(R.array.UM);
        umAdapter=new SpinnerAdapter(getApplicationContext());

        quantity = getResources().getStringArray(R.array.qty);
        qAdapter= new SpinnerAdapter1(getApplicationContext());


        material.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTags(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tagGroup.setOnTagLongClickListener(new TagView.OnTagLongClickListener() {
            @Override
            public void onTagLongClick(Tag tag, int position) {
                Toast.makeText(EditDIYDetailsActivity.this, "Long Click: " + tag.text, Toast.LENGTH_SHORT).show();
            }
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                material.setText(tag.text);
                material.setSelection(tag.text.length());//to set cursor position

            }
        });
        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditDIYDetailsActivity.this, R.style.AppTheme);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(EditDIYDetailsActivity.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });



        procedure = (EditText) findViewById(R.id.etProcedures);
        imgView = (ImageView) findViewById(R.id.add_product_image_plus_icon);

        diyImage= String.valueOf(intent.getStringExtra("diyImage"));

        Glide.with(this)
                .load(diyImage)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter().crossFade()
                .into(imgView);

        materialsList = (ListView) findViewById(R.id.materialsList);

        proceduresList = (ListView) findViewById(R.id.proceduresList);
        btnAddMaterial = (ImageButton) findViewById(R.id.btnMaterial);
        btnAddProcedure = (ImageButton) findViewById(R.id.btnProcedure);
        mDeleteDIYBtn = (Button) findViewById(R.id.deleteDIYBn);

        itemMaterial = new ArrayList<>();
        itemProcedure = new ArrayList<>();
        itemUnit = new ArrayList<>();
        itemMat = new ArrayList<>();
        itemQuantity = new ArrayList<>();
        dbMaterials = new ArrayList<>();
        dbSelling = new ArrayList<>();
        dbBidding = new ArrayList<>();

        mAdapter = new CommunityAdapter(getApplicationContext(), itemMaterial);
        pAdapter = new CommunityAdapter(getApplicationContext(), itemProcedure);


        materialsList.setAdapter(mAdapter);
        proceduresList.setAdapter(pAdapter);

        String[] values = new String[] { "Quantity" };

        getWordBank();
        prepareTags();

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("INTENT_NAME"));

        mDeleteDIYBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EditDIYDetailsActivity.this);
                builder.setTitle("Delete DIY: ");
                builder.setMessage("Are you sure? ");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(EditDIYDetailsActivity.this, "User Unblock!", Toast.LENGTH_SHORT).show();

                        HashMap<String, Object> bidDone = new HashMap<>();
                        bidDone.put("identity", "Done Bidding");
                        donebiddingReference.updateChildren(bidDone);

                        HashMap<String, Object> bidDoneByUser = new HashMap<>();
                        bidDoneByUser.put("identity", "Done Bidding");
                        byuser_Reference.updateChildren(bidDoneByUser);

                        Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.child("materials").getChildren()) {
                   Log.e("postSnapshot", String.valueOf(postSnapshot));

                    String material_name = postSnapshot.child("name").getValue(String.class);
                    int material_qty = postSnapshot.child("quantity").getValue(int.class);
                    String material_unit = postSnapshot.child("unit").getValue(String.class);
                    Log.e("material_name",material_name);
                    Log.e("material_qty", String.valueOf(material_qty));
                    Log.e("material_unit",material_unit);

                    // Putting Data into Getter Setter \\
                    DBMaterial matList = new DBMaterial();
                    matList.setName(material_name);
                    matList.setQuantity(material_qty);
                    matList.setUnit(material_unit);

                    dbMaterials.add(matList);
                    mAdapter.notifyDataSetChanged();
                    dbMaterialsAdapter = new DBMaterialsAdapter(getApplicationContext(), dbMaterials);

                    materialsList.setAdapter(dbMaterialsAdapter);


                }

                for (DataSnapshot postSnapshots : dataSnapshot.child("procedures").getChildren()) {
                    String diy_procedures = postSnapshots.child("val").getValue(String.class);
                    Log.e("diy_procedures", diy_procedures);

                    CommunityItem prodList = new CommunityItem();
                    prodList.setVal(diy_procedures);

                    itemProcedure.add(prodList);
                    pAdapter.notifyDataSetChanged();
                    pAdapter = new CommunityAdapter(getApplicationContext(), itemProcedure);
                    proceduresList.setAdapter(pAdapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



// ADD DIY TO THE COMMUNITY
        btnAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputMaterials = material.getText().toString();
                if (inputMaterials.isEmpty()) {
                    Toast.makeText(EditDIYDetailsActivity.this, "Please enter materials", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditDIYDetailsActivity.this, spinner_item_q + spinner_item_um, Toast.LENGTH_SHORT).show();


                    final Dialog dialog = new Dialog(EditDIYDetailsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.row_spinner);
                    dialog.setCancelable(true);

                    final Spinner sp_for_unit = (Spinner) dialog.findViewById(R.id.unitspinner);
                    final Spinner sp_for_qty = (Spinner) dialog.findViewById(R.id.qtySpinner);
                    Button okButton = (Button) dialog.findViewById(R.id.okaybtn);

                    sp_for_unit.setAdapter(umAdapter);
                    sp_for_qty.setAdapter(qAdapter);

                    sp_for_unit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                    sp_for_qty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


                    dialog.show();

                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String quantityMaterials = spinner_item_q + " " + spinner_item_um + " " + material.getText().toString();
                            String unit_material = spinner_item_um;
                            int quantity = Integer.parseInt(spinner_item_q);
                            String materials = material.getText().toString();

                            CommunityItem qm = new CommunityItem(quantityMaterials);
                            CommunityItem mat = new CommunityItem(materials);

                            QuantityItem qty_qty_mat = new QuantityItem(quantity);
                            QuantityItem _unit = new QuantityItem(unit_material);
                            itemMaterial.add(qm);

                            dbMaterials.add(new DBMaterial().setName(materials).setQuantity(quantity).setUnit(unit_material));
                            itemQuantity.add(qty_qty_mat);
                            itemUnit.add(_unit);
                            itemMat.add(mat);

                            mAdapter.notifyDataSetChanged();
                            material.setText("");

                            Toast.makeText(EditDIYDetailsActivity.this, spinner_item_q + " " + spinner_item_um, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });

        btnAddProcedure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputProcedure = procedure.getText().toString();
                if (inputProcedure.isEmpty()) {
                    Toast.makeText(EditDIYDetailsActivity.this, "Please enter procedures", Toast.LENGTH_SHORT).show();
                } else {
                    CommunityItem md = new CommunityItem(inputProcedure);
                    itemProcedure.add(md);
                    pAdapter.notifyDataSetChanged();
                    procedure.setText(" ");

                }
            }
        });


        final ImageView addProductImagePlusIcon = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        addProductImagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //Add DIY to the community ->upload data to firebase
        diy_Button = (Button) findViewById(R.id.communityDiy);
        database = FirebaseDatabase.getInstance();
        diy_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                Toast.makeText(EditDIYDetailsActivity.this, "Clicked position : " +
                        categorySpinner.getSelectedItemPosition(), Toast.LENGTH_SHORT).show();

                HashMap<String, Object> results = new HashMap<>();
                results.put("diyName", name.getText().toString());
                results.put("category", categorySpinner.getSelectedItem());
                results.put("materials", dbMaterials);
                results.put("procedures", itemProcedure);

                Log.e("name", name.getText().toString());
                Log.e("categoryyy", String.valueOf(categorySpinner.getSelectedItem()));
                Log.e("materialssss", String.valueOf(dbMaterials));
                Log.e("proceduressss", String.valueOf(itemProcedure));

                databaseReference.updateChildren(results);
                byuser_Reference.updateChildren(results);

                Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();


//                imageRef = storageReference.child(String.valueOf(name.getText()));
//
//                //creating and showing progress dialog
//                progressDialog = new ProgressDialog(EditDIYDetailsActivity.this);
//                progressDialog.setMax(100);
//                progressDialog.setMessage("Adding DIY to the Community...");
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.show();
//                progressDialog.setCancelable(false);
//
////                //starting upload
//                uploadTask = imageRef.putFile(Uri.fromFile(photoFile));
////                // Observe state change events such as progress, pause, and resume
//                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        //sets and increments value of progressbar
//                        progressDialog.incrementProgressBy((int) progress);
//                    }
//                });
//                // Register observers to listen for when the download is done or if it fails
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                        Toast.makeText(EditDIYDetailsActivity.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                        HashMap<String, Object> results = new HashMap<>();
//                        results.put("diyName", name.getText().toString());
//                        results.put("diyUrl", taskSnapshot.getDownloadUrl().toString());
//                        results.put("category", categorySpinner.getSelectedItem());
//                        results.put("materials", dbMaterials);
//                        results.put("procedures", itemProcedure);
//
//                        Log.e("name", name.getText().toString());
//                        Log.e("categoryyy", String.valueOf(categorySpinner.getSelectedItem()));
//                        Log.e("materialssss", String.valueOf(dbMaterials));
//                        Log.e("proceduressss", String.valueOf(itemProcedure));
//                        Log.e("diyUrllll", taskSnapshot.getDownloadUrl().toString());
//
//
//                        databaseReference.updateChildren(results);
//                        byuser_Reference.updateChildren(results);
//
//
//                        Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
//                        startActivity(intent);
//
//
//                        Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
//
//                        progressDialog.dismiss();
//
//                    }
//                });



            }
        });


        //SELL DIY TO THE COMMUNITY
        sellButton = (Button) findViewById(R.id.sellDiy);
        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(EditDIYDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.sell_diy_option);
                dialog.setCancelable(true);

                final EditText etPrice = (EditText) dialog.findViewById(R.id.etPrice);
                etPrice.setText(diyPrice);

                final EditText etQuantity = (EditText) dialog.findViewById(R.id.etQty);
                etQuantity.setText(diyQty);

                final EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
                etDescription.setText(diyDsc);

                Button sellOkButton = (Button) dialog.findViewById(R.id.okaybtn);


                dialog.show();

                sellOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(EditDIYDetailsActivity.this, etPrice.getText() + "," + etQuantity.getText() + "," + etDescription.getText(),
                                Toast.LENGTH_SHORT).show();

                        String for_price = etPrice.getText().toString();
                        String for_qty = etQuantity.getText().toString();
                        final String for_descr = etDescription.getText().toString();
                        final float price = Float.parseFloat((for_price));
                        final int qty = Integer.parseInt(for_qty);

                        dbSelling.add(new SellingDIY().setSelling_price(price).setSelling_qty(qty).setSelling_descr(for_descr));
                        HashMap<String, Object> sellResults = new HashMap<>();
                        sellResults.put("diyName", name.getText().toString());
                        sellResults.put("category", categorySpinner.getSelectedItem());
                        sellResults.put("DIY Price", dbSelling);
                        sellResults.put("materials", dbMaterials);
                        sellResults.put("procedures", itemProcedure);

                        Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();

                        databaseReference.updateChildren(sellResults);
                        byuser_Reference.updateChildren(sellResults);

                        Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
                        startActivity(intent);


//                        imgRef = storageRef.child(String.valueOf(name.getText()));
//
////                        //starting upload
//                        uploadTask = imgRef.putFile(Uri.fromFile(photoFile));
////                        // Observe state change events such as progress, pause, and resume
//                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//
//                            }
//                        });
////                        // Register observers to listen for when the download is done or if it fails
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
////                                // Handle unsuccessful uploads
//                                Toast.makeText(EditDIYDetailsActivity.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                                String for_price = etPrice.getText().toString();
//                                String for_qty = etQuantity.getText().toString();
//                                final String for_descr = etDescription.getText().toString();
//                                final float price = Float.parseFloat((for_price));
//                                final int qty = Integer.parseInt(for_qty);
//
//                                dbSelling.add(new SellingDIY().setSelling_price(price).setSelling_qty(qty).setSelling_descr(for_descr));
//                                HashMap<String, Object> sellResults = new HashMap<>();
//                                sellResults.put("diyName", name.getText().toString());
//                                sellResults.put("diyUrl", taskSnapshot.getDownloadUrl().toString());
//                                sellResults.put("category", categorySpinner.getSelectedItem());
//                                sellResults.put("DIY Price", dbSelling);
//                                sellResults.put("materials", dbMaterials);
//                                sellResults.put("procedures", itemProcedure);
//
//                                Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
//
//                                databaseReference.updateChildren(sellResults);
//                                byuser_Reference.updateChildren(sellResults);
//
//                                Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//
//                        dialog.dismiss();

                    }
                });


            }
        });


        //Add DIY for Bidding
        bidButton = (Button) findViewById(R.id.bidDiy);
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditDIYDetailsActivity.this, "Bid button clicked", Toast.LENGTH_SHORT).show();

                final Dialog dialog = new Dialog(EditDIYDetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bidding_dialog);
                dialog.show();

                mEtPriceMin = (EditText) dialog.findViewById(R.id.etBidInitialPrice);
                mEtPriceMin.setText(diyBidInitialPrice);

                mEtPriceMessage = (EditText) dialog.findViewById(R.id.etBidMessage);
                mEtPriceMessage.setText(diyBidComment);

                mEtExpiryDate = (EditText) dialog.findViewById(R.id.etBidExpiryDate);
                mEtExpiryDate.setText(diyBidExpiry);

                mTvDateToday = (TextView) dialog.findViewById(R.id.tv_on_date_today);
                mBtnAddBid = (Button) dialog.findViewById(R.id.btnAddBid);

                mTvDateToday.setText(sdate);

                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                userID = mFirebaseUser.getUid();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }

                };

                mEtExpiryDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(EditDIYDetailsActivity.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

//                mEtExpiryDate.setOnClickListener(this);

                mBtnAddBid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(EditDIYDetailsActivity.this, "Bid Info button clicked", Toast.LENGTH_SHORT).show();

                        DIYBidding formBidding = getFormInput();

                        HashMap<String, Object> bidResults = new HashMap<>();
                        bidResults.put(diyKeyInside, formBidding);
                        biddingReference.updateChildren(bidResults);

                        HashMap<String, Object> bid = new HashMap<>();
                        bid.put("diyName", name.getText().toString());
                        bid.put("category", categorySpinner.getSelectedItem());
                        bid.put("materials", dbMaterials);
                        bid.put("procedures", itemProcedure);
                        databaseReference.updateChildren(bid);

                        HashMap<String, Object> bidResultsByUser = new HashMap<>();
                        bidResultsByUser.put("bidding", formBidding);
                        byuser_Reference.updateChildren(bidResultsByUser);

                        Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
                        startActivity(intent);

                        Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();


//                        imgRef = storageRef.child(String.valueOf(name.getText()));
//
////                        //starting upload
//                        uploadTask = imgRef.putFile(Uri.fromFile(photoFile));
//                        // Observe state change events such as progress, pause, and resume
//                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            }
//                        });
////                        // Register observers to listen for when the download is done or if it fails
//                        uploadTask.addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception exception) {
//                                // Handle unsuccessful uploads
//                                Toast.makeText(EditDIYDetailsActivity.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//
//                                DIYBidding formBidding = getFormInput();
//
//                                HashMap<String, Object> bidResults = new HashMap<>();
//                                bidResults.put(diyKeyInside, formBidding);
//                                biddingReference.updateChildren(bidResults);
//
//                                HashMap<String, Object> bid = new HashMap<>();
//                                bid.put("diyName", name.getText().toString());
//                                bid.put("diyUrl", taskSnapshot.getDownloadUrl().toString());
//                                bid.put("category", categorySpinner.getSelectedItem());
//                                bid.put("materials", dbMaterials);
//                                bid.put("procedures", itemProcedure);
//                                databaseReference.updateChildren(bid);
//
//                                HashMap<String, Object> bidResultsByUser = new HashMap<>();
//                                bidResultsByUser.put("bidding", formBidding);
//                                byuser_Reference.updateChildren(bidResultsByUser);
//
//                                Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
//                                startActivity(intent);
//
//                                Toast.makeText(EditDIYDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
//
//
//                            }
//                        });
//
//                        dialog.dismiss();
                    }
                });

            }
        });


    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedMaterial = intent.getStringExtra("materialName");
            String receivedProcedure = intent.getStringExtra("procedureName");
//            String receivedImage = intent.getStringExtra("diyImageUri");

            material.setText(receivedMaterial);
            procedure.setText(receivedProcedure);

            Log.e("receivedMaterial", "" + receivedMaterial);
            Log.e("receivedProcedure", "" + receivedProcedure);
//            Log.e("receivedImage", "" + receivedImage);
//
//            Log.e("Check", "Inside On Receiver");
//            Toast.makeText(getApplicationContext(), "received",
//                    Toast.LENGTH_LONG).show();
        }
    };


    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        mEtExpiryDate.setText(sdf.format(myCalendar.getTime()));
    }

    private DIYBidding getFormInput(){
        return new DIYBidding()
                .setBidder(this.userID)
                .setMessage(this.mEtPriceMessage.getText()+"")
                .setInitialPrice(Integer.parseInt(this.mEtPriceMin.getText()+""))
                .setDate(sdate)
                .setXpire_date(mEtExpiryDate.getText().toString());
    }

    @Override
    public void onClick(View v) {
        if(v==diy_Button){
            registerForContextMenu(diy_Button);
            openContextMenu(diy_Button);
        }

    }



    public static String generateString() {
        String prod_id = UUID.randomUUID().toString();
        return prod_id;
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

    //Materials Tags
    private void prepareTags() {
        tagList = new ArrayList<>();
        JSONArray jsonArray;
        JSONObject temp;
        try {
            jsonArray = new JSONArray(Constants.MATERIALS);
            for (int i = 0; i < jsonArray.length(); i++) {
                temp = jsonArray.getJSONObject(i);
                tagList.add(new TagClass(temp.getString("code"), temp.getString("name")));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTags(CharSequence cs) {
        /**
         * for empty edittext
         */
        if (cs.toString().equals("")) {
            tagGroup.addTags(new ArrayList<Tag>());
            return;
        }

        String text = cs.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag;

        for (int i = 0; i < tagList.size(); i++) {
            if (tagList.get(i).getName().toLowerCase().startsWith(text.toLowerCase())) {
                tag = new Tag(tagList.get(i).getName());
                tag.radius = 10f;
                tag.layoutColor = Color.parseColor(tagList.get(i).getColor());
                if (i % 2 == 0) // you can set deletable or not
                    tag.isDeletable = true;
                tags.add(tag);
            }
        }
        tagGroup.addTags(tags);

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.add_submit_diy){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        photoFile = getPhotoFileUri(photoFileName);
        Log.e("photoFile", String.valueOf(photoFile));

        Uri fileProvider = FileProvider.getUriForFile(EditDIYDetailsActivity.this, "cabiso.daphny.com.g_companion", photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.e(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            //if (resultCode == MainActivity.RESULT_OK) {

            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            imgView = (ImageView) findViewById(R.id.add_product_image_plus_icon);
            imgView.setImageBitmap(takenImage);

            if(takenImage == null){
                Toast.makeText(this, "NULL IMAGE", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(this, "NOT NULL IMAGE", Toast.LENGTH_SHORT).show();
            }

        } else { // Result was a failure
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();


        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        diyPictureUri = Uri.fromFile(image);
        return image;
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditDIYDetailsActivity.this, MainActivity.class);
        startActivity(intent);
    }



}
