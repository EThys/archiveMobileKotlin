package com.android.archive.room.daoInterface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.android.archive.models.room.User

@Dao
interface IUserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}