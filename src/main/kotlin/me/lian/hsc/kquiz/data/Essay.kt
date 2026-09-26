package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonValue
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize

/**
 * A question that is answered with a free-form text (and/or files) and graded manually.
 *
 * @property responseFormat how the student enters the response
 * @property responseRequired whether the student must enter text, or may e.g. only upload files
 * @property responseFieldLines the height of the response input, in lines
 * @property minWordLimit the minimum number of words the response must have, if any
 * @property maxWordLimit the maximum number of words the response may have, if any
 * @property attachments how many files the student may upload (-1 for unlimited)
 * @property attachmentsRequired how many files the student must upload
 * @property maxBytes the maximum size of each uploaded file in bytes, if limited (0 uses the course's limit)
 * @property fileTypes the accepted file types, e.g. `.pdf,.png` or `image`, if limited
 * @property graderInfo information for graders, e.g. a model answer, shown to them but not to students
 * @property responseTemplate text the response input is pre-filled with
 * @see ResponseFormat
 * @see Question
 */
@JsonTypeName("essay")
class Essay(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  idNumber: String?,
  @JsonProperty("responseformat") val responseFormat: ResponseFormat,
  @JsonProperty("responserequired") @JsonSerialize(using = NumericBooleanSerializer::class) val responseRequired: Boolean,
  @JsonProperty("responsefieldlines") val responseFieldLines: Int,
  @JsonProperty("minwordlimit") val minWordLimit: Int?,
  @JsonProperty("maxwordlimit") val maxWordLimit: Int?,
  @JsonProperty("attachments") val attachments: Int,
  @JsonProperty("attachmentsrequired") val attachmentsRequired: Int,
  @JsonProperty("maxbytes") val maxBytes: Long?,
  @JsonProperty("filetypeslist") val fileTypes: String?,
  @JsonProperty("graderinfo") val graderInfo: SimpleText?,
  @JsonProperty("responsetemplate") val responseTemplate: SimpleText?,
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden, idNumber) {

  /**
   * How the student enters the response.
   */
  enum class ResponseFormat(@get:JsonValue val value: String) {

    /**
     * A rich-text editor.
     */
    Editor("editor"),

    /**
     * A rich-text editor that also allows embedding files.
     */
    EditorWithFilePicker("editorfilepicker"),

    /**
     * A plain-text field.
     */
    PlainText("plain"),

    /**
     * A plain-text field with a monospaced font.
     */
    Monospaced("monospaced"),

    /**
     * No text input; the response consists of attachments only.
     */
    NoInlineText("noinline"),

  }

}
