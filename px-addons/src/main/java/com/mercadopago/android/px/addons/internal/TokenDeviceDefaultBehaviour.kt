package com.mercadopago.android.px.addons.internal

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.addons.model.TokenStatus

internal class TokenDeviceDefaultBehaviour : TokenDeviceBehaviour {
    override val isFeatureAvailable: Boolean = false
    override val tokensStatus: List<TokenStatus> = listOf()
    override fun getTokenStatus(cardId: String) = TokenStatus(cardId, TokenStatus.Status.NONE)
}