package uncommon.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import uncommon.common.R;
import uncommon.common.models.Review;

public class ReviewAdapter extends ArrayAdapter<String> {

    private final Context context;
    private LayoutInflater inflater;
    private List<Review> reviews;
    private RatingBar rating;
    private Review positionReview;
    private TextView reviewTitle;
    private TextView reviewContent;
    private TextView userName;

    public ReviewAdapter(Context context, List<Review> reviews){
        super(context, android.R.layout.simple_list_item_1);
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public String getItem(int position) {
        return reviews.get(position).getUserName();
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
            convertView = inflater.inflate(R.layout.review_item, null, false);
        }

        positionReview = reviews.get(position);

        rating = (RatingBar) convertView.findViewById(R.id.reviewRating);
        reviewContent = (TextView) convertView.findViewById(R.id.reviewContent);
        reviewTitle = (TextView) convertView.findViewById(R.id.reviewTitle);
        userName = (TextView) convertView.findViewById(R.id.userName);

        rating.setRating(positionReview.getRating());
        reviewContent.setText(positionReview.getContent().toString());
        reviewTitle.setText("\"" + positionReview.getTitle().toString() + "\"");
        userName.setText(positionReview.getUserName().toString());

        return convertView;
    }
}
