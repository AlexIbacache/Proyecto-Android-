package com.example.proyectoandroid.data;

import androidx.lifecycle.LiveData;
import com.example.proyectoandroid.util.Result;
import com.google.firebase.auth.FirebaseUser;

public interface AuthRepository {
    LiveData<Result<FirebaseUser>> login(String email, String password);
    LiveData<Result<FirebaseUser>> loginWithGoogle(String idToken);
    LiveData<Result<FirebaseUser>> register(String name, String email, String password);
    void logout();
}
