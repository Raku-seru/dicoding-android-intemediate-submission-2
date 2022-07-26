package com.rakuseru.storyapp1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM app_remote_keys WHERE id = :id")
    suspend fun getRemoteKeysId(id: String): RemoteKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeyEntity: List<RemoteKeyEntity>)

    @Query("DELETE FROM app_remote_keys")
    suspend fun deleteRemoteKeys()
}