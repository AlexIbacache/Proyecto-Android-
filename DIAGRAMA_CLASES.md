# Diagrama de Clases - Proyecto Android

## Estructura del Proyecto por Paquetes

```
com.example.proyectoandroid/
├── model/              # Modelos de datos (POJOs)
├── data/               # Repositorios y acceso a datos
├── ui/                 # Activities, Fragments y ViewModels
│   ├── login/
│   ├── register/
│   ├── main/
│   ├── maquinaria/
│   ├── reparacion/
│   ├── repuesto/
│   ├── profile/
│   ├── reportes/
│   └── admin/
├── adapters/           # Adaptadores de RecyclerView
└── util/               # Clases utilitarias
```

## Diagrama de Clases en Mermaid

```mermaid
classDiagram
    %% ========================================
    %% PAQUETE: model
    %% ========================================
    namespace model {
        class Maquinaria {
            -String documentId
            -String nombre
            -String numeroIdentificador
            -Timestamp fechaIngreso
            -String descripcion
            -List~String~ partesPrincipales
            -boolean estado
            -boolean isSelected
            -String imagenUrl
            +getDocumentId() String
            +setDocumentId(String)
            +getNombre() String
            +setNombre(String)
            +getNumeroIdentificador() String
            +getFechaIngreso() Timestamp
            +getDescripcion() String
            +getPartesPrincipales() List~String~
            +isEstado() boolean
            +getImagenUrl() String
        }

        class Reparacion {
            -String documentId
            -Timestamp fecha
            -String notas
            -List~ParteReparada~ partesReparadas
            -String estado
            +getDocumentId() String
            +getFecha() Timestamp
            +getNotas() String
            +getPartesReparadas() List~ParteReparada~
            +getEstado() String
            +setEstado(String)
        }

        class ParteReparada {
            -String nombreParte
            -List~Repuesto~ repuestos
            +getNombreParte() String
            +setNombreParte(String)
            +getRepuestos() List~Repuesto~
            +setRepuestos(List~Repuesto~)
        }

        class Repuesto {
            -String documentId
            -String nombre
            -String codigo
            -int cantidad
            +getDocumentId() String
            +getNombre() String
            +getCodigo() String
            +getCantidad() int
            +setCantidad(int)
            +equals(Object) boolean
            +hashCode() int
        }

        class RepuestoUsado {
            -DocumentReference repuestoRef
            -String nombreRepuesto
            -long cantidad
            +getRepuestoRef() DocumentReference
            +getNombreRepuesto() String
            +getCantidad() long
        }

        class LogEntry {
            -String documentId
            -String userId
            -String userEmail
            -String userName
            -String actionType
            -String actionDescription
            -String entityType
            -String entityId
            -Timestamp timestamp
            -Map~String, Object~ additionalData
            +getUserId() String
            +getActionType() String
            +getActionDescription() String
            +getTimestamp() Timestamp
            +addAdditionalData(String, Object)
        }
    }

    %% ========================================
    %% PAQUETE: data
    %% ========================================
    namespace data {
        class MaquinariaRepository {
            -FirebaseFirestore db
            -FirebaseAuth mAuth
            -FirebaseStorage storage
            +getNewMaquinariaId() String
            +getMaquinariaList() LiveData~List~Maquinaria~~
            +getMaquinariaById(String) LiveData~Maquinaria~
            +guardarMaquinaria(Maquinaria, FirestoreCallback)
            +actualizarMaquinaria(Maquinaria, FirestoreCallback)
            +subirFotoMaquinaria(String, Uri, UploadImageCallback)
            +getReparacionAbierta(String, ReparacionCallback)
            +getReparacionesDeMaquina(String) LiveData~List~Reparacion~~
            +guardarReparacion(String, Reparacion, FirestoreCallback)
            +actualizarReparacion(String, Reparacion, FirestoreCallback)
            +eliminarMaquinaria(String)
            +deleteReparacion(String, String, String)
            +getReparacionStats(String, ReparacionStatsCallback)
        }

        class RepuestoRepository {
            -FirebaseFirestore db
            +getCatalogoRepuestos() LiveData~List~Repuesto~~
        }

        class LoggerRepository {
            -FirebaseFirestore db
            +saveLog(LogEntry)
            +getUserLogs(String) LiveData~List~LogEntry~~
            +getAllLogs() LiveData~List~LogEntry~~
            +deleteOldLogs(int)
        }

        class AuthRepository {
            <<interface>>
            +login(String, String) LiveData~Result~FirebaseUser~~
            +loginWithGoogle(String) LiveData~Result~FirebaseUser~~
            +register(String, String, String) LiveData~Result~FirebaseUser~~
            +logout()
        }

        class FirebaseAuthRepository {
            -FirebaseAuth mAuth
            -FirebaseFirestore db
            +login(String, String) LiveData~Result~FirebaseUser~~
            +loginWithGoogle(String) LiveData~Result~FirebaseUser~~
            +register(String, String, String) LiveData~Result~FirebaseUser~~
            +logout()
        }
    }

    %% ========================================
    %% PAQUETE: ui.login
    %% ========================================
    namespace ui_login {
        class LoginActivity {
            -LoginViewModel viewModel
            -ActivityResultLauncher launcher
            +onCreate(Bundle)
            +onActivityResult(int, int, Intent)
        }

        class LoginViewModel {
            -AuthRepository authRepository
            -MutableLiveData~Void~ _googleSignInEvent
            +LiveData~Void~ googleSignInEvent
            +login(String, String) LiveData~Result~FirebaseUser~~
            +initiateGoogleSignIn()
            +handleGoogleSignInResult(String) LiveData~Result~FirebaseUser~~
        }
    }

    %% ========================================
    %% PAQUETE: ui.register
    %% ========================================
    namespace ui_register {
        class RegistrarFormActivity {
            -RegisterViewModel viewModel
            +onCreate(Bundle)
            +onRegisterClick()
        }

        class RegisterViewModel {
            -AuthRepository authRepository
            +register(String, String, String) LiveData~Result~FirebaseUser~~
        }
    }

    %% ========================================
    %% PAQUETE: ui.main
    %% ========================================
    namespace ui_main {
        class MainActivity {
            -NavController navController
            -BottomNavigationView bottomNav
            +onCreate(Bundle)
            +onSupportNavigateUp() boolean
        }
    }

    %% ========================================
    %% PAQUETE: ui.maquinaria
    %% ========================================
    namespace ui_maquinaria {
        class MaquinariaFragment {
            -MaquinariaViewModel viewModel
            -MaquinariaAdapter adapter
            +onCreateView() View
            +onViewCreated(View, Bundle)
        }

        class MaquinariaFormFragment {
            -MaquinariaFormViewModel viewModel
            +onCreateView() View
            +onSaveClick()
        }

        class MaquinariaViewModel {
            -MaquinariaRepository repository
            -LiveData~List~Maquinaria~~ maquinarias
            -MutableLiveData~Maquinaria~ _maquinaSeleccionada
            +LiveData~List~String~~ partesSeleccionadas
            +LiveData~Boolean~ botonesDeAccionVisibles
            +getMaquinarias() LiveData~List~Maquinaria~~
            +onMaquinaSeleccionada(Maquinaria)
            +eliminarMaquinaSeleccionada(Maquinaria)
            +eliminarParte(int)
            +actualizarParte(int, String)
        }

        class MaquinariaFormViewModel {
            -MaquinariaRepository repository
            +guardarMaquinaria(Maquinaria, FirestoreCallback)
            +actualizarMaquinaria(Maquinaria, FirestoreCallback)
            +subirFotoMaquinaria(String, Uri, UploadImageCallback)
        }

        class PartesAdapter {
            -List~String~ partes
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }
    }

    %% ========================================
    %% PAQUETE: ui.reparacion
    %% ========================================
    namespace ui_reparacion {
        class ReparacionFragment {
            -ReparacionViewModel viewModel
            -SharedReparacionViewModel sharedViewModel
            +onCreateView() View
            +onGuardarReparacion()
        }

        class ReparacionRepuestoFragment {
            -ReparacionRepuestoViewModel viewModel
            -SharedReparacionViewModel sharedViewModel
            +onCreateView() View
            +onAgregarRepuesto()
        }

        class ReparacionViewModel {
            -MaquinariaRepository repository
            +getReparacionAbierta(String, ReparacionCallback)
            +guardarReparacion(String, Reparacion, FirestoreCallback)
            +actualizarReparacion(String, Reparacion, FirestoreCallback)
        }

        class ReparacionRepuestoViewModel {
            -RepuestoRepository repository
            +getCatalogoRepuestos() LiveData~List~Repuesto~~
        }

        class SharedReparacionViewModel {
            -MutableLiveData~List~ParteReparada~~ partesReparadas
            +getPartesReparadas() LiveData~List~ParteReparada~~
            +addParteReparada(ParteReparada)
        }

        class ReparacionPartesAdapter {
            -List~ParteReparada~ partes
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }
    }

    %% ========================================
    %% PAQUETE: ui.repuesto
    %% ========================================
    namespace ui_repuesto {
        class RepuestoActivity {
            -RepuestoViewModel viewModel
            -RepuestoAdapter adapter
            +onCreate(Bundle)
            +onAddRepuesto()
        }

        class RepuestoViewModel {
            -RepuestoRepository repository
            +getCatalogoRepuestos() LiveData~List~Repuesto~~
        }

        class RepuestoAdapter {
            -List~Repuesto~ repuestos
            -OnRepuestoClickListener listener
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }
    }

    %% ========================================
    %% PAQUETE: ui.profile
    %% ========================================
    namespace ui_profile {
        class ProfileFragment {
            -ProfileViewModel viewModel
            +onCreateView() View
            +loadUserStats()
        }

        class ProfileViewModel {
            -MaquinariaRepository repository
            +getReparacionStats(String, ReparacionStatsCallback)
        }
    }

    %% ========================================
    %% PAQUETE: ui.reportes
    %% ========================================
    namespace ui_reportes {
        class ReportesFragment {
            -ReportesViewModel viewModel
            -ReportesAdapter adapter
            +onCreateView() View
            +onGenerarReporte()
        }

        class ReportesViewModel {
            -MaquinariaRepository repository
            +getMaquinariaList() LiveData~List~Maquinaria~~
            +getReparacionesDeMaquina(String) LiveData~List~Reparacion~~
        }

        class ReportesAdapter {
            -List~Maquinaria~ maquinarias
            -OnReporteClickListener listener
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }

        class ReparacionesDialogAdapter {
            -List~Reparacion~ reparaciones
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }
    }

    %% ========================================
    %% PAQUETE: ui.admin
    %% ========================================
    namespace ui_admin {
        class AdminFragment {
            +onCreateView() View
            +onDeleteOldLogs()
            +onViewAllLogs()
        }
    }

    %% ========================================
    %% PAQUETE: adapters
    %% ========================================
    namespace adapters {
        class MaquinariaAdapter {
            -List~Maquinaria~ maquinarias
            -OnMaquinariaClickListener listener
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
            +updateList(List~Maquinaria~)
        }

        class MaquinariaSpinnerAdapter {
            -List~Maquinaria~ maquinarias
            +getView(int, View, ViewGroup) View
            +getDropDownView(int, View, ViewGroup) View
            +getCount() int
        }

        class ParteReparadaAdapter {
            -List~ParteReparada~ partes
            +onCreateViewHolder() ViewHolder
            +onBindViewHolder(ViewHolder, int)
            +getItemCount() int
        }
    }

    %% ========================================
    %% PAQUETE: util
    %% ========================================
    namespace util {
        class UserActionLogger {
            -LoggerRepository loggerRepository$
            +logAction(String, String, String, String)$
            +logLogin(String)$
            +logLogout()$
            +logRegister(String, String)$
            +logCreate(String, String, String)$
            +logUpdate(String, String, String)$
            +logDelete(String, String, String)$
            +logView(String, String, String)$
            +logCreateMaquinaria(String, String)$
            +logUpdateMaquinaria(String, String)$
            +logDeleteMaquinaria(String, String)$
            +logCreateReparacion(String, String)$
            +logUpdateReparacion(String, String)$
            +logDeleteReparacion(String, String)$
            +logCreateRepuesto(String, String)$
            +logUploadImage(String, String)$
            +logCreateReporte(String)$
        }

        class AdminHelper {
            -FirebaseFirestore db$
            +isAdmin(String, OnAdminCheckListener)$
            +isCurrentUserAdmin(OnAdminCheckListener)$
        }

        class Validators {
            +isValidEmail(String)$ boolean
            +isValidPassword(String)$ boolean
            +isValidName(String)$ boolean
        }

        class Result~T~ {
            <<sealed>>
            +Success~T~
            +Error~T~
            +Loading~T~
        }

        class SingleLiveEvent~T~ {
            -AtomicBoolean pending
            +observe(LifecycleOwner, Observer~T~)
            +setValue(T)
            +call()
        }
    }

    %% ========================================
    %% RELACIONES ENTRE PAQUETES
    %% ========================================
    
    %% Composición de modelos (model)
    Reparacion "1" *-- "0..*" ParteReparada : contiene
    ParteReparada "1" *-- "0..*" Repuesto : usa
    
    %% Repositorios y modelos (data -> model)
    MaquinariaRepository ..> Maquinaria : gestiona
    MaquinariaRepository ..> Reparacion : gestiona
    RepuestoRepository ..> Repuesto : gestiona
    LoggerRepository ..> LogEntry : gestiona
    
    %% Implementación de interfaces (data)
    FirebaseAuthRepository ..|> AuthRepository : implementa
    
    %% ViewModels y Repositorios (ui -> data)
    MaquinariaViewModel --> MaquinariaRepository : usa
    MaquinariaFormViewModel --> MaquinariaRepository : usa
    ReparacionViewModel --> MaquinariaRepository : usa
    ReparacionRepuestoViewModel --> RepuestoRepository : usa
    RepuestoViewModel --> RepuestoRepository : usa
    LoginViewModel --> AuthRepository : usa
    RegisterViewModel --> AuthRepository : usa
    ProfileViewModel --> MaquinariaRepository : usa
    ReportesViewModel --> MaquinariaRepository : usa
    
    %% Utilidades y Repositorios (util -> data)
    UserActionLogger --> LoggerRepository : usa
    
    %% Fragments/Activities y ViewModels (ui)
    MaquinariaFragment --> MaquinariaViewModel : observa
    MaquinariaFormFragment --> MaquinariaFormViewModel : observa
    ReparacionFragment --> ReparacionViewModel : observa
    ReparacionFragment --> SharedReparacionViewModel : observa
    ReparacionRepuestoFragment --> ReparacionRepuestoViewModel : observa
    ReparacionRepuestoFragment --> SharedReparacionViewModel : observa
    RepuestoActivity --> RepuestoViewModel : observa
    LoginActivity --> LoginViewModel : observa
    RegistrarFormActivity --> RegisterViewModel : observa
    ProfileFragment --> ProfileViewModel : observa
    ReportesFragment --> ReportesViewModel : observa
    
    %% Adaptadores y Modelos (adapters -> model)
    MaquinariaAdapter ..> Maquinaria : muestra
    MaquinariaSpinnerAdapter ..> Maquinaria : muestra
    ParteReparadaAdapter ..> ParteReparada : muestra
    RepuestoAdapter ..> Repuesto : muestra
    ReportesAdapter ..> Maquinaria : muestra
    ReparacionesDialogAdapter ..> Reparacion : muestra
    
    %% Fragments y Adaptadores (ui -> adapters)
    MaquinariaFragment --> MaquinariaAdapter : usa
    ReportesFragment --> ReportesAdapter : usa
    RepuestoActivity --> RepuestoAdapter : usa
    ReparacionFragment --> ReparacionPartesAdapter : usa
    
    %% Logging (ui -> util)
    LoginActivity ..> UserActionLogger : registra
    RegistrarFormActivity ..> UserActionLogger : registra
    MaquinariaRepository ..> UserActionLogger : registra
    
    %% Admin (ui.admin -> util)
    AdminFragment ..> AdminHelper : verifica
    MainActivity ..> AdminHelper : verifica
```

