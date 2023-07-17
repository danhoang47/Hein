package com.hein.home.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {
    public MutableLiveData<String> username = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<Boolean> loginState = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasRememberMe = new MutableLiveData<>();

    public void setUsername(String username) {
        this.username.postValue(username);
    }

    public void setPassword(String password) {
        this.password.postValue(password);
    }

    public void setLoginState(Boolean loginState) {
        this.loginState.postValue(loginState);
    }

    public String getPassword() { return this.password.getValue(); }

    public String getUsername() { return this.username.getValue(); }

    public void setHasRememberMe(boolean hasRememberMe) {
        this.hasRememberMe.postValue(hasRememberMe);
    }

    public LoginViewModel() {
        super();
        loginState.postValue(false);
        hasRememberMe.postValue(false);
    }
}