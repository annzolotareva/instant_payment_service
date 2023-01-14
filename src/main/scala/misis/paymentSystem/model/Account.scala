package misis.paymentSystem.model

import java.util.UUID

case class Account(id: UUID = UUID.randomUUID(), owner: String, email: String, count: Int)

case class CreateAccount(owner: String, email: String, count: Int)
case class GetAccount(email: String)
case class RefillAccount(id: UUID, amount: Int)
case class WithdrawalMoney(id: UUID, amount: Int)
case class ChangeCash(from: UUID, to: UUID, amount: Int)
