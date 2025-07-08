package com.example.demoapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class PatientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        val genderSpinner = findViewById<Spinner>(R.id.spinnerGender)
        val appointmentTime = findViewById<EditText>(R.id.editTextAppointmentTime)
        val nextButton = findViewById<Button>(R.id.buttonNext)

        nextButton.setOnClickListener {
            val gender = genderSpinner.selectedItem.toString()
            val appointment = appointmentTime.text.toString()

            // Passing data to the AddPatientActivity
            val intent = Intent(this, AddPatientActivity::class.java)
            intent.putExtra("gender", gender)
            intent.putExtra("appointment", appointment)
            startActivity(intent)
        }
    }
}
