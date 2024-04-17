package it.avbo.dilaxia.api.servlets.auth;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import it.avbo.dilaxia.api.database.UserSource;
import it.avbo.dilaxia.api.entities.User;
import it.avbo.dilaxia.api.models.auth.RegistrationModel;
import it.avbo.dilaxia.api.entities.enums.UserRole;
import it.avbo.dilaxia.api.services.Utils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.SaltedSimpleDigestPassword;
import org.wildfly.security.password.spec.ClearPasswordSpec;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Date;
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
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Optional<String> data = Utils.stringFromReader(req.getReader());

        if(data.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            return;
        }
        RegistrationModel registrationModel;
        try {
            registrationModel = gson.fromJson(data.get(), RegistrationModel.class);
        } catch (JsonSyntaxException e) {
            resp.sendError(
                    HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Il formato dei dati inviati non corrisponde alla documentazione"
            );
            return;
        }

        if(!(EmailValidator.getInstance().isValid(registrationModel.getEmail()) &&
                usernameValidator.isValid(registrationModel.getUsername()))) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        ClearPasswordSpec clearPasswordSpec = new ClearPasswordSpec(registrationModel.getPassword().toCharArray());
        try {
            SaltedSimpleDigestPassword digestedPassword = (SaltedSimpleDigestPassword) passwordFactory.generatePassword(clearPasswordSpec);

            UserRole role;
            if(registrationModel.getEmail().contains("@aldini")) {
                role = UserRole.Student;
            } else if(registrationModel.getEmail().contains("@avbo")) {
                role = UserRole.Teacher;
            } else {
                role = UserRole.External;
            }

            User user = new User(
                    registrationModel.getUsername(),
                    registrationModel.getEmail(),
                    registrationModel.getSex(),
                    Date.valueOf(registrationModel.getBirthday()),
                    role,
                    digestedPassword.getDigest(),
                    digestedPassword.getSalt()
            );

            if(!UserSource.addUser(user)) {
                resp.sendError(
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Impossibile aggiungere l'utente"
                );
                return;
            }
            req.getSession().setAttribute("role", user.role);
            req.getSession().setMaxInactiveInterval(300);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            return;
        } catch (InvalidKeySpecException ignored) {

        }
        resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
    
}
