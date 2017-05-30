import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import common._

class MasterActor extends Actor {
	val numberKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	val numberClientActors = ConfigFactory.load.getInt("number-clientActors")
	var numberTickets = ConfigFactory.load.getInt("number-TicketsA")
	var leftNeighbor = self
	var rightNeighbor = self


	def receive = {
		case Neighbors(leftNeighbor, rightNeighbor) => 
			leftNeighbor = leftNeighbor
			rightNeighbor = rightNeighbor

	}
}