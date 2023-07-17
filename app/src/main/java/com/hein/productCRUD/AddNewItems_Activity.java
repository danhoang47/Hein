package com.hein.productCRUD;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hein.R;
import com.hein.activities.adapters.ColorBookingdatper;
import com.hein.entity.Booking;
import com.hein.entity.Product;
import com.hein.home.filter.ColorViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AddNewItems_Activity extends AppCompatActivity {
    AppCompatButton Uploadbutton;

    TextInputEditText productName, productBrand, productCategory, productQuantity, productPrice, productType;

    CheckBox isMale, isFemale;

    RelativeLayout PickImagebutton;
    ViewPager viewPager;
    Uri ImageUri;
    ArrayList<Uri> ChooseImageList;
    ArrayList<String> UrlsList;

    FirebaseFirestore firestore;


    StorageReference storagereference;

    FirebaseStorage mStorage;

    ProgressDialog progressDialog;

    Set<String> bookingColor = new HashSet<>();

    ColorAddProductAdapter colorAddProductAdatper;

    List<String> listColor = Arrays.asList("black", "white", "gray",
            "persian_rose", "purple", "blue_gem", "royal_blue", "picton_blue", "zircon", "lightGrey", "duskYellow", "light_green", "green");

    RecyclerView viewColors;

    Button addSizeBtn;

    LinearLayout containerSizeLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_items);
        PickImagebutton = findViewById(R.id.ChooseImage);
        viewPager = findViewById(R.id.viewPager);
        productName = findViewById(R.id.product_name);
        productBrand = findViewById(R.id.product_brand);
        productCategory = findViewById(R.id.product_category);
        productQuantity = findViewById(R.id.product_quantity);
        productPrice = findViewById(R.id.product_price);
        productType = findViewById(R.id.product_type);
        isMale = findViewById(R.id.is_male);
        isFemale = findViewById(R.id.is_female);
        addSizeBtn = findViewById(R.id.add_size);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");


        // firebase Instance
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();
        Uploadbutton = findViewById(R.id.UploadBtn);
        containerSizeLayout = findViewById(R.id.size_container);

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();

        viewColors = findViewById(R.id.color_add_product);

        initColorRV(viewColors, listColor);
        PickImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckPermission();

            }
        });
        Uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadIMages();
            }
        });

        addSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTextView();
                addEditText();
                addEditText2();
                /*addDualEditText();*/
            }
        });
    }

    private Map<String, Integer> extractMapFromList(List<String> listSize) {
        Map<String, Integer> listExtract = new HashMap<>();

        for (int i = 0; i < listSize.size(); i += 2) {
            String key = (String) listSize.get(i);
            int value = Integer.parseInt(listSize.get(i + 1));
            listExtract.put(key, value);
        }

        return listExtract;
    }

    private void addDualEditText() {
        FrameLayout editTextSize = findViewById(R.id.product_size_quantity_item);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        editTextSize.setLayoutParams(params);

        containerSizeLayout.addView(editTextSize);
    }

    private void addEditText() {
        TextInputEditText editTextSize = new TextInputEditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        editTextSize.setLayoutParams(params);
        editTextSize.setHint("Size");

        containerSizeLayout.addView(editTextSize);
    }

    private void addTextView() {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText("New size");

        containerSizeLayout.addView(textView);
    }

    private void addEditText2() {
        TextInputEditText editTextQuantity = new TextInputEditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);;

        editTextQuantity.setLayoutParams(params);
        editTextQuantity.setHint("Quantity");

        containerSizeLayout.addView(editTextQuantity);
    }

    private void UploadIMages() {

        // we need list that images urls
        for (int i = 0; i < ChooseImageList.size(); i++) {
            Uri IndividualImage = ChooseImageList.get(i);
            if (IndividualImage != null) {
                progressDialog.show();
                StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages");
                final StorageReference ImageName = ImageFolder.child("Image" + i + ": " + IndividualImage.getLastPathSegment());
                ImageName.putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ImageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UrlsList.add(String.valueOf(uri));
                                if (UrlsList.size() == ChooseImageList.size()) {


                                    StoreLinks(UrlsList);
                                }
                            }
                        });

                    }
                });
            } else {
                Toast.makeText(this, "Please fill All Field", Toast.LENGTH_SHORT).show();
            }
        }


    }


    private List<String> getAllEditTextData() {
        int childCount = containerSizeLayout.getChildCount();
        List<String> editTextDataList = new ArrayList<>();

        for (int i = 0; i < childCount; i++) {
            View childView = containerSizeLayout.getChildAt(i);
            if (childView instanceof TextInputEditText) {
                TextInputEditText editText = (TextInputEditText) childView;
                String editTextData = editText.getText().toString();
                editTextDataList.add(editTextData);
            }
        }

        return editTextDataList;
        // Do something with the editTextDataList containing the data from all EditText views
    }

    public void initColorRV(View view, List<String> colorsList) {

        List<ColorViewModel> colors = new ArrayList<>();

        for(String color : colorsList) {
            colors.add(new ColorViewModel(color, color, false));
        }

        RecyclerView recyclerView = view.findViewById(R.id.color_add_product);
        colorAddProductAdatper = new ColorAddProductAdapter(getApplicationContext(), colors, bookingColor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(colorAddProductAdatper);
    }


    private void StoreLinks(ArrayList<String> urlsList) {

        List<String> listSize = getAllEditTextData();
        Map<String, Integer> sizeMap = extractMapFromList(listSize);

        // now we need get text from EditText
        String productNameVal = productName.getText().toString();
        String productBrandVal = productBrand.getText().toString();
        String productCategoryVal = productCategory.getText().toString();
        int productQuantityVal = 0;
        try {
            productQuantityVal = Integer.parseInt(productQuantity.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Quantity require a number", Toast.LENGTH_SHORT).show();
            return;
        }

        Double productPriceVal = Double.valueOf(0);

        try {
            productPriceVal = Double.parseDouble(productPrice.getText().toString());
        } catch (Exception e) {
            Toast.makeText(this, "Price require a number", Toast.LENGTH_SHORT).show();
            return;
        }

        String productTypeVal = productType.getText().toString();
        List<String> classification = new ArrayList<>();
        List<String> colors = new ArrayList<>(bookingColor);

        if(isMale.isChecked()) {
            classification.add("male");
        }

        if(isFemale.isChecked()) {
            classification.add("female");
        }

        if (!TextUtils.isEmpty(productNameVal) && !TextUtils.isEmpty(productBrandVal) && !TextUtils.isEmpty(productCategoryVal) &&
                productQuantityVal != 0 &&
                productPriceVal != 0 &&
                !TextUtils.isEmpty(productTypeVal) && ImageUri != null) {
            // now we need a model class
            Product model = new Product(productBrandVal, productCategoryVal,
                    classification, colors, productNameVal, productPriceVal, productQuantityVal, productTypeVal, sizeMap);
            model.setImages(urlsList);
            firestore.collection("Product").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    // now here we need Item id and set into model
                    model.setId(documentReference.getId());
                    firestore.collection("Product").document(model.getId())
                            .set(model, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i("list url", urlsList.toString());

                                    progressDialog.dismiss();
                                    // if data uploaded successfully then show ntoast

                                    Toast.makeText(AddNewItems_Activity.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(AddNewItems_Activity.this, ViewAllProductAdminActivity.class);
                                    startActivity(i);

                                }
                            });


                }
            });

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill All field", Toast.LENGTH_SHORT).show();
        }
        // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();


    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(AddNewItems_Activity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddNewItems_Activity.this, new
                        String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            } else {
                PickImageFromgallry();
            }

        } else {
            PickImageFromgallry();
        }
    }

    private void PickImageFromgallry() {
        // here we go to gallery and select Image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                ImageUri = data.getClipData().getItemAt(i).getUri();
                ChooseImageList.add(ImageUri);
// now we need Adapter to show Images in viewpager

            }
            setAdapter();

        }
    }

    private void setAdapter() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, ChooseImageList);
        viewPager.setAdapter(adapter);
    }






}