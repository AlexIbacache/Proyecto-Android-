package com.example.proyectoandroid.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.AuthRepository;
import com.example.proyectoandroid.data.FirebaseAuthRepository;
import com.example.proyectoandroid.util.Result;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Result<FirebaseUser>> _registerResult = new MutableLiveData<>();
    public LiveData<Result<FirebaseUser>> registerResult = _registerResult;

    public RegisterViewModel() {
        this.authRepository = new FirebaseAuthRepository();
    }

    public void register(String name, String email, String password) {
        authRepository.register(name, email, password).observeForever(result -> {
            _registerResult.setValue(result);
        });
    }
}
