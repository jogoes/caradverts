package repository

import repository.inmemory.TransientInMemoryCarAdvertRepository

class TransientInMemoryCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec {

  val repositoryFactory = () => new TransientInMemoryCarAdvertRepository
}
