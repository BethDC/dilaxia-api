package it.avbo.dilaxia.api.resources;

import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.AuthResponse;
import it.avbo.dilaxia.api.models.auth.LoginModel;
import it.avbo.dilaxia.api.models.auth.RegistrationModel;
import it.avbo.dilaxia.api.services.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.SaltedSimpleDigestPassword;
import org.wildfly.security.password.spec.ClearPasswordSpec;
import org.wildfly.security.password.spec.SaltedHashPasswordSpec;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@Path("/auth")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    @PersistenceContext
    private EntityManager entityManager;

    private static final PasswordFactory passwordFactory;

    static {
        try {
            passwordFactory = PasswordFactory.getInstance(SaltedSimpleDigestPassword.ALGORITHM_SALT_PASSWORD_DIGEST_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @POST
    @Path("/login")
    public Response login(LoginModel loginModel) {
        User user = entityManager.find(User.class, loginModel.getUsername());
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        SaltedHashPasswordSpec saltedHashSpec = new SaltedHashPasswordSpec(user.getPasswordDigest(), user.getSalt());

        try {
            SaltedSimpleDigestPassword restored = (SaltedSimpleDigestPassword) passwordFactory.generatePassword(saltedHashSpec);
            passwordFactory.verify(restored, loginModel.getPassword().toCharArray());
            String token = JwtService.generateToken(user);
            return Response.ok(new AuthResponse(token)).build();
        } catch (InvalidKeyException | InvalidKeySpecException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/register")
    @Transactional
    public Response register(RegistrationModel registrationModel) {
        ClearPasswordSpec clearPasswordSpec = new ClearPasswordSpec(registrationModel.getPassword().toCharArray());
        try {
            SaltedSimpleDigestPassword digestedPassword = (SaltedSimpleDigestPassword) passwordFactory.generatePassword(clearPasswordSpec);
            User user = new User(
                    registrationModel.getUsername(),
                    digestedPassword.getDigest(),
                    digestedPassword.getSalt()
            );
            entityManager.persist(user);
            String token = JwtService.generateToken(user);
            return Response.ok(new AuthResponse(token)).build();
        } catch (InvalidKeySpecException ignored) {
            return Response.serverError().build();
        }
    }
}
