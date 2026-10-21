# Etapa 97 — Validação final e preparação para produção

## Objetivo

Consolidar uma validação reproduzível antes de declarar o sistema Centro Cirúrgico pronto para produção. Esta etapa não substitui homologação clínica/operacional nem comprova execução dos testes: ela documenta a sequência de validação e os critérios de aceite.

## Execução recomendada

Na raiz do projeto, em PowerShell:

```powershell
# 1. Compilação limpa
.\mvnw.cmd clean compile

# 2. Testes unitários e de integração habilitados pelo projeto
.\mvnw.cmd test

# 3. Empacotamento
.\mvnw.cmd package
```

Se houver testes Testcontainers, o Docker deve estar disponível. Confirme que os testes de integração não estão sendo ignorados pela configuração Maven.

## Critérios de aceite

- [ ] `clean compile` termina com `BUILD SUCCESS`.
- [ ] `test` termina com `BUILD SUCCESS`; falhas não são ignoradas sem justificativa registrada.
- [ ] A aplicação sobe usando uma base PostgreSQL de homologação vazia ou restaurada de backup de teste.
- [ ] Todas as migrações Flyway são aplicadas sem erro; não alterar manualmente a base de produção para contornar uma migração.
- [ ] Login, logout, perfis e autorização são verificados com contas de cada perfil. Usuários sem permissão não podem acessar URLs diretamente.
- [ ] Escalas 12x36, plantões pares/ímpares, férias, trocas pendentes/aprovadas e conflitos de horário são testados com dados representativos.
- [ ] Estoque, lotes, FEFO, carrinhos e armários são testados para saldo insuficiente, concorrência e rollback em caso de falha.
- [ ] Cirurgias e mutirões são testados para conflitos de sala, profissionais, datas e horários.
- [ ] Backup é criado e a restauração é testada em ambiente separado.
- [ ] Segredos e credenciais não estão versionados; produção usa variáveis de ambiente e credenciais fortes.
- [ ] Logs não expõem senhas, tokens nem dados pessoais desnecessários.
- [ ] Menu e páginas críticas são verificadas em desktop e celular.

## Teste funcional mínimo

1. Criar usuário de homologação e conferir o perfil atribuído.
2. Cadastrar colaborador, equipe e escala; validar uma data par e uma ímpar.
3. Registrar férias e confirmar que a geração/consulta de escala trata o período corretamente.
4. Criar uma troca de plantão e testar os estados pendente, rejeitada e aprovada.
5. Cadastrar item, saldo e lote; realizar saída inferior ao saldo e verificar movimentações e rastreabilidade.
6. Tentar saída superior ao disponível e confirmar que não há saldo negativo nem registro parcial.
7. Criar sala, equipe e duas cirurgias com horários sobrepostos; confirmar que o conflito é rejeitado onde a regra estiver implementada.
8. Criar mutirão e associar cirurgias compatíveis com o período e procedimento.
9. Fazer backup e restaurar a base em ambiente isolado.

## Segurança e dados

- Use uma base de homologação; não execute testes destrutivos em produção.
- Confirme que os dados reais de pacientes são acessados apenas por perfis autorizados.
- Defina política de retenção, acesso e auditoria com a administração hospitalar.
- A aprovação final deve incluir responsáveis técnicos, de segurança e operacionais do hospital.

## Resultado da etapa

Registre data, commit/versão, ambiente, saída dos comandos Maven, versão PostgreSQL, migrações aplicadas, testes aprovados/reprovados e pendências. Só declare o sistema pronto quando todos os critérios críticos estiverem aprovados.
