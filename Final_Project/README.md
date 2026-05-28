# DrivePulse

*Every road tells a story. Share yours.*

DrivePulse é uma aplicação Android (Kotlin + Jetpack Compose) direcionada a entusiastas de automóveis. 
Permite registar trajetos, partilhar com a comunidade, explorar mapas de locais recomendados e gerir o seu "perfil de condutor" (estilo Strava para carros).

## Stack Técnica
- **UI:** Jetpack Compose, Material Design 3
- **Arquitetura:** Clean Architecture + MVVM + MVI-like StateFlow
- **Injeção de Dependências:** Hilt
- **Dados Locais:** Room, DataStore
- **Dados Remotos:** Firebase (Auth, Firestore, Storage)
- **Localização & Mapa:** FusedLocationProviderClient, Google Maps SDK for Android
- **Tarefas em Background:** WorkManager, Foreground Services
- **Networking:** Retrofit, kotlinx.serialization, Open-Meteo API
- **Multimédia:** Coil, Media3

## Setup do Projeto

1. Clone o repositório.
2. Adicione as suas chaves e tokens no ficheiro `local.properties` (na raiz do projeto):
   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   OPEN_METEO_BASE_URL=https://api.open-meteo.com/
   ```
3. Coloque o ficheiro `google-services.json` na pasta `app/`.
4. (Opcional) Verifique `.env.example` para outras chaves que possa precisar de configurar dependendo das features implementadas.
5. Compile o projeto e instale no dispositivo Android ou Emulador.

## Arquitetura
A app segue rigorosamente uma arquitetura dividida em camadas:
- **UI Layer:** Ecrãs (Compose), Navigation, Componentes e ViewModels por feature.
- **Domain Layer:** Modelos core, interfaces de Repositórios e UseCases.
- **Data Layer:** Implementação de repositórios, Room DAOs, DataStore, Firebase Data Sources e APIs.

Consultar `docs/` para mais detalhes sobre Arquitetura, Base de Dados, Flow de ecrãs e Guias de Estilo.
