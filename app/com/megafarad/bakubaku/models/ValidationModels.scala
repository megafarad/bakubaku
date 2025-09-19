package com.megafarad.bakubaku.models

import play.api.libs.json.*

case class XsdInput(name: String, content: String)
case class ValidateOptions(maxErrors: Int = 100, useSecureProcessing: Boolean = true, namespaceAware: Boolean = true)
case class ValidateRequest(xml: String, xsds: Seq[XsdInput], options: Option[ValidateOptions])

enum ValidationIssueSeverity:
  case Error, Warning, Fatal

object ValidationIssueSeverity:
  given severityFormat: Format[ValidationIssueSeverity] = Format(
    Reads {
      case JsString("error") => JsSuccess(Error)
      case JsString("warning") => JsSuccess(Warning)
      case JsString("fatal") => JsSuccess(Fatal)
      case _ => JsError("Invalid severity")
    },
    Writes {
      case Error => JsString("error")
      case Warning => JsString("warning")
      case Fatal => JsString("fatal")
    }
  )

case class ValidationIssue(severity: ValidationIssueSeverity,
                           message: String,
                           line: Option[Int],
                           column: Option[Int],
                           systemId: Option[String])

case class ValidateResponse(valid: Boolean, issues: Seq[ValidationIssue], stats: Map[String, Long])