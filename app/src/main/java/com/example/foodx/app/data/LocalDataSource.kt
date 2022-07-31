package com.example.foodx.app.data

import com.example.foodx.app.database.dao.RecipesDao
import com.example.foodx.app.database.entity.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readDatabase(): Flow<List<RecipesEntity>> {
        return recipesDao.readAllRecipes()
    }

    suspend fun insertDao(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }
}