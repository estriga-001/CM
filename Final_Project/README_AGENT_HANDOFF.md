# DrivePulse — Technical Agent Handoff

Este documento detalha o estado técnico completo do projeto Android **DrivePulse** (clone do Strava para carros) para permitir a retoma do trabalho num novo agente/sessão, garantindo consistência técnica e arquitetural de forma estrita.

---

## 1. Estado Atual do Projeto

### O que já foi implementado
- **Fase 1 (Base UI & Navegação)**: Setup Gradle, Clean Architecture base, Tema (Jetpack Compose), Navegação base (BottomBar), Strings Multilíngua (PT, EN, ES) e UI em Mock das tabs (Home, Mapa, Comunidade, Perfil, etc).
- **Fase 2 (Auth)**: Arquitetura e UI para Autenticação usando Firebase Auth. Inclui a Domain Layer, Data Layer (AuthRepositoryImpl) e UI Layer (Login/Register/AuthViewModel). A funcionalidade de LogOut está ligada.
- **Fase 3 (Run Tracking & Room)**: Registo GPS da run (Foreground Service, FusedLocationProviderClient) e gravação contínua na base de dados local Room (`DrivePulseDatabase`). HUD com Google Maps e Polyline em tempo real no ecrã `RunRecorderActivity`.

### O que ainda falta implementar
- **Fase 4 (Comunidade & Cloud Sync)**: Funcionalidades de partilha, feed via Firebase Firestore, e sincronização das Runs guardadas no Room para a Cloud. Ecrã detalhado da rota.
- **Fase 5 (Refinamentos)**: Perfil detalhado, premium features, multilinguismo completo dos conteúdos dinâmicos.

### Decisões de Arquitetura
- **Clean Architecture estrita**: O projeto está perfeitamente dividido em `ui` (presentation), `domain` (business logic pura, sem dependências Android) e `data` (Room, Firebase).
- **MVI/MVVM com StateFlow**: Toda a UI reage a estados fechados (`sealed interface`) expostos via `StateFlow` pelos ViewModels. 
- **Sem lógica em Composables**: Toda a injeção de dependências usa Hilt (`@HiltViewModel`, `@AndroidEntryPoint`). A lógica de negócio reside apenas nos UseCases.
- **Navegação**: Navigation Compose. O fluxo autenticado vs não-autenticado é gerido dinamicamente. `RunRecorderActivity` existe numa Activity separada para permitir isolamento no Foreground Service.

### O que está mockado vs. ligado
- **Ligado**: Firebase Auth (Login/Registo reais), Foreground Service para GPS Real/Emulado, Base de Dados Local Room (Guardar Runs e Coordenadas). O ficheiro `google-services.json` já é **o real e funcional**.
- **Mockado**: As tabs da BottomNav (Home, Map, Community, Profile) ainda contêm apenas placeholders estáticos. O envio das runs para a Cloud (Firestore) ainda não existe.

---

## 2. Estrutura Atual de Ficheiros

- `com.drivepulse.core` -> Utilitários transversais.
  - `.common` -> Constants, Results.
  - `.designsystem` -> Theme, Components (DrivePulseButton, TopBar, etc).
  - `.location` -> `LocationTracker` (interface) e `FusedLocationTracker`.
- `com.drivepulse.data` -> Camada de Dados.
  - `.local.database` -> Entidades do Room (`RunEntity`, `CoordinateEntity`) e `DrivePulseDatabase`.
  - `.local.dao` -> DAOs do Room para acessos SQL.
  - `.repository` -> Implementações (`AuthRepositoryImpl`, `RunRepositoryImpl`).
  - `.di` -> Módulos Hilt (`DataModule`, `LocationModule`).
- `com.drivepulse.domain` -> Camada de Negócio.
  - `.model` -> Modelos puros (`User`, `Run`, `Coordinate`).
  - `.repository` -> Interfaces (`AuthRepository`, `RunRepository`).
  - `.usecase` -> `StartRunUseCase`, `SaveCoordinateUseCase`, etc.
- `com.drivepulse.feature.*` -> UI por feature.
  - `.auth` -> AuthActivity, AuthViewModel, LoginScreen, RegisterScreen.
  - `.main` -> MainActivity (hospedeiro da BottomBar).
  - `.run` -> RunRecorderActivity, ViewModel, ecrã HUD do mapa.
  - Outras pastas mock: `home`, `map`, `community`, `profile`.

---

## 3. Ficheiros Importantes Criados/Alterados Recentes

| Caminho / Ficheiro | Objetivo / Estado Atual | Dependências / Detalhes |
|---|---|---|
| `data/repository/RunRepositoryImpl.kt` | Gravação das runs no Room. | Mapeamento Flow -> Domain. Utiliza `first()` idiomático no encerramento da run. |
| `core/location/TrackingForegroundService.kt` | Rastreio GPS ativo mesmo com a app minimizada. | Depende de `FusedLocationTracker`. Expõe um `SharedFlow` estático para a UI. |
| `feature/run/screens/RunRecorderScreen.kt` | HUD de condução com mapa, timer e stats. | Depende de Maps Compose. **Nota**: Evitar o uso de `AnimatedVisibility` aninhado dentro de Box/Column (erro conhecido de resolução do Compose). |
| `data/di/LocationModule.kt` | Injeção do FusedLocation. | `ApplicationContext` injetado como `@Singleton`. |
| `settings.gradle.kts` | Setup de repositórios base. | Adicionado o plugin `foojay-resolver-convention` para resolver problemas de download do JDK/Jlink automaticamente. |

---

## 4. Configuração do Projeto

