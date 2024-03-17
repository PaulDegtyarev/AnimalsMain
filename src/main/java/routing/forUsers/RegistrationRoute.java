package routing.forUsers;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import services.forUsers.UsersManagerService;

import java.util.Optional;
import java.util.stream.Stream;

public class RegistrationRoute {

    private Vertx vertx;
    private Router registrationRouter;
    private UsersManagerService usersManagerService;

    JWTAuthOptions config = new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setBuffer("keyboard cat"));

    JWTAuth provider = JWTAuth.create(vertx, config);

    ServiceRequest request = new ServiceRequest();

    public Router getRouter() {
        return registrationRouter;
    }

    public RegistrationRoute(UsersManagerService usersManagerService, Vertx vertx){
        this.usersManagerService = usersManagerService;
        this.registrationRouter = Router.router(vertx);
        this.vertx = vertx;
        initRouter();
    }

    public void initRouter(){
        registrationRouter.post("/registration").handler(this::registrationUserRoute);
    }

    private void registrationUserRoute(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                String userToken = routingContext.request().getHeader("token");

                JsonObject userInfo = new JsonObject(bufferAsyncResult.result());

                String userEmail = userInfo.getString("email");

                ServiceRequest request = new ServiceRequest();

                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);
                boolean emailIsEmpty = usersManagerService.emailIsBusy(userEmail);

                if (Stream.of(
                        Optional.ofNullable(userInfo.getString("firstName")).map(String::trim),
                        Optional.ofNullable(userInfo.getString("lastName")).map(String::trim),
                        Optional.ofNullable(userEmail).map(String::trim),
                        Optional.ofNullable(userInfo.getString("password")).map(String::trim)
                ).anyMatch(optional -> optional.isEmpty() || optional.get().isEmpty()) || !userEmail.contains("@")) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }
                if (isAuthorize) {
                    routingContext.response().setStatusCode(403).end();
                    return;
                }

                if (!emailIsEmpty) {
                    routingContext.response().setStatusCode(409).end();
                    return;
                }

                else {
                    String newtoken = provider.generateToken(new JsonObject().put("us", userEmail), new JWTOptions());
                    userInfo.put("token", newtoken);

                    usersManagerService.registration(userInfo, request, resultHandler -> {
                        if (resultHandler.succeeded()){
                            ServiceResponse response = resultHandler.result();
                            routingContext.response().setStatusCode(201).end(response.getPayload().toString());
                        }
                    });
                }
            }
        });
    }
}
