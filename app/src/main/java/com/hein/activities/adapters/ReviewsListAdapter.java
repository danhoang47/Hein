package com.hein.activities.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hein.R;
import com.hein.entity.Review;

import java.util.List;

public class ReviewsListAdapter extends BaseAdapter{

    Context context;
    String listReviewsContent[];
    String username[];
    String timereview[];
    int givenPoint[];
    String listImages[];

    List<Review> listReview;
    LayoutInflater inflater;

    public ReviewsListAdapter(Context context, String[] reviewsContentList, String[] username, String[] timeReview, int[] givenPoint, String[] images) {
        this.context = context;
        this.listReviewsContent = reviewsContentList;
        this.listImages = images;
        this.username = username;
        this.timereview = timeReview;
        this.givenPoint = givenPoint;
        this.inflater = LayoutInflater.from(context);
    }

    public ReviewsListAdapter(Context context, List<Review> listReview) {
        this.context = context;
        this.listReview = listReview;
        this.inflater = LayoutInflater.from(context);

        int size = listReview.size();
        listReviewsContent = new String[size];
        username = new String[size];
        timereview = new String[size];
        givenPoint = new int[size];
        listImages = new String[size];

        for(int i =0 ;i<listReview.size(); i++) {
            listReviewsContent[i] = listReview.get(i).getReviewContent();
            username[i] = listReview.get(i).getUserReviewName();
            timereview[i] = listReview.get(i).getTimestamp();
            givenPoint[i] = listReview.get(i).getRatingPoint();
            listImages[i] = listReview.get(i).getUserAvatar();
            Log.i("User avatar name", listReview.get(i).getUserAvatar() + "");
        }
    }

    @Override
    public int getCount() {
        return listReviewsContent.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.activity_reviews_list, null);
        TextView textView = (TextView) view.findViewById(R.id.user_review_content);
        /*CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.profile_image);*/
        ImageView circleImageView = (ImageView)  view.findViewById(R.id.profile_image);
        TextView textViewUsername = (TextView) view.findViewById(R.id.user_review_username);
        TextView textViewTimeReview = (TextView) view.findViewById(R.id.user_review_time);
        TextView textViewGivenPoint = (TextView) view.findViewById(R.id.user_review_point);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.product_rating);

        if(username[i] == null || username.length == 0) {
            textViewUsername.setText("user");
        } else {
            textViewUsername.setText(username[i]);
        }

        textViewTimeReview.setText(timereview[i]);
        textViewGivenPoint.setText(String.valueOf(givenPoint[i]));
        textView.setText(listReviewsContent[i]);
        /*circleImageView.setImageDrawable(LoadImageFromWebOperations(listImages[i]));*/
        if(listImages[i] == null || listImages.length == 0) {
            Glide.with(context).load("https://villagesonmacarthur.com/wp-content/uploads/2020/12/Blank-Avatar.png").circleCrop().placeholder(com.denzcoskun.imageslider.R.drawable.loading).into(circleImageView);
        } else {
            Glide.with(context).load(listImages[i]).circleCrop().placeholder(com.denzcoskun.imageslider.R.drawable.loading).into(circleImageView);
        }
        ratingBar.setRating((float) givenPoint[i]);
        return view;
    }


}
