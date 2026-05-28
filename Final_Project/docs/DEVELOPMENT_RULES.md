# Regras de desenvolvimento

## Organização

Cada feature deve ter:

```text
FeatureScreen.kt
FeatureViewModel.kt
FeatureUiState.kt
FeatureUiEvent.kt
components/
```

Cada feature com dados deve ter:

```text
domain/model
domain/repository
domain/usecase
data/repository
data/local
data/remote
data/mapper
```

## Documentação de código

No topo de cada ficheiro:

```kotlin
/**
 * Ficheiro responsável por ...
 *
 * Camada: UI/Data/Domain/Core
 * Feature: Auth/Run/Map/etc.
 */
```

Em classes públicas:

```kotlin
/**
 * ViewModel responsável por gerir o estado do ecrã de login.
 *
 * @property loginUseCase caso de uso que autentica o utilizador.
 */
```

Em funções públicas:

```kotlin
/**
 * Inicia uma nova run para o utilizador autenticado.
 *
 * @param privacyMode visibilidade inicial da run.
 * @return identificador da run criada localmente.
 */
```

## Convenções Kotlin

- Classes: `PascalCase`.
- Funções e variáveis: `camelCase`.
- Constantes: `UPPER_SNAKE_CASE`.
- Evitar abreviações.
- Preferir `val`.
- Usar `data class` para estados/modelos.
- Usar `sealed interface` para eventos e resultados.
- Evitar nullable sem necessidade.
- Não usar `!!`.

## ViewModel

Obrigatório:

- expor `StateFlow<UiState>`;
- receber eventos por função `onEvent`;
- usar use cases;
- não chamar Firebase diretamente;
- não guardar `Context`, exceto quando absolutamente necessário via abstração.

Exemplo:

```kotlin
fun onEvent(event: LoginUiEvent) {
    when (event) {
        is LoginUiEvent.EmailChanged -> updateEmail(event.email)
        is LoginUiEvent.Submit -> login()
    }
}
```

## Compose

Composables devem ser divididos em:

```text
Route -> liga ViewModel ao ecrã
Screen -> recebe state e callbacks
Componentes -> partes pequenas reutilizáveis
```

Exemplo:

```kotlin
@Composable
fun LoginRoute(viewModel: LoginViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LoginScreen(
        state = state,
        onEvent = viewModel::onEvent
    )
}
```

## Repositories

Um repository deve coordenar:

- local data source;
- remote data source;
- mappers;
- sync status;
- tratamento de erros.

Não deve devolver DTOs à domain/UI layer.

## Firestore

Regras:

- nunca fazer queries sem limite;
- usar paginação;
- usar índices;
- contadores agregados para likes/comments/saves;
- evitar arrays gigantes em documentos;
- media sempre em Storage.

## Room

Regras:

- route points podem crescer muito;
- inserir pontos em batch quando possível;
- apagar drafts antigos com confirmação;
- criar migrations quando alterar schema;
- usar `Flow` nos DAOs para dados observáveis.

## WorkManager

Usar para:

- upload de media;
- sincronização de runs;
- sincronização de posts;
- retry quando offline;
- limpeza de cache antiga.

Não usar WorkManager para tracking em tempo real.

## Foreground Service

Usar para:

- tracking GPS durante run;
- mostrar notificação persistente;
- permitir parar a run pela notificação.

Regras:

- pedir permissões antes;
- declarar service type location;
- parar service ao terminar;
- persistir estado de run para recuperação.

## Segurança

- Nunca guardar API keys no Git.
- Nunca guardar passwords.
- Usar Firebase Auth.
- Validar ownership no client e nas rules.
- Remover dados sensíveis de logs.
- Não guardar matrícula por defeito.
- Não mostrar localização em tempo real fora de eventos aceites.
- Permitir apagar dados próprios.

## Privacidade

A run deve permitir visibilidade:

```text
PRIVATE
FRIENDS
PUBLIC
```

Default recomendado: `PRIVATE` ou perguntar ao utilizador.

## Qualidade mínima antes de merge

Checklist:

- compila;
- sem warnings graves;
- sem credenciais;
- estados loading/error/success;
- strings não hardcoded;
- função pública documentada;
- não existe lógica de negócio em Composables;
- PR descreve ficheiros alterados;
- pelo menos teste simples se houver lógica.

## Ordem de implementação sugerida

1. Criar projeto e tema.
2. Navegação base e bottom bar.
3. Auth com Firebase.
4. Guest mode.
5. Firestore user profile.
6. Room base.
7. Home com dados simulados.
8. Mapa com pins simulados.
9. Run tracking local.
10. Publicar run.
11. Comunidade/feed.
12. Perfil/carro/avatar.
13. Premium eventos.
14. Multilíngua/help/about/settings.
15. Testes e relatório.
