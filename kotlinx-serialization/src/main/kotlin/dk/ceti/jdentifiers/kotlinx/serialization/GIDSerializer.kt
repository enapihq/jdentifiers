package dk.ceti.jdentifiers.kotlinx.serialization

import dk.ceti.jdentifiers.id.GID
import dk.ceti.jdentifiers.id.IDAble
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object GIDSerializer : KSerializer<GID<IDAble>> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(GID::class.qualifiedName!!, PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: GID<IDAble>) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): GID<IDAble> {
        val text = decoder.decodeString()
        // parseStrict (not lenient fromString): reject non-canonical UUIDs — short
        // groups and the "sign hole" (+e83dd89-... -> 0e83dd89-...) — rather than
        // silently coercing them.
        return GID.parseStrict<IDAble>(text).orElseThrow {
            SerializationException("Invalid GID: $text")
        }
    }
}
