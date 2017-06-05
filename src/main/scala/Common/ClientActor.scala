package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class ClientActor extends Actor {
	var listOfKioskActors = new ListBuffer[ActorRef]()
	val numberOfKioskActors = ConfigFactory.load.getInt("number-kioskActors")
	var isTheEventSoldOut = false
	val randomNumberGenerator = scala.util.Random


	def receive = {
		case TheKioskActors(theKioskActors) => 
			listOfKioskActors = theKioskActors
			Thread.sleep(50)
			if (isTheEventSoldOut != true) {
				var randomIndex = randomNumberGenerator.nextInt(numberOfKioskActors)
				println(self.path.name + " buying ticket from " + listOfKioskActors(randomIndex).path.name)
				listOfKioskActors(randomIndex) ! BuyTicket
			}
		case TicketSold => 
			println("Received ticket from " + sender.path.name)
		case NoMoreTickets => 
			println("Awe shucks! " + sender.path.name + " I..." + self.path.name + ", will be back again!")
	}
}