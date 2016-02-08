package io.codearte.accurest.wiremock

import groovy.transform.CompileStatic
import io.codearte.accurest.file.Contract

@CompileStatic
interface SingleFileConverter {

	boolean canHandleFileName(String fileName)

	String convertContent(String rootName, Contract content)

	String generateOutputFileNameForInput(String inputFileName)
}