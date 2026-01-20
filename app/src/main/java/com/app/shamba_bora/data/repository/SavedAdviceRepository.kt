package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.db.dao.SavedAdviceDao
import com.app.shamba_bora.data.db.entities.SavedAdvice
import com.app.shamba_bora.data.model.FarmAdviceResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing saved farm advice
 */
@Singleton
class SavedAdviceRepository @Inject constructor(
    private val savedAdviceDao: SavedAdviceDao,
    private val gson: Gson
) {
    
    fun getAllSavedAdvices(): Flow<List<SavedAdvice>> {
        return savedAdviceDao.getAllSavedAdvices()
    }
    
    suspend fun getSavedAdviceById(id: Long): SavedAdvice? {
        return savedAdviceDao.getSavedAdviceById(id)
    }
    
    suspend fun saveAdvice(title: String, adviceResponse: FarmAdviceResponse): Long {
        val savedAdvice = SavedAdvice(
            title = title,
            overallAssessment = adviceResponse.overallAssessment,
            strengths = gson.toJson(adviceResponse.strengths),
            weaknesses = gson.toJson(adviceResponse.weaknesses),
            recommendations = gson.toJson(adviceResponse.recommendations),
            bestPractices = gson.toJson(adviceResponse.bestPractices),
            cropOptimizationAdvice = adviceResponse.cropOptimizationAdvice,
            investmentAdvice = adviceResponse.investmentAdvice
        )
        return savedAdviceDao.insertAdvice(savedAdvice)
    }
    
    suspend fun deleteAdvice(advice: SavedAdvice) {
        savedAdviceDao.deleteAdvice(advice)
    }
    
    suspend fun deleteAdviceById(id: Long) {
        savedAdviceDao.deleteAdviceById(id)
    }
    
    suspend fun getAdviceCount(): Int {
        return savedAdviceDao.getAdviceCount()
    }
    
    /**
     * Convert SavedAdvice entity back to FarmAdviceResponse
     */
    fun toFarmAdviceResponse(savedAdvice: SavedAdvice): FarmAdviceResponse {
        return FarmAdviceResponse(
            overallAssessment = savedAdvice.overallAssessment,
            strengths = gson.fromJson(savedAdvice.strengths, Array<String>::class.java).toList(),
            weaknesses = gson.fromJson(savedAdvice.weaknesses, Array<String>::class.java).toList(),
            recommendations = gson.fromJson(
                savedAdvice.recommendations,
                Array<com.app.shamba_bora.data.model.FarmRecommendation>::class.java
            ).toList(),
            bestPractices = gson.fromJson(
                savedAdvice.bestPractices,
                Array<com.app.shamba_bora.data.model.BestPractice>::class.java
            ).toList(),
            cropOptimizationAdvice = savedAdvice.cropOptimizationAdvice,
            investmentAdvice = savedAdvice.investmentAdvice
        )
    }
}
