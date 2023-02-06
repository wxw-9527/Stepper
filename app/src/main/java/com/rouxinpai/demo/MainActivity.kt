package com.rouxinpai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rouxinpai.stepper.NumberInputEdittext
import com.rouxinpai.stepper.Stepper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stepper = findViewById<Stepper>(R.id.stepper)

        stepper.setMinValue(0f)
        stepper.setMaxValue(12f)

        // stepper.setValue(12f)

        val input = findViewById<NumberInputEdittext>(R.id.number_input_edittext)
        input.setMinValue(0f)
        input.setMaxValue(12f)
        input.setValue(26000f)
    }
}