package com.mfarag.learn.akka.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.TestKit
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

}


trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: TestKit with Suite =>
  override protected def afterAll() {
    super.afterAll()
    system.terminate()
  }
}