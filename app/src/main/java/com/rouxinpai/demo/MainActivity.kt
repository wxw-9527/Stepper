package com.rouxinpai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.core.os.postDelayed
import com.rouxinpai.stepper.DataElement
import com.rouxinpai.stepper.Stepper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stepper = findViewById<Stepper>(R.id.stepper)

        stepper.setMinValue(0f)
        stepper.setMaxValue(30f)
        stepper.setValue(30f)
        stepper.setEnable(false)

        val stepper2 = findViewById<Stepper>(R.id.stepper_2).apply {
            setMinValue(0f)
            setMaxValue(30f)
        }


        val stepper3 = findViewById<Stepper>(R.id.stepper_3).apply {
            setMinValue(0f)
            setMaxValue(30f)
        }
        findViewById<Button>(R.id.btn_change_date_element)?.setOnClickListener {
            stepper3.setDataElement(DataElement.INT)
        }
    }
}