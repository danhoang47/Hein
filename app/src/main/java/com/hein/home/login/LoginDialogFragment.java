package com.hein.home.login;

import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.hein.R;
import com.hein.databinding.FragmentLoginBinding;
import com.hein.entity.User;
import com.hein.home.HomeActivity;

public class LoginDialogFragment extends BottomSheetDialogFragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;
    BottomSheetBehavior bottomSheetBehavior;
    private EditText edtUsername;
    private EditText edtPassword;
    private Button btnLogin;
    private CheckBox cbRememberMe;
    private TextView signUpLink;
    private TextView signUpTitle;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.DialogSlideAnim);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        FrameLayout loginLayout = getDialog().findViewById(R.id.login_layout);
        loginLayout.setMinimumHeight((int)(getResources().getDisplayMetrics().heightPixels*0.90));

        edtUsername = view.findViewById(R.id.username);
        edtPassword = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.login);
        cbRememberMe = view.findViewById(R.id.remember_me_btn);
        signUpLink = view.findViewById(R.id.sign_up_link);
        signUpTitle = view.findViewById(R.id.sign_up_title);


        loginViewModel = new ViewModelProvider(getActivity()).get(LoginViewModel.class);


        signUpLink.setOnClickListener(signUpLink -> displayWithSignup());

        displayWithLogin();
    }

    public void displayWithSignup() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Sign Up");
        signUpTitle.setText("Already have an account ?");
        signUpLink.setText(R.string.login_link);
        signUpLink.setOnClickListener(signUpLink -> {
            displayWithLogin();
        });
        cbRememberMe.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(btnView -> onSignup(btnView));
    }

    public void displayWithLogin() {
        btnLogin.setEnabled(true);
        btnLogin.setText("Login");
        signUpTitle.setText("Need an account?");
        signUpLink.setText(R.string.sign_up_link);
        cbRememberMe.setVisibility(View.VISIBLE);
        signUpLink.setOnClickListener(signUpLink -> displayWithSignup());
        btnLogin.setOnClickListener(btnView -> onLogin(btnView));
    }

    public void onSignup(View view) {
        btnLogin.setEnabled(false);
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if (isUsernameAndPasswordSatisfaction()) {
            CollectionReference userRef = FirebaseFirestore.getInstance().collection("User");
            userRef
                    .whereEqualTo("username", username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() != 0) {
                                Toast.makeText(
                                        getContext(),
                                        "Username is existed",
                                        Toast.LENGTH_SHORT).show();
                                btnLogin.setEnabled(true);
                            }
                            else {
                                User user = new User();
                                user.setUsername(username);
                                user.setPassword(password);

                                userRef.add(user)
                                        .addOnCompleteListener(docRef -> {
                                            if (docRef.isSuccessful()) {
                                                String id = docRef.getResult().getId();
                                                userRef.document(id)
                                                        .update("id", id)
                                                        .addOnCompleteListener(command -> {
                                                            btnLogin.setEnabled(true);
                                                            Toast.makeText(
                                                                    getContext(),
                                                                    "Sign up successfully !!!",
                                                                    Toast.LENGTH_SHORT).show();
                                                            displayWithLogin();
                                                        });
                                            }
                                        });
                            }
                        }
                    });
        }
        else {
            Toast.makeText(
                    getContext(),
                    "Username or password must not empty !!",
                    Toast.LENGTH_SHORT).show();
            btnLogin.setEnabled(true);
        }
    }

    public void onLogin(View view) {
        if (isUsernameAndPasswordSatisfaction()) {
            String username = edtUsername.getText().toString();
            String password = edtPassword.getText().toString();
            db = FirebaseFirestore.getInstance();
            db.collection("User")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().size() != 0) {
                            User user = task.getResult().toObjects(User.class).get(0);
                            HomeActivity.currentUser = user;
                            loginViewModel.setLoginState(true);
                            loginViewModel.setHasRememberMe(cbRememberMe.isChecked());
                            getDialog().dismiss();
                        } else {
                            Toast.makeText(
                                    getContext(),
                                    "Username and password not found",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public boolean isUsernameAndPasswordSatisfaction() {
        return (
            !edtUsername.getText().toString().isEmpty() &&
            !edtPassword.getText().toString().isEmpty()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();

        int width = (int)(getResources().getDisplayMetrics().widthPixels*1);
        int height = (int)(getResources().getDisplayMetrics().heightPixels*0.90);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}