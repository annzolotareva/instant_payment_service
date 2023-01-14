package misis.paymentSystem.db

import misis.paymentSystem.model._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag
import java.util.UUID

object AccountDb {
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts") {
    val id = column[UUID]("id", O.PrimaryKey)
    val owner = column[String]("owner")
    val email = column[String]("email")
    val count = column[Int]("count")

    def * = (id, owner, email, count) <> ((Account.apply _).tupled, Account.unapply _)
  }

  val accountTable = TableQuery[AccountTable]
}
