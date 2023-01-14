package misis.paymentSystem.route

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import misis.paymentSystem.model._
import misis.paymentSystem.repository._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class AccountRoute (repository: AccountRepository)(implicit val ec: ExecutionContext) extends FailFastCirceSupport {
  def route =
    (path("accounts") & get) {
      val list = repository.list()
      complete(list)
    } ~
      path("account") {
        (post & entity(as[CreateAccount])) { newAcc =>
          complete(repository.createAccount(newAcc))
        }
      } ~
      path("account" / JavaUUID) { id =>
        get {
          onComplete(repository.get(id)) {
            case Success(value) => complete(value)
            case Failure(e: NonAccount) => complete(StatusCodes.NotFound, "Такой счет не найден.")
          }
        }
      } ~
      path("person" / Segment) { email =>
        get {
          onComplete(repository.getAccount(GetAccount(email))) {
            case Success(value) => complete(value)
            case Failure(e: NonAccount) => complete(StatusCodes.NotFound, "Такой счет не найден.")
          }
        }
      } ~
      path("account" / "refill") {
        (put & entity(as[RefillAccount])) { acc =>
          onComplete(repository.refillAccount(acc)) {
            case Success(value) => complete(value)
            case Failure(e) => complete(StatusCodes.NotFound, "Такой счет не найден.")
          }
        }
      } ~
      path("account" / "withdrawal") {
        (put & entity(as[WithdrawalMoney])) { acc =>
          onComplete(repository.withdrawalMoney(acc)) {
            case Success(value) => complete(value)
            case Failure(e: TooMuchWithdrawalAmount) => complete(StatusCodes.NotAcceptable, "Операция отклонена из-за недостатка средств на счету.")
            case Failure(e: NonAccount) => complete(StatusCodes.NotFound, "Такой счет не найден.")
          }
        }
      } ~
      path("account" / "change") {
        (put & entity(as[ChangeCash])) { acc =>
          onComplete(repository.changeCash(acc)) {
            case Success(value) => complete(value)
            case Failure(e: TooMuchWithdrawalAmount) => complete(StatusCodes.NotAcceptable, "Операция отклонена из-за недостатка средств на счету.")
            case Failure(e: NonAccount) => complete(StatusCodes.NotFound, "Такой счет не найден.")
          }
        }
      }
}
