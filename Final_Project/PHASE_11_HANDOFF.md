# DrivePulse — Handoff Fase 11

> **Para o próximo agente:** Lê este documento do início ao fim antes de tocar num único ficheiro.
> O projeto compila com **BUILD SUCCESSFUL**. O ambiente Windows requer `org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr` no `gradle.properties` — **não remover esta linha**.

---

## Estado Actual da App (Fases 10 e 11 + Hotfixes)

| Feature | Estado |
|---|---|
| Registo / Login / Google Sign-In / Auto-Login | ✅ Funcional |
| Onboarding (username, nome, carro) | ✅ Funcional |
| Modo Convidado | ✅ Funcional |
| GPS Run Tracking | ✅ Funcional |
| Publicação de Run com descrição + imagem | ✅ Funcional |
| Feed da Comunidade (PostCard com mapa, stats, likes) | ✅ Funcional |
| Foto de Perfil (Base64, compressão e redimensionamento) | ✅ Funcional |
| Imagens em PostCard (Base64 via `getCoilDataModel`) | ✅ Funcional |
| Runs no Perfil do Utilizador | ✅ Funcional |
| Detalhes da Rota (`RouteDetailActivity`) | ✅ Implementado — mapa + stats + autor + foto |
| Settings — UI | ✅ Completo (Tema, Idioma, Alterar Password, Logout) |
| Settings — Tema dinâmico (sem restart) | ✅ Funcional via `MainViewModel` + `DrivePulseTheme(darkTheme=)` |
| Settings — Idioma | ✅ Funcional via `AppCompatDelegate.setApplicationLocales()` |
| Settings — Alterar Password (Firebase email) | ✅ Funcional |
| Settings — Logout | ✅ Funcional |
| Crash ao navegar Perfil / Comunidade | ✅ Corrigido — migração para `AppCompatActivity` |
| HomeScreen | ❌ Stub — só mostra texto "Home" |
| MapScreen | ❌ Stub — só mostra texto "Map" |

---

## Arquitectura do Projeto

```
app/
  core/
    common/         -> Constants.kt (inclui getCoilDataModel para Base64)
    designsystem/
      theme/        -> Theme.kt (suporta darkTheme param), Color.kt (tokens @Composable), Typography.kt
      components/   -> DrivePulseBottomBar, DrivePulseTopBar, DrivePulseButton, AuthGate
    navigation/     -> AppDestination, BottomNavItem, MainNavGraph
  data/
    local/          -> Room DB (runs, coordinates)
    remote/dto/     -> PostDto.kt, UserDto.kt, CommentDto.kt (com mappers .toDomain() e .toDto())
    repository/     -> AuthRepositoryImpl, UserRepositoryImpl, PostRepositoryImpl, RunRepositoryImpl
    preferences/    -> PreferencesManager.kt (DataStore — tema e idioma)
    di/             -> DataModule (Hilt)
  domain/
    model/          -> User, Run, Post, Comment, Coordinate, RunStatus, MediaType
    repository/     -> AuthRepository, UserRepository, RunRepository, PostRepository (interfaces)
    usecase/        -> auth/, profile/, run/
  feature/
    auth/           -> AuthActivity (AppCompatActivity), AuthNavGraph, AuthViewModel, LoginScreen, RegisterScreen
    community/      -> CommunityScreen, CommunityViewModel, PostCard (completo)
    createpost/     -> CreatePostScreen (ecrã de publicação com descrição + media)
    home/           -> HomeScreen (STUB)
    main/           -> MainActivity (AppCompatActivity), MainViewModel (expõe appTheme)
    map/            -> MapScreen (STUB)
    profile/        -> ProfileScreen, ProfileViewModel, EditProfileScreen
    routedetail/    -> RouteDetailActivity (AppCompatActivity), RouteDetailViewModel
    run/            -> RunRecorderActivity (AppCompatActivity), RunRecorderViewModel, RunRecorderScreen
    settings/       -> SettingsScreen (funcional), SettingsViewModel (funcional)
```

### Firestore Collections
- `users/{uid}` — perfil do utilizador (inclui `profileImageUrl` como Base64)
- `usernames/{username}` — reserva de usernames únicos
- `posts/{postId}` — publicações (inclui `mediaUrl` como Base64, `runCoordinates`, stats de run)
- `posts/{postId}/likes/{userId}` — subcoleção de likes (toggleLike com Firestore Transaction)
- `posts/{postId}/comments/{commentId}` — subcoleção de comentários

