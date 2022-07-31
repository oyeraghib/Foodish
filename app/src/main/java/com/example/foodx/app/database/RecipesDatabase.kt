package com.example.foodx.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.foodx.app.database.dao.RecipesDao
import com.example.foodx.app.database.entity.RecipesEntity

@Database(entities = [RecipesEntity::class], version = 1, exportSchema = false)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase() {

    abstract fun recipesDao(): RecipesDao
}