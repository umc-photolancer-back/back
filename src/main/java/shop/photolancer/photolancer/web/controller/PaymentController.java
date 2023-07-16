package shop.photolancer.photolancer.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.photolancer.photolancer.domain.User;
import shop.photolancer.photolancer.exception.ResponseMessage;
import shop.photolancer.photolancer.exception.StatusCode;
import shop.photolancer.photolancer.repository.UserRepository;
import shop.photolancer.photolancer.service.PaymentService;
import shop.photolancer.photolancer.web.dto.PaymentRequestDto;
import shop.photolancer.photolancer.web.dto.base.DefaultRes;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PaymentController {

    public final PaymentService paymentService;
    public final UserRepository userRepository;

    //jwt 토큰, 충전금액, 충전 방법
    @PostMapping("/my-profile/charge")
    public ResponseEntity charge(PaymentRequestDto.ChargeDto request){
        try {
            //추후 유저 인증 구현
            //
            Long userId = Long.valueOf(1);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            //

            Integer amount = request.getAmount();
            String paymentMethod = request.getPaymentMethod();

            if (amount == null){
                return new ResponseEntity( DefaultRes.res(StatusCode.BAD_REQUEST, ResponseMessage.AMOUT_EXIT_ERROR), HttpStatus.BAD_REQUEST);
            } else if (paymentMethod==null) {
                return new ResponseEntity( DefaultRes.res(StatusCode.BAD_REQUEST, ResponseMessage.PAYMENTMETHOD_EXIT_ERROR), HttpStatus.BAD_REQUEST);
            }

            paymentService.charge(user, amount, paymentMethod);

            return new ResponseEntity( DefaultRes.res(StatusCode.OK, ResponseMessage.CHARGE_POINT_SUCCESS), HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

}
