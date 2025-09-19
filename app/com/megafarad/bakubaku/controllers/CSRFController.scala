package com.megafarad.bakubaku.controllers

import play.api.mvc.*
import play.api.mvc.Results.*
import play.filters.csrf.*
import play.filters.csrf.CSRF.Token

import javax.inject.Inject

class CSRFController @Inject()(cc: ControllerComponents, addToken: CSRFAddToken, checkToken: CSRFCheck) extends AbstractController(cc) {
  def getToken: Action[AnyContent] = addToken(Action { implicit request =>
    val Token(name, value) = CSRF.getToken.get
    Ok(s"$name=$value")
  })
}
