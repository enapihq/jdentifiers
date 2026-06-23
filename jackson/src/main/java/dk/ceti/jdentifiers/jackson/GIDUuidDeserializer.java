package dk.ceti.jdentifiers.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import dk.ceti.jdentifiers.id.GID;

import java.io.IOException;
import java.io.Serial;

public class GIDUuidDeserializer extends StdScalarDeserializer<GID<?>> {
    @Serial
    private static final long serialVersionUID = 1L;

    public GIDUuidDeserializer() {
        super(GID.class);
    }

    @Override
    public GID<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        // parseStrict (not the lenient fromString) so non-canonical UUIDs — short
        // groups and the "sign hole" (+e83dd89-... -> 0e83dd89-...) — are rejected
        // rather than silently coerced.
        var parsed = GID.parseStrict(text);
        if (parsed.isPresent()) {
            return parsed.get();
        }
        // Jackson's helper builds a JsonMappingException with line/column + field path.
        return (GID<?>) ctxt.handleWeirdStringValue(GID.class, text,
            "Invalid GID format: not a canonical UUID: %s", text
        );
    }
}
