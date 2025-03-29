package com.deakin.todoapp2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class AddTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Task storage stuff
        val prefs: SharedPreferences = this.getSharedPreferences("task_prefs", MODE_PRIVATE)
        val gson = Gson()

        // Save tasks as a JSON string
        fun saveTasks(tasks: List<String>) {
            val json = gson.toJson(tasks)
            prefs.edit { putString("tasks", json) }
        }

        // Retrieve tasks
        fun getTasks(): List<String> {
            val json = prefs.getString("tasks", "[]") ?: "[]"
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json, type)
        }

        // View elements stuff
        // submit button
        val submitButton = findViewById<Button>(R.id.AddTaskButton)
        val taskNameInput = findViewById<EditText>(R.id.taskNameInput)
        val taskDescriptionInput = findViewById<EditText>(R.id.taskDescriptionInput)

        submitButton.setOnClickListener {
            val taskName = taskNameInput.text.toString().trim()
            val taskDescription = taskDescriptionInput.text.toString().trim()

            if (taskName.isEmpty()) {
                taskNameInput.error = "Task name cannot be empty"
                return@setOnClickListener
            }

            // from ChatGPT
            val tasks = getTasks().toMutableList()
            tasks.add("$taskName: $taskDescription")
            saveTasks(tasks)

            // Intent to go back to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}