# SparePartsM - Gestión de Repuestos de Maquinaria 📌

SparePartsM es una aplicación Android diseñada para talleres y operadores de maquinaria, permitiendo un registro y gestión eficientes de los repuestos utilizados en las reparaciones. El objetivo es centralizar y digitalizar esta información crítica, utilizando Firebase como backend para la persistencia de datos en tiempo real.

---

## 💾 Estructura de la Base de Datos (Cloud Firestore) 
La base de datos utiliza un modelo NoSQL jerárquico centrado en dos colecciones raíz principales: `catalogoRepuestos` para información global y `users` para datos específicos de cada usuario, incluyendo su maquinaria y reparaciones.

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
    * **Subcolección:** `maquinaria/`

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
    * **Subcolección:** `reparaciones/`

* **`users/{user_uid_X}/maquinaria/{maquina_auto_id_X}/reparaciones/`** ➡️ **Historial de Reparaciones**
    * **Documento ID:** `{reparacion_auto_id_X}` (ID autogenerado).
    * **Campos:**
        * `fecha`: `Timestamp`.
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

## 🏗️ Arquitectura: De Monolítica a MVVM

El proyecto ha sido sometido a una refactorización completa para pasar de una estructura de código monolítica a una arquitectura moderna y escalable **MVVM (Model-View-ViewModel)**.

### Estado Anterior
Inicialmente, la lógica de negocio, el manejo de datos y las interacciones con la UI estaban fuertemente acoplados dentro de las Activities y Fragments, dificultando el mantenimiento y la escalabilidad.

### Transformación a MVVM
La nueva arquitectura separa claramente las responsabilidades, haciendo la app más **robusta**, **testeable** y fácil de entender.

* **View (Vista):** Compuesta por Activities y Fragments. Su única responsabilidad es dibujar la UI y notificar al ViewModel de las interacciones del usuario (clics, texto ingresado, etc.).
* **ViewModel:** Contiene toda la lógica de la UI y el estado. Se comunica con el Repository para obtener y guardar datos. Sobrevive a cambios de configuración (como rotación de pantalla), evitando la pérdida de datos.
* **Model (Modelo):** Representado por el Repository. Es la única fuente de verdad para los datos de la aplicación. Se encarga de decidir si obtiene los datos de una fuente remota (Firestore) o una local (en el futuro).

---

## 📁 Estructura de Carpetas Actual

El código fuente ahora está organizado en paquetes según su responsabilidad:

app/src/main/java/com/example/proyectoandroid/ | |-- data/         # Repositorios (AuthRepository, MaquinariaRepository) |-- model/        # Clases de datos o POJOs (Maquinaria.java) |-- ui/           # Componentes de la UI (Vistas y ViewModels) |   |-- login/      # --- LoginActivity, LoginViewModel |   |-- main/       # --- MainActivity |   |-- maquinaria/ # --- MaquinariaFragment, MaquinariaFormFragment, etc. |   |-- profile/    # --- ProfileFragment, ProfileViewModel |   |-- register/   # --- RegistrarFormActivity, RegisterViewModel |   |-- reparacion/ # --- ReparacionFragment, ReparacionViewModel |   |-- reportes/   # --- ReportesFragment, ReportesViewModel |   -- ... | -- util/         # Clases de utilidad (Result.java, SingleLiveEvent.java)


---

## 🚀 Instalación y Configuración

> **¡LEER CON ATENCIÓN!**
> La conexión con Firebase es sensible a la configuración. Sigue estos pasos para garantizar que la aplicación se ejecute sin errores de conexión.

1.  **Clonar el Repositorio:**
    ```bash
    git clone [https://github.com/AlexIbacache/Proyecto-Android-](https://github.com/AlexIbacache/Proyecto-Android-)
    ```

2.  **Crear un Nuevo Proyecto en Firebase:**
    * Ve a la [Consola de Firebase](https://console.firebase.google.com/) y crea un proyecto nuevo.
    * Dentro del proyecto, añade una nueva aplicación de Android. Asegúrate de que el nombre del paquete sea exactamente `com.example.proyectoandroid`.

3.  **Añadir la Huella Digital SHA-1 (Paso CRÍTICO):**
    * Abre una terminal en Android Studio (`View` -> `Tool Windows` -> `Terminal`).
    * Ejecuta el comando para generar el informe de firmas:
        ```bash
        .\gradlew signingReport
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

6.  **Sincronizar y Ejecutar:**
    * En Android Studio, haz clic en `Sync Project with Gradle Files`.
    * Se recomienda limpiar el proyecto (`Build` -> `Clean Project`) y reconstruirlo (`Build` -> `Rebuild Project`).
    * Ejecuta la app en un emulador o dispositivo físico. Si usas un emulador, asegúrate de que tenga los Google Play Services instalados y conexión a Internet.

---

## 🛠️ Componentes Clave Utilizados

* **Arquitectura:** ViewModel y LiveData para implementar el patrón MVVM.
* **UI:** Fragments para modularizar las pantallas, RecyclerView para listas eficientes y Material Components para el diseño.
* **Datos:**
    * **Firebase Authentication:** Para el login con Email/Contraseña y Google.
    * **Cloud Firestore:** Como base de datos NoSQL en tiempo real.
    * **Google Sign-In Services:** Para la integración del login con Google.
* **Exportación:** Apache POI para la generación de archivos Excel.

---

## 🔮 Mejoras Futuras

La arquitectura actual sienta las bases para futuras mejoras:

* **Navegación:** Implementar Jetpack Navigation Component para gestionar el flujo entre Fragments de forma más visual y segura.
* **Asincronía:** Migrar las llamadas a Firebase para usar Kotlin Coroutines, simplificando el código asíncrono.
* **Soporte Offline:** Integrar Room como base de datos local para permitir que la app funcione sin conexión y se sincronice con Firestore cuando vuelva a tener red.
* **Inyección de Dependencias:** Introducir Hilt para gestionar las dependencias, facilitando las pruebas y el mantenimiento a largo plazo.
```

---
