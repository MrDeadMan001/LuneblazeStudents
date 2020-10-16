package com.avadna.luneblaze.helperClasses;

import android.app.Dialog; import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import android.content.Context;

import com.avadna.luneblaze.R;

public class MyCustomThemeDialog extends Dialog {

    public MyCustomThemeDialog(Context context) {
        super(context, R.style.NoTitleDialogTheme);
    }

    public MyCustomThemeDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

}

