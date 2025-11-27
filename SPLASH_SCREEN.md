# Splash Screen - Sistema de Reparaciones

## 🎨 Descripción

Pantalla de bienvenida animada que se muestra al iniciar la aplicación. Presenta el logo del sistema con animaciones de herramientas de reparación en los colores corporativos (negro y dorado).

## ✨ Características

### Animaciones
1. **Engranaje de Fondo**: Rotación continua con efecto pulsante
2. **Llave Inglesa**: Movimiento de balanceo simulando reparación
3. **Textos**: Aparición con efecto de escala y fade-in
4. **Transiciones**: Cambio suave entre pantallas

### Diseño
- **Colores**: Negro (#000000) y Dorado (#FFD700, #B8860B)
- **Fondo**: Gradiente oscuro sutil
- **Iconos**: Vectoriales escalables (SVG)
- **Tipografía**: Sans-serif con diferentes pesos

## 📁 Archivos Creados

### Layouts
- `activity_splash.xml` - Layout principal del splash screen

### Drawables
- `ic_wrench.xml` - Icono de llave inglesa (dorado claro)
- `ic_gear.xml` - Icono de engranaje (dorado oscuro)
- `ic_hammer.xml` - Icono de martillo (dorado claro)
- `splash_gradient.xml` - Gradiente de fondo

### Animaciones
- `rotate_gear.xml` - Rotación y escala del engranaje
- `swing_wrench.xml` - Balanceo de la llave inglesa
- `fade_in_scale.xml` - Aparición con escala

### Java
- `SplashActivity.java` - Activity principal del splash

### Configuración
- `AndroidManifest.xml` - SplashActivity como LAUNCHER
- `themes.xml` - Tema personalizado para splash

## ⚙️ Funcionamiento

1. **Inicio**: La app inicia con SplashActivity
2. **Animaciones**: Se ejecutan durante 3 segundos
3. **Verificación**: Comprueba si el usuario está autenticado
4. **Navegación**:
   - Si está autenticado → `MainActivity`
   - Si no está autenticado → `LoginActivity`
5. **Transición**: Fade suave entre pantallas

## 🔧 Personalización

### Cambiar Duración
```java
private static final long SPLASH_DURATION = 3000; // milisegundos
```

### Modificar Colores
Editar `res/values/colors.xml`:
```xml
<color name="gold_light">#FFD700</color>
<color name="gold_dark">#B8860B</color>
```

### Ajustar Animaciones
Editar archivos en `res/anim/`:
- `android:duration` - Duración de la animación
- `android:repeatCount` - Número de repeticiones
- `android:interpolator` - Tipo de interpolación

## 📱 Compatibilidad

- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Orientación**: Portrait y Landscape
- **Densidades**: Todos los tamaños de pantalla

## 🎯 Mejoras Futuras

- [ ] Animación de partículas flotantes
- [ ] Sonido de herramientas (opcional)
- [ ] Modo oscuro/claro automático
- [ ] Animación de carga progresiva
- [ ] Versión de la app en el splash
