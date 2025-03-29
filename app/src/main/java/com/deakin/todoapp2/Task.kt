package com.deakin.todoapp2

data class Task(
    val name: String,
    val description: String,
    var done: Boolean = false
)
