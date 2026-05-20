# BragaFlow Atlas

Projeto em Java com MongoDB Atlas para praticar CRUD, orientação a objetos e padrões criacionais, usando uma interface web simples para testes.

## O que o professor pediu

O trabalho foi montado para atender estes pontos:

- Conectar Java ao MongoDB Atlas.
- Fazer operações reais de inserir, consultar, atualizar e remover dados.
- Aplicar Singleton em um cache global de usuários.
- Aplicar Factory Method para criar tipos diferentes de frete/entrega.
- Usar GitHub Copilot como apoio no desenvolvimento.
- Integrar tudo em um projeto organizado e fácil de testar.

## Como isso foi usado no projeto

### Singleton + MongoDB

O Singleton ficou em `cache/UserCache.java`. Ele guarda usuários em memória por um tempo configurável e tenta responder primeiro pelo cache antes de consultar o MongoDB. Isso deixa a busca mais rápida quando o mesmo usuário é acessado várias vezes.

Como desafio extra, também foi criado `cache/FreightCache.java`, que carrega os dados da coleção `fretes` e entrega o valor do frete para as classes de logística.

### Factory Method + MongoDB

O Factory Method ficou em `factory/`. Ele cria o tipo certo de entrega de acordo com o transporte escolhido e também foi adaptado para as classes `Caminhao`, `Navio`, `Drone`, `Trem` e `LogisticaAerea`. No projeto, isso alimenta o cadastro de pedidos no MongoDB e deixa a criação dos objetos mais organizada.

A coleção `fretes` é preenchida automaticamente com:
- `1` - Caminhão - valor `500`
- `2` - Navio - valor `1000`
- `3` - Drone - valor `100`
- `4` - Trem - valor `900`

A coleção `produtos` também recebe seed inicial para permitir montar pedidos com frete e itens vinculados.

### CRUD completo

As rotas da API ficam em `api/` e usam `repository/` e `service/` para separar o acesso ao banco das regras de negócio. Assim o projeto não fica tudo misturado em uma classe só.

### Frontend básico

A pasta `web/` tem uma interface simples para testar usuários e entregas pelo navegador, sem precisar usar só terminal ou Postman.

### GitHub Copilot

O Copilot ajudou principalmente na montagem da estrutura inicial, na repetição dos handlers, no HTML/CSS da tela de teste e na organização geral dos arquivos. Em trabalhos assim ele ajuda bastante porque acelera a parte repetitiva, mas ainda exige revisão manual.

## Por que usar esses padrões

- Singleton é útil quando você quer uma única instância compartilhada, como um cache global.
- Factory Method ajuda quando a criação do objeto muda conforme o tipo de entrada.
- Separar repository, service e api deixa o código mais limpo e mais fácil de manter.

## Estrutura

- `App.java`: ponto de entrada do servidor HTTP.
- `config/`: conexão com o MongoDB Atlas.
- `domain/`: entidades do sistema.
- `repository/`: acesso ao MongoDB.
- `service/`: regras de negócio.
- `factory/`: Factory Method das entregas.
- `cache/`: Singleton do cache de usuários.
- `api/`: handlers HTTP.
- `util/`: utilitários auxiliares.
- `web/`: frontend estático.
- `web/assets/`: pasta reservada para logo e imagens do projeto.
- `docs/`: textos da atividade e reflexão.

## Como executar

1. Compile com `javac -cp ".;lib/*" App.java`.
2. Execute com `java -cp ".;lib/*" App`.
3. Acesse `http://localhost:8080` no navegador.

Observação: neste projeto a string de conexão do Atlas está definida diretamente em `config/MongoConnection.java`, então não precisa configurar variável de ambiente para rodar.

### Resolvendo porta ocupada (BindException)

Se ao iniciar com `java -cp ".;lib/*" App` você receber um erro parecido com `Address already in use: bind` significa que a porta (por padrão `8080`) já está sendo usada por outro processo. Use os comandos abaixo no PowerShell para identificar e encerrar o processo que está ocupando a porta, ou para iniciar o servidor em outra porta.

1. Identificar o PID que usa a porta 8080:

```powershell
netstat -ano | findstr :8080
# ou (PowerShell)
Get-NetTCPConnection -LocalPort 8080 | Select-Object LocalAddress,LocalPort,State,OwningProcess
```

2. Verificar qual é o processo (substitua `1234` pelo PID encontrado):

```powershell
tasklist /FI "PID eq 1234"
# ou (PowerShell)
Get-Process -Id 1234
```

3. Encerrar o processo que está usando a porta (substitua `1234` pelo PID):

```powershell
taskkill /PID 1234 /F
# ou (PowerShell)
Stop-Process -Id 1234 -Force
```

4. Depois de liberar a porta, recompile e execute:

```powershell
javac -cp ".;lib/*" App.java
java -cp ".;lib/*" App
```

Alternativa: rodar em outra porta sem encerrar processos existentes (ex.: 9090):

```powershell
$env:PORT="9090"; java -cp ".;lib/*" App
```

Observações:
- A mensagem `ADVERTÊNCIA: SLF4J not found on the classpath` é apenas um aviso do driver MongoDB sobre logging — não impede o funcionamento.
- Caso planeje subir o projeto para repositório público, remova a string de conexão embutida e passe via variável de ambiente por segurança.

### Logo e identidade visual

O frontend já tem um espaço pronto para a marca do projeto. Os arquivos atualmente usados estão em `web/assets/`:

- `braga_512x512.png` — logo marca (exibido no topo da página).
- `braga_hero_banner_1672x941.png` — banner destaque na seção hero.
- `braga_foto_perfil_1254x1254.png` — foto de perfil no cartão do projeto (substitui iniciais BF).
- `braga_banner_identidade_visual_1983x793.png` — guia visual de identidade (reservado).
- `braga_background_1672x941.png` — fundo decorativo (reservado para uso futuro).

Para trocar os assets, substitua os arquivos PNG em `web/assets/` mantendo os nomes iguais ou atualize as referências em `web/index.html`.

## Rotas principais

- `GET /api/users`
- `POST /api/users`
- `GET /api/users/{username}`
- `PUT /api/users/{username}`
- `DELETE /api/users/{username}`
- `GET /api/freights`
- `GET /api/products`
- `GET /api/deliveries`
- `POST /api/deliveries`
- `PUT /api/deliveries/{id}`
- `DELETE /api/deliveries/{id}`

## Reflexão rápida

- O Singleton foi o mais fácil de entender e encaixar com o banco.
- O Factory Method deu um pouco mais de trabalho porque precisa separar bem cada tipo de entrega.
- O Copilot foi mais útil para ganhar tempo na estrutura do projeto e na repetição do código.
