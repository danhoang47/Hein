package com.hein.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hein.R;
import com.hein.entity.Booking;
import com.hein.entity.Product;
import com.example.a2handee.activities.services.DashboardService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hein.productCRUD.ViewAllProductAdminActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.List;
import java.util.Map;

public class IncomeFragment extends Fragment {

    FirebaseFirestore db;

    TextView orderNumber, returnNumber, totalIncome, totalIncomeProgress, averageIncome, averageIncomeProgress;

    CircularProgressBar totalProgress, averageProgress;

    AppCompatButton manageProductBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fetchData();
        View view = inflater.inflate(R.layout.fragment_income, container, false);
        manageProductBtn = view.findViewById(R.id.btn_product_manage);
        manageProductBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewAllProductAdminActivity.class);
            startActivity(intent);
        });
        return view;
    }


    public void fetchData() {
        db = FirebaseFirestore.getInstance();

        db.collection("Booking").orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Booking> bookingList = queryDocumentSnapshots.toObjects(Booking.class);

                for(Booking booking: bookingList) {
                    Log.i("Booking order", booking.toString());
                    Log.i("Booking totalPrice", booking.getTotalPrice() + "");
                }

                Map<String, Integer> totalIncomeVal = DashboardService.getIncome(bookingList);
                List<String> listProductId = DashboardService.getNotDuplicatedListId(bookingList);

                db.collection("Product")
                        .whereIn(FieldPath.documentId(), listProductId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);

                                /* Get total price base on quantity and price of each product */
                                int result = 0;
                                int numberOfProduct = 0;

                                for(int i =0 ; i<productList.size(); i++) {
                                    result += productList.get(i).getQuantity() *  productList.get(i).getPrice();
                                    Log.i("Quantity xxxx", result + "");
                                    numberOfProduct += productList.get(i).getQuantity();
                                }

                                int totalPercent = 0;
                                int averagePercent = 0;

                                try {
                                    totalPercent = (int) (((double) totalIncomeVal.get("total") / (totalIncomeVal.get("total") + result)) * 100);

                                } catch (Exception e) {
                                    totalPercent = 0;
                                }

                                try {
                                    averagePercent = (int) (((double) totalIncomeVal.get("average") / (double) (totalIncomeVal.get("average") + result / numberOfProduct)) * 100);
                                } catch (Exception e) {
                                    averagePercent = 0;
                                }




                                totalIncomeProgress = getView().findViewById(R.id.total_income_progress_value);
                                totalIncomeProgress.setText(totalPercent + "%");
                                totalProgress = getView().findViewById(R.id.total_income_progress);
                                totalProgress.setProgress(totalPercent);

                                averageIncomeProgress = getView().findViewById(R.id.average_progress_value);
                                averageIncomeProgress.setText(averagePercent + "%");
                                averageProgress = getView().findViewById(R.id.average_progress);
                                averageProgress.setProgress(averagePercent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle any errors
                                Log.e("TAG", "Error querying documents: " + e.getMessage());
                            }
                        });




                Log.i("total", totalIncomeVal.get("total") + "");

                /* Set text for total income */
                totalIncome = getView().findViewById(R.id.total_income_value);
                totalIncome.setText(totalIncomeVal.get("total") + "");

                /* Set text for average income */
                averageIncome = getView().findViewById(R.id.average_value);
                averageIncome.setText(totalIncomeVal.get("average") + "");

                /* Set text for number of orders */
                orderNumber = getView().findViewById(R.id.total_order_number);
                orderNumber.setText(bookingList.size() + "");

                /* Set text for number of returns */
                returnNumber = getView().findViewById(R.id.total_return_number);
                returnNumber.setText("0");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                Log.i("message", e.getMessage());
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}