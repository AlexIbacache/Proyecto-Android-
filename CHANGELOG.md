# Registro de Cambios del Proyecto

Este documento resume las principales configuraciones, modificaciones y mejoras realizadas en el proyecto.

## 1. Configuración del Proyecto y Dependencias

- **Apache POI**: Se añadieron las dependencias de `org.apache.poi:poi` y `org.apache.poi:poi-ooxml` al archivo `app/build.gradle.kts` para habilitar la futura funcionalidad de manejo de archivos Excel (`.xlsx`).
- **Nivel de API Mínimo**: Se actualizó el `minSdk` de `24` a `26` en `app/build.gradle.kts` para resolver un error de compatibilidad (`Dexing`) causado por la librería Apache POI.
- **Firebase Firestore**: Se añadió la dependencia `com.google.firebase:firebase-firestore` para permitir la interacción con la base de datos Cloud Firestore.

## 2. Base de Datos y Seguridad (Cloud Firestore)

- **Estructura de Datos**: Se diseñó y documentó una estructura de datos escalable para Cloud Firestore, utilizando colecciones (`catalogoRepuestos`, `users`) y sub-colecciones (`maquinaria`, `reparaciones`) para organizar la información de forma lógica.
- **Reglas de Seguridad**: Se implementaron reglas de seguridad en `firestore.rules` para proteger la base de datos. La regla principal asegura que un usuario solo puede leer y escribir sus propios datos (`allow read, write: if request.auth.uid == userId;`).

## 3. Flujo de Autenticación de Usuarios

Se implementó un flujo de autenticación completo y seguro.

### Pantalla de Registro (`RegistrarFormActivity`)

- **Creación de Usuario**: Se implementó la lógica para registrar nuevos usuarios usando `createUserWithEmailAndPassword`.
- **Sincronización con Firestore**: Tras un registro exitoso, se crea automáticamente un documento para el nuevo usuario en la colección `users` de Firestore, guardando su `email`.
- **Experiencia de Usuario (UX)**:
    - Después de registrarse, el usuario es redirigido directamente al `MenuActivity`, ya que Firebase lo autentica automáticamente.
    - Se añadió un `ProgressBar` que se muestra al presionar "Registrarte", deshabilitando el botón para evitar clics múltiples.
    - Se corrigió la posición del `ProgressBar` para que no obstruya otros elementos visuales.
- **Seguridad y Validación**:
    - Se añadió validación en el lado del cliente para el formato del email (`Patterns.EMAIL_ADDRESS`) y la longitud de la contraseña (mínimo 6 caracteres).
    - Se implementó un campo para **confirmar la contraseña** y la lógica para verificar que ambas contraseñas coincidan.
    - Se añadió la funcionalidad de **mostrar/ocultar contraseña** en ambos campos de contraseña mediante `TextInputLayout`.
    - El manejo de errores ahora es más seguro: los errores técnicos se imprimen en **Logcat** para depuración, mientras que al usuario se le muestran mensajes claros y amigables (ej. "Este correo ya está en uso").

### Pantalla de Inicio de Sesión (`LoginActivity`)

- **Experiencia de Usuario (UX)**:
    - Se añadió un `ProgressBar` similar al de la pantalla de registro para indicar que el proceso de inicio de sesión está en curso.
    - Se añadió la funcionalidad de **mostrar/ocultar contraseña**.
- **Seguridad y Manejo de Errores**:
    - Se mejoró el manejo de errores para que, en lugar de mostrar mensajes técnicos, se muestren errores específicos en los campos de texto (ej. "Esta cuenta de correo no está registrada" o "La contraseña es incorrecta").
    - Los detalles de los errores se imprimen en **Logcat** para facilitar la depuración.

## 4. Funcionalidad Futura ("Promesas")

Se realizó una revisión de varias actividades para identificar botones sin funcionalidad. Se les añadió un `Toast` con el mensaje **"Función próximamente..."** para mejorar la claridad sobre el estado del desarrollo.

- **`AgregarRepuestoDialog`**: Botón "Aceptar".
- **`MaquinariaFormActivity`**: Botón "Guardar".
- **`VerReportes`**: Botón "Exportar a Excel".
