package com.rouxinpai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast
import com.rouxinpai.stepper.DataElement
import com.rouxinpai.stepper.OnValueChangeListener
import com.rouxinpai.stepper.Stepper

class MainActivity : AppCompatActivity(), OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stepper = findViewById<Stepper>(R.id.stepper).apply {
            setMinValue(0f)
            setMaxValue(30.60f)
            setValue(0f)
            setEnable(true)
            setOnInputClickListener(this@MainActivity)
            addListener(object : OnValueChangeListener {
                override fun onValueChanged(view: View, value: Float?) {
                    val a = value
                }
            })
        }

        val stepper2 = findViewById<Stepper>(R.id.stepper_2).apply {
            setMinValue(0f)
            setMaxValue(30f)
            setEnable(true)
            setOnInputClickListener(this@MainActivity)
        }

        val stepper3 = findViewById<Stepper>(R.id.stepper_3).apply {
            setMinValue(0f)
            setMaxValue(30f)
        }

        findViewById<Button>(R.id.btn_change_date_element)?.setOnClickListener {
            stepper3.setDataElement(DataElement.INT)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.stepper -> {
                Toast.makeText(this@MainActivity, "111", Toast.LENGTH_SHORT).show()
            }
            R.id.stepper_2 -> {
                Toast.makeText(this@MainActivity, "222", Toast.LENGTH_SHORT).show()
            }
        }
    }
}