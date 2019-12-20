package com.example.weaterapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weaterapp.requests.entity_requests.Favorite

@Database(entities = [Favorite::class], version = 1)
abstract class RoomManager : RoomDatabase(){
    abstract fun getCityDao(): CityDao

    companion object {
        var instance: RoomManager? = null

        fun getInstance(context: Context) : RoomManager? {
            synchronized(RoomManager::class.java) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomManager::class.java,
                        "Weather.db"
                    ).build()
                }
            }
            return instance
        }
    }
}