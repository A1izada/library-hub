package az.libraryhub.userservice.service;

import az.libraryhub.userservice.dto.*;
import az.libraryhub.userservice.entity.User;
import az.libraryhub.userservice.exception.UserAlreadyExistsException;
import az.libraryhub.userservice.repository.BorrowHistoryRepository;
import az.libraryhub.userservice.repository.UserRepository;
import az.libraryhub.userservice.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final BorrowHistoryRepository borrowHistoryRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       BorrowHistoryRepository borrowHistoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.borrowHistoryRepository = borrowHistoryRepository;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Bu istifadəçi adı artıq mövcuddur: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Bu email artıq mövcuddur: " + request.getEmail());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole("USER");
        user.setIsActive(true);

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole(),
                saved.getCreatedAt()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("İstifadəçi tapılmadı"));
        String token = jwtUtil.generateToken(request.getUsername(), user.getRole(), user.getId());

        return new AuthResponse(token, "Bearer", 86400L);
    }

    public UserResponse getByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "İstifadəçi tapılmadı: " + username));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public UserResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "İstifadəçi tapılmadı: " + username));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        user.setUpdatedAt(java.time.LocalDateTime.now());

        User saved = userRepository.save(user);

        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole(),
                saved.getCreatedAt()
        );
    }

    public List<BorrowHistoryResponse> getBorrowHistoryByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "İstifadəçi tapılmadı: " + username));

        return borrowHistoryRepository.findByUserId(user.getId())
                .stream()
                .map(h -> new BorrowHistoryResponse(
                        h.getBookId(), h.getBookTitle(), h.getBorrowedAt(), h.getReturnedAt(), h.getStatus()))
                .toList();
    }
}
