package me.lian.hsc.kquiz.serialization

import com.fasterxml.jackson.annotation.JsonInclude
import me.lian.hsc.kquiz.data.Quiz
import tools.jackson.databind.ObjectWriter
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.XmlWriteFeature
import java.nio.file.Path
import kotlin.io.path.writeText

val kQuizXmlWriter: ObjectWriter = XmlMapper.builder()
  .changeDefaultPropertyInclusion {
    JsonInclude.Value.construct(
      JsonInclude.Include.NON_EMPTY,
      JsonInclude.Include.NON_NULL
    )
  }
  .configure(XmlWriteFeature.WRITE_XML_DECLARATION, true)
  .configure(XmlWriteFeature.WRITE_NULLS_AS_XSI_NIL, false)
  .build()
  .writerWithDefaultPrettyPrinter()

fun Quiz.toXml(): String = kQuizXmlWriter.writeValueAsString(this)

fun Quiz.saveTo(file: Path) {
  file.writeText(toXml())
}