package Common

case class Neighbors(leftNeighbor: ActorRef, rightNeighbor: ActorRef)
case object SoldOut
case object Start