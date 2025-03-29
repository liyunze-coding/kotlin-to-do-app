package com.deakin.todoapp2

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
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

        // Retrieve tasks (default empty list if none found)
        fun getTasks(): List<Task> {
            val json = prefs.getString("tasks", "[]") ?: "[]"
            val type = object : TypeToken<List<Task>>() {}.type
            return gson.fromJson(json, type)
        }

        fun addTask(taskName: String, taskDescription: String) {
            val tasks = getTasks().toMutableList()
            tasks.add(Task(taskName, taskDescription, false))
            saveTasks(tasks)
        }

        fun displayTasks() {
            val tasksLayout = findViewById<LinearLayout>(R.id.tasksLayout)
            val tasks = getTasks()
            tasksLayout.removeAllViews()

            if (tasks.isNotEmpty()) {
                tasks.forEachIndexed { index, task ->
                    var bgColor = Color.WHITE
                    if (task.done) {
                        bgColor = ContextCompat.getColor(this, R.color.green)
                    }

                    val taskButton = Button(this).apply {
                        text = "• ${task.name}"  // Add bullet point
                        textSize = 18f
                        setPadding(16, 16, 16, 16)
                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START // Align text to start
                        setBackgroundColor(bgColor) // Remove default button styling
                        setTextColor(Color.BLACK) // Set text color
                        isAllCaps = false // Prevent automatic capitalization

                        // Set click listener to open EditTaskActivity with task data
                        setOnClickListener {
                            val intent =
                                Intent(this@MainActivity, EditTaskActivity::class.java).apply {
                                    putExtra("task_id", index)
                                    putExtra("task_name", task.name)
                                    putExtra("task_description", task.description)
                                    putExtra("task_done", task.done) // Boolean extra
                                }
                            startActivity(intent)
                        }
                    }
                    tasksLayout.addView(taskButton)
                }
            }
        }

        // floating action button, redirect to add task activity
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        displayTasks()

        fab.setOnClickListener {
            addTask("Task", "Description")
            displayTasks()
        }
    }
}