# DrivePulse — Handoff Fase 10

> **Para o próximo agente:** Lê este documento do início ao fim antes de tocar num único ficheiro.
> O projeto compila com **BUILD SUCCESSFUL**. O ambiente Windows requer `org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr` no `gradle.properties` — **não remover esta linha**.

---

## Estado Atual da App (O que funciona)

| Feature | Estado |
|---|---|
| Registo com Email/Password | ✅ Funcional |
| Login com Email/Password | ✅ Funcional |
| Google Sign-In | ✅ Funcional |
| Auto-Login (sessão persistida) | ✅ Funcional |
| Onboarding (username, nome, carro) | ✅ Funcional |
| Modo Convidado | ✅ Funcional |
| GPS Run Tracking | ✅ Funcional |
| Publicação de Run para comunidade | ✅ Funcional (mas limitado — ver bugs) |
| Feed da Comunidade | ⚠️ Funcional mas incompleto |
| Perfil (ver dados) | ✅ Funcional |
| Editar Perfil (texto) | ✅ Funcional |
| Foto de Perfil | ❌ Bug — não funciona (tamanho) |
| Settings | ⚠️ Ecrã existe mas sem funcionalidade real |
| Runs no Perfil do User | ❌ Não implementado |
| Likes / Comentários nas Runs | ❌ Não implementado |
| Detalhes da Run (percurso) | ❌ Não acessível a partir do feed/perfil |
| Publicação com Descrição + Foto/Vídeo | ❌ Não implementado |

---

## Arquitectura do Projeto

```
app/
  data/
    local/         -> Room DB (runs, coordinates)
    remote/        -> Firestore + Storage
      dto/         -> PostDto.kt, UserDto.kt (mappers .toDomain() e .toDto())
    repository/    -> AuthRepositoryImpl, UserRepositoryImpl, RunRepositoryImpl
    di/            -> DataModule (Hilt)
  domain/
    model/         -> User, Run, Post, Coordinate, RunStatus, MediaType
    repository/    -> AuthRepository, UserRepository, RunRepository (interfaces)
    usecase/       -> Pasta auth/, profile/, run/
  feature/
    auth/          -> AuthActivity, AuthNavGraph, AuthViewModel, LoginScreen, RegisterScreen
    community/     -> CommunityScreen, CommunityViewModel, RunCard
    createpost/    -> CreatePostScreen (existente mas stub)
    home/          -> HomeScreen
    profile/       -> ProfileScreen, ProfileViewModel, EditProfileScreen, ProfileSetupScreen, ProfileSetupViewModel
    run/           -> RunRecorderActivity, RunRecorderViewModel, RunRecorderScreen
    routedetail/   -> RouteDetailActivity
    settings/      -> SettingsScreen (stub)
```

### Firestore Collections
- `users/{uid}` — perfil do utilizador
- `usernames/{username}` — reserva de usernames únicos
- `runs/{runId}` — runs publicadas (sistema antigo, migrar para posts)
- `posts/{postId}` — **modelo criado, ainda NAO populado** (Post.kt + PostDto.kt ja existem)

### Modelos ja criados mas nao ligados
- `Post.kt` (domain) e `PostDto.kt` (data) — tem os campos: `userId`, `username`, `userProfileImage`, `description`, `runId`, `mediaUrl`, `mediaType`, `likesCount`, `commentsCount`

---

## Bugs a Corrigir

### Bug 1 — Foto de Perfil (Prioridade Alta)
**Ficheiro:** `UserRepositoryImpl.kt` -> `uploadProfileImage()`

**Causa:** O Firebase Storage tem limites e as imagens da galeria podem ter varios MB sem compressao.

**Solucao:** Comprimir antes de fazer upload:
```kotlin
val compressedBytes = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    ?.let { bitmap ->
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        outputStream.toByteArray()
    } ?: imageBytes
imageRef.putBytes(compressedBytes).await()
```

**Cache busting no Coil:** Nos ecras `ProfileScreen` e `EditProfileScreen`, usar:
```kotlin
AsyncImage(
    model = ImageRequest.Builder(context)
        .data(user.profileImageUrl)
        .memoryCacheKey("${user.profileImageUrl}_${user.updatedAt}")
        .diskCachePolicy(CachePolicy.DISABLED)
        .build(), ...
)
```

---

### Bug 2 — Runs nao identificadas na Comunidade (Prioridade Alta)
**Causa:** O `RunCard` mostra o titulo e data mas nao mostra quem fez a run. O `Run` domain model tem `userId` mas o card nao busca o perfil.

**Solucao:** Migrar do sistema `runs/` para o sistema `posts/` que ja tem os dados denormalizados do utilizador. Ver Tarefa 1 abaixo.

---

### Bug 3 — Settings sem funcionalidade (Prioridade Media)
**Ficheiro:** `SettingsScreen.kt` (stub vazio)

**O que deve incluir:**
- Idioma (pt, en, es) — guardar em DataStore
- Tema Escuro/Claro — guardar em DataStore
- Conta (Logout, alterar Password)

---

## Tarefas para a Fase 10

### Tarefa 1 — Sistema de Posts (CRITICO)