## Descripción por Paquetes

### 📦 **model** - Modelos de Datos
Contiene los POJOs (Plain Old Java Objects) que representan las entidades del dominio:
- `Maquinaria`: Datos de una máquina
- `Reparacion`: Registro de reparación
- `ParteReparada`: Parte reparada con repuestos
- `Repuesto`: Repuesto del catálogo
- `RepuestoUsado`: Referencia a repuesto usado
- `LogEntry`: Registro de acciones de usuario

### 📦 **data** - Capa de Datos
Implementa el patrón Repository para abstraer el acceso a datos:
- `MaquinariaRepository`: CRUD de maquinarias y reparaciones
- `RepuestoRepository`: Gestión del catálogo de repuestos
- `LoggerRepository`: Gestión de logs
- `AuthRepository` (interface): Contrato de autenticación
- `FirebaseAuthRepository`: Implementación con Firebase

### 📦 **ui** - Capa de Presentación
Organizada por features, cada uno con su Activity/Fragment y ViewModel:

#### **ui.login**
- `LoginActivity`: Pantalla de inicio de sesión
- `LoginViewModel`: Lógica de autenticación

#### **ui.register**
- `RegistrarFormActivity`: Pantalla de registro
- `RegisterViewModel`: Lógica de registro

#### **ui.main**
- `MainActivity`: Activity principal con navegación

