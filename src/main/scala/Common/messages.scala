package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer

case class Neighbors(leftNeighbor: ActorRef, rightNeighbor: ActorRef)
case class TheKioskActors(theKioskActors: ListBuffer[ActorRef])
case class TicketsFromMaster(theTickets: Integer)
case class TicketsFromNeighbor(moreTickets: Integer)
case object BuyTicket
case object TicketSold
case object NoMoreTickets
case object SoldOut
case object Start
case object ResumeMessageProcessing
case object CanIHaveMoreTickets
case object ComeBackLater
case object IHaveNothing
