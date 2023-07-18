package com.hein.productCRUD;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hein.R;
import com.hein.entity.Product;
import com.hein.entity.Review;
import com.hein.home.filter.ColorViewModel;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditOrDeleteProductActivity extends AppCompatActivity {

    AppCompatButton Uploadbutton, DeleteButton;

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

    Product selectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_or_delete_product);

        Intent intent = getIntent();
        selectedProduct = (Product) intent.getSerializableExtra("selectedProduct");

        // Use the selectedProduct object as needed
        if (selectedProduct == null) {
            Log.i("selected product", selectedProduct.toString());
            Toast.makeText(this, "There is no data for this product", Toast.LENGTH_SHORT).show();
        }

        PickImagebutton = findViewById(R.id.ChooseImage_ed);
        viewPager = findViewById(R.id.viewPager_ed);
        productName = findViewById(R.id.product_name_ed);
        productBrand = findViewById(R.id.product_brand_ed);
        productCategory = findViewById(R.id.product_category_ed);
        productQuantity = findViewById(R.id.product_quantity_ed);
        productPrice = findViewById(R.id.product_price_ed);
        productType = findViewById(R.id.product_type_ed);
        isMale = findViewById(R.id.is_male_ed);
        isFemale = findViewById(R.id.is_female_ed);
        addSizeBtn = findViewById(R.id.add_size_ed);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Data");
        progressDialog.setMessage("Please Wait While Uploading Your data...");


        // firebase Instance
        firestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storagereference = mStorage.getReference();
        Uploadbutton = findViewById(R.id.UploadBtn);
        DeleteButton = findViewById(R.id.DeleteBtn);
        containerSizeLayout = findViewById(R.id.size_container);

        ChooseImageList = new ArrayList<>();
        UrlsList = new ArrayList<>();

        viewColors = findViewById(R.id.color_add_product);

        fetchData();
        initColorRV(viewColors, listColor);
        showingPickedImage();

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

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDeleteDialog();
            }
        });


        addSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditText();
                addTextView1("Size");
                addTextView1("Quantity");
            }
        });

        isMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classificationCheck("male");
            }
        });

        isFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                classificationCheck("female");
            }
        });

    }

    private void addEditText() {
        TextInputEditText editTextSize = new TextInputEditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        editTextSize.setLayoutParams(params);
        editTextSize.setHint("New Size");

        containerSizeLayout.addView(editTextSize);
    }

    private void addTextView1(String value) {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText(value);

        containerSizeLayout.addView(textView);
    }


    public void fetchData() {
        productName.setText(selectedProduct.getName().toString());
        productBrand.setText(selectedProduct.getBrand().toString());
        if(selectedProduct.getClassifications().contains("male")) {
            isMale.setChecked(true);
        }
        if(selectedProduct.getClassifications().contains("female")) {
            isFemale.setChecked(true);
        }
        productCategory.setText(selectedProduct.getCategory().toString());
        productQuantity.setText(selectedProduct.getQuantity() + "");
        productPrice.setText(selectedProduct.getPrice() + "");
        productType.setText(selectedProduct.getType() + "");

        for (Map.Entry<String, Integer> entry : selectedProduct.getSizes().entrySet()) {
            // Create TextInputLayout
            addTextView();
            addTextView2(entry.getKey(), "Size");
            addTextView2(entry.getValue().toString(), "Quantity");
        }
    }

    private void showConfirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete_product_layout, null);
        builder.setView(dialogView);

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
                deleteProduct();
                dialog.dismiss(); // Dismiss the dialog after processing
            }
        });

        dialog.show();
    }

    public void classificationCheck(String classType) {
        if(selectedProduct.getClassifications().contains(classType)) {
            selectedProduct.getClassifications().remove(classType);
        } else {
            selectedProduct.getClassifications().add(classType);
        }
    }

    private void addTextView() {
        TextView textView = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setText("New size");
        textView.setTextColor(Color.parseColor("#bdbdbd"));

        containerSizeLayout.addView(textView);
    }

    private void addTextView2(String size, String hint) {
        TextInputEditText editTextSize = new TextInputEditText(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        editTextSize.setLayoutParams(params);
        editTextSize.setHint(hint);
        editTextSize.setText(size);

        containerSizeLayout.addView(editTextSize);
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

    private void UploadIMages() {

        if(ChooseImageList.size() == 0) {
            ArrayList<String> listImgs = new ArrayList<>(selectedProduct.getImages());
            StoreLinks(listImgs);
            return;
        }

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

    private List<String> getAllTextInputEditTextValues() {
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

    private Map<String, Integer> extractMapFromList(List<String> listSize) {
        Log.i("list size text", listSize.toString());
        Map<String, Integer> listExtract = new HashMap<>();

        for (int i = 0; i < listSize.size(); i += 2) {
            String key = (String) listSize.get(i);
            int value = Integer.parseInt(listSize.get(i + 1));
            listExtract.put(key, value);
        }

        return listExtract;
    }

    private void StoreLinks(ArrayList<String> urlsList) {

        List<String> listSize = getAllTextInputEditTextValues();
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
        if(colors.size() == 0) {
             colors = selectedProduct.getColors();
        }


        if(isMale.isChecked()) {
            classification.add("male");
        }

        if(isFemale.isChecked()) {
            classification.add("female");
        }


        if (!TextUtils.isEmpty(productNameVal) && !TextUtils.isEmpty(productBrandVal) && !TextUtils.isEmpty(productCategoryVal) &&
                productQuantityVal != 0 &&
                productPriceVal != 0 &&
                !TextUtils.isEmpty(productTypeVal) && selectedProduct.getImages().size() != 0) {
            // now we need a model class
            Product model = new Product(productBrandVal, productCategoryVal,
                    classification, colors, productNameVal, productPriceVal, productQuantityVal, productTypeVal, sizeMap);
            model.setImages(urlsList);

            model.setId(selectedProduct.getId());

            Log.i("update product", model.toString());

            firestore.collection("Product").document(selectedProduct.getId()).set(model)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(EditOrDeleteProductActivity.this, "Product has been updated ....", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(EditOrDeleteProductActivity.this, ViewAllProductAdminActivity.class);
                            startActivity(i);
                        }



                /*@Override
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
                                    Toast.makeText(EditOrDeleteProductActivity.this, "Your data Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });


                }*/
            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditOrDeleteProductActivity.this, "Update product failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Fill All field", Toast.LENGTH_SHORT).show();
        }
        // if you want to clear viewpager after uploading Images
        ChooseImageList.clear();


    }

    private void deleteProduct() {
        // below line is for getting the collection
        // where we are storing our courses.
        firestore.collection("Product").document(selectedProduct.getId()).delete().
                // after deleting call on complete listener
                // method to delete this data.
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // inside on complete method we are checking
                        // if the task is success or not.
                        if (task.isSuccessful()) {
                            // this method is called when the task is success
                            // after deleting we are starting our MainActivity.
                            Toast.makeText(EditOrDeleteProductActivity.this, "Deleted producted successfully", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(EditOrDeleteProductActivity.this, ViewAllProductAdminActivity.class);
                            startActivity(i);
                        } else {
                            // if the delete operation is failed
                            // we are displaying a toast message.
                            Toast.makeText(EditOrDeleteProductActivity.this, "Deleted producted failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void CheckPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(EditOrDeleteProductActivity.this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(EditOrDeleteProductActivity.this, new
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

    private void showingPickedImage() {
        int count = selectedProduct.getImages().size();
        ArrayList<Uri> listImgUri = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Uri selectProductImageUri = Uri.parse(selectedProduct.getImages().get(i));
            listImgUri.add(selectProductImageUri);
        }
        setAdapter(listImgUri);
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
        EditOrDeletePagerAdapter adapter = new EditOrDeletePagerAdapter(this, ChooseImageList);
        viewPager.setAdapter(adapter);
    }

    private void setAdapter(ArrayList<Uri> listPickedImg) {
        EditOrDeletePagerAdapter adapter = new EditOrDeletePagerAdapter(this, listPickedImg);
        viewPager.setAdapter(adapter);
    }
}