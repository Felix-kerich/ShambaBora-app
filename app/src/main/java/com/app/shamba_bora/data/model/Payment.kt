package com.app.shamba_bora.data.model

import com.google.gson.annotations.SerializedName

data class Payment(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("orderId")
    val orderId: Long,
    
    @SerializedName("checkoutRequestId")
    val checkoutRequestId: String? = null,
    
    @SerializedName("responseCode")
    val responseCode: String? = null,
    
    @SerializedName("responseDescription")
    val responseDescription: String? = null,
    
    @SerializedName("customerMessage")
    val customerMessage: String? = null,
    
    @SerializedName("status")
    val status: String? = "PENDING", // PENDING, SUCCESS, FAILED, CANCELLED
    
    @SerializedName("amount")
    val amount: Double? = null,
    
    @SerializedName("mpesaReceiptNumber")
    val mpesaReceiptNumber: String? = null,
    
    @SerializedName("transactionDate")
    val transactionDate: String? = null,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class PaymentRequest(
    @SerializedName("orderId")
    val orderId: Long,
    
    @SerializedName("phoneNumber")
    val phoneNumber: String, // Format: 254XXXXXXXXX or 0XXXXXXXXX
    
    @SerializedName("accountReference")
    val accountReference: String? = null
)

data class PaymentCallback(
    @SerializedName("Body")
    val body: PaymentCallbackBody? = null
)

data class PaymentCallbackBody(
    @SerializedName("stkCallback")
    val stkCallback: StkCallback? = null
)

data class StkCallback(
    @SerializedName("MerchantRequestID")
    val merchantRequestId: String? = null,
    
    @SerializedName("CheckoutRequestID")
    val checkoutRequestId: String? = null,
    
    @SerializedName("ResultCode")
    val resultCode: Int? = null,
    
    @SerializedName("ResultDesc")
    val resultDesc: String? = null,
    
    @SerializedName("CallbackMetadata")
    val callbackMetadata: CallbackMetadata? = null
)

data class CallbackMetadata(
    @SerializedName("Item")
    val items: List<CallbackItem>? = null
)

data class CallbackItem(
    @SerializedName("Name")
    val name: String? = null,
    
    @SerializedName("Value")
    val value: Any? = null
)
