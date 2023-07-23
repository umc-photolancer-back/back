package shop.photolancer.photolancer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import shop.photolancer.photolancer.domain.User;
import shop.photolancer.photolancer.domain.enums.Purpose;
import shop.photolancer.photolancer.domain.enums.UserStatus;
import shop.photolancer.photolancer.repository.UserRepository;
import shop.photolancer.photolancer.web.dto.UserJoinRequestDto;
import shop.photolancer.photolancer.web.dto.UserUpdateRequestDto;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    protected final UserRepository userRepository;

    public Map<String, String> validateHandling(BindingResult bindingResult) {
        Map<String, String> validatorResult = new HashMap<>();
        // 유효성 검사에 실패한 필드들을 Map 자료구조를 통해 {키 값, 에러 메시지}형태로 응답
        // Key : valid_{dto 필드명}, Message : dto에서 작성한 Message값
        for(FieldError error : bindingResult.getFieldErrors()) {
            String validKeyName = String.format("valid_%s", error.getField());
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 만약 데이터가 존재한다면 IllegalStateException이 일어나게 됨 -> 500error
    public void checkUsernameDuplication(UserJoinRequestDto userDto) {
        boolean usernameDuplicate = userRepository.existsByName(userDto.toEntity().getName());
        if (usernameDuplicate) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
    }
    public void checkIdDuplication(UserJoinRequestDto userDto) {
        boolean IdDuplicate = userRepository.existsByUserId(userDto.toEntity().getUserId());
        if (IdDuplicate) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
    }

    public void checkEmailDuplication(UserJoinRequestDto userDto) {
        boolean emailDuplicate = userRepository.existsByEmail(userDto.toEntity().getEmail());
        if (emailDuplicate) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }

    public void checkPassword(UserJoinRequestDto userDto){
        boolean passwordCheck = userDto.getPassword().equals(userDto.getAgain_password()) ? true : false ;
        if (!passwordCheck) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    public void checkUserNickNameDuplication(UserUpdateRequestDto requestDto) {
        boolean userNickNameDuplicate = userRepository.existsByNickname(requestDto.toEntity().getNickname());
        if (userNickNameDuplicate) {
            throw new IllegalStateException("이미 존재하는 닉네임입니다.");
        }
    }

    public User findUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NoSuchElementException("No user found with id " + userId);
        }
    }

    public void createUser(UserJoinRequestDto userInfo) {
        User user = User.builder()
                .userId(userInfo.getUser_id())
                .password(userInfo.getPassword())
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .purpose(Purpose.valueOf(userInfo.getPurpose()))
                .status(UserStatus.ACTIVE)
                .build();

        // 사용자 등록
//        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }
    //소셜 로그인에 쓰일 함수
    public void createUser(User user){
        userRepository.save(user);
    }

    public User deactivateUser(User user) {
        user.setStatus(UserStatus.INACTIVE);
        return userRepository.save(user);
    }

    public User updateUser(UserUpdateRequestDto requestDto,User user) {
        user.setNickname(requestDto.getNickname());
        user.setExplane(requestDto.getExplane());
//        user.setBookmark(requestDto.getBookmark());
        return userRepository.save(user);
    }
}