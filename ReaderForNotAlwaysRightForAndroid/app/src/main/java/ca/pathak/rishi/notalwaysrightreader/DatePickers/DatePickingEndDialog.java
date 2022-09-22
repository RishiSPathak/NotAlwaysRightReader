package ca.pathak.rishi.notalwaysrightreader.DatePickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import org.threeten.bp.LocalDate;

import ca.pathak.rishi.notalwaysrightreader.Activities.MainActivity;

public class DatePickingEndDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue()-1;
        int day = today.getDayOfMonth();

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    LocalDate newDate = LocalDate.of(year, month+1, day);
                    LocalDate currentDate = LocalDate.now();
                    if (newDate.isAfter(currentDate)) {
                        Toast.makeText(MainActivity.mainActivity,"Error: New date cannot be in the future.",Toast.LENGTH_LONG).show();
                    } else if (newDate.isBefore(MainActivity.StartDate)) {
                        Toast.makeText(MainActivity.mainActivity,"Error: End date must be after the start date.",Toast.LENGTH_LONG).show();
                    } else {
                        MainActivity.EndDate = newDate;
                        MainActivity.mainActivity.updateUI();
                    }
                }
            };
}
