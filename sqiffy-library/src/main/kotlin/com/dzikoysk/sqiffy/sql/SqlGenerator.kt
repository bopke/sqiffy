package com.dzikoysk.sqiffy.sql

import com.dzikoysk.sqiffy.DataType.BINARY
import com.dzikoysk.sqiffy.DataType.BLOB
import com.dzikoysk.sqiffy.DataType.BOOLEAN
import com.dzikoysk.sqiffy.DataType.CHAR
import com.dzikoysk.sqiffy.DataType.DATE
import com.dzikoysk.sqiffy.DataType.DATETIME
import com.dzikoysk.sqiffy.DataType.DOUBLE
import com.dzikoysk.sqiffy.DataType.FLOAT
import com.dzikoysk.sqiffy.DataType.INT
import com.dzikoysk.sqiffy.DataType.TEXT
import com.dzikoysk.sqiffy.DataType.TIMESTAMP
import com.dzikoysk.sqiffy.DataType.UUID_VARCHAR
import com.dzikoysk.sqiffy.DataType.VARCHAR
import com.dzikoysk.sqiffy.PropertyData

interface SqlGenerator {

    fun createTable(name: String, properties: List<PropertyData>): String

    fun renameTable(currentName: String, renameTo: String): String

    fun createColumn(tableName: String, property: PropertyData): String

    fun renameColumn(tableName: String, currentName: String, renameTo: String): String

    fun removeColumn(tableName: String, columnName: String): String

}

class MySqlGenerator : SqlGenerator {

    override fun createTable(name: String, properties: List<PropertyData>): String =
        """CREATE TABLE "$name" (${properties.joinToString(separator = ", ") { "\"${it.name}\" ${createDataType(it)}" }})"""

    override fun renameTable(currentName: String, renameTo: String): String =
        """ALTER TABLE "$currentName" RENAME "$renameTo""""

    override fun createColumn(tableName: String, property: PropertyData): String =
        """ALTER TABLE "$tableName" ADD "${property.name}" ${createDataType(property)}"""

    override fun renameColumn(tableName: String, currentName: String, renameTo: String): String =
        """ALTER TABLE "$tableName" RENAME COLUMN "$currentName" TO "$renameTo""""

    override fun removeColumn(tableName: String, columnName: String): String =
        """ALTER TABLE "$tableName" DROP COLUMN "$columnName""""

    private fun createDataType(property: PropertyData): String =
        with (property) {
            when (type) {
                CHAR -> "CHAR($details)"
                UUID_VARCHAR -> "VARCHAR(36)"
                VARCHAR -> "VARCHAR($details)"
                BINARY -> "BINARY($details)"
                TEXT -> "TEXT"
                BLOB -> "BLOB"
                BOOLEAN -> "BOOLEAN"
                INT -> "INT"
                FLOAT -> "FLOAT"
                DOUBLE -> "DOUBLE"
                DATE -> "DATE"
                DATETIME -> "DATETIME"
                TIMESTAMP -> "TIMESTAMP"
                else -> throw UnsupportedOperationException("Cannot create data type based on $property")
            }
        }

}