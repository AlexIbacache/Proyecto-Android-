package com.example.proyectoandroid.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.login.LoginActivity;
import com.example.proyectoandroid.ui.main.MainActivity;
import com.example.proyectoandroid.util.Result;
import com.example.proyectoandroid.util.Validators;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistrarFormActivity extends AppCompatActivity {

    private static final String TAG = "RegistrarFormActivity";

    private RegisterViewModel registerViewModel;

    private TextInputEditText etNombre, etEmail, etPassword, etConfirmPassword;
    private Button btnRegistrarU;
    private TextView tvIniciarSesion;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_form);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegistrarU = findViewById(R.id.btnRegistrarU);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);
        progressBar = findViewById(R.id.progressBar);

        registerViewModel.registerResult.observe(this, result -> {
            showLoading(false);
            if (result instanceof Result.Success) {
                Toast.makeText(RegistrarFormActivity.this, "Bienvenido " + ((Result.Success<com.google.firebase.auth.FirebaseUser>) result).data.getEmail(), Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else if (result instanceof Result.Error) {
                Exception exception = ((Result.Error) result).exception;
                Log.w(TAG, "createUserWithEmail:failure", exception);
                if (exception instanceof FirebaseAuthWeakPasswordException) {
                    etPassword.setError("La contraseña es muy débil.");
                    etPassword.requestFocus();
                } else if (exception instanceof FirebaseAuthUserCollisionException) {
                    etEmail.setError("Este correo ya está en uso.");
                    etEmail.requestFocus();
                } else {
                    Toast.makeText(RegistrarFormActivity.this, "El registro falló.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegistrarU.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (validateInput(nombre, email, password, confirmPassword)) {
                showLoading(true);
                registerViewModel.register(nombre, email, password);
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
        if (!Validators.isEmailValid(email)) {
            etEmail.setError("Por favor, ingresa un correo electrónico válido");
            etEmail.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(RegistrarFormActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegistrarU.setEnabled(!isLoading);
    }
}
