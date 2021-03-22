package com.example.happy

import java.util.jar.Manifest
import android.net.Uri
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.example.happy.ProcessLocationData


class UserPickPlaces : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val processLocationData = ProcessLocationData()
        val coordinates = processLocationData.getCoordinates(this)
        val Uri_substring = "geo:" + coordinates + "?q=places to eat"
        val gmmIntentUri = Uri.parse(Uri_substring)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }
}

