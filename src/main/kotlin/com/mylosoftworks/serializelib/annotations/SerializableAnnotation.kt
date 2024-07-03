package com.mylosoftworks.serializelib.annotations

/**
 * An attribute which specifies that the targeted class is serializable using SerializeLib.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerializeClass

/**
 * An attribute which specifies that the targeted field or property is serializable using SerializeLib.
 * @param order The index of this entry (since order won't match, unlike C#)
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class SerializeField(val order: Int)