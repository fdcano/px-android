package com.mercadopago.android.px.plugins;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.components.Renderer;
import com.mercadopago.android.px.components.RendererFactory;

public abstract class PluginRenderer<T extends PluginComponent> extends Renderer<T> {

    @Override
    @CallSuper
    public View render(@NonNull final T component, @NonNull final Context context, final ViewGroup parent) {
        final ViewGroup view = (ViewGroup) inflate(R.layout.px_plugin_layout, parent);
        RendererFactory.create(context, component.getToolbarComponent()).render(view);

        final View contents = renderContents(component, context);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0, 1f);
        contents.setLayoutParams(params);
        view.addView(contents);
        return view;
    }

    public abstract View renderContents(final T component, final Context context);
}
