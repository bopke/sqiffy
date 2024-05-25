---
outline: deep
aside: false
---

# Interacting with DSL

To compile the schema definition into a type-safe Kotlin DSL API, you need to run KSP _(Kotlin Symbol Processing)_ task.
It's automatically added to your build process, but building whole project can be time-consuming, so you can run it separately:

```bash
$ gradle kspKotlin
```

After running the task, you can start using the generated DSL API in your project.
By default, the generated API is placed in the `build/generated/ksp` directory.
For a definition we've created in the previous section, the generated API will contain:

* `User` - a data class representing the record in `users` table
* `UnidentifiedUser` - a data class without autogenerated keys _(like e.g. serial id)_
* `UserTable` - a class representing the `users` table for built-in DSL
* `UserTableNames` - object with table & column names
* SQL changelog for migrations between each version

Let's try to insert a new user into the database. We need to create database instance:

```kotlin
val database = Sqiffy.createDatabase(
    dataSource = createHikariDataSource(
        driver = "org.postgresql.Driver",
        url = jdbcUrl,
        username = username,
        password = password
    ),
    logger = Slf4JSqiffyLogger(LoggerFactory.getLogger(SqiffyDatabase::class.java))
)
```

To initialize schema in the database, we can use migrator. 
See [migrations](migrations.md) guide for more details, for now let's use the default one:

```kotlin
val changelog = database.generateChangeLog(tables = listOf(UserDefinition::class))
val sqiffyMigrator = SqiffyMigrator(changeLog = changelog)
database.runMigrations(sqiffyMigrator)
```

And with the following code we can insert a new user:

```kotlin
val newUser = UnidentifiedUser(
    name = "Panda"
)

val insertedUser = database
    .insert(newUser)
    .map { userToInsert.withId(id = it[UserTable.id]) }
    .first()
```

That's it! You've just inserted a new user into the database using Sqiffy DSL API. 
Let's try to query the user from the database:

```kotlin
val userFromDatabase = database.select(UserTable)
    .where { UserTable.name eq "Panda" }
    .map { it.toUser() }
    .first()
```

To learn more about the DSL API and all its features, see the [DSL API docs](/dsl/queries).