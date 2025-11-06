package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class FarmerProfile(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("userId")
    val userId: Long? = null,
    
    @SerializedName("farmName")
    val farmName: String,
    
    @SerializedName("farmSize")
    val farmSize: Double? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("county")
    val county: String? = null,
    
    @SerializedName("farmDescription")
    val farmDescription: String? = null,
    
    @SerializedName("alternatePhone")
    val alternatePhone: String? = null,
    
    @SerializedName("postalAddress")
    val postalAddress: String? = null,
    
    @SerializedName("primaryCrops")
    val primaryCrops: List<String>? = null,
    
    @SerializedName("farmingExperience")
    val farmingExperience: Int? = null,
    
    @SerializedName("certifications")
    val certifications: List<String>? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class FarmerProfileRequest(
    @SerializedName("farmName")
    val farmName: String,
    
    @SerializedName("farmSize")
    val farmSize: Double? = null,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("county")
    val county: String? = null,
    
    @SerializedName("farmDescription")
    val farmDescription: String? = null,
    
    @SerializedName("alternatePhone")
    val alternatePhone: String? = null,
    
    @SerializedName("postalAddress")
    val postalAddress: String? = null,
    
    @SerializedName("primaryCrops")
    val primaryCrops: List<String>? = null,
    
    @SerializedName("farmingExperience")
    val farmingExperience: Int? = null,
    
    @SerializedName("certifications")
    val certifications: List<String>? = null
)

