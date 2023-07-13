package com.hein.home;

import android.app.Application;

import com.hein.home.Dao;

import com.hein.entity.User;

import java.util.List;

public class UserRepository {
    public static Dao dao;

    public UserRepository(Application application) {
        UserDatabase database =
                UserDatabase.getInstance(application);
        dao = database.Dao();
    }

    public void insert(User user) {
        dao.insert(user);
    }
    public void update(User user) {
        dao.update(user);
    }
    // creating a method to delete the data in our database.
    public void delete(User user) {
        dao.delete(user);
    }
    public List<User> getAll() { return dao.getAll(); }
}
