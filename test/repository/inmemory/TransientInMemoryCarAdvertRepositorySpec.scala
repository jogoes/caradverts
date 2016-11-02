package repository.inmemory

import repository.AbstractCarAdvertRepositorySpec

class TransientInMemoryCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec {

  val repositoryFactory = () => new TransientInMemoryCarAdvertRepository
}
