package routing.forUsers;

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


import java.util.ArrayList;
import java.util.Arrays;

import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.System.out;

public class UsersRoute {
    private UsersManagerService usersManagerService;
    private Router usersRouter;
    private Vertx vertx;
    private final String[] roles = {"ADMIN", "CHIPPER", "USER"};
    ArrayList<String> rolesList = new ArrayList<>(Arrays.asList(roles));

    JWTAuthOptions config = new JWTAuthOptions()
            .addPubSecKey(new PubSecKeyOptions()
                    .setAlgorithm("HS256")
                    .setBuffer("keyboard cat"));

    JWTAuth provider = JWTAuth.create(vertx, config);

    ServiceRequest request = new ServiceRequest();

    public UsersRoute(UsersManagerService usersManagerService, Vertx vertx) {
        this.usersManagerService = usersManagerService;
        this.usersRouter = Router.router(vertx);
        this.vertx = vertx;
        initRouter();
    }

    public Router getRouter(){
        return usersRouter;
    }

    private void initRouter() {
        usersRouter.get("/accounts/search").handler(this::getUserByParameterRoute);
        usersRouter.get("/accounts/:accountId").handler(this::getUserByIdRoute);
        usersRouter.get("/accounts/").handler(this::getUserByIdRoute);
        usersRouter.post("/accounts").handler(this::addUserRoute);
        usersRouter.put("/accounts/:accountId").handler(this::updateUserByIdRoute);
        usersRouter.put("/accounts/").handler(this::updateUserByIdRoute);
        usersRouter.delete("/accounts/:accountId").handler(this::deleteUserByIdRoute);
        usersRouter.delete("/accounts/").handler(this::deleteUserByIdRoute);
    }


    private void deleteUserByIdRoute(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()){
                String userAccountIdForDeleteParameter = routingContext.pathParam("accountId");
                String userToken = routingContext.request().getHeader("token");

                ServiceRequest request = new ServiceRequest();

                boolean userIsValid = usersManagerService.checkAccountId(userAccountIdForDeleteParameter);
                boolean userLinkedWithAnimal = usersManagerService.linkedWithAnimal(userAccountIdForDeleteParameter);
                boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);

