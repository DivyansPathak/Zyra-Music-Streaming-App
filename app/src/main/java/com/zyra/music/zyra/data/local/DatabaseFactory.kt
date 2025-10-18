package com.zyra.music.zyra.data.local

import android.content.Context
import androidx.room.Room
import com.zyra.music.zyra.data.utils.DATABASE_NAME

object DatabaseFactory {

    fun create(context : Context) : AppDatabase{
        return Room
            .databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()
    }
}