package com.mercadopago.android.px.internal.features.express.slider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;

public interface PaymentMethod {
    interface View extends MvpView {
        void updateView();

        void updateState();

        void disable();

        void animateHighlightMessageIn(@Nullable final String text);

        void animateHighlightMessageOut();
    }

    interface Action {
        void onFocusIn();

        void onFocusOut();

        void onApplicationChanged(@NonNull final String paymentTypeId);
    }
}