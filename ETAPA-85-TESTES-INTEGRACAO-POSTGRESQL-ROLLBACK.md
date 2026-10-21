# Etapa 85 — Testes de integração PostgreSQL e rollback transacional

## Objetivo
Adicionar testes de integração com PostgreSQL real (em container descartável) para confirmar que uma falha no meio de uma baixa não deixa alterações parciais no saldo do lote, estoque geral ou histórico de movimentações.

## Implementação
- Dependências de teste Testcontainers JUnit Jupiter e PostgreSQL.
- Teste `PostgreSqlRollbackIntegridadeEstoqueIT` com cenário de rollback após falha tardia.
- Cenário positivo que confirma persistência conjunta de lote, estoque geral e movimentação quando a transação conclui.
- O teste cria tabelas isoladas com prefixo `teste_` e não utiliza nem altera o banco hospitalar configurado para a aplicação.

## Pré-requisitos
- JDK 21
- Docker Desktop ativo
- Acesso à internet na primeira execução para baixar imagens/dependências Maven

## Executar no Windows PowerShell
```powershell
.\mvnw.cmd -Dtest=PostgreSqlRollbackIntegridadeEstoqueIT test
```

Como a classe termina em `IT`, o comando acima seleciona explicitamente a classe. Para executar os testes unitários e de integração existentes:
```powershell
.\mvnw.cmd test
```

## Limitação importante
Este teste valida o comportamento transacional do PostgreSQL com tabelas isoladas. Não é um teste de ponta a ponta do `RastreabilidadeCirurgiaService` nem executa todas as migrações Flyway da aplicação. Para homologação, também devem ser executados testes de integração do serviço com as entidades e repositórios reais.
