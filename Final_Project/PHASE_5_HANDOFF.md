# DrivePulse - Agent Handoff (Fase 5 Concluída)
**Data:** 28 de Maio de 2026

Este documento reflete o estado rigoroso da aplicação no fim da **Fase 5**. Serve como ponte arquitetural para a Fase 6, garantindo que o próximo agente retoma com contexto 100% alinhado com o Master Roadmap.

---

## 1. O Que Foi Feito Hoje (Fase 5)
A fundação de UI e proteção de rotas está completa:
1. **Design System & Theme**:
   - `Color.kt` refatorado para usar os hexadecimais rígidos exigidos pelo projeto (`#09090B` para background, `#E50914` para vermelho primário, etc).
   - O `Theme.kt` força sempre o `DrivePulseDarkColorScheme` como Default Theme (Dark Mode first and only).
2. **Navegação Principal**:
   - A `DrivePulseBottomBar` foi confirmada com a ordem estrita exigida: *Home*, *Mapa*, *[+ Run]*, *Comunidade*, *Perfil*.
   - O botão `+ Run` foi centralizado com destaque flutuante (CircleShape e DpPrimaryRed).
3. **Modo Convidado & AuthGate**:
   - Criação do `SessionMode.kt` e injetado globalmente na árvore do Compose via `LocalSessionMode`.
   - Criação do `AuthGate.kt`: Um Composable de interceção que exibe um `AlertDialog` elegante se um Guest tentar invocar uma ação protegida.
   - Aplicação do `AuthGate` na `MainActivity`, protegendo o acesso ao ecrã de `RunRecorderActivity`.

---

## 2. Estado Atual do Projeto

### Arquitetura & Decisões
- **Clean Architecture** (Core, Data, Domain, Feature).
- **Injeção de Dependências**: Hilt em uso (Activities com `@AndroidEntryPoint`, ViewModels com `@HiltViewModel`).
- **Navegação**: Jetpack Compose Navigation (`MainNavGraph.kt`).
- **Persistência**: Room para Local Cache (Runs) e Firebase Firestore para a Nuvem.
- **Sessão**: `AuthRepository` ligado ao Firebase Authentication. Sessão atual é lida na `MainActivity` via Intent e providenciada como `LocalSessionMode`.

### O Que Já Está Funcional
- App abre, navegação da tab bar funciona (embora as tabs em si ainda tenham UI muito básica/mock).
- Ação `+ Run` devidamente protegida se o user for Guest (aparece Dialog). Se logado, abre a `RunRecorderActivity`.
- A `RunRecorderActivity` captura GPS, guarda no Room e envia para o Firestore de forma sincronizada.

---

## 3. O Que Falta Fazer (Roadmap)
Baseado no documento completo de especificações do utilizador:
- Criação completa do documento Firestore do utilizador (`users/{uid}`) na altura do Registo.
- Implementação detalhada do **Perfil**, incluindo o gerador MVP de avatar do Carro.
- Dashboard completo na **Home** com acesso à **Open-Meteo API** (`WeatherRepository`).
- O **Mapa Globo** com as 11 categorias de `mapPins`, filtros de UI e paginations.
- Upgrade à captura de **Run** (gravar "warnings"/perigos durante a viagem).
- Ecrã novo de **CreatePostActivity/RouteDetailActivity** (publicação de fotos, vídeos, e avisos com upload para Storage).
- Hub Social na **Comunidade** com Likes, Comentários, Paginação e Cache no Room.
- **Funcionalidade Premium** (Clubs, Events).
- Internacionalização (Multilingue PT, EN, ES).

---

## 4. Instruções para a Próxima Fase (Fase 6)
**Objetivo da Fase 6:** User Identity & Firebase Schema
O foco deverá ser alargar a estrutura de autenticação e perfis de utilizador.

### Tarefas Sugeridas para o próximo Agente:
1. **Firestore Schema para Users**: 
   - No `AuthRepositoryImpl`, no método `register()`, após criar o utilizador no Firebase Auth, criar imediatamente o documento em `users/{uid}` com a estrutura base exigida (bio, totalKm, friendsCount=0, etc).
2. **Atualização do Domain Model (User)**:
   - Atualizar a class `User.kt` para suportar as dezenas de campos exigidos (`selectedCarBrand`, `selectedCarModel`, `followingCount`, etc).
3. **Ecrã de Criação de Perfil**:
   - Um ecrã ou fluxo (após o registo) para permitir escolher o Carro.
4. **Gerador de Avatar do Carro (MVP)**:
   - Implementar uma factory estática local que devolve um ID de `Drawable` com base na combinação escolhida (Marca/Modelo/Cor), para mostrar no UI.

> **Regra Crucial para o Próximo Agente**: Não alterar as definições visuais da `Color.kt` e respeitar a `LocalSessionMode` para proteger as próximas features que exijam login (ex: Follow User, Upload Photo).
