package me.lian.hsc.kquiz.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeName
import com.fasterxml.jackson.annotation.JsonUnwrapped
import com.fasterxml.jackson.annotation.JsonValue
import me.lian.hsc.kquiz.serialization.NumericBooleanSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import tools.jackson.dataformat.xml.annotation.JacksonXmlProperty

/**
 * A calculated question represents a question that uses wildcards to calculate the answer.
 * Wildcards are referenced in the [question] text and [answers] by using the `{wildcard}` format, where `wildcard` is the name of the wildcard.
 *
 * An answer must not be a single wildcard without any actual calculation done on it.
 *
 * Every dataset must already include its items.
 * The size of a dataset should be equal to the number of items in the dataset.
 * If it is less than the number of items in the dataset, only the first `itemcount` items are used, all further items are ignored.
 * If it is greater than the number of items in the dataset, Moodle will ignore the extra items.
 * The configuration for the dataset (i.e., its distribution, minimum and maximum value, etc.) should match the item in the dataset;
 * however, Moodle will not check this -- therefore, any item will be importated, regardless of whether it is correct or not.
 * The configuration of the dataset is only used to generate new items.
 *
 * For a dataset to be shared between multiple questions, the question must have its [synchronizeWildcards] property set to [SynchronizeWildcards.Public] or [SynchronizeWildcards.PublicNamed]
 * AND the dataset must be set to be [Dataset.Status.Shared].
 * If either the question or the dataset is set to be [SynchronizeWildcards.Private] or [Dataset.Status.Private], the dataset is not shared.
 * A shared dataset must be defined in the same way for every question that uses that dataset;
 * otherwise, Moodle will assume the dataset to be private for the second question which is encountered.
 * If a shared dataset already exists, but a question tries to define it differently, Moodle will set the new dataset to be [Dataset.Status.Private].
 *
 * @property answers the answers to the question
 * @property synchronizeWildcards whether the question wildcards are synchronized with other questions or not
 * @property units the units of the question
 * @property multipleTries handling of multiple tries for the question
 * @property datasets the datasets used for the question
 * @see Answer
 * @see Units
 * @see MultipleTries
 * @see Dataset
 * @see Question
 */
@JsonTypeName("calculated")
class Calculated(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  @JsonProperty("synchronize") val synchronizeWildcards: SynchronizeWildcards,
  @JsonUnwrapped val units: Units,
  @JsonUnwrapped val multipleTries: MultipleTries,
  @JacksonXmlElementWrapper(localName = "dataset_definitions") @JsonProperty("dataset_definition") val datasets: List<Dataset>
) : Question(name, question, defaultGrade, tags, generalFeedback, hidden) {

  /**
   * Whether the question wildcards are synchronized with other questions or not.
   */
  enum class SynchronizeWildcards(@get:JsonValue val value: Int) {
    /**
     * The question wildcards are not synchronized with other questions.
     * If the [Calculated.Dataset.Status] of the dataset is set to [Calculated.Dataset.Status.Shared], the dataset is still not shared.
     */
    Private(0),

    /**
     * The question wildcards are shared if and only if the [Calculated.Dataset.Status] of the dataset is set to [Calculated.Dataset.Status.Shared].
     * If the [Calculated.Dataset.Status] of the dataset is set to [Calculated.Dataset.Status.Private], the dataset is not shared.
     */
    Public(1),

    /**
     * Has the same effect as [Public] but assumes, that the name of all shared datasets is added to the name of the question in the following format:
     * `#{sharedWildcard1}{sharedWildcard2}...{sharedWildcardN}#questionName`.
     * [Calculated.Dataset.Status.Private] wildcards should not be used in the name of the question.
     *
     * If the name of the question does not match the format, Moodle will import the question with the set name anyway.
     * Only if the question is edited afterward, the name of the question will be changed.
     */
    PublicNamed(2),
  }

  /**
   * An answer to a calculated question.
   * @property fraction the fraction of the total points that the answer is worth
   * @property tolerance the tolerance of the answer
   * @property toleranceType the type of the tolerance
   * @property correctAnswerLength the length of the correct answer
   * @property correctAnswerFormat the format of the correct answer
   */
  class Answer(
    val text: String,
    @JacksonXmlProperty(isAttribute = true) val fraction: Fraction,
    val tolerance: Double,
    @JsonProperty("tolerancetype") val toleranceType: ToleranceType,
    @JsonProperty("correctanswerlength") val correctAnswerLength: Int,
    @JsonProperty("correctanswerformat") val correctAnswerFormat: AnswerFormat,
  ) {

    enum class ToleranceType(@get:JsonValue val value: Int) {
      /**
       * The tolerance is relative to the correct answer.
       * The answer is correct if `|dx|/x <= tolerance`.
       */
      Relative(0),

      /**
       * The tolerance is absolute.
       * The answer is correct if `|dx| <= tolerance`.
       */
      Nominal(1),

      /**
       * The tolerance is a geometric factor.
       * The answer is correct if `x/(1 + tolerance) <= (x + dx) <= x * (1 + tolerance)`.
       */
      Geometric(2),
    }

    /**
     * The format of the correct answer.
     */
    enum class AnswerFormat(@get:JsonValue val value: Int) {
      /**
       * The length is measured in decimal places.
       */
      Decimal(1),

      /**
       * The length is measured in significant figures.
       */
      SignificantFigures(2),
    }

  }

  /**
   * A dataset used for a calculated question.
   * See [Calculated] for more information.
   *
   * @property status the status of the dataset
   * @property name the name of the dataset
   * @property distribution the distribution of the dataset
   * @property minimum the minimum value of the dataset
   * @property maximum the maximum value of the dataset
   * @property decimals the number of decimals of the dataset
   * @property itemCount the number of items in the dataset
   * @property items the items of the dataset
   * @see Calculated
   */
  class Dataset(
    val status: WrappedText<Status>,
    val name: WrappedText<String>,
    val distribution: WrappedText<Distribution>,
    val minimum: WrappedText<Double>,
    val maximum: WrappedText<Double>,
    val decimals: WrappedText<Int>,
    @JsonProperty("itemcount") val itemCount: Int,
    @JacksonXmlElementWrapper(localName = "dataset_items") @JsonProperty("dataset_item") val items: List<Item>
  ) {

    // Required by Moodle, but always set to calculated.
    // Yet to figure out what this is for.
    val type: String = "calculated"

    /**
     * The status of the dataset.
     */
    enum class Status(@get:JsonValue val text: String) {
      /**
       * The dataset is only used by this question.
       */
      Private("private"),

      /**
       * The dataset is shared between multiple questions.
       * If the question's synchronization status is set to [SynchronizeWildcards.Private], the dataset is not shared, even if the dataset status is set to [Shared].
       *
       * See [Calculated] for more information on how the sharing of datasets works.
       */
      Shared("shared"),
    }

    /**
     * The distribution of the dataset.
     */
    enum class Distribution(@get:JsonValue val text: String) {
      /**
       * The dataset is uniformly distributed.
       */
      Uniform("uniform"),

      /**
       * The dataset is distributed uniformly with a bias towards the lower end of the distribution.
       */
      LongUniform("longuniform"),
    }

    /**
     * An item of a dataset.
     * @property number the number of the item
     * @property value the value of the item
     */
    data class Item(
      val number: Int,
      val value: Double,
    )

  }

}

