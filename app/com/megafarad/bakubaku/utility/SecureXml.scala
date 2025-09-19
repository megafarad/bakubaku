package com.megafarad.bakubaku.utility

import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import javax.xml.validation.SchemaFactory

object SecureXml {
  def secureSaxFactory(namespaceAware: Boolean): SAXParserFactory = {
    val factory = SAXParserFactory.newInstance()
    factory.setNamespaceAware(namespaceAware)
    factory.setValidating(false)
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory
  }

  def secureSchemaFactory(): SchemaFactory = {
    val factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
    factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "")
    factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
    factory
  }

}
