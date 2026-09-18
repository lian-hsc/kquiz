package me.lian.hsc.kquiz.dsl

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A property that must be set exactly once before it is read.
 * Reading it before it is set, or writing it more than once, fails immediately, naming [label] (the
 * property's own name by default; pass [label] explicitly when a backing property's name would leak
 * an implementation detail instead of the DSL function the caller actually used).
 */
internal class Required<T : Any>(private val label: String? = null) : ReadWriteProperty<Any?, T> {

  private var value: T? = null

  override fun getValue(thisRef: Any?, property: KProperty<*>): T =
    checkNotNull(value) { "${label ?: property.name} is not set" }

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    check(this.value == null) { "${label ?: property.name} is already set" }
    this.value = value
  }

}
