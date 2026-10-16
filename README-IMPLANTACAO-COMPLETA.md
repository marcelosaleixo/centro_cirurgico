# Projeto completo — Centro Cirúrgico

## Base consolidada
Este pacote parte da versão com correção da constraint de movimentação de estoque (Flyway V15), inclui cadastro de salas cirúrgicas, gestão de usuários já existente, menu lateral por módulos responsivo, configuração de implantação e CSV de feriados.

## Stack
- Java 21
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Thymeleaf
- Apache POI para exportação Excel

## Antes de iniciar
1. Crie o banco `centro_cirurgico` no PostgreSQL ou ajuste `DB_URL`.
2. Configure as variáveis `DB_USERNAME`, `DB_PASSWORD`, `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD`.
3. Use uma senha administrativa exclusiva com no mínimo 12 caracteres. Não coloque senhas reais no Git.
4. Em produção, use HTTPS e defina `SESSION_COOKIE_SECURE=true`.
5. Confirme backup do banco antes de aplicar migrações. O Flyway aplica as migrations existentes automaticamente.

## Criar o administrador inicial
`UsuarioSistemaInicializador` lê `RH_ADMIN_USERNAME` e `RH_ADMIN_PASSWORD`. Se a conta não existir, cria-a com senha BCrypt e perfil `ADMIN_RH`. Se a senha não estiver configurada ou for curta, o inicializador registra erro e não cria a conta. Configure a variável de ambiente antes de iniciar.

PowerShell (apenas sessão atual):
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/centro_cirurgico"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="SUA_SENHA_DO_POSTGRES"
$env:RH_ADMIN_USERNAME="admin.rh"
$env:RH_ADMIN_PASSWORD="SENHA_FORTE_EXCLUSIVA_COM_12+_CARACTERES"
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## Cadastro de feriados na implantação
O arquivo `dados/feriados-implantacao.csv` e sua cópia em `src/main/resources/data/` são modelos de referência com feriados nacionais fixos de 2026/2027. A aplicação atual não importa CSV automaticamente. Cadastre pela tela `/feriados` usando a abrangência `NACIONAL`, ou use o CSV como base para importação manual após conferir as datas.

O CSV não inclui feriados móveis, estaduais ou municipais. Confirme os feriados aplicáveis ao hospital e a legislação vigente antes de cadastrar.

## Salas e usuários
- Salas cirúrgicas: `/salas-cirurgicas` (usuário autenticado).
- Gestão de usuários: `/admin/usuarios` (perfil `ADMIN_RH`).
- Feriados: `/feriados`.
- Dashboard: `/dashboard`.
- Fechamento RH: `/relatorios/fechamento-rh`.

## Validação
Execute `mvnw.cmd clean test` localmente antes de implantar. Este pacote foi montado pela consolidação dos artefatos existentes; os testes Maven não foram executados neste ambiente.
