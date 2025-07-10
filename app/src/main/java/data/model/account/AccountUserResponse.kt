package data.model.account

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountUserResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "main")
    val main: Boolean,
    @Json(name = "balance")
    val balance: Double
)
