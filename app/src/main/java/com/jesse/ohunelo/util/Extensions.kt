package com.jesse.ohunelo.util

import android.text.*
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.WindowInsetsController
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.jesse.ohunelo.R
import kotlin.math.roundToInt

/**
 * This code is a Kotlin extension function that sets resizable text on a TextView. The purpose of this function is to allow for text that can be expanded or collapsed when the TextView is clicked. The function takes several parameters, including the full text to display, the maximum number of lines to show, and whether or not a "read more" or "read less" option should be displayed.

The function first checks whether the TextView is wide enough to display the full text without truncation. If so, the text is simply displayed as is. If not, the function truncates the text to the specified number of lines and adds a "read more" option if requested.

To add the "read more" option, the function uses a Spannable to add a clickable "read more" or "read less" link to the end of the truncated text. When the link is clicked, the text is expanded or collapsed accordingly.

The NoUnderlineClickSpan class is used to remove the underline from the clickable link. The setResizableText function also accepts an optional parameter applyExtraHighlights, which can be used to apply additional highlights to the text using a lambda expression.
 * **/
fun TextView.setResizableText(
    fullText: String,
    maxLines: Int,
    viewMore: Boolean,
    applyExtraHighlights: ((Spannable) -> (Spannable))? = null
) {
    val width = width
    if (width <= 0) {
        post {
            setResizableText(fullText, maxLines, viewMore, applyExtraHighlights)
        }
        return
    }
    movementMethod = LinkMovementMethod.getInstance()
    // Since we take the string character by character, we don't want to break up the Windows-style
    // line endings.
    val adjustedText = fullText.replace("\r\n", "\n")
    // Check if even the text has to be resizable.
    val textLayout = StaticLayout.Builder.obtain(adjustedText, 0, adjustedText.length, paint, width - paddingLeft - paddingRight)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
        .setIncludePad(includeFontPadding)
        .build()

    if (textLayout.lineCount <= maxLines || adjustedText.isEmpty()) {
        // No need to add 'read more' / 'read less' since the text fits just as well (less than max lines #).
        val htmlText = adjustedText.replace("\n", "<br/>")
        text = addClickablePartTextResizable(
            fullText,
            maxLines,
            HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            null,
            viewMore,
            applyExtraHighlights
        )
        return
    }
    val charactersAtLineEnd = textLayout.getLineEnd(maxLines - 1)
    val suffixText =
        if (viewMore) resources.getString(R.string.view_more) else resources.getString(R.string.view_less)
    var charactersToTake = charactersAtLineEnd - suffixText.length / 2 // Good enough first guess
    if (charactersToTake <= 0) {
        // Happens when text is empty
        val htmlText = adjustedText.replace("\n", "<br/>")
        text = addClickablePartTextResizable(
            fullText,
            maxLines,
            HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            null,
            viewMore,
            applyExtraHighlights
        )
        return
    }
    if (!viewMore) {
        // We can set the text immediately because nothing needs to be measured
        val htmlText = adjustedText.replace("\n", "<br/>")
        text = addClickablePartTextResizable(
            fullText,
            maxLines,
            HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
            suffixText,
            viewMore,
            applyExtraHighlights
        )
        return
    }
    val lastHasNewLine =
        adjustedText.substring(textLayout.getLineStart(maxLines - 1), textLayout.getLineEnd(maxLines - 1))
            .contains("\n")
    val linedText = if (lastHasNewLine) {
        val charactersPerLine =
            textLayout.getLineEnd(0) / (textLayout.getLineWidth(0) / textLayout.ellipsizedWidth.toFloat())
        val lineOfSpaces =
            "\u00A0".repeat(charactersPerLine.roundToInt()) // non breaking space, will not be thrown away by HTML parser
        charactersToTake += lineOfSpaces.length - 1
        adjustedText.take(textLayout.getLineStart(maxLines - 1)) +
                adjustedText.substring(textLayout.getLineStart(maxLines - 1), textLayout.getLineEnd(maxLines - 1))
                    .replace("\n", lineOfSpaces) +
                adjustedText.substring(textLayout.getLineEnd(maxLines - 1))
    } else {
        adjustedText
    }
    // Check if we perhaps need to even add characters? Happens very rarely, but can be possible if there was a long word just wrapped
    val shortenedString = linedText.take(charactersToTake)
    val shortenedStringWithSuffix = shortenedString + suffixText
    val shortenedStringWithSuffixLayout = StaticLayout.Builder.obtain(shortenedStringWithSuffix, 0, shortenedStringWithSuffix.length, paint, width - paddingLeft - paddingRight)
        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
        .setIncludePad(includeFontPadding)
        .build()

    val modifier: Int
    if (shortenedStringWithSuffixLayout.getLineEnd(maxLines - 1) >= shortenedStringWithSuffix.length) {
        modifier = 1
        charactersToTake-- // We might just be at the right position already
    } else {
        modifier = -1
    }
    do {
        charactersToTake += modifier
        val baseString = linedText.take(charactersToTake)
        val appended = baseString + suffixText
        val newLayout = StaticLayout.Builder.obtain(appended, 0, appended.length, paint, width - paddingLeft - paddingRight)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
            .setIncludePad(includeFontPadding)
            .build()
    } while ((modifier < 0 && newLayout.getLineEnd(maxLines - 1) < appended.length) ||
        (modifier > 0 && newLayout.getLineEnd(maxLines - 1) >= appended.length)
    )
    if (modifier > 0) {
        charactersToTake-- // We went overboard with 1 char, fixing that
    }
    // We need to convert newlines because we are going over to HTML now
    val htmlText = linedText.take(charactersToTake).replace("\n", "<br/>")
    text = addClickablePartTextResizable(
        fullText,
        maxLines,
        HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT),
        suffixText,
        viewMore,
        applyExtraHighlights
    )
}

private fun TextView.addClickablePartTextResizable(
    fullText: String,
    maxLines: Int,
    shortenedText: Spanned,
    clickableText: String?,
    viewMore: Boolean,
    applyExtraHighlights: ((Spannable) -> (Spannable))? = null
): Spannable {
    val builder = SpannableStringBuilder(shortenedText)
    if (clickableText != null) {
        builder.append(clickableText)
        val startIndexOffset = if (viewMore) 1 else 0 // Do not highlight the 3 dots and the space
        builder.setSpan(object : NoUnderlineClickSpan(context) {
            override fun onClick(widget: View) {
                if (viewMore) {
                    setResizableText(fullText, maxLines, false, applyExtraHighlights)
                } else {
                    setResizableText(fullText, maxLines, true, applyExtraHighlights)
                }
            }
        }, builder.indexOf(clickableText) + startIndexOffset, builder.indexOf(clickableText) + clickableText.length, 0)
    }
    if (applyExtraHighlights != null) {
        return applyExtraHighlights(builder)
    }
    return builder
}
