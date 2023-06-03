package com.example.proyecto

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

class DestinoTuristicoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "destinos_turisticos.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_DESTINOS_TURISTICOS = "destinos_turisticos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NOMBRE = "nombre"
        private const val COLUMN_DIRECCION = "direccion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = "CREATE TABLE $TABLE_DESTINOS_TURISTICOS " +
                "($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOMBRE TEXT, " +
                "$COLUMN_DIRECCION TEXT)"
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_DESTINOS_TURISTICOS"
        db.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertDestinoTuristico(nombre: String, direccion: String): Long {
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_DIRECCION, direccion)
        }
        val db = writableDatabase
        return db.insert(TABLE_DESTINOS_TURISTICOS, null, values)
    }

    fun isTableExists(tableName: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'",
            null
        )
        val tableExists = cursor.moveToFirst()
        cursor.close()
        return tableExists
    }

    @SuppressLint("Range")
    fun getAllDestinosTuristicos(): List<DestinoTuristico> {
        val destinosTuristicos = mutableListOf<DestinoTuristico>()

        val query = "SELECT * FROM $TABLE_DESTINOS_TURISTICOS"
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE))
                val direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION))


                val destinoTuristico = DestinoTuristico(id, nombre, direccion)
                destinosTuristicos.add(destinoTuristico)
                // Resto del código para acceder a las columnas
            }

        }

        cursor.close()
        db.close()

        return destinosTuristicos
    }

    @SuppressLint("Range")
    fun getDestinoTuristicoById(id: Int): DestinoTuristico? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_DESTINOS_TURISTICOS WHERE $COLUMN_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query, selectionArgs)

        if (cursor.moveToFirst()) {
            val nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE))
            val direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION))

            cursor.close()
            db.close()

            return DestinoTuristico(id, nombre, direccion)
        }

        cursor.close()
        db.close()

        return null
    }

    fun getDestinosTuristicosCount(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_DESTINOS_TURISTICOS"
        val cursor = db.rawQuery(query, null)

        var count = 0

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()

        return count
    }
}


data class DestinoTuristico(val id: Int, val nombre: String, val direccion: String)

