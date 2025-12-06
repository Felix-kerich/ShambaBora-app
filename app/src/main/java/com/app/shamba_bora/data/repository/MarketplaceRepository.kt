package com.app.shamba_bora.data.repository

import com.app.shamba_bora.data.model.Order
import com.app.shamba_bora.data.model.Payment
import com.app.shamba_bora.data.model.PaymentRequest
import com.app.shamba_bora.data.model.Product
import com.app.shamba_bora.data.network.ApiResponse
import com.app.shamba_bora.data.network.ApiService
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.utils.Resource
import javax.inject.Inject

class MarketplaceRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getProducts(
        query: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<Product>> {
        return try {
            val response = apiService.getProducts(query, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get products")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get products")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getProduct(id: Long): Resource<Product> {
        return try {
            val response = apiService.getProduct(id)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get product")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun createProduct(product: Product): Resource<Product> {
        return try {
            val response = apiService.createProduct(product)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to create product")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to create product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateProduct(id: Long, product: Product): Resource<Product> {
        return try {
            val response = apiService.updateProduct(id, product)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to update product")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update product")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun setProductAvailability(id: Long, available: Boolean): Resource<Unit> {
        return try {
            val response = apiService.setProductAvailability(id, available)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success) {
                    Resource.Success(Unit)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to update availability")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update availability")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun placeOrder(order: Order): Resource<Order> {
        return try {
            val response = apiService.placeOrder(order)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to place order")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to place order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun updateOrderStatus(id: Long, status: String): Resource<Order> {
        return try {
            val response = apiService.updateOrderStatus(id, status)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to update order")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to update order")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getOrdersByBuyer(
        buyerId: Long,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<Order>> {
        return try {
            val response = apiService.getOrdersByBuyer(buyerId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get orders")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getOrdersBySeller(
        sellerId: Long,
        page: Int = 0,
        size: Int = 10
    ): Resource<PageResponse<Order>> {
        return try {
            val response = apiService.getOrdersBySeller(sellerId, page, size)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get orders")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get orders")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    // ========== PAYMENT METHODS ==========
    suspend fun initiatePayment(paymentRequest: PaymentRequest): Resource<Payment> {
        return try {
            val response = apiService.initiatePayment(paymentRequest)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to initiate payment")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to initiate payment")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPaymentStatus(paymentId: Long): Resource<Payment> {
        return try {
            val response = apiService.getPaymentStatus(paymentId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get payment status")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get payment status")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
    
    suspend fun getPaymentByOrderId(orderId: Long): Resource<Payment> {
        return try {
            val response = apiService.getPaymentByOrderId(orderId)
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                if (apiResponse.success && apiResponse.data != null) {
                    Resource.Success(apiResponse.data!!)
                } else {
                    Resource.Error(apiResponse.message ?: "Failed to get payment")
                }
            } else {
                Resource.Error(response.message() ?: "Failed to get payment")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

