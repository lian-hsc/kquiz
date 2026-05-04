package me.lian.hsc.kquiz.serialization

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.jackson.JsonValueSerializer
import tools.jackson.databind.ser.std.StdSerializer

class NumericBooleanSerializer : StdSerializer<Boolean>(Boolean::class.java) {

  override fun serialize(value: Boolean?, gen: JsonGenerator, ctxt: SerializationContext) {
    gen.writeNumber(if (value == true) 1 else 0)
  }

}