package com.dongnh.bubblepicker.model

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Created by irinagalata on 1/19/17.
 */
data class PickerItem @JvmOverloads constructor(
    var title: String? = null,
    var icon: Drawable? = null,
    var iconOnTop: Boolean = true,
    @ColorInt var color: Int? = null,
    var gradient: BubbleGradient? = null,
    var overlayAlpha: Float = 0.4f,
    var typeface: Typeface = Typeface.DEFAULT,
    @ColorInt var textColor: Int? = null,
    var textSize: Float = 40f,
    var imgDrawable: Drawable? = null,
    var showImageOnUnSelected: Boolean = false,
    var isSelected: Boolean = false,
    var isViewBorderSelected: Boolean = false,
    @ColorInt var colorBorderSelected: Int? = null,
    var strokeWidthBorder: Float = 10f,
    var customData: Any? = null
)