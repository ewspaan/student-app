package nl.spaan.student_app.service;

import nl.spaan.student_app.model.*;
import nl.spaan.student_app.payload.request.LoginRequest;
import nl.spaan.student_app.payload.request.SignupRequest;
import nl.spaan.student_app.payload.response.JwtResponse;
import nl.spaan.student_app.payload.response.MessageResponse;
import nl.spaan.student_app.repository.AccountRepository;
import nl.spaan.student_app.repository.HouseRepository;
import nl.spaan.student_app.repository.RoleRepository;
import nl.spaan.student_app.repository.UserRepository;
import nl.spaan.student_app.service.security.jwt.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthorizationService {

    private static final String ROLE_NOT_FOUND_ERROR = "Error: Role is not found.";
    private static final String HOUSE_NOT_FOUND_ERROR = "Error: House is not found.";

    private UserRepository userRepository;
    private HouseRepository houseRepository;
    private AccountRepository accountRepository;
    private PasswordEncoder encoder;
    private RoleRepository roleRepository;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private BillService billService;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setEncoder(PasswordEncoder passwordEncoder) {
        this.encoder = passwordEncoder;
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setHouseRepository(HouseRepository houseRepository) {this.houseRepository = houseRepository;}

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setBillService(BillService billService) {
        this.billService = billService;
    }

    public ResponseEntity<MessageResponse> registerUser(@Valid SignupRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is al in gebruik"));
        }

        // Create new users account samen met nieuw house account.
        Account account = new Account("12345678",0,0,0,0);
        House house = new House();

        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setDateOfBirth(signUpRequest.getDateOfBirth());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setHouse(house);

        Set<Role> roles = new HashSet<>();
        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
        roles.add(modRole);
        user.setRoles(roles);
        house.setAccount(account);
        account.setHouse(house);

        accountRepository.save(account);
        houseRepository.save(house);
        userRepository.save(user);
        LocalDate date = LocalDate.now();
        int month = date.getMonthValue();
        int year = date.getYear();
        billService.createBill(house.getId(), month, year);


        return ResponseEntity.ok(new MessageResponse("Je bent geregistreerd"));
    }

    //Voeg huisgenoten toe aan huis
    public ResponseEntity<MessageResponse> authenticateUserToHouse(SignupRequest signUpRequest){

        if (Boolean.FALSE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            System.out.println(signUpRequest.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Niet toegestaan. Meld je aan als huisoudste of laat je toevoegen aan het huis."));
        }

        User user = userRepository.findByEmail(signUpRequest.getEmail());

        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is al in gebruik"));
        }
        user.setUsername(signUpRequest.getUsername());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setDateOfBirth(signUpRequest.getDateOfBirth());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        
        House house = houseRepository.findById(signUpRequest.getHouseId()).orElseThrow(() -> new RuntimeException(HOUSE_NOT_FOUND_ERROR));

        user.setHouse(house);

        Set<Role> roles = new HashSet<>();
        Role modRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_ERROR));
        roles.add(modRole);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /*
    Bij inloggen
     Authenticate user bij username
    */

    public ResponseEntity<JwtResponse> authenticateUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponse(jwt));

    }

}
