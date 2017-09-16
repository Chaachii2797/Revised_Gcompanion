package cabiso.daphny.com.g_companion.Model;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by cicctuser on 9/15/2017.
 */
public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private DatabaseReference databaseReference;
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();


//        if(parent.getItemAtPosition(position).toString().equals("bottle")){
//            databaseReference = FirebaseDatabase.getInstance().getReference("DIY_Methods").child("category").child("bottle");
//            CategoryItem categoryItem = new CategoryItem(parent.getItemAtPosition(position).toString());
//            categoryItem.getName();
//            databaseReference.push().setValue(categoryItem);
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
