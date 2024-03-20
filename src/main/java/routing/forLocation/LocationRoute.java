package routing.forLocation;

import io.swagger.models.auth.In;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import services.forLocation.LocationManagerService;
import services.forUsers.UsersManagerService;

import static java.lang.System.out;

public class LocationRoute {

    private UsersManagerService usersManagerService;

    private LocationManagerService locationManagerService;
    private Router locationRouter;
    private Vertx vertx;

    ServiceRequest request = new ServiceRequest();

    public LocationRoute(LocationManagerService locationManagerService, Vertx vertx, UsersManagerService usersManagerService) {
        this.locationManagerService = locationManagerService;
        this.locationRouter = Router.router(vertx);
        this.vertx = vertx;
        this.usersManagerService = usersManagerService;
        initRouter();
    }

    public Router getRouter(){
        return locationRouter;
    }

    private void initRouter() {
        locationRouter.get("/locations/:pointId").handler(this::getLocationById);
        locationRouter.get("/locations/").handler(this::getLocationById);

        locationRouter.post("/locations").handler(this::addLocation);

        locationRouter.put("/locations/:pointId").handler(this::updateLocationById);
        locationRouter.put("/locations/").handler(this::updateLocationById);

        locationRouter.delete("/locations/:pointId").handler(this::deleteLocationById);
        locationRouter.delete("/locations/").handler(this::deleteLocationById);
    }

    private void getLocationById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult ->{
            if (bufferAsyncResult.succeeded()){
                String pointIdParam = routingContext.pathParam("pointId");
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = locationManagerService.checkLocationId(pointIdParam);
                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);

                if (!idIsValid) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }
                if (!isAuthorize) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }
                if (locationManagerService.emptyOrNot(Integer.parseInt(pointIdParam))) {
                    routingContext.response().setStatusCode(404).end();
                    return;
                }
                else locationManagerService.getLocationById(Integer.parseInt(pointIdParam), request, resultHandler ->{
                    if (resultHandler.succeeded()){
                        ServiceResponse response = resultHandler.result();
                        routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                    }
                });

            }
        });
    }

    private void addLocation(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                JsonObject infoAboutNewLocation = new JsonObject(bufferAsyncResult.result());
                String userToken = routingContext.request().getHeader("token");

                boolean dataIsValid = locationManagerService.dataIsValid(infoAboutNewLocation);
                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);
                boolean coordsIsFree = locationManagerService.coordIsFree(infoAboutNewLocation);

                if (!dataIsValid) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                if (!isAuthorize) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                String userRole = usersManagerService.checkRole(userToken);

                if (userRole.equals("USER")){
                    routingContext.response().setStatusCode(403).end();
                    return;
                }

                if (!coordsIsFree) {
                    routingContext.response().setStatusCode(409).end();
                    return;
                }

                else locationManagerService.addLocation(infoAboutNewLocation, request, resultHandler -> {
                    if (resultHandler.succeeded()){
                        ServiceResponse response = resultHandler.result();
                        routingContext.response().setStatusCode(201).end(response.getPayload().toString());
                    }
                });
            }
        });
    }

    private void updateLocationById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()) {
                JsonObject updateInfoAboutLocation = new JsonObject(bufferAsyncResult.result());
                String idLocationParam = routingContext.pathParam("pointId");
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = locationManagerService.checkLocationId(idLocationParam);
                if (!idIsValid){
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                boolean dataIsValid = locationManagerService.dataIsValid(updateInfoAboutLocation);
                Integer idLocation = Integer.parseInt(idLocationParam);
                boolean visitedByAnimal = locationManagerService.visitedOrNot(idLocation);
                boolean chippingPoint = locationManagerService.chippingOrNot(updateInfoAboutLocation);

                if (!dataIsValid || visitedByAnimal || chippingPoint) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);
                if (!isAuthorize) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                String userRole = usersManagerService.checkRole(userToken);
                if (userRole.equals("USER")){
                    routingContext.response().setStatusCode(403).end();
                    return;
                }

                boolean notFound = locationManagerService.emptyOrNot(idLocation);
                if (notFound){
                    routingContext.response().setStatusCode(404).end();
                    return;
                }

                boolean pointIsBusy = locationManagerService.coordIsFree(updateInfoAboutLocation);
                if (!pointIsBusy){
                    routingContext.response().setStatusCode(409).end();
                    return;
                }
                else {
                    locationManagerService.updateLocationById(idLocation, updateInfoAboutLocation, request, resultHandler ->{
                        if (resultHandler.succeeded()){
                            ServiceResponse response = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                        }
                    });
                }

            }
        });
    }

    private void deleteLocationById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                String locationIdForDeleteParam = routingContext.pathParam("pointId");
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = locationManagerService.checkLocationId(locationIdForDeleteParam);
                if (!idIsValid) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                Integer locationIdForDelete = Integer.parseInt(locationIdForDeleteParam);
                boolean linkedWithAnimal = locationManagerService.linkedWithAnimal(locationIdForDelete);
                if (linkedWithAnimal){
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);
                if (!isAuthorize){
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                String userRole = usersManagerService.checkRole(userToken);
                if (!userRole.equals("ADMIN")){
                    routingContext.response().setStatusCode(403).end();
                    return;
                }

                boolean notFound = locationManagerService.emptyOrNot(locationIdForDelete);
                if (notFound){
                    routingContext.response().setStatusCode(404).end();
                    return;
                }
                else {
                    locationManagerService.deleteLocationById(locationIdForDelete, request, resultHandler -> {
                        if (resultHandler.succeeded()){
                            ServiceResponse response = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                        }
                    });
                }
            }
        });
    }
}