### Como funcionam as imagens (Base64)
> **CRÍTICO — não alterar esta lógica sem ler:**
> O Firebase Storage não está ativado (seria pago). Em vez disso:
> - As imagens são redimensionadas (`compressAndResizeImage`) antes do upload:
>   - Foto de perfil: **max 300px**, qualidade JPEG 60%
>   - Media de post: **max 600px**, qualidade JPEG 60%
> - Guardadas no Firestore como string `data:image/jpeg;base64,...`
> - Exibidas via `Constants.getCoilDataModel(url)` que converte a string Base64 num `ByteArray` antes de passar ao Coil

---

## Mudanças Críticas Desta Fase (Ler Antes de Tocar no Código)

### 1. Todas as Activities são agora `AppCompatActivity`

**Porquê:** `AppCompatDelegate.setApplicationLocales()` (para troca de idioma) só funciona em Activities que herdem de `AppCompatActivity`. Com `ComponentActivity`, a troca de idioma era ignorada completamente e a app crashava ao navegar para ecrãs com Google Maps ou Coil quando o `LocalContext` era substituído.

**Ficheiros afectados:**
- `feature/main/MainActivity.kt` → `AppCompatActivity`
- `feature/auth/AuthActivity.kt` → `AppCompatActivity`
- `feature/run/RunRecorderActivity.kt` → `AppCompatActivity`
- `feature/routedetail/RouteDetailActivity.kt` → `AppCompatActivity`

### 2. Tema XML usa `Theme.AppCompat.DayNight.NoActionBar`

**Porquê:** O `Theme.Material3.DayNight.NoActionBar` não existe no projeto (requer a biblioteca `com.google.android.material` separada). O `Theme.AppCompat.DayNight.NoActionBar` vem com o `androidx.appcompat:1.7.0` já incluído no projeto.

**Ficheiros afectados:**
- `res/values/themes.xml`
- `res/values-night/themes.xml`

### 3. Color.kt — Tokens de cor são agora `@Composable` getters

**Porquê:** Os tokens como `DpBackground`, `DpCard`, `DpTextPrimary` eram antes valores estáticos fixos (`Color(0xFF09090B)`). Isso impedia o tema claro de funcionar porque os ecrãs continuavam com cores escuras independentemente do tema. Agora são getters composable que leem do `MaterialTheme.colorScheme`.

**Regra:** Só podem ser usados dentro de funções `@Composable`. Para uso não-composable (ex: inicialização de ColorScheme em `Theme.kt`), usar as variantes `_Static` (ex: `DpBackground_Static`).

```kotlin
// ✅ Correcto — dentro de @Composable
Column(modifier = Modifier.background(DpBackground)) { ... }

// ❌ Errado — fora de @Composable
val color = DpBackground  // erro de compilação

// ✅ Correcto — para Theme.kt (não-composable)
background = DpBackground_Static
```

### 4. Idioma — Fluxo correcto

O idioma é alterado através do `SettingsViewModel.setLanguage()`:
1. Guarda o idioma no DataStore via `PreferencesManager.setLanguage()`
2. Chama `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))`
3. O `AppCompatActivity` detecta a mudança e recria a Activity automaticamente
4. A nova Activity carrega os resources da locale correcta (`values-pt/`, `values-es/`, etc.)

**Strings localizadas em:** `res/values/strings.xml` (EN), `res/values-pt/strings.xml` (PT), `res/values-es/strings.xml` (ES)

---

## O que Falta Fazer

### Tarefa 1 — HomeScreen (PRIORITÁRIO)

**Ficheiro:** `feature/home/HomeScreen.kt` (stub vazio — só texto "Home")

**O que deve mostrar** (ver `docs/UI_STYLE_GUIDE.md` → secção Home):
```
+-------------------------------------------+
| 🚗  DrivePulse                            |
+-------------------------------------------+
| Olá, @username! 👋                        |
| [Última Run: 24.3 km • 32 min]            |
+-------------------------------------------+
|  [BOTÃO GRANDE] Começar Nova Corrida      |
+-------------------------------------------+
| [Atalho] Ver Mapa  |  [Atalho] Comunidade |
+-------------------------------------------+
| ── As Minhas Corridas Recentes ──          |
| [LazyRow de mini-cards de runs]           |
+-------------------------------------------+
```

**Como implementar:**
1. Criar `HomeViewModel` com Hilt que injeta `PostRepository`, `UserRepository`
2. Expor via `StateFlow`:
   - `userProfile: StateFlow<User?>` — `userRepository.getUserProfile(uid)`
   - `recentPosts: StateFlow<List<Post>>` — `postRepository.getUserPosts(uid)` (últimos 5)
