# Etapa 95 — Testes das regras de escalas e trocas de plantão

## Objetivo
Adicionar testes unitários às regras centrais de Recursos Humanos sem alterar controllers, telas, entidades ou o schema do banco.

## Testes adicionados

### `EscalaPlantaoServiceRegrasTest`
- aceita escala 12x36 em data alternada quando a classificação corresponde à referência;
- rejeita escala em data não alternada;
- rejeita classificação de plantão divergente da referência;
- rejeita colaborador inativo.

### `TrocaPlantaoServiceRegrasTest`
- rejeita troca entre colaboradores de funções diferentes;
- rejeita solicitação de troca consigo mesmo;
- confirma que uma solicitação válida começa como `PENDENTE`, sem aprovador nem data de decisão.

## Executar no Windows PowerShell
Na raiz do projeto:

```powershell
.\mvnw.cmd -Dtest=EscalaPlantaoServiceRegrasTest,TrocaPlantaoServiceRegrasTest test
```

Para executar a suíte completa:

```powershell
.\mvnw.cmd clean test
```

## Limitação da validação
O ambiente de preparação não conseguiu baixar o Maven Wrapper de `repo.maven.apache.org`, portanto os testes foram adicionados mas não puderam ser executados aqui. Execute os comandos acima localmente e corrija qualquer falha reportada antes de promover a versão.
