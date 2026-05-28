# Funcionalidades e fluxos

## 1. Guest mode

### Objetivo

Permitir que o utilizador explore a app sem criar conta, para aumentar curiosidade e conversão.

### Pode fazer sem login

- ver Home com dados simulados/parciais;
- ver mapa;
- ver pins públicos;
- ver posts públicos;
- abrir perfis públicos;
- ver detalhes de rotas públicas;
- ver help/about.

### Não pode fazer sem login

- iniciar run;
- publicar run;
- comentar;
- dar like;
- guardar percurso;
- seguir pessoas;
- criar evento;
- aderir a evento;
- reportar alertas;
- editar perfil.

### Implementação

Criar `SessionMode`:

```kotlin
enum class SessionMode {
    GUEST,
    AUTHENTICATED
}
```

Cada ação protegida chama `RequireAuthGate`.

## 2. Login

### Processo

1. Utilizador abre app.
2. `AuthActivity` verifica Firebase Auth currentUser.
3. Se já existe sessão, abre `MainActivity`.
4. Caso contrário mostra login/registo/continuar como convidado.
5. Login usa Firebase Auth Email/Password.
6. Após login, carrega `users/{uid}`.
7. Se perfil incompleto, abrir setup de carro.
8. Abrir `MainActivity`.

### Otimização

- Não guardar password.
- Guardar só `uid`, `sessionMode` e preferências em DataStore.
- Firestore user profile deve ser lido uma vez e mantido em cache local.
- Usar loading state para evitar cliques repetidos.

## 3. Registo

### Campos

- nome;
- email;
- password;
- confirmação de password;
- username único;
- carro principal:
  - marca;
  - modelo;
  - ano;
  - tipo;
  - cor;
  - matrícula opcional, mas não recomendada por privacidade.

### Processo

1. Validar campos localmente.
2. Criar conta Firebase Auth.
3. Criar documento `users/{uid}`.
4. Criar `cars/{carId}` em subcoleção do user ou coleção global.
5. Gerar `carAvatar`.
6. Guardar perfil localmente.
7. Abrir `MainActivity`.

### Avatar artificial do carro

Opções:

- MVP: avatar vetorial gerado por camadas:
  - tipo de carro;
  - cor;
  - jantes;
  - fundo;
  - sombra;
  - badge.
- Extra AI: Cloud Function chama serviço de geração de imagem a partir de prompt:
  - "stylized vector car avatar, front 3/4 view, black background, red neon accent..."
- Fallback obrigatório: se AI falhar, usar avatar vetorial local.

## 4. Run tracking

### Objetivo

Registar um passeio completo com rota, tempo, distância, velocidade média e alertas.

### Fluxo

1. Utilizador toca em `+ Run`.
2. Se guest, mostrar AuthRequired dialog.
3. Se autenticado, abrir `RunRecorderActivity`.
4. Pedir permissão de localização.
5. Iniciar Foreground Service.
6. Guardar run como draft local:
   - `runId`;
   - `userId`;
   - `startedAt`;
   - `status = RECORDING`.
7. A cada localização válida:
   - guardar `RoutePointEntity`;
   - atualizar distância incremental;
   - atualizar velocidade média;
   - atualizar polyline no mapa.
8. Utilizador pode criar alertas.
9. Ao terminar:
   - parar service;
   - guardar resumo;
   - perguntar: publicar, guardar rascunho, apagar.
10. Se publicar:
   - upload route points;
   - upload media se existir;
   - criar post opcional.

### Dados calculados

- `durationSeconds`;
- `distanceMeters`;
- `averageSpeedKmh`;
- `maxSpeedKmh`, apenas para estatística privada; não destacar publicamente;
- `elevationGain`, se disponível;
- `startLocation`;
- `endLocation`;
- bounding box da rota;
- encoded polyline simplificada para pré-visualização;
- pontos completos guardados separadamente.

### Regras de segurança

- Não usar ranking por velocidade.
- Não mostrar “recorde de velocidade”.
- Mensagem fixa: “Conduz sempre de acordo com o Código da Estrada.”

## 5. Alertas na estrada

### Tipos

```text
POTHOLE
ROADWORKS
ACCIDENT
SLIPPERY_ROAD
CLOSED_ROAD
TRAFFIC
ANIMAL
GENERIC_HAZARD
SAFETY_CONTROL
```

