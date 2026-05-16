# GearShare — Application Concept Document (ACD)
### Computação Móvel · ENIDH · Fase 1: Conceito · 2025/2026

---

## 1. Nome e Conceito Geral

**Nome da Aplicação:** GearShare

**Tagline:** *"Every road tells a story. Share yours."*

**Plataforma:** Android (API 26+)  
**Tecnologias Core:** Kotlin · Firebase (Auth, Firestore, Storage) · Google Maps SDK · GPS/Location Services

GearShare é uma rede social móvel direcionada a entusiastas de automóveis e condução. A aplicação combina o registo de trajetos em estrada com partilha social, descoberta de estradas panorâmicas e telemetria de condução — criando um ecossistema completo para a comunidade automóvel. A inspiração vem de plataformas como o Strava (registo de atividade + comunidade) e o Wikiloc (descoberta de POIs geo-referenciados), adaptadas ao universo da condução.

---

## 2. Público-Alvo

O público principal são **homens e mulheres entre os 18 e 45 anos**, com paixão por automóveis e condução ativa. Subdividem-se em três perfis:

| Perfil | Descrição |
|---|---|
| **Enthusiast Casual** | Conduz ao fim de semana, partilha fotos do carro, quer descobrir novas estradas |
| **Track Day Enthusiast** | Vai a pistas, quer telemetria e comparação de tempos |
| **Roadtrip Explorer** | Planeia viagens longas, usa o mapa de spots para encontrar percursos panorâmicos |

**Dimensão do mercado:** A comunidade automóvel online conta com mais de 500 milhões de pessoas globalmente (Reddit r/cars, Instagram car community, YouTube automotive channels). Em Portugal, há mais de 5,8 milhões de condutores registados, dos quais uma fracção significativa segue conteúdo automóvel digital.

---

## 3. Problema que Resolve

A comunidade automóvel está fragmentada entre múltiplas plataformas genéricas (Instagram, YouTube, Facebook Groups) que **não foram desenhadas para condução**. Não existe uma aplicação móvel que:

1. **Registe e partilhe trajetos** de forma integrada com dados de condução reais (velocidade, distância, tempo)
2. **Mapeie coletivamente** as melhores estradas e curvas — o conhecimento fica em fóruns dispersos
3. **Associe o carro do utilizador** ao seu perfil e às suas runs, criando identidade automóvel
4. **Ofereça telemetria acessível** sem equipamento externo caro (OBD-II dongles, etc.)

O utilizador atual tem de usar: Strava (não é para carros), Google Maps (não partilha runs com contexto social), Instagram (sem dados de condução) e aplicações de telemetria isoladas. **GearShare unifica tudo num único ecossistema.**

---

## 4. Proposta de Valor

> **GearShare é o Strava dos entusiastas de automóveis** — regista as tuas runs, descobre estradas épicas, partilha com a comunidade e afirma a identidade do teu carro.

### Funcionalidades Core (Free)

- **Feed Social** — Partilha de runs com fotos/vídeos, comentários e reações
- **Mapa de Spots** — Mapa colaborativo com POIs (estradas panorâmicas, curvas técnicas, miradouros)
- **Registo de Run** — GPS automático: velocidade média, distância, tempo, rota no mapa
- **Garagem Virtual** — Perfil com o carro do utilizador (via Car API), estatísticas totais de KM e runs
- **Navegação para Spots** — Integração com Google Maps para navegar até um spot

### Funcionalidade Premium — GearShare Pro (1€/mês)

- **Modo Pista** — Telemetria em tempo real: G-forces (acelerómetro), velocidade de topo, mapa de calor por setor, tempos por volta. Inclui disclaimer legal obrigatório.
- **Offline Maps** — Download de mapas de spots para condução em zonas sem rede
- **Run Analytics Avançado** — Comparação histórica de runs no mesmo spot, evolução de desempenho

---

## 5. Requisitos Obrigatórios — Conformidade ENIDH

