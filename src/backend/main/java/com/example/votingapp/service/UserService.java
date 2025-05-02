//package com.example.votingapp.service;
//
//import com.example.votingapp.model.User;
//import com.example.votingapp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public User registerUser(User user) {
//        // Перевірка віку користувача
//        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
//            throw new IllegalArgumentException("Користувачу має бути не менше 18 років");
//        }
//        // Хешування паролю
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        // Встановлення ролі за замовчуванням
//        user.setRole("USER");
//
//        // Встановлення дефолтного значення для emailVerified
//        user.setEmailVerified(false);
//
//        // Збереження користувача
//        return userRepository.save(user);
//    }
//
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
//
//    public User findById(Long id) {
//        Optional<User> user = userRepository.findById(id);
//        return user.orElse(null);
//    }
//
//    public void deleteById(Long id) {
//        userRepository.deleteById(id);
//    }
//
//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
//
//    public User save(User user) {
//        return userRepository.save(user);
//    }
//
//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//}


//package com.example.votingapp.service;
//
//import com.example.votingapp.model.User;
//import com.example.votingapp.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.time.Period;
//import java.util.List;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@Service
//public class UserService {
//
//    private static final Logger logger = Logger.getLogger(UserService.class.getName());
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public User registerUser(User user) {
//        // Перевірка віку користувача
//        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
//            throw new IllegalArgumentException("Користувачу має бути не менше 18 років");
//        }
//        // Хешування паролю
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        // Встановлення ролі за замовчуванням
//        user.setRole("USER");
//
//        // Встановлення дефолтного значення для emailVerified
//        user.setEmailVerified(false);
//
//        // Збереження користувача
//        return userRepository.save(user);
//    }
//
//    public List<User> findAll() {
//        return userRepository.findAll();
//    }
//
//    public User findById(Long id) {
//        Optional<User> user = userRepository.findById(id);
//        return user.orElse(null);
//    }
//
//    public void deleteById(Long id) {
//        userRepository.deleteById(id);
//    }
//
//    public User findByUsername(String username) {
//        logger.info("Searching for user with username: " + username);
//        User user = userRepository.findByUsername(username);
//        if (user != null) {
//            logger.info("User found: " + user.toString());
//        } else {
//            logger.info("User not found");
//        }
//        return user;
//    }
//
//    public User save(User user) {
//        return userRepository.save(user);
//    }
//
//    public User findByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//}

package com.example.votingapp.service;

import com.example.votingapp.model.Poll;
import com.example.votingapp.model.User;
import com.example.votingapp.repository.PollRepository;
import com.example.votingapp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Перевірка віку користувача
        if (Period.between(user.getBirthDate(), LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("Користувачу має бути не менше 18 років");
        }

        // Хешування паролю
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Встановлення ролі за замовчуванням
        user.setRole("USER");

        // Встановлення дефолтного значення для emailVerified
        user.setEmailVerified(false);

        // Збереження користувача
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findByUsername(String username) {
        logger.info("Searching for user with username: " + username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            logger.info("User found: " + user.toString());
        } else {
            logger.info("User not found");
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public User save(User user) {
        return userRepository.save(user);
    }


    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }


    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
        return true;
    }

    public boolean existsByUsernameAndIdNot(String username, Long id) {
        return userRepository.existsByUsernameAndIdNot(username, id);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        return userRepository.existsByEmailAndIdNot(email, id);
    }

    public User updateProfile(Long userId, String username, String email) {
        User user = findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (username != null && !username.equals(user.getUsername())) {
            if (existsByUsernameAndIdNot(username, userId)) {
                throw new IllegalArgumentException("Username is already taken");
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (existsByEmailAndIdNot(email, userId)) {
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(email);
        }

        return save(user);
    }

    public boolean isUserOldEnough(User user) {
        return Period.between(user.getBirthDate(), LocalDate.now()).getYears() >= 18;
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Метод для завантаження користувача за іменем
    public UserDetails loadUserByUsername(String username) {
        User user = findByUsername(username);
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.getPassword());
        builder.roles(user.getRole());
        return builder.build();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);  // Повертаємо Optional<User>
    }

}
