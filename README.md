# Nombre de la App: MachineManagement

## 📌 Propósito
MachineManagement es una aplicación Android orientada a talleres mecánicos, que permite **registrar y gestionar repuestos utilizados en maquinarias**.  
El objetivo es organizar la información de repuestos y tenerla disponible de manera digital y centralizada con Firebase.  

---

## 📱 Pantallas (Activities previstas)
1. **LoginActivity** → Inicio de sesión de usuario.  
2. **MainActivity** → Menú principal (opciones de registro y consulta).  
3. **RegistroActivity** → Formulario para registrar un repuesto.  
4. **ListaActivity** → Visualización de repuestos registrados en Firebase.  

---

## 🔄 Navegación
- **LoginActivity → MainActivity**.  
- **MainActivity → RegistroActivity**.  
- **RegistroActivity → ListaActivity**.  
- **MainActivity → ListaActivity**.  

---

## ⚙️ Componentes previstos
- **Activities**: para cada pantalla mencionada.  
- **Intents**: para navegación y paso de datos (extras).  
- **Firebase Authentication**: gestión de usuarios (opcional).  
- **Firebase Firestore / Realtime Database**: almacenamiento de datos de repuestos.  
- **RecyclerView**: mostrar listas de registros.  
- **Firebase Storage (opcional)**: guardar fotos de repuestos o facturas.  
