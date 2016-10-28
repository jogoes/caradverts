package repository.jooq

import java.sql.Date
import java.time.LocalDate

import org.jooq.Converter

class SqlDateToLocalDateConverter extends Converter[Date, LocalDate] {

  override def toType: Class[LocalDate] = classOf[LocalDate]

  override def from(d: Date): LocalDate = if (d == null) null else d.toLocalDate

  override def to(d: LocalDate): Date = if(d == null) null else Date.valueOf(d)

  override def fromType(): Class[Date] = classOf[Date]
}