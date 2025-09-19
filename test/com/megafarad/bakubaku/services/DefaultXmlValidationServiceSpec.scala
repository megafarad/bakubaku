package com.megafarad.bakubaku.services

import com.megafarad.bakubaku.models.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.io.Source
import scala.util.{Failure, Success, Try}

class DefaultXmlValidationServiceSpec extends AnyWordSpec with Matchers {

  private val service: XmlValidationService = new DefaultXmlValidationService()

  // Simple schema: <note><to/><from/></note>
  private val simpleXsd: String =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
      |           elementFormDefault="qualified">
      |  <xs:element name="note">
      |    <xs:complexType>
      |      <xs:sequence>
      |        <xs:element name="to"   type="xs:string"/>
      |        <xs:element name="from" type="xs:string"/>
      |      </xs:sequence>
      |    </xs:complexType>
      |  </xs:element>
      |</xs:schema>
      |""".stripMargin

  private val validXml: String =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<note>
      |  <to>Ada</to>
      |  <from>Bob</from>
      |</note>
      |""".stripMargin

  // Missing <from> to trigger a schema error at a predictable line/column.
  private val invalidXml: String =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<note>
      |  <to>Ada</to>
      |</note>
      |""".stripMargin

  // XXE / DOCTYPE should be blocked by SecureXml settings.
  private val xxeXml: String =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE note [
      |  <!ENTITY xxe SYSTEM "file:///etc/hosts">
      |]>
      |<note>
      |  <to>&xxe;</to>
      |  <from>Bob</from>
      |</note>
      |""".stripMargin

  private def req(xml: String, xsd: String): ValidateRequest = {
    ValidateRequest(
      xml = xml,
      xsds = Seq(XsdInput(name = "simple.xsd", content = xsd)),
      options = Some(ValidateOptions(maxErrors = 100, useSecureProcessing = true, namespaceAware = true))
    )

  }

  "DefaultXmlValidationService" should {
    "return valid=true and no issues for a conforming document" in {
      val res = service.validate(req(validXml, simpleXsd))
      res.valid shouldBe true
      res.issues shouldBe empty
      // sanity check on stats
      res.stats.get("totalMs") should not be empty
    }

    "return valid=false with at least one error for a non-conforming document and include line/column when available" in {
      val res = service.validate(req(invalidXml, simpleXsd))
      res.valid shouldBe false
      res.issues should not be empty

      val first = res.issues.head
      first.severity should (be(ValidationIssueSeverity.Error) or be(ValidationIssueSeverity.Fatal))
      // We expect a useful position (line numbers can vary by parser, but should be > 0)
      first.line.foreach(_ should be > 0)
      first.column.foreach(_ should be > 0)
      // The message should mention the content model violation
      first.message.toLowerCase should include regex "cvc|content|expected"
    }

    "reject a document with DOCTYPE/XXE (secure defaults) either by throwing or by emitting a fatal error" in {
      // Depending on parser behavior, a disallowed DOCTYPE may throw before the handler runs.
      Try(service.validate(req(xxeXml, simpleXsd))) match {
        case Failure(e) =>
          // Acceptable: secure parser threw on DOCTYPE
          e.getClass.getName should include("SAX") // e.g., SAXParseException
        case Success(res) =>
          // Also acceptable: the service trapped it as a validation issue
          res.valid shouldBe false
          res.issues.exists(_.message.toLowerCase.contains("doctype")) shouldBe true
      }
    }

    "validate the patients file" in {
      val schemaResource = Source.fromResource("patients_schema.xsd")
      val schema = schemaResource.mkString
      val patientsResource = Source.fromResource("patients.xml")
      val patients = patientsResource.mkString

      val res = service.validate(req(patients, schema))
      res.valid shouldBe true
    }

    "validate the first patient list" in {
      val schemaResource = Source.fromResource("patients_schema.xsd")
      val schema = schemaResource.mkString
      val patientListResource = Source.fromResource("patient_list_001.xml")
      val patientList = patientListResource.mkString

      val res = service.validate(req(patientList, schema))
      res.valid shouldBe true
    }

    "validate the second patient list" in {
      val schemaResource = Source.fromResource("patients_schema.xsd")
      val schema = schemaResource.mkString
      val patientListResource = Source.fromResource("patient_list_002.xml")
      val patientList = patientListResource.mkString
      val res = service.validate(req(patientList, schema))
      res.valid shouldBe false
    }

  }
}


