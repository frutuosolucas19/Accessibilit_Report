package com.example.acessibilit_report.util;

import android.graphics.Typeface;
import android.text.InputType;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.example.acessibilit_report.R;

/**
 * Configura o ícone de olho (drawableEnd) de um EditText de senha para
 * mostrar / ocultar o texto digitado ao ser tocado.
 */
public final class PasswordToggle {

    private PasswordToggle() {}

    public static void setup(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                android.graphics.drawable.Drawable endIcon = editText.getCompoundDrawables()[2];
                if (endIcon != null) {
                    float touchX = event.getX();
                    float iconStart = editText.getWidth()
                            - editText.getPaddingEnd()
                            - endIcon.getIntrinsicWidth();
                    if (touchX >= iconStart) {
                        toggle(editText);
                        v.performClick();
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private static void toggle(EditText et) {
        int cursor = et.getSelectionEnd();
        boolean isHidden = (et.getInputType() & InputType.TYPE_MASK_VARIATION)
                == InputType.TYPE_TEXT_VARIATION_PASSWORD;

        if (isHidden) {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            et.setCompoundDrawablesWithIntrinsicBounds(
                    et.getCompoundDrawables()[0], null,
                    ContextCompat.getDrawable(et.getContext(), R.drawable.ic_eye_off), null);
        } else {
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et.setCompoundDrawablesWithIntrinsicBounds(
                    et.getCompoundDrawables()[0], null,
                    ContextCompat.getDrawable(et.getContext(), R.drawable.ic_eye), null);
        }

        // setInputType reseta o Typeface — restaura para evitar mudança de fonte
        et.setTypeface(Typeface.DEFAULT);
        et.setSelection(Math.min(cursor, et.getText().length()));
    }
}
