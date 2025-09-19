package com.megafarad.bakubaku.controllers

import com.megafarad.bakubaku.models.*
import com.megafarad.bakubaku.services.XmlValidationService
import play.api.mvc.*
import play.api.libs.json.*

import javax.inject.Inject

class ValidateController @Inject() (cc: ControllerComponents, xmlValidationService: XmlValidationService) extends AbstractController(cc) {

  implicit val xsdReads: Reads[XsdInput] = Json.reads[XsdInput]
  implicit val optionsReads: Reads[ValidateOptions] = Json.reads[ValidateOptions]
  implicit val validateRequestReads: Reads[ValidateRequest] = Json.reads[ValidateRequest]

  implicit val issueWrites: OWrites[ValidationIssue] = Json.writes[ValidationIssue]
  implicit val responseWrites: OWrites[ValidateResponse] = Json.writes[ValidateResponse]

  def validate: Action[JsValue] = Action(parse.json(maxLength = 2 * 1024 * 1024)) { request =>
    request.body.validate[ValidateRequest].fold(
      errors => BadRequest(Json.obj("error" -> "Invalid payload", "details" -> JsError.toJson(errors))),
      payload => {
        if (payload.xml.length > 1_000_000) {
          BadRequest(Json.obj("error" -> "XML too large"))
        } else if (payload.xsds.length > 8) {
          BadRequest(Json.obj("error" -> "Too many XSDs"))
        } else {
          val response = xmlValidationService.validate(payload)
          Ok(Json.toJson(response))
        }
      }
    )
  }

}
