package org.jala.university.infrastructure.external.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * Serializes and deserializes {@link LocalDate} instances using the ISO format {@code yyyy-MM-dd}.
 * Used to register with Gson for proper JSON handling of LocalDate fields.
 *
 */
public class LocalDateTypeAdapter implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Serializes a {@link LocalDate} to JSON as a string in {@code yyyy-MM-dd} format.
     *
     * @param date    the date to serialize (may be null)
     * @param typeOfSrc the actual type (ignored)
     * @param context context for serialization (ignored)
     * @return JSON string representation or {@code null} if date is null
     */
    @Override
    public JsonElement serialize(final LocalDate date, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(date.format(formatter));
    }

    /**
     * Deserializes a JSON string in {@code yyyy-MM-dd} format to a {@link LocalDate}.
     *
     * @param json    the JSON element to deserialize
     * @param typeOfT the type of the object to deserialize to (ignored)
     * @param context context for deserialization (ignored)
     * @return the parsed {@link LocalDate}
     * @throws JsonParseException if the string is not a valid date
     */
    @Override
    public LocalDate deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {
        return LocalDate.parse(json.getAsString(), formatter);
    }
}
