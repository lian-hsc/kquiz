package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

@JsonTypeName("truefalse")
class TrueFalse private constructor(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<MultipleChoice.Answer>,
  @JsonProperty("showstandardinstructions") @JsonSerialize(using = NumericBooleanSerializer::class) val showStandardInstructions: Boolean,
  ) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {


  constructor(
    name: WrappedText<String>,
    question: SimpleText,
    correctAnswer: Boolean,
    tags: List<WrappedText<String>>,
    generalFeedback: SimpleText?,
    hidden: Boolean?,
    trueFeedback: SimpleText?,
    falseFeedback: SimpleText?,
    defaultGrade: Double,
    showStandardInstructions: Boolean,
  ) : this(
    name,
    question,
    defaultGrade,
    tags,
    generalFeedback,
    hidden,
    listOf(
      MultipleChoice.Answer(
        "true",
        emptyList(),
        Text.Format.PlainText,
        if (correctAnswer) Fraction.Positive.One else Fraction.Zero,
        trueFeedback
      ),
      MultipleChoice.Answer(
        "false",
        emptyList(),
        Text.Format.PlainText,
        if (!correctAnswer) Fraction.Positive.One else Fraction.Zero,
        falseFeedback
      )
    ),
    showStandardInstructions
  )

}