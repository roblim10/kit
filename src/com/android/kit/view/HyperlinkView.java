package com.android.kit.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Custom view built on top of TextView to create hyperlinks.
 * Use setClickableAction() and setClickableText() to set hyperlink click action and hyperlink text.
 * @author rlim
 *
 */
public class HyperlinkView extends TextView {
	
	private ClickableSpan clickableSpan;
	
	public HyperlinkView(Context context, AttributeSet attrs)  {
		super(context, attrs);
		setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	public void setClickableAction(final ClickableAction action)  {
		this.clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				action.onClick(widget);
			}
		};
	}
	
	public void setClickableText(CharSequence text)  {
		SpannableString ss = new SpannableString(text);
		ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		super.setText(ss, BufferType.SPANNABLE);
	}
	
	public static abstract class ClickableAction  {
		public abstract void onClick(View widget);
	}
}
