package Common

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.collection.mutable.ListBuffer
import com.typesafe.config.ConfigFactory
import Common._

class ClientActor extends Actor {
	var listOfKioskActors = new ListBuffer[ActorRef]()


	def receive = {
		case TheKioskActors(theKioskActors) => 
			listOfKioskActors = theKioskActors
	}
}