package com.app.shamba_bora.data.db.dao

import androidx.room.*
import com.app.shamba_bora.data.db.entities.SavedAdvice
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing saved farm advice
 */
@Dao
interface SavedAdviceDao {
    @Query("SELECT * FROM saved_advice ORDER BY timestamp DESC")
    fun getAllSavedAdvices(): Flow<List<SavedAdvice>>
    
    @Query("SELECT * FROM saved_advice WHERE id = :id")
    suspend fun getSavedAdviceById(id: Long): SavedAdvice?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvice(advice: SavedAdvice): Long
    
    @Delete
    suspend fun deleteAdvice(advice: SavedAdvice)
    
    @Query("DELETE FROM saved_advice WHERE id = :id")
    suspend fun deleteAdviceById(id: Long)
    
    @Query("SELECT COUNT(*) FROM saved_advice")
    suspend fun getAdviceCount(): Int
}
