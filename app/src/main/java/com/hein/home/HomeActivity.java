package com.hein.home;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hein.BaseActivity;
import com.hein.account.AccountActivity;
import com.hein.activities.DetailedActivity;
import com.hein.home.filter.Classification;
import com.hein.entity.Product;
import com.hein.R;
import com.hein.entity.User;
import com.hein.home.filter.FilterDialogFragment;
import com.hein.home.filter.FilterViewModel;
import com.hein.home.login.LoginDialogFragment;
import com.hein.home.login.LoginViewModel;
import com.hein.shoppingcart.ShoppingCartActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HomeActivity extends BaseActivity implements OnProductClickListener {
    ClassificationRVAdapter classificationRVAdapter;
    ProductRVAdapter productRVAdapter;
    Button openUserMenuBtn;
    Button openFilterBtn;
    FilterViewModel filterViewModel;
    public static User currentUser;
    LoginViewModel loginViewModel;
    public static double MIN_PRICE;
    public static double MAX_PRICE;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        filterViewModel = new FilterViewModel();

        setSupportActionBar(findViewById(R.id.header_bar));
        initClassificationRV();
        fetchProduct(this::initProductRV);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        initNavigationBoard();

        openFilterBtn = findViewById(R.id.open_filter_btn);
        openFilterBtn.setOnClickListener(view -> showFilterDialog());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        observeLoginViewModel();

        filterViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
        observerFilterViewModel();

        userRepository = new UserRepository(getApplication());

        fetchUserFromLocalStorage();
    }

    public void initNavigationBoard() {

        bottomNavigationView.setSelectedItemId(R.id.page_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.page_setting) {
                if (currentUser == null) {
                    showLoginDialog();
                    return false;
                } else {
                Intent intent = new Intent(this, AccountActivity.class);
                intent.putExtra("userId", currentUser.getId());
                overridePendingTransition(0, 0);
                startActivity(intent);
//                overridePendingTransition(0, 0);
                return true;}
            } else if (itemId == R.id.page_shopping_cart) {
                if (currentUser == null) {
                    showLoginDialog();
                    return false;
                } else {
                    Intent intent = new Intent(this, ShoppingCartActivity.class);
                    intent.putExtra("userId", currentUser.getId());
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    return true;
                }
            } else if (itemId == R.id.page_setting) {
                if (currentUser == null) {
                    showLoginDialog();
                    return false;
                } else {
                    // NOTE: Account Setting Intent
                    Intent intent = new Intent(this, ShoppingCartActivity.class);
                    intent.putExtra("userId", currentUser.getId());
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    return true;
                }
            }

            return false;
        });
    }

    @Override
    public void onProductClick(String productId) {
        Intent detailIntent = new Intent(getApplicationContext(), DetailedActivity.class);
        detailIntent.putExtra("productId", productId);
        startActivity(detailIntent);
    }

    public void observeLoginViewModel() {
        loginViewModel.loginState.observe(this, observer -> {
            if (loginViewModel.loginState.getValue()) {
                Toast.makeText(
                        this,
                        "Welcome back, " + currentUser.getName(),
                        Toast.LENGTH_SHORT)
                .show();

                Log.i("MSG", "User Id: " + currentUser.getId());
            } else {

            }
        });
    }

    public void observerFilterViewModel() {
        filterViewModel.filterState.observe(this, observer -> {
            if (filterViewModel.filterState.getValue()) {
                filterViewModel.filterState.setValue(false);
                fetchProduct(products -> {
                    productRVAdapter.setProducts(products);
                    productRVAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    public void fetchUserFromLocalStorage() {
        List<User> savedUsers = userRepository.getAll();
        if (savedUsers.size() != 0) {
            currentUser = savedUsers.get(0);
            loginViewModel.setLoginState(true);
        }
    }

    public void fetchProduct(Consumer<List<Product>> onCompleteCallback) {
        Query query = FirebaseFirestore.getInstance().collection("Product");
        Set<String> genders = filterViewModel.getGenders();
        Set<String> sizes = filterViewModel.getSizes();
        Set<String> colors = filterViewModel.getColors();
        Set<String> types = filterViewModel.getTypes();
        Log.i("MSG", filterViewModel.toString());

        if (genders.size() != 0) {
            query = query.whereArrayContainsAny(
                    "classifications",
                    Arrays.asList(genders.toArray()));
        }
        if (
            filterViewModel.getPriceFrom() != MIN_PRICE ||
            filterViewModel.getPriceTo() != MAX_PRICE) {
            query = query
                        .whereLessThanOrEqualTo("price", filterViewModel.getPriceTo())
                        .whereGreaterThanOrEqualTo("price", filterViewModel.getPriceFrom());

        }

        query
                .get()
                .addOnCompleteListener(task -> {
                    Log.i("MSG", "task completed");
                   if (task.isSuccessful()) {
                       List<Product> products = task.getResult()
                            .toObjects(Product.class)
                            .stream()
                            .filter(product -> {
                               boolean isTypeMatched = true;
                               boolean isSizeMatched = true;
                               boolean isColorMatched = true;
                               if (types.size() != 0) {
                                    isTypeMatched = types.contains(product.getType());
                               }
                               if (colors.size() != 0) {
                                   isColorMatched = colors
                                           .stream()
                                           .anyMatch(color -> product.getColors().contains(color));
                               }
                               if (sizes.size() != 0) {
                                   isSizeMatched = sizes.stream()
                                           .anyMatch(size -> product.getSizes().get(size) != null);
                               }
                               return isTypeMatched && isSizeMatched && isColorMatched;
                            }).collect(Collectors.toList());
                       onCompleteCallback.accept(products);
                   }
                });
    }

    protected void initProductRV(List<Product> products) {
        RecyclerView productRV = findViewById(R.id.productRV);
        productRVAdapter = new ProductRVAdapter(this, products, (productId -> {
            onProductClick(productId);
        }));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(HomeActivity.this, 2);
        productRV.setAdapter(productRVAdapter);
        productRV.setLayoutManager(gridLayoutManager);
    }


    protected void initClassificationRV() {
        List<Classification> classifications = getClassification();

        RecyclerView classificationRV = findViewById(R.id.classificationRV);
        classificationRVAdapter = new ClassificationRVAdapter(this, classifications);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        classificationRV.setLayoutManager(layoutManager);
        classificationRV.setAdapter(classificationRVAdapter);
    }

    private List<Classification> getClassification() {
        List<Classification> classifications = new ArrayList<>();

        Resources resources = HomeActivity.this.getResources();
        int categoryResourceId = R.drawable.outline_category_24;
        classifications.add(new Classification(1, resources.getDrawable(categoryResourceId), "Category"));
        classifications.add(new Classification(2, resources.getDrawable(categoryResourceId), "Man"));
        classifications.add(new Classification(3, resources.getDrawable(categoryResourceId), "Woman"));
        classifications.add(new Classification(5, resources.getDrawable(categoryResourceId), "Child"));
        classifications.add(new Classification(6, resources.getDrawable(categoryResourceId), "Infant"));
        classifications.add(new Classification(7, resources.getDrawable(categoryResourceId), "Pet"));
        classifications.add(new Classification(8, resources.getDrawable(categoryResourceId), "Adult"));
        classifications.add(new Classification(9, resources.getDrawable(categoryResourceId), "Mermaid"));

        return classifications;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        openUserMenuBtn = findViewById(R.id.open_user_menu_btn);

        openUserMenuBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
            popupMenu.getMenuInflater().inflate(R.menu.user_menu, popupMenu.getMenu());

            if (loginViewModel.loginState.getValue()) {
                popupMenu.getMenu().findItem(R.id.login_menu_item).setVisible(false);
                popupMenu.getMenu().findItem(R.id.logout_menu_item).setVisible(true);

                if (currentUser.getRole() == 2) {
                    popupMenu.getMenu().findItem(R.id.dashboard_menu_item).setVisible(true);
                }
            } else {
                popupMenu.getMenu().findItem(R.id.login_menu_item).setVisible(true);
                popupMenu.getMenu().findItem(R.id.logout_menu_item).setVisible(false);
                popupMenu.getMenu().findItem(R.id.dashboard_menu_item).setVisible(false);
            }
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.login_menu_item) {
                    showLoginDialog();
                    return true;
                } else if (id == R.id.logout_menu_item) {
                    List<User> savedUsers = userRepository.getAll();
                    if (savedUsers.size() != 0) {
                         userRepository.delete(currentUser);
                    }
                    currentUser = null;
                    loginViewModel.setHasRememberMe(false);
                    loginViewModel.setLoginState(false);
                }
                else if (id == R.id.dashboard_menu_item) {
                    // TODO: navigation to dashboard
                }
                return false;
            });

            popupMenu.show();
        });

        return super.onCreateOptionsMenu(menu);
    }

    protected void showLoginDialog() {
        LoginDialogFragment dialog = new LoginDialogFragment();

        dialog.show(getSupportFragmentManager(), "loginDialog");
    }

    protected  void showFilterDialog() {
        FilterDialogFragment dialog = new FilterDialogFragment();

        dialog.show(getSupportFragmentManager(), "filterDialog");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.page_home);
        if (HomeActivity.currentUser != null) {
            loginViewModel.setLoginState(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        Log.i("MSG", "ACTIVITY DESTROY");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (loginViewModel.hasRememberMe != null
            && loginViewModel.hasRememberMe.getValue()
        ) {
            userRepository.insert(currentUser);
        }
        Log.i("MSG", "ACTIVITY STOP");
    }
}