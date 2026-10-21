# BragaFlow Atlas

API em Java com MongoDB Atlas para fretes e entregas, com uma interface web simples para testar. Projeto acadêmico para praticar CRUD de verdade no banco, orientação a objetos e padrões criacionais.

## O que tem

- Servidor HTTP em Java puro, sem framework, com as rotas `/api/users`, `/api/products`, `/api/freights` e `/api/deliveries`
- Camadas separadas: `api` recebe a requisição, `service` aplica as regras, `repository` fala com o MongoDB e `domain` guarda as entidades
- **Factory Method** em `factory/`: cria a entrega certa para caminhão, trem, navio ou drone
- **Singleton** em `cache/UserCache.java` e `cache/FreightCache.java`, para responder do cache antes de ir ao banco
- Seed das coleções `fretes` e `produtos`, para já dar para montar pedidos
- Interface web estática em `web/`

## Como rodar

Você precisa do JDK e de uma string de conexão do MongoDB Atlas.

```bash
cd Projetos/MongoJava
export MONGODB_URI="mongodb+srv://usuario:senha@seu-cluster.mongodb.net"
export MONGODB_DB="bragaflow"            # opcional, o padrão é "teste"
javac -cp ".:lib/*" App.java
java -cp ".:lib/*" App
```

No Windows (PowerShell), use `$env:MONGODB_URI="..."` e troque `:` por `;` no classpath (`".;lib/*"`).

Depois abra http://localhost:8080. Para usar outra porta, defina a variável `PORT`.

A organização completa das pastas e dos padrões está em [`Projetos/MongoJava/docs/README.md`](Projetos/MongoJava/docs/README.md).
