package repository.jdbc

import repository.{AbstractCarAdvertRepositorySpec, DatabaseEvolution}

class JdbcCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec with DatabaseEvolution {

  override protected def beforeEach(): Unit = {
    runEvolution()
    super.beforeEach()
  }

  val repositoryFactory = () => new JdbcCarAdvertRepository(database)
}
