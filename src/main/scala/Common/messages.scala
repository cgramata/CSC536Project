package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer

case class Neighbors(leftNeighbor: ActorRef, rightNeighbor: ActorRef)
case class TheKioskActors(theKioskActors: ListBuffer[ActorRef])
case class TicketsFromMaster(theTickets: Integer)
case object TicketBought
case object SoldOut
case object Start