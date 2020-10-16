package com.avadna.luneblaze.helperClasses;

public class VenueSlotJsons {
    public static String venue8 = "{\"days\":[{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]}]}";

    public static String venue9 = "{\"days\":[{\"slots\":[{\"start\":\"09:00\",\"end\":\"11:00\"},{\"start\":\"19:00\",\"end\":\"21:00\"}]},{\"slots\":[{\"start\":\"09:00\",\"end\":\"11:00\"},{\"start\":\"19:00\",\"end\":\"21:00\"}]},{\"slots\":[{\"start\":\"09:00\",\"end\":\"11:00\"},{\"start\":\"19:00\",\"end\":\"21:00\"}]},{\"slots\":[{\"start\":\"09:00\",\"end\":\"11:00\"},{\"start\":\"19:00\",\"end\":\"21:00\"}]},{\"slots\":[{\"start\":\"09:00\",\"end\":\"11:00\"},{\"start\":\"19:00\",\"end\":\"21:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]},{\"slots\":[{\"start\":\"10:00\",\"end\":\"17:00\"}]}]}";

    public static String venue11 = "{\"days\":[{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[{\"start\":\"11:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"11:00\",\"end\":\"14:00\"}]}]}";

    public static String venue14 = "{\"days\":[{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[{\"start\":\"16:00\",\"end\":\"18:00\"}]},{\"slots\":[]}]}";

    public static String venue15 = "{\"days\":[{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[]},{\"slots\":[]}]}";

    public static String venue16 = "{\"days\":[{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[{\"start\":\"14:00\",\"end\":\"15:30\"}]},{\"slots\":[]},{\"slots\":[]}]}";

    public static String venue17 = "{\"days\":[{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[]},{\"slots\":[{\"start\":\"11:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"11:00\",\"end\":\"17:00\"}]}]}";

    public static String venue19 = "{\"days\":[{\"slots\":[{\"start\":\"12:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"12:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"12:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"12:00\",\"end\":\"14:00\"}]},{\"slots\":[{\"start\":\"12:00\",\"end\":\"14:00\"}]},{\"slots\":[]},{\"slots\":[]}]}";

    public static String getVenueSlotJson(int venueId) {
        switch (venueId) {
            case 8:
                return venue8;
            case 9:
                return venue9;

            case 11:
                return venue11;
            case 14:
                return venue14;
            case 15:
                return venue15;
            case 16:
                return venue16;
            case 17:
                return venue17;

            case 19:
                return venue19;
            default:
                return venue8;
        }
    }
}
