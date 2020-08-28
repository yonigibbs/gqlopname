package com.yg.gqlopname

import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.DirtiesContext

private const val REQUEST = "{ foos { bar } }"

private const val OPERATION_NAME = "MyOperationName"

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OperationNameTest(@Autowired private val restTemplate: TestRestTemplate) {

    @LocalServerPort
    private var port = 0

    @Test
    @DirtiesContext // Force Spring to restart to clear out old Actuator data
    fun `operation name available when operationName JSON field included`()  = runTest(true)

    @Test
    @DirtiesContext // Force Spring to restart to clear out old Actuator data
    fun `operation name available when operationName JSON field excluded`()  = runTest(false)

    private fun runTest(includeOperationNameJsonField: Boolean) {
        // Call the GraphQL endpoint to get some Actuator data created.
        execGraphQLQuery(includeOperationNameJsonField)

        // Call the Actuator endpoint and read all the tags for `operationName`: we expect these to contain
        // OPERATION_NAME (i.e. "MyOperationName"), regardless of whether the operationName JSON field was included.
        val response = restTemplate.getForEntity(
            "http://localhost:$port/actuator/metrics/graphql.timer.query", ObjectNode::class.java
        )
        val values =
            response.body!!["availableTags"]
                .first { (it["tag"] as TextNode).textValue() == "operationName" }
                .get("values") as ArrayNode
        assertThat(values.elements().asSequence().map { it.textValue() }.toList()).contains(OPERATION_NAME)
    }

    private fun execGraphQLQuery(includeOperationNameJsonField: Boolean) {
        val query =
            if (includeOperationNameJsonField)
                """{"operationName": "$OPERATION_NAME", "query": "query $OPERATION_NAME $REQUEST"}"""
            else
                """{"query": "query $OPERATION_NAME $REQUEST"}"""
        val response = restTemplate.postForEntity("http://localhost:$port/graphql", query, String::class.java)
        assertThat(response.statusCode.is2xxSuccessful).isTrue()
    }
}
