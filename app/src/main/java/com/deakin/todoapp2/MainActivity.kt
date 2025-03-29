package com.deakin.todoapp2


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

        // Retrieve tasks (default empty list if none found)
        fun getTasks(): List<String> {
            val json = prefs.getString("tasks", "[]") ?: "[]"
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json, type)
        }

        // val taskListTextView = findViewById<TextView>(R.id.taskListTextView)
        val tasksLayout = findViewById<LinearLayout>(R.id.tasksLayout)

        // Load and display tasks
        val tasks = getTasks()
        tasksLayout.removeAllViews()

        if (tasks.isNotEmpty()) {
            tasks.forEach { task ->
                val radioButton = RadioButton(this).apply {
                    text = task
                    textSize = 18f
                    setPadding(16, 16, 16, 16)
                }
                tasksLayout.addView(radioButton)
            }
        }


        // floating action button, redirect to add task activity
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }
    }
}