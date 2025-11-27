package com.example.proyectoandroid.ui.splash;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Pantalla de splash con animación de herramientas de reparación.
 * Muestra el logo de la aplicación mientras verifica la autenticación del
 * usuario.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 3000; // 3 segundos
    private ImageView gearBackground;
    private ImageView wrenchIcon;
    private TextView appName;
    private TextView appSubtitle;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        initViews();

        // Iniciar animaciones
        startAnimations();

        // Navegar después del tiempo de splash
        new Handler(Looper.getMainLooper()).postDelayed(this::navigateToNextScreen, SPLASH_DURATION);
    }

    /**
     * Inicializa las vistas del splash screen
     */
    private void initViews() {
        gearBackground = findViewById(R.id.gearBackground);
        wrenchIcon = findViewById(R.id.wrenchIcon);
        appName = findViewById(R.id.appName);
        appSubtitle = findViewById(R.id.appSubtitle);
    }

    /**
     * Inicia todas las animaciones del splash screen
     */
    private void startAnimations() {
        // Animación del engranaje (rotación continua)
        Animation gearRotation = AnimationUtils.loadAnimation(this, R.anim.rotate_gear);
        gearBackground.startAnimation(gearRotation);

        // Animación de la llave inglesa (balanceo)
        Animation wrenchSwing = AnimationUtils.loadAnimation(this, R.anim.swing_wrench);
        wrenchIcon.startAnimation(wrenchSwing);

        // Animación del texto (fade in con escala)
        Animation textFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        appName.startAnimation(textFadeIn);

        // Animación del subtítulo con delay
        Animation subtitleFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_scale);
        subtitleFadeIn.setStartOffset(400);
        appSubtitle.startAnimation(subtitleFadeIn);
    }

    /**
     * Navega a la siguiente pantalla según el estado de autenticación
     */
    private void navigateToNextScreen() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent;
        if (currentUser != null) {
            // Usuario ya autenticado, ir a MainActivity
            intent = new Intent(SplashActivity.this,
                    com.example.proyectoandroid.ui.main.MainActivity.class);
        } else {
            // Usuario no autenticado, ir a LoginActivity
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();

        // Transición suave entre activities
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Limpiar animaciones si la activity se pausa
        if (gearBackground != null) {
            gearBackground.clearAnimation();
        }
        if (wrenchIcon != null) {
            wrenchIcon.clearAnimation();
        }
    }
}
