# Nombre de la App: SparePartsM (SPM)

## 📌 Propósito
SparePartsM (SPM) es una aplicación Android orientada a talleres mecánicos, que permite **registrar y gestionar repuestos utilizados en maquinarias**.  
El objetivo es organizar la información de repuestos y tenerla disponible de manera digital y centralizada con Firebase.  

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
