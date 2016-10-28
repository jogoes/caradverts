package repository

import repository.jooq.JooqCarAdvertRepository

class JooqCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec with DatabaseEvolution {

  override protected def beforeEach(): Unit = {
    runEvolution()
    super.beforeEach()
  }

  val repositoryFactory = () => new JooqCarAdvertRepository(database)

}