#### **ui.maquinaria**
- `MaquinariaFragment`: Lista de maquinarias
- `MaquinariaFormFragment`: Formulario de maquinaria
- `MaquinariaViewModel`: Lógica de listado
- `MaquinariaFormViewModel`: Lógica de formulario
- `PartesAdapter`: Adaptador para partes

#### **ui.reparacion**
- `ReparacionFragment`: Gestión de reparaciones
- `ReparacionRepuestoFragment`: Selección de repuestos
- `ReparacionViewModel`: Lógica de reparaciones
- `ReparacionRepuestoViewModel`: Lógica de repuestos
- `SharedReparacionViewModel`: Estado compartido
- `ReparacionPartesAdapter`: Adaptador de partes

#### **ui.repuesto**
- `RepuestoActivity`: Gestión de repuestos
- `RepuestoViewModel`: Lógica de repuestos
- `RepuestoAdapter`: Adaptador de repuestos

#### **ui.profile**
- `ProfileFragment`: Perfil de usuario
- `ProfileViewModel`: Lógica de perfil y estadísticas

#### **ui.reportes**
- `ReportesFragment`: Generación de reportes
- `ReportesViewModel`: Lógica de reportes
- `ReportesAdapter`: Adaptador de reportes
- `ReparacionesDialogAdapter`: Diálogo de reparaciones