3. Seguir a estrutura Route → Screen → Components definida em `docs/DEVELOPMENT_RULES.md`
4. Usar `DrivePulseButton` para o botão principal
5. Usar `MaterialTheme.colorScheme.*` em vez de `DpBackground` directamente para compatibilidade com tema claro

**Assinaturas já existentes no HomeScreen:**
```kotlin
fun HomeScreen(
    onStartRun: () -> Unit,     // já ligado ao RunRecorderActivity
    onNavigateToMap: () -> Unit  // já existe
)
```

---

### Tarefa 2 — MapScreen (PRIORITÁRIO)

**Ficheiro:** `feature/map/MapScreen.kt` (stub vazio — só texto "Map")

**O que deve mostrar** (ver `docs/UI_STYLE_GUIDE.md` → secção Mapa):
- `GoogleMap` a ocupar o ecrã inteiro
- Localização actual do utilizador (ponto azul — `isMyLocationEnabled = true`)
- Markers nas localizações das runs da comunidade
- Ao clicar num Marker → abrir `RouteDetailActivity` com o `postId`

**Como implementar:**
1. Criar `MapViewModel` com Hilt que injeta `PostRepository`
2. Expor `feedPosts: StateFlow<List<Post>>` — `postRepository.getFeedPosts()`
3. Para cada post com `runCoordinates` não vazio, colocar `Marker` em `runCoordinates.first()`
4. Clicar no Marker → `onPinClick(post.id)`

**Assinatura já existente:**
```kotlin
fun MapScreen(onPinClick: (String) -> Unit)
```

**No `MainNavGraph.kt`:** ligar `onPinClick` para lançar `RouteDetailActivity` via `context.startActivity(Intent(...).putExtra(Constants.EXTRA_ROUTE_ID, postId))`

---

### Tarefa 3 — Comentários no PostCard (SECUNDÁRIO)

**Estado actual:** `onCommentClick = { }` vazio em `CommunityScreen.kt`.

**Como implementar:**
1. Adicionar ao `CommunityViewModel`:
   - `val selectedPostId: StateFlow<String?>`
   - `fun selectPostForComments(postId: String)`
   - `val comments: StateFlow<List<Comment>>`
   - `fun addComment(text: String)`
2. No `CommunityScreen`, mostrar `ModalBottomSheet` quando `selectedPostId != null`:
   - `LazyColumn` de comentários
   - `TextField` + botão "Enviar"

---

## Notas Críticas para o Próximo Agente

> [!IMPORTANT]
> **Base64 + Coil:** NUNCA passar uma string `data:image/jpeg;base64,...` directamente ao `AsyncImage`. Usar sempre `Constants.getCoilDataModel(url)` que converte para `ByteArray`. O Coil carrega `ByteArray` nativamente e sem problemas de limites de textura GPU.

> [!IMPORTANT]
> **Firestore + Ordenação:** Não usar `.orderBy()` no Firestore sem criar o índice composto correspondente na Consola Firebase. Ordenar sempre em memória com `.sortedByDescending { it.createdAt }`.

> [!IMPORTANT]
> **AppCompatActivity:** Todas as Activities DEVEM herdar de `AppCompatActivity` (não `ComponentActivity`). A mudança de idioma via `AppCompatDelegate` e a compatibilidade com `setContent {}` funcionam correctamente com `AppCompatActivity`.

> [!IMPORTANT]
> **Tema XML:** O pai do tema em `res/values/themes.xml` é `Theme.AppCompat.DayNight.NoActionBar`. NÃO alterar para `Theme.Material3.*` — essa biblioteca não está no projeto e causa erro de build AAPT.

> [!IMPORTANT]
> **Color tokens:** `DpBackground`, `DpCard`, `DpTextPrimary`, etc. são `@Composable` getters em `Color.kt`. Só podem ser lidos dentro de funções `@Composable`. Para contextos não-composable (como `Theme.kt`), usar `DpBackground_Static`, `DpCard_Static`, etc.

> [!WARNING]
> O `AuthViewModel` é partilhado entre `LoginScreen` e `RegisterScreen` via parâmetro — NAO usar `hiltViewModel()` sem receber a instância passada pelo `AuthNavGraph`.

> [!TIP]
> Seguir sempre `docs/DEVELOPMENT_RULES.md` e `docs/UI_STYLE_GUIDE.md`. Manter ficheiros Kotlin abaixo de 300 linhas. Strings nunca hardcoded em Composables — usar `stringResource(R.string.*)`.

---

## Como Verificar que Tudo Compila

```powershell
cd D:\CM\CM\Final_Project
.\gradlew compileDebugKotlin 2>&1 | Select-Object -Last 30
```

Deve terminar com `BUILD SUCCESSFUL`.
