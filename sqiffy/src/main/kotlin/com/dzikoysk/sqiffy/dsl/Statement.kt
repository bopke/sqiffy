package com.dzikoysk.sqiffy.dsl

import com.dzikoysk.sqiffy.shared.get
import org.jdbi.v3.core.result.RowView

interface Statement

class Row(val view: RowView) {
    operator fun <T> get(column: Column<T>): T = view[column]
    operator fun <T> get(column: Aggregation<T>): T = view[column]
}

class Values {

    private val values: MutableMap<Column<*>, Any?> = mutableMapOf()

    operator fun <T : Any?> set(column: Column<T>, value: T) {
        values[column] = value
    }

    fun getColumn(column: String): Column<*>? =
        values.entries
            .firstOrNull { it.key.name == column }
            .let { it?.key }

    fun getValue(column: Column<*>): Any? =
        values[column]

    fun getColumns(): Set<Column<*>> =
        values.keys

    fun getValues(): Map<Column<*>, Any?> =
        values

}