#### **ui.admin**
- `AdminFragment`: Panel de administración

### 📦 **adapters** - Adaptadores Compartidos
Adaptadores de RecyclerView y Spinner usados en múltiples pantallas:
- `MaquinariaAdapter`: Lista de maquinarias
- `MaquinariaSpinnerAdapter`: Spinner de maquinarias
- `ParteReparadaAdapter`: Lista de partes reparadas

### 📦 **util** - Utilidades
Clases helper y utilitarias:
- `UserActionLogger`: Singleton para logging de acciones
- `AdminHelper`: Verificación de permisos de admin
- `Validators`: Validaciones de entrada
- `Result<T>`: Clase sellada para estados (Success/Error/Loading)
- `SingleLiveEvent<T>`: LiveData para eventos únicos

## Patrones de Diseño Aplicados

1. **MVVM (Model-View-ViewModel)**: Separación clara de responsabilidades
2. **Repository Pattern**: Abstracción de fuente de datos
3. **Singleton**: `UserActionLogger`, `AdminHelper`
4. **Observer Pattern**: LiveData para comunicación reactiva
5. **Adapter Pattern**: RecyclerView adapters
6. **Factory Pattern**: ViewModels con ViewModelProvider
7. **Dependency Injection**: Repositorios inyectados en ViewModels

## Flujo de Datos

```
View (Fragment/Activity) 
    ↓ observa
ViewModel 
    ↓ usa
Repository 
    ↓ accede
Firebase (Firestore/Auth/Storage)
```

## Tecnologías Utilizadas

- **Firebase**: Firestore, Authentication, Storage
- **Android Architecture Components**: LiveData, ViewModel, Navigation
- **Material Design**: Components UI
- **RecyclerView**: Listas dinámicas
