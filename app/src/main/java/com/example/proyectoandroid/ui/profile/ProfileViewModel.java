package com.example.proyectoandroid.ui.profile;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.data.AuthRepository;
import com.example.proyectoandroid.data.FirebaseAuthRepository;
import com.example.proyectoandroid.util.SingleLiveEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileViewModel";
    private final AuthRepository authRepository;
    private final MutableLiveData<String> _userEmail = new MutableLiveData<>();
    public LiveData<String> getUserEmail() { return _userEmail; }
    
    private final MutableLiveData<String> _userName = new MutableLiveData<>();
    public LiveData<String> getUserName() { return _userName; }

    private final MutableLiveData<String> _location = new MutableLiveData<>();
    public LiveData<String> getLocation() { return _location; }

    private final SingleLiveEvent<Void> _logoutEvent = new SingleLiveEvent<>();
    public LiveData<Void> getLogoutEvent() { return _logoutEvent; }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new FirebaseAuthRepository();
        Log.d(TAG, "ViewModel inicializado");
    }

    public void loadUserData() {
        Log.d(TAG, "loadUserData llamado");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "Usuario encontrado: " + user.getEmail());
            _userEmail.setValue(user.getEmail());
            _userName.setValue(user.getDisplayName());
        } else {
            Log.w(TAG, "Ningún usuario ha iniciado sesión");
            _userEmail.setValue("Usuario no logueado");
            _userName.setValue("N/A");
        }
    }

    public void onLocationReceived(Location receivedLocation) {
        if (receivedLocation != null) {
            Log.d(TAG, "Ubicación recibida: " + receivedLocation.getLatitude() + "," + receivedLocation.getLongitude());
            obtenerNombreUbicacion(receivedLocation);
        } else {
            Log.w(TAG, "onLocationReceived con ubicación nula");
            _location.setValue("Ubicación no disponible");
        }
    }

    public void onLocationFailed(String errorMessage) {
        Log.e(TAG, "Falló la obtención de ubicación: " + errorMessage);
        _location.setValue(errorMessage);
    }

    private void obtenerNombreUbicacion(Location location) {
        Log.d(TAG, "obtenerNombreUbicacion llamado");
        Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), new Locale("es", "CL"));
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                String nombre = a.getSubLocality();
                if (nombre == null) nombre = a.getLocality();
                if (nombre == null) nombre = a.getSubAdminArea();
                if (nombre == null) nombre = a.getAdminArea();
                Log.d(TAG, "Nombre de ubicación geocodificado: " + nombre);
                _location.setValue(nombre != null ? nombre : "Nombre de ubicación no encontrado");
            } else {
                Log.w(TAG, "No se encontró dirección por geocodificador");
                _location.setValue("Dirección no encontrada");
            }
        } catch (IOException e) {
            Log.e(TAG, "Falló el geocodificador", e);
            _location.setValue("Error de geocodificación");
        }
    }

    public void logout() {
        Log.d(TAG, "logout llamado");
        authRepository.logout();
        _logoutEvent.call();
    }
}
