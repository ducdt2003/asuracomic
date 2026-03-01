/*
package com.example.asuracomic.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/asura")
public class CoinController {
    @GetMapping("/purchase")
    public String purchase(){
        return "web/web-coin/shop";
    }

    @GetMapping("/membership")
    public String membership(){
        return "web/web-coin/membership";
    }

    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapter(@PathVariable String comicSlug,
                                @PathVariable String chapterSlug,
                                HttpSession session) {

        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            // Redirect to login page with return URL
            String redirectUrl = "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
            return "redirect:/asura/login?redirectUrl=" + redirectUrl;
        }

        // (Optional) kiểm tra quyền truy cập hoặc load chapter nếu cần
        return "web/web-coin/unlock-chapter";
    }


}
*/



//có mỡ khóa được
/*
package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.CoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;

    @GetMapping("/purchase")
    public String purchase() {
        return "web/web-coin/shop";
    }

    @GetMapping("/membership")
    public String membership() {
        return "web/web-coin/membership";
    }

    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapterPage(@PathVariable String comicSlug,
                                    @PathVariable String chapterSlug,
                                    HttpSession session,
                                    Model model) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            String redirectUrl = "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
            return "redirect:/asura/login?redirectUrl=" + redirectUrl;
        }

        // Lấy ID người dùng từ session (giả sử UserDTO có id)
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại."));

        // Tìm chapter theo slug
        Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

        // Thêm thông tin vào model
        model.addAttribute("chapter", chapter);
        model.addAttribute("comicSlug", comicSlug);
        model.addAttribute("user", user); // Thêm thông tin người dùng để hiển thị coinBalance

        return "web/web-coin/unlock-chapter";
    }

    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapter(@PathVariable String comicSlug,
                                @PathVariable String chapterSlug,
                                RedirectAttributes redirectAttributes) {
        try {
            // Tìm chapter theo slug
            Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                    .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

            // Gọi service để mở khóa
            String redirectUrl = coinService.unlockChapter(chapter.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa chương thành công!");
            return "redirect:" + redirectUrl;
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
        }
    }
}*/


// có ghi lại mỡ khóa chướng
/*
package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.Chapter;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.repository.ChapterRepository;
import com.example.asuracomic.repository.UnlockedChapterRepository;
import com.example.asuracomic.repository.UserRepository;
import com.example.asuracomic.service.CoinService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;

    @GetMapping("/purchase")
    public String purchase() {
        return "web/web-coin/shop";
    }

    @GetMapping("/membership")
    public String membership() {
        return "web/web-coin/membership";
    }

    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapterPage(@PathVariable String comicSlug,
                                    @PathVariable String chapterSlug,
                                    HttpSession session,
                                    Model model) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            String redirectUrl = "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
            return "redirect:/asura/login?redirectUrl=" + redirectUrl;
        }

        // Lấy ID người dùng từ session
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại."));

        // Tìm chapter theo slug
        Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

        // Kiểm tra nếu chương miễn phí hoặc đã được mở khóa
        boolean isUnlocked = unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapter.getId());
        if (chapter.isFree() || isUnlocked) {
            return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
        }

        // Thêm thông tin vào model nếu cần hiển thị trang mở khóa
        model.addAttribute("chapter", chapter);
        model.addAttribute("comicSlug", comicSlug);
        model.addAttribute("user", user);

        return "web/web-coin/unlock-chapter";
    }

    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapter(@PathVariable String comicSlug,
                                @PathVariable String chapterSlug,
                                RedirectAttributes redirectAttributes) {
        try {
            // Tìm chapter theo slug
            Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                    .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

            // Gọi service để mở khóa
            String redirectUrl = coinService.unlockChapter(chapter.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa chương thành công!");
            return "redirect:" + redirectUrl;
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
        }
    }
}
*/




/*chạy được */
package com.example.asuracomic.controller.web;

