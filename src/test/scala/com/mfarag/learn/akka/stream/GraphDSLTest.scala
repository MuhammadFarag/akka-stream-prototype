package com.mfarag.learn.akka.stream

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.GraphDSL.Implicits._
import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source}
import akka.testkit.TestKit
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSuiteLike, Matchers}

import scala.concurrent.Future

class GraphDSLTest extends TestKit(ActorSystem("test-system")) with FunSuiteLike with StopSystemAfterAll with Matchers with ScalaFutures {

  implicit val materializer: ActorMaterializer = ActorMaterializer()

  test("Hello GraphDSL, constructing a simple flow from source to Sink using GraphDSL") {

    val in = Source(1 to 10)
    val out: Sink[Int, Future[Seq[Int]]] = Sink.seq[Int]

    val graph: Graph[ClosedShape.type, Future[Seq[Int]]] =
      GraphDSL.create(out) { implicit builder: GraphDSL.Builder[Future[Seq[Int]]] =>
        out =>
          in ~> out
          ClosedShape
      }

    val runnableGraph: RunnableGraph[Future[Seq[Int]]] = RunnableGraph.fromGraph(graph)

    whenReady(runnableGraph.run()) { result =>
      result shouldBe (1 to 10)
    }

  }
}
