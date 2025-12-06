package com.app.shamba_bora.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.shamba_bora.data.model.Order
import com.app.shamba_bora.data.model.Payment
import com.app.shamba_bora.data.model.PaymentRequest
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
    
    private val _buyerOrdersState = MutableStateFlow<Resource<PageResponse<Order>>>(Resource.Loading())
    val buyerOrdersState: StateFlow<Resource<PageResponse<Order>>> = _buyerOrdersState.asStateFlow()
    
    private val _sellerOrdersState = MutableStateFlow<Resource<PageResponse<Order>>>(Resource.Loading())
    val sellerOrdersState: StateFlow<Resource<PageResponse<Order>>> = _sellerOrdersState.asStateFlow()
    
    private val _paymentState = MutableStateFlow<Resource<Payment>>(Resource.Loading())
    val paymentState: StateFlow<Resource<Payment>> = _paymentState.asStateFlow()
    
    private val _paymentStatusState = MutableStateFlow<Resource<Payment>>(Resource.Loading())
    val paymentStatusState: StateFlow<Resource<Payment>> = _paymentStatusState.asStateFlow()
    
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
    
    fun placeOrder(productId: Long, quantity: Int, deliveryAddress: String? = null, phoneNumber: String? = null) {
        viewModelScope.launch {
            val order = Order(
                buyerId = PreferenceManager.getUserId(),
                productId = productId,
                quantity = quantity,
                deliveryAddress = deliveryAddress
            )
            val orderResult = repository.placeOrder(order)
            if (orderResult is Resource.Success && orderResult.data?.id != null && phoneNumber != null) {
                // Initiate payment after order is placed
                initiatePayment(
                    orderId = orderResult.data!!.id!!,
                    phoneNumber = phoneNumber,
                    accountReference = "ORDER-${orderResult.data!!.id}"
                )
            }
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
    
    fun loadOrdersByBuyer(buyerId: Long) {
        viewModelScope.launch {
            _buyerOrdersState.value = Resource.Loading()
            _buyerOrdersState.value = repository.getOrdersByBuyer(buyerId)
        }
    }
    
    fun loadOrdersBySeller(sellerId: Long) {
        viewModelScope.launch {
            _sellerOrdersState.value = Resource.Loading()
            _sellerOrdersState.value = repository.getOrdersBySeller(sellerId)
        }
    }
    
    fun updateOrderStatus(id: Long, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(id, status)
            // Reload both buyer and seller orders
            val userId = PreferenceManager.getUserId()
            loadOrdersByBuyer(userId)
            loadOrdersBySeller(userId)
        }
    }
    
    // ========== PAYMENT METHODS ==========
    fun initiatePayment(orderId: Long, phoneNumber: String, accountReference: String? = null) {
        viewModelScope.launch {
            _paymentState.value = Resource.Loading()
            val paymentRequest = PaymentRequest(
                orderId = orderId,
                phoneNumber = phoneNumber,
                accountReference = accountReference ?: "ORDER-$orderId"
            )
            _paymentState.value = repository.initiatePayment(paymentRequest)
        }
    }
    
    fun getPaymentStatus(paymentId: Long) {
        viewModelScope.launch {
            _paymentStatusState.value = Resource.Loading()
            _paymentStatusState.value = repository.getPaymentStatus(paymentId)
        }
    }
    
    fun getPaymentByOrderId(orderId: Long) {
        viewModelScope.launch {
            _paymentStatusState.value = Resource.Loading()
            _paymentStatusState.value = repository.getPaymentByOrderId(orderId)
        }
    }
    
    fun getCurrentUserId(): Long {
        return PreferenceManager.getUserId()
    }
}

