# Nombre de la App: SparePartsM (SPM)

## 📌 Propósito
SparePartsM (SPM) es una aplicación Android orientada a talleres mecánicos, que permite **registrar y gestionar repuestos utilizados en maquinarias**.  
El objetivo es organizar la información de repuestos y tenerla disponible de manera digital y centralizada con Firebase.  

---

## 🚀 Instalación
1. **Clonar el repositorio**  
   ```bash
   git clone https://github.com/AlexIbacache/Proyecto-Android-
   ```
   Luego abre el proyecto en **Android Studio**.

2. **Configurar el SDK y Gradle**  
   - Asegúrate de tener instalado **Android Studio (versión recomendada: Iguana o superior)**.  
   - Usar **JDK 21** o la versión configurada en `gradle.properties`.  
   - Verifica que el proyecto compile correctamente con **API Level 34** (Android 14).
   - Nota: cambiar version de agp en caso de incompatibilidad

3. **Configurar Firebase(Aún por implementar)**  
   - Crea un proyecto en [Firebase Console](https://console.firebase.google.com/).  
   - Descarga el archivo `google-services.json` y colócalo en la carpeta:  
     ```
     app/google-services.json
     ```
   - Habilita los servicios necesarios en Firebase:
     - **Authentication** (si se usa LoginActivity).  
     - **Cloud Firestore o Realtime Database** (para almacenar datos).  
     - **Storage** (si se exportan/guardan archivos).  

4. **Sincronizar dependencias**  
   - En Android Studio, haz clic en **Sync Project with Gradle Files** para descargar las librerías necesarias.

5. **Ejecutar la aplicación**  
   - Conecta un dispositivo físico (con **depuración USB activada**) o utiliza un **emulador Android** configurado con API 34.  
   - Presiona **Run ▶️** en Android Studio para compilar y desplegar la app.

---

## 📱 Pantallas (Activities previstas)
1. **LoginActivity** → Autenticación de usuario (opcional, si se requiere control de acceso).  
2. **MainActivity** → Menú principal o dashboard con opciones (ej. ver maquinaria, reportes).  
3. **MaquinariaListActivity** → Lista de maquinaria registrada.  
4. **MaquinariaFormActivity** → Formulario modal para agregar o editar maquinaria (se abre sobre MaquinariaListActivity).
5. **ReparacionActivity** → Formulario para registrar reparaciones, seleccionar maquinaria y agregar repuestos usados.
6. **ReporteActivity** → Exportación a Excel y visualización de informes.

---

## 🔄 Navegación
- **LoginActivity → MainActivity**.  
- **MainActivity → MaquinariaListActivity**.  
- **MaquinariaListActivity  → MaquinariaFormActivity**.  
- **MaquinariaListActivity  → ReparacionActivity**.
- **ReparacionActivity   → Modal para agregar repuestos**.
- **MainActivity    → ReporteActivity**.

---

## ⚙️ Componentes previstos
- **Firebase Authentication**: Para gestionar el login de usuarios (si decides usar LoginActivity).  
- **Firebase Firestore / Realtime Database**: Para almacenar y sincronizar datos de maquinaria, reparaciones y repuestos usados en la nube.  
- **Activities**: Para cada pantalla mencionada.  
- **DialogFragment**: Para formularios modales (agregar maquinaria y repuestos).  
- **RecyclerView**: Para mostrar listas (maquinaria, reparaciones, repuestos).  
- **FloatingActionButton (FAB)**: Para agregar repuestos en ReparacionActivity.  
- **ViewModel + LiveData (opcional)**: Para manejar datos y observar cambios en Firestore.  
- **Intents explícitos**: Para navegación entre Activities.  
- **Librería para exportar Excel**: Apache POI o similar.  
- **FileProvider**: Para compartir archivos Excel exportados.  
