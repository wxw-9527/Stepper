package com.rouxinpai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rouxinpai.stepper.Stepper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stepper = findViewById<Stepper>(R.id.stepper)

        stepper.setMinValue(0f)
        stepper.setMaxValue(999999f)

        stepper.setEnable(false)

        val stepper2 = findViewById<Stepper>(R.id.stepper_2)
        stepper2.setEnable(true)
    }
}