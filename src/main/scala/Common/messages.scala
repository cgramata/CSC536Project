package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

case class Neighbors(leftNeighbor: ActorRef, rightNeighbor: ActorRef)
case class TicketsFromMaster(theTickets: Integer)
case object SoldOut
case object Start