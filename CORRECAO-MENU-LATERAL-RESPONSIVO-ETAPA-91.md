# Etapa 91 — Correção final do menu lateral responsivo

## Alterações
- Mantém a barra lateral fixa em telas grandes.
- Ativa o menu recolhido com botão e fundo escurecido em telas de até 1024px, incluindo tablets em orientação horizontal.
- Evita que o conteúdo fique deslocado pela largura da barra lateral em dispositivos móveis.
- Garante rolagem vertical dentro do menu e impede rolagem horizontal da página.
- Melhora espaçamento, quebra de textos longos, área de toque e foco por teclado.
- Respeita `prefers-reduced-motion`.
- Mantém os links e rotas existentes.

## Validação
O arquivo CSS e o template de navegação foram inspecionados e o ZIP foi validado estruturalmente. A aplicação não foi iniciada com PostgreSQL neste ambiente.

## Teste manual
1. Execute `./mvnw clean compile` (Windows: `.mvnw.cmd clean compile`).
2. Abra uma tela do sistema e teste larguras de 1440px, 1024px, 768px e 390px.
3. Em larguras até 1024px, use o botão Menu para abrir/fechar a barra lateral; toque no fundo escurecido ou pressione Escape para fechar.
