package com.nokhyun.sensorexam

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.nokhyun.sensorexam.ui.theme.SensorExamTheme
import kotlin.math.abs

/**
 * 만보기 샘플
 * Created by Nokhyun on 2023-04-17
 * */
class MainActivity : ComponentActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var isRunning = false
    private var prevStep = 0
    private var stepCount = 0

    private var _stepValue: MutableState<String> = mutableStateOf("0")
    val stepValue: State<String> = _stepValue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1001)
            }
        }

        sensorManager = getSystemService(SensorManager::class.java)

        setContent {
            SensorExamTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Text(text = stepValue.value)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        val sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        sensor?.let {
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val isIncrementStep = abs(event.values[0].toInt() - prevStep)
            prevStep = event.values?.get(0)?.toInt()!!
            log("abs(event.values[0].toInt() - totalStep): $isIncrementStep :: ${isIncrementStep == 1}")

            if (isRunning && isIncrementStep == 1) {
                _stepValue.value = (++stepCount).toString()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun log(msg: String) {
        Log.e(this.javaClass.simpleName, msg)
    }
}