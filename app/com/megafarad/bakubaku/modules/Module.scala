package com.megafarad.bakubaku.modules

import com.google.inject.{AbstractModule, Scopes}
import com.megafarad.bakubaku.services.*

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[XmlValidationService])
      .to(classOf[DefaultXmlValidationService])
      .in(Scopes.SINGLETON)
  }
}
