package fun.reactive.fizzbuzz

import akka.actor.ActorSystem
import akka.stream.{Attributes, ClosedShape, ActorMaterializer}
import akka.stream.scaladsl._
import scala.concurrent.{Future, Await}
import scalaz._
import Scalaz._


object FizzBuzz {

  def main(args: Array[String]): Unit = {
    doItForMe()
  }


  def doItForMe() = {
    implicit val actorSystem = ActorSystem()
    implicit val flowMaterializer = ActorMaterializer()


    val fizzbuzzGraph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._

      val inputSource = akka.stream.scaladsl.Source(1 to 100)
      val bcast = builder.add(Broadcast[Int](3))

      val merge = builder.add(ZipWith[Option[String], Option[String], String, String](
        (a, b, c) => (a  | "" )+ ( b| ( (a.isDefined) ?  "" | c ) )
      ))


      val fizzer = Flow[Int].map(x => {

        Thread.sleep(20)
         (x % 3 == 0) ? "Fizz".some  | none[String]
      }).addAttributes(Attributes.asyncBoundary)
      val buzzer = Flow[Int].map(x => {
        Thread.sleep(10)

         (x % 5 == 0) ? "Buzz".some | none[String]
      }).addAttributes(Attributes.asyncBoundary)
      val stringify = Flow[Int].map(_.toString)

      val out = akka.stream.scaladsl.Sink.foreach[String] { value =>
        println(value)
      }

        inputSource ~> bcast     ~> fizzer ~> merge.in0
                                  bcast ~> buzzer   ~> merge.in1
                                  bcast ~> stringify ~> merge.in2

                                        merge.out ~> out
      ClosedShape
    })



    val materialized = fizzbuzzGraph.run()



  }
}
