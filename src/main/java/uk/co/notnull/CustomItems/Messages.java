package uk.co.notnull.CustomItems;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum Messages implements MessageKeyProvider {

    JOIN__UNCLAIMED_ITEMS_AVAILABLE,
    COMMAND__INVALID_ITEM,
    COMMAND__INVALID_CATEGORY,
    COMMAND__GRANT_SUCCESS,
    COMMAND__GIVE_SUCCESS,
    COMMAND__GIVE_CATEGORY_SUCCESS;

    /**
     * Message keys that grab from the config to send messages
     */
    private final MessageKey key = MessageKey.of(this.name().toLowerCase().replace("__", ".").replace("_", "-"));


    /**
     * Get the message get from the config
     * @return message key
     */
    public MessageKey getMessageKey() {
        return key;
    }
}
