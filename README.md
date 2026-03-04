# 💳 Módulo de Pagos Externos

Sistema de gestión de pagos para servicios externos desarrollado en Java con interfaz gráfica JavaFX.

## 📋 Descripción

Este módulo permite gestionar el registro de servicios externos, clientes y el procesamiento de facturas de pago. Cuenta con una interfaz gráfica intuitiva desarrollada con JavaFX y utiliza una base de datos MySQL para la persistencia de datos.

## ✨ Características

- 📝 Registro y gestión de servicios externos
- 👥 Administración de clientes
- 💰 Procesamiento de facturas de pago
- 📄 Generación de documentos PDF
- 🔍 Historial de pagos
- 🎨 Interfaz gráfica moderna con JavaFX

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **JavaFX 22** - Interfaz gráfica
- **Spring Boot 3.2.4** - Framework principal
- **JPA/Hibernate** - Persistencia de datos
- **MySQL** - Base de datos
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate
- **JUnit 5 + Mockito** - Testing
- **Apache PDFBox** - Generación de PDFs
- **OkHttp** - Cliente HTTP
- **Gson** - Procesamiento JSON

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/org/jala/university/
│   │   ├── application/        # Lógica de aplicación y servicios
│   │   ├── domain/             # Entidades y repositorios
│   │   ├── infrastructure/     # Implementación de persistencia
│   │   ├── presentation/       # Controladores de la UI
│   │   └── MainApp.java        # Punto de entrada
│   └── resources/
│       ├── *.fxml              # Vistas de JavaFX
│       ├── styles/             # Estilos CSS
│       └── sql/                # Scripts de base de datos
└── test/                       # Pruebas unitarias
```

## 🚀 Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6 o superior
- MySQL 8.0 o superior
- Git

## ⚙️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://gitlab.com/jala-university1/cohort-2/desarrollo-de-software-2-es/practitioners/capstone/external-payment-module.git
cd external-payment-module
```

### 2. Configurar la base de datos

```bash
# Crear la base de datos ejecutando el script
mysql -u root -p < src/main/resources/sql/create_database.sql
```

### 3. Configurar la persistencia

Edita el archivo `src/main/resources/META-INF/persistence.xml` con tus credenciales de MySQL:

```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/tu_base_de_datos"/>
<property name="jakarta.persistence.jdbc.user" value="tu_usuario"/>
<property name="jakarta.persistence.jdbc.password" value="tu_contraseña"/>
```

### 4. Instalar dependencias

```bash
mvn clean install
```

## 🎯 Ejecución

### Ejecutar la aplicación

```bash
mvn javafx:run
```

O ejecutar el JAR empaquetado:

```bash
java -jar target/external-payment-module-1.0-SNAPSHOT-shaded.jar
```

### Ejecutar pruebas

```bash
mvn test
```

### Generar reporte de cobertura

```bash
mvn clean test
# El reporte se genera en: target/site/jacoco/index.html
```

### Verificar estilo de código

```bash
mvn checkstyle:check
```

## 📦 Dependencias de Módulos

Este proyecto depende de otros módulos internos:
- `commons-module` (v1.0-SNAPSHOT)
- `transaction-module` (v1.0-SNAPSHOT)

Estos módulos se obtienen automáticamente desde los repositorios de GitLab configurados en el `pom.xml`.

## 🧪 Testing

El proyecto utiliza:
- **JUnit 5** para pruebas unitarias
- **Mockito** para mocking
- **JaCoCo** para cobertura de código (mínimo 50%)

Ejecutar todas las pruebas:

```bash
mvn clean verify
```

## 📊 Cobertura de Código

El proyecto requiere un mínimo de 50% de cobertura de código. Para ver el reporte:

1. Ejecuta `mvn test`
2. Abre `target/site/jacoco/index.html` en tu navegador

## 🐳 Docker

El proyecto incluye un archivo `compose.yml` para facilitar el despliegue con Docker:

```bash
docker-compose up
```

## 📄 Licencia

Este proyecto es parte del programa académico de Jala University.

## 👥 Autores

Proyecto Capstone - Jala University Cohort 5 - Desarrollo de Software 2

- **Abigail Quiroz** 
- **Carla Mayoli Catari Calderon**
- **Luis Eduardo Barajas Cabrera** 
- **Rodys Enrique Rodriguez Santamaria** 
- **Jean Pierre Crespin Huaman** 

---

**Jala University** © 2025