| Requisito | Implementação em GearShare |
|---|---|
| ≥ 4 ecrãs principais | Feed, Mapa, Gravar Run, Perfil (+ Modo Pista) |
| Multimédia | Fotos e vídeos nas runs, ilustrações de carros |
| Estado local | Room Database (runs, spots em cache) |
| Estado remoto | Firebase Firestore + Firebase Storage |
| Estado privado | Runs em modo privado, dados de perfil |
| Estado partilhado | Runs públicas, spots, feed social |
| Plano de negócio | GearShare Pro — 1€/mês (detalhe na secção 7) |
| Multilingue | PT 🇵🇹 · EN 🇬🇧 · ES 🇪🇸 |

---

## 6. Arquitetura de Informação — Entidades Principais

### Diagrama Entidade-Associação (sem atributos)

```
┌──────────┐     segue      ┌──────────┐
│          │◄──────────────►│          │
│   USER   │                │   USER   │
│          │────────────────┤          │
└────┬─────┘    tem(1,N)    └──────────┘
     │
     │ possui (1,N)            ┌─────────────┐
     │                         │     CAR     │
     ├────────────────────────►│             │
     │                         └─────────────┘
     │
     │ cria (1,N)              ┌─────────────┐     tem (1,N)    ┌─────────────┐
     │                         │    POST     │─────────────────►│   COMMENT   │
     ├────────────────────────►│             │                   └─────────────┘
     │                         │  (run_id?)  │
     │                         └──────┬──────┘
     │                                │ referencia (0,1)
     │ grava (1,N)                    ▼
     │                         ┌─────────────┐
     │                         │     RUN     │
     ├────────────────────────►│             │
     │                         └──────┬──────┘
     │                                │ termina em (0,1)
     │                                ▼
     │ cria/valida (1,N)       ┌─────────────┐
     │                         │    SPOT     │
     └────────────────────────►│             │
                                └─────────────┘
```

### Entidades

| Entidade | Papel |
|---|---|
| **User** | Utilizador registado; tem perfil, garagem, runs e posts |
| **Car** | O carro do utilizador; associado ao perfil (marca, modelo, ano) |
| **Post** | Publicação no feed; pode referenciar uma Run e conter media |
| **Run** | Trajeto gravado pelo GPS; tem métricas (KM, tempo, velocidade) |
| **Spot** | POI no mapa; uma estrada, curva ou local de interesse |
| **Comment** | Comentário num Post; pertence a um User |

### Associações

- **User** segue **User** (N:M)
- **User** possui **Car** (1:N)
- **User** cria **Post** (1:N)
- **User** grava **Run** (1:N)
- **User** cria/valida **Spot** (1:N)
- **Post** referencia **Run** (N:1, opcional)
- **Post** tem **Comment** (1:N)
- **Run** termina em **Spot** (N:1, opcional)

---

## 7. Plano de Negócio — GearShare Pro (1€/mês)

### Justificação da Funcionalidade Paga

A funcionalidade paga centra-se no **Modo Pista com Telemetria Avançada**, complementada por **Mapas Offline** e **Analytics Histórico**.

**Porquê o Modo Pista engaja o utilizador a pagar?**

