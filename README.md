# Capital Gains Calculator CLI

Esta aplicação é uma CLI para calcular impostos sobre ganho de capital em operações financeiras, desenvolvida com Spring Boot e Spring Shell.

## Como executar

1. Clone o repositório e navegue até a pasta do projeto.
2. Compile e execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Use o comando `calcular` no terminal seguido de um JSON com as operações.
   Exemplo:
   ```bash
   calcular '[{"operation":"buy","unit-cost":10.00,"quantity":100},{"operation":"sell","unit-cost":15.00,"quantity":50}]'
   ```

## Como rodar os testes

Execute os testes com o Maven:
```bash
./mvnw test
```

## Decisões Técnicas

- **Spring Shell**: Facilita a implementação de comandos interativos.
- **Jackson**: Para manipulação de JSON.
- **BigDecimal**: Para cálculos precisos com valores monetários.