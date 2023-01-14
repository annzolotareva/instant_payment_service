package misis.paymentSystem.repository

import misis.paymentSystem.model._

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryInMemory(implicit val ec: ExecutionContext) extends AccountRepository {
  private val bank = mutable.Map[UUID, Account]()

  override def list(): Future[Seq[Account]] = Future {
    bank.values.toList
  }

  override def createAccount(acc: CreateAccount): Future[Account] = Future {
    val account = Account(id = UUID.randomUUID(), owner = acc.owner, email = acc.email, count = acc.count)
    bank.put(account.id, account)
    account
  }


  override def get(acc: UUID): Future[Account] = Future {
    bank(acc)
  }

  override def getAccount(acc: GetAccount): Future[Seq[UUID]] = {
    val l = list().map(accounts => accounts.filter(account => account.email == acc.email))
    l.map(accounts => accounts.map(_.id))
  }

  override def refillAccount(acc: RefillAccount): Future[Account] = Future {
    val add = bank.get(acc.id).map { account =>
      val new_count = account.count + acc.amount
      val updated = account.copy(count = new_count)
      bank.put(account.id, updated)
      account
    }
    add.get
  }

  override def withdrawalMoney(acc: WithdrawalMoney): Future[Account] = Future {
    val sub = bank.get(acc.id).map { account =>
      val new_count = account.count - acc.amount
      val updated = account.copy(count = new_count)
      bank.put(account.id, updated)
      account
    }
    sub.get
  }

  override def changeCash(operation: ChangeCash): Future[Int] = Future {
    bank.get(operation.from).map { account =>
      val from_count = account.count - operation.amount
      val updated = account.copy(count = from_count)
      bank.put(account.id, updated)
    }
    bank.get(operation.to).map { account =>
      val to_count = account.count + operation.amount
      val updated = account.copy(count = to_count)
      bank.put(account.id, updated)
    }
    operation.amount
  }
}
