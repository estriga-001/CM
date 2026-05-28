# DrivePulse — Handoff Técnico: Fase 6 Concluída
**Data:** 28 de Maio de 2026  
**Build:** ✅ SUCCESSFUL — sem erros de compilação

---

## 1. O Que Foi Implementado na Fase 6

### Google Sign-In (AndroidX Credential Manager)
- **Abordagem:** AndroidX Credential Manager (padrão moderno, *future-proof* para Passkeys).
- **LoginScreen** tem agora o botão "Continuar com Google" com feedback visual correto:
  - Loading indicator enquanto processa
  - Mensagens de erro visíveis ao utilizador (sem falhas silenciosas)
  - Cancelamento tratado silenciosamente
- `GoogleSignInUseCase` criado na Domain Layer.
- `AuthViewModel.googleSignIn(idToken)` adicionado.

### Firestore User Document (Hook no Registo)
- `AuthRepositoryImpl` recebe agora o `FirebaseFirestore` via DI.
- Método `ensureUserDocument(firebaseUser)` é chamado **sempre** após login/registo.
- Se o documento `users/{uid}` ainda não existir → cria-o com os campos base (zeros, timestamps, etc).
- Funciona para **Email/Password** e **Google Sign-In**.

### Domain Model Expandido
- `User.kt` agora tem todos os campos sociais:
  - `displayName`, `bio`, `selectedCarBrand`, `selectedCarModel`, `selectedCarYear`
  - `totalKm`, `totalRuns`, `followersCount`, `followingCount`, `friendsCount`
  - `createdAt`, `updatedAt`, `isPremium`, `generatedCarImageUrl`
- `UserDto.kt` criado com mappers bidireccionais (`toDomain()` / `toDto()`).

### Ecrã de Setup do Carro (Onboarding MVP)
- **Fluxo:** `LOGIN → (Registo Email) → SETUP → MAIN`
- `ProfileSetupScreen.kt` — formulário para Marca, Modelo e Ano.
- `ProfileSetupViewModel.kt` — salva no Firestore e aceita "Saltar por agora".
- `CarAvatarGenerator.kt` — factory local (placeholder MVP; substitui por API de IA futuramente).
- `AuthNavGraph.kt` — rota `SETUP` injetada antes do salto para `MAIN`.

---

## 2. Problema Atual — Google Sign-In Não Abre o Seletor de Conta

### Diagnóstico
O botão "Continuar com Google" não funcionava porque o `google-services.json` **não tem o Android OAuth client** (SHA-1 fingerprint). O ficheiro atual só tem `client_type: 3` (Web client), faltando o `client_type: 1` (Android client).

O utilizador via um ecrã em branco porque o erro era capturado silenciosamente. Isso já está corrigido — agora mostra a mensagem de erro.

### Solução Necessária (Ação Manual — 5 min)

**1. Vai à Firebase Console:**  
👉 https://console.firebase.google.com/project/drivepulse-32328/settings/general/android:com.drivepulse

**2. Na secção "Impressões digitais do certificado SHA", clica em "Adicionar impressão digital".**

**3. Cola a tua SHA-1 de debug:**
```
BA:6F:01:F6:B2:F8:4E:26:F4:43:6E:71:E4:AE:0C:6C:97:34:D9:BB
```

**4. Clica em "Guardar".**

**5. Faz download do novo `google-services.json`** e substitui o que está em `app/google-services.json`.

**6. Rebuild o projeto** (Gradle Sync + Run).

Após isto, ao clicar em "Continuar com Google", o seletor de conta nativo do Android vai aparecer normalmente.

---

## 3. Estado Atual do Projeto (Completo)

| Fase | Estado | Notas |
|------|--------|-------|
| Fase 1 — UI & Navegação | ✅ Completo | Bottom Bar, NavGraph, 5 tabs |
| Fase 2 — Autenticação | ✅ Completo | Firebase Auth (Email + Google) |
| Fase 3 — GPS & Tracking | ✅ Completo | Foreground Service, Room, Polyline |
| Fase 4 — Cloud Sync | ✅ Completo | Firestore Runs, Community Feed |
| Fase 5 — Design System | ✅ Completo | Tema Black/Red, AuthGate, BottomBar |
| Fase 6 — User Identity | ✅ Completo* | *Google Sign-In requer SHA-1 no Firebase |

---

## 4. O Que Ainda Falta (Roadmap Completo)

### Fase 7 — Profile Screen
- Ecrã de perfil completo com foto, bio, carro, e estatísticas.
- Edição do perfil (nome, bio, escolha de carro).
- Upload de foto de perfil para Firebase Storage.
- Exibir avatar do carro gerado.

