package me.lian.hsc.kquiz.dsl

/**
 * Text builder for an [me.lian.hsc.kquiz.data.EmbeddedAnswer] question.
 *
 * In addition to the regular [HtmlBuilder] markup, sub-questions can be inserted inline at the point
 * in the text where they should appear, using [multipleChoice], [multiResponse], [shortAnswer] and [numeric].
 */
@KQuizMarker
class ClozeTextBuilder : HtmlBuilder() {

  fun multipleChoice(block: ClozeMultipleChoiceDsl.() -> Unit) {
    raw(ClozeMultipleChoiceDsl().apply(block).build())
  }

  fun multiResponse(block: ClozeMultiResponseDsl.() -> Unit) {
    raw(ClozeMultiResponseDsl().apply(block).build())
  }

  fun shortAnswer(block: ClozeShortAnswerDsl.() -> Unit) {
    raw(ClozeShortAnswerDsl().apply(block).build())
  }

  fun numeric(block: ClozeNumericDsl.() -> Unit) {
    raw(ClozeNumericDsl().apply(block).build())
  }

}

/**
 * DSL for an embedded multiple-choice sub-question, where the student picks exactly one option,
 * e.g. `{1:MCS:=Correct~Wrong}` for a shuffled dropdown.
 */
@KQuizMarker
class ClozeMultipleChoiceDsl {

  /**
   * How the options are presented to the student.
   */
  enum class Type(internal val code: String) {
    Dropdown("MC"),
    RadioVertical("MCV"),
    RadioHorizontal("MCH"),
  }

  var type: Type = Type.Dropdown
  var shuffle: Boolean = false
  var score: Double = 1.0

  private val options = mutableListOf<ClozeOption>()

  fun option(text: String, block: ClozeOptionDsl.() -> Unit = {}) {
    options += ClozeOptionDsl(text).apply(block).build()
  }

  internal fun build(): String {
    check(options.isNotEmpty()) { "multipleChoice needs at least one option" }
    check(options.any { it.score.percentage > 0.0 }) { "multipleChoice needs at least one correct option" }
    val code = type.code + if (shuffle) "S" else ""
    return "{${formatWeight(score)}:$code:${options.joinToString("~") { it.render() }}}"
  }

}

/**
 * DSL for an embedded multi-response sub-question, where the student can pick multiple options,
 * e.g. `{1:MRHS:=Correct~=AlsoCorrect~Wrong}` for shuffled checkboxes laid out horizontally.
 */
@KQuizMarker
class ClozeMultiResponseDsl {

  /**
   * How the checkboxes are laid out.
   */
  enum class Layout(internal val code: String) {
    Vertical("MR"),
    Horizontal("MRH"),
  }

  var layout: Layout = Layout.Vertical
  var shuffle: Boolean = false
  var score: Double = 1.0

  private val options = mutableListOf<ClozeOption>()

  fun option(text: String, block: ClozeOptionDsl.() -> Unit = {}) {
    options += ClozeOptionDsl(text).apply(block).build()
  }

  internal fun build(): String {
    check(options.isNotEmpty()) { "multiResponse needs at least one option" }
    check(options.any { it.score.percentage > 0.0 }) { "multiResponse needs at least one correct option" }
    val code = layout.code + if (shuffle) "S" else ""
    return "{${formatWeight(score)}:$code:${options.joinToString("~") { it.render() }}}"
  }

}

/**
 * A single option of an embedded multiple-choice or short-answer sub-question.
 * @property text the option's text
 */
@KQuizMarker
class ClozeOptionDsl(private val text: String) {

  var score: Score = Score.Wrong
  private var feedback: String? = null

  fun feedback(text: String) {
    feedback = text
  }

  internal fun build() = ClozeOption(text, score, feedback)

}

internal data class ClozeOption(val text: String, val score: Score, val feedback: String?) {

  fun render(): String {
    val prefix = when (score.percentage) {
      100.0 -> "="
      0.0 -> ""
      else -> "%${formatWeight(score.percentage)}%"
    }
    val feedbackSuffix = feedback?.let { "#${escapeCloze(it)}" } ?: ""
    return "$prefix${escapeCloze(text)}$feedbackSuffix"
  }

}

/**
 * DSL for an embedded short-answer sub-question, e.g. `{1:SA:=Paris~Berlin}`.
 */
@KQuizMarker
class ClozeShortAnswerDsl {

  var score: Double = 1.0
  var caseSensitive: Boolean = false

  private val options = mutableListOf<ClozeOption>()

  fun answer(text: String, block: ClozeOptionDsl.() -> Unit = { score = Score.Correct }) {
    options += ClozeOptionDsl(text).apply(block).build()
  }

  internal fun build(): String {
    check(options.isNotEmpty()) { "shortAnswer needs at least one answer" }
    check(options.any { it.score.percentage > 0.0 }) { "shortAnswer needs at least one correct answer" }
    val type = if (caseSensitive) "SAC" else "SA"
    return "{${formatWeight(score)}:$type:${options.joinToString("~") { it.render() }}}"
  }

}

/**
 * DSL for an embedded numerical sub-question, e.g. `{1:NM:=13:0.5}`.
 */
@KQuizMarker
class ClozeNumericDsl {

  var score: Double = 1.0

  private val answers = mutableListOf<Triple<Double, Double, Score>>()

  fun answer(value: Double, tolerance: Double = 0.0, score: Score = Score.Correct) {
    answers += Triple(value, tolerance, score)
  }

  internal fun build(): String {
    check(answers.isNotEmpty()) { "numeric needs at least one answer" }
    check(answers.any { it.third.percentage > 0.0 }) { "numeric needs at least one correct answer" }
    val rendered = answers.joinToString("~") { (value, tolerance, score) ->
      val prefix = when (score.percentage) {
        100.0 -> "="
        0.0 -> ""
        else -> "%${formatWeight(score.percentage)}%"
      }
      "$prefix$value:$tolerance"
    }
    return "{${formatWeight(score)}:NM:$rendered}"
  }

}

private fun formatWeight(value: Double): String =
  if (value == value.toLong().toDouble()) value.toLong().toString() else value.toString()

private fun escapeCloze(text: String): String = buildString {
  for (c in text) {
    if (c in "\\:=#{}~") append('\\')
    append(c)
  }
}
