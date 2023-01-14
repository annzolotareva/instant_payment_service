package misis.paymentSystem.db

import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.PostgresProfile.api._
import misis.paymentSystem.db.AccountDb._

class InitDb(implicit val ec: ExecutionContext, db: Database) {
  def prepare(): Future[_] = {
    db.run(accountTable.schema.createIfNotExists)
  }
}
