package com.app.shamba_bora.data.repository

import android.util.Log
import com.app.shamba_bora.data.model.MaizePatchDTO
import com.app.shamba_bora.data.model.PatchComparisonDTO
import com.app.shamba_bora.data.model.PatchSummaryDTO
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class PatchRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun createPatch(patch: MaizePatchDTO): Resource<MaizePatchDTO> {
        return try {
            val response = apiService.createPatch(patch)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to create patch")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPatches(): Resource<List<MaizePatchDTO>> {
        return try {
            val response = apiService.getPatches()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Log.d("PatchRepository", "Successfully fetched ${body.size} patches")
                    Resource.Success(body)
                } else {
                    Log.e("PatchRepository", "Response body is null")
                    Resource.Error("Response body is null")
                }
            } else {
                val errorMsg = response.message() ?: "Failed to get patches (${response.code()})"
                Log.e("PatchRepository", "API error: $errorMsg")
                Resource.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e("PatchRepository", "Exception while fetching patches", e)
            Resource.Error(e.message ?: "An error occurred: ${e.javaClass.simpleName}")
        }
    }
    
    suspend fun getPatch(id: Long): Resource<MaizePatchDTO> {
        return try {
            val response = apiService.getPatch(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get patch")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updatePatch(id: Long, patch: MaizePatchDTO): Resource<MaizePatchDTO> {
        return try {
            val response = apiService.updatePatch(id, patch)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to update patch")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun deletePatch(id: Long): Resource<Unit> {
        return try {
            val response = apiService.deletePatch(id)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message() ?: "Failed to delete patch")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPatchSummary(patchId: Long): Resource<PatchSummaryDTO> {
        return try {
            val response = apiService.getPatchSummary(patchId)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to get patch summary")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun comparePatches(patchIds: List<Long>): Resource<PatchComparisonDTO> {
        return try {
            val response = apiService.comparePatches(patchIds)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message() ?: "Failed to compare patches")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}
