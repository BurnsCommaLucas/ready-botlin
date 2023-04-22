package io.burnscommalucas.readybotlin

fun plural(count: Number, pluralizer: String = "s"): String = if (count.toLong() > 1) pluralizer else ""