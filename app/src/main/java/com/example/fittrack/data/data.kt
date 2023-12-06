package com.example.fittrack.data

import android.app.Application
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import com.example.fittrack.AppDatabase

data class RunData(
    val sessionId: Int,
    val date: String,
    val distance: Double,
    val duration: String,
    val pace: String
)

enum class Tab { Home, Record, Calendar }

@Entity
class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val sessionId: Int
)

@Entity
class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    var distance: Double,
    val duration: Long
)

class MyApp : Application() {

    companion object {
        var database: AppDatabase? = null
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, AppDatabase::class.java, "fit-track-db")
            .build()
    }
}