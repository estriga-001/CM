# Setup, APIs e ambiente

## Android Studio

Criar projeto:

- Template: Empty Activity / Compose
- Language: Kotlin
- Minimum SDK: 26 ou superior
- UI: Jetpack Compose
- Build system: Gradle Kotlin DSL

## Firebase

Ativar:

- Authentication → Email/Password
- Firestore Database
- Storage
- Cloud Functions, opcional/recomendado
- Cloud Messaging, opcional

Passos:

1. Criar projeto Firebase.
2. Registar app Android com o package name final.
3. Transferir `google-services.json`.
4. Colocar em `app/google-services.json`.
5. Nunca commitar o ficheiro real para GitHub público.
6. Criar regras Firestore e Storage.
7. Criar índices.

## Maps

Opção recomendada para MVP académico: Google Maps SDK + Maps Compose.

Motivos:

- integração direta com Android;
- documentação forte;
- bom suporte com Compose;
- fácil demonstrar ao professor.

Alternativa: Mapbox, caso a equipa queira estilo visual mais personalizável.

## Meteorologia

Usar Open-Meteo:

```text
https://api.open-meteo.com/v1/forecast
```

Exemplo de parâmetros:

```text
latitude=38.7223
longitude=-9.1393
current=temperature_2m,precipitation,wind_speed_10m,weather_code
hourly=temperature_2m,precipitation_probability,wind_speed_10m,visibility
timezone=auto
```

## Variáveis de ambiente

Android não usa `.env` diretamente como backend Node. Usar:

- `local.properties` para chaves locais;
- `BuildConfig` para injetar valores;
- Firebase Remote Config para flags;
- `.env.example` apenas como documentação da equipa.

## `local.properties` local

Exemplo:

```properties
MAPS_API_KEY=your_google_maps_key
OPEN_METEO_BASE_URL=https://api.open-meteo.com/
IMAGE_GENERATION_PROXY_URL=https://your-cloud-function-url
```

No Gradle, expor de forma controlada:

```kotlin
val mapsApiKey = localProperties.getProperty("MAPS_API_KEY") ?: ""
buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
```

## Dependências sugeridas

Usar Version Catalog em `gradle/libs.versions.toml`.

Categorias:

- Compose BOM
- Material 3
- Navigation Compose
- Lifecycle ViewModel Compose
- Hilt
- Hilt Navigation Compose
- Room
- DataStore
- Firebase BOM
- Firebase Auth
- Firebase Firestore
- Firebase Storage
- Firebase Functions
- Firebase Messaging
- Google Maps Compose
- Play Services Location
- Retrofit ou Ktor
- kotlinx.serialization
- Coil
- Media3
- WorkManager
- Timber
- JUnit
- Turbine
- MockK
- Compose UI Test

## Configuração de permissões

Permissões esperadas:

```xml
<uses-permission android:name="android.permission.INTERNET" />

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
```

Só pedir permissões no momento em que forem necessárias.

## Ambientes

Criar product flavors:

```text
dev
staging
prod
```

### dev

- Firebase dev project;
- logs ativos;
- dados simulados permitidos;
- premium toggle manual.

### staging

- Firebase staging;
- logs moderados;
- teste de usabilidade.

### prod

- Firebase prod;
- logs reduzidos;
- regras de segurança endurecidas.

## Git

Branches:

```text
main
develop
feature/auth
feature/home
feature/map
feature/run-tracking
feature/community
feature/profile
feature/premium-events
fix/<nome>
docs/<nome>
```

Commits:

```text
feat(auth): add firebase login repository
fix(run): prevent duplicate location points
docs(schema): update firestore collections
style(ui): adjust red accent palette
test(feed): add community ranking tests
```
