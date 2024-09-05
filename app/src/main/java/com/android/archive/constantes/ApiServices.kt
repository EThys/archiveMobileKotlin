package com.android.archive.constantes

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import com.android.archive.models.room.User

data class LoginRequest(val username: String, val password: String)

data class ApiResponseDirectory(
    val directory :List<ApiDirectory>,
)
data class ApiResponseInvoiceKey(
    val invoiceKey:List<ApiInvoiceKey>
)
data class ApiDirectory(
    val DirectoryId :Int,
    val DirectoryName :String,
    val parentId :String?,
    val available:Int,
    val created_at:String,
    val updated_at:String
)
data class ApiInvoiceKey(
    val InvoicekeyId:Int,
    val Invoicekey:String,
    val DirectoryFId:Int,
    val created_at:String,
    val updated_at:String
)
data class ApiUser(
    val user: User,
    val token:String,
    var message: String =""
)

data class ApiInvoice(
    val message: String,
    val invoiceId:Int
)

interface ApiService {
    @POST("login")
    fun login(@Body request: LoginRequest): Call<ApiUser>
}