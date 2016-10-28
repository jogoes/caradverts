package repository.jooq

import java.util.UUID

import org.jooq.Converter

class UUIDConverter extends Converter[String, UUID] {

  override def toType: Class[UUID] = classOf[UUID]

  override def from(s: String): UUID = UUID.fromString(s)

  override def to(uuid: UUID): String = uuid.toString

  override def fromType(): Class[String] = classOf[String]
}
