package com.example.foodish.app.repo

import com.example.foodish.app.data.LocalDataSource
import com.example.foodish.app.data.RemoteDataSource
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/*Activity Retained scope makes sure that the repository instance is present until the activity is present and
does not get lost during configuration changes.
*/
@ActivityRetainedScoped
class FoodRecipeRepository @Inject constructor(private val remoteDataSource: RemoteDataSource,
private val localDataSource: LocalDataSource) {
    val remote = remoteDataSource
    val local = localDataSource

}