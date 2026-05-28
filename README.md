# Mochila App

Aplicativo **Mochila Hub** em [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) com [Compose Multiplatform](https://www.jetbrains.com/compose-multiplatform/): mesma base de código para **Android** e **desktop (JVM)**, com Material 3, SQLite e fluxos de estudo (disciplinas, tarefas, cadastro e conta).

## Sumário

- [Sobre o projeto](#sobre-o-projeto)
- [Stack](#stack)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do Android Studio](#configuração-do-android-studio)
- [Configuração do SendGrid (e-mail)](#configuração-do-sendgrid-e-mail)
- [Como rodar](#como-rodar)
- [Testes e verificação](#testes-e-verificação)
- [Estrutura do repositório](#estrutura-do-repositório)
- [Documentação e referências](#documentação-e-referências)
- [Contribuindo](#contribuindo)

## Sobre o projeto

Cliente multiplataforma com UI em Compose, persistência local (SQLite) e recursos compartilhados (`composeResources`). O módulo principal é `:composeApp`, com código comum em `commonMain` e alvos em `androidMain` e `jvmMain`.

## Stack

| Área | Tecnologia |
|------|------------|
| Linguagem | Kotlin 2.2.x |
| UI | Compose Multiplatform, Material 3 |
| Android | AGP 8.11, `minSdk` 24, `compileSdk` / `targetSdk` 36 |
| Desktop | Compose for Desktop, JVM 11 |
| Dados | SQLite (driver JDBC no common), `kotlinx-datetime` |
| Build | Gradle 8.14 (wrapper), version catalog (`gradle/libs.versions.toml`) |

## Pré-requisitos

- **JDK 11** (alvo de bytecode do projeto).
- **Android Studio** (recomendado) ou Android SDK + variáveis de ambiente, para compilar/instalar o app Android.
- Para **desktop**, nenhum SDK Android é necessário — apenas JDK e o Gradle Wrapper do repositório.

## Configuração do Android Studio

1. **Versão da IDE** — Use uma versão **estável e recente** do [Android Studio](https://developer.android.com/studio), compatível com o **Android Gradle Plugin** declarado em `gradle/libs.versions.toml` (hoje **8.11.x**). Isso evita avisos de incompatibilidade na sincronização do Gradle.

2. **Abrir o projeto** — `File` → `Open` e selecione a **pasta raiz** do repositório (`mochila-app`), **não** apenas `composeApp/`. Aguarde o **Gradle Sync** terminar; na primeira abertura o download do JDK/dependências pode levar alguns minutos.

3. **JDK do Gradle** — `File` → `Settings` (ou `Android Studio` → `Settings` no macOS) → `Build, Execution, Deployment` → `Build Tools` → `Gradle` → **Gradle JDK**. Escolha um **JDK 17 ou superior** (o JBR embutido no Android Studio costuma ser suficiente). O projeto compila para **bytecode JVM 11**, mas o Gradle e o AGP atuais exigem um JDK mais novo para *executar* o build.

4. **Android SDK** — `Tools` → `SDK Manager`:
   - **SDK Platforms**: instale a plataforma com **API 36** (alinhada ao `compileSdk` do projeto) e, se quiser testar no mínimo suportado, uma imagem com **API 24+** (`minSdk`).
   - **SDK Tools**: mantenha **Android SDK Build-Tools**, **Platform-Tools** e **Android SDK Command-line Tools** atualizados.

5. **Executar no Android** — Com um emulador (`Device Manager`) ou aparelho físico (USB com depuração) disponível, escolha a configuração de execução do app **composeApp** na barra de *Run* e execute **Run** (`Shift+F10` / `Ctrl+R` no macOS conforme atalho configurado).

6. **Executar o desktop (JVM)** — Se não existir configuração pronta: `Run` → `Edit Configurations…` → `+` → **Gradle** → selecione o projeto Gradle **`composeApp`** na árvore → em **Run** / *Tasks* use `:composeApp:run` (ou `composeApp:run`). Salve e execute para abrir a janela **Mochila Hub**.

7. **Plugins úteis** — O suporte a **Kotlin** e **Compose** já vem integrado; para fluxos **Kotlin Multiplatform**, confira na `Settings` → `Plugins` se há atualizações do ecossistema JetBrains recomendadas para a sua versão do Studio.

## Configuração do SendGrid (e-mail)

O envio de e-mails (verificação de conta e recuperação de senha) usa a API do [SendGrid](https://sendgrid.com). Para que esses fluxos funcionem localmente, crie o arquivo `composeApp/sendgrid.properties` com suas credenciais:

```properties
SENDGRID_API_KEY=sua_chave_aqui
SENDGRID_SENDER_EMAIL=seu_email_aqui
SENDGRID_SENDER_NAME=Mochila Hub
```

> **Importante:** esse arquivo esta listado no `.gitignore` e **nunca deve ser commitado**. Para referencia, o repositorio inclui `composeApp/sendgrid.properties.example` com o formato esperado.

Se o arquivo nao existir (ou as variaveis de ambiente `SENDGRID_API_KEY` e `SENDGRID_SENDER_EMAIL` nao estiverem definidas), o app inicializa normalmente, mas os fluxos de e-mail ficam desabilitados — `EmailService.isConfigured` retornara `false`.

## Como rodar

Na raiz do repositório, use o Gradle Wrapper.

### Android

**Compilar o APK de debug:**

```shell
# macOS / Linux
./gradlew :composeApp:assembleDebug
```

```powershell
# Windows (PowerShell ou CMD)
.\gradlew.bat :composeApp:assembleDebug
```

**Instalar no dispositivo/emulador conectado** (útil para testar rapidamente):

```shell
./gradlew :composeApp:installDebug
```

```powershell
.\gradlew.bat :composeApp:installDebug
```

No Android Studio, após a [configuração da IDE](#configuração-do-android-studio), use a configuração de execução **Android** do `composeApp` na barra de *Run*.

### Desktop (JVM)

```shell
./gradlew :composeApp:run
```

```powershell
.\gradlew.bat :composeApp:run
```

A janela desktop usa o título **Mochila Hub** (`jvmMain`). Pacotes nativos (MSI, DMG, DEB) podem ser gerados pelas tarefas de distribuição do Compose Desktop configuradas em `composeApp/build.gradle.kts` (`nativeDistributions`).

### Hot reload (desenvolvimento)

O plugin **Compose Hot Reload** está aplicado no módulo `composeApp`. Consulte a [documentação oficial do hot reload](https://github.com/JetBrains/compose-hot-reload) para o fluxo recomendado com sua IDE.

## Testes e verificação

```shell
./gradlew :composeApp:allTests
./gradlew :composeApp:check
```

```powershell
.\gradlew.bat :composeApp:allTests
.\gradlew.bat :composeApp:check
```

- `allTests` — testes agregados dos alvos configurados.
- `check` — checagens padrão do módulo (inclui testes e outras verificações configuradas).

## Estrutura do repositório

| Caminho | Descrição |
|---------|-----------|
| `composeApp/` | Módulo KMP + Android Application + Compose Desktop |
| `composeApp/src/commonMain/` | UI e lógica compartilhada (`br.com.mochila`, recursos Compose) |
| `composeApp/src/androidMain/` | Entrada Android (`MainActivity`), integrações Android |
| `composeApp/src/jvmMain/` | Entrada desktop (`main.kt`, janela **Mochila Hub**) |
| `composeApp/src/commonTest/` | Testes comuns |
| `gradle/libs.versions.toml` | Versões e catálogo de dependências |
| `settings.gradle.kts` | Nome do projeto e inclusão de `:composeApp` |

**Identificadores Android:** `applicationId` e `namespace` — `br.com.mochila`.

## Documentação e referências

- [Compose Multiplatform — Get started](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Padrão “Standard Readme” (estrutura de README)](https://github.com/RichardLitt/standard-readme) — inspiração para seções e ordem lógica (descrição → instalação/uso → extras → contribuição).

## Contribuindo

Pull requests e issues são bem-vindos. Para dúvidas, use as issues do repositório. Mantenha o estilo e a organização já usados no código (por exemplo, pacote `br.com.mochila` e separação `ui` / `presenter` / `data` onde aplicável).
