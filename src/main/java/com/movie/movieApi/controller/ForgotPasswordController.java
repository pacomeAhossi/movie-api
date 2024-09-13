package com.movie.movieApi.controller;

import com.movie.movieApi.auth.model.ForgotPassword;
import com.movie.movieApi.auth.model.User;
import com.movie.movieApi.auth.repository.ForgotPasswordRepository;
import com.movie.movieApi.auth.repository.UserRepository;
import com.movie.movieApi.auth.utils.ChangePassword;
import com.movie.movieApi.dto.MailBody;
import com.movie.movieApi.exceptions.OtpNotValidException;
import com.movie.movieApi.exceptions.UserWithUsernameNotFoundException;
import com.movie.movieApi.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(EmailService emailService, UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // On envoi un email pour vérifier l'adresse mail de l'utilisateur
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyMail(@PathVariable String email){
        // On vérifie qu'un user avec cet email existe dans la DB
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserWithUsernameNotFoundException("Entrez un email valide!"));

        // On génère le otp
        Integer otp = generateOtp();

        // On crée l'objet ForgotPassword pou y associer le OTP et ensuite persisté
        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 2*60*1000))
                .user(user)
                .build();


        // On construit le message à envoyer comme corps de mail pour la réinitialisation du
        // mot de passe contenant également le code OTP
        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("Réénitialisation de mot de passe : Voici le code de vérification à 6 chiffres que vous avez demandé")
                .text("Bonjour " + user.getName() + ",\n" +
                        "\n" +
                        "\n" +
                        "Utilisez le code ci-dessous pour réénitialiser votre mot de passe.\n" +
                        "\n" +
                        "\n" +
                        "Ce code expire dans 2 minutes.\n" +
                        "\n" + otp)
                .build();


        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.ok("Email envoyé pour vérification");

    }


    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email){
        // On vérifie qu'un user avec cet email existe dans la DB
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserWithUsernameNotFoundException("Entrez un email valide!"));

        // On vérifie si on a un enregistrement de ForgotPassword avec le otp et l'email fourni par le user
        ForgotPassword forgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new OtpNotValidException("OTP non valide!"));

        // On vérifie now si le OTP n'est pas expiré
        if (forgotPassword.getExpirationTime().before(Date.from(Instant.now()))){
            // On supprime le forgotPassword
            forgotPasswordRepository.deleteById(forgotPassword.getId());
            return new ResponseEntity<>("Le OTP a expiré!", HttpStatus.EXPECTATION_FAILED);
        }


        return ResponseEntity.ok("OTP vérifié avec succès");
    }


    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(
            @PathVariable String email,
            @RequestBody ChangePassword changePassword){

        // On vérifie si les champs password et repeatPassword sont conformes
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())){
            return new ResponseEntity<>("Les mots de passe ne correspondent pas.", HttpStatus.EXPECTATION_FAILED);
        }

        // On encode le mot de passe
        String encodedPassword = passwordEncoder.encode(changePassword.repeatPassword());

        // On modifie le mot de passe
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Mot de passe modifié avec succès");

    }


    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    private Integer generateOtp(){
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(900_000) + 100_000;
    }
}
