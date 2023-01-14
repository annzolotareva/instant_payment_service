package misis.paymentSystem

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.paymentSystem.repository.AccountRepositoryInMemory
import misis.paymentSystem.route.{AccountRoute, HelloRoute}

object PaymentsMemoryApp extends App with FailFastCirceSupport {

  implicit val system: ActorSystem = ActorSystem("PayApp")
  implicit val ec = system.dispatcher
  val repository = new AccountRepositoryInMemory
  val helloRoute = new HelloRoute().route
  val accRoute = new AccountRoute(repository).route

  Http().newServerAt("0.0.0.0", 8080).bind(helloRoute ~ accRoute)
}
