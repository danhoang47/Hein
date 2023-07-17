package com.hein.home.filter;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hein.R;
import com.hein.entity.Product;
import com.hein.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior bottomSheetBehavior;
    SizeRVAdapter sizeRVAdapter;
    ColorRVAdatper colorRVAdatper;
    TypeRVAdapter typeRVAdapter;
    FilterViewModel filterViewModel;
    MaterialButton menSelectBtn;
    MaterialButton womenSelectBtn;
    RangeSlider priceRangeSlider;
    ExtendedFloatingActionButton filterBtn;
    ExtendedFloatingActionButton resetBtn;
    FirebaseFirestore db;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_filter, container, false);
        setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.DialogSlideAnim);

        filterBtn = view.findViewById(R.id.filter_btn);
        filterBtn.setOnClickListener(filterBtnView -> onFilterClick(filterBtnView));

        resetBtn = view.findViewById(R.id.reset_btn);

        priceRangeSlider = view.findViewById(R.id.price_range_slider);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        FrameLayout loginLayout = getDialog().findViewById(R.id.filter_layout);
        loginLayout.setMinimumHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.90));

        filterViewModel = new ViewModelProvider(getActivity()).get(FilterViewModel.class);
        db = FirebaseFirestore.getInstance();


        if (filterViewModel.getPriceFrom() != 0 &&
                filterViewModel.getPriceTo() != 0) {
            initPriceRangeSlider();
        } else {
            db.collection("Product")
                    .orderBy("price", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().size() != 0) {
                            HomeActivity.MAX_PRICE = task.getResult().toObjects(Product.class).get(0).getPrice();

                        }

                        db.collection("Product")
                                .orderBy("price")
                                .limit(1)
                                .get()
                                .addOnCompleteListener(secondTask -> {
                                    if (secondTask.isSuccessful() && secondTask.getResult().size() != 0) {
                                        HomeActivity.MIN_PRICE = secondTask.getResult().toObjects(Product.class).get(0).getPrice();
                                        initPriceRangeSlider();
                                    }
                                });
                    });

        }


        initAllFilterOption(view);

        resetBtn.setOnClickListener(resetViewBtn -> {
            filterViewModel.unsetAllProperties(HomeActivity.MIN_PRICE, HomeActivity.MAX_PRICE);
            initAllFilterOption(view);
            initPriceRangeSlider();
        });
    }

    private void initPriceRangeSlider() {
        priceRangeSlider.setValueTo((float) HomeActivity.MAX_PRICE);
        priceRangeSlider.setValueFrom((float) HomeActivity.MIN_PRICE);
        priceRangeSlider.setValues((float) HomeActivity.MIN_PRICE, (float) HomeActivity.MAX_PRICE);
        priceRangeSlider.setStepSize(1);
    }

    private void initAllFilterOption(View view) {
        initSizeRV(view);
        initColorRV(view);
        initTypeRV(view);
        initGenderSelectGroup(view);
    }

    public void onFilterClick(View view) {
        // TODO: get valueFrom and valueTo from rangeSlider
        filterViewModel.setPriceFrom(priceRangeSlider.getValues().get(0));
        filterViewModel.setPriceTo(priceRangeSlider.getValues().get(1));
        filterViewModel.filterState.postValue(true);
        getDialog().dismiss();
    }

    public void initGenderSelectGroup(View rootView) {
        menSelectBtn = rootView.findViewById(R.id.men_filter_option);
        womenSelectBtn = rootView.findViewById(R.id.women_filter_option);
        final String MALE = "male";
        final String FEMALE = "female";
        setGenderButtonBackgroundColor(menSelectBtn, "white", "black", "gray");
        setGenderButtonBackgroundColor(womenSelectBtn, "white", "black", "gray");

        if (filterViewModel.getGenders().contains(MALE)) {
            setGenderButtonBackgroundColor(
                    menSelectBtn,
                    "black",
                    "white",
                    "black");
        }
        if (filterViewModel.getGenders().contains(FEMALE)) {
            setGenderButtonBackgroundColor(
                    womenSelectBtn,
                    "black",
                    "white",
                    "black");
        }

        menSelectBtn.setOnClickListener(view -> onGenderSelect((MaterialButton) view, MALE));
        womenSelectBtn.setOnClickListener(view -> onGenderSelect((MaterialButton) view, FEMALE));
    }

    public void onGenderSelect(MaterialButton view, String gender) {
        if (filterViewModel.getGenders().contains(gender)) {
            filterViewModel.getGenders().remove(gender);
            setGenderButtonBackgroundColor(view, "white", "black", "gray");
        } else {
            filterViewModel.getGenders().add(gender);
            setGenderButtonBackgroundColor(view, "black", "white", "black");
        }
    }

    private void setGenderButtonBackgroundColor(
            MaterialButton view,
            String backgroundTintColor,
            String textColor,
            String strokeColor) {
        view.setBackgroundTintList(getColorStateListByName(backgroundTintColor));
        view.setTextColor(getColorStateListByName(textColor));
        view.setStrokeColor(getColorStateListByName(strokeColor));
    }

    private ColorStateList getColorStateListByName(String colorName) {
        return getResources().getColorStateList(
                getResources().getIdentifier(colorName, "color", getActivity().getPackageName()),
                null
        );
    }

    public void initTypeRV(View view) {
        List<String> types = new ArrayList<>();

        types.add("Top");
        types.add("Bottom");
        types.add("Denim");
        types.add("Dresses");
        types.add("Jewelry & Watches");
        types.add("Accessories");

        RecyclerView recyclerView = view.findViewById(R.id.typeFilterRV);
        typeRVAdapter = new TypeRVAdapter(this.getContext(), types, filterViewModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(typeRVAdapter);
    }

    public void initColorRV(View view) {
        List<ColorViewModel> colors = new ArrayList<>();

        colors.add(new ColorViewModel("black", "black"));
        colors.add(new ColorViewModel("white", "white"));
        colors.add(new ColorViewModel("persian_rose", "persian_rose"));
        colors.add(new ColorViewModel("blue_gem", "blue_gem"));
        colors.add(new ColorViewModel("purple", "purple"));
        colors.add(new ColorViewModel("picton_blue", "picton_blue"));

        RecyclerView recyclerView = view.findViewById(R.id.colorFilterRV);
        colorRVAdatper = new ColorRVAdatper(this.getContext(), colors, filterViewModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(colorRVAdatper);
    }

    public void initSizeRV(View view) {
        List<String> productSizes = new ArrayList<>();

        productSizes.add("XS");
        productSizes.add("SM");
        productSizes.add("MD");
        productSizes.add("LG");
        productSizes.add("XL");
        productSizes.add("XXL");

        RecyclerView recyclerView = view.findViewById(R.id.sizeFilterRV);
        sizeRVAdapter = new SizeRVAdapter(this.getContext(), productSizes, filterViewModel);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sizeRVAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();

        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        window.setLayout(width, height);
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}