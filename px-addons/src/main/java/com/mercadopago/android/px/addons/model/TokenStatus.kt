package com.mercadopago.android.px.addons.model

data class TokenStatus(val cardId: String, val status: Status) {
    enum class Status {
        ENABLED,
        PENDING,
        DELETED,
        NONE
    }
}