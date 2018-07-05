import java.util.UUID

import cats.data.NonEmptyList
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import com.example.JsonProcessor
import com.example.validation.ValidationError

case class Data(uuid: UUID, title: String, version: Int)

object Main extends App {
  import spray.json._

  val json =
    """
      |{
      | "uuid": "4a1eb5a3-447e-4143-a00d-bba7d926fd3f",
      | "titleX": "Some title",
      | "version": "foo"
      |}
    """.stripMargin
  val obj = json.parseJson.asJsObject

  val result =
    (
      JsonProcessor.readUuid(obj, "uuid"),
      JsonProcessor.readString(obj, "title"),
      JsonProcessor.readInt(obj, "version")
    ).mapN(Data)

  result match {
    case Valid(data) => println(s"Data: $data")
    case Invalid(nel: NonEmptyList[ValidationError]) =>
      println("Parsing errors:")
      nel.toList.foreach(error => println(s" - $error"))
  }
}
