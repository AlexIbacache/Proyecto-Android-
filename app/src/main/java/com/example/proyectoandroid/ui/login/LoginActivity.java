package com.example.proyectoandroid.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.main.MainActivity;
import com.example.proyectoandroid.ui.register.RegistrarFormActivity;
import com.example.proyectoandroid.util.Result;
import com.example.proyectoandroid.util.Validators;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistrar;
    private SignInButton btnGoogleSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            goToMainActivity();
            return;
        }

        setContentView(R.layout.activity_main);

        setupViews();
        setupViewModel();
        setupGoogleSignIn();
        setupClickListeners();
        setupObservers();
    }

    private void setupViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistrar = findViewById(R.id.tvRegistrar);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupViewModel() {
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null && account.getIdToken() != null) {
                                loginViewModel.handleGoogleSignInResult(account.getIdToken())
                                        .observe(this, getLoginObserver());
                            } else {
                                showLoading(false);
                                Toast.makeText(this, "No se pudo obtener el token de Google.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (ApiException e) {
                            showLoading(false);
                            Log.w(TAG, "Google sign in failed", e);
                            Toast.makeText(this, "Falló el inicio de sesión con Google.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showLoading(false);
                    }
                });
    }

    private Observer<Result<FirebaseUser>> getLoginObserver() {
        return result -> {
            showLoading(false);
            if (result instanceof Result.Success) {
                FirebaseUser user = ((Result.Success<FirebaseUser>) result).data;
                Toast.makeText(LoginActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else if (result instanceof Result.Error) {
                Exception exception = ((Result.Error) result).exception;
                Log.w(TAG, "Authentication failed", exception);
                if (exception instanceof FirebaseAuthInvalidUserException) {
                    etEmail.setError("Esta cuenta de correo no está registrada.");
                    etEmail.requestFocus();
                } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                    etPassword.setError("La contraseña es incorrecta.");
                    etPassword.requestFocus();
                } else {
                    Toast.makeText(LoginActivity.this, "La autenticación falló. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (validateInput(email, password)) {
                showLoading(true);
                loginViewModel.login(email, password).observe(this, getLoginObserver());
            }
        });

        btnGoogleSignIn.setOnClickListener(v -> {
            showLoading(true);
            loginViewModel.initiateGoogleSignIn();
        });

        tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrarFormActivity.class);
            startActivity(intent);
        });
    }

    private void setupObservers() {
        loginViewModel.googleSignInEvent.observe(this, v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private boolean validateInput(String email, String password) {
        if (!Validators.isEmailValid(email)) {
            etEmail.setError("Ingresa un correo válido");
            etEmail.requestFocus();
            return false;
        }
        if (!Validators.isPasswordValid(password)) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres.");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        btnGoogleSignIn.setEnabled(!isLoading);
    }
}
