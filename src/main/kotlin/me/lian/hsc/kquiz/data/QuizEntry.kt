package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

/**
 * A quiz entry represents a single question in a quiz.
 */
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type"
)
sealed interface QuizEntry

/**
 * A question in a quiz.
 * @property name the name of the question
 * @property question the question text
 * @property defaultGrade the default grade for the question that is used when added to a quiz
 * @property tags the tags for the question
 * @property generalFeedback the general feedback for the question, regardless of whether it is answered correctly or not
 * @property hidden whether the question is hidden
 */
sealed class Question(
  val name: WrappedText<String>,
  @JsonProperty("questiontext") val question: SimpleText,
  @JsonProperty("defaultgrade") val defaultGrade: Double,
  @JacksonXmlElementWrapper(localName = "tags") @JsonProperty("tag") val tags: List<WrappedText<String>>,
  @JsonProperty("generalfeedback") val generalFeedback: SimpleText?,
  @JsonSerialize(using = NumericBooleanSerializer::class) val hidden: Boolean?,
) : QuizEntry
