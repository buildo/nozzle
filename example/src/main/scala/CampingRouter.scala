import models._
import nozzle.monadicctrl.JSendMarshallingSupport._
import nozzle.monadicctrl.RoutingHelpers._

import spray.routing._
import spray.routing.Directives._
import spray.httpx.SprayJsonSupport._
import ExampleJsonProtocol._

import scala.concurrent.ExecutionContext.Implicits.global

trait CampingRouter {
  val route: Route
}

@nozzle.modules.module
class CampingRouterImpl(campingController: CampingController) extends CampingRouter {

  override val route = {
    pathPrefix("campings") {
      (get & pathEnd & parameters('coolness.as[String], 'size.as[Int].?) /**
        get campings matching the requested coolness and size
        @param coolness how cool it is
        @param size the number of tents
      */) (returns[List[Camping]].ctrl(campingController.getByCoolnessAndSize _)) ~
      // get /campings/13
      (get & path(IntNumber) /**
        get a camping by id
      */) (returns[Camping].ctrl(campingController.getById _)) ~
      // post /campings
      (post & pathEnd & entity(as[Camping]) /**
        create a camping
      */) (returns[Camping].ctrl(campingController.create _)) ~
      // get /campings
      (get & pathEnd /**
        get all campings
      */) (returns[List[Camping]].ctrl(campingController.getAll _))
    }
  }

}
