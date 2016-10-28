package repository

import play.api.db.Databases
import play.api.db.evolutions.Evolutions

trait DatabaseEvolution {
  val database = Databases(
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:caradverttest"
  )

  def runEvolution(): Unit = {
    Evolutions.applyEvolutions(database)

    database.withConnection(connection => {
      val stmt = connection.createStatement()
      stmt.execute("TRUNCATE TABLE caradvert")
    })
  }


}
