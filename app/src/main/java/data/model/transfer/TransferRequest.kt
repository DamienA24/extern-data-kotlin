package data.model.transfer

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransferRequest(
    @Json(name = "sender")
    val sender: String,
    @Json(name = "recipient")
    val recipient: String,
    @Json(name = "amount")
    val amount: Double

)
