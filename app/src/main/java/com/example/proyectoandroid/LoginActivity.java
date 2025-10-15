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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegistrar;
    private SignInButton btnGoogleSignIn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegistrar = findViewById(R.id.tvRegistrar);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        progressBar = findViewById(R.id.progressBar);

        tvRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrarFormActivity.class);
            startActivity(intent);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (validateInput(email, password)) {
                showLoading(true);
                loginWithEmailPassword(email, password);
            }
        });

        btnGoogleSignIn.setOnClickListener(v -> {
            showLoading(true);
            signInWithGoogle();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToMainActivity();
        }
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            etEmail.setError("Ingresa un correo");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingresa un correo válido");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Ingresa una contraseña");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(LoginActivity.this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            etEmail.setError("Esta cuenta de correo no está registrada.");
                            etEmail.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            etPassword.setError("La contraseña es incorrecta.");
                            etPassword.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "La autenticación falló.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                showLoading(false);
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Error en Google Sign-In.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showLoading(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Bienvenido " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    } else {
                        Toast.makeText(this, "Error en autenticación con Google.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            btnGoogleSignIn.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            btnGoogleSignIn.setEnabled(true);
        }
    }
}
