package com.BE.service.implementServices;

import com.BE.enums.GenderEnum;
import com.BE.enums.RoleEnum;
import com.BE.enums.StatusEnum;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.UserMapper;
import com.BE.model.entity.User;
import com.BE.model.request.CreateUserRequest;
import com.BE.model.request.UserProfileRequest;
import com.BE.model.response.UserResponse;
import com.BE.repository.AuthenRepository;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.service.interfaceServices.IUserService;
import com.BE.service.interfaceServices.IWalletService;
import com.BE.utils.AccountUtils;
import com.BE.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class UserServiceImpl implements IUserService {

    final AuthenRepository repository;
    final UserMapper mapper;
    final PasswordEncoder passwordEncoder;
    final PageUtil pageUtil;
    final IWalletService iWalletService;
    final AccountUtils accountUtils;
    final IAuthenticationService iAuthenticationService;

    @Value("${spring.sendgrid.template.verify}")
    String templateVerifyUser;

    @Value("${spring.link.verify}")
    String linkVerifyUser;


    @Override
    public UserResponse update(UUID id, UserProfileRequest request) {
        User existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy hồ sơ người dùng để cập nhật."));
        mapper.update(existing, request);
        return mapper.toResponse(repository.save(existing));
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        User user = mapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(StatusEnum.INACTIVE);
        try{
            if(RoleEnum.TEACHER.equals(request.getRole()) || RoleEnum.PARTNER.equals(request.getRole())) iWalletService.create(user);
            if(!RoleEnum.ADMIN.equals(request.getRole())) iAuthenticationService.sendEmailByTemplate(user,templateVerifyUser, linkVerifyUser);
            user = repository.save(user);
            return mapper.toResponse(user);
        } catch (DataIntegrityViolationException e) {
            System.out.println(e.getMessage());
            if(e.getMessage().contains("users.UK_6dotkott2kjsp8vw4d0m25fb7")){
                throw new DataIntegrityViolationException("Đã có email này!");
            }else{
                throw new DataIntegrityViolationException("Đã có username này!");
            }
        }
    }

    @Override
    public UserResponse updateStatus(UUID id, StatusEnum status) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy người dùng"));
        user.setStatus(status);
        return mapper.toResponse(repository.save(user));
    }


    @Override
    public Page<UserResponse> getUsersWithFilter(String search, RoleEnum role, StatusEnum status, GenderEnum gender,
                                                 int offset, int pageSize,
                                                 String sortBy, String sortDirection) {

        User currentUser = accountUtils.getCurrentUser();
        RoleEnum currentRole = currentUser.getRole();

        pageUtil.checkOffset(offset);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(offset - 1, pageSize, sort);

        Specification<User> spec = Specification.where(null);

        // ✅ Phân quyền theo role hiện tại
        if (currentRole == RoleEnum.ADMIN) {
            // ADMIN không thấy ADMIN khác
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.notEqual(root.get("role"), RoleEnum.ADMIN)
            ));
        } else if (currentRole == RoleEnum.STAFF) {
            // STAFF chỉ thấy TEACHER + PARTNER, không thấy chính mình
            spec = spec.and((root, query, cb) -> cb.and(
                    cb.not(root.get("role").in(RoleEnum.ADMIN, RoleEnum.STAFF))
            ));
        }

        // 🔍 Lọc theo tên
        if (StringUtils.hasText(search)) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("fullName")), "%" + search.toLowerCase() + "%"));
        }

        // 🔍 Lọc theo vai trò (nếu có)
        if (role != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("role"), role));
        }

        // 🔍 Lọc theo trạng thái
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        // 🔍 Lọc theo giới tính
        if (gender != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("gender"), gender));
        }

        return repository.findAll(spec, pageable).map(mapper::toResponse);
    }


    @Override
    public UserResponse getUserById(UUID id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("User không tồn tại"));
        return mapper.toResponse(user);
    }


}
