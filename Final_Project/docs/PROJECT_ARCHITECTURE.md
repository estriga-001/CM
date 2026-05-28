# Arquitetura do projeto Android

## Visão geral

A app deve seguir **Clean Architecture + MVVM + Repository Pattern**, com UI reativa em Jetpack Compose.

Fluxo base:

```text
Composable Screen
    ↓ envia eventos
ViewModel
    ↓ chama use cases
Domain UseCase
    ↓ usa interfaces
Repository
    ↓ coordena dados
Local Data Source / Remote Data Source / External API
```

## Camadas

### UI layer

Responsável por:

- ecrãs Compose;
- componentes reutilizáveis;
- navegação;
- UI State;
- UI Events;
- renderização de loading/error/empty/success.

Não pode conter:

- queries Firebase;
- cálculos complexos de rotas;
- lógica de permissões espalhada;
- regras de negócio;
- acesso direto a Room/Firebase/API.

### Domain layer

Responsável por:

- entidades de domínio;
- use cases;
- regras de negócio;
- validações;
- interfaces dos repositories.

Não pode depender de Android SDK, Firebase, Retrofit, Room ou Compose.

### Data layer

Responsável por:

- implementação dos repositories;
- DTOs;
- mappers;
- Room DAOs;
- Firebase data sources;
- APIs externas;
- WorkManager;
- sincronização;
- tratamento de cache.

## Estrutura recomendada

```text
app/
  src/main/
    java/com/drivepulse/
      DrivePulseApplication.kt

      core/
        common/
          Result.kt
          AppError.kt
          DispatcherProvider.kt
        designsystem/
          theme/
            Color.kt
            Theme.kt
            Type.kt
          components/
            DrivePulseButton.kt
            DrivePulseCard.kt
            DrivePulseTopBar.kt
            DrivePulseBottomBar.kt
            LoadingState.kt
            ErrorState.kt
        navigation/
          AppNavGraph.kt
          AppDestination.kt
          BottomNavItem.kt
        permissions/
          LocationPermissionHandler.kt
          PermissionState.kt
        location/
          LocationTracker.kt
          AndroidLocationTracker.kt
          TrackingForegroundService.kt
        media/
          MediaCompressor.kt
          ThumbnailGenerator.kt
        utils/
          DateTimeUtils.kt
          DistanceUtils.kt
          GeoHashUtils.kt
          ValidationUtils.kt

      data/
        local/
          DrivePulseDatabase.kt
          dao/
            RunDao.kt
            RoutePointDao.kt
            PostDao.kt
            MapPinDao.kt
          entity/
            RunEntity.kt
            RoutePointEntity.kt
            PostEntity.kt
            MapPinEntity.kt
        remote/
          firebase/
            FirebaseAuthDataSource.kt
            FirestoreUserDataSource.kt
            FirestoreRunDataSource.kt
            FirestorePostDataSource.kt
            FirebaseStorageDataSource.kt
          weather/
            WeatherApi.kt
            WeatherDto.kt
        repository/
          AuthRepositoryImpl.kt
          RunRepositoryImpl.kt
          PostRepositoryImpl.kt
          MapRepositoryImpl.kt
          ProfileRepositoryImpl.kt
          WeatherRepositoryImpl.kt
          PremiumRepositoryImpl.kt
        mapper/
          RunMapper.kt
          PostMapper.kt
          UserMapper.kt
          WeatherMapper.kt
        sync/
          SyncRunsWorker.kt
          SyncPostsWorker.kt
          UploadMediaWorker.kt

      domain/
        model/
          UserProfile.kt
          CarProfile.kt
          CarAvatar.kt
          Run.kt
          RoutePoint.kt
          RoadAlert.kt
          Post.kt
          Comment.kt
          MapPin.kt
          WeatherSummary.kt
          PremiumPlan.kt
          Event.kt
        repository/
          AuthRepository.kt
          RunRepository.kt
          PostRepository.kt
          MapRepository.kt
          ProfileRepository.kt
          WeatherRepository.kt
          PremiumRepository.kt
        usecase/
          auth/
            LoginUseCase.kt
            RegisterUseCase.kt
            LogoutUseCase.kt
            ContinueAsGuestUseCase.kt
          run/
            StartRunUseCase.kt
            AddRoutePointUseCase.kt
            StopRunUseCase.kt
            SaveRunDraftUseCase.kt
            PublishRunUseCase.kt
            ReportRoadAlertUseCase.kt
          community/
            GetFeedUseCase.kt
            LikePostUseCase.kt
            CommentPostUseCase.kt
            SaveRouteUseCase.kt
          map/
            GetMapPinsUseCase.kt
            GetNearbyRoutesUseCase.kt
            GetRoadAlertsUseCase.kt
          profile/
            GetProfileUseCase.kt
            UpdateCarUseCase.kt
            GenerateCarAvatarUseCase.kt
          weather/
            GetDriveWeatherAdviceUseCase.kt
          premium/
            CreateEventUseCase.kt
            JoinEventUseCase.kt

      feature/
        auth/
          AuthActivity.kt
          AuthNavGraph.kt
          login/
            LoginScreen.kt
            LoginViewModel.kt
            LoginUiState.kt
          register/
            RegisterScreen.kt
            RegisterViewModel.kt
            RegisterUiState.kt
          carsetup/
            CarSetupScreen.kt
            CarSetupViewModel.kt

        main/
          MainActivity.kt
          MainScaffold.kt

        home/
          HomeScreen.kt
          HomeViewModel.kt
          HomeUiState.kt

        map/
          MapScreen.kt
          MapViewModel.kt
          MapUiState.kt
          components/
            MapPinBottomSheet.kt
            MapFiltersRow.kt

        run/
          RunRecorderActivity.kt
          RunRecorderScreen.kt
          RunRecorderViewModel.kt
          RunRecorderUiState.kt
          components/
            RunStatsPanel.kt
            RoadAlertButton.kt
            FinishRunDialog.kt

        createpost/
          CreatePostScreen.kt
          CreatePostViewModel.kt
          CreatePostUiState.kt
          components/
            MediaPickerRow.kt
            HashtagInput.kt

        community/
          CommunityScreen.kt
          CommunityViewModel.kt
          CommunityUiState.kt
          postdetail/
            PostDetailScreen.kt
            PostDetailViewModel.kt

        profile/
          ProfileScreen.kt
          ProfileViewModel.kt
          ProfileUiState.kt
          settings/
            SettingsScreen.kt
          about/
            AboutScreen.kt
          help/
            HelpScreen.kt

        premium/
          EventsScreen.kt
          EventDetailScreen.kt
          CreateEventScreen.kt
```

