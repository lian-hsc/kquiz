package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Essay

/**
 * DSL for an [Essay] question.
 */
@KQuizMarker
class EssayDsl : QuestionDsl<Essay>() {

  var defaultGrade: Double = 1.0
  var responseFormat: Essay.ResponseFormat = Essay.ResponseFormat.Editor
  var responseRequired: Boolean = true
  var responseFieldLines: Int = 15
  var minWordLimit: Int? = null
  var maxWordLimit: Int? = null
  var attachments: Int = 0
  var attachmentsRequired: Int = 0

  /** The maximum size of each uploaded file in bytes, if limited (0 uses the course's limit). */
  var maxBytes: Long? = null

  /** The accepted file types, e.g. `.pdf,.png` or `image`, if limited. */
  var fileTypes: String? = null

  private var questionBlock: (HtmlBuilder.() -> Unit) by Required("question")
  private var graderInfoBlock: (HtmlBuilder.() -> Unit)? = null
  private var responseTemplateBlock: (HtmlBuilder.() -> Unit)? = null

  fun question(block: HtmlBuilder.() -> Unit) {
    questionBlock = block
  }

  /**
   * Information for graders, e.g. a model answer; shown to graders, not to students.
   */
  fun graderInfo(block: HtmlBuilder.() -> Unit) {
    graderInfoBlock = block
  }

  /**
   * Text the response input is pre-filled with.
   */
  fun responseTemplate(block: HtmlBuilder.() -> Unit) {
    responseTemplateBlock = block
  }

  override fun build(): Essay {
    check(attachmentsRequired <= attachments || attachments == -1) {
      "essay requires $attachmentsRequired attachments but only allows $attachments"
    }
    val question = HtmlBuilder().apply(questionBlock)
    return Essay(
      buildName(),
      question.toSimpleText(),
      defaultGrade,
      buildTags(),
      buildGeneralFeedback(),
      hidden,
      idNumber,
      responseFormat,
      responseRequired,
      responseFieldLines,
      minWordLimit,
      maxWordLimit,
      attachments,
      attachmentsRequired,
      maxBytes,
      fileTypes,
      graderInfoBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
      responseTemplateBlock?.let { HtmlBuilder().apply(it).toSimpleText() },
    )
  }

}
