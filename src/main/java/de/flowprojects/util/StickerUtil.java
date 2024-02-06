package de.flowprojects.util;

public final class StickerUtil {

    public static final String STICKER_BASE_URL = "https://media.discordapp.net/stickers/";

    // Necessary because
    public static String getStickerExtension(int fileFormat) {
        String fileExtension = "";

        switch (fileFormat) {
            case 1,2 -> fileExtension = ".png";
            case 3 -> fileExtension = ".json";
            case 4 -> fileExtension = ".gif";
            default -> fileExtension = "UNKNOWN";
        }

        return fileExtension;
    }

    public static String getStickerURL(int fileFormat, long stickerId) {
        return StickerUtil.STICKER_BASE_URL.concat(Long.toString(stickerId).concat(StickerUtil.getStickerExtension(fileFormat)));
    }

    public static String getStickerURL(int fileFormat, String stickerId) {
        return StickerUtil.STICKER_BASE_URL.concat(stickerId).concat(StickerUtil.getStickerExtension(fileFormat));
    }
}
