package me.lian.hsc.kquiz.test

import com.fasterxml.jackson.annotation.JsonInclude
import lian.hsc.ktypst.stdlib.cetz.CetzLine
import me.lian.hsc.kquiz.data.*
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.dataformat.xml.XmlWriteFeature
import kotlin.io.encoding.Base64

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
          MultipleChoice(
            WrappedText("Capitals"),
            SimpleText(
              "What is the capital of France?",
              emptyList(),
              Text.Format.MoodleAutoFormat
            ),
            1.0,
            emptyList(),
            null,
            false,
            listOf(
              MultipleChoice.Answer("Paris", emptyList(), Text.Format.MoodleAutoFormat, Fraction.Positive.One, null),
              MultipleChoice.Answer("Berlin", emptyList(), Text.Format.MoodleAutoFormat, Fraction.Zero, null),
              MultipleChoice.Answer("London", emptyList(), Text.Format.MoodleAutoFormat, Fraction.Zero, null),
            ),
            single = true,
            shuffle = true,
            showStandardInstructions = false,
            numbering = MultipleChoice.Numbering.None,
            combinedFeedback = CombinedFeedback(null, null, false, null),
            multipleTries = MultipleTries(null, emptyList())
          )
        )
      )
    )

  OsFile("test.xml").writeText(value)
}