**1.1 — Criar `PostRepository` interface:**
```kotlin
interface PostRepository {
    fun getFeedPosts(): Flow<List<Post>>
    suspend fun createPost(post: Post, mediaBytes: ByteArray?): AppResult<Unit>
    suspend fun toggleLike(postId: String, userId: String): AppResult<Unit>
    suspend fun addComment(postId: String, userId: String, username: String, text: String): AppResult<Unit>
    fun getComments(postId: String): Flow<List<Comment>>
    fun getUserPosts(userId: String): Flow<List<Post>>
}
```

**1.2 — Estrutura Firestore:**
- `posts/` — colecao principal
- `posts/{postId}/likes/{userId}` — sub-colecao (1 doc por like, ID = uid)
- `posts/{postId}/comments/{commentId}` — sub-colecao

**1.3 — toggleLike com Transaction:**
```kotlin
firestore.runTransaction { transaction ->
    val likeRef = postsCollection.document(postId).collection("likes").document(userId)
    val postRef = postsCollection.document(postId)
    val likeSnap = transaction.get(likeRef)
    if (likeSnap.exists()) {
        transaction.delete(likeRef)
        transaction.update(postRef, "likesCount", FieldValue.increment(-1))
    } else {
        transaction.set(likeRef, mapOf("uid" to userId, "createdAt" to System.currentTimeMillis()))
        transaction.update(postRef, "likesCount", FieldValue.increment(1))
    }
}
```

---

### Tarefa 2 — Publicacao com Descricao + Foto/Video

**Ficheiro:** `CreatePostScreen.kt` (stub, ja existe)

O ecra deve mostrar:
- Preview mini do percurso da run (GoogleMap em miniatura com a polyline)
- Estatisticas da run (distancia, tempo, velocidade)
- Campo de texto para Descricao
- Botao "Adicionar Foto/Video" -> `ActivityResultContracts.PickVisualMedia()`
- Preview da media selecionada
- Botao "Publicar"

**Fluxo apos terminar uma run:**
1. `RunRecorderActivity` mostra um dialogo/bottom sheet com as opcoes "Publicar" ou "Guardar privado"
2. Se "Publicar" -> abrir `CreatePostScreen` passando o `runId`
3. O utilizador preenche e carrega em Publicar
4. Upload da media para `Storage posts_media/{userId}/{uuid}.jpg`
5. Criar documento `PostDto` no Firestore com todos os dados

---

### Tarefa 3 — PostCard com Author + Likes + Comentarios

Substituir `RunCard` por um `PostCard` completo:

```
+-------------------------------------------+
| [Avatar] @username              14 min ago |
+-------------------------------------------+
| "Descida pelo Sintra-Cascais!"             |
+-------------------------------------------+
| [Mini Mapa com percurso GPS]               |
+-------------------------------------------+
| [Foto/Video se existir]                    |
+-------------------------------------------+
|  24.3 km    32:14    45.2 km/h             |
+-------------------------------------------+
| [Coração] 12   [Comentario] 4   [->]       |
+-------------------------------------------+
```

- Clicar no card -> abrir detalhe da run (`RouteDetailActivity` com `runId`)
- Clicar coracao -> `toggleLike()`, animacao de escala
- Clicar comentario -> abrir `ModalBottomSheet` com lista de comentarios + campo de texto

---

### Tarefa 4 — Runs no Perfil do Utilizador

**`ProfileScreen.kt`** — adicionar seccao "As minhas corridas":
- Chamar `PostRepository.getUserPosts(currentUserId)` para runs publicadas
- Chamar `RunRepository.getRunsByUser(userId)` para runs locais privadas
- Mostrar `LazyRow` de mini-cards com mini-mapa e stats
- Clicar -> abrir detalhe com percurso completo

---

### Tarefa 5 — Settings funcional

**DataStore:**
```kotlin
val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
val LANGUAGE_KEY = stringPreferencesKey("language") // "pt", "en", "es"
```

**Tema dinamico na MainActivity:**
```kotlin
val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
DrivePulseTheme(darkTheme = isDarkTheme) { ... }
```

**Idioma:**
```kotlin
AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
```

---

### Tarefa 6 — Criar modelo Comment

```kotlin
// domain/model/Comment.kt
data class Comment(
    val id: String,
    val postId: String,
    val userId: String,
    val username: String,
    val userProfileImage: String?,
    val text: String,
    val createdAt: Long
)
```

E o correspondente `CommentDto.kt` na camada data.

---

## Notas Criticas para o Proximo Agente

> [!IMPORTANT]
> O `AuthViewModel` e partilhado entre `LoginScreen` e `RegisterScreen` via parametro — NAO usar `hiltViewModel()` sem receber a instancia passada pelo `AuthNavGraph`.

> [!IMPORTANT]
> `SessionRestored` vs `Success` em `AuthState`: `SessionRestored` e emitido no arranque pelo `checkSession()`. `Success` e emitido apos login/registo manual. O `LoginScreen` so reage a `Success`.

> [!WARNING]
> As Firestore Security Rules permitem tudo ate Julho 2026. Quando implementares likes, a regra de `likes` deve verificar `request.auth.uid == request.resource.data.uid`.

> [!TIP]
> Manter ficheiros Kotlin abaixo de 300 linhas. Usar apenas cores do `DrivePulseTheme` — nunca hardcoded.
