# Guia visual — preto/vermelho

## Identidade visual

Estilo: moderno, automóvel, noturno, premium, desportivo, mas legível.

Palavras-chave:

- dark mode first;
- vermelho como ação principal;
- cards escuros;
- cantos arredondados;
- mapas e media em destaque;
- dados de run com leitura rápida;
- não sobrecarregar.

## Paleta

```text
Background:        #09090B
Surface:           #111113
SurfaceVariant:    #1A1A1D
Card:              #18181B
CardElevated:      #202024

Primary Red:       #E50914
Primary Red Dark:  #B20710
Primary Red Soft:  #FF3B3B

Text Primary:      #F5F5F5
Text Secondary:    #B3B3B3
Text Muted:        #737373

Success:           #22C55E
Warning:           #F59E0B
Danger:            #EF4444
Info:              #38BDF8

Map Pin View:      #F97316
Map Pin Road:      #E50914
Map Pin Curves:    #A855F7
Map Pin Coast:     #38BDF8
Map Pin Mountain:  #84CC16
```

## Theme Compose

Criar:

```text
core/designsystem/theme/Color.kt
core/designsystem/theme/Theme.kt
core/designsystem/theme/Type.kt
```

## Regras de UI

### Bottom bar

- 5 itens.
- Ícones simples.
- `+ Run` central com destaque vermelho.
- Labels curtas:
  - Home
  - Mapa
  - Run
  - Comunidade
  - Perfil

### Cards

- Background `SurfaceVariant`.
- Radius 20dp.
- Padding 16dp.
- Título 16sp/18sp semibold.
- Texto secundário 13sp/14sp.
- Separadores subtis.

### Botões

Primário:

- fundo vermelho;
- texto branco;
- radius 16dp;
- altura mínima 48dp.

Secundário:

- fundo transparente ou surface;
- border vermelho;
- texto vermelho.

Perigoso:

- usar `Danger`;
- pedir confirmação.

### Inputs

- fundo `SurfaceVariant`;
- border vermelho ao focar;
- mensagens de erro abaixo;
- password com toggle de visibilidade.

## Ecrãs

### Home

Componentes:

- `WeeklyStatsCard`;
- `WeatherAdviceCard`;
- `RecommendedRoutesCarousel`;
- `TrendingCarsCard`;
- `StartRunButton`.

### Mapa

- Mapa ocupa ecrã todo.
- Top search/filter overlay.
- Bottom sheet para pins.
- Chips de categoria horizontais.
- Botão localização no canto inferior.

### Run

- Mapa como fundo.
- Painel inferior com:
  - tempo;
  - distância;
  - velocidade média;
  - botão alerta;
  - botão terminar.
- Botão “pausar” opcional.
- Não usar elementos que incentivem velocidade.

### Comunidade

- Feed em cards.
- Media grande.
- Título e autor.
- Chip com distância/zona.
- Botões: like, comentar, guardar.
- Avatar do carro do autor.

### Perfil

- Header com carro estilizado.
- Nome e username.
- Stats em 3 ou 4 cards.
- Tabs:
  - Runs;
  - Guardados;
  - Eventos;
  - Sobre.

## Animações obrigatórias

Para cumprir requisito académico, incluir pelo menos:

- animação no splash/logo;
- animação no botão `+ Run`;
- loading shimmer em feed;
- transição de cards na Home;
- pulso subtil no tracking ativo.

Usar animações curtas e funcionais, não exageradas.

## Multilíngua

Strings nunca hardcoded em Composables.

Criar:

```text
res/values/strings.xml       -> inglês ou base
res/values-pt/strings.xml    -> português
res/values-es/strings.xml    -> espanhol
```

Chaves:

```xml
<string name="nav_home">Home</string>
<string name="nav_map">Mapa</string>
<string name="nav_run">Run</string>
<string name="nav_community">Comunidade</string>
<string name="nav_profile">Perfil</string>
```
