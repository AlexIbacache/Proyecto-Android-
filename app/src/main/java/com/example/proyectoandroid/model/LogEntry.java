package com.example.proyectoandroid.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de datos para representar una entrada de log en el sistema.
 * Cada log registra una acción realizada por un usuario en la aplicación.
 */
public class LogEntry {

    @DocumentId
    private String documentId;
    private String userId;
    private String userEmail;
    private String userName;
    private String actionType;
    private String actionDescription;
    private String entityType;
    private String entityId;
    private Timestamp timestamp;
    private Map<String, Object> additionalData;

    // Constructor vacío requerido para Firestore
    public LogEntry() {
        this.additionalData = new HashMap<>();
    }

    public LogEntry(String userId, String userEmail, String userName, String actionType,
            String actionDescription, String entityType, String entityId) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.actionType = actionType;
        this.actionDescription = actionDescription;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = Timestamp.now();
        this.additionalData = new HashMap<>();
    }

    // Getters y Setters
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public void addAdditionalData(String key, Object value) {
        if (this.additionalData == null) {
            this.additionalData = new HashMap<>();
        }
        this.additionalData.put(key, value);
    }
}
