package com.dzikoysk.sqiffy.dsl

import com.dzikoysk.sqiffy.SqiffyDatabase
import com.dzikoysk.sqiffy.dsl.statements.AutogeneratedKeyInsertStatement
import com.dzikoysk.sqiffy.dsl.statements.DeleteStatement
import com.dzikoysk.sqiffy.dsl.statements.InsertStatement
import com.dzikoysk.sqiffy.dsl.statements.SelectStatement
import com.dzikoysk.sqiffy.dsl.statements.UpdateStatement
import com.dzikoysk.sqiffy.dsl.statements.UpdateValues
import com.dzikoysk.sqiffy.transaction.HandleAccessor
import org.jdbi.v3.core.Handle

interface DslHandle {

    fun getDatabase(): SqiffyDatabase

    fun getHandleAccessor(): HandleAccessor

    fun select(table: Table): SelectStatement =
        SelectStatement(getDatabase(), getHandleAccessor(), table)

    fun <KEY> insert(table: TableWithAutogeneratedKey<KEY>, values: (Values) -> Unit): AutogeneratedKeyInsertStatement =
        AutogeneratedKeyInsertStatement(getDatabase(), getHandleAccessor(), table, Values().also { values.invoke(it) })

    fun insert(table: Table, values: (Values) -> Unit): InsertStatement =
        InsertStatement(getDatabase(), getHandleAccessor(), table, Values().also { values.invoke(it) })

    fun update(table: Table, values: (UpdateValues) -> Unit): UpdateStatement =
        UpdateStatement(getDatabase(), getHandleAccessor(), table, UpdateValues().also { values.invoke(it) })

    fun delete(table: Table): DeleteStatement =
        DeleteStatement(getDatabase(), getHandleAccessor(), table)

}

class JdbiDslHandle(private val database: SqiffyDatabase, private val handle: Handle) : DslHandle, HandleAccessor {

    override fun <R> inHandle(body: (Handle) -> R): R =
        body.invoke(handle)

    override fun getHandleAccessor(): HandleAccessor =
        this

    override fun getDatabase(): SqiffyDatabase =
        database

}