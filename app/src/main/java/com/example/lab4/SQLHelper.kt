package com.example.lab4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addStudent(
        fullName: String,
        classNumber: Int,
        classLetter: String,
        averageGrade: Double
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_FULL_NAME, fullName)
        values.put(COLUMN_CLASS_NUMBER, classNumber)
        values.put(COLUMN_CLASS_LETTER, classLetter)
        values.put(COLUMN_AVERAGE_GRADE, averageGrade)

        val id = db.insert(TABLE_NAME, null, values)
        db.close()

        return id
    }

    fun getAllStudents(): ArrayList<Person> {
        val studentList = ArrayList<Person>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val fullName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULL_NAME))
                val classNumber = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NUMBER))
                val classLetter = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_LETTER))
                val averageGrade = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AVERAGE_GRADE))

                val person = Person(id, fullName, classNumber, classLetter, averageGrade)
                studentList.add(person)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return studentList
    }

    fun updateStudent(person: Person): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_FULL_NAME, person.fullName)
        values.put(COLUMN_CLASS_NUMBER, person.classNumber)
        values.put(COLUMN_CLASS_LETTER, person.classLetter)
        values.put(COLUMN_AVERAGE_GRADE, person.averageGrade)

        val result = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_ID = ?",
            arrayOf(person.id.toString())
        )

        db.close()
        return result
    }

    fun deleteStudent(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    companion object {
        const val DATABASE_NAME = "school.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "students"

        const val COLUMN_ID = "_id"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_CLASS_NUMBER = "class_number"
        const val COLUMN_CLASS_LETTER = "class_letter"
        const val COLUMN_AVERAGE_GRADE = "average_grade"

        private const val CREATE_TABLE = """
            CREATE TABLE students (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name TEXT NOT NULL,
                class_number INTEGER,
                class_letter TEXT,
                average_grade REAL
            )
        """
    }
}