# Correção da causa raiz — calendário sem colaboradores

## Diagnóstico do ZIP enviado
A geração das escalas e a consulta do controller estavam separadas da renderização: o controller entregava um mapa indexado por data e o template precisava resolver a chave do mapa via SpringEL/Thymeleaf. Mesmo após os ajustes anteriores, o calendário continuava sem exibir os registros.

## Alteração definitiva nesta versão
- Criada a classe `DiaCalendarioEscala` como modelo de apresentação.
- O controller agrupa as escalas por `LocalDate` no Java e monta cada dia do calendário com sua própria lista de escalas.
- O Thymeleaf renderiza diretamente `${dia.escalas}` e não faz lookup em mapa por string/data.
- A identificação de mês atual, fim de semana e dia sem escala é calculada no DTO.
- Não altera a lógica de geração, entidades ou dados do PostgreSQL.

## Teste
1. Faça uma cópia de segurança do projeto (não do banco é necessário para esta mudança de visualização).
2. Substitua os arquivos pelo projeto deste ZIP.
3. Execute `.\mvnw.cmd clean package -DskipTests`.
4. Reinicie a aplicação e acesse `/escalas/calendario?mes=2026-10&colaboradorId=1`.
5. As escalas já geradas para outubro devem ser exibidas dentro das células do calendário.

Se ainda não aparecer, confirme a existência dos registros na tabela `escala_plantao`; a geração informada na mensagem indica que foram processados dias, mas apenas uma consulta ao banco pode confirmar persistência.
