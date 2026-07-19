package az.libraryhub.user.service

import az.libraryhub.userservice.dto.LoginRequest
import az.libraryhub.userservice.dto.RegisterRequest
import az.libraryhub.userservice.entity.User
import az.libraryhub.userservice.exception.UserAlreadyExistsException
import az.libraryhub.userservice.repository.BorrowHistoryRepository
import az.libraryhub.userservice.repository.UserRepository
import az.libraryhub.userservice.security.JwtUtil
import az.libraryhub.userservice.service.UserService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    AuthenticationManager authenticationManager = Mock()
    JwtUtil jwtUtil = Mock()
    BorrowHistoryRepository borrowHistoryRepository = Mock()

    UserService userService = new UserService(userRepository, passwordEncoder, authenticationManager, jwtUtil, borrowHistoryRepository)

    def "yeni istifadəçi uğurla qeydiyyatdan keçir"() {
        given: "qeydiyyat üçün düzgün məlumatlar"
        def request = new RegisterRequest()
        request.username = "testuser"
        request.email = "test@example.com"
        request.password = "Password123!"
        request.fullName = "Test User"

        userRepository.existsByUsername("testuser") >> false
        userRepository.existsByEmail("test@example.com") >> false
        passwordEncoder.encode("Password123!") >> "hashedPassword"
        userRepository.save(_ as User) >> { User u -> u.setId(1L); return u }

        when: "register metodu çağırılır"
        def result = userService.register(request)

        then: "istifadəçi düzgün formada qaytarılır"
        result.username == "testuser"
        result.email == "test@example.com"
        result.role == "USER"
    }

    def "artıq mövcud olan username ilə qeydiyyat rədd edilir"() {
        given: "artıq mövcud olan username"
        def request = new RegisterRequest()
        request.username = "existinguser"
        request.email = "new@example.com"
        request.password = "Password123!"

        userRepository.existsByUsername("existinguser") >> true

        when: "register metodu çağırılır"
        userService.register(request)

        then: "UserAlreadyExistsException atılır"
        thrown(UserAlreadyExistsException)
    }

    def "artıq mövcud olan email ilə qeydiyyat rədd edilir"() {
        given: "artıq mövcud olan email"
        def request = new RegisterRequest()
        request.username = "newuser"
        request.email = "existing@example.com"
        request.password = "Password123!"

        userRepository.existsByUsername("newuser") >> false
        userRepository.existsByEmail("existing@example.com") >> true

        when: "register metodu çağırılır"
        userService.register(request)

        then: "UserAlreadyExistsException atılır"
        thrown(UserAlreadyExistsException)
    }
    def "düzgün məlumatlarla login uğurla baş tutur"() {
        given: "düzgün username və parol"
        def request = new LoginRequest()
        request.username = "testuser"
        request.password = "Password123!"

        def user = new User()
        user.id = 1L
        user.username = "testuser"
        user.role = "USER"

        authenticationManager.authenticate(_) >> null
        userRepository.findByUsername("testuser") >> Optional.of(user)
        jwtUtil.generateToken("testuser", "USER", 1L) >> "fake-jwt-token"

        when: "login metodu çağırılır"
        def result = userService.login(request)

        then: "token uğurla qaytarılır"
        result.accessToken == "fake-jwt-token"
        result.tokenType == "Bearer"
    }

    def "mövcud olmayan istifadəçi ilə login rədd edilir"() {
        given: "bazada olmayan username"
        def request = new LoginRequest()
        request.username = "unknown"
        request.password = "SomePassword"

        authenticationManager.authenticate(_) >> null
        userRepository.findByUsername("unknown") >> Optional.empty()

        when: "login metodu çağırılır"
        userService.login(request)

        then: "UsernameNotFoundException atılır"
        thrown(org.springframework.security.core.userdetails.UsernameNotFoundException)
    }
}