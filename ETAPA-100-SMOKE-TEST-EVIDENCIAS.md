# Etapa 100 — Smoke test e evidências de liberação

## Objetivo
Complementar o ensaio de build/teste com uma verificação HTTP simples após iniciar a aplicação. O teste não substitui testes funcionais, de segurança, banco de dados ou homologação com usuários.

## Uso (PowerShell)
Com a aplicação em execução local:

```powershell
.\scripts\smoke-test-producao.ps1
```

Para testar outro endereço:

```powershell
.\scripts\smoke-test-producao.ps1 -BaseUrl 'https://seu-dominio'
```

Se o projeto tiver o Actuator instalado e o endpoint estiver habilitado e acessível:

```powershell
.\scripts\smoke-test-producao.ps1 -VerificarActuator
```

O relatório é salvo por padrão em `target/release-evidence/smoke-test.txt`. O script aceita redirecionamentos e respostas 401/403 em rotas protegidas, pois isso pode indicar que a segurança está ativa. Ele reprova respostas inesperadas e falhas de conexão; não considera HTTP 200 prova de que o conteúdo ou a regra de negócio esteja correto.

## Checklist antes de liberar
- [ ] `clean test` concluiu com sucesso.
- [ ] Migrações Flyway aplicadas em uma base de homologação restaurável.
- [ ] Smoke test executado com a aplicação iniciada.
- [ ] Login e permissões verificados com usuários de perfis diferentes.
- [ ] Fluxos de escala, férias, troca, consumo de estoque, cirurgia e mutirão testados manualmente.
- [ ] Backup recente restaurado em ambiente de teste.
- [ ] HTTPS, cookies seguros, credenciais por variável de ambiente e logs sem segredos conferidos.
- [ ] Responsável do hospital aprovou os fluxos operacionais.

## Limitações
O script usa rotas que existem no histórico do projeto, mas a configuração local pode variar. Se alguma rota foi alterada, atualize o teste. Ele não cria dados, não autentica usuários e não executa operações de escrita.
