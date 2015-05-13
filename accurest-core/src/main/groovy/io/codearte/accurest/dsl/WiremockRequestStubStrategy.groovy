package io.codearte.accurest.dsl
import groovy.transform.PackageScope
import groovy.transform.TypeChecked
import io.codearte.accurest.dsl.internal.ClientRequest
import io.codearte.accurest.dsl.internal.Request
import io.codearte.accurest.util.JsonConverter

import java.util.regex.Pattern

@TypeChecked
@PackageScope
class WiremockRequestStubStrategy extends BaseWiremockStubStrategy {

	private final Request request

	WiremockRequestStubStrategy(GroovyDsl groovyDsl) {
		this.request = groovyDsl.request
	}

	@PackageScope
	Map buildClientRequestContent() {
		return buildRequestContent(new ClientRequest(request))
	}

	private Map<String, Object> buildRequestContent(ClientRequest request) {
		return ([method    : request?.method?.clientValue,
		        headers   : buildClientRequestHeadersSection(request.headers)
		] << appendUrl(request) << appendBody(request)).findAll { it.value }
	}

	private Map<String, Object> appendUrl(ClientRequest clientRequest) {
		Object url = clientRequest?.url?.clientValue
		return url instanceof Pattern ? [urlPattern: ((Pattern)url).pattern()] : [url: url]
	}

	private Map<String, Object> appendBody(ClientRequest clientRequest) {
		Object body = clientRequest?.body?.clientValue
		if (body == null) {
			return [:]
		}
		if (containsRegex(body)) {
			return [bodyPatterns: [[matches: parseBody(JsonConverter.transformValues(body, {
				it instanceof Pattern ? it.toString() : it
			}))]]]
		}
		return [bodyPatterns: [[equalTo: parseBody(body)]]]
	}

	boolean containsRegex(Object bodyObject) {
		String bodyString = bodyObject as String
		return (bodyString =~ /\^.*\$/).find()
	}

	boolean containsRegex(Map map) {
		return map.values().any { it instanceof Pattern }
	}

}
