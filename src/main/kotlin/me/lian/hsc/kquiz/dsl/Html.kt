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

  /**
   * Adds an HTML comment, e.g. credits: kept in the text, but not shown.
   * @throws IllegalArgumentException if [text] contains `-->`, which would end the comment early
   */
  fun comment(text: String) {
    require("-->" !in text) { "an HTML comment can't contain \"-->\"" }
    builder.append("<!--").append(text).append("-->")
  }

  fun br(vararg attributes: Pair<String, String>) {
    voidElement("br", *attributes)
  }

  /**
   * Adds an image with raw [bytes] as content and references it as `name` from the text.
   * @throws IllegalStateException if a file called [name] was already added
   */
  fun image(name: String, bytes: ByteArray, alt: String? = null, vararg attributes: Pair<String, String>): Unit =
    image(name, Base64.encode(bytes), alt, *attributes)

  /**
   * Adds an image with base64-encoded [content] and references it as `name` from the text. Further
   * [attributes] of the `img` tag, e.g. `"width" to "400"`, follow `src` and [alt].
   * @throws IllegalStateException if a file called [name] was already added
   */
  fun image(name: String, content: String, alt: String? = null, vararg attributes: Pair<String, String>) {
    attachment(name, content)
    val all = listOf("src" to "@@PLUGINFILE@@/$name") + listOfNotNull(alt?.let { "alt" to it }) + attributes
    raw("<img${attributeString(all)}>")
  }

  /**
   * Adds a base64-encoded file called [name] to the text without showing it, e.g. to link it with
   * `a("@@PLUGINFILE@@/$name") { }` or to reference it more than once.
   * @throws IllegalStateException if a file called [name] was already added
   */
  fun attachment(name: String, content: String) {
    check(fileNames.add(name)) { "file \"$name\" was already added" }
    files += File(name, content, null, File.Encoding.Base64)
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
//
// Each takes the tag's attributes, e.g. `p("dir" to "ltr", "style" to "text-align: left;") { }`.

/** The element [name] with [attributes] around the content of [block], e.g. `element("video") { }`. */
fun <T : HtmlBuilder> T.element(name: String, vararg attributes: Pair<String, String>, block: T.() -> Unit) {
  raw("<$name${attributeString(attributes.toList())}>")
  block()
  raw("</$name>")
}

/** The element [name] with [attributes] and without content or end tag, e.g. `voidElement("hr")`. */
fun HtmlBuilder.voidElement(name: String, vararg attributes: Pair<String, String>) {
  raw("<$name${attributeString(attributes.toList())}>")
}

fun <T : HtmlBuilder> T.p(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("p", *attributes, block = block)
fun <T : HtmlBuilder> T.div(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("div", *attributes, block = block)
fun <T : HtmlBuilder> T.span(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("span", *attributes, block = block)
fun <T : HtmlBuilder> T.h1(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h1", *attributes, block = block)
fun <T : HtmlBuilder> T.h2(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h2", *attributes, block = block)
fun <T : HtmlBuilder> T.h3(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h3", *attributes, block = block)
fun <T : HtmlBuilder> T.h4(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h4", *attributes, block = block)
fun <T : HtmlBuilder> T.h5(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h5", *attributes, block = block)
fun <T : HtmlBuilder> T.h6(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("h6", *attributes, block = block)
fun <T : HtmlBuilder> T.strong(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("strong", *attributes, block = block)
fun <T : HtmlBuilder> T.b(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("b", *attributes, block = block)
fun <T : HtmlBuilder> T.em(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("em", *attributes, block = block)
fun <T : HtmlBuilder> T.i(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("i", *attributes, block = block)
fun <T : HtmlBuilder> T.u(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("u", *attributes, block = block)
fun <T : HtmlBuilder> T.s(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("s", *attributes, block = block)
fun <T : HtmlBuilder> T.sub(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("sub", *attributes, block = block)
fun <T : HtmlBuilder> T.sup(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("sup", *attributes, block = block)
fun <T : HtmlBuilder> T.small(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("small", *attributes, block = block)
fun <T : HtmlBuilder> T.code(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("code", *attributes, block = block)
fun <T : HtmlBuilder> T.pre(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("pre", *attributes, block = block)
fun <T : HtmlBuilder> T.blockquote(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("blockquote", *attributes, block = block)
fun <T : HtmlBuilder> T.ul(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("ul", *attributes, block = block)
fun <T : HtmlBuilder> T.ol(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("ol", *attributes, block = block)
fun <T : HtmlBuilder> T.li(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("li", *attributes, block = block)
fun <T : HtmlBuilder> T.table(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("table", *attributes, block = block)
fun <T : HtmlBuilder> T.thead(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("thead", *attributes, block = block)
fun <T : HtmlBuilder> T.tbody(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("tbody", *attributes, block = block)
fun <T : HtmlBuilder> T.tr(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("tr", *attributes, block = block)
fun <T : HtmlBuilder> T.th(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("th", *attributes, block = block)
fun <T : HtmlBuilder> T.td(vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit = element("td", *attributes, block = block)

fun <T : HtmlBuilder> T.a(href: String, vararg attributes: Pair<String, String>, block: T.() -> Unit): Unit =
  element("a", "href" to href, *attributes, block = block)

fun HtmlBuilder.hr(vararg attributes: Pair<String, String>): Unit = voidElement("hr", *attributes)

private fun attributeString(attributes: List<Pair<String, String>>): String =
  attributes.joinToString("") { (name, value) -> " $name=\"${escapeHtmlAttribute(value)}\"" }

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
