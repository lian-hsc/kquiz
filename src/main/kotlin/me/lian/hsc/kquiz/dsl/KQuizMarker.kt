package me.lian.hsc.kquiz.dsl

/**
 * The marker for the kquiz DSL, preventing implicit access to outer receivers from nested builder blocks.
 */
@DslMarker
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class KQuizMarker
