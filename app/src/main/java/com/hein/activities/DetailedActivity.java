package com.hein.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hein.activities.adapters.ColorBookingdatper;
import com.hein.activities.adapters.RadioBuyBtnAdapter;
import com.hein.activities.adapters.ReviewsListAdapter;
import com.hein.entity.Booking;
import com.hein.entity.Product;
import com.hein.entity.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hein.R;
import com.hein.entity.User;
import com.hein.home.HomeActivity;
import com.hein.home.filter.ColorRVAdatper;
import com.hein.home.filter.ColorViewModel;
import com.hein.home.filter.FilterViewModel;
import com.hein.home.login.LoginDialogFragment;
import com.hein.ordered_product.OrderActivity;

public class DetailedActivity extends AppCompatActivity implements RadioBuyBtnAdapter.RadioButtonClickListener {

    String userReviewsContent[] = {"This is a nice product lorem200 jiw jiqwoidj bubiqw huwdq xiqbwx uqwbx quiw This is a nice product lorem200 jiw jiqwoidj bubiqw huwdq xiqbwx uqwbx quiw This is a nice product lorem200 jiw jiqwoidj bubiqw huwdq xiqbwx uqwbx quiw", "This is suck", "Danh bida de", "Android dau buoi", "Qua de voi huy"};
    String userName[] ={"Le Duc", "Quoc Dat", "Huy hue", "Trong Hieu", "Phan Duc Thinh"};
    String timeReview[] = {"12:30", "14:34", "15:50", "19:20", "12:12"};
    int givenPoint[] ={3, 4, 4, 5, 2};
    String userAvatar[] = {"https://fastly.picsum.photos/id/769/200/300.jpg?hmac=cl3KEs924CuE_nF1wC98S7NBc8JPXkf0hlwtPXGIIhM", "https://fastly.picsum.photos/id/717/200/300.jpg?hmac=OJYQhMLNcYlN5qz8IR345SJ7t6A0vyHzT_RdMxO4dSc", "https://fastly.picsum.photos/id/254/200/300.jpg?hmac=VoOUXxjWvbLuWPBSHy_pbMAoLSYCaO-3drnOhwvA2yY", "https://fastly.picsum.photos/id/256/200/300.jpg?hmac=6-SQmUqIECHQ4QadM7mAYY3sHPH5r_8e2pCBs7V67Sc", "https://fastly.picsum.photos/id/504/200/300.jpg?hmac=mycti8qYrnGcag5zUhsVOq7hQwb__R-Zf--aBJAH_ec"};

    /* Color String ["Red", "Blue", "Green"]*/
    List<String> itemList = new ArrayList<>();

    /* size String ["XL", "L", "M", "S"]*/
    List<String> itemListSize = new ArrayList<>();
    List<String> itemListColor = Arrays.asList("#FF0000", "#0000FF", "#00FF00");


    DynamicHeightListView listView;

    FirebaseFirestore db;

    TextView productName, brandName, productColor, productType, productGender, productPrice, filterTextView;

    ImageSlider imageSlider;

    Button addCartBtn, buyBtn, addReviewBtn, buySelectionBtn;

    int activeFilterOption = 0;

    private RadioGroup colorSelection, sizeSelection;

    NumberPicker quantitySelection;

    private RadioBuyBtnAdapter adapter;

    private String featurePic;

    ColorBookingdatper colorBookingdatper;

    private FilterViewModel filterViewModel;

    int selectedColorPosition = -1;

    Set<String> bookingColor = new HashSet<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();


        imageSlider = findViewById(R.id.image_slider);

         productName = (TextView) findViewById(R.id.detailed_product_name);
         brandName = (TextView) findViewById(R.id.brand_name);
         productColor = (TextView) findViewById(R.id.product_color);
         productType = (TextView) findViewById(R.id.type_name);
         productGender = (TextView) findViewById(R.id.gender);
         addReviewBtn = (Button) findViewById(R.id.add_review_button);
         productPrice = (TextView) findViewById(R.id.detailed_product_price);
        filterTextView = (TextView) findViewById(R.id.filter);
        buyBtn = (Button) findViewById(R.id.product_buy_btn);
        addCartBtn = (Button) findViewById(R.id.add_cart_btn);


        String productId = intent.getStringExtra("productId");

         addReviewBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (HomeActivity.currentUser == null) {
                     LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                     loginDialogFragment.show(getSupportFragmentManager(), "loginDialog");
                 } else {
                     showReviewDialog(HomeActivity.currentUser.getId(), productId);

                 }
             }
         });

        filterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFiltelDialog(productId);
            }
        });

        addCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HomeActivity.currentUser == null) {
                    LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(getSupportFragmentManager(), "loginDialog");
                } else {
                    Log.i("userId", HomeActivity.currentUser.getId());
                    showBuyingDialog(HomeActivity.currentUser.getId(), productId, "cart");
                }
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (HomeActivity.currentUser == null) {
                    LoginDialogFragment loginDialogFragment = new LoginDialogFragment();
                    loginDialogFragment.show(getSupportFragmentManager(), "loginDialog");
                } else {
                    showBuyingDialog(HomeActivity.currentUser.getId(), productId, "buy");
                }
            }
        });



        fetchData(productId);
        fetchDataReviews(productId);
        /*fetchDataUserTest("xnJNFIe8KoGheyJXrLWf");*/

    }

    private void showReviewDialog(String userId, String productId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_review_layout, null);
        builder.setView(dialogView);

        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        EditText reviewEditText = dialogView.findViewById(R.id.reviewEditText);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button okButton = dialogView.findViewById(R.id.okButton);

        final AlertDialog dialog = builder.create();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog on Cancel button click
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) ratingBar.getRating();
                String review = reviewEditText.getText().toString();

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();

                Review reviewAddData = new Review();

                reviewAddData.setRatingPoint(rating);
                reviewAddData.setReviewContent(review);
                reviewAddData.setProductId(productId);
                reviewAddData.setUserId(userId);
                reviewAddData.setTimestamp(formatter.format(date));

                addReview(reviewAddData);

                fetchDataReviews(productId);

                dialog.dismiss(); // Dismiss the dialog after processing
            }
        });

        dialog.show();
    }

    private void showFiltelDialog(String productId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_filter_layout, null);
        builder.setView(dialogView);

        RadioButton start1 = dialogView.findViewById(R.id.radioButton1);
        RadioButton start2 = dialogView.findViewById(R.id.radioButton2);
        RadioButton start3 = dialogView.findViewById(R.id.radioButton3);
        RadioButton start4 = dialogView.findViewById(R.id.radioButton4);
        RadioButton start5 = dialogView.findViewById(R.id.radioButton5);

        final AlertDialog dialog = builder.create();


        radioButtonOnclick(start1, dialog, productId, 1);

        radioButtonOnclick(start2, dialog, productId, 2);

        radioButtonOnclick(start3, dialog, productId, 3);

        radioButtonOnclick(start4, dialog, productId, 4);

        radioButtonOnclick(start5, dialog, productId, 5);

        Log.i("Current option", activeFilterOption + "");

        switch (activeFilterOption) {
            case 1: start1.setChecked(true); break;
            case 2: start2.setChecked(true); break;
            case 3: start3.setChecked(true); break;
            case 4: start4.setChecked(true); break;
            case 5: start5.setChecked(true); break;
            default:
                start1.setChecked(false);
                start2.setChecked(false);
                start3.setChecked(false);
                start4.setChecked(false);
                start5.setChecked(false);
                break;
        }



        dialog.show();
    }


    public void initColorRV(View view, List<String> colorsList, Booking booking) {
        List<ColorViewModel> colors = new ArrayList<>();

        for(String color : colorsList) {
            colors.add(new ColorViewModel(color, color, false));
        }

        RecyclerView recyclerView = view.findViewById(R.id.colorFilterRV);
        colorBookingdatper = new ColorBookingdatper(getApplicationContext(), colors, bookingColor, selectedColorPosition, booking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(colorBookingdatper);
    }

    private void showBuyingDialog(String userId, String productId, String type) {

        Booking booking = new Booking();
        booking.setProductId(productId);
        booking.setUserId(userId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_buy_product_layout, null);
        builder.setView(dialogView);


        String productNameStr = productName.getText().toString();
        booking.setProductName(productNameStr);
        booking.setColor(itemList.stream().findFirst().orElse(null));

        initColorRV(dialogView, itemList, booking);



        /*Display color selection*/
        /*colorSelection = dialogView.findViewById(R.id.color_selection);

        for(int i =0 ;i<itemList.size(); i++) {
            *//*RadioButton radioButton = new RadioButton(this);*//*
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio_button, colorSelection, false);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(10); // Set the corner radius as per your requirement
            drawable.setColor(Color.parseColor(itemListColor.get(i)));

            radioButton.setTag(itemList.get(i));
            Log.i("Itemlist", itemList.get(i).toString());
            radioButton.setId(View.generateViewId());
            *//*radioButton.setBackgroundColor(Color.parseColor(itemListColor.get(i)));*//*
            radioButton.setBackground(drawable);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    *//*String selectedValue = ((RadioButton) view).getText().toString();*//*
                    String selectedValue = (String) view.getTag();
                    booking.setColor(selectedValue);
                    onRadioButtonClicked(selectedValue);
                }
            });
            colorSelection.addView(radioButton);
        }*/


        /*Display size selection*/
        sizeSelection = dialogView.findViewById(R.id.size_selection);

        for(int i =0 ;i<itemListSize.size(); i++) {
            /*RadioButton radioButton = new RadioButton(this);*/
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.item_radio_button_size, sizeSelection, false);

            radioButton.setText(itemListSize.get(i));
            radioButton.setTag(itemListSize.get(i));
            radioButton.setId(View.generateViewId());
            /*radioButton.setBackgroundColor(Color.parseColor(itemListColor.get(i)));*/
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*String selectedValue = ((RadioButton) view).getText().toString();*/
                    String selectedValue = (String) view.getTag();
                    booking.setSize(selectedValue);
                }
            });
            sizeSelection.addView(radioButton);
        }

        /*Display quantity selection*/
        quantitySelection = dialogView.findViewById(R.id.quantity_selection);

        quantitySelection.setMinValue(1);
        quantitySelection.setMaxValue(50);

        quantitySelection.setValue(1);
        booking.setQuantity(1);

        quantitySelection.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                booking.setQuantity(newVal);
               }
        });

        /*Buying button clicked*/
        buySelectionBtn = (Button) dialogView.findViewById(R.id.okButton);

        switch (type) {
            case "buy": {
                buySelectionBtn.setText("Booking");
                break;
            }
            case "cart": {
                buySelectionBtn.setText("Add to cart");
                break;
            }
        }

        final AlertDialog dialog = builder.create();

        dialog.show();

        buySelectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(booking.getColor()==null || booking.getColor().length() == 0) {
                    Toast.makeText(DetailedActivity.this, "Please pick one color", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bookingColor.size()>1) {
                    Toast.makeText(DetailedActivity.this, "Only pick one color pls", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(booking.getSize()==null || booking.getSize().length() == 0) {
                    Toast.makeText(DetailedActivity.this, "Please pick a size", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("Quantity", booking.getQuantity() + "");

                float productPriceVal = (float) Double.parseDouble(productPrice.getText().toString().substring(1));

                booking.setTotalPrice(booking.getQuantity() * productPriceVal);

                switch (type) {
                    case "buy": {
                        booking.setStatus(0);
                        booking.setType(1);
                        Log.i("booking data", booking.toString());
                        bookingAProduct(booking);
                        break;
                    }
                    case "cart": {
                        booking.setType(0);
                        Log.i("Add cart info", booking.toString());
                        Log.i("feature", featurePic);
                        addProductToShoppingCart(booking);
                        break;
                    }
                }

                dialog.dismiss();

            }
        });
    }


    @Override
    public void onRadioButtonClicked(String selectedValue) {

    }


    public void radioButtonOnclick(RadioButton radioButton, AlertDialog dialog, String productId, int point) {
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activeFilterOption != point) {
                    fetchDataReviewsFilter(productId, point);
                    activeFilterOption = point;
                }  else {
                    fetchDataReviews(productId);
                }

                dialog.dismiss();
            }
        });
    }

    public void fetchData(String productId) {
        db = FirebaseFirestore.getInstance();

        DocumentReference document = db.collection("Product").document(productId);
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    Product productDisplay = documentSnapshot.toObject(Product.class);

                    productName.setText(productDisplay.getName());
                    brandName.setText(productDisplay.getBrand());
                    productColor.setText(String.join(", ", productDisplay.getColors()));
                    productType.setText(productDisplay.getType());
                    productGender.setText(String.join(", ", productDisplay.getClassifications()));
                    productPrice.setText("$"+productDisplay.getPrice());

                    for (Map.Entry<String,Integer> entry : productDisplay.getSizes().entrySet()) {
                        System.out.println("Key = " + entry.getKey() +
                                ", Value = " + entry.getValue());
                        itemListSize.add(entry.getKey());
                    }

                    for(String color : productDisplay.getColors()) {
                        itemList.add(color);
                    }

                    ArrayList<SlideModel> slideModels = new ArrayList<>();

                    for(String product : productDisplay.getImages()) {
                        slideModels.add(new SlideModel(product, ScaleTypes.FIT));
                    }

                    featurePic = productDisplay.getImages().get(0);

                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("message", e.getMessage());
                Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void fetchDataReviews(String productId) {
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("Review").whereEqualTo("productId", productId);

        TextView totalReviewsNumber = (TextView) findViewById(R.id.product_review_number);
        TextView averageRatingPoint = (TextView) findViewById(R.id.product_rating_point);
        RatingBar averageRatingbar = (RatingBar) findViewById(R.id.product_rating);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Review> reviews = new ArrayList<>();
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Query successful, iterate over the documents
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Access the document data
                            String productId = document.getString("productId");
                            int ratingPoint = document.getLong("ratingPoint").intValue();
                            String reviewContent = document.getString("reviewContent");
                            String timeStamp = document.getString("timestamp");
                            String userId = document.getString("userId");
                            Review review = new Review(productId, ratingPoint, reviewContent, timeStamp);



                            // Get a reference to the User document using the userId
                            DocumentReference userRef = db.collection("User")
                                    .document("xnJNFIe8KoGheyJXrLWf");

                            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);

                                        review.setUserReviewName(user.getName());
                                        review.setUserAvatar(user.getAvatar());


                                        reviews.add(review);

                                        listView = (DynamicHeightListView) findViewById(R.id.list_view);
                                        ReviewsListAdapter reviewsListAdapter = new ReviewsListAdapter(getApplicationContext(), reviews);
                                        listView.setAdapter(reviewsListAdapter);


                                        double averageRatingPointCount = getAveragePoint(reviews);

                                        if(averageRatingPointCount == 0 ||
                                                (averageRatingPointCount + "").length() == 0) {
                                            averageRatingPoint.setText(0 + "");
                                        } else {
                                            averageRatingPoint.setText(averageRatingPointCount + "");
                                        }

                                        averageRatingbar.setRating((float) averageRatingPointCount);

                                        if(reviews.size() == 0) {
                                            totalReviewsNumber.setText(0 + "");
                                        } else {
                                            totalReviewsNumber.setText(reviews.size() + "");
                                        }

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Log.i("message", e.getMessage());
                                    Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }


                } else {
                    // Query failed, handle the error
                    Exception exception = task.getException();
                    Log.i("Error", exception.getMessage());
                    Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public double getAveragePoint(List<Review> listReview) {

        if(listReview.size()==0) {
            return 0;
        }

        double result = 0;

        for(Review review : listReview) {
            result += review.getRatingPoint();
        }

        return result/listReview.size();
    }

    public void fetchDataReviewsFilter(String productId, int ratingPoint) {
        db = FirebaseFirestore.getInstance();


        Query query = db.collection("Review").whereEqualTo("productId", productId).whereEqualTo("ratingPoint", ratingPoint);


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Review> reviews = new ArrayList<>();
                    String userId = "";
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Query successful, iterate over the documents
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Access the document data
                            String productId = document.getString("productId");
                            int ratingPoint = document.getLong("ratingPoint").intValue();
                            String reviewContent = document.getString("reviewContent");
                            String timeStamp = document.getString("timestamp");
                            userId = document.getString("userId");
                            Review review = new Review(productId, ratingPoint, reviewContent, timeStamp);



                            // Get a reference to the User document using the userId
                            DocumentReference userRef = db.collection("User").document("xnJNFIe8KoGheyJXrLWf");



                            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()) {
                                        User user = documentSnapshot.toObject(User.class);

                                        review.setUserReviewName(user.getName());
                                        review.setUserAvatar(user.getAvatar());


                                        reviews.add(review);

                                        listView = (DynamicHeightListView) findViewById(R.id.list_view);
                                        ReviewsListAdapter reviewsListAdapter = new ReviewsListAdapter(getApplicationContext(), reviews);
                                        listView.setAdapter(reviewsListAdapter);

                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                    Log.i("message", e.getMessage());
                                    Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }


                } else {
                    // Query failed, handle the error
                    Exception exception = task.getException();
                    Log.i("Error", exception.getMessage());
                    Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void addReview(Review review) {
        Map<String, Object> reviewData = new HashMap<>();
        reviewData.put("productId", review.getProductId());
        reviewData.put("ratingPoint", review.getRatingPoint());
        reviewData.put("reviewContent", review.getReviewContent());
        reviewData.put("timestamp", review.getTimestamp());
        reviewData.put("userId", review.getUserId());

        db = FirebaseFirestore.getInstance();
        // Add a new document with a generated ID
        db.collection("Review")
                .add(reviewData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(DetailedActivity.this, "Review added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedActivity.this, "Review added failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void addProductToShoppingCart(Booking booking) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();


        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("productId", booking.getProductId());
        bookingData.put("userId", booking.getUserId());
        bookingData.put("quantity", booking.getQuantity());
        bookingData.put("size", booking.getSize());
        bookingData.put("color", booking.getColor());
        bookingData.put("timestamp", formatter.format(date));
        bookingData.put("totalPrice", booking.getTotalPrice());
        bookingData.put("productName", booking.getProductName());
        bookingData.put("status", booking.getStatus());
        bookingData.put("id", "");
        bookingData.put("type", booking.getType());

        db = FirebaseFirestore.getInstance();
        // Add a new document with a generated ID
        db.collection("Booking")
                .add(bookingData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        db.collection("Booking")
                                        .document(documentReference.getId())
                                                .update("id", documentReference.getId())
                                .addOnSuccessListener(task -> {
                                    Toast.makeText(getApplicationContext(), "Added to cart !!!", Toast.LENGTH_SHORT).show();
                                });
                    }
                });
    }

    public void bookingAProduct(Booking booking) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();


        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("productId", booking.getProductId());
        bookingData.put("userId", booking.getUserId());
        bookingData.put("quantity", booking.getQuantity());
        bookingData.put("size", booking.getSize());
        bookingData.put("color", booking.getColor());
        bookingData.put("timestamp", formatter.format(date));
        bookingData.put("totalPrice", booking.getTotalPrice());
        bookingData.put("productName", booking.getProductName());
        bookingData.put("status", booking.getStatus());
        bookingData.put("id", "");
        bookingData.put("type", booking.getType());

        db = FirebaseFirestore.getInstance();
        // Add a new document with a generated ID
        db.collection("Booking")
                .add(bookingData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        DocumentReference productRef = db.collection("Product").document(booking.getProductId());
                        db.collection("Booking")
                                .document(documentReference.getId())
                                .update("id", documentReference.getId());

                        productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()) {
                                    Product updateProduct = documentSnapshot.toObject(Product.class);

                                    Log.i("booking", booking.toString());

                                    Log.i("sizes", updateProduct.getSizes().get(booking.getSize()).toString());

                                    if(updateProduct.getQuantity()-booking.getQuantity() < 0 || updateProduct.getSizes().get(booking.getSize()) - booking.getQuantity() < 0) {
                                        Toast.makeText(DetailedActivity.this, "This product is not available now", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    updateProduct.setQuantity(updateProduct.getQuantity()-booking.getQuantity());

                                    updateProduct.getSizes().put(booking.getSize(), updateProduct.getSizes().get(booking.getSize()) - booking.getQuantity());

                                    db.collection("Product").document(booking.getProductId()).set(updateProduct).
                                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(DetailedActivity.this, "Successfully booking", Toast.LENGTH_SHORT).show();
                                                    Log.i("Update quantity", "Update quantity successfully");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i("Update quantity", "Update quantity failed");
                                                }
                                            });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                                Log.i("message", e.getMessage());
                                Toast.makeText(DetailedActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                }).continueWith(command -> {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.putExtra("userId", HomeActivity.currentUser.getId());
                    startActivity(intent);
                    Toast.makeText(this,"Order success", Toast.LENGTH_LONG).show();

                    return null;
                });
    }
}