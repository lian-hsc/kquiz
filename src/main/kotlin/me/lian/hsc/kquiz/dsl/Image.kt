package me.lian.hsc.kquiz.dsl

import me.lian.hsc.ktypst.data.output.Artifact
import me.lian.hsc.ktypst.data.output.TypstCompileOutput
import me.lian.hsc.kquiz.data.File
import java.nio.file.Path
import kotlin.io.encoding.Base64
import kotlin.io.path.name
import kotlin.io.path.readBytes

/**
 * Builds a Moodle [File] called `name` from base64-encoded [content].
 */
fun image(name: String, content: String): File = File(name, content, null, File.Encoding.Base64)

/**
 * Builds a Moodle [File] called `name` from raw [bytes].
 */
fun image(name: String, bytes: ByteArray): File = image(name, Base64.encode(bytes))

/**
 * Builds a Moodle [File] by reading raw bytes from [path] on disk, named after `name` (the file name by default).
 */
fun image(path: Path, name: String = path.name): File = image(name, path.readBytes())

/**
 * Builds a Moodle [File] called `name` from a ktypst [Artifact], e.g. the result of rendering a diagram with ktypst.
 */
fun image(name: String, artifact: Artifact): File = image(name, artifact.base64)

/**
 * Builds a Moodle [File] called `name` from the standard artifact of a ktypst compile [output].
 * @throws IllegalStateException if [output] has no standard artifact
 */
fun image(name: String, output: TypstCompileOutput): File =
  image(name, checkNotNull(output.stdArtifact) { "ktypst output has no artifact to use as image \"$name\"" })
