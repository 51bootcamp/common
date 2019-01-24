package uncommon.common.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ChangeDate implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener {
    private Calendar calendar;
    private Context ctx;
    private TextView changeDate;

    public ChangeDate(TextView editText, Context ctx){
        this.changeDate = editText;
        this.changeDate.setOnFocusChangeListener(this);
        calendar = Calendar.getInstance();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String myFormat = "MMM dd, yyyy"; //In which you need put here
        SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        changeDate.setText(sdformat.format(calendar.getTime()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            new DatePickerDialog(ctx, this, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }
}
