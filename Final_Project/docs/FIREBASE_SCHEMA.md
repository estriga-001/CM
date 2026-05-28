# Firebase, Room e schema de dados

## Firebase recomendado

- Firebase Authentication: login/registo.
- Cloud Firestore: dados estruturados.
- Firebase Storage: fotos/vídeos/avatars.
- Cloud Functions: contadores, thumbnails, notificações, validações premium.
- Cloud Messaging: notificações de eventos, comentários, likes, convoys.

## Coleções Firestore

```text
users/{uid}
cars/{carId}
runs/{runId}
posts/{postId}
posts/{postId}/comments/{commentId}
posts/{postId}/likes/{uid}
posts/{postId}/saves/{uid}
mapPins/{pinId}
roadAlerts/{alertId}
events/{eventId}
events/{eventId}/participants/{uid}
relationships/{relationshipId}
hashtags/{tag}
reports/{reportId}
```

## `users/{uid}`

```json
{
  "uid": "string",
  "displayName": "string",
  "username": "string",
  "email": "string",
  "photoUrl": "string?",
  "mainCarId": "string?",
  "carAvatarUrl": "string?",
  "isPremium": false,
  "stats": {
    "totalKm": 0.0,
    "totalRuns": 0,
    "totalTimeSeconds": 0,
    "savedRoutes": 0
  },
  "followersCount": 0,
  "followingCount": 0,
  "mutualFriendsCount": 0,
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## `cars/{carId}`

```json
{
  "carId": "string",
  "ownerUid": "string",
  "make": "Mazda",
  "model": "MX-5",
  "year": 2018,
  "bodyType": "ROADSTER",
  "color": "RED",
  "avatarPrompt": "string?",
  "avatarUrl": "string?",
  "createdAt": "timestamp"
}
```

## `runs/{runId}`

```json
{
  "runId": "string",
  "ownerUid": "string",
  "title": "string?",
  "description": "string?",
  "status": "DRAFT|PRIVATE|PUBLIC",
  "startedAt": "timestamp",
  "endedAt": "timestamp?",
  "durationSeconds": 0,
  "distanceMeters": 0.0,
  "averageSpeedKmh": 0.0,
  "maxSpeedKmhPrivate": 0.0,
  "startLat": 0.0,
  "startLng": 0.0,
  "endLat": 0.0,
  "endLng": 0.0,
  "bbox": {
    "minLat": 0.0,
    "minLng": 0.0,
    "maxLat": 0.0,
    "maxLng": 0.0
  },
  "encodedPolylinePreview": "string",
  "routePointsStoragePath": "runs/{runId}/points.json",
  "visibility": "PUBLIC|FRIENDS|PRIVATE",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

### Route points

Para muitos pontos, evitar guardar milhares de documentos. Opções:

1. Guardar pontos em ficheiro JSON comprimido no Storage.
2. Guardar amostra simplificada em Firestore para preview.
3. Guardar pontos completos localmente em Room.

Storage path:

```text
runs/{uid}/{runId}/points.json
```

## `posts/{postId}`

```json
{
  "postId": "string",
  "authorUid": "string",
  "runId": "string?",
  "title": "string",
  "description": "string",
  "hashtags": ["serra", "curvas"],
  "media": [
    {
      "type": "IMAGE|VIDEO",
      "url": "string",
      "thumbnailUrl": "string?"
    }
  ],
  "location": {
    "lat": 0.0,
    "lng": 0.0,
    "geohash": "string"
  },
  "visibility": "PUBLIC|FRIENDS|PRIVATE",
  "likesCount": 0,
  "commentsCount": 0,
  "savesCount": 0,
  "popularityScore": 0,
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## `comments`

```json
{
  "commentId": "string",
  "postId": "string",
  "authorUid": "string",
  "text": "string",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## `mapPins/{pinId}`

```json
{
  "pinId": "string",
  "createdByUid": "string",
  "title": "string",
  "description": "string",
  "category": "VIEWPOINT|GOOD_ROAD|CURVES|COAST|MOUNTAIN|NIGHT_DRIVE|PHOTO_SPOT|CAFE_STOP|MEETING_POINT",
  "lat": 0.0,
  "lng": 0.0,
  "geohash": "string",
  "rating": 0.0,
  "ratingsCount": 0,
  "photos": ["url"],
  "createdAt": "timestamp"
}
```

## `roadAlerts/{alertId}`

```json
{
  "alertId": "string",
  "createdByUid": "string",
  "type": "POTHOLE|ROADWORKS|ACCIDENT|SLIPPERY_ROAD|CLOSED_ROAD|TRAFFIC|ANIMAL|GENERIC_HAZARD|SAFETY_CONTROL",
  "lat": 0.0,
  "lng": 0.0,
  "geohash": "string",
  "runId": "string?",
  "confirmations": 0,
  "dismissals": 0,
  "expiresAt": "timestamp",
  "createdAt": "timestamp"
}
```

## `events/{eventId}`

```json
{
  "eventId": "string",
  "organizerUid": "string",
  "title": "string",
  "description": "string",
  "routeId": "string?",
  "meetingLat": 0.0,
  "meetingLng": 0.0,
  "geohash": "string",
  "startsAt": "timestamp",
  "endsAt": "timestamp?",
  "visibility": "PUBLIC|PRIVATE",
  "maxParticipants": 20,
  "participantsCount": 0,
  "premiumOnly": true,
  "createdAt": "timestamp"
}
```

## `relationships/{relationshipId}`

```json
{
  "relationshipId": "uidA_uidB",
  "fromUid": "string",
  "toUid": "string",
  "status": "FOLLOWING|FRIENDS|BLOCKED",
  "createdAt": "timestamp"
}
```

## Room local entities

Room deve guardar:

- perfil atual;
- runs locais;
- route points;
- posts recentes;
- map pins recentes;
- road alerts ativos;
- weather cache;
- pending uploads.

## Estratégia offline-first

### Single Source of Truth

A UI lê preferencialmente da Room. Repositories sincronizam Room ↔ Firebase.

### Escritas

- Escritas críticas: guardar primeiro localmente.
- Marcar como `syncStatus = PENDING`.
- WorkManager sincroniza.
- Se sincronização falhar, manter fila pendente.
- Mostrar estado ao utilizador.

### Leituras

- Mostrar cache imediatamente.
- Atualizar remoto em background.
- Escrever novos dados na Room.
- UI atualiza automaticamente via Flow.

## Índices Firestore necessários

Criar índices para:

```text
posts: visibility + createdAt desc
posts: visibility + popularityScore desc
posts: location.geohash + createdAt desc
mapPins: category + geohash
roadAlerts: geohash + expiresAt
events: geohash + startsAt
runs: ownerUid + startedAt desc
relationships: fromUid + status
relationships: toUid + status
```

## Storage paths

```text
users/{uid}/avatar/profile.jpg
users/{uid}/cars/{carId}/avatar.png
posts/{postId}/images/{imageId}.jpg
posts/{postId}/videos/{videoId}.mp4
posts/{postId}/thumbnails/{mediaId}.jpg
runs/{uid}/{runId}/points.json
events/{eventId}/cover.jpg
```

## Regras de segurança — rascunho conceptual

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    function signedIn() {
      return request.auth != null;
    }

    function isOwner(uid) {
      return signedIn() && request.auth.uid == uid;
    }

    match /users/{uid} {
      allow read: if true;
      allow create, update: if isOwner(uid);
      allow delete: if false;
    }

    match /runs/{runId} {
      allow read: if resource.data.visibility == "PUBLIC"
                  || (signedIn() && resource.data.ownerUid == request.auth.uid);
      allow create: if signedIn() && request.resource.data.ownerUid == request.auth.uid;
      allow update, delete: if signedIn() && resource.data.ownerUid == request.auth.uid;
    }

    match /posts/{postId} {
      allow read: if resource.data.visibility == "PUBLIC" || signedIn();
      allow create: if signedIn() && request.resource.data.authorUid == request.auth.uid;
      allow update, delete: if signedIn() && resource.data.authorUid == request.auth.uid;
    }

    match /posts/{postId}/comments/{commentId} {
      allow read: if true;
      allow create: if signedIn() && request.resource.data.authorUid == request.auth.uid;
      allow update, delete: if signedIn() && resource.data.authorUid == request.auth.uid;
    }

    match /mapPins/{pinId} {
      allow read: if true;
      allow create: if signedIn();
      allow update, delete: if signedIn() && resource.data.createdByUid == request.auth.uid;
    }

    match /roadAlerts/{alertId} {
      allow read: if true;
      allow create: if signedIn();
      allow update: if signedIn();
      allow delete: if false;
    }

    match /events/{eventId} {
      allow read: if true;
      allow create: if signedIn() && request.resource.data.organizerUid == request.auth.uid;
      allow update, delete: if signedIn() && resource.data.organizerUid == request.auth.uid;
    }
  }
}
```

Este rascunho deve ser endurecido antes da entrega final.
