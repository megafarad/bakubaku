package com.megafarad.bakubaku.services

import com.megafarad.bakubaku.models.*

trait XmlValidationService {
  def validate(request: ValidateRequest): ValidateResponse
}
