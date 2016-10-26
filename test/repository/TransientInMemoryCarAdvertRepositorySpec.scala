package repository

class TransientInMemoryCarAdvertRepositorySpec extends AbstractCarAdvertRepositorySpec {

  val repositoryFactory = () => new TransientInMemoryCarAdvertRepository
}
