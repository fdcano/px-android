package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.model.TokenStatus

interface TokenDeviceBehaviour {
    val isFeatureAvailable: Boolean
    val tokensStatus: List<TokenStatus>
    fun getTokenStatus(cardId: String): TokenStatus
}