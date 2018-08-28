package org.trustnote.superwallet.widget;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class MyPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            return '•'; //●⚫ This is the important part
        }
        //「∙•」「•」「・」「●●」， letterSpacing 分别叫做「Bullet Operator」、「Bullet」、「Katakana Middle Dot」、「Black Circle」

        public int length() {
            return mSource.length(); // Return default
        }

        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}