### Fase 8 — Home Dashboard
- Dashboard da Home com dados pessoais do utilizador.
- Integração com **Open-Meteo API** (`WeatherRepository`) para clima atual.
- Últimas runs do utilizador em cards.
- Atalho rápido para iniciar nova run.

### Fase 9 — Mapa Global
- Mapa com marcadores para 11 categorias de `mapPins` (road, curves, coast, mountain, night, photo, cafe, meeting, event, warning, view).
- Filtros de UI por categoria.
- Paginação de pins do Firestore.
- Criar pin no mapa.

### Fase 10 — CreatePost & RouteDetail
- `CreatePostActivity` — publicar run com fotos/vídeos.
- Upload de media para Firebase Storage.
- Avisos/perigos na rota.
- `RouteDetailActivity` — ver detalhes de uma run publicada.

### Fase 11 — Community Hub
- Likes nos posts da comunidade.
- Comentários em tempo real.
- Paginação do feed com cache Room.
- Follow/Unfollow de utilizadores.

### Fase 12 — Premium & Events
- Sistema de Clubs (criar, entrar, administrar).
- Events automóveis.
- Features premium (badge, estatísticas avançadas).

### Fase 13 — Internacionalização
- Strings multilingue (PT, EN, ES) — base já criada nas `strings.xml`.
- Selector de idioma nas Definições.

---

## 5. Estrutura de Ficheiros Relevante

```
app/src/main/java/com/drivepulse/
├── core/
│   ├── common/
│   │   ├── SessionMode.kt           ← GUEST/AUTHENTICATED + LocalSessionMode
│   │   ├── CarAvatarGenerator.kt    ← MVP Avatar factory
│   │   └── components/
│   │       └── AuthGate.kt          ← Intercepta ações protegidas para Guests
│   ├── designsystem/
│   │   ├── theme/Color.kt           ← Paleta Black/Red definitiva
│   │   └── components/DrivePulseBottomBar.kt ← 5 tabs com FAB central
│   └── navigation/
│       └── MainNavGraph.kt          ← Todas as rotas principais
├── data/
│   ├── remote/dto/
│   │   ├── UserDto.kt               ← DTO completo do utilizador
│   │   └── RunDto.kt
│   ├── repository/
│   │   └── AuthRepositoryImpl.kt    ← ensureUserDocument() aqui
│   └── di/DataModule.kt             ← Injeção com FirebaseFirestore
├── domain/
│   ├── model/User.kt                ← Modelo expandido (30+ campos)
│   └── usecase/auth/
│       ├── LoginUseCase.kt
│       ├── RegisterUseCase.kt
│       └── GoogleSignInUseCase.kt   ← NOVO
└── feature/
    ├── auth/
    │   ├── AuthNavGraph.kt          ← LOGIN → REGISTER → SETUP → MAIN
    │   ├── AuthViewModel.kt         ← googleSignIn() adicionado
    │   └── screens/LoginScreen.kt   ← Botão Google + erros visíveis
    └── profile/
        ├── ProfileSetupScreen.kt    ← NOVO — Onboarding do carro
        └── ProfileSetupViewModel.kt ← NOVO — Guarda no Firestore
```

---

## 6. Configurações Necessárias para Correr

### local.properties
```properties
MAPS_API_KEY=<tua_chave_google_maps>
```

### Firebase Console (Ação Obrigatória para Google Sign-In)
- SHA-1 Debug: `BA:6F:01:F6:B2:F8:4E:26:F4:43:6E:71:E4:AE:0C:6C:97:34:D9:BB`
- Adicionar em: Firebase Console → Project Settings → App Android → Fingerprints

### Google Services (Authentication)
- Ativar no Firebase Console: **Authentication → Sign-in method → Google → Enable**
- O Web client ID é gerado automaticamente e inserido no `google-services.json`.

---

## 7. Regras Arquiteturais para o Próximo Agente

1. **NÃO alterar** `Color.kt` — a paleta `#09090B`/`#E50914` está fechada.
2. **SEMPRE** chamar `ensureUserDocument()` ou equivalente após qualquer forma de auth.
3. **USAR** `LocalSessionMode` (via `CompositionLocalProvider`) para proteger novas features com `AuthGate`.
4. **RESPEITAR** o fluxo de navegação: o NavGraph de Auth tem 3 destinos: `LOGIN`, `REGISTER`, `SETUP`.
5. **PRÓXIMO PASSO RECOMENDADO:** Fase 7 — Profile Screen.
