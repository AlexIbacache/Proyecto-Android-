package com.example.proyectoandroid.data;

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

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public LiveData<Result<FirebaseUser>> login(String email, String password) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        liveData.setValue(new Result.Success<>(firebaseAuth.getCurrentUser()));
                    } else {
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public LiveData<Result<FirebaseUser>> loginWithGoogle(String idToken) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        liveData.setValue(new Result.Success<>(firebaseAuth.getCurrentUser()));
                    } else {
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public LiveData<Result<FirebaseUser>> register(String name, String email, String password) {
        MutableLiveData<Result<FirebaseUser>> liveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Save user data to Firestore
                                            String userId = user.getUid();
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("email", user.getEmail());
                                            userData.put("nombre", name);

                                            db.collection("users").document(userId)
                                                    .set(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        liveData.setValue(new Result.Success<>(user));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        liveData.setValue(new Result.Error<>(e));
                                                    });
                                        } else {
                                            liveData.setValue(new Result.Error<>(profileTask.getException()));
                                        }
                                    });
                        } else {
                            liveData.setValue(new Result.Error<>(new Exception("User is null after registration")));
                        }
                    } else {
                        liveData.setValue(new Result.Error<>(task.getException()));
                    }
                });
        return liveData;
    }

    @Override
    public void logout() {
        firebaseAuth.signOut();
    }
}
