package it.avbo.dilaxia.api.servlets.auth;

import com.google.gson.Gson;
import it.avbo.dilaxia.api.database.DBWrapper;
import it.avbo.dilaxia.api.database.UsersSource;
import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.AuthResponse;
import it.avbo.dilaxia.api.models.auth.RegistrationModel;
import it.avbo.dilaxia.api.models.auth.enums.UserRole;
import it.avbo.dilaxia.api.services.JwtService;
import it.avbo.dilaxia.api.services.Utils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.SaltedSimpleDigestPassword;
import org.wildfly.security.password.spec.ClearPasswordSpec;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;

@WebServlet("/auth/register")
public class RegistrationServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private static final PasswordFactory passwordFactory;
    private static final RegexValidator usernameValidator;

    static {
        usernameValidator = new RegexValidator(
                "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$"
        );
        try {
            passwordFactory = PasswordFactory.getInstance(SaltedSimpleDigestPassword.ALGORITHM_SALT_PASSWORD_DIGEST_SHA_256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<String> data = Utils.stringFromReader(req.getReader());

        if(data.isEmpty()) {
            resp.setStatus(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
            return;
        }

        RegistrationModel registrationModel = gson.fromJson(data.get(), RegistrationModel.class);

        if(!(EmailValidator.getInstance().isValid(registrationModel.getEmail()) &&
                usernameValidator.isValid(registrationModel.getUsername()))) {
            resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            return;
        }

        ClearPasswordSpec clearPasswordSpec = new ClearPasswordSpec(registrationModel.getPassword().toCharArray());
        try {
            SaltedSimpleDigestPassword digestedPassword = (SaltedSimpleDigestPassword) passwordFactory.generatePassword(clearPasswordSpec);
            User user = new User(
                    registrationModel.getUsername(),
                    registrationModel.getEmail(),
                    digestedPassword.getDigest(),
                    digestedPassword.getSalt()
            );


            if(registrationModel.getEmail().contains("@aldini")) {
                user.setRole(UserRole.Student);
            } else if(registrationModel.getEmail().contains("@avbo")) {
                user.setRole(UserRole.Teacher);
            } else {
                resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
                return;
            }

            if(!UsersSource.addUser(user)) {
                resp.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
                return;
            }
            req.getSession(true);
            resp.setStatus(Response.Status.OK.getStatusCode());
            return;
        } catch (InvalidKeySpecException ignored) {
        }
        resp.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
    }
}
