package com.mercadopago.android.px.tracking.internal.mapper;

import androidx.annotation.NonNull;
import com.mercadopago.android.px.internal.mappers.NonNullMapper;
import com.mercadopago.android.px.model.AccountMoneyMetadata;
import com.mercadopago.android.px.model.BenefitsMetadata;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.internal.OneTapItem;
import com.mercadopago.android.px.tracking.internal.model.AccountMoneyExtraInfo;
import com.mercadopago.android.px.tracking.internal.model.AvailableMethod;
import com.mercadopago.android.px.tracking.internal.model.CardExtraExpress;
import java.util.Set;

public class FromExpressMetadataToAvailableMethods extends NonNullMapper<OneTapItem, AvailableMethod> {

    @NonNull private final FromApplicationToApplicationInfo fromApplicationToApplicationInfo;
    @NonNull private final Set<String> cardsWithEsc;
    @NonNull private final Set<String> cardsWithSplit;

    public FromExpressMetadataToAvailableMethods(
        @NonNull final FromApplicationToApplicationInfo fromApplicationToApplicationInfo,
        @NonNull final Set<String> cardsWithEsc,
        @NonNull final Set<String> cardsWithSplit) {
        this.fromApplicationToApplicationInfo = fromApplicationToApplicationInfo;
        this.cardsWithEsc = cardsWithEsc;
        this.cardsWithSplit = cardsWithSplit;
    }

    @Override
    public AvailableMethod map(@NonNull final OneTapItem oneTapItem) {
        boolean hasInterestFree = false;
        boolean hasReimbursement = false;
        final BenefitsMetadata benefits = oneTapItem.getBenefits();

        if (benefits != null) {
            hasInterestFree = benefits.getInterestFree() != null;
            hasReimbursement = benefits.getReimbursement() != null;
        }

        final AvailableMethod.Builder builder = new AvailableMethod.Builder(
            oneTapItem.getPaymentMethodId(),
            oneTapItem.getPaymentTypeId(),
            hasInterestFree, hasReimbursement, fromApplicationToApplicationInfo.map(oneTapItem.getApplications()));

        if (oneTapItem.isCard()) {
            final CardMetadata card = oneTapItem.getCard();

            builder.setExtraInfo(
                CardExtraExpress
                    .expressSavedCard(card, cardsWithEsc.contains(card.getId()), cardsWithSplit.contains(card.getId()))
                    .toMap()
            );
        } else if (oneTapItem.getAccountMoney() != null) {
            final AccountMoneyMetadata accountMoney = oneTapItem.getAccountMoney();

            builder.setExtraInfo(
                new AccountMoneyExtraInfo(accountMoney.getBalance(), accountMoney.isInvested()).toMap()
            );
        } else if (oneTapItem.isNewCard()) {
            return null;
        }

        return builder.build();
    }
}
