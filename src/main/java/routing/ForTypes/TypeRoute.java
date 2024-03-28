package routing.ForTypes;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import services.ForTypes.TypesManagerService;
import services.forUsers.UsersManagerService;

public class TypeRoute {

    private final UsersManagerService usersManagerService;
    private final TypesManagerService typesManagerService;



    private Router typesRouter;

    private Vertx vertx;

    ServiceRequest request = new ServiceRequest();
    public TypeRoute(TypesManagerService typesManagerService, UsersManagerService usersManagerService, Vertx vertx) {
        this.typesManagerService = typesManagerService;
        this.usersManagerService = usersManagerService;
        this.typesRouter = Router.router(vertx);
        this.vertx = vertx;
        initRouter();
    }

    public Router getTypesRouter(){return typesRouter;}
    private void initRouter() {
        typesRouter.get("/animals/types/:typeId").handler(this::findTypeById);
        typesRouter.get("/animals/types/").handler(this::findTypeById);
    }

    private void findTypeById(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                String idParam = routingContext.pathParam("typeId");
                String userToken = routingContext.request().getHeader("token");

                boolean idIsValid = usersManagerService.checkAccountId(idParam);
                if (!idIsValid){
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);
                if (!isAuthorize) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                Integer typeId = Integer.parseInt(idParam);
                boolean notFound = typesManagerService.checkTypeIsExist(typeId);
                if (notFound) {
                    routingContext.response().setStatusCode(404).end();
                    return;
                }

                else typesManagerService.getTypesById(typeId, request, resultHandler -> {
                    if (resultHandler.succeeded()){
                        ServiceResponse response = resultHandler.result();
                        routingContext.response().setStatusCode(200).end(response.getPayload().toString());
                    }
                });
            }
        });
    }
}
