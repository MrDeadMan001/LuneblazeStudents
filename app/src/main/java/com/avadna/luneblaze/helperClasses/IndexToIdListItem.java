package com.avadna.luneblaze.helperClasses;

public class IndexToIdListItem {
    public int oldIndex;
    public int spannedIndexStart;
    public int spannedIndexEnd;
    public String userId;

    public IndexToIdListItem(int oldIndex, int spannedIndexStart, int spannedIndexEnd, String userId) {
        this.oldIndex = oldIndex;
        this.spannedIndexStart = spannedIndexStart;
        this.spannedIndexEnd = spannedIndexEnd;
        this.userId = userId;
    }
}
