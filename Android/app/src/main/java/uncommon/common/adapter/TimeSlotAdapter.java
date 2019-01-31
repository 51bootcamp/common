package uncommon.common.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import uncommon.common.models.TimeTable;


public class TimeSlotAdapter extends ArrayAdapter<String> {

    private ArrayList<Integer> timeSlotIdxList = new ArrayList<Integer>();
    private Context context;
    private final List<String> timeList;
    private List<TimeTable> timeslot;

    public TimeSlotAdapter(Context context, List<String> timeList, List<TimeTable> timeslot) {
        super(context, android.R.layout.simple_list_item_1, (List<String>) timeList);

        this.context = context;
        this.timeList = timeList;
        this.timeslot = timeslot;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the current item from ListView
        View view = super.getView(position, convertView, parent);

        return view;
    }
}
