package me.lian.hsc.kquiz.dsl

import me.lian.hsc.kquiz.data.Units

/**
 * DSL for [Units], shared by [me.lian.hsc.kquiz.data.Numerical] and [me.lian.hsc.kquiz.data.Calculated]-based questions.
 */
@KQuizMarker
class UnitsDsl {

  var handling: Units.UnitHandling = Units.UnitHandling.UnusedOrOptional
  var penalty: Double? = null
  var displayMode: Units.UnitDisplayMode = Units.UnitDisplayMode.TextInput
  var location: Units.UnitLocation = Units.UnitLocation.Right

  private val units = mutableListOf<Units.UnitEntry>()

  /**
   * Adds a unit; the first unit added should have a [multiplier] of `1.0`.
   */
  fun unit(name: String, multiplier: Double = 1.0) {
    units += Units.UnitEntry(name, multiplier)
  }

  internal fun build() = Units(handling, penalty, displayMode, location, units.toList())

}
