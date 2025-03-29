package com.deakin.todoapp2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import androidx.core.content.ContextCompat

class EditTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Task storage stuff
        val prefs: SharedPreferences = this.getSharedPreferences("task_prefs", MODE_PRIVATE)
        val gson = Gson()

        // Save tasks as a JSON string
        fun saveTasks(tasks: List<Task>) {
            val json = gson.toJson(tasks)
            prefs.edit { putString("tasks", json) }
        }

        // Retrieve tasks
        fun getTasks(): List<Task> {
            val json = prefs.getString("tasks", "[]") ?: "[]"
            val type = object : TypeToken<List<Task>>() {}.type
            return gson.fromJson(json, type)
        }

        fun editTask(index: Int, taskName: String, taskDescription: String) {
            val tasks = getTasks().toMutableList()

            if (index in tasks.indices) { // Ensure index is valid
                tasks[index] = Task(taskName, taskDescription, tasks[index].done) // Preserve "done" status
                saveTasks(tasks)
            } else {
                Log.e("editTask", "Invalid task index: $index") // Debugging log
            }
        }

        fun updateDoneButton(button: Button, done: Boolean) {
            val greenColor = ContextCompat.getColor(this, R.color.green)
            val grayColor = ContextCompat.getColor(this, android.R.color.darker_gray)
            val whiteColor = ContextCompat.getColor(this, android.R.color.white)
            val blackColor = ContextCompat.getColor(this, android.R.color.black)

            if (done) {
                button.setBackgroundColor(greenColor) // Set resolved green color
                button.setTextColor(whiteColor) // White text
            } else {
                button.setBackgroundColor(grayColor) // Transparent background
                button.setTextColor(blackColor) // Black text
            }
        }

        fun toggleDone(button: Button, index: Int) {
            val tasks = getTasks().toMutableList()

            if (index in tasks.indices) { // Ensure index is valid
                val task = tasks[index]
                val newBool = !task.done
                tasks[index] = Task(task.name, task.description, newBool) // Preserve "done" status
                saveTasks(tasks)
                updateDoneButton(button, newBool)
            } else {
                Log.e("editTask", "Invalid task index: $index") // Debugging log
            }
        }

        fun delete(index: Int) {
            val tasks = getTasks().toMutableList()

            if (index in tasks.indices) { // Ensure index is valid
                tasks.removeAt(index) // Remove the task at the given index
                saveTasks(tasks) // Save updated list
            } else {
                Log.e("deleteTask", "Invalid task index: $index") // Debugging log
            }
        }

        fun backToHome() {
            // Intent to go back to main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // intents
        val receivedID = intent.getIntExtra("task_id", 0)
        val receivedTaskName = intent.getStringExtra("task_name") ?: "Task"
        val receivedTaskDescription = intent.getStringExtra("task_description") ?: ""
        val receivedTaskDone = intent.getBooleanExtra("task_done", false)

        // View elements stuff
        // submit button
        val submitButton = findViewById<Button>(R.id.AddTaskButton)
        val taskNameInput = findViewById<EditText>(R.id.taskNameInput)
        val taskDescriptionInput = findViewById<EditText>(R.id.taskDescriptionInput)
        val taskDoneButton = findViewById<Button>(R.id.DoneButton)
        val deleteButton = findViewById<Button>(R.id.DeleteButton)

        // edit texts based on received Intents
        taskNameInput.setText(receivedTaskName)
        taskDescriptionInput.setText(receivedTaskDescription)

        updateDoneButton(taskDoneButton, receivedTaskDone)

        taskDoneButton.setOnClickListener {
            toggleDone(taskDoneButton, receivedID)
        }

        deleteButton.setOnClickListener {
            delete(receivedID)
            backToHome()
        }

        submitButton.setOnClickListener {
            val taskName = taskNameInput.text.toString().trim()
            val taskDescription = taskDescriptionInput.text.toString().trim()

            if (taskName.isEmpty()) {
                taskNameInput.error = "Task name cannot be empty"
                return@setOnClickListener
            }

            editTask(receivedID, taskName, taskDescription)

            backToHome()
        }
    }
}