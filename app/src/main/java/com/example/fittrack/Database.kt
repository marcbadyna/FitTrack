package com.example.fittrack

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Database
import androidx.room.Delete
import androidx.room.RoomDatabase
import com.example.fittrack.data.LocationEntity
import com.example.fittrack.data.SessionEntity

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(locationEntity: LocationEntity)

    @Query("SELECT * FROM LocationEntity")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE sessionId = :sessionId")
    suspend fun getLocationsBySession(sessionId: Int): List<LocationEntity>

    @Query("DELETE FROM LocationEntity WHERE sessionId = :sessionId")
    suspend fun deleteLocationsBySession(sessionId: Int)

    @Query("DELETE FROM LocationEntity WHERE sessionId NOT IN (SELECT id FROM SessionEntity)")
    suspend fun deleteOrphanedLocations()

}

@Dao
interface SessionDao {
    @Query("SELECT * FROM SessionEntity")
    suspend fun getAllSessions(): List<SessionEntity>
    @Insert
    suspend fun insertSession(session: SessionEntity)

    @Delete
    suspend fun deleteSession(session: SessionEntity)
}


@Database(entities = [LocationEntity::class, SessionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun sessionDao(): SessionDao
}