package com.example.demoapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AddPatientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_patient)

        // Get data passed from PatientActivity
        val gender = intent.getStringExtra("gender")
        val appointment = intent.getStringExtra("appointment")

        // Find your UI elements in AddPatientActivity (e.g., TextView)
        val genderTextView = findViewById<TextView>(R.id.textViewGender)
        val appointmentTextView = findViewById<TextView>(R.id.textViewAppointment)

        // Set the values
        genderTextView.text = gender
        appointmentTextView.text = appointment
    }
}
