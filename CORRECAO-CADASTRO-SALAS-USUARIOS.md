# Correção — Cadastro de salas cirúrgicas e usuários

## O que foi incluído
- CRUD de salas cirúrgicas em `/salas-cirurgicas` (listar, criar, editar e ativar/desativar).
- Formulário responsivo para nome, localização e status.
- Link para **Salas cirúrgicas** no menu lateral.
- Link para **Gestão de usuários** no menu lateral, que abre `/admin/usuarios`.
- O cadastro de usuários já existente foi preservado; seu acesso continua restrito a `ADMIN_RH` pela configuração de segurança.
- A autorização para consulta de salas foi incluída na lista de rotas de leitura; gravações exigem `ADMIN_RH` ou `RH`.
- Não foi criada migração, pois `sala_cirurgica` já existe na migração inicial.

## Como testar
1. Execute `./mvnw clean test` (Windows: `./mvnw.cmd clean test`).
2. Inicie a aplicação.
3. Entre com perfil `ADMIN_RH` ou `RH` e abra `/salas-cirurgicas`.
4. Cadastre uma sala ativa e acesse `/estoque/recursos/armarios/novo`; a sala deverá aparecer no seletor.
5. Entre com `ADMIN_RH` e abra `/admin/usuarios` para cadastrar contas. O formulário exige senha inicial com pelo menos 12 caracteres.

## Segurança
A tela de gestão de usuários continua protegida para `ADMIN_RH`. O link é visível no menu, mas perfis sem autorização serão bloqueados pelo Spring Security. Para esconder o link de outros perfis, pode-se adicionar o Thymeleaf Spring Security Extras e `sec:authorize` em uma alteração posterior.

A compilação precisa ser confirmada no ambiente local.
