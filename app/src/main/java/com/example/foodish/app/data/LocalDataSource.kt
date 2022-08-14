package com.example.foodish.app.data

import com.example.foodish.app.database.dao.RecipesDao
import com.example.foodish.app.database.entity.RecipesEntity
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