package com.example.proyectoandroid.ui.login;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.AuthRepository;
import com.example.proyectoandroid.data.FirebaseAuthRepository;
import com.example.proyectoandroid.util.Result;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {

    private static final String TAG = "LoginViewModel";
    private final AuthRepository authRepository;

    private final MutableLiveData<Void> _googleSignInEvent = new MutableLiveData<>();
    public LiveData<Void> googleSignInEvent = _googleSignInEvent;

    public LoginViewModel() {
        this.authRepository = new FirebaseAuthRepository();
    }

    public LiveData<Result<FirebaseUser>> login(String email, String password) {
        Log.d(TAG, "login() llamado para el correo: " + email);
        // Devuelve el LiveData directamente del repositorio.
        return authRepository.login(email, password);
    }

    public void initiateGoogleSignIn() {
        Log.d(TAG, "initiateGoogleSignIn() llamado");
        _googleSignInEvent.setValue(null);
    }

    public LiveData<Result<FirebaseUser>> handleGoogleSignInResult(String idToken) {
        Log.d(TAG, "handleGoogleSignInResult() llamado");
        // Devuelve el LiveData directamente del repositorio.
        return authRepository.loginWithGoogle(idToken);
    }
}
