package com.mercadopago.android.px.internal.features.checkout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mercadolibre.android.cardform.internal.LifecycleListener;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.experiments.KnownVariant;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.view.experiments.ExperimentHelper;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.Callback;

public class CheckoutPresenter extends BasePresenter<Checkout.View> implements Checkout.Actions {

    @NonNull /* default */ final PaymentRepository paymentRepository;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final UserSelectionRepository userSelectionRepository;
    @NonNull private final InitRepository initRepository;
    @NonNull private final PostPaymentUrlsMapper postPaymentUrlsMapper;
    @NonNull /* default */ ExperimentsRepository experimentsRepository;

    /* default */ CheckoutPresenter(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final InitRepository initRepository,
        @NonNull final PaymentRepository paymentRepository,
        @NonNull final ExperimentsRepository experimentsRepository,
        @NonNull final PostPaymentUrlsMapper postPaymentUrlsMapper) {

        this.paymentSettingRepository = paymentSettingRepository;
        this.userSelectionRepository = userSelectionRepository;
        this.initRepository = initRepository;
        this.paymentRepository = paymentRepository;
        this.experimentsRepository = experimentsRepository;
        this.postPaymentUrlsMapper = postPaymentUrlsMapper;
    }

    @Override
    public void initialize() {
        getView().showProgress();
        if (isViewAttached()) {
            initRepository.init().enqueue(new Callback<InitResponse>() {
                @Override
                public void success(final InitResponse initResponse) {
                    if (isViewAttached()) {
                        getView().hideProgress();
                        getView().showOneTap(ExperimentHelper.INSTANCE.getVariantFrom(
                            experimentsRepository.getExperiments(), KnownVariant.SCROLLED));
                    }
                }

                @Override
                public void failure(final ApiException apiException) {
                    if (isViewAttached()) {
                        getView().showError(
                            new MercadoPagoError(apiException, ApiUtil.RequestOrigin.POST_INIT));
                    }
                }
            });
        }
    }

    @Override
    public void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError) {
        getView().cancelCheckout();
    }

    @Override
    public void recoverFromFailure() {
        initialize();
    }

    @Override
    public void onPaymentResultResponse(@Nullable final Integer customResultCode, @Nullable final String backUrl,
        @Nullable final String redirectUrl) {
        final IPaymentDescriptor payment = paymentRepository.getPayment();
        final PostPaymentUrlsMapper.Response postPaymentUrls = postPaymentUrlsMapper.map(
            new PostPaymentUrlsMapper.Model(
                redirectUrl, backUrl, payment, paymentSettingRepository.getCheckoutPreference(),
                paymentSettingRepository.getSite().getId()
            )
        );
        new PostCongratsDriver.Builder(payment, postPaymentUrls)
            .customResponseCode(customResultCode)
            .action(new PostCongratsDriver.Action() {
                @Override
                public void goToLink(@NonNull final String link) {
                    getView().goToLink(link);
                }

                @Override
                public void openInWebView(@NonNull final String link) {
                    getView().openInWebView(link);
                }

                @Override
                public void exitWith(@Nullable final Integer customResponseCode, @Nullable final Payment payment) {
                    getView().finishWithPaymentResult(customResultCode, payment);
                }
            }).build().execute();
    }

    @Override
    public void onCardAdded(@NonNull final String cardId, @NonNull final LifecycleListener.Callback callback) {
        initRepository.refreshWithNewCard(cardId).enqueue(new Callback<InitResponse>() {
            @Override
            public void success(final InitResponse initResponse) {
                callback.onSuccess();
            }

            @Override
            public void failure(final ApiException apiException) {
                callback.onError();
            }
        });
    }
}
