# DrivePulse - Handoff para a Fase 8

## Estado Atual (Fim da Fase 7)
- **Autenticação:** A funcionar plenamente (Email/Password e Google Sign-In). 
- **Erro "User profile not found":** Corrigido! Foi removido um "swallow exception" no processo de criação de perfil, garantindo que qualquer problema de rede ou permissão cancela o login corretamente em vez de deixar a app num estado inconsistente. Também foi adicionado um mecanismo de fallback no `UserRepositoryImpl`.
- **Profile Screen:** Carrega dados em tempo real do Firestore, mostra foto de perfil (via Coil), estatísticas e detalhes do carro.
- **Edit Profile Screen:** Permite editar dados base e fazer upload de fotografia para o Firebase Storage, atualizando automaticamente o perfil.

## Pedido do Utilizador para a Fase 8
O utilizador solicitou uma reestruturação do fluxo de Onboarding (logo após o registo) com os seguintes requisitos:
1. **Informações do Utilizador:** Pedir Primeiro Nome e Último Nome.
2. **Username Único (@username):** Pedir um username e garantir que é único na base de dados.
3. **Informações do Carro:** Pedir Marca, Modelo e Ano (que já estava parcialmente no `ProfileSetupScreen`).
4. **Gerar Imagem do Carro:** Avaliar a viabilidade de gerar dinamicamente uma imagem do carro inserido.

### Análise da Viabilidade ("Vale a pena gerar a imagem do carro?")
**É possível?** Sim. Poderíamos usar uma API gratuita como o Unsplash (que devolve imagens reais, mas muitas vezes não acerta no modelo exato) ou uma API paga de Inteligência Artificial (ex: OpenAI DALL-E, mas consome saldo).
**Vale a pena?** **Sinceramente, não.** 
1. **Engajamento:** Numa app de comunidade automóvel ("Strava para carros"), o utilizador tem muito orgulho no *seu* próprio carro (as jantes, a cor, o cenário). Mostrar uma foto genérica gerada por IA tira toda a personalização e ligação emocional com a app.
2. **Complexidade e Custos:** APIs de geração custam dinheiro e APIs de pesquisa de imagens falham frequentemente na precisão do modelo exato do carro, causando frustração ("Este não é o meu carro!").
**Alternativa Recomendada:** Ter um *placeholder* (uma silhueta vetorial elegante e desportiva) e incentivar o utilizador ativamente a fazer upload da foto *real* do seu carro através da galeria ou câmara. Fica muito mais premium, interativo e pessoal.

## Plano de Implementação para a Fase 8

### Passo 1: Expandir o Modelo de Dados (✅ Concluído)
- Atualizar o `UserDto` e `User` (Domain) para separar `firstName` e `lastName`. (Feito)

### Passo 2: Sistema de Usernames Únicos (✅ Concluído)
- Criada a infraestrutura no repositório (`UserRepository` e `UserRepositoryImpl`) para verificar disponibilidade de usernames (`isUsernameAvailable`) e completar o onboarding de forma atómica usando Firestore Transactions (`completeOnboarding`).
- Criados os UseCases da camada de Domain: `CheckUsernameUseCase` e `CompleteOnboardingUseCase`.

### Passo 3: Refatorar o Onboarding (`ProfileSetupScreen`) (Próxima Tarefa)
- Refatorar o `ProfileSetupViewModel` para usar Clean Architecture (injetando os novos UseCases em vez de aceder ao Firestore diretamente) e adicionar validação live (debounce) do username.
- Transformar o `ProfileSetupScreen` num formulário de passo único ou multi-step com:
  - Campo `@username` (com validação live de disponibilidade).
  - Campos Primeiro e Último Nome.
  - Campos Marca, Modelo e Ano do Carro.
- Atualizar o `AuthNavGraph` para verificar se o onboarding já foi concluído (se o user já tem um username guardado) no momento do Login.

### Passo 4: Home Dashboard
- Após o onboarding ser completado com sucesso, redirecionar para a Home e construir o ecrã inicial conforme o UI Style Guide.
- Integrar a Open-Meteo API (sem necessidade de chaves) para dar o "Weather Advice" (ex: "Estrada molhada, conduz com cuidado").

---
**Progresso Recente (Claude):**
- Modelo de dados atualizado com `firstName` e `lastName`.
- Repositórios expandidos com lógica de validação e criação de username único via Firestore Transaction.
- Use cases de domínio `CheckUsernameUseCase` e `CompleteOnboardingUseCase` implementados.

**Nota para o próximo Agente:** O ambiente local Windows do utilizador não tem o `jlink.exe` no JDK embutido da extensão do VS Code, pelo que o ficheiro `gradle.properties` foi configurado com `org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr`. Não remover esta linha durante compilações!
