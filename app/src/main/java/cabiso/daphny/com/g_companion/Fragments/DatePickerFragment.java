//package cabiso.daphny.com.g_companion.Fragments;
//
//
//import android.app.DatePickerDialog;
//import android.app.Dialog;
//import android.icu.text.DateFormat;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.widget.DatePicker;
//import android.widget.EditText;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Locale;
//
//import cabiso.daphny.com.g_companion.R;
//
///**
// * A simple {@link Fragment} subclass.subclass
// */
//public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//
//
//    public DatePickerFragment() {
//        // Required empty public constructor
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar calendar = Calendar.getInstance();
//        int start_year = calendar.get(Calendar.YEAR);
//        int start_month = calendar.get(Calendar.MONTH);
//        int start_day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new instance of DatePickerDialog and return it
//        return new DatePickerDialog(getActivity(), this, start_year, start_month, start_day);
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        // Do something with the date chosen by the user
//        EditText startDate = (EditText) getActivity().findViewById(R.id.et_start_date);
//        startDate.setText(month+" "+dayOfMonth+" "+year);
//
//        // Create a Date variable/object with user chosen date
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(0);
//        cal.set(year, month, dayOfMonth, 0, 0, 0);
//        Date chosenDate = cal.getTime();
//
//        // Format the date using style medium and US locale
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL,Locale.US);
//            String df_full_str = dateFormat.format(chosenDate);
//
//            startDate.setText(startDate.getText() + df_full_str);
//        }
//    }
//}