import com.example.asuracomic.dto.UserDTO;
import com.example.asuracomic.entity.*;
import com.example.asuracomic.exception.BadRequestException;
import com.example.asuracomic.mapper.UserMapper;
import com.example.asuracomic.model.enums.TransactionType;
import com.example.asuracomic.repository.*;
import com.example.asuracomic.service.*;
import com.paypal.orders.LinkDescription;
import com.paypal.orders.Order;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/asura")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;
    private final ChapterRepository chapterRepository;
    private final UserRepository userRepository;
    private final UnlockedChapterRepository unlockedChapterRepository;
    private final VipConfigRepository vipConfigRepository;
    private final TransactionRepository transactionRepository;
    private final VipService vipService;
    private final CoinPackageRepository coinPackageRepository;
    private final PayCoinService payCoinService;

    private final PayPalService payPalService;
    private final UserService userService;

    @GetMapping("/coin") // ĐƯỜNG DẪN NÀY PHẢI CÓ
    public String showCoinShop(Model model, Principal principal) {
        model.addAttribute("coinPackages", coinPackageRepository.findAll());

        if (principal != null) {
            // Đảm bảo hàm này trả về User có chứa field bạn gọi ở HTML
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("currentUser", user);
        }
        return "web/web-coin/shop"; // Tên file HTML của bạn (không có .html)
    }

    @GetMapping("/membership")
    public String membership(Model model, HttpSession session) {
        UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
        if (currentUser != null) {
            model.addAttribute("currentUser", currentUser);
        }
        return "web/web-coin/membership";
    }
    @PostMapping("/pay")
    public String pay(@RequestParam Long packageId) {
        try {
            CoinPackage pkg = coinPackageRepository.findById(packageId)
                    .orElseThrow(() -> new RuntimeException("Gói coin không tồn tại"));

            BigDecimal priceVnd = pkg.getPrice();
            BigDecimal rate = new BigDecimal("25000");
            BigDecimal usdValue = priceVnd.divide(rate, 2, RoundingMode.HALF_UP);

            Order order = payPalService.createOrder(
                    usdValue.doubleValue(),
                    "http://localhost:9090/asura/paypal/success?packageId=" + packageId,
                    "http://localhost:9090/asura/paypal/cancel"
            );

            // QUAN TRỌNG: Tìm link approve trong response của PayPal để chuyển hướng người dùng
            for (LinkDescription link : order.links()) {
                if (link.rel().equalsIgnoreCase("approve")) {
                    return "redirect:" + link.href(); // Chuyển sang trang đăng nhập PayPal
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/asura/coin?error";
    }


    @GetMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapterPage(@PathVariable String comicSlug,
                                    @PathVariable String chapterSlug,
                                    HttpSession session,
                                    Model model) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            String redirectUrl = "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
            return "redirect:/asura/login?redirectUrl=" + redirectUrl;
        }

        // Lấy ID người dùng từ session thông tin user
        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại."));

        // Tìm chapter theo slug ()Optional
        Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

        // Kiểm tra nếu chương miễn phí hoặc đã được mở khóa
        boolean isUnlocked = unlockedChapterRepository.existsByUserIdAndChapterId(userId, chapter.getId());
        if (chapter.isFree() || isUnlocked) {
            return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug;
        }

        // Thêm thông tin vào model để hiển thị trang mở khóa
        model.addAttribute("chapter", chapter);
        model.addAttribute("comicSlug", comicSlug);
        model.addAttribute("user", user);

        return "web/web-coin/unlock-chapter";
    }

    @PostMapping("/comic/{comicSlug}/chapter/{chapterSlug}/unlock")
    public String unlockChapter(@PathVariable String comicSlug,
                                @PathVariable String chapterSlug,
                                HttpSession session,
                                // thông báo tạm thoi như là số dư không dủ
                                RedirectAttributes redirectAttributes) {
        try {
            // Tìm chapter theo slug
            Chapter chapter = chapterRepository.findByComicSlugAndChapterSlug(comicSlug, chapterSlug)
                    .orElseThrow(() -> new IllegalArgumentException("Chương không tồn tại."));

            // Kiểm tra số dư coin trước khi gọi service
            Object currentUser = session.getAttribute("currentUser");
            // kiểm tra xe người dung đã đăng nhập chưa
            if (currentUser == null) {
                String redirectUrl = "/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
                return "redirect:/asura/login?redirectUrl=" + redirectUrl;
            }
            Long userId = ((UserDTO) currentUser).getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại."));
            if (user.getCoinBalance().compareTo(chapter.getCoinPrice()) < 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Số dư coin không đủ. Vui lòng nạp thêm coin.");
                return "redirect:/asura/purchase";
            }

            // Gọi service để mở khóa
            String redirectUrl = coinService.unlockChapter(chapter.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Mở khóa chương thành công!");
            return "redirect:" + redirectUrl;
        } catch (BadRequestException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/asura/comic/" + comicSlug + "/chapter/" + chapterSlug + "/unlock";
        }
    }

    @GetMapping("/transaction-history")
    public String transactionHistory(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     HttpSession session,
                                     Model model) {
        Object currentUser = session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/asura/login?redirectUrl=/asura/transaction-history";
        }

        Long userId = ((UserDTO) currentUser).getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại."));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> transactionsPage = coinService.getUserTransactionHistory(pageable);

        model.addAttribute("transactionsPage", transactionsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("user", user); // Ensure user is added

        return "web/web-coin/transaction-history";
    }



    // na coin
  /*  @GetMapping("/shop") // Đường dẫn đến trang nạp coin của bạn
    public String showShopPage(Model model, Principal principal) {
        // 1. Lấy danh sách gói coin (để tránh lỗi danh sách trống)
        List<CoinPackage> packages = payPalService.findAll();
        model.addAttribute("coinPackages", packages);

        // 2. Xử lý người dùng hiện tại
        if (principal != null) {
            // Nếu đã đăng nhập, tìm user từ DB và gửi sang View
            User user = payPalService.findByUserName(principal.getName());
            model.addAttribute("currentUser", user);
        } else {
            // Nếu chưa đăng nhập, gửi một đối tượng rỗng hoặc null
            // để HTML không bị lỗi khi truy cập currentUser.userName
            model.addAttribute("currentUser", null);
        }

        return "web/web-coin/shop"; // Đường dẫn file HTML của bạn
    }*/
    // Xử lý nạp coin tiên
    @PostMapping("/topup")
    public String topUp(@RequestParam Long packageId,
                        HttpSession session,
                        RedirectAttributes ra) {

        if (session.getAttribute("currentUser") == null) {
            return "redirect:/asura/login";
        }

        try {
            payCoinService.topUpCoin(packageId, session);
            ra.addFlashAttribute("successMessage", "Nạp coin thành công 🎉");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/asura/coin"; // ❗ KHÔNG phải /purchase
    }


}
