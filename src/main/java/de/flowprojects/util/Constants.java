package de.flowprojects.util;

public enum Constants {
    DISCORD_API_TOKEN("MTIwMjI0NDQ1OTkyMDg5NjAyMA.GjSdEh.M8xtZzofxsnG0R5-9s41i_4aRx04yE7f4VH-C0"),
    GUILD_ID("463535775280136194"),
    APP_ID("1202244459920896020"),
    OWM_API_TOKEN("443a7dcba5ca5353d2fc3e5536ed64c6"),
    RIOT_API_TOKEN("RGAPI-1d979f1d-18b6-470b-b258-e2fe64e6e9f4"),
    GIPHY_API_TOKEN("clB2UrhdL6QEykjrYA5Frm9AXmUbIHzi");

    public final String value;

    private Constants(String value){
        this.value = value;
    }
}
