package com.zkerriga

import com.zkerriga.id.endpoints.runners.{EndpointRunner, ErrorHandler}
import com.zkerriga.id.services.registration.RegistrationService

package object id {
  type LogicServices           = RegistrationService
  type InfrastructuralServices = ErrorHandler & EndpointRunner

  type AllServices = LogicServices & InfrastructuralServices
}
