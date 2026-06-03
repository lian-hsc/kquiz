package me.lian.hsc.kquiz.test

import com.fasterxml.jackson.annotation.JsonInclude
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
          DragAndDropOntoImage(
            WrappedText("ddoi"),
            SimpleText("question [[1]] [[1]]", emptyList(), Text.Format.PlainText),
            0.0,
            emptyList(),
            null,
            null,
            File("background.png", Base64.encode(OsFile("guess-and-proof.png").readBytes()), null, File.Encoding.Base64),
            listOf(
              DragAndDropOntoImage.DraggableItem(1, 1, "drag1", null),
              DragAndDropOntoImage.DraggableItem(2, 2, null, File("drag2.png", Base64.encode(OsFile("drag-1-1.png").readBytes()), null, File.Encoding.Base64)),
              DragAndDropOntoImage.DraggableItem(3, 9, "drag3", null),
            ),
            false,
            listOf(
              DragAndDropOntoImage.Dropzone(1, "drop1", 1, -10.0, -10.0),
              DragAndDropOntoImage.Dropzone(2, "drop2", 2, 1600.0, 700.0),
            ),
            CombinedFeedback(null, null, false, null),
            MultipleTries(null, emptyList())
          )
        )
      )
    )

  OsFile("test.xml").writeText(value)
}