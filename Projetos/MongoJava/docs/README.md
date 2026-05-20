# BragaFlow Atlas - Organização do Projeto

Este documento junta a visão geral do projeto, a divisão de pastas e o resumo dos padrões usados, sem repetir informação desnecessária.

## Visão geral

BragaFlow Atlas é um projeto em Java com MongoDB Atlas para praticar CRUD, orientação a objetos e padrões criacionais, com uma interface web simples para testes. O trabalho foi montado para conectar Java ao MongoDB, fazer operações reais de inserir, consultar, atualizar e remover dados, usar Singleton em cache, aplicar Factory Method nas entregas e organizar tudo em uma estrutura fácil de manter.

## Como o projeto foi organizado

O código foi separado em camadas para manter cada responsabilidade no lugar certo:

- `App.java` inicia o servidor HTTP.
- `config/` concentra a conexão com o MongoDB e outras configurações.
- `domain/` guarda as entidades do sistema.
- `repository/` faz o acesso ao banco.
- `service/` aplica regras de negócio.
- `api/` recebe as requisições HTTP e devolve respostas.
- `factory/` cria objetos de entrega e logística.
- `cache/` mantém dados temporários em memória.
- `util/` reúne funções auxiliares reutilizáveis.
- `web/` contém o frontend estático.
- `docs/` concentra a documentação do projeto.

## O que deve existir em cada pasta

`config/` deve guardar a classe de conexão com o MongoDB, leitura de variáveis de ambiente e constantes globais. `domain/` deve conter classes simples como `User`, `Product`, `Freight` e `Delivery`, com atributos, construtores e getters/setters.

`repository/` é a camada de consultas, inserções, atualizações e remoções no MongoDB. `service/` centraliza validações e regras de negócio. `api/` deve apenas ler método HTTP, parâmetros, corpo da requisição e montar a resposta.

`factory/` serve para criar tipos diferentes de logística, como caminhão, navio, drone e trem, sem espalhar essa decisão pelo sistema. `cache/` guarda informações temporárias para reduzir consultas repetidas ao banco. `util/` fica para conversões, formatações e pequenos helpers. `web/` reúne o frontend estático, e `docs/` guarda a documentação complementar.

## Como as camadas se conectam

1. `web/` envia ações do usuário ou a requisição chega diretamente à API.
2. `api/` recebe a requisição HTTP.
3. `service/` aplica a regra de negócio.
4. `repository/` acessa o MongoDB.
5. `domain/` representa os dados trafegados entre as camadas.
6. `factory/` e `cache/` entram quando a lógica precisa de criação de objetos ou otimização em memória.
7. `util/` suporta as demais camadas com pequenas funções auxiliares.

## CRUD e métodos HTTP

CRUD é a base das operações mais comuns de um sistema:

- Create: cria um dado novo, como cadastrar um usuário.
- Read: busca ou lê os dados, como listar usuários ou ver um perfil.
- Update: altera um dado existente.
- Delete: apaga um dado permanentemente, como excluir uma conta.

No projeto web, isso aparece ligado aos métodos HTTP:

- POST: cria um novo registro.
- GET: consulta ou lista dados.
- PUT: substitui o dado inteiro por uma nova versão.
- PATCH: altera só uma parte do dado.
- DELETE: remove um registro.

Resumo rápido:

- POST cria.
- GET lê.
- PUT substitui tudo.
- PATCH altera só uma parte.
- DELETE remove.

## Como os padrões foram usados

O Singleton foi usado no cache global de usuários em `cache/UserCache.java`, com a ideia de responder primeiro do cache antes de consultar o MongoDB. Também existe `cache/FreightCache.java`, que ajuda a carregar o valor do frete para as classes de logística.

O Factory Method ficou na pasta `factory/`, criando a entrega certa de acordo com o tipo escolhido. Isso ajuda a manter a criação dos objetos organizada e também alimenta a coleção `fretes`, que recebe os valores base de caminhão, navio, drone e trem. A coleção `produtos` também recebe seed inicial para permitir montar pedidos.

As rotas da API usam `api/`, `service/` e `repository/` para separar acesso ao banco e regras de negócio. A pasta `web/` oferece uma interface simples para testar o sistema no navegador. O GitHub Copilot ajudou principalmente na montagem da estrutura, nos handlers repetitivos, no HTML/CSS e na organização geral.

## O que não misturar

Para manter o projeto organizado, evite colocar SQL ou queries dentro de `web/`, regras de negócio dentro de `api/`, manipulação de requisições HTTP dentro de `repository/` e classes de domínio com lógica de acesso ao banco.

## Como executar

1. Compile com `javac -cp ".;lib/*" App.java`.
2. Execute com `java -cp ".;lib/*" App`.
3. Acesse `http://localhost:8080` no navegador.

Se a porta estiver ocupada, identifique o processo com `netstat -ano | findstr :8080` e finalize o PID correspondente, ou rode em outra porta com `$env:PORT="9090"; java -cp ".;lib/*" App`.

## Resumo

A estrutura foi pensada para deixar o projeto fácil de entender, testar e expandir. Cada pasta tem um papel claro, o que evita código misturado e facilita a manutenção quando novas funcionalidades forem adicionadas.
