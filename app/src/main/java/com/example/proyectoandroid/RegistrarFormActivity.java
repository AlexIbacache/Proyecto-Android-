package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrarFormActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarFormActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextInputEditText etNombre;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnRegistrarU;
    private TextView tvIniciarSesion;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_form);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegistrarU = findViewById(R.id.btnRegistrarU);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);
        progressBar = findViewById(R.id.progressBar);

        btnRegistrarU.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInput(nombre, email, password, confirmPassword)) {
                showLoading(true);
                crearUsuario(nombre, email, password);
            }
        });

        tvIniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrarFormActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean validateInput(String nombre, String email, String password, String confirmPassword) {
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            etNombre.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("El correo es obligatorio");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Por favor, ingresa un correo electrónico válido");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");
            etPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("Confirma la contraseña");
            etConfirmPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void crearUsuario(String nombre, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(nombre)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                            crearDocumentoUsuarioEnFirestore(user, nombre);
                                        }
                                    });
                        }
                    } else {
                        showLoading(false);
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            etPassword.setError("La contraseña es muy débil.");
                            etPassword.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            etEmail.setError("Este correo ya está en uso.");
                            etEmail.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(RegistrarFormActivity.this, "El registro falló.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void crearDocumentoUsuarioEnFirestore(FirebaseUser firebaseUser, String nombre) {
        String userId = firebaseUser.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", firebaseUser.getEmail());
        userData.put("nombre", nombre);

        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // No es necesario ocultar el loading aquí, porque la actividad será destruida.
                    Log.d(TAG, "Documento de usuario creado con éxito");
                    Toast.makeText(RegistrarFormActivity.this, "Bienvenido.", Toast.LENGTH_SHORT).show();
                    firebaseUser.sendEmailVerification();
                    Intent intent = new Intent(RegistrarFormActivity.this, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.w(TAG, "Error al escribir documento de usuario", e);
                    Toast.makeText(RegistrarFormActivity.this, "Error al guardar perfil de usuario.", Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegistrarU.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegistrarU.setEnabled(true);
        }
    }
}
