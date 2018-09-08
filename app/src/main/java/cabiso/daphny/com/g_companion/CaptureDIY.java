package cabiso.daphny.com.g_companion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
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

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import cabiso.daphny.com.g_companion.Adapter.CommunityAdapter;
import cabiso.daphny.com.g_companion.Model.CommunityItem;
import cabiso.daphny.com.g_companion.Model.Constants;
import cabiso.daphny.com.g_companion.Model.DBMaterial;
import cabiso.daphny.com.g_companion.Model.DIYBidding;
import cabiso.daphny.com.g_companion.Model.DIYSell;
import cabiso.daphny.com.g_companion.Model.DIYnames;
import cabiso.daphny.com.g_companion.Model.QuantityItem;
import cabiso.daphny.com.g_companion.Model.SellingDIY;
import cabiso.daphny.com.g_companion.Model.TagClass;
import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;

/**
 * Created by Lenovo on 8/22/2017.
 */


public class CaptureDIY extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference databaseReference;
    private DatabaseReference byuser_Reference;
    private Task<Void> materialsReference;
    private Task<Void> proceduresReference;
    private FirebaseDatabase database;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseStorage mStorage;
    private StorageReference storageReference, imageRef;

    private StorageReference storageRef, imgRef;

    private FirebaseUser mFirebaseUser;
    private String userID;
    private String imageFileName;

    private Uri diyPictureUri;

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 11;
    private List<String> tags = new ArrayList<>();
    private List<String> extras = new ArrayList<>();
    private List<String> validWords = new ArrayList<>();

    final ClarifaiClient client;

    public CaptureDIY() {
        client = new ClarifaiBuilder("cb169e9d3f9e4ec5a7769cc0422f3162").buildSync();
    }

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

    //for bidding
    private EditText mEtPriceMin;
    private EditText mEtPriceMessage;
    private EditText mEtExpiryDate;
    private TextView mTvDateToday;
    private String diyName, diyOwner, diyMaterials, diyProcedures;
    private Button mBtnAddBid;
    String sdate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    Calendar myCalendar = Calendar.getInstance();

    private DatabaseReference loggedInName;
    private String loggedInUserName;

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
        setContentView(R.layout.activity_capture_diy);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("diy_by_tags");
        byuser_Reference = FirebaseDatabase.getInstance().getReference().child("diy_by_users").child(userID);

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/").child("diy_by_tags");
        storageRef = mStorage.getReferenceFromUrl("gs://g-companion-v2.appspot.com/").child("diy_by_tags");
        loggedInName = FirebaseDatabase.getInstance().getReference().child("userdata");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddDIY);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationIcon(R.drawable.back_btn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent im = new Intent(CaptureDIY.this,MainActivity.class);
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

        unitOfMeasurement = getResources().getStringArray(R.array.UM);
        umAdapter=new SpinnerAdapter(getApplicationContext());

        quantity = getResources().getStringArray(R.array.qty);
        qAdapter=new SpinnerAdapter1(getApplicationContext());


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
                Toast.makeText(CaptureDIY.this, "Long Click: " + tag.text, Toast.LENGTH_SHORT).show();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(CaptureDIY.this, R.style.AppTheme);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(CaptureDIY.this, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });

        procedure = (EditText) findViewById(R.id.etProcedures);
        imgView = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        materialsList = (ListView) findViewById(R.id.materialsList);

        proceduresList = (ListView) findViewById(R.id.proceduresList);
        btnAddMaterial = (ImageButton) findViewById(R.id.btnMaterial);
        btnAddProcedure = (ImageButton) findViewById(R.id.btnProcedure);

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

