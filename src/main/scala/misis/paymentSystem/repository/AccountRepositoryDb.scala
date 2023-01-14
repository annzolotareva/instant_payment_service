package misis.paymentSystem.repository

import misis.paymentSystem.db.AccountDb._
import misis.paymentSystem.model.{Account, ChangeCash, CreateAccount, GetAccount, RefillAccount, WithdrawalMoney}
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends AccountRepository {
  override def list(): Future[Seq[Account]] = {
    db.run(accountTable.result)
  }

  override def createAccount(acc: CreateAccount): Future[Account] = {
    val a = Account(owner = acc.owner, email = acc.email, count = acc.count)
    for {
      _ <- db.run(accountTable += a)
      res <- get(a.id)
    } yield res
  }

  override def get(acc: UUID): Future[Account] = {
    val query = accountTable.filter(_.id === acc)
    db.run(query.result.head)
  }

  def find(acc: UUID): Future[Option[Account]] = {
    db.run(accountTable.filter(_.id === acc).result.headOption)
  }

  override def getAccount(acc: GetAccount): Future[Seq[UUID]] = {
    val query = accountTable.filter(_.email === acc.email)
    db.run(query.map(a => a.id).result).map { a =>
      if (a.nonEmpty) a
      else throw new NonAccount
    }
  }

  override def refillAccount(acc: RefillAccount): Future[Account] = {
    db.run(accountTable.filter(_.id === acc.id).map(a => a.id).result).flatMap { a =>
      if (a.nonEmpty) {
        get(acc.id).map { account =>
          val new_count = account.count + acc.amount
          db.run(accountTable.filter(_.id === acc.id).map(_.count).update(new_count))
          account
        }
      }
      else throw new NonAccount
    }
  }

  override def withdrawalMoney(acc: WithdrawalMoney): Future[Account] = {
    db.run(accountTable.filter(_.id === acc.id).map(a => a.id).result).flatMap { a =>
      if (a.nonEmpty) {
        get(acc.id).map { ac =>
          val new_count = ac.count - acc.amount
          if (new_count > 0) {
            db.run(accountTable.filter(_.id === acc.id).map(_.count).update(new_count))
            ac
          }
          else
            throw new TooMuchWithdrawalAmount
        }
      }
      else throw new NonAccount
    }
  }

  override def changeCash(acc: ChangeCash): Future[Int] = {
    db.run(accountTable.filter(_.id === acc.from).map(_.id).result).map { ac =>
      if (ac.nonEmpty) ac
      else throw new NonAccount
    }
    db.run(accountTable.filter(_.id === acc.to).map(_.id).result).map { ac =>
      if (ac.nonEmpty) ac
      else throw new NonAccount
    }
    get(acc.from).map { account =>
      val new_count = account.count - acc.amount
      if (new_count >= 0) new_count
      else throw new TooMuchWithdrawalAmount
    }
    get(acc.from).map { account =>
      val new_count = account.count - acc.amount
      for {
        _ <- db.run(accountTable.filter(_.id === acc.from).map(_.count).update(new_count))
        res <- find(acc.from)
      } yield res
    }
    get(acc.to).flatMap { account =>
      val new_count = account.count + acc.amount
      db.run(accountTable.filter(_.id === acc.to).map(_.count).update(new_count))
    }
  }
}

case class NonAccount() extends Exception
case class TooMuchWithdrawalAmount() extends Exception