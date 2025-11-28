# SparePartsM - Gestión de Repuestos de Maquinaria 📌

SparePartsM es una aplicación Android diseñada para talleres y operadores de maquinaria, permitiendo un registro y gestión eficientes de los repuestos utilizados en las reparaciones. El objetivo es centralizar y digitalizar esta información crítica, utilizando Firebase como backend para la persistencia de datos en tiempo real.

---

## 💾 Estructura de la Base de Datos (Cloud Firestore) 
La base de datos utiliza un modelo NoSQL jerárquico centrado en tres colecciones raíz principales: `catalogoRepuestos` para información global, `users` para datos operativos, y `historial-logs` para auditoría.

### Colecciones Raíz

* **`catalogoRepuestos/`** ➡️ **Catálogo Global de Repuestos**
    * **Documento ID:** `{repuesto_auto_id_X}` (ID autogenerado).
    * **Campos:**
        * `nombre`: `string` (Ej: "Placa Fijacion").
        * `codigoNParte`: `string` (Ej: "1008290614").

* **`users/`** ➡️ **Usuarios de la Aplicación**
    * **Documento ID:** `{user_uid_X}` (ID de usuario de Firebase Authentication).
    * **Campos:**
        * `email`: `string` (Ej: "usuario1@email.com").
        * `role`: `string` (Opcional, Ej: "admin").
    * **Subcolección:** `maquinaria/`

* **`historial-logs/`** ➡️ **Registro de Auditoría y Actividad**
    * **Documento ID:** `{log_auto_id_X}`.
    * **Campos:**
        * `actionType`: `string` (Ej: "CREATE", "UPDATE", "LOGIN").
        * `actionDescription`: `string` (Ej: "Creó maquinaria: Alpha 30").
        * `entityType`: `string` (Ej: "MAQUINARIA", "REPARACION").
        * `entityId`: `string`.
        * `userId`: `string` (UID del usuario que realizó la acción).
        * `userEmail`: `string`.
        * `timestamp`: `Timestamp`.

### Estructura de Maquinaria y Reparaciones

La gestión operativa se anida dentro de la subcolección `maquinaria/` de cada usuario:

* **`users/{user_uid_X}/maquinaria/`** ➡️ **Maquinaria Registrada por el Usuario**
    * **Documento ID:** `{maquina_auto_id_X}` (ID autogenerado).
    * **Campos:**
        * `nombre`: `string` (Ej: "Alpha 30").
        * `numeroIdentificador`: `string` (Ej: "EQ: 163").
        * `fechaIngreso`: `Timestamp`.
        * `descripcion`: `string` (Ej: "Falla en el sistema hidráulico.").
        * `partesPrincipales`: `Array` de `string` (Ej: ["BRAZO BB-2027", "BLOCK BOMBEO"]).
        * `estado`: `boolean` (`falso` = en reparación/inactiva; `true` = operativa).
        * `imagenUrl`: `string` (URL de la imagen en Firebase Storage).
    * **Subcolección:** `reparaciones/`

* **`users/{user_uid_X}/maquinaria/{maquina_auto_id_X}/reparaciones/`** ➡️ **Historial de Reparaciones**
    * **Documento ID:** `{reparacion_auto_id_X}` (ID autogenerado).
    * **Campos:**
        * `fecha`: `Timestamp`.
        * `estado`: `string` ("Abierta" o "Cerrada").
        * `notas`: `string` (Ej: "Se encontraron pernos sueltos...").
        * `repuestosUsados`: `Array` de **Mapas (Objetos)**.

#### Detalle de `repuestosUsados` (Array de Mapas)

Cada elemento dentro del array `repuestosUsados` es un objeto que detalla el repuesto utilizado, manteniendo una referencia al catálogo principal:

| Campo | Tipo | Ejemplo | Descripción |
| :--- | :--- | :--- | :--- |
| `repuestoRef` | `Reference` | `catalogoRepuestos/repuesto_auto_id_1` | Referencia directa al documento en la colección raíz `catalogoRepuestos`. |
| `nombreRepuesto` | `string` | "Placa Fijacion" | Nombre del repuesto (copia desnormalizada para visualización rápida). |
| `cantidad` | `number` | 2 | Cantidad de unidades utilizadas. |

---

## 🛡️ Roles y Permisos

El sistema implementa un control de acceso basado en roles (RBAC) simple:

1.  **Usuario Normal (Estándar):**
    *   Acceso completo a la gestión de **su propia** maquinaria y reparaciones.
    *   Capacidad de generar reportes Excel.
    *   Visualización de estadísticas personales en el perfil.
    *   Navegación estándar: Perfil, Maquinaria, Reparación, Reportes.