// ADD DIY TO THE COMMUNITY
        btnAddMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputMaterials = material.getText().toString();
                if (inputMaterials.isEmpty()) {
                    Toast.makeText(CaptureDIY.this, "Please enter materials", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CaptureDIY.this, spinner_item_q + spinner_item_um, Toast.LENGTH_SHORT).show();


                    final Dialog dialog = new Dialog(CaptureDIY.this);
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

                            Toast.makeText(CaptureDIY.this, spinner_item_q + " " + spinner_item_um, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CaptureDIY.this, "Please enter procedure", Toast.LENGTH_SHORT).show();
                } else {
                    CommunityItem md = new CommunityItem(inputProcedure);
                    itemProcedure.add(md);
                    pAdapter.notifyDataSetChanged();
                    procedure.setText("");

                }
            }
        });

        loggedInName.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loggedInUserName = dataSnapshot.child("f_name").getValue(String.class);
                loggedInUserName +=" "+dataSnapshot.child("l_name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

                //imageRef = storageReference.child(photoFile.getAbsolutePath());
                imageRef = storageReference.child(String.valueOf(name.getText()));

                //DIY category
                final String category = categorySpinner.getSelectedItem().toString();
                final int categoryPos = categorySpinner.getSelectedItemPosition();
                Log.e("CategorySelected", category);
                Log.e("categoryPos", String.valueOf(categoryPos));

                //creating and showing progress dialog
                progressDialog = new ProgressDialog(CaptureDIY.this);
                progressDialog.setMax(100);
                progressDialog.setMessage("Adding DIY to the Community...");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setCancelable(false);

                //starting upload
                uploadTask = imageRef.putFile(Uri.fromFile(photoFile));
                // Observe state change events such as progress, pause, and resume
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        //sets and increments value of progressbar
                        progressDialog.incrementProgressBy((int) progress);
                    }
                });
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(CaptureDIY.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Float float_this = Float.valueOf(0);

                        String upload = databaseReference.push().getKey();
                        String productID = generateString();

                        //push data to Firebase Database - diy_by_tags node
                        databaseReference.child(upload).setValue(new DIYnames(name.getText().toString(),
                                taskSnapshot.getDownloadUrl().toString(), userID, productID, "community",
                                float_this, float_this, loggedInUserName));
                        databaseReference.child(upload).child("materials").setValue(dbMaterials);
                        databaseReference.child(upload).child("procedures").setValue(itemProcedure);
                        databaseReference.child(upload).child("category").setValue(category);
                        databaseReference.child(upload).child("category_postition").setValue(categoryPos);
                        databaseReference.child(upload).child("dateAdded").setValue(sdate);

                        //push data to Firebase Database - diy_by_user node
                        byuser_Reference.child(upload).setValue(new DIYnames(name.getText().toString(),
                                taskSnapshot.getDownloadUrl().toString(), userID, productID, "community",
                                float_this, float_this, loggedInUserName));
                        byuser_Reference.child(upload).child("materials").setValue(dbMaterials);
                        byuser_Reference.child(upload).child("procedures").setValue(itemProcedure);
                        byuser_Reference.child(upload).child("category").setValue(category);
                        byuser_Reference.child(upload).child("category_postition").setValue(categoryPos);
                        byuser_Reference.child(upload).child("dateAdded").setValue(sdate);


                        Toast.makeText(CaptureDIY.this, "Upload successful", Toast.LENGTH_SHORT).show();

                        // Alert Dialog for finished uploaing DIYs
                        AlertDialog.Builder ab = new AlertDialog.Builder(CaptureDIY.this, R.style.MyAlertDialogStyle);
                        ab.setMessage("Thank you for contributing to the DIY Community!");
                        ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent in = new Intent(CaptureDIY.this, MainActivity.class);
                                startActivity(in);
                            }
                        });

                        ab.create().show();
                        progressDialog.dismiss();
                    }
                });

            }
        });


        //SELL DIY TO THE COMMUNITY
        sellButton = (Button) findViewById(R.id.sellDiy);
        sellButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(CaptureDIY.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.sell_diy_option);
                dialog.setCancelable(true);

                final EditText etPrice = (EditText) dialog.findViewById(R.id.etPrice);
                final EditText etQuantity = (EditText) dialog.findViewById(R.id.etQty);
                final EditText etDescription = (EditText) dialog.findViewById(R.id.etDescription);
                Button sellOkButton = (Button) dialog.findViewById(R.id.okaybtn);


                dialog.show();

                sellOkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(CaptureDIY.this, etPrice.getText() + "," + etQuantity.getText() + "," + etDescription.getText(),
                                Toast.LENGTH_SHORT).show();

                       // imgRef = storageRef.child(photoFile.getAbsolutePath());
                        imgRef = storageRef.child(String.valueOf(name.getText()));

                        final String category = categorySpinner.getSelectedItem().toString();
                        final int categoryPos = categorySpinner.getSelectedItemPosition();
                        Log.e("CategorySelected", category);
                        Log.e("categoryPos", String.valueOf(categoryPos));

                        //creating and showing progress dialog
                        progressDialog = new ProgressDialog(CaptureDIY.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage("Adding DIY for Selling...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        //starting upload
                        uploadTask = imgRef.putFile(Uri.fromFile(photoFile));
                        // Observe state change events such as progress, pause, and resume
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //sets and increments value of progressbar
                                progressDialog.incrementProgressBy((int) progress);
                            }
                        });
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(CaptureDIY.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String for_price = etPrice.getText().toString();
                                String for_qty = etQuantity.getText().toString();
                                final String for_descr = etDescription.getText().toString();
                                final float price = Float.parseFloat((for_price));
                                final int qty = Integer.parseInt(for_qty);

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Float float_this = Float.valueOf(0);

                                String upload = databaseReference.push().getKey();

                                Random random = new Random();
                                String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
//                                dbMaterials.add(new DBMaterial().setName(materials).setQuantity(quantity).setUnit(unit_material));
                                dbSelling.add(new SellingDIY().setSelling_price(price).setSelling_qty(qty).setSelling_descr(for_descr));
                                SellingDIY sellingDIY = new SellingDIY();
                                String sellingPrice = String.valueOf(sellingDIY.setSelling_price(price));
                                String sellingQty = String.valueOf(sellingDIY.setSelling_qty(qty));
                                String sellingDescr = String.valueOf(sellingDIY.setSelling_descr(for_descr));

                                //push data to Firebase Database - diy_by_tags node
                                String productID_sell = generateString();
                                databaseReference.child(upload).setValue(new DIYSell(name.getText().toString(),
                                        taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "Selling",
                                        float_this, float_this, "none", loggedInUserName));
                                databaseReference.child(upload).child("materials").setValue(dbMaterials);
                                databaseReference.child(upload).child("procedures").setValue(itemProcedure);
                                databaseReference.child(upload).child("DIY Price").setValue(dbSelling);
//                                databaseReference.child(upload).child("sellingPrice").setValue(sellingPrice);
//                                databaseReference.child(upload).child("sellingQty").setValue(sellingQty);
//                                databaseReference.child(upload).child("sellingDescr").setValue(sellingDescr);
                                databaseReference.child(upload).child("category").setValue(category);
                                databaseReference.child(upload).child("category_postition").setValue(categoryPos);
                                databaseReference.child(upload).child("dateAdded").setValue(sdate);

                                //push data to Firebase Database - diy_by_user node
                                byuser_Reference.child(upload).setValue(new DIYSell(name.getText().toString(),
                                        taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "Selling",
                                        float_this, float_this, "none", loggedInUserName));
                                byuser_Reference.child(upload).child("materials").setValue(dbMaterials);
                                byuser_Reference.child(upload).child("procedures").setValue(itemProcedure);
                                byuser_Reference.child(upload).child("DIY Price").setValue(dbSelling);
//                                databaseReference.child(upload).child("sellingPrice").setValue(sellingPrice);
//                                databaseReference.child(upload).child("sellingQty").setValue(sellingQty);
//                                databaseReference.child(upload).child("sellingDescr").setValue(sellingDescr);
                                byuser_Reference.child(upload).child("category").setValue(category);
                                byuser_Reference.child(upload).child("category_postition").setValue(categoryPos);
                                byuser_Reference.child(upload).child("dateAdded").setValue(sdate);

                                Toast.makeText(CaptureDIY.this, "Upload successful", Toast.LENGTH_SHORT).show();

                                // Alert Dialog for finished uploaing DIYs
                                AlertDialog.Builder ab = new AlertDialog.Builder(CaptureDIY.this, R.style.MyAlertDialogStyle);
                                ab.setMessage("Thank you for selling your DIY!");
                                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent in = new Intent(CaptureDIY.this, MainActivity.class);
                                        startActivity(in);
                                    }
                                });

                                ab.create().show();
                                progressDialog.dismiss();
                            }
                        });

                        dialog.dismiss();

                    }
                });


            }
        });


        //Add DIY for Bidding
        bidButton = (Button) findViewById(R.id.bidDiy);
        bidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CaptureDIY.this, "Bid button clicked", Toast.LENGTH_SHORT).show();

                final Dialog dialog = new Dialog(CaptureDIY.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bidding_dialog);
                dialog.show();

                mEtPriceMin = (EditText) dialog.findViewById(R.id.etBidInitialPrice);
                mEtPriceMessage = (EditText) dialog.findViewById(R.id.etBidMessage);
                mEtExpiryDate = (EditText) dialog.findViewById(R.id.etBidExpiryDate);
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
                        new DatePickerDialog(CaptureDIY.this, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

//                mEtExpiryDate.setOnClickListener(this);

                mBtnAddBid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgRef = storageRef.child(String.valueOf(name.getText()));

                        final String category = categorySpinner.getSelectedItem().toString();
                        final int categoryPos = categorySpinner.getSelectedItemPosition();
                        Log.e("CategorySelected", category);
                        Log.e("categoryPos", String.valueOf(categoryPos));

                        //creating and showing progress dialog
                        progressDialog = new ProgressDialog(CaptureDIY.this);
                        progressDialog.setMax(100);
                        progressDialog.setMessage("Adding DIY for Bidding...");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.show();
                        progressDialog.setCancelable(false);

                        //starting upload
                        uploadTask = imgRef.putFile(Uri.fromFile(photoFile));
                        // Observe state change events such as progress, pause, and resume
                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                //sets and increments value of progressbar
                                progressDialog.incrementProgressBy((int) progress);
                            }
                        });
                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(CaptureDIY.this, "Error in uploading!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String initial_price = mEtPriceMin.getText().toString();
                                String bid_message = mEtPriceMessage.getText().toString();
                                String expiry_date = mEtExpiryDate.getText().toString();
                                int initialBidPrice = Integer.parseInt((initial_price));

                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Float float_this = Float.valueOf(0);

                                String upload = databaseReference.push().getKey();

                                Random random = new Random();
                                String candidateChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

//                                dbBidding.add(new DIYBidding().setInitialPrice(initialBidPrice).setMessage(bid_message)
//                                                .setXpire_date(expiry_date));

                                DIYBidding formBidding = getFormInput();

                                //push data to Firebase Database - diy_by_tags node
                                String productID_sell = generateString();
                                databaseReference.child(upload).setValue(new DIYSell(name.getText().toString(),
                                        taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "ON BID!",
                                        float_this, float_this, "none", loggedInUserName));
                                databaseReference.child(upload).child("materials").setValue(dbMaterials);
                                databaseReference.child(upload).child("procedures").setValue(itemProcedure);
                                databaseReference.child(upload).child("category").setValue(category);
                                databaseReference.child(upload).child("category_postition").setValue(categoryPos);
                                databaseReference.child(upload).child("bidding").push().setValue(formBidding);
                                databaseReference.child(upload).child("dateAdded").setValue(sdate);

                                //push data to Firebase Database - diy_by_user node
                                byuser_Reference.child(upload).setValue(new DIYSell(name.getText().toString(),
                                        taskSnapshot.getDownloadUrl().toString(), userID, productID_sell, "ON BID!",
                                        float_this, float_this, "none", loggedInUserName));
                                byuser_Reference.child(upload).child("materials").setValue(dbMaterials);
                                byuser_Reference.child(upload).child("procedures").setValue(itemProcedure);
                                byuser_Reference.child(upload).child("category").setValue(category);
                                byuser_Reference.child(upload).child("category_postition").setValue(categoryPos);
                                byuser_Reference.child(upload).child("bidding").push().setValue(formBidding);
                                byuser_Reference.child(upload).child("dateAdded").setValue(sdate);

                                Toast.makeText(CaptureDIY.this, "Upload successful", Toast.LENGTH_SHORT).show();

                                // Alert Dialog for finished uploaing DIYs
                                AlertDialog.Builder ab = new AlertDialog.Builder(CaptureDIY.this, R.style.MyAlertDialogStyle);
                                ab.setMessage("Thank you for bidding your DIY!");
                                ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent in = new Intent(CaptureDIY.this, MainActivity.class);
                                        startActivity(in);
                                    }
                                });

                                ab.create().show();
                                progressDialog.dismiss();
                            }
                        });

                        dialog.dismiss();
                    }
                });
            }
        });
    }


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

        //for bidding calendar
