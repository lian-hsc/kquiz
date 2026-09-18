package me.lian.hsc.kquiz.dsl

import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.kquiz.data.File
import me.lian.hsc.kquiz.data.SimpleText
import me.lian.hsc.kquiz.data.Text
import java.nio.file.Path
import kotlin.io.encoding.Base64
import kotlin.io.path.name
import kotlin.io.path.readBytes

/**
 * Builds a piece of [Text] using a small HTML DSL.
 *
 * Plain text added via [String.unaryPlus] is escaped.
 * Use [raw] to insert markup or other pre-built content verbatim.
 *
 * Images added via [image] are automatically registered as [me.lian.hsc.kquiz.data.File]s
 * so they are exported alongside the quiz and referenced from the text via Moodle's `@@PLUGINFILE@@` syntax.
 */
@KQuizMarker
open class HtmlBuilder {

  private val builder = StringBuilder()
  private val fileNames = mutableSetOf<String>()
  internal val files = mutableListOf<File>()

  operator fun String.unaryPlus() {
    builder.append(escapeHtml(this))
  }

  /**
   * Inserts [content] into the text without any escaping.
   */
  fun raw(content: String) {
    builder.append(content)
  }

  fun br() {
    builder.append("<br>")
  }

  /**
   * Adds an image with raw [bytes] as content and references it as `name` from the text.
   * @throws IllegalStateException if an image called [name] was already added
   */
  fun image(name: String, bytes: ByteArray, alt: String? = null): Unit =
    image(name, Base64.encode(bytes), alt)

  /**
   * Adds an image with base64-encoded [content] and references it as `name` from the text.
   * @throws IllegalStateException if an image called [name] was already added
   */
  fun image(name: String, content: String, alt: String? = null) {
    check(fileNames.add(name)) { "image \"$name\" was already added" }
    files += File(name, content, null, File.Encoding.Base64)
    builder.append("<img src=\"@@PLUGINFILE@@/").append(escapeHtmlAttribute(name)).append('"')
    if (alt != null) builder.append(" alt=\"").append(escapeHtmlAttribute(alt)).append('"')
    builder.append(">")
  }

  /**
   * Reads the image from [path] on disk and references it as `name` (the file name by default).
   */
  fun image(path: Path, name: String = path.name, alt: String? = null): Unit =
    image(name, path.readBytes(), alt)

  /**
   * Adds an image from a ktypst [Artifact], e.g. the result of rendering a diagram with ktypst.
   */
  fun image(name: String, artifact: Artifact, alt: String? = null): Unit =
    image(name, artifact.base64, alt)

  /**
   * Adds an image from the standard artifact of a ktypst compile [output].
   * @throws IllegalStateException if [output] has no standard artifact
   */
  fun image(name: String, output: TypstCompileOutput, alt: String? = null): Unit =
    image(name, checkNotNull(output.stdArtifact) { "ktypst output has no artifact to use as image \"$name\"" }, alt)

  internal fun build(): String = builder.toString()

  internal fun toSimpleText(format: Text.Format = Text.Format.HTML): SimpleText =
    SimpleText(build(), files, format)

}

// The tag functions below are generic extension functions, rather than members of HtmlBuilder, so that
// e.g. `p { }` called on a ClozeTextBuilder keeps ClozeTextBuilder (not HtmlBuilder) as the receiver
// inside the block. Without that, markers like `multipleChoice { }` would be invisible inside `p { }`:
// @KQuizMarker blocks reaching past the nearest receiver to an outer one, and a member `fun p(block:
// HtmlBuilder.() -> Unit)` would make the nearest receiver plain HtmlBuilder, hiding the subtype.
fun <T : HtmlBuilder> T.p(block: T.() -> Unit): Unit = htmlTag(this, "p", block)
fun <T : HtmlBuilder> T.strong(block: T.() -> Unit): Unit = htmlTag(this, "strong", block)
fun <T : HtmlBuilder> T.em(block: T.() -> Unit): Unit = htmlTag(this, "em", block)
fun <T : HtmlBuilder> T.code(block: T.() -> Unit): Unit = htmlTag(this, "code", block)
fun <T : HtmlBuilder> T.ul(block: T.() -> Unit): Unit = htmlTag(this, "ul", block)
fun <T : HtmlBuilder> T.ol(block: T.() -> Unit): Unit = htmlTag(this, "ol", block)
fun <T : HtmlBuilder> T.li(block: T.() -> Unit): Unit = htmlTag(this, "li", block)

fun <T : HtmlBuilder> T.a(href: String, block: T.() -> Unit) {
  raw("<a href=\"${escapeHtmlAttribute(href)}\">")
  block()
  raw("</a>")
}

private fun <T : HtmlBuilder> htmlTag(receiver: T, name: String, block: T.() -> Unit) {
  receiver.raw("<$name>")
  receiver.block()
  receiver.raw("</$name>")
}

internal fun escapeHtml(text: String): String = buildString {
  for (c in text) {
    when (c) {
      '&' -> append("&amp;")
      '<' -> append("&lt;")
      '>' -> append("&gt;")
      else -> append(c)
    }
  }
}

internal fun escapeHtmlAttribute(text: String): String = escapeHtml(text).replace("\"", "&quot;")
