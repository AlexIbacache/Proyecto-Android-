package com.example.proyectoandroid.ui.profile;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

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

    private final AuthRepository authRepository;
    private final MutableLiveData<String> _userEmail = new MutableLiveData<>();
    public LiveData<String> getUserEmail() { return _userEmail; }

    private final MutableLiveData<String> _location = new MutableLiveData<>();
    public LiveData<String> getLocation() { return _location; }

    private final SingleLiveEvent<Void> _logoutEvent = new SingleLiveEvent<>();
    public LiveData<Void> getLogoutEvent() { return _logoutEvent; }

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new FirebaseAuthRepository();
    }

    public void loadUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            _userEmail.setValue(user.getEmail());
        } else {
            _userEmail.setValue("Usuario no logueado");
        }
    }

    public void onLocationReceived(Location receivedLocation) {
        if (receivedLocation != null) {
            obtenerNombreUbicacion(receivedLocation);
        } else {
            _location.setValue("Ubicación no disponible");
        }
    }

    public void onLocationFailed(String errorMessage) {
        _location.setValue(errorMessage);
    }

    private void obtenerNombreUbicacion(Location location) {
        Geocoder geocoder = new Geocoder(getApplication().getApplicationContext(), new Locale("es", "CL"));
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                String nombre = a.getSubLocality();
                if (nombre == null) nombre = a.getLocality();
                if (nombre == null) nombre = a.getSubAdminArea();
                if (nombre == null) nombre = a.getAdminArea();
                _location.setValue(nombre != null ? nombre : "Nombre de ubicación no encontrado");
            } else {
                _location.setValue("Dirección no encontrada");
            }
        } catch (IOException e) {
            _location.setValue("Error de geocodificación");
        }
    }

    public void logout() {
        authRepository.logout();
        _logoutEvent.call();
    }
}