## Activities necessárias para cumprir o enunciado

Embora a arquitetura moderna em Compose normalmente favoreça uma Activity principal, o enunciado académico pede pelo menos 3 Activities significativas. Usar:

### `AuthActivity`

Entrada para login, registo, recuperação de password e guest mode.

Recebe:

```kotlin
EXTRA_START_MODE = "login" | "register" | "guest"
```

Devolve:

```kotlin
RESULT_AUTH_SUCCESS
EXTRA_SESSION_MODE = "AUTHENTICATED" | "GUEST"
```

### `MainActivity`

Host principal da app com bottom navigation.

Recebe:

```kotlin
EXTRA_SESSION_MODE
EXTRA_DEEP_LINK_ROUTE_ID
```

### `RunRecorderActivity`

Activity dedicada para tracking GPS.

Recebe:

```kotlin
EXTRA_START_LOCATION
EXTRA_SELECTED_CAR_ID
EXTRA_PRIVACY_MODE
```

Devolve:

```kotlin
EXTRA_RUN_ID
EXTRA_RUN_STATUS = "DRAFT" | "PUBLISHED" | "DISCARDED"
```

### `RouteDetailActivity` opcional

Pode ser usada para detalhe de rota, abrindo a partir de mapa/feed/perfil.

Recebe:

```kotlin
EXTRA_ROUTE_ID
EXTRA_SOURCE = "MAP" | "FEED" | "PROFILE"
```

Devolve:

```kotlin
EXTRA_ROUTE_SAVED = true/false
```

## Navegação Compose

Dentro de `MainActivity`:

```text
main_graph
  home
  map
  run_entry
  community
  profile
  create_post/{runId}
  post_detail/{postId}
  route_detail/{routeId}
  settings
  help
  about
  events
```

## UI State padrão

Cada ViewModel deve expor um único estado:

```kotlin
data class ExampleUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val data: List<Example> = emptyList()
)
```

Para casos complexos, usar sealed interface:

```kotlin
sealed interface ScreenState<out T> {
    data object Loading : ScreenState<Nothing>
    data class Error(val message: String) : ScreenState<Nothing>
    data class Success<T>(val data: T) : ScreenState<T>
}
```

## Result wrapper

```kotlin
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Error(val error: AppError) : AppResult<Nothing>
}
```

## Regras de performance

- Feed e mapa devem ser paginados.
- Nunca carregar todos os posts/pins.
- Cache local para posts recentes, pins e runs.
- Uploads de media via WorkManager.
- Imagens com thumbnails.
- Vídeos comprimidos ou limitados.
- Recomposition Compose deve ser controlada com states estáveis.
- `Flow` deve ser convertido para `StateFlow` no ViewModel.
- Composables recolhem estado com `collectAsStateWithLifecycle()`.

## Testes mínimos

- Unit tests para use cases.
- Unit tests para ViewModels.
- Repository tests com fake data sources.
- Testes instrumentados para Room.
- UI tests para login, guest mode, iniciar run e feed.
