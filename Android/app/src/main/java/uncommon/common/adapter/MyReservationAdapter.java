package uncommon.common.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import uncommon.common.R;
import uncommon.common.activity.ConfirmReservationActivity;
import uncommon.common.activity.PlaceActivity;
import uncommon.common.activity.ReviewActivity;
import uncommon.common.models.Reservation;
import uncommon.common.utils.GradientTransformation;

public class MyReservationAdapter extends ArrayAdapter<String> {

    private Button reviewButton;
    private Context context;
    private ImageView classImageView;
    private LayoutInflater inflater;
    private List<Reservation> reservations;
    private Reservation positionReservation;
    private String reservationTime;
    private TextView classNameTextView;
    private TextView dateTextView;
    private TextView timeTextView;

    public MyReservationAdapter(Context context, List<Reservation> reservations){
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.reservations = reservations;
    }

    @Override
    public int getCount() {
        return reservations.size();
    }

    @Override
    public String getItem(int position) {
        return reservations.get(position).getClassName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final Context context = parent.getContext();
            if (inflater == null) {
                inflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = inflater.inflate(R.layout.reservation_item, parent, false);
        }

        positionReservation = reservations.get(position);

        classImageView = (ImageView) convertView.findViewById(R.id.classImageView);
        classNameTextView = (TextView) convertView.findViewById(R.id.classNameTextView);
        dateTextView = (TextView) convertView.findViewById(R.id.dateTextView);
        reviewButton = (Button) convertView.findViewById(R.id.reviewButton);
        timeTextView = (TextView) convertView.findViewById(R.id.timeTextView);

        String imageURL = "http://52.8.187.167:8000" + positionReservation.getCoverImg();
        Picasso.get().load(imageURL).fit().transform(new GradientTransformation()).into
                (classImageView);
        classNameTextView.setText(positionReservation.getClassName().toString());
        dateTextView.setText(positionReservation.getDate().toString());
        reservationTime = positionReservation.getStartTime().toString() + " ~ " +
                positionReservation.getEndTime().toString();
        timeTextView.setText(reservationTime);

        //go to ReviewActivity
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewIntent = new Intent(context, ReviewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("classID", positionReservation.getClassId());

                context.startActivity(reviewIntent);
            }
        });
        return convertView;
    }
}