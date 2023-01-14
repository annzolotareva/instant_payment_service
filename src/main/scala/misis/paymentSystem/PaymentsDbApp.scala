package misis.paymentSystem

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import misis.paymentSystem.db.InitDb
import misis.paymentSystem.repository.AccountRepositoryDb
import misis.paymentSystem.route.{AccountRoute, HelloRoute}
import slick.jdbc.PostgresProfile.api._

object PaymentsDbApp extends App with FailFastCirceSupport {
  implicit val system: ActorSystem = ActorSystem("PayApp")
  implicit val ec = system.dispatcher
  implicit val db = Database.forConfig("database.postgres")


  new InitDb().prepare()
  val repository = new AccountRepositoryDb

  val helloRoute = new HelloRoute().route
  val accRoute = new AccountRoute(repository).route

  Http().newServerAt("0.0.0.0", 8080).bind(helloRoute ~ accRoute)
}
