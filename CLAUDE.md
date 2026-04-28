# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Run (Desktop)
```bash
./gradlew :composeApp:run
```

### Build
```bash
# Android APK
./gradlew :composeApp:assembleDebug

# Desktop distributable
./gradlew :composeApp:createDistributable
```

### Tests
```bash
# All tests
./gradlew :composeApp:allTests

# JVM tests only
./gradlew :composeApp:jvmTest

# Android unit tests
./gradlew :composeApp:testDebugUnitTest

# Single test class
./gradlew :composeApp:jvmTest --tests "br.com.mochila.YourTestClass"
```

### Lint / Check
```bash
./gradlew :composeApp:check
```

## Architecture

**Single Gradle module** (`:composeApp`) targeting Android and JVM desktop via Kotlin Multiplatform. All shared code lives in `commonMain`; platform-specific entry points are in `androidMain` and `jvmMain`.

### Entry Points

- **Desktop:** `jvmMain/.../main.kt` — calls `DatabaseHelper.connect()` then launches a Compose `Window` with `App()`
- **Android:** `androidMain/.../MainActivity.kt` — `ComponentActivity` that calls `setContent { App() }`

### Navigation

`App.kt` (commonMain) owns all navigation. There is **no Jetpack Navigation or NavHost** — navigation is a manually managed `screenStack: List<String>` (push/pop). Shared navigation context (`currentUserId`, `selectedSubjectId`, `selectedTaskId`) is passed down as lambda callbacks to each screen composable.

Flow: `login → home` (main path) and `login → recovery → email_code → new_password` (recovery path). Email verification (`email_verify`) is required before reaching `home`.

### Data Layer

`DatabaseHelper` manages a single JDBC `Connection` to `mochila.db` (SQLite). On first connection it runs `composeResources/files/db_init.sql` to create the schema, then applies incremental migrations inline (adding `token_recuperacao`, `email_verificado`, `foto_perfil`). All repositories use **raw parameterized SQL** via `DatabaseHelper.executeQuery()` / `executeUpdate()` — no ORM.

Repositories are Kotlin `object` singletons: `UserRepository`, `SubjectRepository`, `TaskRepository`, `TokenRepository`.

`UserSession` is a Compose-state singleton that holds the currently authenticated `User?` and is read directly by screens that need the current user.

### Presenter Pattern (MVP)

Each screen has a matching `Presenter` class. The screen defines a `View` interface, then instantiates a local `remember { }` block that creates an anonymous implementation of the interface wired to local `mutableStateOf` variables, and passes it to the presenter. Business logic (validation, repository calls) lives entirely in the presenter.

### Email / SendGrid

`EmailService` reads credentials from `composeApp/sendgrid.properties` (or env vars `SENDGRID_API_KEY`, `SENDGRID_SENDER_EMAIL`). This file is required for email verification and password recovery flows to work. Check `EmailService.isConfigured` before calling send methods.

### Password Security

`PasswordHash` uses PBKDF2-HMAC-SHA256 (100 000 iterations, 16-byte random salt). Stored format: `<hex_salt>:<hex_hash>`. Always use `PasswordHash.hash()` / `verify()` — never store or compare plain-text passwords.

### Database Schema Key Points

- Main tables: `usuario`, `disciplina`, `tarefa`
- `usuario` has `email_verificado` (boolean) and `foto_perfil` (nullable path) columns added via migration
- `token_recuperacao` stores password-reset tokens with a 15-minute expiry; tokens are consumed (marked used) on validation
- All foreign keys use `ON DELETE CASCADE`
