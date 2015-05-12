package io.codearte.accurest.builder

import io.codearte.accurest.dsl.GroovyDsl
import spock.lang.Specification

/**
 * @author Jakub Kubrynski
 */
class SpockMethodBuilderSpec extends Specification {

	def "should generate assertions for simple response body"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body """{
    "property1": "a",
    "property2": "b"
}"""
				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody.property1 == \"a\"")
			blockBuilder.toString().contains("responseBody.property2 == \"b\"")
	}

	def "should generate assertions for array in response body"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body """[
{
    "property1": "a"
},
{
    "property2": "b"
}]"""
				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody[0].property1 == \"a\"")
			blockBuilder.toString().contains("responseBody[1].property2 == \"b\"")
	}

	def "should generate assertions for array inside response body element"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body """{
    "property1": [
    { "property2": "test1"},
    { "property3": "test2"}
    ]
}"""
				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody.property1[0].property2 == \"test1\"")
			blockBuilder.toString().contains("responseBody.property1[1].property3 == \"test2\"")
	}

	def "should generate assertions for nested objects in response body"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body '''\
{
    "property1": "a",
    "property2": {"property3": "b"}
}
'''
				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody.property1 == \"a\"")
			blockBuilder.toString().contains("responseBody.property2.property3 == \"b\"")
	}

	def "should generate regex assertions for map objects in response body"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body(
							property1: "a",
							property2: value(
									client('123'),
									server(regex('[0-9]{3}'))
							)
					)
					headers {
						header('Content-Type': 'application/json')

					}

				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody.property1 == \"a\"")
			blockBuilder.toString().contains("responseBody.property2 ==~ java.util.regex.Pattern.compile('[0-9]{3}')")
	}

	def "should generate regex assertions for string objects in response body"() {
		given:
			GroovyDsl contractDsl = GroovyDsl.make {
				request {
					method "GET"
					url "test"
				}
				response {
					status 200
					body( """{"property1":"a","property2":"${value(client('123'), server(regex('[0-9]{3}')))}"}""")
					headers {
						header('Content-Type': 'application/json')

					}

				}
			}
			SpockMethodBodyBuilder builder = new SpockMethodBodyBuilder(contractDsl)
			BlockBuilder blockBuilder = new BlockBuilder(" ")
		when:
			builder.appendTo(blockBuilder)
		then:
			blockBuilder.toString().contains("responseBody.property1 == \"a\"")
			blockBuilder.toString().contains("responseBody.property2 ==~ java.util.regex.Pattern.compile('[0-9]{3}')")
	}

}
