package com.megafarad.bakubaku.services

import com.megafarad.bakubaku.models.ValidationIssueSeverity.Fatal
import com.megafarad.bakubaku.models.{ValidateRequest, ValidateResponse, ValidationIssue, ValidationIssueSeverity}
import com.megafarad.bakubaku.utility.SecureXml
import org.xml.sax.{ErrorHandler, InputSource, SAXParseException}

import javax.xml.transform.stream.StreamSource
import javax.xml.transform.Source
import java.io.StringReader
import javax.inject.{Inject, Singleton}
import scala.collection.mutable

@Singleton
class DefaultXmlValidationService @Inject()() extends XmlValidationService {
  override def validate(request: ValidateRequest): ValidateResponse = {
    val started = System.nanoTime()

    val schemaFactory = SecureXml.secureSchemaFactory()
    val xsdSources: Seq[Source] = request.xsds.map(x => new StreamSource(new StringReader(x.content), x.name))
    val schema = schemaFactory.newSchema(xsdSources.toArray)

    val saxParserFactory = SecureXml.secureSaxFactory(request.options.forall(_.namespaceAware))
    val parser = saxParserFactory.newSAXParser()
    val reader = parser.getXMLReader
    val validationHandler = schema.newValidatorHandler()

    val issues = mutable.Buffer[ValidationIssue]()

    validationHandler.setErrorHandler(new ErrorHandler {
      def toIssue(severity: ValidationIssueSeverity, exception: SAXParseException) = ValidationIssue(
        severity = severity, 
        message = Option(exception.getMessage).getOrElse(""), 
        line = Option(exception.getLineNumber).filter(_ > 0), 
        column = Option(exception.getColumnNumber).filter(_ > 0), 
        systemId = Option(exception.getSystemId)
      )
      
      def warning(exception: SAXParseException): Unit = issues += toIssue(ValidationIssueSeverity.Warning, exception)

      def error(exception: SAXParseException): Unit = issues += toIssue(ValidationIssueSeverity.Error, exception)

      def fatalError(exception: SAXParseException): Unit = issues += toIssue(Fatal, exception)
    })

    reader.setContentHandler(validationHandler)
    val parseStart = System.nanoTime()
    reader.parse(new InputSource(new StringReader(request.xml)))
    val parseEnd = System.nanoTime()

    val valid = !issues.exists(_.severity == ValidationIssueSeverity.Error || issues.exists(_.severity == Fatal))
    val totalMs = (System.nanoTime() - started) / 1_000_000
    val stats: Map[String, Long] = Map(
      "parseMs" -> (parseEnd - parseStart) / 1_000_000,
      "totalMs" -> totalMs,
      "xmlBytes" -> request.xml.getBytes("UTF-8").length.toLong,
      "xsdBytes" -> request.xsds.map(_.content.getBytes("UTF-8").length.toLong).sum.toLong
    )

    ValidateResponse(valid, issues.toSeq, stats)
  }
}
