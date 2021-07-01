package com.mercadopago.android.px.model.internal;

import com.mercadopago.android.px.addons.model.TokenStatus

internal data class CardStatusDM(val cardId: String, val tokenizeStatus: TokenStatusDM, val hasEsc: Boolean) {
    enum class TokenStatusDM {
        ENABLED,
        PENDING,
        DELETED,
        NONE;

        companion object {
            fun from(tokenStatus: TokenStatus.Status?): TokenStatusDM {
                return when (tokenStatus) {
                    TokenStatus.Status.ENABLED -> ENABLED
                    TokenStatus.Status.PENDING -> PENDING
                    TokenStatus.Status.DELETED -> DELETED
                    TokenStatus.Status.NONE -> NONE
                    else -> NONE
                }
            }
        }
    }
}