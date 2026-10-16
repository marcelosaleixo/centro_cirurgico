# Correção — PasswordEncoder ausente no classpath

## Causa
O erro `NoClassDefFoundError: org/springframework/security/crypto/password/PasswordEncoder`
indica que a classe do Spring Security Crypto não está disponível no classpath usado para iniciar
a aplicação. O log não aponta problema de banco de dados. A dependência `spring-boot-starter-security`
deve trazer essa biblioteca transitivamente; esta correção também declara `spring-security-crypto`
explicitamente e recomenda reconstruir o projeto para eliminar um classpath antigo.

## Passos no PowerShell
Na raiz do projeto (onde está o `pom.xml`):

```powershell
# Pare a aplicação na IDE antes de executar
.\mvnw.cmd -U dependency:tree -Dincludes=org.springframework.security:spring-security-crypto
.\mvnw.cmd -U clean package -DskipTests
.\mvnw.cmd spring-boot:run
```

A árvore deve mostrar `org.springframework.security:spring-security-crypto` no classpath.
Na IDE, faça Maven > Reload/Reimport Project e encerre/reinicie a configuração de execução.

Se persistir, confira se está executando a mesma pasta de projeto e se a configuração da IDE usa
o classpath Maven, não um classpath manual/antigo. Não remova Spring Security nem o `PasswordEncoder`,
pois isso enfraqueceria a autenticação.

A compilação precisa ser validada localmente; não foi executada neste ambiente.
