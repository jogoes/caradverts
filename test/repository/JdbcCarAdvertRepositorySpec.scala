package repository

import play.api.db.Databases
import play.api.db.evolutions._


class JdbcCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec {

  val database = Databases(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:play"
  )

  override protected def beforeEach(): Unit = {
    Evolutions.applyEvolutions(database)

    database.withConnection(connection => {
      val stmt = connection.createStatement()
      stmt.execute("TRUNCATE TABLE caradvert")
    })

    super.beforeEach()
  }

  val repositoryFactory = () => new JdbcCarAdvertRepository(database)

}