### Processo

1. Durante run, utilizador toca em alerta.
2. Escolhe tipo.
3. App grava localização atual.
4. Guarda localmente.
5. Sincroniza com Firestore.
6. Alertas têm expiração automática.
7. Outros utilizadores podem confirmar/remover.

### Validade sugerida

- perigo/acidente/trânsito: 2h;
- buraco/obra: 7 dias;
- estrada cortada: 24h;
- safety control: curto e neutro.

## 6. Criar publicação

### Entrada

Pode ser aberta:

- após terminar uma run;
- a partir de rascunhos;
- a partir de perfil.

### Campos

- run escolhida;
- título;
- descrição;
- hashtags;
- fotos;
- vídeos;
- visibilidade:
  - público;
  - amigos;
  - privado;
- categoria:
  - scenic;
  - curvas;
  - estrada boa;
  - costa;
  - serra;
  - encontro/evento.

### Processo

1. Ler runs locais/publicáveis.
2. Utilizador escolhe run.
3. Adiciona texto/media.
4. Media fica em Storage.
5. Post fica em Firestore.
6. Feed atualiza por query/paginação.
7. Dados locais são atualizados para aparecer imediatamente.

## 7. Mapa

### Funções

- mostrar posição do utilizador;
- mostrar pins;
- filtrar por categorias;
- abrir detalhe;
- guardar locais;
- mostrar eventos premium;
- mostrar rotas públicas próximas;
- mostrar alertas ativos.

### Otimização

- Query por bounding box/geohash.
- Cache por região.
- Atualizar quando a câmara para de mexer, não a cada pixel.
- Clusterização de pins se houver muitos.
- Limitar resultados por zoom.

### Categorias de pins

```text
VIEWPOINT
GOOD_ROAD
CURVES
COAST
MOUNTAIN
NIGHT_DRIVE
PHOTO_SPOT
CAFE_STOP
MEETING_POINT
```

## 8. Home

### Cards

- “Esta semana”: runs, km, tempo total.
- “Bom dia para passear?” com meteorologia.
- “Rotas perto de ti”.
- “Tendências na zona”.
- “Iniciar run”.
- “Evento em destaque”, se premium/eventos ativos.

### Meteorologia

Critérios simples:

- sem chuva forte;
- vento baixo/moderado;
- visibilidade boa, se API fornecer;
- temperatura confortável;
- sem alertas meteorológicos.

Output:

```text
Bom dia para passear
Dia aceitável
Melhor evitar
```

## 9. Comunidade

### Ordenação

Prioridade:

1. posts de amigos;
2. posts próximos;
3. popularidade;
4. recência.

### Fórmula sugerida

```text
score = friendBoost + proximityBoost + popularityScore + recencyScore
```

### Popularidade

```text
popularityScore = likes * 2 + comments * 3 + saves * 4
```

### Funcionalidades

- like;
- comentário;
- guardar percurso;
- abrir perfil;
- abrir rota no mapa;
- denunciar post;
- filtro por distância/categoria.

## 10. Perfil

### Conteúdo

- foto/avatar;
- nome;
- username;
- carro principal;
- avatar estilizado do carro;
- seguidores/amigos mútuos;
- seguidos;
- km totais;
- runs totais;
- rotas públicas;
- rotas guardadas;
- badges;
- eventos criados/participados;
- estado premium;
- settings;
- about;
- help.

### Badges sugeridas

- Primeiro passeio;
- 100 km;
- 500 km;
- Explorador da Serra;
- Rota Costeira;
- Fotógrafo;
- Organizador;
- Premium Founder.

## 11. Premium: eventos e runs em conjunto

### Versão gratuita

- ver eventos públicos;
- abrir detalhe;
- ver localização base.

### Versão premium

- criar eventos;
- editar evento;
- criar grupo privado;
- live convoy;
- partilha de localização entre participantes;
- chat simples do evento, opcional;
- estatísticas do evento;
- badge no perfil.

### Processo de evento

1. Premium cria evento.
2. Define título, descrição, local, data, limite de participantes.
3. Escolhe rota ou ponto de encontro.
4. Evento aparece no mapa.
5. Outros users aderem.
6. No dia do evento, participantes podem ativar live convoy.
7. Ao terminar, gera resumo do evento.