- **Versões Base**: Kotlin `~1.9.0`, Compose Bom `2024.x`, Gradle AGP `8.x`.
- **Dependências Chave**: Firebase (Auth, Firestore, BoM), Room Database (com suporte Coroutines), Google Maps Compose, Hilt (Dagger).
- **Permissões (AndroidManifest)**: `INTERNET`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`.
- **Configuração Externa**: 
  - `google-services.json` já inclui as chaves válidas na pasta `/app`.
  - É necessário garantir que o ficheiro `local.properties` contém a chave: `MAPS_API_KEY=YOUR_API_KEY`.

---

## 5. Como Correr e Testar o Projeto

1. **No Android Studio**: Sincronizar o projeto (Sync Project with Gradle Files). Se pedir para atualizar o JDK, confirmar a ação (o plugin `foojay` fará o trabalho silenciosamente).
2. **Build**: Executar `Run -> app`.
3. **O que testar primeiro**:
   - **Auth**: Criar uma conta no ecrã de registo. Fazer Logout no Profile, e Login novamente.
   - **GPS Tracking**: Clicar no botão Central da BottomBar. Conceder permissão de localização. Clicar em **Start**. 
   - Usar os **Extended Controls do Emulador -> Location** para definir uma rota. Verificar se a linha vermelha é desenhada no mapa e os KM/Tempo aumentam.

**Possíveis Erros e Soluções:**
- *Erro de compilação Jlink/JDK*: Garantir que o Android Studio está configurado para usar o seu JBR 17/21 (Settings -> Build -> Gradle -> Gradle JDK) em vez de um JRE externo.
- *Erro de Maps branco*: Falta de configuração da `MAPS_API_KEY` válida no `local.properties`.

---

## 6. Funcionalidades Implementadas

- ✅ **Auth**: Login, Registo, Logout. Firebase configurado. Guest mode funcional.
- 🟡 **Home**: Placeholder de UI UI.
- 🟡 **Mapa**: Placeholder UI.
- ✅ **Run tracking**: 100% Funcional offline. Mapas, Cronómetro, Velocidade, Polyline desenhada, e guardar as sessões no Room (`DrivePulseDatabase`).
- 🟡 **Criar post**: Não iniciado.
- 🟡 **Comunidade**: Placeholder UI.
- 🟡 **Perfil**: Apenas Logout funcional, restante UI em mockup.
- 🟡 **Premium / Settings / Help / About**: Placeholders criados.
- ✅ **Multilingue**: Estrutura de resources XML (pt, en, es) montada e a funcionar para os ecrãs base.

---

## 7. Bugs / Problemas Atuais

- Sem erros de compilação ou crashes conhecidos no fluxo principal.
- **Aviso Técnico**: As rotas gravadas estão apenas guardadas no Room localmente. Fechar a app destrói a gravação atual se o `ForegroundService` for morto pelo sistema de forma agressiva (Android 14+ strict mode não foi totalmente otimizado).
- **Dúvida pendente**: Se a tab Comunidade será um feed linear simples ou incluirá sistema complexo de comentários (a avaliar na Fase 4).

---

## 8. Próximas Tarefas Recomendadas (Por Prioridade)

1. **Fase 4 - Firestore Remote Data Source**: Criar as funções no repositório para converter as `RunEntity` do Room em documentos do Firebase Firestore e publicá-las.
2. **Sincronização de Runs**: Acionar o upload automático/manual de uma Run para a cloud após a gravação.
3. **Tab Comunidade (Feed)**: Desenvolver o ViewModel para escutar as Runs globais no Firestore e exibi-las no ecrã da comunidade (listar cartões com mapa em miniatura e dados da run).
4. **Tab Perfil (Detalhes)**: Apresentar o histórico pessoal de corridas (lendo do Room ou do Firestore).

---

## 9. Regras para o Próximo Agente

O próximo agente **deve rigorosamente**:
- **Manter arquitetura limpa**: NENHUM código de UI deve aceder a DAOs ou Firebase diretamente. Usar sempre ViewModels e UseCases.
- **Não refazer ficheiros sem necessidade**: Confiar no código já testado, a menos que haja um bug claro.
- **Não mudar nomes de packages** sem a autorização do utilizador.
- **Não misturar lógica Firebase nos Composables**: O Compose apenas consome Data Classes puras e invoca lambdas de eventos.
- **Não colocar secrets no código**: Usar sempre `local.properties` para chaves.
- **Implementar UMA feature de cada vez**: Evitar grandes PRs/refactors globais em simultâneo.
- **Explicar alterações antes de agir**: Apresentar o Implementation Plan de forma clara.
- **No fim de cada etapa**, fazer um resumo de: "O que foi criado, o que foi testado, o que falta".

---

## 10. Prompt de Continuação

*Copia e cola o texto abaixo num novo chat para continuar o desenvolvimento:*

```text
Olá! Lê atentamente o ficheiro README_AGENT_HANDOFF.md na raiz do projeto. 
Acabámos de concluir a Fase 3 com sucesso (Auth, Room DB, e GPS Tracking no Foreground Service estão a funcionar perfeitamente em local).

O nosso próximo objetivo é arrancar com a **Fase 4 (Comunidade & Cloud Sync)**.
A tua primeira tarefa, antes de qualquer código de UI, é:
1. Criar a camada remota (Firestore DataSource) para publicar e ler as 'Runs' guardadas.
2. Integrar a sincronização na arquitetura existente (Repository -> Firestore).

Apresenta o teu plano de implementação detalhado, respeitando as regras do Handoff. Aguardo o plano!
```
