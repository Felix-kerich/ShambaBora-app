package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.Order
import com.app.shamba_bora.data.model.Product
import com.app.shamba_bora.data.network.PageResponse
import com.app.shamba_bora.data.repository.MarketplaceRepository
import com.app.shamba_bora.utils.PreferenceManager
import com.app.shamba_bora.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MarketplaceViewModel @Inject constructor(
    private val repository: MarketplaceRepository
) : ViewModel() {
    
    private val _productsState = MutableStateFlow<Resource<PageResponse<Product>>>(Resource.Loading())
    val productsState: StateFlow<Resource<PageResponse<Product>>> = _productsState.asStateFlow()
    
    private val _productState = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val productState: StateFlow<Resource<Product>> = _productState.asStateFlow()
    
    private val _ordersState = MutableStateFlow<Resource<PageResponse<Order>>>(Resource.Loading())
    val ordersState: StateFlow<Resource<PageResponse<Order>>> = _ordersState.asStateFlow()
    
    init {
        loadProducts()
    }
    
    fun loadProducts(query: String? = null, page: Int = 0, size: Int = 10) {
        viewModelScope.launch {
            _productsState.value = Resource.Loading()
            _productsState.value = repository.getProducts(query, page, size)
        }
    }
    
    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _productState.value = Resource.Loading()
            _productState.value = repository.getProduct(id)
        }
    }
    
    fun createProduct(product: Product) {
        viewModelScope.launch {
            repository.createProduct(product)
            loadProducts()
        }
    }
    
    fun updateProduct(id: Long, product: Product) {
        viewModelScope.launch {
            repository.updateProduct(id, product)
            loadProducts()
        }
    }
    
    fun setProductAvailability(id: Long, available: Boolean) {
        viewModelScope.launch {
            repository.setProductAvailability(id, available)
            loadProducts()
        }
    }
    
    fun placeOrder(productId: Long, quantity: Int, deliveryAddress: String? = null) {
        viewModelScope.launch {
            val order = Order(
                buyerId = PreferenceManager.getUserId(),
                productId = productId,
                quantity = quantity,
                deliveryAddress = deliveryAddress
            )
            repository.placeOrder(order)
            loadOrders()
        }
    }
    
    fun loadOrders(isBuyer: Boolean = true) {
        viewModelScope.launch {
            _ordersState.value = Resource.Loading()
            val userId = PreferenceManager.getUserId()
            _ordersState.value = if (isBuyer) {
                repository.getOrdersByBuyer(userId)
            } else {
                repository.getOrdersBySeller(userId)
            }
        }
    }
    
    fun updateOrderStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, status)
            loadOrders()
        }
    }
}

