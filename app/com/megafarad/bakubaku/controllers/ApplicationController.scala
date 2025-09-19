package com.megafarad.bakubaku.controllers

import controllers.Assets
import play.api.mvc.{Action, AnyContent}

import javax.inject.Inject

class ApplicationController @Inject()(assets: Assets) {

  def index: Action[AnyContent] = assets.at("index.html")

  def assetOrDefault(resource: String): Action[AnyContent] = {
    if (resource.contains(".")) {
      assets.at(resource)
    } else {
      index
    }
  }
}
