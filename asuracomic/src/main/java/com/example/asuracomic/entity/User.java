package com.example.asuracomic.entity;



import com.example.asuracomic.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID duy nhất của người dùng, dùng để tham chiếu trong các bảng khác (VD: bình luận, giao dịch)

    @Column(unique = true, nullable = false, length = 255)
    private String email; // Email người dùng, dùng để đăng nhập, gửi thông báo, và xác thực, đảm bảo duy nhất

    @Column(nullable = false, length = 255)
    private String password; // Mật khẩu mã hóa, dùng để xác thực khi đăng nhập, bảo mật thông tin người dùng

    @Column(unique = true, nullable = false, length = 50)
    private String username; // Tên hiển thị, xuất hiện trong bình luận, hồ sơ, đảm bảo duy nhất để nhận diện

    @Column(length = 255)
    private String avatar; // Đường dẫn ảnh đại diện, hiển thị trong hồ sơ, bình luận, tăng tính cá nhân hóa

    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // Vai trò: USER (người đọc) hoặc ADMIN (quản lý nội dung, báo cáo), kiểm soát quyền truy cập

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal coinBalance = BigDecimal.ZERO; // Số dư coin, dùng để mở khóa chương hoặc mua VIP, hiển thị trong hồ sơ

    @Column(nullable = false)
    private boolean vipStatus = false; // Trạng thái VIP: true nếu người dùng là VIP, hiển thị quyền lợi đặc biệt (VD: đọc chương miễn phí)

    private LocalDateTime vipStartDate; // Ngày bắt đầu VIP

    private LocalDateTime vipExpiryDate; // Thời điểm hết hạn VIP, dùng để kiểm tra tư cách VIP khi đăng nhập hoặc truy cập nội dung


    @Column(nullable = false)
    private boolean isActive = true; // Trạng thái tài khoản: true (hoạt động), false (bị khóa), kiểm soát đăng nhập và quyền truy cập

    private LocalDateTime lastLogin; // Thời điểm đăng nhập cuối, theo dõi hoạt động người dùng, hỗ trợ thống kê hoặc bảo mật

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời điểm tạo tài khoản, dùng để thống kê người dùng mới hoặc quản lý

    @Column(nullable = false)
    private LocalDateTime updatedAt; // Thời điểm cập nhật thông tin tài khoản, theo dõi thay đổi (VD: đổi mật khẩu, cập nhật VIP)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UnlockedChapter> unlockedChapters; // Danh sách chương đã mở khóa, đảm bảo người dùng truy cập được sau khi đăng xuất

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favorite> favorites; // Danh sách truyện yêu thích, hiển thị trong hồ sơ, hỗ trợ cá nhân hóa trải nghiệm

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> comments; // Danh sách bình luận của người dùng, hiển thị trong trang chương, hỗ trợ tương tác

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Report> reports; // Danh sách báo cáo nội dung vi phạm, giúp người dùng đóng góp vào kiểm duyệt cộng đồng

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions; // Lịch sử giao dịch (mua coin, mở khóa, mua VIP), hỗ trợ theo dõi tài chính

    @OneToMany(mappedBy = "resolvedBy", cascade = CascadeType.ALL)
    private List<Report> resolvedReports; // Danh sách báo cáo do quản trị viên này xử lý, hỗ trợ quản lý nội dung (chỉ dành cho ADMIN)

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Rating> ratings; // Danh sách đánh giá truyện của người dùng, góp phần tính điểm trung bình cho truyện

}