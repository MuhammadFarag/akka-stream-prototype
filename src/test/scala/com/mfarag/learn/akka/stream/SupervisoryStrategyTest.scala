package com.mfarag.learn.akka.stream

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Framing, Keep, RunnableGraph, Sink, Source}
import akka.stream.{ActorAttributes, ActorMaterializer, Supervision}
import akka.testkit.TestKit
import akka.util.ByteString
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuiteLike, Matchers}

import scala.collection.immutable
import scala.concurrent.Future

class SupervisoryStrategyTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike with StopSystemAfterAll with Matchers with ScalaFutures {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val parse: Flow[String, Int, NotUsed] = Flow[String].map(_.toInt)


  def graph(parse: Flow[String, Int, NotUsed]): RunnableGraph[Future[immutable.Seq[Int]]] =
    Source.single(ByteString("1,2,a,4"))
      .via(Framing
        .delimiter(ByteString(","), 3, allowTruncation = true)
        .map(_.decodeString("UTF8")))
      .via(parse).toMat(Sink.seq)(Keep.right)


  test("Resume a flow when a specific Exception is thrown") {

    val decider: Supervision.Decider = {
      case _: NumberFormatException => Supervision.Resume
    }

    whenReady(
      graph(
        parse
          .withAttributes(ActorAttributes.supervisionStrategy(decider)))
        .run) { result =>
      result shouldBe Seq(1, 2, 4)
    }
  }

}
