package com.example.reliablehouse

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class VolunteerActivity : AppCompatActivity() {

    private lateinit var availableTasksSection: LinearLayout
    private lateinit var assignedTasksSection: LinearLayout
    private lateinit var backToDashboardButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer)

        // Initialize UI elements
        availableTasksSection = findViewById(R.id.availableTasksSection)
        assignedTasksSection = findViewById(R.id.assignedTasksSection)
        backToDashboardButton = findViewById(R.id.backToDashboardButton)

        // Load available tasks and assigned tasks
        loadAvailableTasks()
        loadAssignedTasks()

        // Handle button click to navigate back to dashboard
        backToDashboardButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAvailableTasks() {
        // For demo purposes, we will add a few mock tasks to the available tasks section
        val task1 = createTaskView("Volunteer for Disaster Relief", "Help out with relief efforts in affected areas.")
        val task2 = createTaskView("Organize Food Distribution", "Assist in organizing food for distribution to the needy.")
        availableTasksSection.addView(task1)
        availableTasksSection.addView(task2)
    }

    private fun loadAssignedTasks() {
        // For demo purposes, we will add a few mock assigned tasks
        val assignedTask1 = createTaskView("Help at Shelter", "Assist at a local shelter.")
        assignedTasksSection.addView(assignedTask1)
    }

    private fun createTaskView(taskName: String, taskDescription: String): View {
        val taskLayout = LinearLayout(this)
        taskLayout.orientation = LinearLayout.HORIZONTAL
        taskLayout.setPadding(10, 10, 10, 10)

        val taskNameTextView = TextView(this)
        taskNameTextView.text = taskName
        taskNameTextView.setTextSize(16f)
        taskNameTextView.setTextColor(resources.getColor(android.R.color.black))

        val taskDescriptionTextView = TextView(this)
        taskDescriptionTextView.text = taskDescription
        taskDescriptionTextView.setTextSize(14f)
        taskDescriptionTextView.setTextColor(resources.getColor(android.R.color.darker_gray))

        taskLayout.addView(taskNameTextView)
        taskLayout.addView(taskDescriptionTextView)

        return taskLayout
    }
}
