package cabiso.daphny.com.g_companion;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cabiso.daphny.com.g_companion.Model.DIYnames;


/**
 * Created by Lenovo on 3/23/2018.
 */

public class YouItemsFragment extends Fragment {
    FirebaseUser mFirebaseUser;
    String userID;
    DatabaseReference by_userReference;
    DatabaseReference databaseReference;

    private OnListFragmentInteractionListener user_Listener;

    private RecyclerView user_recyclerView;
    private Activity context;
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
        databaseReference = FirebaseDatabase.getInstance().getReference();
        by_userReference = databaseReference.child("diy_by_users").child(userID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.your_items_fragment, container, false);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        user_recyclerView = (RecyclerView) view.findViewById(R.id.userList);
        int numberOfColumns = 1;
        user_recyclerView.setLayoutManager(new GridLayoutManager(context, numberOfColumns));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerAdapter<DIYnames, YouItemsFragment.ItemViewHolder> user_Adapter =
                new FirebaseRecyclerAdapter<DIYnames, YouItemsFragment.ItemViewHolder>(DIYnames.class,
                        R.layout.recycler_by_user, ItemViewHolder.class, by_userReference) {
                    @Override
                    protected void populateViewHolder(final ItemViewHolder viewHolder, DIYnames model, final int position) {
                        viewHolder.user_NameView.setText(model.diyName);
                        if(model.identity!=null){
                            if(model.identity.equals("selling")){
                                viewHolder.user_Identity.setText("Selling");
                                viewHolder.user_Identity.setBackgroundColor(Color.RED);
                            }else{
                                viewHolder.user_Identity.setText("Community");
                                viewHolder.user_Identity.setBackgroundColor(Color.YELLOW);
                            }

                            try{
                                String productPictureURL = model.diyUrl;
                                Log.d("ppURL", productPictureURL);
                                StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(productPictureURL);
                                pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Got the download URL for 'users/me/profile.png'
                                        // Pass it to Picasso to download, show in ImageView and caching
                                        Log.d("Product Picture URI is", uri.toString());
                                        Glide.with(getContext()).load(uri)
                                                .fitCenter().centerCrop()
                                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                                .into(viewHolder.user_ProductImageView);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                            catch(Exception e){
                                Log.d("Exception", "Failed to fetch product Picture");
                            }
                            viewHolder.user_View.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (null != user_Listener) {
                                        // Notify the active callbacks interface (the activity, if the
                                        // fragment is attached to one) that an item has been selected.
                                        user_Listener.onListFragmentInteractionListener(getRef(position));
                                        Toast.makeText(getActivity(), "You clicked on position!" + " " + position, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                };
        user_recyclerView.setAdapter(user_Adapter);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View user_View;
        public final TextView user_NameView;
        public final TextView user_Identity;
        public final ImageView user_ProductImageView;

        public ItemViewHolder(View view){
            super(view);
            user_View = view;

            user_NameView = (TextView) view.findViewById(R.id.user_item_name);
            user_ProductImageView = (ImageView) view.findViewById(R.id.user_diy_item_icon);
            user_Identity = (TextView) view.findViewById(R.id.user_item_identity);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            user_Listener = (OnListFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        user_Listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteractionListener(DatabaseReference ref);
    }
}
