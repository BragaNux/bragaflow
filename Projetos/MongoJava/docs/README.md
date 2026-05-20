# Organização do Projeto MongoJava

Este arquivo descreve como o projeto foi organizado por pastas, qual a responsabilidade de cada parte e o que cada diretório deve conter para manter o código limpo e fácil de evoluir.

## Visão geral

O projeto foi dividido em camadas para separar responsabilidades:

- `App.java` inicia o servidor HTTP e registra a aplicação.
- `config/` concentra a configuração de conexão com o MongoDB.
- `domain/` guarda as entidades do sistema.
- `repository/` faz o acesso ao banco.
- `service/` aplica regras de negócio.
- `api/` recebe as requisições HTTP e devolve respostas.
- `factory/` cria objetos de entrega e logística.
- `cache/` mantém dados temporários em memória.
- `util/` reúne funções auxiliares reutilizáveis.
- `web/` contém o frontend estático.
- `docs/` concentra documentação do projeto.

## O que deve existir em cada pasta

### `config/`

Fica responsável por arquivos de configuração, como conexão com o banco, portas, credenciais e parâmetros globais da aplicação.

Exemplos do que deve ficar aqui:
- classe de conexão com o MongoDB;
- leitura de variáveis de ambiente, se existirem;
- constantes de configuração que não fazem parte da regra de negócio.

Evite colocar aqui:
- regras de CRUD;
- lógica de tela;
- código de rotas HTTP.

### `domain/`

Guarda as classes que representam os dados principais do sistema.

Exemplos:
- `User`;
- `Product`;
- `Freight`;
- `Delivery`.

Essas classes devem ser simples e conter principalmente atributos, construtores, getters, setters e, quando necessário, pequenas validações de modelo.

### `repository/`

É a camada que conversa diretamente com o MongoDB.

Aqui entram:
- consultas;
- inserções;
- atualizações;
- remoções;
- busca por filtros e coleções.

Boa prática para essa pasta:
- não colocar regra de negócio complexa;
- não montar resposta HTTP;
- não misturar leitura de banco com tratamento de interface.

### `service/`

Centraliza as regras de negócio do projeto.

É o lugar certo para:
- validar dados antes de salvar;
- decidir se uma operação pode ou não acontecer;
- tratar regras específicas de usuário, produto, frete e entrega;
- combinar dados de mais de um repository quando necessário.

Essa camada existe para evitar que os handlers da API fiquem grandes demais.

### `api/`

Contém os handlers HTTP que recebem as requisições, chamam a service correta e montam a resposta final.

Aqui devem ficar:
- leitura de método HTTP;
- leitura de parâmetros e corpo da requisição;
- envio de status code;
- resposta em JSON ou texto;
- pequenos ajustes de cabeçalho.

O ideal é que a pasta `api/` não tenha regras pesadas de negócio. Ela deve apenas coordenar a comunicação entre o cliente e as camadas internas.

### `factory/`

Armazena as classes responsáveis pela criação de objetos com base em um tipo escolhido em tempo de execução.

No projeto, essa pasta serve para:
- criar tipos diferentes de logística;
- isolar a lógica de escolha entre avião, caminhão, navio, trem e drone;
- manter a criação dos objetos mais organizada.

### `cache/`

Guarda caches em memória para reduzir chamadas repetidas ao banco.

Neste projeto, a ideia é usar essa pasta para:
- armazenar dados consultados com frequência;
- evitar leitura repetida do MongoDB;
- centralizar singleton ou estruturas temporárias de apoio.

Tudo que for armazenado aqui deve ser tratado como temporário.

### `util/`

É a pasta de apoio para funções pequenas e reutilizáveis.

Exemplos:
- conversão de JSON;
- formatação de dados;
- validações genéricas;
- helpers para respostas ou leitura de texto.

Se uma função começar a crescer demais, ela provavelmente pertence a outra camada.

### `web/`

Contém o frontend estático que permite testar o sistema pelo navegador.

O que deve existir aqui:
- `index.html`;
- `styles.css`;
- `app.js`;
- imagens e arquivos de apoio em `assets/`.

Essa pasta deve ficar isolada do backend para facilitar manutenção visual e testes rápidos.

### `docs/`

Área reservada para documentação complementar do projeto.

Pode conter:
- explicações da estrutura;
- reflexão sobre o trabalho;
- anotações da implementação;
- decisões de arquitetura;
- instruções específicas para execução ou avaliação.

## Como as pastas se conectam

A sequência geral do projeto é esta:

1. `web/` envia ações do usuário ou a requisição vem direto para a API.
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

No projeto web, isso normalmente aparece ligado aos métodos HTTP:

- POST: usado para criar um novo registro.
- GET: usado para consultar ou listar dados.
- PUT: usado para substituir o dado inteiro por uma nova versão.
- PATCH: usado para alterar só uma parte do dado.
- DELETE: usado para remover um registro.

Resumo rápido:

- POST cria.
- GET lê.
- PUT substitui tudo.
- PATCH altera só uma parte.
- DELETE remove.

## Critérios de organização usados no projeto

- Cada camada tem uma responsabilidade principal.
- O acesso ao banco fica separado da regra de negócio.
- A API fica separada da criação de objetos e da lógica de cache.
- O frontend estático fica isolado no diretório `web/`.
- A documentação fica concentrada em `docs/`.

## O que não misturar

Para manter o projeto organizado, evite colocar:

- SQL, Mongo queries ou chamadas ao banco dentro de `web/`;
- regras de negócio dentro de `api/`;
- manipulação de requisições HTTP dentro de `repository/`;
- classes de domínio cheias de lógica de acesso ao banco;
- arquivos temporários fora de `cache/` ou `docs/`.

## Resumo

A estrutura atual foi pensada para deixar o projeto fácil de entender, testar e expandir. Cada pasta tem um papel claro, o que ajuda a evitar código misturado e facilita a manutenção quando novas funcionalidades forem adicionadas.
