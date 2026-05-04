package me.lian.hsc.kquiz.serialization

import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ser.std.StdSerializer

class PresenceBooleanSerializer : StdSerializer<Boolean>(Boolean::class.java) {

  override fun serialize(value: Boolean?, gen: JsonGenerator, ctxt: SerializationContext) {
    if (value == true) gen.writeNull()
  }

  override fun isEmpty(ctxt: SerializationContext?, value: Boolean?): Boolean {
    return value != true
  }

}