2.  **Administrador:**
    *   **Panel Exclusivo:** Al iniciar sesión, se redirige a un panel de administración dedicado (`AdminFragment`).
    *   **Gestión de Logs:** Capacidad para visualizar estadísticas del sistema (total de logs, log más antiguo).
    *   **Mantenimiento:** Herramientas para depurar la base de datos eliminando logs antiguos (30, 60, 90 días) o purgar todo el historial.
    *   **Restricción:** No tiene acceso a la navegación operativa estándar desde el panel admin.

---

## 📊 Estadísticas y Visualización

### Perfil de Usuario Premium
El perfil del usuario ha sido mejorado para funcionar como un dashboard personal:
*   **Geolocalización:** Muestra la ubicación actual del usuario mediante Google Location Services.
*   **Actividad Reciente:** Visualización gráfica animada de:
    *   Maquinaria Creada (Barra de progreso verde).
    *   Reparaciones en Curso (Barra azul - Datos en tiempo real).
    *   Reparaciones Finalizadas (Barra verde - Datos en tiempo real).
    *   Reportes Generados (Barra naranja).

### Panel de Administración
Dashboard técnico para el mantenimiento del sistema:
*   Contadores en tiempo real de la actividad del sistema.
*   Herramientas de limpieza de base de datos con confirmaciones de seguridad.

---

## 🏗️ Arquitectura: De Monolítica a MVVM

El proyecto ha sido sometido a una refactorización completa para pasar de una estructura de código monolítica a una arquitectura moderna y escalable **MVVM (Model-View-ViewModel)**.

### Componentes Principales

*   **View (Vista):** `MainActivity`, `ProfileFragment`, `AdminFragment`, `MaquinariaFragment`, etc. Responsables solo de la UI y animaciones.
*   **ViewModel:** `ProfileViewModel`, `MaquinariaViewModel`, etc. Gestionan el estado de la UI y sobreviven a cambios de configuración.
*   **Model (Repository):** 
    *   `MaquinariaRepository`: Lógica de negocio para maquinaria y reparaciones.
    *   `LoggerRepository`: Gestión centralizada de logs y auditoría.
*   **Utils:**
    *   `UserActionLogger`: Singleton para registrar acciones desde cualquier punto de la app.
    *   `AdminHelper`: Utilidad para verificación de roles.

---

## 🧩 Patrones de Diseño Implementados

*   **MVVM (Model-View-ViewModel):** Arquitectura base del proyecto.
*   **Repository Pattern:** Abstracción de la fuente de datos (Firestore).
*   **Singleton Pattern:** Utilizado en `UserActionLogger` y `FirebaseFirestore.getInstance()`.
*   **Observer Pattern:** Implementado a través de `LiveData` y callbacks personalizados (`ReparacionStatsCallback`) para actualizaciones reactivas de la UI.
*   **Adapter Pattern:** `MaquinariaAdapter`, `ReparacionAdapter` para vincular datos con RecyclerViews.
*   **ViewHolder Pattern:** Optimización de vistas en listas.

---

## � Estructura del Proyecto

El código fuente está organizado en paquetes siguiendo la separación de responsabilidades:

```
com.example.proyectoandroid
├── 📂 adapters       # Adaptadores para RecyclerViews (Maquinaria, Reparaciones, Repuestos)
├── 📂 data           # Repositorios (MaquinariaRepository, LoggerRepository)
├── 📂 model          # Clases de Modelo (POJOs: Maquinaria, Reparacion, LogEntry, User)
├── 📂 ui             # Vistas y ViewModels (Fragments y Activities)
│   ├── 📂 admin      # Panel de administración
│   ├── 📂 login      # Pantallas de autenticación
│   ├── 📂 main       # Activity principal y navegación
│   ├── 📂 maquinaria # Listado y gestión de maquinaria
│   ├── 📂 profile    # Perfil de usuario y estadísticas
│   ├── 📂 reparacion # Gestión de reparaciones
│   └── 📂 reportes   # Generación de reportes Excel
└── 📂 util           # Clases utilitarias (UserActionLogger, AdminHelper, ExcelUtils)
```

## 🔐 Permisos Requeridos

La aplicación solicita los siguientes permisos en el `AndroidManifest.xml`:

*   `INTERNET`: Para conectar con Firebase y Google Services.
*   `ACCESS_FINE_LOCATION`: Para obtener la ubicación precisa del usuario en el perfil.
*   `ACCESS_COARSE_LOCATION`: Ubicación aproximada (respaldo).
*   `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE`: (Dependiendo de la versión de Android) Para guardar reportes Excel y seleccionar imágenes.

---

## �🚀 Instalación y Configuración

> **¡LEER CON ATENCIÓN!**
> La conexión con Firebase es sensible a la configuración. Sigue estos pasos para garantizar que la aplicación se ejecute sin errores de conexión.

1.  **Clonar el Repositorio:**
    ```bash
    git clone https://github.com/AlexIbacache/Proyecto-Android-
    ```

2.  **Crear un Nuevo Proyecto en Firebase:**
    * Ve a la [Consola de Firebase](https://console.firebase.google.com/) y crea un proyecto nuevo.
    * Dentro del proyecto, añade una nueva aplicación de Android. Asegúrate de que el nombre del paquete sea exactamente `com.example.proyectoandroid`.

3.  **Añadir la Huella Digital SHA-1 (Paso CRÍTICO):**
    * Abre una terminal en Android Studio (`View` -> `Tool Windows` -> `Terminal`).
    * Ejecuta el comando para generar el informe de firmas:
        ```bash
        ./gradlew signingReport
        ```
    * Busca la variante `debug` y copia la huella digital **SHA-1**.
    * Vuelve a la Consola de Firebase, ve a `Configuración del proyecto` (⚙️) y en la sección "Tus apps", añade la huella digital SHA-1 que acabas de copiar.

4.  **Descargar y Añadir `google-services.json`:**
    * Después de añadir la huella, descarga el archivo `google-services.json` que te proporciona Firebase.
    * En Android Studio, cambia a la vista "**Project**" y coloca este archivo en la carpeta `app/`.

5.  **Habilitar Servicios de Firebase:**
    * En la Consola de Firebase, ve a la sección "Build".
    * Habilita **Authentication** y activa el proveedor de "Correo electrónico y contraseña" y "Google".
    * Habilita **Firestore Database**.
    * Habilita **Storage** (para imágenes de maquinaria).

6.  **Sincronizar y Ejecutar:**
    * En Android Studio, haz clic en `Sync Project with Gradle Files`.
    * Se recomienda limpiar el proyecto (`Build` -> `Clean Project`) y reconstruirlo (`Build` -> `Rebuild Project`).
    * Ejecuta la app en un emulador o dispositivo físico. Si usas un emulador, asegúrate de que tenga los Google Play Services instalados y conexión a Internet.

---

## 🛠️ Tecnologías y Librerías

El proyecto utiliza las siguientes tecnologías y dependencias clave:

### Core
*   **Lenguaje:** Java 17
*   **SDK:** Min SDK 26 (Android 8.0) -> Target SDK 35 (Android 15)
*   **Gradle:** Kotlin DSL (`build.gradle.kts`)

### Backend (Firebase BOM 34.2.0)
*   **Firebase Authentication:** Gestión de usuarios y sesiones.
*   **Cloud Firestore:** Base de datos NoSQL en tiempo real.
*   **Firebase Storage:** Almacenamiento de imágenes de maquinaria.
*   **Firebase Analytics:** Métricas de uso.

### Google Services
*   **Play Services Auth (20.7.0):** Inicio de sesión con Google.
*   **Play Services Location (21.0.1):** Geolocalización para el perfil de usuario.

### UI & Arquitectura
*   **Material Components:** Diseño moderno (Cards, Buttons, Inputs).
*   **AndroidX Lifecycle (2.7.0):** ViewModel y LiveData para MVVM.
*   **ConstraintLayout:** Diseño de interfaces flexibles.
*   **Facebook Shimmer (0.5.0):** Efectos de carga (esqueletos) para mejorar la UX.

### Utilidades
*   **Glide (4.16.0):** Carga y caché eficiente de imágenes.
*   **Apache POI (5.4.1):** Generación profesional de reportes en formato Excel (.xlsx).

---

## 🔮 Mejoras Futuras

*   **Notificaciones Push:** Alertas cuando una reparación cambia de estado.
*   **Modo Offline:** Persistencia local con Room para trabajar sin conexión.
*   **Gráficos Avanzados:** Implementar MPAndroidChart para estadísticas históricas más detalladas.
*   **Chat Técnico:** Comunicación en tiempo real entre administradores y técnicos.

---
*    **Link de Documentación:** https://drive.google.com/drive/folders/1ukdKRbC2Jni99Bzh0Iavmv2h-Ph46o0k?usp=sharing
