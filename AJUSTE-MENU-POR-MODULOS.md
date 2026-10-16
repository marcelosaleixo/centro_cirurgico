# Ajuste do menu por módulos

O menu lateral foi reorganizado em grupos para facilitar a localização das funções:

- **Visão geral:** Dashboard.
- **Recursos Humanos:** colaboradores, escalas, fechamento do RH, férias, feriados, banco de horas, trocas, equipes e funções.
- **Suprimentos e estrutura:** estoque, carrinhos/armários e salas cirúrgicas.
- **Administração:** gestão de usuários.
- **Conta e segurança:** senha, auditorias e painel de segurança.

Os caminhos existentes foram preservados. O CSS adiciona separadores discretos entre os grupos. A autorização continua sendo aplicada pelas regras de segurança do servidor; ocultar um item no menu não substitui a autorização.

## Validar
1. Execute `./mvnw clean test` (Windows: `mvnw.cmd clean test`).
2. Inicie a aplicação e atualize o navegador com `Ctrl+F5`.
3. Confira a navegação em desktop e celular.
