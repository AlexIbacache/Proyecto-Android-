package com.example.proyectoandroid.ui.register;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.AuthRepository;
import com.example.proyectoandroid.data.FirebaseAuthRepository;
import com.example.proyectoandroid.util.Result;
import com.google.firebase.auth.FirebaseUser;

public class RegisterViewModel extends ViewModel {

    private static final String TAG = "RegisterViewModel";
    private final AuthRepository authRepository;
    private final MutableLiveData<Result<FirebaseUser>> _registerResult = new MutableLiveData<>();
    public LiveData<Result<FirebaseUser>> registerResult = _registerResult;

    public RegisterViewModel() {
        this.authRepository = new FirebaseAuthRepository();
        Log.d(TAG, "ViewModel inicializado");
    }

    public void register(String name, String email, String password) {
        Log.d(TAG, "register() llamado para el correo: " + email);
        authRepository.register(name, email, password).observeForever(result -> {
            if (result instanceof Result.Success) {
                Log.d(TAG, "Registro exitoso para el correo: " + email);
            } else if (result instanceof Result.Error) {
                Log.e(TAG, "Falló el registro para el correo: " + email, ((Result.Error<FirebaseUser>) result).exception);
            }
            _registerResult.setValue(result);
        });
    }
}
