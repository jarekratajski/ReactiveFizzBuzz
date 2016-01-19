package fun.reactive.fizzbuzz

import akka.actor.ActorSystem
import akka.stream.{ClosedShape, ActorMaterializer}
import akka.stream.scaladsl._

object FizzBuzz {

  def main(args: Array[String]): Unit = {
    doItForMe()
  }


  def doItForMe() = {
    implicit val actorSystem = ActorSystem()
    import actorSystem.dispatcher
    implicit val flowMaterializer = ActorMaterializer()



    val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._

      val inputSource = Source(1 to 100)
      val bcast = builder.add(Broadcast[Int](3))
      val merge = builder.add(ZipWith[Option[String], Option[String], String, String](
        (a, b, c) => a.getOrElse("") + b.getOrElse(if (a.isDefined) {""} else c )
      ))


      val fizzer = Flow[Int].map(x => if (x % 3 == 0) Some("Fizz") else None)
      val buzzer = Flow[Int].map(x => if (x % 5 == 0) Some("Buzz") else None)
      val stringify = Flow[Int].map(_.toString)
      val out = Sink.foreach[String] { value =>
        println(value)
      }

      inputSource ~> bcast ~> fizzer ~> merge.in0
      bcast ~> buzzer ~> merge.in1
      bcast ~> stringify ~> merge.in2

      merge.out ~> out



      ClosedShape
    })


    g.run()


  }
}