/**
 * A calculated multiple-choice question represents a question that uses wildcards to calculate the answer.
 * This question has the same properties as [Calculated] but displays all possible [answers] to the user and
 * allows the user to select one or more of them as their answer in the same way as a [MultipleChoice] question.
 *
 * An answer may use `{wildcard}` to reference a wildcard and `{=calculation}` to reference a calculation.
 * A calculation may again contain `{wildcard}`s.
 *
 * @property answers the answers to the question
 * @property single whether the question is single- or multi-answered
 * @property synchronizeWildcards whether the question wildcards are synchronized with other questions or not
 * @property combinedFeedback the combined feedback for the question
 * @property multipleTries handling of multiple tries for the question
 * @property datasets the datasets used for the question
 * @see Calculated
 * @see CombinedFeedback
 * @see MultipleTries
 * @see Dataset
 * @see Question
 */
@JsonTypeName("calculatedmulti")
class CalculatedMultipleChoice(
  name: WrappedText<String>,
  question: SimpleText,
  defaultGrade: Double,
  tags: List<WrappedText<String>>,
  generalFeedback: SimpleText?,
  hidden: Boolean?,
  @JacksonXmlElementWrapper(useWrapping = false) @JsonProperty("answer") val answers: List<Answer>,
  @JsonSerialize(using = NumericBooleanSerializer::class) val single: Boolean,
  @JsonProperty("synchronize") val synchronizeWildcards: Calculated.SynchronizeWildcards,
  @JsonUnwrapped val combinedFeedback: CombinedFeedback,
  @JsonUnwrapped val multipleTries: MultipleTries,
  @JacksonXmlElementWrapper(localName = "dataset_definitions") @JsonProperty("dataset_definition") val datasets: List<Calculated.Dataset>
): Question(name, question, defaultGrade, tags, generalFeedback, hidden) {


  /**
   * An answer to a calculated multiple-choice question.
   * @property fraction the fraction of the total points that the answer is worth
   * @property tolerance the tolerance of the answer
   * @property toleranceType the type of the tolerance
   * @property correctAnswerLength the length of the correct answer
   * @property correctAnswerFormat the format of the correct answer
   * @see Text
   */
  class Answer(
    text: String,
    files: List<File>,
    format: Text.Format,
    @JacksonXmlProperty(isAttribute = true) val fraction: Fraction,
    val tolerance: Double,
    @JsonProperty("tolerancetype") val toleranceType: Calculated.Answer.ToleranceType,
    @JsonProperty("correctanswerlength") val correctAnswerLength: Int,
    @JsonProperty("correctanswerformat") val correctAnswerFormat: Calculated.Answer.AnswerFormat,
  ) : Text(text, files, format)

}