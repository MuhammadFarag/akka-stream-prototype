package com.mfarag.learn.akka.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Framing, Sink, Source}
import akka.testkit.TestKit
import akka.util.ByteString
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.Future

class HelloStreamTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike with StopSystemAfterAll with Matchers with ScalaFutures {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  test("A Simple flow with a default Sink: Sink.seq") {
    val source: Source[Int, NotUsed] = Source.repeat(1)

    val materializedResult: Future[Seq[Int]] = source.take(10).runWith(Sink.seq)
    whenReady(materializedResult) { result =>
      result shouldBe Seq.fill(10)(1)
    }
  }

  test("framing an incoming ByteString into its components") {
    val split: Flow[ByteString, String, NotUsed] = Framing
      .delimiter(ByteString(","), 3, allowTruncation = true)
      .map(_.decodeString("UTF8"))

    val sourceString = ByteString("h,e,l,l,o")
    val source: Source[ByteString, NotUsed] = Source.single(sourceString)
    val materializedResult: Future[Seq[String]] = source.via(split).runWith(Sink.seq)
    whenReady(materializedResult) { result =>
      result shouldBe Seq("h", "e", "l", "l", "o")
    }


  }

  test("Framing an incoming ByteString around new lines") {
    val sourceString = ByteString(
      """Hello
        |World!""".stripMargin)

    val splitLines: Flow[ByteString, String, NotUsed] = Framing
      .delimiter(ByteString("\n"), 20, allowTruncation = true)
      .map(_.decodeString("UTF8"))


    val lines: Future[Seq[String]] = Source.single(sourceString).via(splitLines).runWith(Sink.seq)
    whenReady(lines) { result =>

      result shouldBe Seq("Hello", "World!")

    }
  }

}


trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    system.terminate()
  }
}