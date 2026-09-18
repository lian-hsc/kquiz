package me.lian.hsc.kquiz.dsl

/**
 * How many of the points of an embedded Cloze sub-question an option is worth, as a percentage
 * from `-100.0` (full negative marking) to `100.0` (fully correct).
 *
 * This is Cloze-specific: unlike every other question type, whose answers grade with the discrete
 * set of point fractions in [me.lian.hsc.kquiz.data.Fraction], Cloze's `{weight:TYPE:...}` syntax embeds
 * an arbitrary percentage directly, so there is no existing enum for this DSL to reuse instead.
 */
sealed interface Score {

  val percentage: Double

  object Correct : Score {
    override val percentage = 100.0
  }

  object Wrong : Score {
    override val percentage = 0.0
  }

  /**
   * @param fraction the fraction of the points, from `-1.0` to `1.0`
   */
  class Percentage(fraction: Double) : Score {
    init {
      require(fraction in -1.0..1.0) { "fraction must be between -1.0 and 1.0, was $fraction" }
    }

    override val percentage = fraction * 100
  }

}
