package com.example.proyectoandroid.data;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.util.Result;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseAuthRepository implements AuthRepository {

    private static final String TAG = "FirebaseAuthRepo";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public LiveData<Result<FirebaseUser>> login(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        Log.d(TAG, "Intentando iniciar sesión con el correo: " + email);
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Inicio de sesión exitoso para el correo: " + email);
                        liveData.setValue(new Result.Success<>(firebaseAuth.getCurrentUser()));
                    } else {
                        Log.e(TAG, "Falló el inicio de sesión para el correo: " + email, task.getException());
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public LiveData<Result<FirebaseUser>> loginWithGoogle(String idToken) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        Log.d(TAG, "Intentando iniciar sesión con Google");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Inicio de sesión con Google exitoso");
                        liveData.setValue(new Result.Success<>(firebaseAuth.getCurrentUser()));
                    } else {
                        Log.e(TAG, "Falló el inicio de sesión con Google", task.getException());
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public LiveData<Result<FirebaseUser>> register(String name, String email, String password) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        Log.d(TAG, "Intentando registrar un nuevo usuario con el correo: " + email);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            Log.d(TAG, "Creación de usuario exitosa, actualizando perfil para: " + name);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            Log.d(TAG, "Actualización de perfil exitosa. Guardando usuario en Firestore.");
                                            String userId = user.getUid();
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("email", user.getEmail());
                                            userData.put("nombre", name);

                                            db.collection("users").document(userId)
                                                    .set(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Log.d(TAG, "Datos del usuario guardados en Firestore exitosamente.");
                                                        liveData.setValue(new Result.Success<>(user));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e(TAG, "Error al guardar los datos del usuario en Firestore", e);
                                                        liveData.setValue(new Result.Error<>(e));
                                                    });
                                        } else {
                                            Log.e(TAG, "Falló la actualización del perfil", profileTask.getException());
                                            liveData.setValue(new Result.Error<>(profileTask.getException()));
                                        }
                                    });
                        } else {
                            Exception e = new Exception("El usuario es nulo después del registro");
                            Log.e(TAG, "Falló el registro", e);
                            liveData.setValue(new Result.Error<>(e));
                        }
                    } else {
                        Log.e(TAG, "Falló la creación del usuario", task.getException());
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public void logout() {
        Log.d(TAG, "Cerrando sesión del usuario");
        firebaseAuth.signOut();
    }
}
