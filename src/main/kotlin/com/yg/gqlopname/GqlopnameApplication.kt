package com.yg.gqlopname

import graphql.kickstart.tools.GraphQLQueryResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class GqlopnameApplication

fun main(args: Array<String>) {
    runApplication<GqlopnameApplication>(*args)
}

@Component
class GraphQLQueries : GraphQLQueryResolver {
    fun foos() = listOf(Foo("foo-1"), Foo("foo-2"))
}

data class Foo(val bar: String)