                if (!userIsValid || userLinkedWithAnimal) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                if (!isAuthorize) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                Integer userAccountIdForDelete = Integer.parseInt(userAccountIdForDeleteParameter);
                String userRole = usersManagerService.checkRole(userToken);
                boolean notEmpty = usersManagerService.emptyOrNot(userAccountIdForDelete);
                if (userRole.equals("ADMIN")) {
                    if (notEmpty) {
                        usersManagerService.deleteUserById(userAccountIdForDelete, request, resultHandler -> {
                            if (resultHandler.succeeded()) {
                                ServiceResponse result = resultHandler.result();
                                routingContext.response().setStatusCode(200).end(result.getPayload().toString());
                            }
                        });
                    } else {
                        routingContext.response().setStatusCode(404).end();
                        return;
                    }
                }
                if (userRole.equals("USER") || userRole.equals("CHIPPER")){
                    boolean notStranger = usersManagerService.stranger(userToken, userAccountIdForDelete);
                    if (notStranger && notEmpty){
                        usersManagerService.deleteUserById(userAccountIdForDelete, request, resultHandler ->{
                            ServiceResponse result = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(result.getPayload().toString());
                        });
                    } else {
                        routingContext.response().setStatusCode(403).end();
                        return;
                    }
                }
            }
        });
    }

    private void updateUserByIdRoute(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()) {
                JsonObject newInfo = new JsonObject(bufferAsyncResult.result());

                ServiceRequest request = new ServiceRequest();

                if (!usersManagerService.checkAccountId(routingContext.pathParam("accountId")) || Stream.of(
                        Optional.ofNullable(newInfo.getString("firstName")).map(String::trim),
                        Optional.ofNullable(newInfo.getString("lastName")).map(String::trim),
                        Optional.ofNullable(newInfo.getString("email")).map(String::trim),
                        Optional.ofNullable(newInfo.getString("password")).map(String::trim),
                        Optional.ofNullable(newInfo.getString("role")).map(String::trim)
                ).anyMatch(optional -> optional.isEmpty() || optional.get().isEmpty()) || !newInfo.getString("email").contains("@") || !rolesList.contains(newInfo.getString("role"))) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                if (!usersManagerService.checkUserToAuthorize(routingContext.request().getHeader("token"))) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                if (usersManagerService.checkRole(routingContext.request().getHeader("token")).equals("ADMIN")) {
                    if (!usersManagerService.emptyOrNot(Integer.parseInt(routingContext.pathParam("accountId")))) {
                        routingContext.response().setStatusCode(404).end();
                        return;
                    }
                    if (!usersManagerService.emailIsBusy(newInfo.getString("email"))) {
                        routingContext.response().setStatusCode(409).end();
                        return;
                    }
                    else usersManagerService.updateUserById(Integer.parseInt(routingContext.pathParam("accountId")), newInfo, request, resultHandler -> {
                        if (resultHandler.succeeded()) {
                            ServiceResponse result = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(result.getPayload().toString());
                        }
                    });

                }

                if (usersManagerService.checkRole(routingContext.request().getHeader("token")).equals("USER") || usersManagerService.checkRole(routingContext.request().getHeader("token")).equals("CHIPPER")) {
                    if (!(usersManagerService.stranger(routingContext.request().getHeader("token"), Integer.parseInt(routingContext.pathParam("accountId"))) && usersManagerService.emptyOrNot(Integer.parseInt(routingContext.pathParam("accountId"))))) {
                        routingContext.response().setStatusCode(403).end();
                        return;
                    }
                    if (!usersManagerService.emailIsBusy(newInfo.getString("email"))) {
                        routingContext.response().setStatusCode(409).end();
                        return;
                    }
                    else usersManagerService.updateUserById(Integer.parseInt(routingContext.pathParam("accountId")), newInfo, request, resultHandler -> {
                        if (resultHandler.succeeded()) {
                            ServiceResponse result = resultHandler.result();
                            routingContext.response().setStatusCode(200).end(result.getPayload().toString());
                        }
                    });
                }
            }
        });
    }

    private void addUserRoute(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
            if (bufferAsyncResult.succeeded()) {
                JsonObject info = new JsonObject(bufferAsyncResult.result());

                info.put("token", provider.generateToken(new JsonObject().put("us", info.getString("email")), new JWTOptions()));

                ServiceRequest request = new ServiceRequest();

                if (Stream.of(
                        Optional.ofNullable(info.getString("firstName")).map(String::trim),
                        Optional.ofNullable(info.getString("lastName")).map(String::trim),
                        Optional.ofNullable(info.getString("email")).map(String::trim),
                        Optional.ofNullable(info.getString("password")).map(String::trim),
                        Optional.ofNullable(info.getString("role")).map(String::trim)
                ).anyMatch(optional -> optional.isEmpty() || optional.get().isEmpty()) || !info.getString("email").contains("@") || !rolesList.contains(info.getString("role"))) {
                    routingContext.response().setStatusCode(400).end();
                    return;
                }

                if (!usersManagerService.checkUserToAuthorize(routingContext.request().getHeader("token"))) {
                    routingContext.response().setStatusCode(401).end();
                    return;
                }

                if (!usersManagerService.checkRole(routingContext.request().getHeader("token")).equals("ADMIN")) {
                    routingContext.response().setStatusCode(403).end();
                    return;
                }

                if (!usersManagerService.emailIsBusy(info.getString("email"))) {
                    routingContext.response().setStatusCode(409).end();
                    return;
                }

                else usersManagerService.addUser(info, request, resultHandler -> {
                    if (resultHandler.succeeded()) {
                        ServiceResponse result = resultHandler.result();
                        routingContext.response().setStatusCode(201).end(result.getPayload().toString());
                    }
                });
            }
        });
    }

    private void getUserByParameterRoute(RoutingContext routingContext) {
        String firstName = routingContext.request().getParam("firstName");
        String lastName = routingContext.request().getParam("lastName");
        String email = routingContext.request().getParam("email");
        int from = 0;
        int size = 10;

        String userToken = routingContext.request().getHeader("token");

        var fromParam = routingContext.request().getParam("from");
        if (fromParam != null && !fromParam.isEmpty()) {
            from = Integer.parseInt(routingContext.request().getParam("from"));
        }

        String sizeParam = routingContext.request().getParam("size");
        if (sizeParam != null && !sizeParam.isEmpty()) {
            size = Integer.parseInt(routingContext.request().getParam("size"));
        }

        boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);

        if (from < 0 || size <= 0) {
            routingContext.response().setStatusCode(400).end();
            return;
        }

        if (!isAuthorize) {
            routingContext.response().setStatusCode(401).end();
            return;
        }

        String role = usersManagerService.checkRole(userToken);

        if (!role.equals("ADMIN")) {
            routingContext.response().setStatusCode(403).end();
            return;
        }

        else usersManagerService.getUserByParam(firstName, lastName, email, from, size, request, resultHandler -> {
            if (resultHandler.succeeded()){
                ServiceResponse result = resultHandler.result();
                routingContext.response().setStatusCode(200).end(result.getPayload().toString());
            }
        });
    }


    private void getUserByIdRoute(RoutingContext routingContext) {
        routingContext.request().body().onComplete(bufferAsyncResult -> {
           if (bufferAsyncResult.succeeded()){
               String idParam = routingContext.pathParam("accountId");
               String userToken = routingContext.request().getHeader("token");

               boolean idIsValid = usersManagerService.checkAccountId(idParam);
               boolean isAuthorize = usersManagerService.checkUserToAuthorize(userToken);

               if (!idIsValid) {
                   routingContext.response().setStatusCode(400).end();
                   return;
               }

               Integer accountId = Integer.parseInt(idParam);

               boolean notempty = usersManagerService.emptyOrNot(accountId);

               if (!isAuthorize) {
                   routingContext.response().setStatusCode(401).end();
                   return;
               }

               String role = usersManagerService.checkRole(userToken);
               boolean notstranger = usersManagerService.stranger(userToken, accountId);

               if (role.equals("ADMIN")) {
                   if (!notempty) {
                       routingContext.response().setStatusCode(404).end();
                       return;
                   }
                   else usersManagerService.getUserById(accountId, request, resultHandler -> {
                       if (resultHandler.succeeded()) {
                           ServiceResponse result = resultHandler.result();
                           routingContext.response().end(result.getPayload().toString());
                       }
                   });
               } else {
                   if (!notstranger) {
                       routingContext.response().setStatusCode(403).end();
                       return;
                   }
                   else usersManagerService.getUserById(accountId, request, resultHandler -> {
                       if (resultHandler.succeeded()) {
                           ServiceResponse result = resultHandler.result();
                           routingContext.response().end(result.getPayload().toString());
                       }
                   });
               }
           }
        });
    }
}