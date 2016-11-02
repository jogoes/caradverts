package repository.jooq

import repository.{AbstractCarAdvertRepositorySpec, DatabaseEvolution}

class JooqCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec with DatabaseEvolution {

  override protected def beforeEach(): Unit = {
    runEvolution()
    super.beforeEach()
  }

  val repositoryFactory = () => new JooqCarAdvertRepository(database)

}
