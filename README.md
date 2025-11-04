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

## 🔥 Estructura de Colecciones en Firestore

La base de datos de **Cloud Firestore** está organizada en un modelo **NoSQL jerárquico**, con dos colecciones raíz principales:  
`catalogoRepuestos` (catálogo global de repuestos) y `users` (datos y registros de cada usuario).

---

### 🧩 Colección Raíz: `catalogoRepuestos/`

Contiene todos los repuestos disponibles de forma **global** para todos los usuarios.  
Cada documento representa un repuesto dentro del catálogo.

**Ejemplo de estructura:**
```markdown
catalogoRepuestos/
|
--- {repuesto_auto_id_1} (Documento)
|-- nombre: "Placa Fijacion"
|-- codigoNParte: "1008290614"
|
--- {repuesto_auto_id_2} (Documento)
|-- nombre: "Rodillos"
|-- codigoNParte: "1008215400"
```

**Campos por documento:**
| Campo | Tipo | Ejemplo | Descripción |
|-------|------|----------|--------------|
| `nombre` | `string` | `"Placa Fijacion"` | Nombre del repuesto. |
| `codigoNParte` | `string` | `"1008290614"` | Código único o número de parte del repuesto. |

---

### 👤 Colección Raíz: `users/`

Contiene los datos de cada usuario autenticado mediante **Firebase Authentication**.  
Cada documento dentro de esta colección representa un usuario único, identificado por su **UID**.

**Ejemplo de estructura:**
```markdown
users/
|
--- {user_uid_1} (Documento con el ID del usuario)
|-- email: "usuario1@email.com"
|
|--- maquinaria/ (Subcolección)
|
--- {maquina_auto_id_1} (Documento)
|-- nombre: "Alpha 30"
|-- numeroIdentificador: "EQ: 163"
|-- fechaIngreso: Timestamp (ej. 4 de agosto, 2025)
|-- descripcion: "Falla en el sistema hidráulico."
|-- partesPrincipales: ["BRAZO BB-2027", "BLOCK BOMBEO", ...]
|-- estado: boolean (falso = en reparación / true = operativa)
|
|--- reparaciones/ (Subcolección)
|
--- {reparacion_auto_id_1} (Documento)
|-- fecha: August 5, 2025 at 10:30 AM UTC-5 (Timestamp)
|-- notas: "Se encontraron pernos sueltos..." (string)
|-- repuestosUsados: (Array de Mapas)
|
|-- 0:
| |-- repuestoRef: "catalogoRepuestos/repuesto_auto_id_1" (Reference)
| |-- nombreRepuesto: "Placa Fijacion" (string)
| |-- cantidad: 2 (number)
|
|-- 1:
|-- repuestoRef: "catalogoRepuestos/repuesto_auto_id_2" (Reference)
|-- nombreRepuesto: "Rodillos" (string)
|-- cantidad: 4 (number)
```

---

### 🏗️ Detalle de Subcolecciones y Campos

#### 📂 Subcolección: `maquinaria/`
Cada documento representa una **máquina registrada** por el usuario.

| Campo | Tipo | Ejemplo | Descripción |
|--------|------|----------|-------------|
| `nombre` | `string` | `"Alpha 30"` | Nombre o modelo de la máquina. |
| `numeroIdentificador` | `string` | `"EQ: 163"` | Identificador único del equipo. |
| `fechaIngreso` | `timestamp` | `2025-08-04` | Fecha de ingreso al taller. |
| `descripcion` | `string` | `"Falla en el sistema hidráulico."` | Detalle del problema reportado. |
| `partesPrincipales` | `array<string>` | `["BRAZO BB-2027", "BLOCK BOMBEO"]` | Partes clave de la máquina. |
| `estado` | `boolean` | `false` | Estado operativo (`false` = en reparación, `true` = activa). |

---

#### ⚙️ Subcolección: `reparaciones/`
Cada documento representa una **reparación o intervención** realizada sobre una máquina.

| Campo | Tipo | Ejemplo | Descripción |
|--------|------|----------|-------------|
| `fecha` | `timestamp` | `"2025-08-05T10:30:00"` | Fecha de la reparación. |
| `notas` | `string` | `"Se encontraron pernos sueltos..."` | Observaciones del técnico. |
| `repuestosUsados` | `array<Map>` | Ver tabla siguiente | Lista de repuestos utilizados en esta reparación. |

---

#### 🧱 Detalle del Campo `repuestosUsados` (Array de Mapas)

Cada elemento del array representa un repuesto específico utilizado durante la reparación.

| Campo | Tipo | Ejemplo | Descripción |
|--------|------|----------|-------------|
| `repuestoRef` | `Reference` | `catalogoRepuestos/repuesto_auto_id_1` | Referencia directa al repuesto en el catálogo global. |
| `nombreRepuesto` | `string` | `"Placa Fijacion"` | Nombre desnormalizado para visualización rápida. |
| `cantidad` | `number` | `2` | Número de unidades utilizadas. |
---
## Patrones de diseño utilizados

- **Patrón Adaptador (Adapter):**
  Este patrón se usa directamente en la clase `PartesAdapter`. El adaptador convierte la interfaz de una clase (por ejemplo, una `List<String>`) en otra interfaz que el cliente espera (un `RecyclerView`). El adaptador sabe cómo mostrar cada elemento de la lista en las vistas de cada fila del `RecyclerView`.

- **Patrón ViewHolder:**
  Siempre que se utiliza un `RecyclerView`, el patrón ViewHolder es obligatorio. La clase interna `ParteViewHolder` mantiene referencias a las vistas (por ejemplo, `TextView` y `ImageButton`) para cada elemento de la lista. Esto evita llamadas repetitivas a `findViewById()` cada vez que se recicla una vista y mejora el rendimiento al desplazarse por la lista.

- **Patrón Repositorio (Repository):**
  Es fundamental en la arquitectura MVVM. El repositorio actúa como única fuente de verdad para los datos, separando la lógica de acceso a datos de la UI. Abstrae si los datos se obtienen de una base de datos local, una API remota (como Firestore) o una caché en memoria.

---
## 🚀 Instalación y Configuración

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
