package com.pixplicity.sharp;

import android.support.annotation.Nullable;

public interface FontFilenameProvider {
    @Nullable
    String getFontFilename(String family, String style, String weight);
}
