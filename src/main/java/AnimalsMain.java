import io.vertx.core.*;
import io.vertx.ext.web.Router;

import persistance.ForLocation.LocationPersistence;
import persistance.forUsers.UsersPersistence;
import routing.forLocation.LocationRoute;

import routing.forUsers.RegistrationRoute;
import routing.forUsers.UsersRoute;
import services.forLocation.LocationManagerService;
import services.forUsers.UsersManagerService;

import static java.lang.System.out;

public class AnimalsMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router mainRouter = Router.router(vertx);

        UsersPersistence persistence = UsersPersistence.create();
        UsersManagerService usersManagerService = UsersManagerService.create(persistence);
        UsersRoute usersRoute = new UsersRoute(usersManagerService, vertx);

        LocationPersistence locationPersistence = LocationPersistence.create();
        LocationManagerService locationManagerService = LocationManagerService.create(locationPersistence);
        LocationRoute locationRoute = new LocationRoute(locationManagerService, vertx, usersManagerService);

        RegistrationRoute registrationRoute = new RegistrationRoute(usersManagerService, vertx);

        mainRouter.route("/*").subRouter(usersRoute.getRouter());
        mainRouter.route("/*").subRouter(locationRoute.getRouter());
        mainRouter.route("/*").subRouter(registrationRoute.getRouter());

        vertx.createHttpServer().requestHandler(mainRouter).listen(8080, http -> {
            if (http.succeeded()) out.println("server start on port 8080");
            else http.cause();
        });
    }
}
