package routing.ForAreas;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import services.ForAreas.AreasManagerService;
import services.forUsers.UsersManagerService;


import static java.lang.System.out;

public class AreasRoute {
    private final AreasManagerService areasManagerService;

    private final UsersManagerService usersManagerService;

    private Router areasRouter;

    private Vertx vertx;

    ServiceRequest request = new ServiceRequest();

    public AreasRoute(AreasManagerService areasManagerService, UsersManagerService usersManagerService, Vertx vertx){
        this.areasManagerService = areasManagerService;
        this.usersManagerService = usersManagerService;
        this.areasRouter = Router.router(vertx);
        this.vertx = vertx;
        initRouter();
    }

    public Router getRouter(){return areasRouter;}

    private void initRouter() {
        areasRouter.get("/areas/:areaId").handler(this::getAreaById);
        areasRouter.get("/areas/").handler(this::getAreaById);

        areasRouter.post("/areas").handler(this::addArea);

        areasRouter.put("/areas/:areaId").handler(this::updateAreaById);
    }

    private void getAreaById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult ->{
            if (bufferAsyncResult.succeeded()){
                String areaIdParam = routingContext.pathParam("areaId");
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = usersManagerService.checkAccountId(areaIdParam);
                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);

                if (!idIsValid){
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                if (!isAuthorize){
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                Integer areaid = Integer.parseInt(areaIdParam);
                out.println(areaid);
                boolean notFound = areasManagerService.checkArea(areaid);
                out.println(notFound);
                if (notFound){
                    routingContext.response().setStatusCode(404).end();
                    return;
                }

                else {
                    areasManagerService.getAreaById(areaid, request, resultHandler ->{
                        if (resultHandler.succeeded()){
                            ServiceResponse response = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                        }
                    });
                }
            }
        });
    }

    private void addArea(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult ->{
            if (bufferAsyncResult.succeeded()){
                JsonObject infoAboutNewArea = new JsonObject(bufferAsyncResult.result());
                String userToken = routingContext.request().getHeader("token");

                // Если пересечется true
                boolean notValidate = areasManagerService.validateData(infoAboutNewArea);
                out.println(notValidate);

//                boolean areasAreConflict = areasManagerService.checkConflictBetweenNewAreaAndAvailableAreas(infoAboutNewArea);
//                out.println(areasAreConflict);

                if (notValidate){
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

                boolean areaIsBusy = areasManagerService.checkAreaIsBusy(infoAboutNewArea);
                if (areaIsBusy){
                    routingContext.response().setStatusCode(409).end();
                    return;
                }

                else areasManagerService.addNewArea(infoAboutNewArea, request, resultHandler ->{
                        if (resultHandler.succeeded()){
                            ServiceResponse response = resultHandler.result();
                            routingContext.response().setStatusCode(201).end(response.getPayload().toString());
                        }
                });
            }
        });
    }

    private void updateAreaById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                String areaIdForUpdateParam = routingContext.pathParam("areaId");
                JsonObject updateInfoAboutArea = new JsonObject(bufferAsyncResult.result());
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = usersManagerService.checkAccountId(areaIdForUpdateParam);
                boolean dataNotValidate = areasManagerService.validateData(updateInfoAboutArea);

                if (!idIsValid || dataNotValidate){
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

                boolean areaIsBusy = areasManagerService.checkAreaIsBusy(updateInfoAboutArea);
                Integer areaIdForUpdate = Integer.parseInt(areaIdForUpdateParam);
                if (areaIsBusy){
                    routingContext.response().setStatusCode(409).end();
                    return;
                }

                else areasManagerService.updateAreaById(areaIdForUpdate, updateInfoAboutArea, request, resultHandler ->{
                    if (resultHandler.succeeded()){
                        ServiceResponse response = resultHandler.result();
                        routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                    }
                });

            }
        });
    }
}