O entusiasta de track days representa o segmento *power user* da comunidade automóvel. Este utilizador:
- Investe regularmente centenas de euros em track days (inscrição, combustível, desgaste de pneus)
- Paga por equipamento de telemetria dedicado (Harry's LapTimer Pro: 24,99€; RaceChrono Pro: 9,99€)
- Tem motivação intrínseca para melhorar os seus tempos e comparar com outros

Oferecer telemetria de qualidade por **1€/mês (12€/ano)** é uma proposta de valor irrecusável para este segmento. É mais barato do que qualquer alternativa atual e está integrado na rede social onde ele já vive.

### Projeção para 10 Milhões de Utilizadores

| Fase | Utilizadores | Conversão Premium | Receita Mensal |
|---|---|---|---|
| **Lançamento (Ano 1)** | 50.000 | 5% | 2.500€ |
| **Crescimento (Ano 2)** | 500.000 | 7% | 35.000€ |
| **Escala (Ano 3)** | 2.000.000 | 8% | 160.000€ |
| **Maturidade (Ano 5)** | 10.000.000 | 10% | 1.000.000€ |

**Estratégia de crescimento:**
1. **Viral Loop:** Cada run partilhada no feed atrai novos utilizadores orgânicos (como o Strava)
2. **Club System:** Integração com clubes automóvel (ex: Porsche Club Portugal) — aquisição B2B
3. **Car Meetups:** QR codes em eventos automóvel → download imediato com contexto
4. **YouTube/Instagram Integration:** Criadores de conteúdo automóvel como embaixadores

**Disclaimer Legal (Modo Pista):**
O Modo Pista requer aceitação obrigatória de um aviso legal antes de cada sessão, declarando que o utilizador assume total responsabilidade pela utilização em circuito fechado e que a aplicação não é responsável por acidentes ou infrações rodoviárias. Esta proteção legal é essencial e será implementada como um Dialog de aceitação explícita (checkbox + botão).

---

## 8. Mapa de Navegação

```
[Splash Screen / Onboarding]
         │
         ▼
[Login / Register] ──── Firebase Auth (Email + Password)
         │
         ▼
┌────────────────────────────────────────────────────────┐
│                  BOTTOM NAVIGATION BAR                  │
│   [Feed]    [Mapa]    [▶ Gravar]    [Perfil]           │
└────────────────────────────────────────────────────────┘
         │
    ┌────┴──────────────────────────────┐
    │                                   │
    ▼                                   ▼
[FEED]                              [MAPA DE SPOTS]
  │                                     │
  ├── Ver Post                          ├── Ver Detalhe do Spot
  │     └── Comentários                 │     └── [Navegar até Spot] ──► Google Maps
  └── Criar Post                        └── Criar/Sugerir Spot
        └── Selecionar Run gravada
                                    [GRAVAR RUN]
                                         │
                                         ├── Contagem decrescente
                                         ├── HUD em tempo real (velocidade, tempo, KM)
                                         ├── Parar Run → Resumo
                                         └── [Partilhar no Feed] ──► Criar Post

[PERFIL]
  │
  ├── As minhas Runs (histórico)
  ├── A minha Garagem (carro atual)
  │     └── Alterar carro (Car API)
  ├── Estatísticas (KM totais, nº runs)
  └── [GearShare Pro]
        └── Modo Pista ──► [Disclaimer] ──► Telemetria HUD

[DEFINIÇÕES] (acesso via Perfil)
  ├── Idioma: PT / EN / ES
  ├── Notificações
  └── Conta / Logout
```

### Fluxos Principais

| Fluxo | Ecrãs Envolvidos |
|---|---|
| Descobrir e navegar a um spot | Mapa → Detalhe Spot → Google Maps → Gravar Run → Feed |
| Partilhar uma run | Gravar Run → Resumo → Criar Post → Feed |
| Ver o perfil de outro utilizador | Feed → Post → Perfil do autor → Seguir |
| Ativar Modo Pista | Perfil → GearShare Pro → Disclaimer → HUD Telemetria |

---

## 9. Wireframe Textual — Os 4 Ecrãs Principais

> Estilo: Bottom Navigation com 4 tabs. Dark mode por defeito. Linguagem visual: Material Design 3.

---

### Ecrã 1 — FEED (Social)

```
┌─────────────────────────────────┐
│ GearShare        🔔  🔍  [Logo] │  ← TopAppBar
├─────────────────────────────────┤
│ ┌─────────────────────────────┐ │
│ │ 👤 João Silva • BMW M3 E46 │ │  ← Avatar + Nome + Carro
│ │ 📍 Serra da Estrela • 2h   │ │  ← Localização + Tempo
│ │                             │ │
│ │   [ FOTO/VÍDEO DA RUN ]    │ │  ← Media (16:9)
│ │                             │ │
│ │ 🏁 42.3 km · ⏱ 38min      │ │  ← Métricas da Run (chip row)
│ │ 💬 "Que estrada incrível!" │ │  ← Descrição
│ │                             │ │
│ │ ❤️ 128   💬 34   ↗️ Share │ │  ← Action Row
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │  [próximo post...]          │ │
│ └─────────────────────────────┘ │
├─────────────────────────────────┤
│  🏠Feed  🗺️Mapa  ⏺️Gravar  👤 │  ← Bottom Nav
└─────────────────────────────────┘
```

**Elementos:**
- TopAppBar com logo GearShare, ícone de notificações e pesquisa
- Lista vertical infinita de cards (PostCard)
- Cada card: avatar do utilizador, nome, modelo do carro, tempo relativo, foto/vídeo, métricas da run em chips coloridos, texto, botões de like/comentar/partilhar
- FAB ou tab central para iniciar gravação rápida

---

### Ecrã 2 — MAPA DE SPOTS

```
┌─────────────────────────────────┐
│ 🔍 Pesquisar spots...    [⚙️]  │  ← SearchBar flutuante
├─────────────────────────────────┤
│                                 │
│                                 │
│      [ GOOGLE MAPS FULL ]      │  ← Mapa ocupa todo o ecrã
│                                 │
│    📍     📍    📍             │  ← Marcadores de Spots
│         📍                     │    (ícone personalizado por tipo)
│                                 │
│                    📍           │
│                                 │
│    [+ Sugerir Spot]            │  ← FAB bottom-right
├─────────────────────────────────┤
│  🏠Feed  🗺️Mapa  ⏺️Gravar  👤 │
└─────────────────────────────────┘

[Bottom Sheet ao clicar num Spot]
┌─────────────────────────────────┐
│ ═══════ (drag handle)           │
│ 📍 Serra da Estrela — N338      │
│ ⭐ 4.8  (127 runs aqui)        │
│ 🏷️ Panorâmica · Curvas técnicas │
│                                 │
│ [Ver Runs aqui]  [🧭 Navegar]  │
└─────────────────────────────────┘
```

**Elementos:**
- Mapa Google Maps a ecrã completo
- SearchBar flutuante no topo com filtros (tipo de spot, classificação)
- Marcadores customizados com ícone de pneu/estrada por categoria
- Bottom Sheet ao tocar num spot: nome, rating, nº de runs, tags, botões "Ver Runs" e "Navegar"
- FAB "+ Sugerir Spot" para adicionar novo POI

---

### Ecrã 3 — GRAVAR RUN (HUD de Condução)

```
[Estado: A preparar]
┌─────────────────────────────────┐
│              ✕                  │  ← Fechar
│                                 │
│         PRONTO PARA             │
│           GRAVAR?               │
│                                 │
│    📍 Spot detectado:           │
│    Serra da Estrela — N338      │
│                                 │
│       [ ▶️ INICIAR ]           │  ← Botão grande
└─────────────────────────────────┘

[Estado: A Gravar — HUD]
┌─────────────────────────────────┐
│  ● REC  00:12:34        [■ Stop]│  ← Status bar
├─────────────────────────────────┤
│                                 │
│          82 km/h                │  ← Velocidade (grande, centro)
│                                 │
│  ┌──────────┐  ┌──────────┐    │
│  │ 12.4 km  │  │ 00:12:34 │    │  ← Distância | Tempo
│  └──────────┘  └──────────┘    │
│                                 │
│  ┌──────────────────────────┐  │
│  │  [ MINI MAPA DO TRAJETO ]│  │  ← Rota atual em tempo real
│  └──────────────────────────┘  │
│                                 │
│         [ ■ PARAR RUN ]        │  ← Botão de paragem
└─────────────────────────────────┘

[Estado: Resumo pós-run]
┌─────────────────────────────────┐
│ ✅ Run Completa!                │
│                                 │
│  🏁 42.3 km  ⏱ 38min  ⚡ 67km/h│  ← Métricas finais
│                                 │
│  [ MAPA DO PERCURSO ]          │
│                                 │
│  [ Partilhar no Feed ]         │  ← Cria Post automaticamente
│  [ Guardar Privado   ]         │
│  [ Descartar         ]         │
└─────────────────────────────────┘
```

**Elementos:**
- 3 estados sequenciais: Preparação → HUD → Resumo
- HUD com velocidade a grande destaque, distância e cronómetro
- Mini-mapa com rota em tempo real (polyline)
- Ecrã keepAwake durante gravação
- Pós-run: métricas finais, mapa do percurso completo, opções de partilha

---

### Ecrã 4 — PERFIL (Garagem Virtual)

```
┌─────────────────────────────────┐
│ ⚙️                              │  ← Definições (top-right)
│                                 │
│         👤 (Avatar)             │
│       Miguel Silva              │
│    @miguel_petrolhead           │
│                                 │
│  👥 247 seguidores  |  89 runs  │
│                                 │
│ ┌─── A MINHA GARAGEM ─────────┐ │
│ │                             │ │
│ │   [ ILUSTRAÇÃO DO CARRO ]  │ │  ← Car API (desenho/imagem do modelo)
│ │                             │ │
│ │  BMW M3 Competition (G80)  │ │
│ │  2023 · Competition Pack   │ │
│ │           [Alterar Carro]  │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─── ESTATÍSTICAS ────────────┐ │
│ │  🗺️ 3.847 km totais        │ │
│ │  🏁 89 runs                 │ │
│ │  📍 12 spots visitados      │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─── RUNS RECENTES ───────────┐ │
│ │  [miniatura] Serra Estrela  │ │
│ │  [miniatura] Gerês N304     │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─ GEARSHARE PRO ────────────┐ │
│ │ 🏆 Modo Pista + Analytics  │ │
│ │        [ Ativar — 1€/mês ] │ │
│ └─────────────────────────────┘ │
├─────────────────────────────────┤
│  🏠Feed  🗺️Mapa  ⏺️Gravar  👤 │
└─────────────────────────────────┘
```

**Elementos:**
- Header com avatar, nome, handle, contadores de seguidores e runs
- Card da Garagem com ilustração do carro (API de imagens de veículos), modelo/ano, botão alterar
- Card de Estatísticas: KM totais, número de runs, spots visitados
- Grid/lista de runs recentes (miniaturas clicáveis)
- Card GearShare Pro com CTA de subscrição

---

## 10. Tecnologias e Arquitetura

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Kotlin |
| **Arquitetura** | MVVM + Repository Pattern |
| **UI** | Jetpack Compose + Material Design 3 |
| **Navegação** | Navigation Component (Bottom Nav + NavGraph) |
| **Auth** | Firebase Authentication (Email/Password) |
| **Base de dados remota** | Firebase Firestore |
| **Media remoto** | Firebase Storage |
| **Base de dados local** | Room Database |
| **Mapas** | Google Maps SDK for Android |
| **Localização GPS** | FusedLocationProviderClient |
| **Car API** | NHTSA API / CarQuery API (gratuita) |
| **Multilingue** | Android Strings Resources (PT/EN/ES) |
| **Pagamentos (futuro)** | Google Play Billing Library |

---

## 11. Funcionalidades Extra (sujeito a aprovação)

- **Firebase Google Sign-In** — Login social com Google (+1 ponto)
- **Car API "Hello World"** — Demonstração de integração com API de carros (nova tecnologia)
- **Firebase Test App** — App de teste separada com Auth + Firestore + Storage (conforme requisito da Fase 1)

---

*Documento elaborado para a Fase 1 — Conceito | GearShare | Computação Móvel | ENIDH | 2025/2026*
