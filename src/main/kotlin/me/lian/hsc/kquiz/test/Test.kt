package me.lian.hsc.kquiz.test

import com.fasterxml.jackson.annotation.JsonInclude
import me.lian.hsc.kquiz.data.*
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.XmlWriteFeature
import java.text.Format
import java.text.Normalizer
import javax.xml.crypto.Data
import kotlin.random.Random

typealias OsFile = java.io.File

fun main() {
  val value = XmlMapper.builder()
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
    .writeValueAsString(
      Quiz(
        listOf(
          Numerical(
            WrappedText("Question 1"),
            SimpleText("What is 1 + 1?", emptyList(), Text.Format.PlainText),
            1.0,
            emptyList(),
            null,
            false,
            listOf(
              Numerical.Answer("2", emptyList(), Text.Format.PlainText, Fraction.Positive.One, null, 0.0),
            ),
            Units(
              Units.UnitHandling.RequiredResponsePenalty,
              0.5,
              Units.UnitDisplayMode.TextInput,
              Units.UnitLocation.Right,
              listOf(
                Units.UnitEntry("m", 10.0),
                Units.UnitEntry("cm", 1.0),
              )
            ),
            MultipleTries(MultipleTries.Penalty.None, emptyList())
          )
        )
      )
    )

  OsFile("test.xml").writeText(value)
}