//        if (v == mEtExpiryDate) {
//            final Calendar calendar = Calendar.getInstance();
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int date = calendar.get(Calendar.DAY_OF_MONTH);
//
//            Toast.makeText(this, "Dateeeeeeeeee", Toast.LENGTH_SHORT).show();
//            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                    Toast.makeText(CaptureDIY.this, "Expiryy", Toast.LENGTH_SHORT).show();
//                    String set_expiry = year + "-" + String.valueOf(month + 1) + "-" + (dayOfMonth);
////                    datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
////                    String set_expiry = String.valueOf(month + 1) + "/" + (dayOfMonth) + "/" + year;
//                    if (calendar.before(set_expiry)) {
//                        Toast.makeText(getApplication(), "YOU CANNOT PICK PASSED WEEKS!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        mEtExpiryDate.setText(set_expiry);
//                    }
//                }
//            }, year, month, date);
//            datePickerDialog.show();
////        }
//        }
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

        Uri fileProvider = FileProvider.getUriForFile(CaptureDIY.this, "cabiso.daphny.com.g_companion", photoFile);
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

//                diyPictureUri = data.getData();
//                if(diyPictureUri==null){
//                    imgView.setImageURI(diyPictureUri);
//                    Toast.makeText(this, "NULL IMAGE", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(this, "NOT NULL IMAGE ", Toast.LENGTH_SHORT).show();
//                }
//
//
//                Bitmap bmp = (Bitmap) data.getExtras().get("data");
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
//
//                // convert byte array to Bitmap
//                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
//                        byteArray.length);
//                imgView.setImageBitmap(bitmap);

          //  }

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
        Intent intent = new Intent(CaptureDIY.this, MainActivity.class);
        startActivity(intent);
    }

}
