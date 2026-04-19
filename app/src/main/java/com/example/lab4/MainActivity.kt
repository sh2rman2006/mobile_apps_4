package com.example.lab4

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: SQLHelper
    private lateinit var container: LinearLayout

    private lateinit var etFullName: EditText
    private lateinit var etClassNumber: EditText
    private lateinit var etClassLetter: EditText
    private lateinit var etAverageGrade: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = SQLHelper(this)

        container = findViewById(R.id.container)

        etFullName = findViewById(R.id.etFullName)
        etClassNumber = findViewById(R.id.etClassNumber)
        etClassLetter = findViewById(R.id.etClassLetter)
        etAverageGrade = findViewById(R.id.etAverageGrade)

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        val btnShow = findViewById<Button>(R.id.btnShow)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        btnAdd.setOnClickListener {
            addStudent()
        }

        btnShow.setOnClickListener {
            showAllStudents()
        }

        btnUpdate.setOnClickListener {
            updateFirstStudent()
        }

        btnDelete.setOnClickListener {
            deleteLastStudent()
        }
    }

    private fun addStudent() {
        val fullName = etFullName.text.toString().trim()
        val classNumberText = etClassNumber.text.toString().trim()
        val classLetter = etClassLetter.text.toString().trim()
        val averageGradeText = etAverageGrade.text.toString().trim()

        if (fullName.isEmpty() || classNumberText.isEmpty() || classLetter.isEmpty() || averageGradeText.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val classNumber = classNumberText.toIntOrNull()
        if (classNumber == null) {
            Toast.makeText(this, "Класс должен быть числом", Toast.LENGTH_SHORT).show()
            return
        }

        val averageGrade = averageGradeText.toDoubleOrNull()
        if (averageGrade == null) {
            Toast.makeText(this, "Средняя оценка должна быть числом", Toast.LENGTH_SHORT).show()
            return
        }

        val id = dbHelper.addStudent(fullName, classNumber, classLetter, averageGrade)

        if (id != -1L) {
            Toast.makeText(this, "Ученик добавлен", Toast.LENGTH_SHORT).show()
            clearFields()
            showAllStudents()
        } else {
            Toast.makeText(this, "Ошибка добавления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAllStudents() {
        val students = dbHelper.getAllStudents()

        container.removeAllViews()

        if (students.isEmpty()) {
            val textView = TextView(this)
            textView.text = "Список учеников пуст"
            textView.textSize = 16f
            container.addView(textView)
            return
        }

        for (student in students) {
            val textView = TextView(this)
            textView.text =
                "ID: " + student.id +
                        ", ФИО: " + student.fullName +
                        ", Класс: " + student.classNumber + student.classLetter +
                        ", Средняя оценка: " + student.averageGrade
            textView.textSize = 16f
            textView.setPadding(8, 8, 8, 8)
            container.addView(textView)
        }
    }

    private fun updateFirstStudent() {
        val students = dbHelper.getAllStudents()

        if (students.isEmpty()) {
            Toast.makeText(this, "Нет записей для обновления", Toast.LENGTH_SHORT).show()
            return
        }

        val firstStudent = students[0]
        firstStudent.fullName = firstStudent.fullName + " (обновлен)"
        firstStudent.averageGrade = firstStudent.averageGrade + 0.1

        val result = dbHelper.updateStudent(firstStudent)

        if (result > 0) {
            Toast.makeText(this, "Первая запись обновлена", Toast.LENGTH_SHORT).show()
            showAllStudents()
        } else {
            Toast.makeText(this, "Ошибка обновления", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteLastStudent() {
        val students = dbHelper.getAllStudents()

        if (students.isEmpty()) {
            Toast.makeText(this, "Нет записей для удаления", Toast.LENGTH_SHORT).show()
            return
        }

        val lastStudent = students[students.size - 1]
        dbHelper.deleteStudent(lastStudent.id)

        Toast.makeText(this, "Последняя запись удалена", Toast.LENGTH_SHORT).show()
        showAllStudents()
    }

    private fun clearFields() {
        etFullName.text.clear()
        etClassNumber.text.clear()
        etClassLetter.text.clear()
        etAverageGrade.text.clear()
    }
}